#ifndef SKIN_H
#define SKIN_H

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include "operation.h"
#include "whiteBalance.h"

#include "USM.h"

#include "layer.h"
#include "MSRCR.h"
#include <android/log.h>
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)


void skinOverLay(int * src, int * dst, int w, int h);
#endif
