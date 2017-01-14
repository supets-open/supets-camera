#ifndef DARKCIRCLE_H
#define DARKCIRCLE_H


#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <memory.h>
#include <time.h>
//#include <iomanip.h>


int width, height;
int modifyColor1, modifyColor2;
int xl, yl, xr, yr;
int radius1, radius2;
int yuhuaR = 30;
int times = 1;
//int midValue;
int maxDepth;
int *stepDepth;
float angle;
int deltaX;
int deltaY;
char whetherVetical = 0;
float thelta;
float transMatrix[4];

typedef struct
{
	int r;
	int g;
	int b;
}Co;

typedef struct
{
	int dis;
	int val;
	int x;
	int y;
}pro;

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

#endif
