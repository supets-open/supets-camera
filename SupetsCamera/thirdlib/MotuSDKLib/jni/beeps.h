#ifndef BEEPS_H
#define BEEPS_H

#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
//#include <math.h>

void beepsSetupAll(int *pixels, int w, int h);

void BEEPSVerticalHorizontal(double *data, int w, int h);

void BEEPSHorizontalVertical(double *data, int w, int h);

void beepsOverlay(int *src, int *dst);

void beepsDetailRecover(int *src, int *dst);

#endif
