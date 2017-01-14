#ifndef PROGRESSIVE_H
#define PROGRESSIVE_H

#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
//#include <math.h>


void lineInitialize(int tmpR, int w, int h, int tmpTimes, int channel);
int *lineRecursion(int *pixels, int k, int flag);
int *lineProcess(int *srcPixels);
void sourceRelease();

#endif
