#include "bilateral.h"
#include "mtprocessor.h"
#include <math.h>
#include <time.h>
#include <android/log.h>

#define _Debug 0
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)

#define minC(a,b) ((a > b) ? b : a)
#define maxC(a,b) ((a > b) ? a : b)

void CreateGauss(float sigma, float *GaussKernel, int radius)
{
	float i,j,dValue, dSum = 0;
	int nPos = 0;
	for(i = -radius/2; i <= radius/2; ++i)
	{
		for(j = -radius/2; j <= radius/2; ++j)
		{
			dValue = exp(-(1.0 / 2) * (i * i  + j * j) / (sigma * sigma));
			GaussKernel[(nPos++)] = dValue;
			dSum += dValue;
		}
	}

	for(nPos = 0; nPos != radius * radius; ++nPos)
	{
		GaussKernel[nPos] /= dSum;
#ifdef _Debug
		LOGW("GaussKernel[nPos] %f", GaussKernel[nPos]);
#endif
	}
}

void CreateWeight(float *Weight, float delta)
{
	int i;
	for(i = 0; i != 256; ++i)
	{
		Weight[i] = exp(-(1.0/2) * i * i / (delta * delta));
#ifdef _Debug
		LOGW("Weight[i] %f", Weight[i]);
#endif
	}
}
//可以拿函数指针改写
void filterGauss(int *pixels, int w, int h, int radius, float *fpArray, float *Weight_Test)
{
#ifdef _Debug
		LOGW("filterGauss has began");
#endif
	int i,j,k,l, realk, reall, weighttmpR, weighttmpG, weighttmpB;
	float fResultR, fResultG, fResultB, WeightR, WeightG, WeightB, Sum_WeightR, Sum_WeightG, Sum_WeightB;
	int *pixelsResult = (int *)malloc(sizeof(int) * w * h);
	memcpy(pixelsResult, pixels, sizeof(int) * w * h);
	LOGW("filterGauss memcpy finished");
	for(i = 0; i != h; ++i)
	{
		for(j = 0; j != w; ++j)
		{
			fResultR = 0;
			fResultG = 0;
			fResultB = 0;
			WeightR = 0;
			WeightG = 0;
			WeightB = 0;
			Sum_WeightR = 0;
			Sum_WeightG = 0;
			Sum_WeightB = 0;
//			LOGW("filterGauss intialization over");
			for(k = -radius/2; k <= radius/2; ++k)
			{
//				if(k < 0)
//					realk = abs(k);
//				if(k >= h)
//					realk = h - k + h;
				if(k + i < 0)
					continue;
				if(k + i > h - 1)
					break;
				for(l = -radius/2; l <= radius/2; ++l)
				{
//					if(l < 0)
//						reall = abs(l);
//					if(l >= w)
//						reall = w - l + w;
					if(l + j< 0)
						continue;
					if(l + j> w - 1)
						break;

					weighttmpR = getR(pixels[i * w + j]) - getR(pixels[(i + k)* w + j + l]);
					weighttmpG = getG(pixels[i * w + j]) - getG(pixels[(i + k)* w + j + l]);
					weighttmpB = getB(pixels[i * w + j]) - getB(pixels[(i + k)* w + j + l]);

					WeightR = Weight_Test[abs(weighttmpR)];
					WeightG = Weight_Test[abs(weighttmpG)];
					WeightB = Weight_Test[abs(weighttmpB)];

					fResultR += getR(pixels[(i + k) * w + j + l]) * fpArray[(k + radius / 2) * radius + (l + radius / 2)] * WeightR;
					fResultG += getG(pixels[(i + k) * w + j + l]) * fpArray[(k + radius / 2) * radius + (l + radius / 2)] * WeightG;
					fResultB += getB(pixels[(i + k) * w + j + l]) * fpArray[(k + radius / 2) * radius + (l + radius / 2)] * WeightB;

					Sum_WeightR += fpArray[(k + radius / 2) * radius + (l + radius / 2)] * WeightR;
					Sum_WeightG += fpArray[(k + radius / 2) * radius + (l + radius / 2)] * WeightG;
					Sum_WeightB += fpArray[(k + radius / 2) * radius + (l + radius / 2)] * WeightB;
				}
			}
			setR(&pixelsResult[i * w + j], (fResultR / Sum_WeightR));
			setG(&pixelsResult[i * w + j], (fResultG / Sum_WeightG));
			setB(&pixelsResult[i * w + j], (fResultB / Sum_WeightB));
		}
	}
	LOGW("GaussFilter has finished");
	memcpy(pixels, pixelsResult, sizeof(int) * w * h);
}

