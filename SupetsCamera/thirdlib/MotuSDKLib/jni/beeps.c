#include <android/log.h>
#include "math.h"
#include "operation.h"
#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
#include <math.h>
#include "beeps.h"

#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)
#define min(x,y) (x>y?y:x)
#define max(x,y) (x>y?x:y)

//double ZETA_3 = 1.2020569031595942853997381615114499907649862923404988817922715553418382057;
double spatialDecay = 0.01;
double photometricStandardDeviation = 20.0;
int rangeFilter = 0;
double cKernel = 0;
double rho = 0;
double spatialContraDecay = 0;
int startIndex = 0;
int width;
int height;
int channelSize;
double mu;

void setupProgressive(int sharedRangeFilter, double photometricStandardDeviation, double sharedSpatialContraDecay)
{
	rangeFilter = sharedRangeFilter;
//	LOGW("the Value of rangeFilter: %f", rangeFilter);
	spatialContraDecay = sharedSpatialContraDecay;
//	LOGW("the Value of spatialContraDecay: %f", spatialContraDecay);
	rho = 1.0 + spatialContraDecay;
//	LOGW("the Value of rho: %f", rho);
	cKernel = -0.5 / (photometricStandardDeviation * photometricStandardDeviation);
//	LOGW("the Value of cKernel: %f", cKernel);
}
void setupGain(double spatialContraDecay, double *mu)
{
	*mu = (1.0 - spatialContraDecay) / (1.0 + spatialContraDecay);
}

void setupRegressive(int sharedRangeFilter, double photometricStandardDeviation, double sharedSpatialContraDecay)
{
	rangeFilter = sharedRangeFilter;
	spatialContraDecay = sharedSpatialContraDecay;
	rho = 1.0 + spatialContraDecay;
	cKernel = -0.5 / (photometricStandardDeviation * photometricStandardDeviation);
}

void gainRun(double *data, int startIndex, int length, double mu)
{
	int k, K;
	for (k = startIndex, K = startIndex + length; (k < K); k++)
	{
		data[k] *= mu;
	}
}

void progressiveRun(double *data, int startIndex, int length, double rho)
{
	//每次调用的时候这个startIndex的值不一样，需要外部调用
	double mu = 0.0;
	int k, K;
	data[startIndex] /= rho;
	for (k = startIndex + 1, K = startIndex + length; (k < K); k++)
	{
		mu = data[k] - rho * data[k - 1];
		mu = spatialContraDecay * exp(cKernel * mu * mu);
		data[k] = data[k - 1] * mu + data[k] * (1.0 - mu) / rho;
	}
}

void regressiveRun(double *data, int startIndex, int length, double rho)
{
	double mu = 0.0;
	int k;
	data[startIndex + length - 1] /= rho;
	for (k = startIndex + length - 2; (startIndex <= k); k--)
	{
		mu = data[k] - rho * data[k + 1];
		mu = spatialContraDecay * exp(cKernel * mu * mu);
		data[k] = data[k + 1] * mu + data[k] * (1.0 - mu) / rho;
	}
}

void beepsSetupAll(int *pixels, int w, int h)
{
	width = w;
	height = h;
	channelSize = width * height;
	setupProgressive(rangeFilter, photometricStandardDeviation, 1.0 - spatialDecay);
	setupGain(1.0 - spatialDecay, &mu);
//	LOGW("the Value of mu: %f", mu);
	setupRegressive(rangeFilter,  photometricStandardDeviation, 1.0 - spatialDecay);
}

void beepsOverlay(int *src, int *dst)
{
	int i, tmpValue;
	for(i = 0; i != channelSize; ++i)
	{
		tmpValue  = (getR(dst[i]) * getR(src[i]) + getR(src[i]) * (255.0 - getR(src[i]))) / 255.0;
		setR(&dst[i], tmpValue);
		tmpValue  = (getG(dst[i]) * getR(src[i]) + getG(src[i]) * (255.0 - getR(src[i]))) / 255.0;
		setG(&dst[i], tmpValue );
		tmpValue  = (getB(dst[i]) * getR(src[i]) + getB(src[i]) * (255.0 - getR(src[i]))) / 255.0;
		setB(&dst[i], tmpValue);
	}
}

void beepsLightColor(int *src, int *dst)
{
	int i, j, tmp;
	float per = 0;
//	int tMax = -512, tMin = 512;
	for(i = 0; i != width * height; ++i)
	{
		tmp = 0;
		for(j = 0; j != 3; ++j)
		{
			tmp += (( src[i] >> (8 * j) ) & 0xFF);
			tmp -=  (( dst[i] >> (8 * j) ) & 0xFF);
		}
		if(tmp > 0)
		{
			src[i] = src[i];
		}
		else
		{
			src[i] = dst[i];
		}
	}

}

