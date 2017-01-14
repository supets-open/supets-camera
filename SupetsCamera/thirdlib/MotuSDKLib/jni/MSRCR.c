#include <android/log.h>
#include "math.h"
#include "operation.h"
#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
#include <math.h>

#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)
#define min(x,y) (x>y?y:x)
#define max(x,y) (x>y?x:y)
#define PI 3.1415926

#define MAX_RETINEX_SCALES 8

#define RETINEX_UNIFORM 0
#define RETINEX_LOW     1
#define RETINEX_HIGH    2

static float RetinexScales[MAX_RETINEX_SCALES];

typedef struct {
	int N;
	float sigma;
	double B;
	double b[4];
} gauss3_coefs;

typedef struct {
	int scale;
	int nscales;
	int scales_mode;
	float cvar;
} RetinexParams;

typedef enum {
	filter_uniform, filter_low, filter_high
} FilterModeX;

RetinexParams rvals = { 300, /* Scale */
3, /* Scales */
RETINEX_UNIFORM, /* Echelles reparties uniformement */
2.5 /* A voir */
};

void compute_coefs3(gauss3_coefs *c, float sigma) {
	/*
	 * Papers:  "Recursive Implementation of the gaussian filter.",
	 *          Ian T. Young , Lucas J. Van Vliet, Signal Processing 44, Elsevier 1995.
	 * formula: 11b       computation of q
	 *          8c        computation of b0..b1
	 *          10        alpha is normalization constant B
	 */
	float q, q2, q3;

	q = 0;

	if (sigma >= 2.5) {
		q = 0.98711 * sigma - 0.96330;
	} else if ((sigma >= 0.5) && (sigma < 2.5)) {
		q = 3.97156 - 4.14554 * (float) sqrt((double) 1 - 0.26891 * sigma);
	} else {
		q = 0.1147705018520355224609375;
	}

	q2 = q * q;
	q3 = q * q2;
	c->b[0] = (1.57825 + (2.44413 * q) + (1.4281 * q2) + (0.422205 * q3));
	c->b[1] = ((2.44413 * q) + (2.85619 * q2) + (1.26661 * q3));
	c->b[2] = (-((1.4281 * q2) + (1.26661 * q3)));
	c->b[3] = ((0.422205 * q3));
	c->B = 1.0 - ((c->b[1] + c->b[2] + c->b[3]) / c->b[0]);
	c->sigma = sigma;
	c->N = 3;

	/*
	 g_printerr ("q %f\n", q);
	 g_printerr ("q2 %f\n", q2);
	 g_printerr ("q3 %f\n", q3);
	 g_printerr ("c->b[0] %f\n", c->b[0]);
	 g_printerr ("c->b[1] %f\n", c->b[1]);
	 g_printerr ("c->b[2] %f\n", c->b[2]);
	 g_printerr ("c->b[3] %f\n", c->b[3]);
	 g_printerr ("c->B %f\n", c->B);
	 g_printerr ("c->sigma %f\n", c->sigma);
	 g_printerr ("c->N %d\n", c->N);
	 */
}

void retinex_scales_distribution(float* scales, int nscales, int mode, int s) {
	if (nscales == 1) { /* For one filter we choose the median scale */
		scales[0] = (int) s / 2;
	} else if (nscales == 2) { /* For two filters whe choose the median and maximum scale */
		scales[0] = (int) s / 2;
		scales[1] = (int) s;
	} else {
		float size_step = (float) s / (float) nscales;
		int i;

		switch (mode) {
		case RETINEX_UNIFORM:
			for (i = 0; i < nscales; ++i)
				scales[i] = 2. + (float) i * size_step;
			break;

		case RETINEX_LOW:
			size_step = (float) log(s - 2.0) / (float) nscales;
			for (i = 0; i < nscales; ++i)
				scales[i] = 2. + pow(10, (i * size_step) / log(10.0));
			break;

		case RETINEX_HIGH:
			size_step = (float) log(s - 2.0) / (float) nscales;
			for (i = 0; i < nscales; ++i)
				scales[i] = s - pow(10, (i * size_step) / log(10.0));
			break;

		default:
			break;
		}
	}
}

