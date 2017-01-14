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
#define PI 3.1415926

//逐行算法
//提供一种单通道的均值滤波接口

int **dataSet_Line;
int **Buffer_Line;
int *line_Array;
int *real_result;
int line_R;
int line_Times;
int line_W;
int line_H;
int wflag;
int nChannel;

void lineInitialize(int tmpR, int w, int h, int tmpTimes, int channel)
{
	int i;
	line_R = tmpR;
	line_Times = tmpTimes;
	line_W = w;
	line_H = h;
	nChannel = channel;

	line_Array = (int *)malloc(sizeof(int) * line_Times);
	memset(line_Array, 0, sizeof(int) * line_Times);
	dataSet_Line = (int **)malloc(sizeof(int *) * line_Times);
	Buffer_Line = (int **)malloc(sizeof(int *) * line_Times);
	real_result = (int *)malloc(sizeof(int) * line_W);


	for(i = 0; i != line_Times; ++i)
	{
		dataSet_Line[i] = (int *)malloc(sizeof(int) * (2 * line_R + 1) * line_W);

		memset(dataSet_Line[i], 0, sizeof(int) * (2 * line_R + 1) * line_W);

		Buffer_Line[i] = (int *)malloc(sizeof(int) * nChannel * line_W);
		memset(Buffer_Line[i], 0, sizeof(int) * nChannel * line_W);
	}

	LOGW("line_W %d", line_W);
	LOGW("line_H %d", line_H);
	LOGW("channel %d", channel);
	LOGW("line_R %d", line_R);
}

