#ifndef HDR_H
#define HDR_H

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include "operation.h"
#include "whiteBalance.h"
#include "LocaEnhance.h"
#include "USM.h"
#include "NightShoot.h"
#include <android/log.h>
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)



void HistMap(int * hist, int * map, int hist_sum );
static inline void RGB2HSI(int r, int g, int b, double *H, double *S, double *I);
static inline void HSI2RGB(int *r, int *g, int *b, double H, double S, double I);
int GetEdgMax(unsigned char * Y, int width, int height,int * max_x, int * max_y, int * num, int thre);

inline int FastMatch(unsigned char * src1, unsigned char * src2,
			  int height, int width, int channel,
			  int center_x ,int center_y,
			  int center_x1 ,int center_y1,
			  int match_block_size,int skip, int match_scale,
			  int * position_x, int * position_y);

int SelectNum(int * x1, int * y1,int * x2, int * y2, int n,int w, int h, int * max_x, int * max_y);
//void getRT_final(unsigned char * gray1,unsigned char * gray2,int w, int h,
//				 int * x1, int * y1, int * x2, int * y2, int num,
//				 float * RT_final,float * RT_final2);


void getRT_final(unsigned char * gray1,unsigned char * gray2, unsigned char * gray_rectify, int w, int h,
				 int * x1, int * y1, int * x2, int * y2, int num,
				 float * RT_final,float * RT_final2);


void rectifyRGB(int * src, int * dst,int w,int h, float * RT, int skip);

void GetEdg(unsigned char * Y, unsigned char * edg, int width, int height, int thre);
void EdgHistEnhance(unsigned char * Y,unsigned char * edg, int w, int h );
void ImageChangeY1(int * image, unsigned char * Y_dst, int size );

void HDRImageGenerate1(int *image1,int *image2, int * dst_rgb, int w, int h);
void HDR(int * imgDark, int * imgBright, int * dst, int w, int h);


void HDRsimple(int * img, int w, int h, int block , int edg_thre);
#endif
