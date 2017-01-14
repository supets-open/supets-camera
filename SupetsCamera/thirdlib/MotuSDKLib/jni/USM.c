#include <math.h>
#include <time.h>
#include <android/log.h>
#include <memory.h>
#include "mtprocessor.h"
#include "operation.h"
#include "USM.h"
#include <stdio.h>
#include <stdlib.h>
#include <Progressive.h>

//#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)  //打开log
#define LOGW(...)    //关闭log
#define MIN(a,b) (a>b?b:a)
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//写个逐行的
//不同步的问题需要考虑下,需要新建一个缓冲池。。 但是这个缓冲池所有的逐行都要用。。。

int* usmProcessProgressive(int *pixels, int w, int radius, int thres, int amount)
{
	int *pixelsBackup;
	if(pixels != NULL)
	{
		pixelsBackup = (int *)malloc(sizeof(int) * w);
		memcpy(pixelsBackup, pixels, sizeof(int) * w);
	}
	else
		pixelsBackup = NULL;


	//当前处理的行和得到的行不同步。。 需要考虑如何弄一下
	int *tPoint = NULL;
	tPoint = lineProcess(pixelsBackup);
	int i, Value;
	if(tPoint != NULL)
	{
		for(i = 0; i != w; ++i)
		{
			Value = getR(pixels[i]) - getR(tPoint[i]);
			if (abs(Value) > thres) {
				Value = getR(pixels[i]) + amount * Value / 100.0;

				Value = (unsigned char) ((((unsigned short) Value
						| ((short) (255 - Value) >> 15)) & ~Value >> 15));

				setR(&pixels[i], Value);
			}

			Value = getG(pixels[i]) - getG(tPoint[i]);
			if (abs(Value) > thres) {
				Value = getG(pixels[i]) + amount * Value / 100.0;

				Value = (unsigned char) ((((unsigned short) Value
						| ((short) (255 - Value) >> 15)) & ~Value >> 15));
				setG(&pixels[i], Value);
			}

			Value = getB(pixels[i]) - getB(tPoint[i]);
			if (abs(Value) > thres) {
				Value = getB(pixels[i]) + amount * Value / 100.0;

				Value = (unsigned char) ((((unsigned short) Value
						| ((short) (255 - Value) >> 15)) & ~Value >> 15));
				setB(&pixels[i], Value);
			}
		}
	}

	if(pixels != NULL)
		free(pixelsBackup);

	return tPoint;
}


void usmProcess(int *srcPixArray, int w, int h, int radius, int thres,
		int amount) {
//	LOGW("enter usmProcess %d", 99);
	int *pixelBackup = (int *) malloc(sizeof(int) * w * h);
	memcpy(pixelBackup, srcPixArray, sizeof(int) * w * h);
	fastAverageBlur(pixelBackup, w, h, radius / 3);
	fastAverageBlur(pixelBackup, w, h, radius / 3);
	fastAverageBlur(pixelBackup, w, h, radius / 3);
//	LOGW("after fastAverageBlur %d", 99);
	int i, j, Value;

	for (i = 0; i != h; ++i) {
		for (j = 0; j != w; ++j) {
			Value = getR(srcPixArray[i * w + j]) - getR(pixelBackup[i * w + j]);
			if (abs(Value) > thres) {
				Value = getR(srcPixArray[i * w + j]) + amount * Value / 100.0;
				if (Value < 0)
//					LOGW("ValueR: %d", Value);
				Value = (unsigned char) ((((unsigned short) Value
						| ((short) (255 - Value) >> 15)) & ~Value >> 15));

				Value = Value>255?255:Value;
				Value = Value<0?0:Value;
				setR(&srcPixArray[i * w + j], Value);
			}

			Value = getG(srcPixArray[i * w + j]) - getG(pixelBackup[i * w + j]);
			if (abs(Value) > thres) {
				Value = getG(srcPixArray[i * w + j]) + amount * Value / 100.0;
				if (Value < 0)
//					LOGW("ValueG: %d", Value);
				Value = (unsigned char) ((((unsigned short) Value
						| ((short) (255 - Value) >> 15)) & ~Value >> 15));
				Value = Value>255?255:Value;
				Value = Value<0?0:Value;
				setG(&srcPixArray[i * w + j], Value);
			}

			Value = getB(srcPixArray[i * w + j]) - getB(pixelBackup[i * w + j]);
			if (abs(Value) > thres) {
				Value = getB(srcPixArray[i * w + j]) + amount * Value / 100.0;
				if (Value < 0)
//					LOGW("ValueB: %d", Value);
				Value = (unsigned char) ((((unsigned short) Value
						| ((short) (255 - Value) >> 15)) & ~Value >> 15));
				Value = Value>255?255:Value;
				Value = Value<0?0:Value;
				setB(&srcPixArray[i * w + j], Value);
			}
		}
	}
}

