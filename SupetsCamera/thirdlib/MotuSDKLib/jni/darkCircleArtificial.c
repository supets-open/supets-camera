#include "darkCircleArtificial.h"
#include <android/log.h>

#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)

void ForwardAssignmentArtificial(int *srcArr, int *tmpV, int *flagArray, int *baseflag, int x, int y, int w, int h)
{
	int i,j;
	for(i = x - h/2; i < x + h/2; ++i)
		for(j = y - w/2; j < y + w/2; ++j)
		{
			tmpV[ (i + h/2 - x) * w + j + w/2 - y] = srcArr[i * width + j];
			if(baseflag[i * width + j] > 0)
			{
				flagArray[(i + h/2 - x) * w + j + w/2 - y] = 1;
//				LOGW("flagArray: %d", flagArray[(i + h/2 - x) * w + j + w/2 - y]);
			}
		}
}

void ForwardAssignmentColor(int *srcArr, int *tmpV, int x, int y, int w, int h)
{
	int i,j;
	for(i = x - h/2; i < x + h/2; ++i)
		for(j = y - w/2; j < y + w/2; ++j)
		{
			tmpV[ (i + h/2 - x) * w + j + w/2 - y] = srcArr[i * width + j];
		}
}

void ReverseAssignmentArtificial(int *srcArr, int *tmpV, int x, int y, int w, int h)
{
	float ratio = 1.0;
	int i,j;
	for(i = x - h/2; i < x + h/2; ++i)
		for(j = y - w/2; j < y + w/2; ++j)
		{
			setR(&srcArr[i * width + j], getR(srcArr[i * width + j]) * (1 - ratio) + getR(tmpV[ (i + h/2 - x) * w + j + w/2 - y]) * ratio);
			setG(&srcArr[i * width + j], getG(srcArr[i * width + j]) * (1 - ratio) + getG(tmpV[ (i + h/2 - x) * w + j + w/2 - y]) * ratio);
			setB(&srcArr[i * width + j], getB(srcArr[i * width + j]) * (1 - ratio) + getB(tmpV[ (i + h/2 - x) * w + j + w/2 - y]) * ratio);
		}
}

int CalculateDistanceArtificial(int first, int second)
{
	int result  = 0;

	result += (getR(first) - getR(second)) * (getR(first) - getR(second));
	result += (getG(first) - getG(second)) * (getG(first) - getG(second));
	result += (getB(first) - getB(second)) * (getB(first) - getB(second));

	return result;
}

int CalculateSingleDisArtificial(int color)
{
	int re = 0;
	re += getR(color) * getR(color);
	re += getG(color) * getG(color);
	re += getB(color) * getB(color);

	return re;
}

int CompareEleArtificial(int a, int b, int c)
{
	if(a <= b && a <= c)
		return 1;
	if(b <= a && b <= c)
		return 2;
	return 3;
}

int CalCulateFlagArtificial(int p, int *centers)
{
	return CompareEleArtificial(CalculateDistanceArtificial(p, centers[0]), CalculateDistanceArtificial(p, centers[1]), CalculateDistanceArtificial(p, centers[2]));
}