void gausssmooth(float *in, float *out, int size, int rowstride,
		gauss3_coefs *c) {
	/*
	 * Papers:  "Recursive Implementation of the gaussian filter.",
	 *          Ian T. Young , Lucas J. Van Vliet, Signal Processing 44, Elsevier 1995.
	 * formula: 9a        forward filter
	 *          9b        backward filter
	 *          fig7      algorithm
	 */
	int i, n, bufsize;
	float *w1, *w2;

	/* forward pass */
	bufsize = size + 3;
	size -= 1;
	w1 = (float *) malloc(bufsize * sizeof(float));
	w2 = (float *) malloc(bufsize * sizeof(float));
	w1[0] = in[0];
	w1[1] = in[0];
	w1[2] = in[0];
	for (i = 0, n = 3; i <= size; i++, n++) {
		w1[n] = (float) (c->B * in[i * rowstride]
				+ ((c->b[1] * w1[n - 1] + c->b[2] * w1[n - 2]
						+ c->b[3] * w1[n - 3]) / c->b[0]));
	}

	/* backward pass */
	w2[size + 1] = w1[size + 3];
	w2[size + 2] = w1[size + 3];
	w2[size + 3] = w1[size + 3];
	for (i = size, n = i; i >= 0; i--, n--) {
		w2[n] = out[i * rowstride] = (float) (c->B * w1[n]
				+ ((c->b[1] * w2[n + 1] + c->b[2] * w2[n + 2]
						+ c->b[3] * w2[n + 3]) / c->b[0]));
	}

	free(w1);
	free(w2);
}

/*
 * Calculate the average and variance in one go.
 */
void compute_mean_var(float **src, float *mean, float *var, int size, int co) {

	//改成3通道
	float vsquared = 0;
	int i, j;
	mean[co] = 0;
	for (i = 0; i != size; ++i) {
		mean[co] += src[co][i];
		vsquared += src[co][i] * src[co][i];
	}

	mean[co] /= (float) size;
	vsquared /= (float) size;
	var[co] = (vsquared - (mean[co] * mean[co]));
	var[co] = sqrt(var[co]);

}


void GaussSmoothMethod(float *in, int width, int height, int sigma)
{
	gauss3_coefs gaussKernel;
	compute_coefs3(&gaussKernel, sigma);
	int channelsize = width * height;
	float *out = (float *)malloc(sizeof(float) * channelsize);
	memset(out, 0, channelsize * sizeof(float));
//	memcpy(out, in, sizeof(float) * width * height);

	int row, col, pos;
	for (row = 0; row < height; row++) {
		pos = row * width;
		gausssmooth(in + pos, out + pos, width, 1, &gaussKernel);
	}

	memcpy(in, out, channelsize * sizeof(float));
	memset(out, 0, channelsize * sizeof(float));

	for (col = 0; col < width; col++) {
		pos = col;
		gausssmooth(in + pos, out + pos, height, width, &gaussKernel);
	}

	memcpy(in, out, sizeof(float) * channelsize);
	free(out);
}

void GaussSmoothMethod1(float *in, float *out, int width, int height, gauss3_coefs gaussKernel)
{
	int row, col, pos;
	for (row = 0; row < height; row++) {
		pos = row * width;
		gausssmooth(in + pos, out + pos, width, 1, &gaussKernel);
	}

	int channelsize = width * height;
	memcpy(in, out, channelsize * sizeof(float));
	memset(out, 0, channelsize * sizeof(float));

	for (col = 0; col < width; col++) {
		pos = col;
		gausssmooth(in + pos, out + pos, height, width, &gaussKernel);
	}
}

void GaussSmoothMethodAllChannel(int *srcPixArray, int width, int height, int sigma)
{
	gauss3_coefs gaussKernel;
	compute_coefs3(&gaussKernel, sigma);
	int channelsize = width * height;
	float *in = (float *)malloc(sizeof(float) * channelsize);
	float *out = (float *)malloc(sizeof(float) * channelsize);
	memset(out, 0, channelsize * sizeof(float));
	int i, j;
	for(i = 0; i != 3; ++i)
	{
		for(j = 0; j != channelsize; ++j)
		{
			in[j] = (srcPixArray[j] >> (8 * i) ) & 0xFF;
		}

		GaussSmoothMethod1(in, out, width, height, gaussKernel);

		for(j = 0; j != channelsize; ++j)
		{
			if(i == 0)
			{
				srcPixArray[j] = srcPixArray[j] & 0xFFFFFF00;
			}
			else if(i == 1)
			{
				srcPixArray[j] = srcPixArray[j] & 0xFFFF00FF;
			}
			else if(i == 2)
			{
				srcPixArray[j] = srcPixArray[j] & 0xFF00FFFF;
			}

			srcPixArray[j] = srcPixArray[j] | (((int)in[j]) << (i * 8));
		}

	}

	free(in);
	free(out);
}