//閲嶅啓usm

static inline int myJu(int x, int y, int centerX, int centerY, int radius) {
//	LOGW("enter myJu %d", 11);
	if ((x - (centerX + radius * 1.0 / 1.414))
			* (x - (centerX + radius * 1.0 / 1.414))
			+ (y - centerY) * (y - centerY) <= radius * radius) {
		if ((x - (centerX - radius * 1.0 / 1.414))
				* (x - (centerX - radius * 1.0 / 1.414))
				+ (y - centerY) * (y - centerY) <= radius * radius) {
//			LOGW("out myJu %d", 11);
			return 1;
		}
	}
//	LOGW("out myJu %d", 11);
	return 0;
}

void fAssignment(int *srcArr, int w, int h, int x, int y, int radius, int *tmpV,
		int *flagArray) {
	int i, j;
	LOGW("enter fAssignment %d", 99);
	LOGW("enter w %d", w);
	LOGW("enter h %d", h);
	LOGW("enter radius %d", radius);
	LOGW("enter x + radius %d", x + radius);
	LOGW("enter y + radius %d", y + radius);
	LOGW("enter x - radius %d", x - radius);
	LOGW("enter y - radius %d", y - radius);

	for (i = x - radius; i < x + radius; ++i) {
		for (j = y - radius; j < y + radius; ++j) {
			tmpV[(i + radius - x) * 2 * radius + j + radius - y] = srcArr[i * w
					+ j];
//			LOGW("enter tmpV %d", tmpV[ (i + radius - x) * 2 * radius + j + radius - y]);
			if (myJu(i, j, x, y, 1.414 * radius)) {
//				LOGW("enter myJu %d", myJu(i, j, x, y, radius));
				flagArray[(i + radius - x) * 2 * radius + j + radius - y] = 1;
			}
		}
	}

	LOGW("after fAssignment %d", 99);
}

void rAssignment(int *srcArr, int w, int h, int x, int y, int radius, int *tmpV,
		int *flagArray, int ratio) {
	int i, j;
	LOGW("enter rAssignment %d", 99);
	float realratio = ratio * 1.0 / 100.0;
	LOGW("enter realratio %f", realratio);
	int value;
	for (i = x - radius; i < x + radius; ++i) {
		for (j = y - radius; j < y + radius; ++j) {
			if (flagArray[(i + radius - x) * 2 * radius + j + radius - y]) {
				LOGW(
						"enter flagArray %d", flagArray[(i + radius - x) * 2 * radius + j + radius - y]);
//				srcArr[ i * w + j] = tmpV[ (i + radius - x) * 2 * radius + j + radius - y];
				//锟斤拷锟斤拷锟角革拷锟斤拷锟接的程度ｏ拷锟斤拷锟金化碉拷锟缴帮拷
				value = getR(
						tmpV[(i + radius - x) * 2 * radius + j + radius - y])
						* flagArray[(i + radius - x) * 2 * radius + j + radius
								- y]
						+ getR(srcArr[i * w + j])
								* (255
										- flagArray[(i + radius - x) * 2
												* radius + j + radius - y]);
//				value = ((value/255) > 255 ? 255: (value / 255));
				value /= 255;
//				LOGW("enter valueR %d", value);
				setR(&srcArr[i * w + j],
						value * realratio
								+ getR(srcArr[i * w + j]) * (1.0 - realratio));

				value = getG(
						tmpV[(i + radius - x) * 2 * radius + j + radius - y])
						* flagArray[(i + radius - x) * 2 * radius + j + radius
								- y]
						+ getG(srcArr[i * w + j])
								* (255
										- flagArray[(i + radius - x) * 2
												* radius + j + radius - y]);
//				value = ((value/255) > 255 ? 255: (value / 255));
				value /= 255;
//				LOGW("enter valueG %d", value);
				setG(&srcArr[i * w + j],
						value * realratio
								+ getR(srcArr[i * w + j]) * (1.0 - realratio));

				value = getB(
						tmpV[(i + radius - x) * 2 * radius + j + radius - y])
						* flagArray[(i + radius - x) * 2 * radius + j + radius
								- y]
						+ getB(srcArr[i * w + j])
								* (255
										- flagArray[(i + radius - x) * 2
												* radius + j + radius - y]);
//				value = ((value/255) > 255 ? 255: (value / 255));
				value /= 255;
//				LOGW("enter valueB %d", value);
				setB(&srcArr[i * w + j],
						value * realratio
								+ getR(srcArr[i * w + j]) * (1.0 - realratio));
			}
		}
	}
}

