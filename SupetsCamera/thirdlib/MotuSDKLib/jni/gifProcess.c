#include "mtprocessor.h"
#include "gifProcess.h"
#include <android/log.h>
#include "operation.h"
#include "math.h"

#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)
#define min(x,y) (x>y?y:x)
#define max(x,y) (x>y?x:y)
#define PI 3.1415926

int ForwardAssign(int *srcArr, int *tmpV,  int x, int y, int radius,int width,int height)
{
	int i, j;
	if(x - radius < 0 || x + radius >= height)
		return 0;
	if(y - radius < 0 || y + radius >= width)
		return 0;

	for(i = x - radius; i < x + radius; ++i)
	{
		for(j = y - radius; j < y + radius; ++j)
		{
			tmpV[ (i + radius - x) * 2 * radius + j + radius - y] = srcArr[i * width + j];
		}
	}
	return 1;
}

void ReverseAssign(int *srcArr, int *tmpV, int x, int y, int radius,int width,int height)
{
	int i, j;
	for(i = x - radius; i < x + radius; ++i)
		for(j = y - radius; j < y + radius; ++j)
			srcArr[i * width + j] =  tmpV[ (i + radius - x) * (2 * radius) + j + radius - y];
}


void thin(int *pixels, int thinSize, int x1, int y1, int x2, int y2, int degree,
		int w, int h) {
	int top = y1 - thinSize;
	int bottom = y1 + thinSize;
	int left = x1 - thinSize;
	int right = x1 + thinSize;

	if (right > w - 1)
		right = w - 1;
	if (left < 0)
		left = 0;
	if (top < 0)
		top = 0;
	if (bottom > h - 1)
		bottom = h - 1;

	int tw = right - left;
	int th = bottom - top;
	if (tw % 2 != 0)
		tw--;
	if (th % 2 != 0)
		th--;
	int* tempPixels = (int*) malloc(tw * th * sizeof(int));
	int m = 0;
	for (m = 0; m < th; m++) {
		memcpy(tempPixels + m * tw, (pixels + (top + m) * w + left),
				tw * sizeof(int));
	}
	float scale = degree / 100.0;
//	LOGW("tw Value : %d", tw);
//	LOGW("th Value : %d", th);

	thinEffect(tempPixels, tw, th, x1 - left, y1 - top, x2 - left, y2 - top,
			tw / 2, scale, 0);

	for (m = 0; m < th; m++) {
		memcpy(pixels + ((top + m) * w + left), tempPixels + tw * m,
				tw * sizeof(int));
	}
}

//所有的函数都是调用thin的方法，具体看里面传入的数据，是前后两个点的
//嘴的还是用估计的方法吧，嘴的识别不准确，用眼睛的第五个点估算位置，眼睛和眉毛的识别是准确的，
//但是点的位置需要重新定位，防止锯齿的效应，眉毛的作用点位置可能因表情不同而不同

void TestImage(int *srcPixArray, int w, int h, int *p)
{
	int i, j, k;
	for (i = 0; i != 18; ++i) {
		LOGW("The Point Value : %d %d", p[i * 2 + 1], p[i * 2]);
		for (j = -2; j != 2; ++j) {
			for (k = -2; k != 2; ++k) {
				setR(&srcPixArray[(j + p[2 * i + 1]) * w + p[2 * i]] + k, 255);
				setG(&srcPixArray[(j + p[2 * i + 1]) * w + p[2 * i]] + k, 0);
				setB(&srcPixArray[(j + p[2 * i + 1]) * w + p[2 * i]] + k, 0);
			}
		}

	}
}

void realEyeEnlarge(int *srcPixArray, int w, int h, int x, int y, int realradius, float scale)
{
    int *tmpArr = (int *)malloc(sizeof(int) * 2 * realradius * 2 * realradius);
    if(ForwardAssign(srcPixArray, tmpArr, x, y, realradius,w,h))
    {
         eyeEnlarge(tmpArr, 2 * realradius, 2 * realradius, realradius, realradius, realradius, scale);
         ReverseAssign(srcPixArray, tmpArr, x, y, realradius,w,h);
    }
    free(tmpArr);
}

