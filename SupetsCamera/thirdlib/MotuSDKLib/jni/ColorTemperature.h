#ifndef COLORTEMPERATURE_H
#define COLORTEMPERATURE_H

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include "operation.h"
#include "whiteBalance.h"
#include <android/log.h>
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)


int ColorTemperature(int * img,int * dst, int size, int temperature);

#endif