void lightenEyes(int *tmpV, int w, int h, int *flagArray) {
	//锟斤拷锟诫径锟斤拷锟斤拷鸹锟絝lagArray锟缴帮拷
	LOGW("enter lightenEyes %d", 99);

//	autoContrast(tmpV, w, h, 0, 10);
	usmProcess(tmpV, w, h, 30, 0, 120);
	usmProcess(tmpV, w, h, 50, 0, 50);
//	usmProcess(tmpV, w, h, 50, 0, 50);

//	equlizeHist(tmpV, w, h);

	LOGW("after usmProcess %d", 99);
//	int i,j;
//	for(i = 0; i != h; ++i)
//	{
//		for(j = 0; j != w; ++j)
//		{
//			if(flagArray[i * w + j])
//			{
//				float tmp = 100.0 * 2 / w * sqrt( (i - h/2) * (i - h/2) + (j - w/2) * (j - w/2));
//				flagArray[i * w + j] = tmp;
//				if(flagArray[i * w + j] > 255)
//					flagArray[i * w + j] = 255;
//			}
//		}
//	}

	int i, j;
	for (i = 1; i != h - 1; ++i) {
		for (j = 1; j != w - 1; ++j) {
			if (flagArray[i * w + j] > 0) {
				flagArray[i * w + j] = 1
						+ MyminArtificial(flagArray[(i - 1) * w + j - 1],
								flagArray[(i - 1) * w + j],
								flagArray[(i - 1) * w + j + 1],
								flagArray[i * w + j - 1]);
			}
		}
	}

	for (i = h - 2; i > 0; --i) {
		for (j = w - 2; j > 0; --j) {
			if (flagArray[i * w + j] > 0) {
				int tmpMin = MyminArtificial(flagArray[(i + 1) * w + j + 1],
						flagArray[(i + 1) * w + j],
						flagArray[(i + 1) * w + j - 1],
						flagArray[i * w + j + 1]);
				flagArray[i * w + j] = minxArtificial(flagArray[i * w + j],
						tmpMin + 1);
//				LOGW("flagArray[i * w + j]: %d", flagArray[i * w + j]);

			}
		}
	}

	int yhR = 40;

	for (i = 0; i != w * h; ++i) {
		flagArray[i] = 200 * flagArray[i] * 1.0 / yhR;
		if (flagArray[i] > 255)
			flagArray[i] = 255;
	}

}

void initialProcess(int *srcPixArray, int w, int h, int x, int y, int r,
		int ratio) {
	LOGW("enter initialProcess %d", 99);
	LOGW("enter r %d", r);
	int *tmpV = (int *) malloc(sizeof(int) * 1.414 * r * 1.414 * r);
	int *flagArray = (int *) malloc(sizeof(int) * 1.414 * r * 1.414 * r);
	memset(tmpV, 0, sizeof(int) * 1.414 * r * 1.414 * r);
	memset(flagArray, 0, sizeof(int) * 1.414 * r * 1.414 * r);
	fAssignment(srcPixArray, w, h, x, y, 1.414 * r / 2, tmpV, flagArray);
	lightenEyes(tmpV, r * 1.414, r * 1.414, flagArray);
	rAssignment(srcPixArray, w, h, x, y, 1.414 * r / 2, tmpV, flagArray, ratio);
	free(tmpV);
	free(flagArray);
}