void MSRCR(int *src, int width, int height) {

	int scale, row, col;
	int i, j;
	int size;
	int pos;
	int channel;
	int psrc[3]; /* backup pointer for src buffer */
//  float       *dst  = NULL;            /* float buffer for algorithm */
	float pdst[3];
	; /* backup pointer for float buffer */
	float *in, *out;
	int channelsize; /* Float memory cache for one channel */
	float weight;
	gauss3_coefs coef;
	float mean[3], var[3];
	float mini, range, maxi;
	float alpha;
	float gain;
	float offset;
	double max_preview = 0.0;

//  size = width * height * bytes;
//  dst = (float *)malloc (size * sizeof (float));

	float **dst = (float **) malloc(sizeof(float *) * 3);
	for (i = 0; i != 3; ++i) {
		dst[i] = (float *) malloc(sizeof(float) * width * height);
		memset(dst[i], 0, sizeof(float) * width * height);
	}
//   if (dst == NULL)
//     {
// //      g_warning ("Failed to allocate memory");
//       return;
//     }
//   memset (dst, 0, size * sizeof (float));

	channelsize = (width * height);
	in = (float *) malloc(channelsize * sizeof(float));
	if (in == NULL) {
		free(dst);
//      g_warning ("Failed to allocate memory");
		return; /* do some clever stuff */
	}

	out = (float *) malloc(channelsize * sizeof(float));
	if (out == NULL) {
		free(in);
		free(dst);
//      g_warning ("Failed to allocate memory");
		return; /* do some clever stuff */
	}

	/*
	 Calculate the scales of filtering according to the
	 number of filter and their distribution.
	 */

	retinex_scales_distribution(RetinexScales, rvals.nscales, rvals.scales_mode,
			rvals.scale);

	/*
	 Filtering according to the various scales.
	 Summerize the results of the various filters according to a
	 specific weight(here equivalent for all).
	 */
	weight = 1. / (float) rvals.nscales;

	/*
	 The recursive filtering algorithm needs different coefficients according
	 to the selected scale (~ = standard deviation of Gaussian).
	 */

	//重写这段代码

	//把RGB空间转换成HSV
//	int hh, ss, vv;
//	for(i = 0; i != width * height; ++i)
//	{
//		transHSV(getR(src[i]), getG(src[i]), getB(src[i]), &hh, &ss, &vv);
//		setR(&src[i], hh);
//		setG(&src[i], ss);
//		setB(&src[i], vv);
//	}

	pos = 0;
	for (channel = 0; channel < 3; channel++) {

//       for (i = 0; i < channelsize ; i++)
//          {
//             /* 0-255 => 1-256 */
//             in[i] = (float)(src[pos] + 1.0);
//          }

		for (i = 0; i < channelsize; ++i) {
			in[i] = (float) (((src[i] >> (8 * channel)) & 0xFF) + 1.0);
//		  cout << in[i] << " ";
		}

		for (scale = 0; scale < rvals.nscales; scale++) {
			compute_coefs3(&coef, RetinexScales[scale]);
			/*
			 *  Filtering (smoothing) Gaussian recursive.
			 *
			 *  Filter rows first
			 */
			for (row = 0; row < height; row++) {
				pos = row * width;
				gausssmooth(in + pos, out + pos, width, 1, &coef);
			}

			memcpy(in, out, channelsize * sizeof(float));
			memset(out, 0, channelsize * sizeof(float));

			/*
			 *  Filtering (smoothing) Gaussian recursive.
			 *
			 *  Second columns
			 */
			for (col = 0; col < width; col++) {
				pos = col;
				gausssmooth(in + pos, out + pos, height, width, &coef);
			}

			/*
			 Summarize the filtered values.
			 In fact one calculates a ratio between the original values and the filtered values.
			 */

//           for (i = 0, pos = channel; i < channelsize; i++, pos += bytes)
//             {
//               dst[pos] += weight * (log (src[pos] + 1.) - log (out[i]));
//             }
			for (i = 0, pos = channel; i < channelsize; ++i) {
				dst[pos][i] += weight
						* (log(((src[i] >> (8 * pos)) & 0xFF) + 1.0)
								- log(out[i]));
			}

			//这里转成自己的那种数据，不是用bytes的

//            if (!preview_mode)
//              gimp_progress_update ((channel * rvals.nscales + scale) /
//                                    max_preview);
		}

	}
	free(in);
	free(out);

	/*
	 Final calculation with original value and cumulated filter values.
	 The parameters gain, alpha and offset are constants.
	 */
	/* Ci(x,y)=log[a Ii(x,y)]-log[ Ei=1-s Ii(x,y)] */

//   alpha  = 128.;
//   gain   = 1.;
//   offset = 0.;
	alpha = 128.;
	gain = 0.8;
	offset = 0.1;

	//这边要改掉

	for (i = 0; i < channelsize; i++) {
		float logl;

		for (j = 0; j != 3; ++j) {
			psrc[j] = ((src[i] >> (8 * j)) & 0xFF);
			pdst[j] = dst[j][i];
		}

		logl = log((float) psrc[0] + (float) psrc[1] + (float) psrc[2] + 3.);

		dst[0][i] = gain * ((log(alpha * (psrc[0] + 1.)) - logl) * pdst[0])
				+ offset;
		dst[1][i] = gain * ((log(alpha * (psrc[1] + 1.)) - logl) * pdst[1])
				+ offset;
		dst[2][i] = gain * ((log(alpha * (psrc[2] + 1.)) - logl) * pdst[2])
				+ offset;
	}

	/*  if (!preview_mode)
	 gimp_progress_update ((2.0 + (rvals.nscales * 3)) /
	 ((rvals.nscales * 3) + 3));*/

	/*
	 Adapt the dynamics of the colors according to the statistics of the first and second order.
	 The use of the variance makes it possible to control the degree of saturation of the colors.
	 */
//  pdst = dst;

	for (i = 0; i != 3; ++i) {
		compute_mean_var(dst, mean, var, channelsize, i);
	}
//	compute_mean_var (pdst, mean, var, size, bytes);

//  改成3通道的。。。。

	for (i = 0; i != 3; ++i) {
		for (j = 0; j != channelsize; ++j) {
			mini = mean[i] - rvals.cvar * var[i];
			maxi = mean[i] + rvals.cvar * var[i];
			range = maxi - mini;

			if (!range)
				range = 1.0;

			float c = 255 * (dst[i][j] - mini) / range;

//		  float c = dst[i][j];

			if (c < 0)
				c = 0;
			if (c > 255)
				c = 255;

			dst[i][j] = c;
		}
	}

//   mini = mean - rvals.cvar*var;
//   maxi = mean + rvals.cvar*var;
//   range = maxi - mini;
//
//   if (!range)
//     range = 1.0;
//
//
//   //归一化过程，待定
//   for (i = 0; i < size; i+= bytes)
//     {
//       psrc = src + i;
//       pdst = dst + i;
//
//       for (j = 0 ; j < 3 ; j++)
//         {
//           float c = 255 * ( pdst[j] - mini ) / range;
//
// //          psrc[j] = (uchar) CLAMP (c, 0, 255);
// 		  if(c < 0)
// 			  c = 0;
// 		  if(c > 255)
// 			  c = 255;
//
// 		  psrc[j] = c;
//         }
//     }

//  free (dst);

// 考虑如何转回3通道

	for (i = 0; i != channelsize; ++i) {
		setR(&src[i], dst[2][i]);
		setG(&src[i], dst[1][i]);
		setB(&src[i], dst[0][i]);
	}

//	for(i = 0; i != width * height; ++i)
//	{
//		transHSV(getR(src[i]), getG(src[i]), getB(src[i]), &hh, &ss, &vv);
//		setR(&src[i], hh);
//		setG(&src[i], ss);
//		setB(&src[i], vv);
//	}


	for (i = 0; i != 3; ++i)
		free(dst[i]);
	free(dst);

}
