#ifndef MCRCR_H
#define MCRCR_H

#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
//#include <math.h>


void MSRCR (int *src, int width, int height);
void GaussSmoothMethod(float *in, int width, int height, int sigma);
void GaussSmoothMethodAllChannel(int *srcPixArray, int width, int height, int sigma);
#endif