int mmin(int a, int b) {
	if (a <= b)
		return a;
	return b;
}

void BrightEyes(int* srcPixArray, int w, int h, int ratio, int leftX, int leftY,
		int rightX, int rightY) {
	//锟斤拷锟斤拷锟斤拷锟揭伙拷锟斤拷锟斤拷锟斤拷锟斤拷鸹锟饺伙拷锟斤拷锟斤拷锟絬sm锟今化的讹拷锟斤拷锟斤拷usm也锟斤拷锟斤拷锟斤拷锟侥帮拷

	LOGW("enter BrightEyes %d", 99);
	int radius = sqrt(
			(leftX - rightX) * (leftX - rightX)
					+ (leftY - rightY) * (leftY - rightY)) * 0.45;
	int radius1 = Mymin(leftX, -leftX - 1 + h, leftY, -leftY + w - 1);
	radius1 = 1.414 * mmin(radius1, radius);
	int radius2 = Mymin(rightX, -rightX - 1 + h, rightY, -rightY + w - 1);
	radius2 = 1.414 * mmin(radius2, radius);
	//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷圆锟斤拷 锟斤拷锟金化ｏ拷锟斤拷USM锟斤拷锟姐法锟斤拷锟斤拷锟斤拷锟斤拷锟�
	//锟叫讹拷锟斤拷锟斤拷锟角否超癸拷图锟侥边界？
	LOGW("enter radius1 %d", radius1);
	LOGW("enter radius2 %d", radius2);
	LOGW("enter ratio %d", ratio);

	LOGW("enter leftx %d", leftX);
	LOGW("enter lefty %d", leftY);
	LOGW("enter rightx %d", rightX);
	LOGW("enter righty %d", rightY);
//	usmProcess(srcPixArray, w, h, 50, 0, 50);
	initialProcess(srcPixArray, w, h, leftX, leftY, radius1, ratio);
	initialProcess(srcPixArray, w, h, rightX, rightY, radius2, ratio);
}

/////////////////////////////////////////////////////////////////////////////////////////
void whitening(int *srcPixArray, int w, int h, float ratio) {

	int *backup = (int *) malloc(sizeof(int) * w * h);
	memcpy(backup, srcPixArray, sizeof(int) * w * h);
	float realratio = ratio * 1.0 / 100;
	convertRgb2Yuv(backup, w, h);
	int i, value;
	for (i = 0; i != w * h; ++i) {
		value = (255 * getR(backup[i])
				+ getR(srcPixArray[i]) * (255 - getR(backup[i]))) / 255;
		setR(&srcPixArray[i],
				value * realratio + getR(srcPixArray[i]) * (1.0 - realratio));
		value = (255 * getR(backup[i])
				+ getG(srcPixArray[i]) * (255 - getR(backup[i]))) / 255;
		setG(&srcPixArray[i],
				value * realratio + getG(srcPixArray[i]) * (1.0 - realratio));
		value = (255 * getR(backup[i])
				+ getB(srcPixArray[i]) * (255 - getR(backup[i]))) / 255;
		setB(&srcPixArray[i],
				value * realratio + getB(srcPixArray[i]) * (1.0 - realratio));
	}

	convertYuv2Rgb(backup, w, h);
	free(backup);
}

