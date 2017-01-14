#ifndef GIFPROCESS_H
#define GIFPROCESS_H

#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
//#include <math.h>


//static inline int getG(int color) {
//	return ((color >> 8) & 0xFF);
//}
//
//static inline int getR(int color) {
//	return ((color >> 16) & 0xFF);
//}
//
//static inline int getB(int color) {
//	return (color & 0xFF);
//}
//
//static inline int setG(int *color, int c) {
//	(*color) = (*color) & 0xFFFF00FF;
//	(*color) = (*color) | (c << 8);
//	return (*color);
//}
//
//static inline int setR(int *color, int c) {
//	*color = (*color) & 0xFF00FFFF;
//	(*color) = (*color) | (c << 16);
//	return (*color);
//}
//
//static inline int setB(int *color, int c) {
//	(*color) = (*color) & 0xFFFFFF00;
//	(*color) = (*color) | c;
//	return (*color);
//}

void gifProcess(int *pixels, int w, int h, int *p, int mouthCondition, int browCondition, int eyeCondition);

#endif
