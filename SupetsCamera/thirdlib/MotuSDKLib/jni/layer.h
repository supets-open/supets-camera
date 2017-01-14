#ifndef LAYER_H
#define LAYER_H

#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
#include <math.h>
#include <android/log.h>
#include "operation.h"

extern void Darken(int* pixels, int* pixelsLayer, int w, int h);

extern void Lighten(int* pixels, int* pixelsLayer, int w, int h);

extern void MultiplyAlpha(int* pixels, int* pixelsLayer, int w, int h, int alpha);

extern void RSMultiplyAlpha(int* pixels, int* pixelsLayer, int w, int h, int pw, int ph, int alpha);

extern void Multiply(int* pixels, int* pixelsLayer, int w, int h);

extern void RSMultiply(int* pixels, int* pixelsLayer, int w, int h, int pw,
		int ph);

extern void RSCoverage(int* pixels, int* pixelsLayer, int w, int h, int pw, int ph);

extern void Screen(int* pixels, int* pixelsLayer, int w, int h);

extern void Overlay(int* pixels, int* pixelsLayer, int w, int h);

extern void OverlayAlpha(int* pixels, int* pixelsLayer, int w, int h,
		int alpha);

extern void RSOverlay(int* pixels, int* pixelsLayer, int w, int h, int pw, int ph);

extern void RSOverlayAlpha(int* pixels, int* pixelsLayer, int w, int h, int pw, int ph, int alpha);

extern void SoftLight(int* pixels, int* pixelsLayer, int w, int h);

extern void Cover(int* basePixels, int* topPixels, int w, int h);

extern void AlphaComposite(int* basePixels, int* topPixels, int w, int h,
		float alpha);

extern void Dodge(int* pixels, int* pixelsLayer, int w, int h);

extern void LinearDodge(int* pixels, int* pixelsLayer, int w, int h);

extern void ColorBurn(int* pixels, int* pixelsLayer, int w, int h);

extern void RSLinearBurn(int* pixels, int* pixelsLayer, int w, int h, int pw,
		int ph, int alpha);

extern void LinearBurn(int* pixels, int* pixelsLayer, int w, int h,
		int layeralpha);

extern void ScreenWithLimitedLayer(int* pixels, int* pixelsLayer, int w, int h,
		int lw, int lh);

extern void RSScreenWithLimitedLayer(int* pixels, int* pixelsLayer, int w, int h, int lw,
		int lh);

extern void MergeSelection(int* pixels, int* pixelLayer, int* selection, int w,
		int h);

extern void MergeWeight(int* pixels, int* pixelLayer, int w, int h, int weight);

extern int switchIndex(int w, int h, int pw, int ph, int index);
#endif
