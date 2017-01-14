#include "operation.h"

#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
#include <math.h>
#include <android/log.h>
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)

typedef unsigned char uchar;

void makeHistogram(int* pixels, int width, int height, int* hR, int* hG, int* hB) {
	int i;
	int r, g, b;
	int size = width * height;
	memset(hR, 0, 256 * sizeof(int));
	memset(hG, 0, 256 * sizeof(int));
	memset(hB, 0, 256 * sizeof(int));
	for (i = 0; i < size; i ++) {
		getRGB(pixels[i], &r, &g, &b);
		hR[r] ++;
		hG[g] ++;
		hB[b] ++;
	}
}

//cubic spline interpolation 三次样条插值
void initCubicSplineInterpolation(int x0, int y0, int x1, int y1, int x2, int y2, int* outh0, int* outh1, double* outz1) {
	int h0, h1;
	*outh0 = x1 - x0;
	*outh1 = x2 - x1;
	h0 = *outh0;
	h1 = *outh1;
	*outz1 = 3 * ((y2 - y1) * 1.0 / (h1 * (h0 + h1)) - (y1 - y0) * 1.0 / (h0 * (h0 + h1)));
}

int computeCubicSplineInterpolation(int x0, int y0, int x1, int y1, int x2, int y2, int h0, int h1, double z1, int x) {
	int y;
	if (x >= x0 && x <= x1) {
		y = z1 * cubic(x - x0) / (6 * h0) + (y1 * 1.0 / h0 - h0 * z1 / 6) * (x - x0) + y0 * 1.0 * (x1 - x) / h0;
	} else {
		y = z1 * cubic(x2 - x) / (6 * h1) + y2 * 1.0 / h1 * (x - x1) + (y1 * 1.0 / h1 - h1 * z1 / 6) * (x2 - x);
	}
	return y;
}

int cubic(int x) {
	return x * x * x;
}

void transHSV(int r, int g, int b, int* h, int* s, int* v) {
	double fv, fs, fh;
	*v = max(r, g, b);
	fv = *v / 255.0;
	if (fv == 0) {
		fs = 0;
	} else {
		fs = (fv - min(r, g, b) / 255.0) / fv;
	}
	if (*v == r) {
		fh = (g - b) * 60 / 255.0 / fs;
	} else if (*v == g) {
		fh = 180 + (b - r) * 60 / 255.0 / fs;
	} else {
		fh = 240 + (r - g) * 60 / 255.0 / fs;
	}
	if (fh < 0) {
		fh += 360;
	}
	*s = (int)(fs * 255);
	*h = (int)fh / 2;
}

int max(int a, int b, int c) {
	if (a > b) {
		if (a > c) {
			return a;
		} else {
			return c;
		}
	} else {
		if (b > c) {
			return b;
		} else {
			return c;
		}
	}
}

int min(int a, int b, int c) {
	if (a > b) {
		if (b > c) {
			return c;
		} else {
			return b;
		}
	} else {
		if (a > c) {
			return c;
		} else {
			return a;
		}
	}
}

void transRGB(int* pixels, int width, int height, int* transfunc) {
	int n = 0;
	int i, j;
	int r, g, b;
	for (i = 0; i < height; i++) {
		for (j = 0; j < width; j++) {
			getRGB(pixels[n], &r, &g, &b);
			r = transfunc[r];
			g = transfunc[g];
			b = transfunc[b];
			pixels[n++] = (255 << 24) | (r << 16) | (g << 8) | b;
		}
	}
}

void transARGB(int* pixels, int width, int height, int* transfunc) {
	int n = 0;
	int i, j;
	int a, r, g, b;
	for (i = 0; i < height; i++) {
		for (j = 0; j < width; j++) {
			getRGBA(pixels[n], &r, &g, &b, &a);
			r = transfunc[r];
			g = transfunc[g];
			b = transfunc[b];
			pixels[n++] = (a << 24) | (r << 16) | (g << 8) | b;
		}
	}
}

