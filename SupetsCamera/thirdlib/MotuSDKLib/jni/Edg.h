#ifndef EDG_H
#define EDG_H

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include "operation.h"
#include "whiteBalance.h"
#include "LocaEnhance.h"
#include "USM.h"
#include "NightShoot.h"
#include "layer.h"
#include "MSRCR.h"
#include <android/log.h>
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)

void Posterize(int * img , int w, int h ,int levels);//色调分离
void Kirsch(int * rgb, int w, int h , int kind);

void WaterColor(int * src , int * model, int w, int h);
void Pencil(int * src, int * model1, int * model2, int w, int h);
void ColorPencil(int * src, int * model1, int * model2, int w, int h);
void BlueEdg(int * src, int * model1, int * model2, int w, int h);
void OilPainting(int * src, int * model1, int * model2, int w, int h);
void setHopeEffectTexturePixels(int *texArray, int w, int h);
void HopeEffect(int *srcPixArray, int w, int h);
#endif