int JudgeWhiten(int *srcPixArray, int w, int h, int ratio, int sAxis, int lAxis,
		int centerX, int centerY) {
	LOGW("w: %d", w);
	LOGW("h: %d", h);

	LOGW("sAxis: %d", sAxis);
	LOGW("centerX: %d", centerX);
	LOGW("centerY: %d", centerY);
	int i, j;
	//	float *cdf = (float *)malloc(sizeof(float) * 256);
	float cdf[256];
	memset(cdf, 0, sizeof(float) * 256);
	int hh, ss, ii;
	int numcount = 0;

	for (i = 0; i < h; ++i) {
		//		if(i >= centerX - sAxis/2 && i <= centerX + lAxis/2)
		{
			for (j = 0; j < w; ++j) {
				//				if(j >= centerY - sAxis/2 && j <= centerY - sAxis/2)
				if ((i - centerX) * (i - centerX)
						+ (j - centerY) * (j - centerY)
						<= sAxis / 2 * sAxis / 2) {
					transHSV(getR(srcPixArray[i * w + j]),
							getG(srcPixArray[i * w + j]),
							getB(srcPixArray[i * w + j]), &hh, &ss, &ii);
					cdf[ss]++;
					numcount++;
				}
			}
		}
	}

	LOGW("numcount: %d", numcount);
	for (i = 0; i != 256; ++i) {
//		LOGW("i %d", i);
//		LOGW("before whitening cdf: %f", cdf[i]);
		cdf[i] /= numcount;
		if (i == 0)
			continue;
		cdf[i] += cdf[i - 1];
//		LOGW("after whitening cdf: %f", cdf[i]);
	}
	LOGW("whitening final cdf: %f", cdf[255] - cdf[100]);
	if (cdf[255] - cdf[100] > 0.65) {
		whitening(srcPixArray, w, h, (cdf[255] - cdf[100]) * 25); //
		return 1;
	}

	return 0;
}

void darkProcess(int *srcPixArray, int w, int h, int ratio) {
	int i;

	float realratio = ratio * 1.0 / 100;
	int value;
	for (i = 0; i != w * h; ++i) {
		value = 255
				- (255.0 - getR(srcPixArray[i]))
						* (255.0 - getR(srcPixArray[i])) / 255.0;
		setR(&srcPixArray[i],
				getR(srcPixArray[i]) * (1.0 - realratio) + value * realratio);

		value = 255
				- (255.0 - getG(srcPixArray[i]))
						* (255.0 - getG(srcPixArray[i])) / 255.0;
		setG(&srcPixArray[i],
				getG(srcPixArray[i]) * (1.0 - realratio) + value * realratio);

		value = 255
				- (255.0 - getB(srcPixArray[i]))
						* (255.0 - getB(srcPixArray[i])) / 255.0;
		setB(&srcPixArray[i],
				getB(srcPixArray[i]) * (1.0 - realratio) + value * realratio);
	}
}

float MPJudgeDark(int *srcPixArray, int w, int h, int ratio, int sAxis,
		int lAxis, int centerX, int centerY) {
	if (sAxis == 0 && lAxis == 0)
		return 1.0;
	int i, j;
	float cdf[256];
	memset(cdf, 0, sizeof(float) * 256);

	convertRgb2Yuv(srcPixArray, w, h);
	int numcount = 0;
	for (i = 0; i < h; ++i) {
		//		if(i >= centerX - sAxis/2 && i <= centerX + lAxis/2)
		{
			for (j = 0; j < w; ++j) {
				//				if(j >= centerY - sAxis/2 && j <= centerY - sAxis/2)
				if ((i - centerX) * (i - centerX)
						+ (j - centerY) * (j - centerY)
						<= sAxis / 2 * sAxis / 2) {
					cdf[getR(srcPixArray[i * w + j])]++;
					numcount++;
				}
			}
		}
	}

	convertYuv2Rgb(srcPixArray, w, h);

	for (i = 0; i != 256; ++i) {
		cdf[i] /= numcount;
	}
	for (i = 1; i != 256; ++i) {
		cdf[i] += cdf[i - 1];
	}
	LOGW("darkness cdf: %f", cdf[80]);
	if (cdf[80] > 0.36) {
		darkProcess(srcPixArray, w, h, cdf[50] * 50);
		return cdf[80];
	} else
		return 0;

//	JudgeWhiten(srcPixArray, w, h, ratio, sAxis, lAxis, centerX, centerY);
}

