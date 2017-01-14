#ifndef LOCAENHANCE_H
#define LOCAENHANCE_H

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include "operation.h"
#include "whiteBalance.h"
#include "hdr.h"
#include <android/log.h>
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)



void LocaEnhance(unsigned char * gray,unsigned char * dst, int w, int h, int block_num, int edg_thre);
void LocaEnhanceRGB(int * img , int w, int h, int block_num, int edg_thre);

void CLAHE_GRAY(unsigned char * gray,unsigned char * dst, int w, int h, int block_num, int edg_thre);
void CLAHERGB3(int * img , int w, int h, int block_num, int edg_thre);
#endif