void beepsDetailRecover(int *src, int *dst)
{
	//先对原图做一下细节增强，简单加速如下
	float boxValue[9] = {0, -1, 0, -1, 5., -1, 0, -1, 0};
	boxFilter(src, width, height, boxValue, 3);
//	usmProcess(src, width, height, 50, 8, 4);
	beepsLightColor(src, dst);
//	memcpy(dst, src, sizeof(int) * width * height);
//	lightColor(dst, src, width, height);
	int i, tmp;
	for(i = 0; i != width * height; ++i)
	{
		tmp = getR(src[i]) * 0.25 + getR(dst[i]) * 0.75;
		setR(&dst[i], tmp);
		tmp = getG(src[i]) * 0.25 + getG(dst[i]) * 0.75;
		setG(&dst[i], tmp);
		tmp = getB(src[i]) * 0.25 + getB(dst[i]) * 0.75;
		setB(&dst[i], tmp);
	}
}

void BEEPSHorizontalVertical(double *data, int w, int h)
{
	LOGW("HorizontalVertical has entered");
//	width = w;
//	height = h;
//	channelSize = w * h;
	double *g = (double *)malloc(sizeof(double) * w * h);
	double *p = (double *)malloc(sizeof(double) * w * h);
	double *r  = (double *)malloc(sizeof(double) * w * h);
	int i, j, k2, k, k1, K;
	for(j = 0; j != w * h; ++j)
	{
		g[j] = data[j];
	}
	memcpy(p, g, sizeof(double) * w * h);
	memcpy(r, g, sizeof(double) * w * h);
	for (k2 = 0; (k2 < height); k2++)
	{
		progressiveRun(p, k2 * width, width, rho);
		gainRun(g, k2 * width, width, mu);
		regressiveRun(r, k2 * width, width, rho);
	}

	for (k = 0, K = channelSize; (k < K); k++)
	{
		r[k] += p[k] - g[k];
	}
	int m = 0;
	for (k2 = 0; (k2 < height); k2++)
	{
		int n = k2;
		for (k1 = 0; (k1 < width); k1++)
		{
			g[n] = r[m++];
			n += height;
		}
	}

	memcpy(p, g, sizeof(double) * w * h);
	memcpy(r, g, sizeof(double) * w * h);

	for (k1 = 0; (k1 < width); k1++)
	{
		progressiveRun(p, k1 * height, height, rho);
		gainRun(g, k1 * height, height, mu);
		regressiveRun(r, k1 * height, height, rho);
	}
	for (k = 0, K = channelSize; (k < K); k++) {
		r[k] += p[k] - g[k];
	}
	m = 0;
	for (k1 = 0; (k1 < width); k1++)
	{
		int n = k1;
		for (k2 = 0; (k2 < height); k2++)
		{
			data[n] = (float) r[m++];
			n += width;
		}
	}

	free(g);
	free(r);
	free(p);
}

void BEEPSVerticalHorizontal(double *data, int w, int h)
{
	LOGW("VerticalHorizontal has entered");
//	width = w;
//	height = h;
//	channelSize = w * h;
	double *g = (double *)malloc(sizeof(double) * w * h);
	double *p = (double *)malloc(sizeof(double) * w * h);
	double *r = (double *)malloc(sizeof(double) * w * h);
	int k, k1, k2, K;
	int m = 0;
	for (k2 = 0; (k2 < height); k2++) {
		int n = k2;
		for (k1 = 0; (k1 < width); k1++) {
			g[n] = (double) data[m++];
			n += height;
		}
	}

	    int i, j;
		memcpy(p, g, sizeof(double) * w * h);
		memcpy(r, g, sizeof(double) * w * h);
		for (k2 = 0; (k2 < width); k2++) {
			progressiveRun(p, k2 * height, height, rho);
			gainRun(g, k2 * height, height, mu);
			regressiveRun(r, k2 * height, height, rho);
		}

		for (k = 0, K = channelSize; (k < K); k++)
		{
			r[k] += p[k] - g[k];
		}
		m = 0;
		for (k1 = 0; (k1 < width); k1++) {
			int n = k1;
			for (k2 = 0; (k2 < height); k2++) {
				g[n] = r[m++];
				n += width;
			}
		}

		memcpy(p, g, sizeof(double) * w * h);
		memcpy(r, g, sizeof(double) * w * h);

		for (k1 = 0; (k1 < height); k1++)
		{
			progressiveRun(p, k1 * width, width, rho);
			gainRun(g, k1 * width, width, mu);
			regressiveRun(r, k1 * width, width, rho);
		}

		for (k = 0, K = channelSize; (k < K); k++)
		{
			data[k] = (float) (p[k] - g[k] + r[k]);
		}

		free(g);
		free(r);
		free(p);
}
