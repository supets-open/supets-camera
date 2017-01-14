#include "mtprocessor.h"
#include "LightenDemo.h"
#include <android/log.h>
#include "operation.h"

#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)

void LightenModify(int* pixels, int w, int h, int* R_Table, int* G_Table, int* B_Table, int times)
{
	int i , j;
	int nValue;
	for(i = 0; i != times; ++i)
	{
		for(j = 0; j != w * h; ++j)
		{
			nValue = R_Table[getR(pixels[j])];
			setR(&pixels[j], nValue);

			nValue = G_Table[getG(pixels[j])];
			setG(&pixels[j], nValue);

			nValue = B_Table[getB(pixels[j])];
			setB(&pixels[j], nValue);
		}

	}
}

int LightenDemo(int *srcPixArray, int w, int h, int sAxis, int lAxis, int centerX, int centerY)
{
	//�����ⷽ��,�������ݾͲ���һ��ģ�Ҫ����Ƿ�������������

	//�������ͼ��ͨ���۾��Ǹ���λ�ɣ�Ѱ�ҹ̶���С����ͼ�������ֱ��ͼ�ķֲ��������ж��Ƿ���Ҫ���������߱䰵

	int i,j;
	float cdf[256];
	memset(cdf, 0, sizeof(float) * 256);

	int *pixels = (int *)malloc(sizeof(int) * w * h);
	memcpy(pixels, srcPixArray, sizeof(int) * w * h);
	convertRgb2Yuv(pixels, w, h);
	LOGW("w %d", w);
	LOGW("h %d", h);
	LOGW("sAxis %d", sAxis);
	LOGW("lAxis %d", lAxis);
	LOGW("centerX %d", centerX);
	LOGW("centerY %d", centerY);
	int numcount = 0;

//	for(i = centerX - sAxis/2; i < centerX + lAxis/2; ++i)
//	{
//		for(j = centerY - sAxis/2; j < centerY - sAxis/2; ++j)
//		{
//			LOGW("pixels %f", getR(pixels[i * w + j]));
//			cdf[getR(pixels[i * w + j])] ++;
//			LOGW("numcount %f", numcount);
//			numcount++;
//		}
//	}

	for(i = 0; i < h; ++i)
	{
		if(i >= centerX - sAxis/2 && i <= centerX + lAxis/2)
		{
			for(j = 0; j < w; ++j)
			{
				if(j >= centerY - sAxis/2 && j <= centerY - sAxis/2)
				{
//					LOGW("pixels %d", getR(pixels[i * w + j]));
					cdf[getR(pixels[i * w + j])] ++;
//					LOGW("numcount %d", numcount);
					numcount++;
				}
			}
		}
	}

	LOGW("numcountfinal %d", numcount);
	for(i = 0; i != 256; ++i)
	{
		cdf[i] /=  numcount;
	}
	for(i = 1; i != 256; ++i)
	{
		cdf[i] += cdf[i - 1];
	}

	LOGW("testCDF3 %f", cdf[50]);
	if(cdf[50] > 0.18)
	{
		LOGW("testCDF3 %f", cdf[50]);
//		LightenModify(srcPixArray, w, h, ModifyTable, ModifyTable, ModifyTable, 1);
		return 1;
	}
	else
		return 0;
}
