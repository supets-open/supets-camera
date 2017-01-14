
#ifndef NIGHTSHOOT_H
#define NIGHTSHOOT_H

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include "operation.h"
#include "hdr.h"
#include "USM.h"

#include <android/log.h>
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)
void gamaRect(int * src, int *dst, int w, int h, unsigned char * map);

/*
调用方式
ImageInput( src,  dst, w, h, 5, 0)；
ImageInput( src,  dst, w, h, 5, 1)；
ImageInput( src,  dst, w, h, 5, 2)；
ImageInput( src,  dst, w, h, 5, 3)；
ImageInput( src,  dst, w, h, 5, 4)；
*/
//void NightImageInput(int * src, int * dst, int w, int h, int img_total, int img_id);
/*
调用方式
NightGenerate(srcImages, dst , w, h, 5);
*/
void NightGenerate(int ** srcImages, int * dst, int w, int h, int img_total);
void NightGenerateYUV(unsigned char ** YUV, int * dstRGB, int w_src, int h_src, int w_dst, int h_dst, int img_total);//用这个
#endif