int *lineRecursion(int *pixels, int k, int flag)
{
	int tmpf = 0;
	int i;
	int *tmpResult = NULL;

	if( line_Array[k] >= line_H + line_R )
	{
		wflag = -1;
		tmpResult = lineRecursion(NULL, k + 1, 0);
		return tmpResult;
	}
	if(line_Array[k] >= line_H && wflag == -1)
	{
		wflag = 1;
		tmpResult = lineRecursion(NULL, k, 0);
		return tmpResult;
	}

	if(flag == 0)
	{

		tmpResult = (int *)malloc(sizeof(int) * line_W);

		for(i = 0; i != line_W; ++i)
		{

//			int realIndex = ( line_Array[k] % (2 * line_R + 1) - 2 * (line_Array[k] - line_H + 1) ) % (2 * line_R + 1);

			tmpResult[i] = dataSet_Line[k][ ((line_Array[k] + 1) % (2 * line_R + 1)) + i];

//			tmpResult[i] = dataSet_Line[k][realIndex];
		}

		wflag = 1;

		memcpy(real_result, tmpResult, sizeof(int) * line_W);
		free(tmpResult);

		return lineRecursion(real_result, k, 1);
	}
	else
	{
		wflag = -1;
		for(i = 0; i != line_W; ++i)
		{
			dataSet_Line[k][ (line_Array[k] % (2 * line_R + 1)) * line_W + i] = pixels[i];
		}

		int tmpTotal_R = 0, tmpTotal_G = 0, tmpTotal_B = 0, tmpTotal = 0;

		if(line_Array[k] >= line_R)
		{
			tmpf = 1;
			tmpResult = (int *)malloc(sizeof(int) * line_W);
			memcpy(tmpResult, pixels, sizeof(int) * line_W);

			if(line_Array[k] == line_R)
			{
				if(nChannel == 3)
				{
					for(i = 0; i != line_W; ++i)
					{
						Buffer_Line[k][i * 3] += getR(pixels[i]) * 2;
						Buffer_Line[k][i * 3 + 1] += getG(pixels[i]) * 2;
						Buffer_Line[k][i * 3 + 2] += getB(pixels[i]) * 2;
					}
				}
				else
				{
					for(i = 0; i != line_W; ++i)
						Buffer_Line[k][i] += pixels[i] * 2;
				}
			}
			else
			{
				if(nChannel == 3)
				{
					for(i = 0; i != line_W; ++i)
					{
						Buffer_Line[k][i * 3] += getR(pixels[i]) * 1;
						Buffer_Line[k][i * 3 + 1] += getG(pixels[i]) * 1;
						Buffer_Line[k][i * 3 + 2] += getB(pixels[i]) * 1;
					}
				}
				else
				{
					for(i = 0; i != line_W; ++i)
						Buffer_Line[k][i] += pixels[i];
				}
			}

			for(i = -line_R; i <= line_R; ++i)
			{
				if(nChannel == 3)
				{
					tmpTotal_R += Buffer_Line[k][ abs(i) * 3];
					tmpTotal_G += Buffer_Line[k][ abs(i) * 3 + 1];
					tmpTotal_B += Buffer_Line[k][ abs(i) * 3 + 2];
				}
				else
					tmpTotal += Buffer_Line[k][abs(i)];
			}



				if(nChannel == 3)
				{
					for(i = 0; i != line_W; ++i)
					{
						setR(&tmpResult[i], tmpTotal_R / ((2 * line_R + 1) * (2 * line_R + 1)));
						setG(&tmpResult[i], tmpTotal_G / ((2 * line_R + 1) * (2 * line_R + 1)));
						setB(&tmpResult[i], tmpTotal_B / ((2 * line_R + 1) * (2 * line_R + 1)));

						tmpTotal_R -= Buffer_Line[k][ abs(i - line_R) * 3];
						tmpTotal_G -= Buffer_Line[k][ abs(i - line_R) * 3 + 1];
						tmpTotal_B -= Buffer_Line[k][ abs(i - line_R) * 3 + 2];

						tmpTotal_R += Buffer_Line[k][ (line_W - 1 - abs(i + line_R + 1 - line_W + 1)) * 3];
						tmpTotal_G += Buffer_Line[k][ (line_W - 1 - abs(i + line_R + 1 - line_W + 1)) * 3 + 1];
						tmpTotal_B += Buffer_Line[k][ (line_W - 1 - abs(i + line_R + 1 - line_W + 1)) * 3 + 2];
					}
				}
				else
				{
					for(i = 0; i != line_W; ++i)
					{
						tmpResult[i] = tmpTotal / ((2 * line_R + 1) * (2 * line_R + 1));
						tmpTotal -= Buffer_Line[k][abs(i - line_R)];
						tmpTotal += Buffer_Line[k][ (line_W - 1 - abs(i + line_R + 1 - line_W + 1))];
					}

				}


			if(line_Array[k] >= 2 * line_R + 1)
			{
				if(nChannel == 3)
				{
					for(i = 0; i != line_W; ++i)
					{
						Buffer_Line[k][i * 3] -= getR( dataSet_Line[k][ ((line_Array[k] + 1) % (2 * line_R + 1))  * line_W + i]);
						Buffer_Line[k][i * 3 + 1] -= getG( dataSet_Line[k][ ((line_Array[k] + 1) % (2 * line_R + 1))  * line_W + i]);
						Buffer_Line[k][i * 3 + 2] -= getB( dataSet_Line[k][ ((line_Array[k] + 1) % (2 * line_R + 1))  * line_W + i]);
					}
				}
				else
				{
					for(i = 0; i != line_W; ++i)
						Buffer_Line[k][i] -= dataSet_Line[k][ ((line_Array[k] + 1) % (2 * line_R + 1))  * line_W + i];
				}

			}
			else
			{
				if(nChannel == 3)
				{
					for(i = 0; i != line_W; ++i)
					{
						Buffer_Line[k][i * 3] -= getR( dataSet_Line[k][ (line_R - (line_Array[k] - line_R))  * line_W + i]);
						Buffer_Line[k][i * 3 + 1] -= getG( dataSet_Line[k][ (line_R - (line_Array[k] - line_R))  * line_W + i]);
						Buffer_Line[k][i * 3 + 2] -= getB( dataSet_Line[k][ (line_R - (line_Array[k] - line_R))  * line_W + i]);
					}
				}
				else
				{
					for(i = 0; i != line_W; ++i)
						Buffer_Line[k][i] = dataSet_Line[k][ (line_R - (line_Array[k] - line_R))  * line_W + i];
				}

			}
			memcpy(real_result, tmpResult, sizeof(int) * line_W);
			free(tmpResult);

		}
		else
		{
			int tmpflag = 2;
			if(line_Array[k] == 0)
				tmpflag = 1;

				if(nChannel == 3)
				{
					for(i = 0; i != line_W; ++i)
					{
						Buffer_Line[k][i * 3] += getR(pixels[i]) * tmpflag;
						Buffer_Line[k][i * 3 + 1] += getG(pixels[i]) * tmpflag;
						Buffer_Line[k][i * 3 + 2] += getB(pixels[i]) * tmpflag;
					}
				}
				else
				{
					for(i = 0; i != line_W; ++i)
						Buffer_Line[k][i] = pixels[i] * tmpflag;
				}
			tmpflag = 2;
		}

		line_Array[k] ++;
	}

	if(k < line_Times - 1 && line_Array[k] > line_R)
	{
		return lineRecursion(real_result, k + 1, 1);
	}
	else if(k == line_Times - 1 && tmpf == 1)
	{
		return real_result;
	}
	else
	{
		return NULL;
	}
}