void closeEyes(int *srcPixArray, int w, int h, int *p, int condition) {
	int eyeDist = (p[16] - p[26]) * (p[16] - p[26]) + (p[17] - p[27]) * (p[17] - p[27]);
	eyeDist = sqrt(eyeDist);
	int bV, eV;
	switch(condition)
	{
		case 0:
			return ;
		case 1:
			realEyeEnlarge(srcPixArray, w, h, p[17], p[16], eyeDist / 2, 0.6);
			realEyeEnlarge(srcPixArray, w, h, p[27], p[26], eyeDist / 2, 0.6);
			return ;
		case 2:
			bV = 1;
			eV = 3;
			break ;
		case 3:
			realEyeEnlarge(srcPixArray, w, h, p[17], p[16], eyeDist / 2, -0.5);
			realEyeEnlarge(srcPixArray, w, h, p[27], p[26], eyeDist / 2, -0.5);
			return ;
		case 4:
			bV = 1;
			eV = 2;
			break;
		case 5:
			bV = 2;
			eV = 3;
			break ;
		default:
			return ;
	}

	int i, j, k;
	int x[2], y[2], r[2];

//	TestImage(srcPixArray, w, h, p);

	int scale = 90;
	int tmp = 1;

	for(i = bV; i != eV; ++i)
	{
		x[0] = p[2 * (i * 5) - 2] - 1;
		x[1] = p[2 * (i * 5)] + 2;


		y[0] = p[2 * (i * 5) - 1] - 2 * tmp;// 这个可能要换算的，所有一切的数字都需要更换
		y[1] = p[2 * (i * 5) + 1] + 1;

		tmp *= -1;

		r[0] = 4.0 / 11 * abs(p[2 * (i * 5) - 2 + 4] - p[2 * (i * 5) - 2 + 6]);
		r[1] = 5.0 / 11 * abs(p[2 * (i * 5) - 2 + 4] - p[2 * (i * 5) - 2 + 6]);

		for(j = 0; j != 1; ++j)
		{
			thin(srcPixArray, r[j], x[j], y[j], x[j], y[j] + 800, scale, w, h);

			thin(srcPixArray, r[j], x[j] - 10, y[j], x[j], y[j] + 800, scale, w, h);

			thin(srcPixArray, r[j], x[j] + 10, y[j], x[j], y[j] + 800, scale, w, h);

			thin(srcPixArray, r[j], x[j] - 15, y[j], x[j], y[j] + 800, scale, w, h);

			thin(srcPixArray, r[j], x[j] + 15, y[j], x[j], y[j] + 800, scale, w, h);
		}

		for(j = 0; j != 2; ++j)
			thin(srcPixArray, r[1], x[1], y[1] + 5, x[1], y[1] - 300, 90, w, h);
	}
}

void browUpAndDown(int *srcPixArray, int w, int h, int *p, int condition) {

	int bV, eV, flag, tmp;
	switch(condition)
	{
		case 0:
			return ;
		case 1:
			flag = -1;
			tmp = -1;
			bV = 0;
			eV = 2;
			break;
		case 2:
			flag = 1;
			tmp = -1;
			bV = 0;
			eV = 2;
			break ;
		case 4:
			flag = -1;
			bV = 0;
			eV = 1;
			tmp = -1;
			break;
		case 3:
			flag = 1;
			bV = 0;
			eV = 1;
			tmp = -1;
			break;
		case 6:
			flag = -1;
			bV = 1;
			eV = 2;
			tmp = -1;
			break ;
		case 5:
			flag = 1;
			bV = 1;
			eV = 2;
			tmp = -1;
			break;
		case 7:
			flag = 1;
			bV = 0;
			eV = 2;
			tmp = 1;
			break;
		default:
			return ;
	}

	int i, j;
	int browDist;
	int x[2], y[2], r[2];

//	TestImage(srcPixArray, w, h, p);

	int scale = 80;
	for (i = bV; i != eV; ++i) {
		browDist = (p[4 * i] - p[4 * i + 2]) * (p[4 * i] - p[4 * i + 2])
				+ (p[4 * i + 1] - p[4 * i + 2 + 1])
						* (p[4 * i + 1] - p[4 * i + 2 + 1]);

		browDist = sqrt(browDist);

		x[0] = p[4 * i] + 3;
		y[0] = p[4 * i + 1] - flag * 3;
		x[1] = p[4 * i + 2] - 3;
		y[1] = p[4 * i + 3] + flag * 3;

		r[0] = browDist * 5 / 11;
		r[1] = browDist * 5 / 11;

		for (j = 0; j != 1; ++j) {
			thin(srcPixArray, r[0], x[0], y[0], x[0] - 20, y[0] - 800 * flag,
					scale, w, h);

			thin(srcPixArray, r[1], x[1], y[1], x[1] + 20, y[1] + 800 * flag,
					scale, w, h);
		}
		flag *= tmp;
	}
}

