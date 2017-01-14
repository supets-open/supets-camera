#ifndef DECOLORIZATION_H
#define DECOLORIZATION_H

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include "operation.h"
#include "whiteBalance.h"
#include <android/log.h>
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)


void setupDecolorization(int * image, int w, int h);//rgb2gray主函数入口
void Decolorization(int * image, int size);

#endif
