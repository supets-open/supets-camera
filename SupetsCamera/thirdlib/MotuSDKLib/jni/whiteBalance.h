#ifndef WHITEBALANCE_H
#define WHITEBALANCE_H

#include <stdio.h>
#include <sys/time.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include "operation.h"
#include <android/log.h>
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)






static inline long getCurrentTime()
{
   struct timeval tv;
   gettimeofday(&tv,NULL);
   return tv.tv_sec * 1000 + tv.tv_usec / 1000;//单位ms
}


static inline void setRGBA(int *color, int R, int G, int B, int A)
{
	(*color) = ((A << 24)+(R << 16)+(G << 8)+ B );
}
static inline void setRGB(int *color, int R, int G, int B)
{
	(*color) = (*color) & 0xFF000000;
	(*color) = (*color) |((R << 16)+(G << 8)+ B );
}
static inline int getMIN(int a, int b)
{
	return a<b?a:b;
}
static inline int getMAX(int a, int b)
{
	return a>b?a:b;
}

static inline void RGB2GRAY(int * img, unsigned char * gray, int size)
{
	int R,G,B,i;
	for (i = 0; i < size; i++)
	{
		getRGB(img[i],&R,&G,&B);
		gray [i] = (R+G+B)/3;
	}
}

void whiteBalance(int * img, int w, int h);



#endif
