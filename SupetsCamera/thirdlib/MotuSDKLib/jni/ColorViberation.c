#include <android/log.h>
#include "math.h"
#include "operation.h"
#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
#include <math.h>
#include "mtprocessor.h"

#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)
#define min(x,y) (x>y?y:x)
#define max(x,y) (x>y?x:y)
#define PI 3.1415926

float *ssArray, *lArray;
int *ViberationBackup;

void saturationCalculation(int *srcPixArray, int w, int h)
{
	int i;
	int a, r, g, b;
	for (i = 0; i < w * h; i++)
	{
		int colorT = srcPixArray[i];
		r = (colorT >> 16) & 0xFF;
		g = (colorT >> 8) & 0xFF;
		b = colorT & 0xFF;
		a = (colorT >> 24) & 0xFF;

		float rgbMax = r / 255.0;
		float rgbMin = g / 255.0;
		float rgbC = b / 255.0;
		if (rgbMax < rgbC)
			Swap(&rgbMax, &rgbC);
		if (rgbMax < rgbMin)
			Swap(&rgbMax, &rgbMin);
		if (rgbMin> rgbC)
			Swap(&rgbMin, &rgbC);
		float delta = rgbMax - rgbMin;
		float value = rgbMax + rgbMin;
		float S, L = value / 2;
		if(value == 2)
			value = 1.9999;
		if(value == 0)
			value = 0.0001;
		if (L < 0.5)
			S = delta / value;
		else  S = delta  / (2 - value);
		ssArray[i] = S;
	}

	float minV = ssArray[0], maxV = ssArray[0];
	for(i = 0; i != w * h; ++i)
	{
		if(ssArray[i] > maxV)
			maxV = ssArray[i];
		if(ssArray[i] < minV)
			minV = ssArray[i];
	}

	for(i = 0; i != w * h; ++i)
		ssArray[i] = (ssArray[i] - minV) / (maxV - minV);
}

void ViberationInitial(int *srcPixArray, int w, int h)
{
	ssArray = (float *)malloc(sizeof(float) * w * h);
	ViberationBackup = (int *)malloc(sizeof(int) * w * h);
	saturationCalculation(srcPixArray, w, h);
}

void ViberationControl(int *srcPixArray, int w, int h, float  degree)
{
	int i, mtValue;
	memcpy(ViberationBackup, srcPixArray, sizeof(int) * w * h);
//	float scale = 4.0 * degree - 2.0;
//	if(scale < - 1)
//		scale = -1;
	float scale = -1.0 + 1.8 * degree;
	saturationfilter(ViberationBackup, w, h, scale);
	LOGW("The Value of degree, scale: %f %f", degree, scale);

	for(i = 0; i != w * h; ++i)
	{
		float tmpSS =  1.0 - ssArray[i];
		if(scale < 0)
			tmpSS = 1.0;
		mtValue = getR(ViberationBackup[i]) * tmpSS + getR(srcPixArray[i]) * (1 - tmpSS);
		setR(&srcPixArray[i], mtValue);

		mtValue = getG(ViberationBackup[i]) * tmpSS + getG(srcPixArray[i]) * (1 - tmpSS);
		setG(&srcPixArray[i], mtValue);

		mtValue = getB(ViberationBackup[i]) * tmpSS + getB(srcPixArray[i]) * (1 - tmpSS);
		setB(&srcPixArray[i], mtValue);
	}
}

void ViberationRelease()
{
	free(ViberationBackup);
	free(ssArray);
}
