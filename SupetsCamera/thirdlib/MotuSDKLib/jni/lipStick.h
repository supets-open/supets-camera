#ifndef LIPSTICK_H
#define LIPSTICK_H

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include "operation.h"
#include "whiteBalance.h"
#include "autoContrast.h"
#include <android/log.h>
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)

int lipstick(int * Pixels, int * color, int w, int h, int *p);
#endif
