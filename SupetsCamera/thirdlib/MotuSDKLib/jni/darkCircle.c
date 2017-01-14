#include "darkCircle.h"
#include <android/log.h>
#include <math.h>


#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)

//void ForwardAssignment(int *srcArr, int *tmpV, int x, int y, int radius)
//{
//	int i, j;
//	int ex;
//	for(i = x - radius; i < x + radius; ++i)
//		for(j = y - radius; j < y + radius; ++j)
//		{
//			tmpV[ (i + radius - x) * 2 * radius + j + radius - y] = srcArr[ i * width + j];
//		}
//}
//
//void ReverseAssignment(int *srcArr, int *tmpV, int *flagArray, int x, int y, int radius, int bound)
//{
//	int i, j;
//	int ex;
////	LOGW("bound value %d", bound);
//	if(bound == -1)
//	{
//		for(i = x - radius; i < x + radius; ++i)
//			for(j = y - radius; j < y + radius; ++j)
//			{
//				srcArr[i * width + j] =  tmpV[ (i + radius - x) * (2 * radius) + j + radius - y];
//			}
//	}
//	else
//	{
//		for(j = y - radius; j < y + radius; ++j)
//		{
//			for(i = x - radius; i < x + radius; ++i)
//			{
//				//i + radius - x > stepDepth[j] + i + radius - x &&
//
//				if( i + radius - x > bound + stepDepth[j + radius - y])
//				{
//					srcArr[i * width + j] =  tmpV[ (i + radius - x) * (2 * radius) + j + radius - y];
//				}
//			}
//		}
//	}
//
//}

void ReverseWholeImage(int *arr,int w, int h)
{
	int tmp;
	int i,j;
	for(i = 0; i <= h - i - 1; ++i)
	{
		for(j = 0; j < w; ++j)
		{
			tmp = arr[i * w + j];
			arr[i * w + j] = arr[ (h - i - 1) * w + w - j - 1];
			arr[ (h - i - 1) * w + w - j - 1] = tmp;
		}
	}
}

void ForwardAssignment(int *srcArr, int *tmpV, int x, int y, int radius)
{
	int i, j;
//	angle = (float)deltaX / deltaY;
	int ii,jj;
	for(i = x - radius; i < x + radius; ++i)
	{
		for(j = y - radius; j < y + radius; ++j)
		{
			tmpV[ (i + radius - x) * 2 * radius + j + radius - y] = srcArr[ i * width + j];
//			ii = (i * transMatrix[0] + j * transMatrix[1]);
//			jj = (transMatrix[2] * i + transMatrix[3] * j);
//			tmpV[ (i + radius - x) * 2 * radius + j + radius - y] = srcArr[ ii * width + jj];
		}
	}
}

void ReverseAssignment(int *srcArr, int *tmpV, int *flagArray, int x, int y, int radius)
{
	int i, j;
//	angle = (float)deltaX / deltaY;
	int ii,jj;
	for(j = y - radius; j < y + radius; ++j)
	{
		for(i = x - radius; i < x + radius; ++i)
		{
//			ii = (i * transMatrix[0] + j * transMatrix[1]);
//			jj = (transMatrix[2] * i + transMatrix[3] * j);
			if( flagArray[(i + radius - x) * (2 * radius) + j + radius - y] > 0)
			{
				srcArr[i * width + j] =  tmpV[ (i + radius - x) * (2 * radius) + j + radius - y];
			}
		}
	}
}

int CalculateDistance(int first, int second)
{
	int result  = 0;

	result += (getR(first) - getR(second)) * (getR(first) - getR(second));
	result += (getG(first) - getG(second)) * (getG(first) - getG(second));
	result += (getB(first) - getB(second)) * (getB(first) - getB(second));

	return result;
}

int CalculateSingleDis(int color)
{
	int re = 0;
	re += getR(color) * getR(color);
	re += getG(color) * getG(color);
	re += getB(color) * getB(color);

	return re;
}

