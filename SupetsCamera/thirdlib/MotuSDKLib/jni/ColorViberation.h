#ifndef COLORVIBERATION_H
#define COLORVIBERATION_H

#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
//#include <math.h>


void ViberationInitial(int *srcPixArray, int w, int h);

void ViberationControl(int *srcPixArray, int w, int h, float  degree);

void ViberationRelease();

#endif