void brightProcess(int *srcPixArray, int w, int h, int ratio) {
	int i;
	float realratio = ratio * 1.0 / 100;
	int value;
	for (i = 0; i != w * h; ++i) {
		value = getR(srcPixArray[i]) * getR(srcPixArray[i]) / 255.0;
		setR(&srcPixArray[i],
				getR(srcPixArray[i]) * (1.0 - realratio) + value * realratio);

		value = getG(srcPixArray[i]) * getG(srcPixArray[i]) / 255.0;
		setG(&srcPixArray[i],
				getG(srcPixArray[i]) * (1.0 - realratio) + value * realratio);

		value = getB(srcPixArray[i]) * getB(srcPixArray[i]) / 255.0;
		setB(&srcPixArray[i],
				getB(srcPixArray[i]) * (1.0 - realratio) + value * realratio);
	}

}

void JudgeBright(int *srcPixArray, int w, int h, int ratio, int sAxis,
		int lAxis, int centerX, int centerY) {
	int i, j;
	float cdf[256];
	memset(cdf, 0, sizeof(float) * 256);

	//	int *pixels = (int *)malloc(sizeof(int) * w * h);
	//	memcpy(pixels, srcPixArray, sizeof(int) * w * h);
	convertRgb2Yuv(srcPixArray, w, h);
	int numcount = 0;
	for (i = 0; i < h; ++i) {
		//		if(i >= centerX - sAxis/2 && i <= centerX + lAxis/2)
		{
			for (j = 0; j < w; ++j) {
				//				if(j >= centerY - sAxis/2 && j <= centerY - sAxis/2)
				if ((i - centerX) * (i - centerX)
						+ (j - centerY) * (j - centerY)
						<= sAxis / 2 * sAxis / 2) {
					cdf[getR(srcPixArray[i * w + j])]++;
					numcount++;
				}
			}
		}
	}

	convertYuv2Rgb(srcPixArray, w, h);

	for (i = 0; i != 256; ++i) {
		cdf[i] /= numcount;
	}
	for (i = 1; i != 256; ++i) {
		cdf[i] += cdf[i - 1];
	}

	if (cdf[255] - cdf[200] > 0.25) {
		brightProcess(srcPixArray, w, h, (cdf[255] - cdf[200]) * 70);
	} else
		return;

//	JudgeWhiten(srcPixArray, w, h, ratio, sAxis, lAxis, centerX, centerY);
}
void whiten(int* srcPixArray, int w, int h, int ratio, int leftX, int leftY,
		int rightX, int rightY) {

//	whitening(srcPixArray, w, h, 100);

	LOGW("leftX: %d", leftX);
	LOGW("leftY: %d", leftY);
	LOGW("rightX: %d", rightX);
	LOGW("rightY: %d", rightY);
//
	int centerY = (rightY + leftY) / 2;
	int centerX = (rightX + leftX) / 2;
//    int sAxis = rightY - leftY;

	int sAxis = sqrt(
			(rightX - leftX) * (rightX - leftX)
					+ (rightY - leftY) * (rightY - leftY));
	int lAxis = sAxis * 2;

	if (leftX == 0 && leftY == 0 && rightX == 0 && rightY == 0) {
		return;
	}

	if (JudgeWhiten(srcPixArray, w, h, 40, sAxis, lAxis, centerX, centerY)) {
		MPJudgeDark(srcPixArray, w, h, 35, sAxis, lAxis, centerX, centerY);
	} else {
		MPJudgeDark(srcPixArray, w, h, 65, sAxis, lAxis, centerX, centerY);
	}
}

