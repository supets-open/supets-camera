#ifndef MTPROCESSOR_H
#define MTPROCESSOR_H

#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
#include <math.h>
#include <android/log.h>

#include "operation.h"
#include "layer.h"

#define LOGS(...) __android_log_print(ANDROID_LOG_SILENT, "Native", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "Native", __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)

extern void equlizeHist(int* pixels, int w, int h);
extern void normalization(int* pixels, int w, int h, int clipShadows, int clipHighlights);
extern void autoContrast(int* pixels, int w, int h, int clipShadows, int clipHighlights);
extern void autoColor(int* pixels, int w, int h, int clipShadows, int clipHighlights);
extern void brightEffect(int* srcPixArray, int  w, int  h, int bb);
extern void contrastEffect(int* srcPixArray, int  w, int  h, int bb);
extern void hue(int* srcPixArray, int w, int h, int bb);

extern void smoothEffect(int* srcPixArray, int w, int h);
extern void singleColor(int* srcPixArray,int w, int h,float* matrix, float targetRed,float targetGreen,float targetBlue,float threshold,float maxup);
extern void skinWhitePointEffect(int* oldPixels, int *pixels, int w, int h, int x, int y, int r);
extern void skinWhiteTeethPointEffect(int* oldPixels, int *pixels, int w, int h, int x, int y, int r);
extern void skinSmoothPointEffect(int *pixels, int w, int h, int x, int y, int r);
extern void eyeEnlarge(int* srcPixArray, int w, int h, int x, int y, int r, float scale);
extern void eyeEnlargeWithTags(int* in, int* out, int w, int h, int* x, int* y, int* r, float* scale, int num);
extern void eyeBrighten(int* pixels, int w, int bb);
extern void thinEffect(int *pixels, int w, int h, int x, int y, int x2, int y2, int r, float scale, int flag);
extern void thinEffectAuto(int *pixels, int w, int h, int faceLeft, int faceRight, int faceTop, int faceBottom,int degree);
extern void thinEffectWholeFace(int *pixels, int w, int h, int faceLeft, int faceRight, int faceTop, int faceBottom, float scale);
void smileWholeMouth(int *pixels, int w, int h, int mouthLeft, int mouthRight, int mouthTop, int mouthBottom, float scale);
extern void redeye(int *pixels, int w, int h, int x, int y, int r);

extern void curve(int* srcPixArray,int* transr,int* transg,int* transb, int w,int h);
extern void blue(int* srcPixArray,int* transr,int* transg,int* transb, int w,int h);
extern void gray(int* srcPixArray, int w,int h);
extern void blur(int* srcPixArray, int w, int h, int x, int y, int r);
extern void lomo(int* srcPixArray, int w, int h, int color, int brightRatioI, int darkRatioI, int scope);
extern void dlomo(int* srcPixArray, int w, int h , int x , int y, int bound);
extern void llomo(int* srcPixArray, int w, int h, int x, int y, int bound);
extern void cover(int* basePixels, int* topPixels, int w, int h);

extern void gaussBlur(int* srcPixArray, int width, int height,int radius,float sigma);
extern void dreamy(int* srcPixArray, int width, int height,int radius);
extern void autoLevels(int* srcPixArray,int w, int h);
extern void averageBlur(int* srcPixArray,int w, int h, int radius);

extern void blurBackgrounByCircle(int* srcPixArray, int w, int h, int x, int y, int r0, int r1);
extern void blurBackgrounByLine(int* srcPixArray, int w, int h, int x, int y, int r0, int r1, double theta);
extern void blurBackground(int* srcPixArray, int w, int h, int x, int y, int r0, int r1);
extern void hdrEffect(int* srcPixArray, int w, int h, double alpha1, double alpha2);

extern void getDynamicFrame(int* frame, int* oriFrame, int w, int h, int oriW, int oriH);
extern void setVisibleArea(int* img, int w, int h, int *pts, int cnt);

extern void sketch(int* srcPixArray, int w, int h);
extern void popstyle(int* srcPixArray, int w, int h,int type);
extern void unsharp(int* srcPixArray,int* smoothImg, int w, int h, int r, int thresh,float amount);
extern void sharpen(int* srcPixArray, int w, int h, int r);
extern void fastAverageBlur(int* srcPixArray, int width, int height,int radius);
extern void relief(int* srcPixArray, int width, int height,int increment);
extern void emission(int* srcPixArray, int w, int h);
extern void etoc(int* srcPixArray, int w, int h);
extern void postivefilter(int* srcPixArray, int w, int h);
extern void colorLevelFilter(int* srcPixArray, int w, int h, int min, float gray, int max, int outMin, int outMax);

extern void saturationfilter(int* srcPixArray, int w, int h , float sValue);
extern void fastAverageBlurWithThreshold(int *srcPixArray, int width, int height, int radius, int threshold);
extern void fastAverageBlurWithThresholdAndWeight(int *srcPixArray, int width, int height, int radius, int threshold, int weight);
extern void fastAverageBlurWithThresholdWeightSkinDetection(int *srcPixArray, int width, int height,
		int radius, int threshold, int weight, int hmin, int hmax, int smin, int smax, int vmin, int vmax);

#endif /* MTPROCESSOR_H */