int (*setColor)(int *color, int c);
int *lineNonRecursion(int *pixels)
{

	int k = 0;
	int flag = 0;
	int i, j, l;
	int tmpf = 0;
	int *tmpResult;
	int tmpTotal[3];
	wflag = 0;
//	LOGW("The Value of line_Array[k] %d",  line_Array[k]);
	while(1)
	{
		while(line_Array[k] >= line_H + line_R)
		{
			k ++;
		}

		if(line_Array[k] >= line_H && wflag == 0)
			wflag = 0;
		else
			wflag = 1;

		if(wflag == 1)
		{

			for(i = 0; i != line_W; ++i)
			{
				dataSet_Line[k][ (line_Array[k] % (2 * line_R + 1)) * line_W + i] = pixels[i];
			}

			memset(tmpTotal, 0, sizeof(int) * 3);


			if(line_Array[k] >= line_R)
			{
				int nu = 1;
				tmpf = 1;
				tmpResult = (int *)malloc(sizeof(int) * line_W);
				memcpy(tmpResult, pixels, sizeof(int) * line_W);

				if(line_Array[k] == line_R)
				{
					nu = 2;
				}

				for(i = 0; i != line_W; ++i)
				{
					for(l = 0; l != 3; ++l)
						Buffer_Line[k][i * 3 + l] += nu * ( (pixels[i] >> (8 * (2 - l))) & 0xFF );
				}

				for(i = -line_R; i <= line_R; ++i)
				{
					for(l = 0; l != 3; ++l)
						tmpTotal[l] += Buffer_Line[k][ abs(i) * 3 + l];
				}



				for(i = 0; i != line_W; ++i)
				{
					for(l = 0; l != 3; ++l)
					{
						switch(l)
						{
							case 0:
								setColor = setR;
								break;
							case 1:
								setColor = setG;
								break;
							case 2:
								setColor = setB;
								break;
 							default:
 								break;
						}

						(*setColor)(&tmpResult[i], tmpTotal[l] /((2 * line_R + 1) * (2 * line_R + 1)));
						tmpTotal[l] -=  Buffer_Line[k][ abs(i - line_R) * 3 + l];
						tmpTotal[l] += Buffer_Line[k][ (line_W - 1 - abs(i + line_R + 1 - line_W + 1)) * 3 + l];
					}

				}

				if(line_Array[k] >= 2 * line_R + 1)
				{
					for(i = 0; i != line_W; ++i)
					{
						int tmppp = dataSet_Line[k][ ((line_Array[k] + 1) % (2 * line_R + 1))  * line_W + i];
						for(l = 0; l != 3; ++l)
							Buffer_Line[k][i * 3 + l] -= ( (tmppp >> (8 * (2 - l))) & 0xFF );
					}
				}
				else
				{

					for(i = 0; i != line_W; ++i)
					{
						int tmppp = dataSet_Line[k][ (line_R - (line_Array[k] - line_R))  * line_W + i];
						for(l = 0; l != 3; ++l)
							Buffer_Line[k][i * 3 + l] -= ( (tmppp >> (8 * (2 - l))) & 0xFF );
					}

				}

				memcpy(real_result, tmpResult, sizeof(int) * line_W);
				free(tmpResult);

//				line_Array[k] ++;
			}
			else
			{
				tmpf = 0;
				int tmpflag = 2;
				if(line_Array[k] == 0)
					tmpflag = 1;
				for(i = 0; i != line_W; ++i)
				{
					for(l = 0; l != 3; ++l)
						Buffer_Line[k][i * 3 + l] += tmpflag *  ( (pixels[i] >> (8 * (2 - l))) & 0xFF);
				}
			}

			line_Array[k] ++;

			if(tmpf == 0)
				return NULL;

			if(k == line_Times - 1)
				return real_result;

			if(tmpf == 1)
			{
				memcpy(pixels, real_result, sizeof(int) * line_W);
				k ++;
			}

		}
		else
		{
			//由已经有的数据生成新的数据作为输入
			//pixels = (int *)malloc(sizeof(int) * line_W);
//			LOGW("pixels %d", pixels[0]);
			for(i = 0; i != line_W; ++i)
			{
				//int realIndex = ( line_Array[k] % (2 * line_R + 1) - 2 * (line_Array[k] - line_H + 1) ) % (2 * line_R + 1);
				real_result[i] = dataSet_Line[k][ ((line_Array[k] + 1) % (2 * line_R + 1)) + i];
				//tmpResult[i] = dataSet_Line[k][realIndex];
			}

			pixels = real_result;
			wflag = 1;
//			line_Array[k] ++;
//			k ++;
		}

	}

}
int *lineProcess(int *pixels)
{
//		LOGW("lineProcess");
//		if(pixels == NULL)
//			LOGW("lineProcess pixels is null");
		return lineNonRecursion(pixels);
// 	return lineRecursion(pixels, 0, 1);
}

void sourceRelease()
{
	int i;

	free(real_result);

	for(i = 0; i != line_Times; ++i)
		free(dataSet_Line[i]);
	free(dataSet_Line);

	for(i = 0; i != 2 * line_Times + 1; ++i)
		free(Buffer_Line[i]);
	free(Buffer_Line);
}
