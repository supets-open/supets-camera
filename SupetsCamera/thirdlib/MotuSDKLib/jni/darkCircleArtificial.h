#ifndef DARKCIRCLEARTIFICIAL_H
#define DARKCIRCLEARTIFICIAL_H

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <memory.h>
#include <time.h>
//#include <iomanip>

int width, height;
int mColor = 0;
int yuhuaRArtificial = 60;
typedef struct
{
	int r;
	int g;
	int b;
}Co;

static inline int getG(int color) {
	return ((color >> 8) & 0xFF);
}

static inline int getR(int color) {
	return ((color >> 16) & 0xFF);
}

static inline int getB(int color) {
	return (color & 0xFF);
}

static inline int setG(int *color, int c) {
	(*color) = (*color) & 0xFFFF00FF;
	(*color) = (*color) | (c << 8);
	return (*color);
}

static inline int setR(int *color, int c) {
	*color = (*color) & 0xFF00FFFF;
	(*color) = (*color) | (c << 16);
	return (*color);
}

static inline int setB(int *color, int c) {
	(*color) = (*color) & 0xFFFFFF00;
	(*color) = (*color) | c;
	return (*color);
}

int MyminArtificial(int a, int b, int c, int d)
{
	int re;
	re = min(a,b);
	re = min(re,c);
	re = min(re,d);
	return re;
}

#endif