void mouthUpAndDown(int *srcPixArray, int w, int h, int *p, int condition) {


//	TestImage(srcPixArray, w, h, p);
	int bV, eV, flag;
	switch(condition)
	{
		case 0:
			return ;
		case 1:
			flag = 1;
			bV = 2;
			break;
		case 2:
			flag = -1;
			bV = 2;
			break ;
		case 3:
			flag = 1;
			bV = 1;
			break;
		case 4:
			flag = 1;
			bV = -1;
			break;
		case 5:
			flag = -1;
			bV = 1;
			break;
		case 6:
			flag = -1;
			bV = -1;
			break;
		default:
			return ;
	}

	int scale = 80;
	int eyeDist = (p[16] - p[26]) * (p[16] - p[26]) + (p[17] - p[27]) * (p[17] - p[27]);
	eyeDist = sqrt(eyeDist);

	int mouthDist = (p[32] - p[34]) * (p[32] - p[34])
			+ (p[33] - p[35]) * (p[33] - p[35]);
	mouthDist = sqrt(mouthDist);

	int r[2];
	r[0] = mouthDist * 1 / 3;
	r[1] = mouthDist / 4;

	int x[4], y[4];
	x[0] = p[32] - 6;
	x[1] = p[34] + 6;
	y[0] = p[33] - 6 * flag; //- 5 * flag
	y[1] = p[35] - 6 * flag; //- 5 * flag
	x[2] = p[28];
	y[2] = p[29] - 4;
	x[3] = p[30];
	y[3] = p[31] + 4;

	//4个点都确定了，然后就是对对应的点进行拉伸，可能中间需要一定程度的坐标转换

	if (flag == 1) {
		if(bV == 1)
			thin(srcPixArray, r[0], x[0], y[0], x[0] - 40, y[0] - 600 * flag,
					scale, w, h);
		if(bV == -1)
			thin(srcPixArray, r[0], x[1], y[1], x[1] + 40, y[1] - 600 * flag,
					scale, w, h);
		if(bV >= 2)
		{
			thin(srcPixArray, r[0], x[0], y[0], x[0] - 40, y[0] - 600 * flag,
								scale, w, h);
			thin(srcPixArray, r[0], x[1], y[1], x[1] + 40, y[1] - 600 * flag,
								scale, w, h);
			thin(srcPixArray, r[1], x[2], y[2], x[2], y[2] + 400 * flag, scale, w,
								h);
		}

	} else {
		if(bV == 1)
			thin(srcPixArray, r[0], x[0], y[0], x[0] - 40, y[0] - 800 * flag,
					scale, w, h);
		if(bV == -1)
			thin(srcPixArray, r[0], x[1], y[1], x[1] + 40, y[1] - 800 * flag,
					scale, w, h);

		if(bV >= 2)
		{
			thin(srcPixArray, r[0], x[0], y[0], x[0] - 40, y[0] - 800 * flag,
					scale, w, h);
			thin(srcPixArray, r[0], x[1], y[1], x[1] + 40, y[1] - 800 * flag,
								scale, w, h);
			thin(srcPixArray, r[1], x[3], y[3], x[3], y[3] + 600 * flag, scale, w,
					h);
		}
	}

}

void gifProcess(int *pixels, int w, int h, int *p, int mouthCondition, int browCondition, int eyeCondition)
{
	closeEyes(pixels, w, h, p, eyeCondition);
	browUpAndDown(pixels, w, h, p, browCondition);
	mouthUpAndDown(pixels, w, h, p, mouthCondition);
}