int getAvarage(int *pixels, int w, int h, int x, int y, int ra) {
	int i, j;
	int sr, sg, sb;
	int r, g, b;
	int sn;
	sn = (ra + ra + 1) * (ra + ra + 1);
	sr = 0;
	sg = 0;
	sb = 0;
	for (i = x - ra; i <= x + ra; i++) {
		for (j = y - ra; j <= y + ra; j++) {
			getRGB(pixels[j * w + i], &r, &g, &b);
			sr += r;
			sg += g;
			sb += b;
		}
	}
	sr /= sn;
	sg /= sn;
	sb /= sn;
	return (255 << 24) + (sr << 16) + (sg << 8) + sb;
}

void FilterMode(int *dst, int lWidth, int lHeight, double *dMode, int threshold) {

	int i, j, k, l;
	int r, g, b;
	double sum_r, sum_g, sum_b;

	if (threshold <= 0)
		threshold = 256;
	for (i = 2; i < lHeight - 2; i++) {
		for (j = 2; j < lWidth - 2; j++) {
			sum_r = 0.0;
			sum_g = 0.0;
			sum_b = 0.0;
			for (k = -2; k <= 2; k++)
				for (l = -2; l <= 2; l++) {
					int color = dst[(i + k) * lWidth + j + l];
					getRGB(color, &r, &g, &b);
					int ith = (k + 2) * 5 + l + 2;
					sum_r += r * dMode[ith];
					sum_g += g * dMode[ith];
					sum_b += b * dMode[ith];
				}
			if (sum_r > 255)
				sum_r = 255;
			if (sum_g > 255)
				sum_g = 255;
			if (sum_b > 255)
				sum_b = 255;
			if (sum_r < 0)
				sum_r = 0;
			if (sum_g < 0)
				sum_g = 0;
			if (sum_b < 0)
				sum_b = 0;

			getRGB(dst[i * lWidth + j], &r, &g, &b);

			if (abs(sum_r - r) > threshold)
				sum_r = r;
			if (abs(sum_g - g) > threshold)
				sum_g = g;
			if (abs(sum_b - b) > threshold)
				sum_b = b;
			dst[i * lWidth + j] = (255 << 24) + ((int) (sum_r + 0.5) << 16)
					+ ((int) (sum_g + 0.5) << 8) + (int) (sum_b + 0.5);
		}
	}
}

void fastAverageBlur1(int* srcPixArray, int width, int height, int radius) {
	int size = 2 * radius + 1; //模板直径
	int area = size * size; //模板面积

	int i, k, y; //下标
	int row, col; //行列下标
	int color, r, g, b;
	int index, index1, index2;

	int * tValues = (int *) malloc(3 * width * sizeof(int)); //一行元素的模板单列和
	int* tmp = (int *) malloc(width * height * sizeof(int)); //像素rgb临时存储

	memset(tValues, 0, 3 * width * sizeof(int));
	for (k = 0; k < size; k++) { //前size行元素列和
		for (y = 0, index = 0; y < width; y++) {
			color = srcPixArray[k * width + y];
			tValues[index++] += (color >> 16) & 0xFF;
			tValues[index++] += (color >> 8) & 0xFF;
			tValues[index++] += (color) & 0xFF;
		}
	}

	for (row = radius; row < height - radius; row++) {
		r = g = b = 0;

		for (k = 0, index = 0; k < size; k++) {
			r += tValues[index++];
			g += tValues[index++];
			b += tValues[index++];
		}

		col = radius;

		tmp[row * width + col] = ((int) 255 << 24) | ((int) r / area << 16)
				| ((int) g / area << 8) | (int) b / area;

		for (col = radius + 1; col < width - radius; col++) {
			index1 = (col - radius - 1) * 3;
			index2 = (col + radius) * 3;

			r = r - tValues[index1 + 0] + tValues[index2 + 0];
			g = g - tValues[index1 + 1] + tValues[index2 + 1];
			b = b - tValues[index1 + 2] + tValues[index2 + 2];

			tmp[row * width + col] = ((int) 255 << 24) | ((int) r / area << 16)
					| ((int) g / area << 8) | (int) b / area;
		}

		if (row == height - radius - 1)
			break;

		index1 = (row - radius) * width;
		index2 = (row + radius + 1) * width;
		for (y = 0; y < width; y++) {
			color = srcPixArray[index1++];
			index = y * 3;
			tValues[index++] -= (color >> 16) & 0xFF;
			tValues[index++] -= (color >> 8) & 0xFF;
			tValues[index] -= (color) & 0xFF;

			color = srcPixArray[index2++];
			index = y * 3;
			tValues[index++] += (color >> 16) & 0xFF;
			tValues[index++] += (color >> 8) & 0xFF;
			tValues[index] += (color) & 0xFF;

		}
	}

	memcpy(srcPixArray, tmp, width * height * sizeof(int));

	free(tValues);
	free(tmp);
}