void KmeanFunctionArtificial(int *pixels, int w, int h, int *modifyColor)
{
	int *flagArray = (int *)malloc(sizeof(int) * w * h);
	memset(flagArray, 0, sizeof(int) * w * h);
	int kNum = 3;
	double JValue = 0;
	double JBackup = 0;
	int centers[3];
	int i, j;
	int thres = 3;
	//random 3����������
	srand(time(0));
	for(i = 0; i != 3;)
	{
		int tmpNum = pixels[rand() % (w * h) ];
		for(j = 0; j != i; ++j)
		{
			if(centers[j] == tmpNum)
				break;
		}
		if(j == i)
		{
			centers[i] = tmpNum;
			i ++;
		}
	}

	Co CalCenters[3];
	int countNum[3];

	while(1)
	{
		JBackup = JValue;
		JValue = 0;
		for(i = 0; i != w * h; ++i)
		{
			flagArray[i] = CalCulateFlagArtificial(pixels[i], centers);
			JValue += CalculateDistanceArtificial(pixels[i], centers[flagArray[i]]);
		}

		if(abs(JBackup - JValue) < thres)
			break;

		//����centers
		memset(CalCenters, 0, sizeof(int) * 3 * 3);
		memset(countNum, 0, sizeof(int) * 3);

		for(i = 0; i != w * h; ++i)
		{
			CalCenters[flagArray[i] - 1].r += getR(pixels[i]);
			CalCenters[flagArray[i] - 1].g += getG(pixels[i]);
			CalCenters[flagArray[i] - 1].b += getB(pixels[i]);
			countNum[flagArray[i] - 1] ++;
		}
		for(i = 0; i != 3; ++i)
		{
			setR(&centers[i], CalCenters[i].r / countNum[i]);
			setG(&centers[i], CalCenters[i].g / countNum[i]);
			setB(&centers[i], CalCenters[i].b / countNum[i]);
		}
	}

	int MaxDistance = 0;
//	int MinDistance = 3 * 256 * 256;
//	int MaxFlag, MinFlag;

	for(i = 0; i != 3; ++i)
	{
		LOGW("CalCenters[i].r / countNum[i] %d", CalCenters[i].r / countNum[i]);
		LOGW("CalCenters[i].g / countNum[i] %d", CalCenters[i].g / countNum[i]);
		LOGW("CalCenters[i].b / countNum[i] %d", CalCenters[i].b / countNum[i]);
		int tmpDistance = CalculateSingleDis(centers[i]);
		if( tmpDistance > MaxDistance )
		{
//			MaxFlag = i + 1;
			mColor = centers[i];
			MaxDistance = tmpDistance;
		}
//		if( tmpDistance < MinDistance)
//		{
//			MinFlag = i + 1;
//			MinDistance = tmpDistance;
//		}
	}

//	cout << "modifyColor--------------------->>>>" << getR(modifyColor) << " " << getG(modifyColor) << " " << getB(modifyColor) << endl;
}

void quickColorSort(int *a, int left, int right)
{
	int p = (left + right)/2;
	int key = a[p];
	int i,j;
    for(i = left,j = right; i < j;)
    {
        while(!(CalculateSingleDisArtificial(key) < CalculateSingleDisArtificial(a[i]) || p < i))
            i++;
        if(i < p)
        {
            a[p] = a[i];
            p = i;
        }
        while(j>0 && !(j < p || CalculateSingleDisArtificial(a[j]) < CalculateSingleDisArtificial(key)))
            j--;
         if(p < j)
         {
             a[p] = a[j];
             p = j;
         }
     }
     a[p] = key;
     if(p - left > 1)
    	 quickColorSort(a,left,p-1);
     if(right - p > 1)
    	 quickColorSort(a,p + 1, right);
}


void findModifyColor(int *pixels, int w, int h, int *modifyColor)
{
	//Ѱ�ұȽ�������ɫ
//	int i,j;
//	for(i = 0; i != w * h - 1; ++i)
//	{
//		for(j = 0; j != w * h; ++j)
//		{
//			if(CalculateSingleDisArtificial(pixels[i]) > CalculateSingleDisArtificial(pixels[j]))
//			{
//				int tmp = pixels[i];
//				pixels[i] = pixels[j];
//				pixels[j] = tmp;
//			}
//		}
//	}
	quickColorSort(pixels, 0 , w * h - 1);
	int indexColor = 0.9 * w * h;
	mColor = pixels[indexColor];
}