// 閫嗗厜鐨勭浉鍏崇畻娉曠爺绌�
void backLight(int* srcPixArray, int w, int h, int ratio, int leftX, int leftY,
		int rightX, int rightY) {
//	int *green = (int *)malloc(sizeof(int) * w * h);
//	memset(green, 0, sizeof(int) * w * h);
	int i, j, value, tmp;
	int centerY = (rightY + leftY) / 2;
	int centerX = (rightX + leftX) / 2;
	int sAxis = sqrt(
			(rightX - leftX) * (rightX - leftX)
					+ (rightY - leftY) * (rightY - leftY));
	int lAxis = sAxis * 2;

//	int *tmpBackup = (int *)malloc(sizeof(int) * w * h);
//	memcpy(tmpBackup, srcPixArray, sizeof(int) * w * h);
	float jResult = MPJudgeDark(srcPixArray, w, h, ratio, sAxis, lAxis, centerX,
			centerY);
	if (jResult > 0) {
		float realration = MIN(0.2 + jResult, 1.0);
		for (i = 0; i != w * h; ++i) {
			tmp = 255 - getG(srcPixArray[i]);
			//		setR(&tmpBackup[i], getR(tmpBackup[i]) * green[i] * 1.0 / 255);
			//		setG(&tmpBackup[i], getG(tmpBackup[i]) * green[i] * 1.0 / 255);
			//		setB(&tmpBackup[i], getB(tmpBackup[i]) * green[i] * 1.0 / 255);
			value = getR(srcPixArray[i]) * tmp * 1.0 / 255;
			value = 255 - (255 - value) * (255 - getR(srcPixArray[i])) / 255;
			setR(&srcPixArray[i],
					value * realration
							+ getR(srcPixArray[i]) * (1 - realration));
			value = getG(srcPixArray[i]) * tmp * 1.0 / 255;
			value = 255 - (255 - value) * (255 - getG(srcPixArray[i])) / 255;
			setG(&srcPixArray[i],
					value * realration
							+ getG(srcPixArray[i]) * (1 - realration));
			value = getB(srcPixArray[i]) * tmp * 1.0 / 255;
			value = 255 - (255 - value) * (255 - getB(srcPixArray[i])) / 255;
			setB(&srcPixArray[i],
					value * realration
							+ getB(srcPixArray[i]) * (1 - realration));
		}
	}
	//鐢熸垚閫忔槑鍖哄煙
//	free(green);

	return;
}

//#define gfloat float
//#define gint int
//
//typedef struct{
//	float b[4];
//	float B;
//	float sigma;
//	int N;
//}gauss3_coefs;
//
//static void compute_coefs3 (gauss3_coefs *c, gfloat sigma)
//{
//  gfloat q, q2, q3;
//
//  q = 0;
//
//  if (sigma >= 2.5)
//    {
//      q = 0.98711 * sigma - 0.96330;
//    }
//  else if ((sigma >= 0.5) && (sigma < 2.5))
//    {
//      q = 3.97156 - 4.14554 * (gfloat) sqrt ((double) 1 - 0.26891 * sigma);
//    }
//  else
//    {
//      q = 0.1147705018520355224609375;
//    }
//
//  q2 = q * q;
//  q3 = q * q2;
//  c->b[0] = (1.57825+(2.44413*q)+(1.4281 *q2)+(0.422205*q3));
//  c->b[1] = (        (2.44413*q)+(2.85619*q2)+(1.26661 *q3));
//  c->b[2] = (                   -((1.4281*q2)+(1.26661 *q3)));
//  c->b[3] = (                                 (0.422205*q3));
//  c->B = 1.0-((c->b[1]+c->b[2]+c->b[3])/c->b[0]);
//  c->sigma = sigma;
//  c->N = 3;
//
//}
//
//static void gausssmooth (gfloat *in, gfloat *out, gint size, gint rowstride, gauss3_coefs *c)
//{
//
//  gint i,n, bufsize;
//  gfloat *w1,*w2;
//
//  /* forward pass */
//  bufsize = size+3;
//  size -= 1;
//  w1 = (gfloat *) malloc (bufsize * sizeof (gfloat));
//  w2 = (gfloat *) malloc (bufsize * sizeof (gfloat));
//  w1[0] = in[0];
//  w1[1] = in[0];
//  w1[2] = in[0];
//  for ( i = 0 , n=3; i <= size ; i++, n++)
//    {
//      w1[n] = (gfloat)(c->B*in[i*rowstride] +
//                       ((c->b[1]*w1[n-1] +
//                         c->b[2]*w1[n-2] +
//                         c->b[3]*w1[n-3] ) / c->b[0]));
//    }
//
//  /* backward pass */
//  w2[size+1]= w1[size+3];
//  w2[size+2]= w1[size+3];
//  w2[size+3]= w1[size+3];
//  for (i = size, n = i; i >= 0; i--, n--)
//    {
//      w2[n]= out[i * rowstride] = (gfloat)(c->B*w1[n] +
//                                           ((c->b[1]*w2[n+1] +
//                                             c->b[2]*w2[n+2] +
//                                             c->b[3]*w2[n+3] ) / c->b[0]));
//    }
//
//  g_free (w1);
//  g_free (w2);
//}

