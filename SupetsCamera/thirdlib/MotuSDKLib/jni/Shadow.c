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

//写shadow and highlight的,要trans到LAB空间中

void shadowProcess(int *srcPixArray, int w, int h, int lowV, int highV)
{
	int i;
	int minX = 999, maxX = -1;
	for(i = 0; i != w * h; ++i)
	{
		if(srcPixArray[i] > maxX)
			maxX = srcPixArray[i];
		if(srcPixArray[i] < minX)
			minX = srcPixArray[i];
	}
	highV = min(maxX, highV);
//	LOGW("The Value of MAX and MIN: %d %d", maxX, minX);
	for(i = 0; i != w * h; ++i)
	{
		srcPixArray[i] = (srcPixArray[i] - minX) / (maxX - minX) * (highV - lowV) + lowV;
	}
}

void highlightProcess(int *srcPixArray, int w, int h, int lowV, int highV)
{
	int i;
	int minX = 999, maxX = -1;
	for(i = 0; i != w * h; ++i)
	{
		if(srcPixArray[i] > maxX)
			maxX = srcPixArray[i];
		if(srcPixArray[i] < minX)
			minX = srcPixArray[i];
	}
	lowV = max(minX, lowV);
	for(i = 0; i != w * h; ++i)
	{
		srcPixArray[i] = (srcPixArray[i] - minX) / (maxX - minX) * (highV - lowV) + lowV;
	}
}

void shadowAndHighlight(int *srcPixArray, int w, int h, int lowV, int highV)
{
	int tmpA, tmpB, tl, i;
	int *srcTmp = (int *)malloc(sizeof(int) * w * h);
	int *tmpL = (int *)malloc(sizeof(int) * w * h);
	int *tmpLl = (int *)malloc(sizeof(int) * w * h);
	for(i = 0; i != w * h; ++i)
	{
		tmpL[i] = getR(srcPixArray[i]);
		tmpA = getG(srcPixArray[i]);
		tmpB = getB(srcPixArray[i]);
//		LOGW("The Value of tmpL: %d", tmpL[i]);
		transRgb2Lab(&tmpL[i], &tmpA, &tmpB);
		tmpLl[i] = tmpL[i];
		srcTmp[i] = tmpL[i];
//		LOGW("The Value of tmpL: %d", tmpL[i]);
	}
	//先对一个做个模糊
	fastAverageBlur(srcTmp, w, h, 9);
////	LOGW("fastAverageBlur has ended");
//	shadowProcess(tmpL, w, h, 0, highV);
////	LOGW("shadowProcess has ended");
////做个叠加
//	for(i = 0; i != w * h; ++i)
//	{
//		tl = getR(srcPixArray[i]);
//		tmpA = getG(srcPixArray[i]);
//		tmpB = getB(srcPixArray[i]);
//
//		transRgb2Lab(&tl, &tmpA, &tmpB);
//
//		tl = ( tl * srcTmp[i] + tmpL[i] * (255.0 - srcTmp[i]) ) / 255.0 * 0.2 + tl * 0.8;
//		//要有个叠加的程度参数是吗
////		if(tl < 0)
////			tl = 0;
////		if(tl > 255)
////			tl = 255;
//
//		transLab2Rgb(&tl, &tmpA, &tmpB);
//
//		setR(&srcPixArray[i], tl);
//		setG(&srcPixArray[i], tmpA);
//		setB(&srcPixArray[i], tmpB);
//	}

//	highlightProcess(tmpLl, w, h, lowV, highV);
	//做个叠加

	for(i = 0; i != w * h; ++i)
	{
		tl = getR(srcPixArray[i]);
		tmpA = getG(srcPixArray[i]);
		tmpB = getB(srcPixArray[i]);

		transRgb2Lab(&tl, &tmpA, &tmpB);

		if(tmpLl[i] > highV)
		{
			tmpLl[i] = tl + 50;
			tl = ( tl * srcTmp[i] + tmpLl[i] * (255.0 - srcTmp[i]) ) / 255.0; // * 0.5 + tl * 0.5; //
		}
		else if(tmpLl[i] > highV - 50)
		{
			tmpLl[i] = tl + highV - tmpLl[i];
			tl = ( tl * srcTmp[i] + tmpLl[i] * (255.0 - srcTmp[i]) ) / 255.0; // * 0.5 + tl * 0.5; //
		}
		else
			tl = tmpLl[i];

		if(tl < 0)
			tl = 0;
		if(tl > 255)
			tl = 255;

		transLab2Rgb(&tl, &tmpA, &tmpB);

		setR(&srcPixArray[i], tl);
		setG(&srcPixArray[i], tmpA);
		setB(&srcPixArray[i], tmpB);
	}

	free(srcTmp);
	free(tmpL);
	free(tmpLl);
}