void processTmpArr(int *tmpArr, int *flagArray, int w, int h, int *modifyColor)
{
//	float *tmpflag = (float *)malloc(sizeof(float) * w * h);
//	memset(tmpflag, 0, sizeof(float) * w * h);
	int i,j;
//	for(i = 0; i != h; ++i)
//	{
//		for(j = 0; j != w; ++j)
//		{
//			if(flagArray[i * w + j] > 0)
//				tmpflag[i * w + j] = sqrt((double)CalculateDistanceArtificial(tmpArr[i * w + j], *modifyColor));
//		}
//	}
//
//	int midV = tmpflag[0] / 2;
//	for(i = 1; i != w * h; ++i)
//	{
//		if(tmpflag[i] > midV * 2)
//			midV = tmpflag[i] / 2;
//	}
//	float tmpfloat;
//	for(i = 0; i != w * h; ++i)
//	{
//		tmpfloat = tmpflag[i] - midV;
//		tmpflag[i] = exp( - tmpfloat * tmpfloat/ (2 * 3000) );
//	}
//
	int *tmpBackup = (int *)malloc(sizeof(int) * w * h);
	memcpy(tmpBackup, tmpArr, sizeof(int) * w * h);
//
//	for(i = 0; i != w * h; ++i)
//	{
//		if(flagArray[i] > 0)
//		{
////			LOGW("tmpflag: %d", tmpflag[i]);
//			setR(&tmpArr[i], getR(*modifyColor) * tmpflag[i] + getR(tmpArr[i]) * (1.0 - tmpflag[i]));
//			setG(&tmpArr[i], getG(*modifyColor) * tmpflag[i] + getG(tmpArr[i]) * (1.0 - tmpflag[i]));
//			setB(&tmpArr[i], getB(*modifyColor) * tmpflag[i] + getB(tmpArr[i]) * (1.0 - tmpflag[i]));
//		}
//	}

	float tmpR;
	for(i = 0; i != w * h; ++i)
	{
		if(flagArray[i] > yuhuaRArtificial)
			flagArray[i] = 255;
		else
		{
			tmpR = (float)(flagArray[i] * 1.5) / yuhuaRArtificial;
			flagArray[i] = 255 * tmpR;//
			if(flagArray[i] > 255)
				flagArray[i] = 255;
//			LOGW("flagArray: %d", flagArray[i]);
		}

	}

	for(i = 0; i != w * h; ++i)
	{
		if(flagArray[i] > 0)
		{
			int tmpColor = (getR(mColor) * flagArray[i] + getR(tmpBackup[i]) * (255 - flagArray[i])) / 255;

			if(tmpColor > 255)
			{
				tmpColor = 255;
				LOGW("tmpColor R Out: %d", tmpColor);
			}
			setR(&tmpArr[i], tmpColor);
			tmpColor = (getG(mColor) * flagArray[i] + getG(tmpBackup[i]) * (255 - flagArray[i])) / 255;
			if(tmpColor > 255)
			{
				tmpColor = 255;
				LOGW("tmpColor G Out: %d", tmpColor);
			}
			setG(&tmpArr[i], tmpColor);
			tmpColor = (getB(mColor) * flagArray[i] + getB(tmpBackup[i]) * (255 - flagArray[i])) / 255;

			if(tmpColor > 255)
			{
				tmpColor = 255;
				LOGW("tmpColor B Out: %d", tmpColor);
			}
			setB(&tmpArr[i], tmpColor);
		}
	}

//	free(tmpflag);

//	float ratio = 0.5;
	for(i = 0; i != w * h; ++i)
	{
		if(flagArray[i] > 0)
		{
			int tmpColor = getR(tmpArr[i]) * 0.4 + getR(tmpBackup[i]) * 0.6;
			if(tmpColor > 255)
				tmpColor = 255;
			setR(&tmpArr[i], tmpColor);
			tmpColor = getG(tmpArr[i]) * 0.4 + getG(tmpBackup[i]) * 0.6;
			if(tmpColor > 255)
				tmpColor = 255;
			setG(&tmpArr[i], tmpColor);
			tmpColor = getB(tmpArr[i]) * 0.4 + getB(tmpBackup[i]) * 0.6;
			if(tmpColor > 255)
				tmpColor = 255;
			setB(&tmpArr[i], tmpColor);
		}
	}

	free(tmpBackup);
}