void LinearGradient(int* pixels, int colorFrom, int colorTo, int w, int h,
		int x, int y, int r) {
	int i, j;
	int red, green, blue, redFrom, greenFrom, blueFrom, redTo, greenTo, blueTo;
	getRGB(colorFrom, &redFrom, &greenFrom, &blueFrom);
	getRGB(colorTo, &redTo, &greenTo, &blueTo);

	redTo = redTo - redFrom;
	greenTo = greenTo - greenFrom;
	blueTo = blueTo - blueFrom;

	float t;
	int pos = 0;
	for (i = 0; i < h; i++) {
		for (j = 0; j < w; j++) {
			t = sqrt(distanceSquare(i, j, y, x)) / r;
			red = redFrom + redTo * t;
			green = greenFrom + greenTo * t;
			blue = blueFrom + blueTo * t;

			adjustRGB(&red, &green, &blue);
			pixels[pos++] = (255 << 24) | (red << 16) | (green << 8) | blue;
		}
	}
}

void AdjustBrightness(int* srcPixArray, int w, int h, float IncRate) {
	int i;
	int color, gray;

	for (i = 0; i < w * h; i++) {
		color = srcPixArray[i];

		gray = color & 0xFF;

		if (gray > 125)
			gray = gray + gray * IncRate;

		if (gray > 255)
			gray = 255;

		if (gray <= 0)
			gray = 0;
		srcPixArray[i] = (255 << 24) | (gray << 16) | (gray << 8) | gray;
	}
}

void minfilter(int* srcPixArray, int w, int h, int rad) {
	int n = w * h;
	int x, y, xx, yy, i, j, min_r, min_g, min_b;
	int r, g, b, a, color;

	int *butter = (int*) malloc(n * sizeof(int));
	memcpy(butter, srcPixArray, n * sizeof(int));

	for (y = 0; y < h; y++) {
		for (x = 0; x < w; x++) {
			min_r = min_g = min_b = 255;
			for (i = -rad; i <= rad; i++) {
				yy = y + i;
				if (yy < 0 || yy >= h)
					continue;
				for (j = -rad; j <= rad; j++) {
					xx = x + j;
					if (xx < 0 || xx >= w)
						continue;

					color = butter[xx + w * yy];
					a = (color >> 24) & 0xFF;
					r = (color >> 16) & 0xFF;
					g = (color >> 8) & 0xFF;
					b = (color) & 0xFF;

					if (min_r > r)
						min_r = r;
					if (min_g > g)
						min_g = g;
					if (min_b > b)
						min_b = b;

				}
			}

			srcPixArray[x + w * y] = (a << 24) | (min_r << 16) | (min_g << 8)
					| min_b;
		}
	}
	free(butter);
}

void Invert(int* srcPixArray, int w, int h) {
	int i;

	for (i = 0; i < w * h; i++) {
		int color = srcPixArray[i];
		int a = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;

		r = 255 - r;
		g = 255 - g;
		b = 255 - b;

		srcPixArray[i] = (a << 24) | (r << 16) | (g << 8) | b;
	}
}

int colorLevel(int input, float minInput, float mid, float maxInput,
		float minOutput, float maxOutput) {
	float midInput = powf(0.5, mid) * (maxInput - minInput) + minInput;
	if (input >= maxInput) {
		return maxOutput;
	} else if (input <= minInput) {
		return minOutput;
	} else if (input <= midInput) {
		return (input - minInput) / (midInput - minInput)
				* (maxOutput - minOutput) / 2 + minOutput;
	} else {
		return maxOutput
				- (maxInput - input) / (maxInput - midInput)
						* (maxOutput - minOutput) / 2;
	}
}