int ContrastTable[256] = { 0, 2, 3, 5, 6, 8, 9, 11, 13, 14, 16, 17, 19, 20, 22,
		24, 25, 27, 28, 30, 31, 33, 35, 36, 38, 39, 41, 42, 44, 45, 47, 49, 50,
		52, 53, 55, 56, 58, 59, 61, 62, 64, 65, 67, 68, 70, 71, 73, 74, 76, 77,
		79, 80, 82, 83, 84, 86, 87, 89, 90, 92, 93, 95, 96, 97, 99, 100, 102,
		103, 104, 106, 107, 108, 110, 111, 112, 114, 115, 116, 118, 119, 120,
		122, 123, 124, 126, 127, 128, 129, 131, 132, 133, 134, 136, 137, 138,
		139, 140, 142, 143, 144, 145, 146, 147, 148, 150, 151, 152, 153, 154,
		155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168,
		169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 180, 181,
		182, 183, 184, 185, 186, 186, 187, 188, 189, 190, 191, 191, 192, 193,
		194, 195, 195, 196, 197, 198, 198, 199, 200, 201, 201, 202, 203, 204,
		204, 205, 206, 206, 207, 208, 209, 209, 210, 211, 211, 212, 213, 213,
		214, 215, 215, 216, 216, 217, 218, 218, 219, 220, 220, 221, 221, 222,
		223, 223, 224, 224, 225, 226, 226, 227, 227, 228, 228, 229, 230, 230,
		231, 231, 232, 232, 233, 233, 234, 235, 235, 236, 236, 237, 237, 238,
		238, 239, 239, 240, 240, 241, 241, 242, 242, 243, 243, 244, 244, 245,
		246, 246, 247, 247, 248, 248, 249, 249, 250, 250, 251, 251, 252, 252,
		253, 253, 254, 254, 255, 255};

void mytransRgb2Yuv(int *pixels, int w, int h) {
	int i, size;
	int r, g, b, a;
	int y, u, v;
	size = w * h;
	for (i = 0; i < size; i++) {
		getRGBA(pixels[i], &r, &g, &b, &a);
		y = 0.299 * r + 0.587 * g + 0.114 * b;
//		u = 0.436 * (b - y) / (1 - 0.114) + 128;
//		v = 0.615 * (r - y) / (1 - 0.299) + 128;
		u = (r - y) * 0.713 + 128;
		v = (b - y) * 0.564 + 128;
		pixels[i] = (a << 24) + (gan(y) << 16) + (gan(u) << 8) + gan(v);
	}
}

void mytransYuv2Rgb(int *pixels, int w, int h) {
	int i, size;
	int r, g, b, a;
	int y, u, v;
	size = w * h;
	for (i = 0; i < size; i++) {
		getRGBA(pixels[i], &y, &u, &v, &a);
//		r = y + 1.13983 * (v - 128);
//		g = y - 0.39465 * (u - 128) - 0.58060 * (v - 128);
//		b = y + 2.03211 * (u - 128);
		r = (u - 128) / 0.713 + y;
		b = (v - 128) / 0.564 + y;
		g = (y - 0.299 * r - 0.114 * b) / 0.587;

		pixels[i] = (a << 24) + (gan(r) << 16) + (gan(g) << 8) + gan(b);
	}
}

int NewJudgeDark(int *pixels, int w, int h) {
	int totalnum = 0;
	float cdf[256];
	int i, j;
	mytransRgb2Yuv(pixels, w, h);

	for (i = 0; i != h; ++i) {
		for (j = 0; j != w; ++j) {
			if (ContrastTable[getG(pixels[i * w + j])] > 130) {
				cdf[getR(pixels[i * w + j])]++;
				totalnum++;
			}
		}
	}

	mytransYuv2Rgb(pixels, w, h);
	for (i = 0; i != 256; ++i) {
		cdf[i] /= totalnum;
		if (i > 0)
			cdf[i] += cdf[i - 1];
	}

	if (cdf[70] > 0.32)
		return 1;


	return 0;

}