int CompareEle(int a, int b, int c, int d)
{
	if(a <= b && a <= c && a <= d)
		return 1;

	if(b <= a && b <= c && b <= d)
		return 2;

	if(c <= a && c <= b && c <= d)
		return 3;

	return 4;
}

int CalCulateFlag(int p, int *centers)
{
	return CompareEle(CalculateDistance(p, centers[0]), CalculateDistance(p, centers[1]), CalculateDistance(p, centers[2]), CalculateDistance(p, centers[3]));
}

int GetSecond(int a, int b, int c, int d)
{
	if(a >= b && a >= c && d >= a)
		return 1;
	if(b >= a && b >= c && b <= d)
		return 2;
	if(c >= a && c >= b && c <= d)
		return 3;

	return 4;
}

///////////////////////////////////////////////////////////////////////////////

int JudgeBS(int a, int b)
{
	if(CalculateSingleDis(a) < CalculateSingleDis(b))
		return 1;
	return 0;
}

float GaussTemplate(float *gt, int l)
{
	int i;
	memset(gt, 0, sizeof(float) * l);
	float tmp1,tmp2;
	float total = 0;
	for(i = 0; i != l; ++i)
	{
		tmp1 = - (i - (float)(l * 1.0)/6) * (i - (float)(l * 1.0)/6);
		gt[i] = exp(tmp1);
		total += gt[i];
	}

	return total;
}

void quickSort(int *a, int left, int right)
{
	int p = (left + right)/2;
	int key = a[p];
	int i,j;
    for(i = left,j = right; i < j;)
    {
        while(!(key < a[i] || p < i))
            i++;
        if(i < p)
        {
            a[p] = a[i];
            p = i;
        }
        while(j>0 && !(j < p || a[j] < key))
            j--;
         if(p < j)
         {
             a[p] = a[j];
             p = j;
         }
     }
     a[p] = key;
     if(p - left > 1)
         quickSort(a,left,p-1);
     if(right - p > 1)
         quickSort(a,p + 1, right);
}


void produceModifyColor(int *mColor, int l, int *modifyColor)
{
	int i, j;
	int tmpC;
	int ssr = 0, ssb = 0, ssg = 0;

//	for(i = 0; i < l; ++i)
//	{
//		for(j = i + 1; j < l; ++j)
//		{
//			if(JudgeBS(mColor[i], mColor[j]))
//			{
//				tmpC = mColor[j];
//				mColor[j] = mColor[i];
//				mColor[i] = tmpC;
//			}
//		}
//	}

	quickSort(mColor, 0, l - 1);

	float *gt = (float *)malloc(sizeof(float) * l);
	float total = 0;
//	total = GaussTemplate(gt,l);
	memset(gt, 0, sizeof(float) * l);
	float tmp1;
	for(i = 0; i != l; ++i)
	{
		tmp1 = - (i - 2 * (float)(l * 1.0)/3) * (i - 2 * (float)(l * 1.0)/3);
		gt[i] = exp(tmp1 / 3000);
		total += gt[i];
	}

	for(i = 0; i != l; ++i)
	{
		ssr += gt[i] * getR(mColor[i]);
		ssg += gt[i] * getG(mColor[i]);
		ssb += gt[i] * getB(mColor[i]);
	}

	setR(modifyColor, ssr/total);
	setG(modifyColor, ssg/total);
	setB(modifyColor, ssb/total);


	free(gt);
//	LOGW("R: %d", getR(*modifyColor));
//	LOGW("G: %d", getG(*modifyColor));
//	LOGW("B: %d", getB(*modifyColor));

}
//////////////////////////////////////////////////////////////////////////