void convertRgb2Yuv(int* pixels, int w, int h) {
	int i, size;
	int r, g, b, a;
	int y, u, v;
	size = w * h;
	for (i = 0; i < size; i ++) {
		getRGBA(pixels[i], &r, &g, &b, &a);
		y = 0.299 * r + 0.587 * g + 0.114 * b;
		u = 0.436 * (b - y) / (1 - 0.114) + 128;
		v = 0.615 * (r - y) / (1 - 0.299) + 128;
		pixels[i] = (a << 24) + (gan(y) << 16) + (gan(u) << 8) + gan(v);
	}
}

void convertYuv2Rgb(int* pixels, int w, int h) {
	int i, size;
	int r, g, b, a;
	int y, u, v;
	size = w * h;
	for (i = 0; i < size; i ++) {
		getRGBA(pixels[i], &y, &u, &v, &a);
		r = y + 1.13983 * (v - 128);
		g = y - 0.39465 * (u - 128) - 0.58060 * (v - 128);
		b = y + 2.03211 * (u - 128);
		pixels[i] = (a << 24) + (gan(r) << 16) + (gan(g) << 8) + gan(b);
	}
}

void yuv420sp2rgb(int* rgb, unsigned char* yuv, int w, int h) {
    int             sz;
    int             i;
    int             j;
    int             Y;
    int             Cr = 0;
    int             Cb = 0;
    int             pixPtr = 0;
    int             jDiv2 = 0;
    int             R = 0;
    int             G = 0;
    int             B = 0;
    int             cOff;
    int rgbDataSize = 0;
    sz = w * h;

	if(rgbDataSize < sz) {
		rgbDataSize = sz;
	}
	for(j = 0; j < h; j++) {
             pixPtr = j * w;
             jDiv2 = j >> 1;
             for(i = 0; i < w; i++) {
                     Y = yuv[pixPtr];
					 if(Y < 0) Y += 255;
                     if((i & 0x1) != 1) {
                             cOff = sz + jDiv2 * w + (i >> 1) * 2;
                             Cb = yuv[cOff];
                             if(Cb < 0) Cb += 127; else Cb -= 128;
                             Cr = yuv[cOff + 1];
                             if(Cr < 0) Cr += 127; else Cr -= 128;
                     }
                     R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
                     if(R < 0) R = 0; else if(R > 255) R = 255;
                     G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1) + (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
                     if(G < 0) G = 0; else if(G > 255) G = 255;
                     B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
                     if(B < 0) B = 0; else if(B > 255) B = 255;
                     rgb[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;
             }
    }
}

void transToGray(int* src, int width, int height, unsigned char* dst)
{
	int i, r, g, b, a, color;
	for (i = 0; i < width * height; i ++) {
		color = src[i];
		a = color >> 24;
		r = (color >> 16) & 0xFF;
		g = (color >> 8) & 0xFF;
		b = (color) & 0xFF;
		if (a == 0) {
			dst[i] = 255;
		} else {
			dst[i] = (r + g + b) / 3;
		}
	}
}

void transReversedBGR(int* src, int width, int height, unsigned char* dst) {
	int i, j, r, g, b, color, op, dp;
	int n = width * height;
	for (i = 0; i < height; i ++) {
		for (j = 0; j < width; j ++) {
			op = i * width + j;
			color = src[op];
			r = (color >> 16) & 0xFF;
			g = (color >> 8) & 0xFF;
			b = (color) & 0xFF;
			dp = ((height - i - 1) * width + j) * 3;
			dst[dp] = b;
			dst[dp + 1] = g;
			dst[dp + 2] = r;
		}
	}
}

ExpVar computeHueExpectationAndVariance(int* src, int width, int height) {
	int i, r, g, b, a, color;
	int h, s, v;
	int n = width * height;
	long long eh, dh;
	eh = 0;
	dh = 0;
	for (i = 0; i < n; i ++) {
		color = src[i];
		a = color >> 24;
		r = (color >> 16) & 0xFF;
		g = (color >> 8) & 0xFF;
		b = (color) & 0xFF;
		transHSV(r, g, b, &h, &s, &v);
		eh += h;
		dh += h * h;
		src[i] = 0;
	}
	eh /= n;
	dh = (dh - n * eh) / (n - 1);
	ExpVar ev;
	ev.expect = eh;
	ev.var = dh;
	return ev;
}
