#ifndef AUTOCONTRAST_H
#define AUTOCONTRAST_H

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include "operation.h"

#include "whiteBalance.h"
#include <android/log.h>
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)

void getHist_Min_Max(int * hist, int thre_low, int thre_high, int * gray_min, int * gray_max);
void contrastMap(int * map, int min , int max);
void autoContrast(int * img, int w, int h, float thre_low, float thre_high);

#endif