void KmeanFunction(int *pixels, int *flagArray, int w, int h, int *modifyColor)
{
	int kNum = 4;
	double JValue = 0;
	double JBackup = 0;
	int centers[4];
	int i, j, k;
	int thres = 10;
	memset(flagArray, 0, sizeof(int) * w * h);
//	LOGW("Kmeans Initialize", 0);

	//random 4����������
// 	LOGW("Kmeans Initialize end", 0);

	srand(time(0));
	for(i = 0; i != 4;)
	{
		int tmpNum = pixels[rand() % (w * h)];
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
	Co CalCenters[4];
	int countNum[4];
	while(1)
	{
		JBackup = JValue;
		JValue = 0;
		for(i = 0; i != w * h; ++i)
		{
			flagArray[i] = CalCulateFlag(pixels[i], centers);
			JValue += CalculateDistance(pixels[i], centers[flagArray[i]]);
		}

		if(abs(JBackup - JValue) < thres)
			break;

		//����centers
		memset(CalCenters, 0, sizeof(int) * 3 * 4);
		memset(countNum, 0, sizeof(int) * 4);

		for(i = 0; i != w * h; ++i)
		{
			CalCenters[flagArray[i] - 1].r += getR(pixels[i]);
			CalCenters[flagArray[i] - 1].g += getG(pixels[i]);
			CalCenters[flagArray[i] - 1].b += getB(pixels[i]);
			countNum[flagArray[i] - 1] ++;
		}
		for(i = 0; i != 4; ++i)
		{
			setR(&centers[i], CalCenters[i].r / countNum[i]);
			setG(&centers[i], CalCenters[i].g / countNum[i]);
			setB(&centers[i], CalCenters[i].b / countNum[i]);
		}
	}
//	LOGW("Kmeans End: %f", 0);

       	int MaxDistance = 0;
       	int MinDistance = 3 * 256 * 256;
       	int MaxFlag, MinFlag;

    //   	int tmpMaxFlag = GetSecond(CalculateSingleDis(centers[0]), CalculateSingleDis(centers[1]), CalculateSingleDis(centers[2]), CalculateSingleDis(centers[3]));

   		for(i = 0; i != 4; ++i)
   		{
   			int tmpDistance = CalculateSingleDis(centers[i]);
   			if( tmpDistance > MaxDistance )
   			{
   				MaxFlag = i + 1;
   				*modifyColor = centers[i];
   				MaxDistance = tmpDistance;
   			}
   			if( tmpDistance < MinDistance)
   			{
   				MinFlag = i + 1;
   				MinDistance = tmpDistance;
   			}
   		}

   	LOGW("modifyColorR: %d", getR(*modifyColor));
   	LOGW("modifyColorG: %d", getG(*modifyColor));
   	LOGW("modifyColorB: %d", getB(*modifyColor));

   /////////////////////////////////////////////////////////////
   //���������ݿ�����һ�������Ȼ����и�˹����
   int flagCount = 0;
   for(i = 0; i != w * h; ++i)
   {
	   if(flagArray[i] == MaxFlag)
		   flagCount ++;
   }

   int *dataColor = (int *)malloc(sizeof(int) * flagCount);
   flagCount = 0;
   for(i = 0; i != w * h; ++i)
   {
	   if(flagArray[i] == MaxFlag)
		   dataColor[flagCount ++] = pixels[i];
   }

   produceModifyColor(dataColor, flagCount, modifyColor);


  	LOGW("modifyColorR: %d", getR(*modifyColor));
  	LOGW("modifyColorG: %d", getG(*modifyColor));
  	LOGW("modifyColorB: %d", getB(*modifyColor));

   ////////////////////////////////////////////////////////////////


	memset(flagArray, 0, sizeof(int) * w * h);
	for(i = 0; i != h; ++i)
	{
		for(j = 0; j != w; ++j)
		{
			if( (i - h / 2) * (i - h / 2) + (j - w / 2) * (j - w / 2) < w / 2 * w / 2)
			{
				flagArray[i * w + j] = 1;
			}
		}
	}


}

void ModifyImage(int *pixels, int *flagArray, int w, int h, int color)
{
	int i,j;
	for(i = 0;  i != h; ++i)
	{
		for(j = 0; j != w; ++j)
		{
			if( flagArray[i * w + j] > 0)
			{
				setR(&pixels[i * w + j], (getR(color) * flagArray[i * w + j] + getR(pixels[i * w + j]) * (255 - flagArray[i * w + j])) / 255);
				setG(&pixels[i * w + j], (getG(color) * flagArray[i * w + j] + getG(pixels[i * w + j]) * (255 - flagArray[i * w + j])) / 255);
				setB(&pixels[i * w + j], (getB(color) * flagArray[i * w + j] + getB(pixels[i * w + j]) * (255 - flagArray[i * w + j])) / 255);
			}
		}
	}
}

void myModifyImage(int *ori, int *now, int *flagArray, int w, int h)
{
	int i,j;
	for(i = 0;  i != w * h; ++i)
	{
		if(flagArray[i] > 0)
		{
			setR(&now[i], (getR(now[i]) * flagArray[i] + getR(ori[i]) * (255 - flagArray[i])) / 255);
			setG(&now[i], (getG(now[i]) * flagArray[i] + getG(ori[i]) * (255 - flagArray[i])) / 255);
			setB(&now[i], (getB(now[i]) * flagArray[i] + getB(ori[i]) * (255 - flagArray[i])) / 255);
		}
	}
}

int minx(int a, int b)
{
	if(a <= b)
		return a;
	return b;
}

int Mymin(int a, int b, int c, int d)
{
	int re;
	re = minx(a,b);
	re = minx(re,c);
	re = minx(re,d);
	return re;
}

void modelProduce(int *flagArray, int w, int h)
{
	int i,j;
//	memset(flagArray, 0, sizeof(int) * w * h);

// 	for(i = 0; i != h; ++i)
// 	{
// 		for(j = 0; j != w; ++j)
// 		{
////		LOGW("stepDepth: %d", flagArray[i * w + j]);
// 			if( i > bound + stepDepth[j])
// 			{
// 	 			int tmpRR = (i - w / 2) * (i - w / 2) + (j - h / 2) * (j - h / 2);
// 	 			int ra = w / 2 * w / 2;
// 	 			if( tmpRR < ra)
// 	 			{
// 	 				flagArray[i * w + j] = 1;
// 	 				flagArray[i * w + j] = 250 - (float)tmpRR/ra * 200;
// 	 			}
// 			}
// 		}
// 	}

// 	for(i = 0; i != h; ++i)
// 	{
// 		for(j = 0; j != w; ++j)
// 		{
////		LOGW("stepDepth: %d", flagArray[i * w + j]);
// 			if( i > bound + stepDepth[j])
// 			{
// 	 			int tmpRR = (i - w / 2) * (i - w / 2) + (j - h / 2) * (j - h / 2);
// 	 			int ra = w / 2 * w / 2;
// 	 			if( tmpRR < ra)
// 	 			{
// 	 				flagArray[i * w + j] = 1;
// 	 				flagArray[i * w + j] = 160 - (float)tmpRR/ra * 150;
// 	 			}
// 			}
// 		}
// 	}

//	int tmpRR;
//	int ra;
// 	for(i = 0; i != h; ++i)
// 	{
// 		for(j = 0; j != w; ++j)
// 		{
// 			if( i > bound + stepDepth[j])
// 			{
// 	 			tmpRR = (i - w / 2) * (i - w / 2) + (j - h / 2) * (j - h / 2);
// 	 			ra = w / 2 * w / 2;
// 	 			if( tmpRR < ra)
// 	 			{
// 	 				flagArray[i * w + j] = 1;
//// 	 				LOGW("flagArray boud + stepDepth: %d", flagArray[i * w + j]);
// 	 			}
// 			}
// 		}
// 	}

 	 	for(i = 1; i != h - 1; ++i)
 	 	{
 	 		for(j = 1; j != w - 1; ++j)
 	 		{
 	 			if(flagArray[i * w + j] >= 1)
 	 			{
 	 				//���Ҫ��΢�ж�һ�£� ����ֱ�������������У�
 	 				flagArray[i * w + j] = 1 + Mymin(flagArray[(i - 1) * w + j - 1], flagArray[(i - 1) * w + j], flagArray[(i - 1) * w + j + 1], flagArray[i * w + j - 1]);
 	 			}
 	 		}
 	 	}
 	 	int tmpMin;
 	 	for(i = h - 2; i > 0; --i)
 	 	{
 	 		for(j = w - 2; j > 0; --j)
 	 		{
 	 			if(flagArray[i * w + j] >= 1)
 	 			{
 	 				tmpMin = Mymin(flagArray[(i + 1)* w + j + 1], flagArray[(i + 1) * w + j], flagArray[(i + 1) * w + j - 1], flagArray[i * w + j + 1]);
 	 				flagArray[i * w + j] = minx(flagArray[i * w + j], tmpMin + 1);
// 	 	 			if(flagArray[i * w + j] > 3)
// 	 	 				LOGW("flagArray: %d", flagArray[i * w + j]);
 	 			}

 	 		}
 	 	}
 	//
 	 	float tmpR;
 	 	for(i = 0; i != w * h; ++i)
 	 	{

 	 		if(flagArray[i] > yuhuaR)
 	 			flagArray[i] = 255;
 	 		else
 	 		{
 	 			tmpR = (float)(flagArray[i]) / yuhuaR;
 	 			flagArray[i] = tmpR * 255;
 //	 			LOGW("flagArray tmpR: %d", flagArray[i]);
 	 		}
 	 	}
}

int DetectUpBounder(float *arr, int w, int h, int midValue)
{
	//�����Ҫ���Ǹ����Ŷ�ͼ��������
	stepDepth = (int *)malloc(sizeof(int) * w);
	float realstep;
	int i,j;
	//////////////
//	int *dataImg = (int *)malloc(sizeof(int) * w * h);
//	for(i = 0; i != w * h; ++i)
//		dataImg[i] = arr[i] * 255;

	///////////////////////
	for( i = 0; i != w; ++i)
	{
		if( i <= midValue)
		{
			realstep = maxDepth * i / midValue;
			if(realstep - trunc(realstep) >= 0.5)
				realstep += 1;
			stepDepth[i] = realstep;
//			LOGW("stepDepth: %d", stepDepth[i]);
		}
		else
		{
			realstep = maxDepth * (w - midValue - (i - midValue)) / ( w - midValue);
			if(realstep - trunc(realstep) >= 0.5)
				realstep += 1;
			stepDepth[i] = realstep;
//			LOGW("stepDepth: %d", stepDepth[i]);
		}
	}

	//�����Ӧ��ֵ
	//������������,�ҵ�һ������ֵ��ѡ
	int StTotal = 0;
	int tmpSt = 0;
	int maxCount = 0;
	int maxLevel = 0;
	int Smax = 0;
	//��һ����0��ʼ��
	for(i = 2 * h / 7; i < h - maxDepth - 1; ++i)
	{
		StTotal = 0;
		maxCount = 0;
		Smax = 0;
		for(j = 0; j != w; ++j)
		{
			tmpSt = arr[(i + stepDepth[j]) * w + j] * 255;
			if(tmpSt > 150)
			{
				StTotal += 1;
				Smax ++;
			}
			else
			{
				if(StTotal > maxCount)
					maxCount = StTotal;
				StTotal = 0;
			}
		}

		if(	maxCount * 1.0/  w > 0.5 || Smax * 1.0 / w > 0.6)
		{
			maxLevel = i;
			break;
		}
	}

	//����Ҫ��¼��Ӧ�е�

//	if(maxLevel = -1)
//		return -1;
//	else

	free(stepDepth);

	return maxLevel;
}

static inline int JudgeSmall(pro a, pro b, int realx, int realy)
{
//	LOGW("(float)(a.y - realy))/(a.x - realx): %f", ((float)(a.y - realy))/(a.x - realx));
//	LOGW("(float)(b.y - realy))/(b.x - realx): %f", ((float)(b.y - realy))/(b.x - realx));
	if(a.dis < b.dis)
		return 1;
	if(a.dis == b.dis && ((float)(a.y - realy))/(a.x - realx) <= ((float)(b.y - realy))/(b.x - realx))
		return 1;
	return 0;
}

void myQuickSort(pro *a, int left, int right, int realx, int realy)
{
	int p = (left + right)/2;
	pro key = a[p];
	int i,j;
    for(i = left,j = right; i < j;)
    {
        while(!(JudgeSmall(key, a[i], realx, realy)|| p < i)) //key.dis < a[i].dis
            i++;
        if(i < p)
        {
            a[p] = a[i];
            p = i;
        }
        while(j>0 && !(j < p || JudgeSmall(a[j], key, realx, realy))) //a[j].dis < key.dis
            j--;
         if(p < j)
         {
             a[p] = a[j];
             p = j;
         }
     }
     a[p] = key;
     if(p - left > 1)
    	 myQuickSort(a,left,p-1,realx, realy);
     if(right - p > 1)
    	 myQuickSort(a,p + 1, right, realx, realy);
}

void detectTargetArea(float *arr, int *flagArray, int w, int h, int x, int y, int r) // �ýǶȹ���һ��ֵ,��ֱ����
{
	int i,j;
	int fixedRadius = 4 * r;
	x = r;
	y = r;
	memset(flagArray, 0, sizeof(int) * w * h);
	int realy = y + fixedRadius * sin(thelta);
	int realx = x - fixedRadius * cos(thelta);


	pro *processArr = (pro *)malloc(sizeof(pro) * w * h);
	for(i = 0; i != h; ++i)
	{
		for(j = 0; j != w; ++j)
		{
			processArr[i * w + j].dis = sqrt( (i - realx) * (i - realx) + (j - realy) * (j - realy));
			processArr[i * w + j].val = arr[i * w + j] * 255;
			processArr[i * w + j].x = i;
			processArr[i * w + j].y = j;
		}
	}



	//����
	myQuickSort(processArr, 0, w * h - 1, realx, realy);

	//////////////////////////////////////////////////////////////////////////
	int re = 0;
	int count = 1;
	int realcount = 1;
	int recordDis = processArr[0].dis;

	int StTotal = 0;
	int tmpSt = 0;
	int maxCount = 0;
	int maxLevel = 0;
	int Smax = 0;

	for(i = 1; i != w * h; ++i)
	{
//		LOGW("processArr[i].dis: %d", processArr[i].dis);
//		LOGW("recordDis: %d", recordDis);
		if(processArr[i].dis <= 3.5 * r)
		{
			recordDis = processArr[i].dis;
			continue;
		}
		if(processArr[i].dis == recordDis)
		{
			count ++;
			if(processArr[i].val > 150)
			{
				realcount ++;
				StTotal ++;
			}
			else
			{
				if(StTotal > Smax)
					Smax = StTotal;
				StTotal = 0;
			}
//			LOGW("count: %d", count);
//			LOGW("realcount: %d", realcount);
		}
		else
		{
			if(count >= 2 * r)
			{
				LOGW("rate: %f", (float)realcount / count);
				if((float)realcount / count > 0.6 || (float)Smax / count > 0.5)
				{
					re = recordDis;
					break;
				}
				count = 1;
				realcount = 1;
				Smax = 0;
				StTotal = 0;
			}
			recordDis = processArr[i].dis;
		}
	}

	LOGW("re: %d", re);
//	re = 140;
	for(i = 0; i != h; ++i)
	{
		for(j = 0; j != w; ++j)
		{
			if( sqrt( (i - realx) * (i - realx) + (j - realy) * (j - realy)) > re && sqrt( (i - r) * (i - r) + (j - r) * (j - r)) <= r)
			{
				flagArray[i * w + j] = 1;
			}
		}
	}

	free(processArr);
}

void doProcess(int *tmpArr, float *tmpflag, int *flagArray, int w, int h, int modifyColor)
{
 	int ki = 0;
 	int i,j;
	for(ki = 0; ki != h; ++ki)
	{
		for(j = 0; j != w; ++j)
		{
			tmpflag[ki * w + j] = sqrt((double)CalculateDistance(tmpArr[ki * w + j], modifyColor));
		}
	}

	int midV = tmpflag[0] / 2;
	for(i = 1; i != w * h; ++i)
	{
		if(tmpflag[i] > midV * 2)
			midV = tmpflag[i] / 2;
	}
	float tmpfloat;
	for(i = 0; i != w * h; ++i)
	{
		tmpfloat = tmpflag[i] - midV;
		tmpflag[i] = exp( - tmpfloat * tmpfloat/ (2 * 5000) );
	}

	for(ki = 0; ki != h; ++ki)
	{
		for(j = 0; j != w; ++j)
		{
			if(flagArray[ki * w + j] > 0)
			{
				flagArray[ki * w + j] = tmpflag[ki * w + j] * 255;
			}
		}
	}

//	int midValue = w / 2;

//	detectTargetArea(tmpflag, flagArray, w, h, xr, yr, w / 2);

//	DetectUpBounder(tmpflag, w, h, midValue);
}

void moreProcess(int *arr,int *tmpArr, int *tmpBackup, int *flagArray, int r, int x, int y, int *modifyColor)
{
//	LOGW("moreProcess Start: %d", r);
	memset(flagArray, 0, sizeof(int) * 4 * r * r);

	ForwardAssignment(arr, tmpArr, x + 0.6 * r * cos(thelta), y - 0.6 * r * sin(thelta), r);
//	LOGW("cos(atan(angle)): %f", cos(atan(angle)));
//	LOGW("sin(atan(angle): %f", sin(atan(angle)));

 	float *tmpflag = (float *)malloc(sizeof(float) * r * r * 4);
 	memset(tmpflag, 0, sizeof(float) * r * r * 4);
 	KmeanFunction(tmpArr, flagArray, 2 * r, 2 * r, modifyColor);
 	LOGW("KmeanFunction End: %d", *modifyColor);
 	doProcess(tmpArr, tmpflag, flagArray, r * 2, r * 2, *modifyColor);
 	memcpy(tmpBackup, tmpArr, sizeof(int) * 4 * r * r);
	ModifyImage(tmpArr, flagArray, 2 * r, 2 * r, *modifyColor);

	detectTargetArea(tmpflag, flagArray, 2 * r, 2 * r, x + 0.6 * r * cos(thelta), y - 0.6 * r * sin(thelta), r);

   	free(tmpflag);
}

void processCircle(int *arr, int w, int h)
{
	int i ,j, ki;
	int *tmpArr1 = (int *)malloc(sizeof(int) * radius1 * 4 * radius1);
	int *flagArray1 = (int *)malloc(sizeof(int) * 4 * radius1 * radius1);
	int *tmpBackup1 = (int *)malloc(sizeof(int) * 4 * radius1 * radius1);
	int *tmpModel1 = (int *)malloc(sizeof(int) * 4 * radius1 * radius1);

	int *tmpArr2 = (int *)malloc(sizeof(int) * radius2 * 4 * radius2);
	int *flagArray2 = (int *)malloc(sizeof(int) * 4 * radius2 * radius2);
	int *tmpBackup2 = (int *)malloc(sizeof(int) * 4 * radius2 * radius2);
	int *tmpModel2 = (int *)malloc(sizeof(int) * 4 * radius2 * radius2);
//	midValue =  radius;
//	produceModifyColor(arr, width, height, yl, xl, radius, &modifyColor1);
//	produceModifyColor(arr, width, height, xr, yr, radius, &modifyColor2);

	moreProcess(arr, tmpArr1, tmpBackup1, flagArray1, radius1, xl, yl, &modifyColor1);
	moreProcess(arr, tmpArr2, tmpBackup2, flagArray2, radius2, xr, yr, &modifyColor2);
	//////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////
//	LOGW("bound1: %d", bound1);
//	LOGW("bound2: %d", bound2);
	modelProduce(flagArray1, 2 * radius1, 2 * radius1);
	modelProduce(flagArray2, 2 * radius2, 2 * radius2);

	myModifyImage(tmpBackup1, tmpArr1, flagArray1, radius1 * 2, radius1 * 2);
	myModifyImage(tmpBackup2, tmpArr2, flagArray2, radius2 * 2, radius2 * 2);

  	ReverseAssignment(arr, tmpArr1, flagArray1, xl + 0.6 * radius1 * cos(thelta), yl - 0.6 * radius1 * sin(thelta), radius1);
  	ReverseAssignment(arr, tmpArr2, flagArray2, xr + 0.6 * radius2 * cos(thelta), yr - 0.6 * radius2 * sin(thelta), radius2);

	free(tmpArr1);
	free(flagArray1);

	free(tmpArr2);
	free(flagArray2);

	free(tmpModel1);
	free(tmpModel2);

	free(tmpBackup1);
	free(tmpBackup2);

}
void ReduceEffect(int *src, int *dst, int w, int h, float ratio)
{
	int i;
	for(i = 0; i != w * h; ++i)
	{
		setR(&src[i], getR(dst[i]) * ratio + getR(src[i]) * (1.0 - ratio));
		setG(&src[i], getG(dst[i]) * ratio + getG(src[i]) * (1.0 - ratio));
		setB(&src[i], getB(dst[i]) * ratio + getB(src[i]) * (1.0 - ratio));
	}
}
void InitializeCircle(int eyex1, int eyey1, int eyeradius1, int eyex2, int eyey2, int eyeradius2, int *srcPixArray, int w, int h, int scale)
{
	// x, y are both reversed , Attention please !!!!!
	width = w;
	height = h;
	xl = eyex1;
	xr = eyex2;
	yl = eyey1;
	yr = eyey2;

	deltaX = xr - xl;
	deltaY = yr - yl;
//	haveReversed = 0;

	maxDepth = 0;
	angle = 0;
	thelta = atan(angle);

	int radius = sqrt((xl - xr) * (xl - xr) + (yl - yr) * (yl - yr)) / 3;
	radius1 = Mymin(abs(xl + 0.6 * radius * cos(thelta) - 0), abs(xl + 0.6 * radius * cos(thelta) + 1 - height), abs(yl - 0.6 * radius * sin(thelta) - 0), abs(yl - 0.6 * radius * sin(thelta) - w + 1));
	radius1 = minx(radius1 , radius);
	radius2 = Mymin(abs(xr + 0.6 * radius * cos(thelta) - 0), abs(xr + 0.6 * radius * cos(thelta) + 1 - height), abs(yr - 0.6 * radius * sin(thelta) - 0), abs(yr - 0.6 * radius * sin(thelta) - w + 1));
	radius2 = minx(radius2 , radius);
//	LOGW("radius1: %d", radius1);
//	LOGW("radius2: %d", radius2);

	if(radius1 < 10 || radius2 < 10)
		return ;

//	LOGW("thelta: %f", thelta);
	processCircle(srcPixArray, width, height);
}