int minxArtificial(int a, int b)
{
	if(a < b)
		return a;
	return b;
}

void areaProduce(int *flagArray, int w, int h)
{
	int i,j;
	for(i = 1; i != h - 1; ++i)
	{
		for(j = 1; j != w - 1; ++j)
		{
			if(flagArray[i * w + j] > 0)
			{

				flagArray[i * w + j] = 1 + MyminArtificial(flagArray[(i - 1) * w + j - 1], flagArray[(i - 1) * w + j], flagArray[(i - 1) * w + j + 1], flagArray[i * w + j - 1]);
//				LOGW("flagArray[i * w + j]: %d", flagArray[i * w + j]);

			}
		}
	}

	for(i = h - 2; i > 0; --i)
	{
		for(j = w - 2; j > 0; --j)
		{
			if(flagArray[i * w + j] > 0)
			{
				int tmpMin = MyminArtificial(flagArray[(i + 1)* w + j + 1], flagArray[(i + 1) * w + j], flagArray[(i + 1) * w + j - 1], flagArray[i * w + j + 1]);
				flagArray[i * w + j] = minxArtificial(flagArray[i * w + j], tmpMin + 1);
//				LOGW("flagArray[i * w + j]: %d", flagArray[i * w + j]);

			}
		}
	}

}

void produceArea(int *srcPixArray, int *baseflag, int w, int h, int *modifyColor)
{
	//���·��Ҹ����ĵ㣬Ȼ�����һ�������ȥ
	//�ݶ���90�ȵĽǶ�

	int i,j;
	width = w;
	height = h;
	//////////////////////////////////////////////////////////

//	int *backupPixels = (int *)malloc(sizeof(int) * w * h);
//	int step = 20;
//	for(i = 0; i != 2; ++i)
//	{
//		memcpy(backupPixels, srcPixArray, sizeof(int) * w * h);
//	}

	////////////////////////////////////////////////////////////

	int flag = 0;
	int centerX;
	int centerY;
	int Up = 99999999, Down = 0, Right = 0, Left = 999999999;
	for(i = 0; i != h; ++i)
	{
		for(j = 0; j != w; ++j)
		{
			if(baseflag[i * w + j] > 0)
			{
				flag = 1;
				if(i < Up)
				{
					Up = i;
				}
				if(i > Down)
				{
					Down = i;
				}
				if(j < Left)
				{
					Left = j;
				}
				if(j > Right)
				{
					Right = j;
				}
			}
		}
	}

	if(flag == 0)
		return ;

	centerX = (Up + Down) / 2;
	centerY = (Left + Right) / 2;
	int realx = centerX; // + r / 1.414
	int realy = centerY;

	int mh = Down - Up;
	int mw = Right - Left;
	LOGW("mw: %d", mw);
	LOGW("mh: %d", mh);

	int *tmpArr = (int *)malloc(sizeof(int) * mh * mw);
	int *flagArray = (int *)malloc(sizeof(int) * mh * mw);
	memset(flagArray, 0, sizeof(int) * mh * mw);

	for(i = 0; i != 3; ++i)
	{
		ForwardAssignmentArtificial(srcPixArray, tmpArr, flagArray, baseflag, realx, realy, mw, mh);
		int newmh = mh + minxArtificial(h - 1 - realx - mh/2, 20);
		int *tmpArrColor = (int *)malloc(sizeof(int) * mw * newmh);
		ForwardAssignmentColor(srcPixArray, tmpArrColor, realx, realy, mw, newmh);
		findModifyColor(tmpArrColor, mw, newmh, modifyColor);
		free(tmpArrColor);
		areaProduce(flagArray, mw, mh);
		processTmpArr(tmpArr, flagArray, mw, mh, modifyColor);
		ReverseAssignmentArtificial(srcPixArray, tmpArr, realx, realy, mw, mh);
	}
	free(tmpArr);
	free(flagArray);
}