void boxFilter(int *srcPixArray, int w, int h, float *boxValue, int boxValueSize)
{
	//boxFilter改成3个通道的
	//boxFilter有些滤波上的问题，边界不能直接continue
	int i, j, k, l;
	int *backupSrc = (int *)malloc(sizeof(int) * w * h);
	memcpy(backupSrc, srcPixArray, sizeof(int) * w * h);
	int totalValueR, totalValueG, totalValueB;
	for(i = 1; i != h - 1; ++i)
	{
		for(j = 1; j != w - 1; ++j)
		{
			totalValueR = 0;
			totalValueG = 0;
			totalValueB = 0;
			//越界和侧移的问题需要弄一下
			for(k = - 1; k <= 1; ++k)
			{
				for(l = - 1; l <= 1; ++l)
				{
//					LOGW("TestSize: %d",  (k + boxValueSize / 2) * boxValueSize * boxValueSize + l + boxValueSize / 2);
//					LOGW("boxValue: %d", boxValue[(k + boxValueSize / 2) * boxValueSize * boxValueSize + l + boxValueSize / 2]);
					totalValueR += boxValue[(k + 1) * 3 + l + 1] * ((backupSrc[(i + k) * w + (j + l)] >> (8 * 2)) & 0xFF) ;
					totalValueG += boxValue[(k + 1) *3 + l + 1] * ((backupSrc[(i + k) * w + (j + l)] >> (8 * 1)) & 0xFF) ;
					totalValueB += boxValue[(k + 1) * 3 + l + 1] * ((backupSrc[(i + k) * w + (j + l)] >> (8 * 0)) & 0xFF) ;
				}
			}
			setR(&srcPixArray[i * w + j], maxC(minC(totalValueR, 255), 0));
			setG(&srcPixArray[i * w + j], maxC(minC(totalValueG, 255), 0));
			setB(&srcPixArray[i * w + j], maxC(minC(totalValueB, 255), 0));
		}
	}
	free(backupSrc);
}

void lightColorPercent(int *src, int m, float per)
{
	int tmp  = 0;
	tmp = getR(*src) * (1.0 - per) + getR(m) * per;
	if(tmp < 0)
		tmp = 0;
	if(tmp > 255)
		tmp = 255;
	setR(src, tmp);

	tmp = getG(*src) * (1.0 - per) + getG(m) * per;
	if(tmp < 0)
		tmp = 0;
	if(tmp > 255)
		tmp = 255;
	setG(src, tmp);

	tmp = getB(*src) * (1.0 - per) + getB(m) * per;
	if(tmp < 0)
		tmp = 0;
	if(tmp > 255)
		tmp = 255;
	setB(src, tmp);

//	return *src;
}

void lightColor(int *biImage, int *boxImage, int w, int h)
{
	int i, j, tmpTotal = 0;
	for(i = 0; i != w * h; ++i)
	{
		tmpTotal = 0;
		for(j = 0; j != 3; ++j)
		{
			tmpTotal += ((biImage[i] >> (j * 8)) & 0xFF);
			tmpTotal -= ((boxImage[i] >> (j * 8)) & 0xFF);
		}
		//这里要写成20%透明度叠加的，3个通道分别算一下
		if(tmpTotal < 0)
		{
			lightColorPercent(&boxImage[i], biImage[i], 0.4);
			biImage[i] = boxImage[i];
		}
		else
			lightColorPercent(&biImage[i], boxImage[i], 0.4);
	}
}

void Bilateral(int *srcPixArray, int w, int h, int delta, int radius, int sigma)
{
	//把原图的红色通道提取出来。。。
//	int *srcModel = (int *)malloc(sizeof(int) * w * h);
	int i ;
//	for(i = 0; i != w * h; ++i)
//	{
//		srcModel[i] = getR(srcPixArray[i]);
//	}
	int *srcBackupPix = (int *)malloc(sizeof(int) * w * h);
	memcpy(srcBackupPix, srcPixArray, sizeof(int) * w * h);
	LOGW("The Bilateral has began");
	float WeightArray[256];
	CreateWeight(WeightArray, (float)delta);
	float *GaussKernel = (float *)malloc(sizeof(float) * radius * radius);
	CreateGauss((float)sigma, GaussKernel, radius);
//	LOGW("The filterGauss has began");
	filterGauss(srcPixArray, w, h, radius, GaussKernel, WeightArray);

//用那个和原图丛叠加，可以先用那个copy算
	for(i = 0; i != w * h; ++i)
	{
		int mtp = getR(srcBackupPix[i]) * getR(srcBackupPix[i]) + (255.0 -  getR(srcBackupPix[i])) * getR(srcPixArray[i]);
		setR(&srcPixArray[i], mtp / 255);

		mtp = getR(srcBackupPix[i]) * getG(srcBackupPix[i]) + (255.0 -  getR(srcBackupPix[i])) * getG(srcPixArray[i]);
		setG(&srcPixArray[i], mtp / 255);

		mtp = getR(srcBackupPix[i]) * getB(srcBackupPix[i]) + (255.0 -  getR(srcBackupPix[i])) * getB(srcPixArray[i]);
		setB(&srcPixArray[i], mtp / 255);
	}

//生成那个覆盖值，调整透明度为20%-25%，叠加模式为浅色
//	LOGW("The Bilateral has ended");
//	int boxSize = 9;
//	float boxValue[9] = {0, -1, 0, -1, 5., -1, 0, -1, 0};
////	LOGW("boxFilter has began");
//	boxFilter(srcBackupPix, w, h, boxValue, (int)sqrt(boxSize));
////	LOGW("boxFilter has ended");
////	LOGW("lightColor has began");
//	lightColor(srcPixArray, srcBackupPix, w, h);
//	LOGW("lightColor has ended");

	free(srcBackupPix);
//	free(srcModel);
}
