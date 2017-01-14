#ifndef LIGHTENDEMO_H
#define LIGHTENDEMO_H

#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
#include <math.h>

int ModifyTable[256] = { 0, 1, 3, 4, 5, 7, 8, 9, 10, 12, 13, 14, 16, 17, 18, 19,
		21, 22, 23, 25, 26, 27, 28, 30, 31, 32, 33, 35, 36, 37, 39, 40, 41, 42,
		43, 45, 46, 47, 48, 50, 51, 52, 53, 54, 56, 57, 58, 59, 60, 62, 63, 64,
		65, 66, 67, 69, 70, 71, 72, 73, 74, 75, 77, 78, 79, 80, 81, 82, 83, 84,
		86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 97, 98, 99, 100, 101, 102, 103,
		104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 118,
		119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132,
		133, 134, 135, 136, 137, 137, 138, 139, 140, 141, 142, 143, 144, 145,
		146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 157, 158,
		159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 168, 169, 170, 171,
		172, 173, 174, 175, 176, 177, 177, 178, 179, 180, 181, 182, 183, 184,
		185, 185, 186, 187, 188, 189, 190, 191, 192, 192, 193, 194, 195, 196,
		197, 198, 198, 199, 200, 201, 202, 203, 204, 204, 205, 206, 207, 208,
		209, 209, 210, 211, 212, 213, 214, 215, 215, 216, 217, 218, 219, 220,
		220, 221, 222, 223, 224, 225, 225, 226, 227, 228, 229, 230, 230, 231,
		232, 233, 234, 234, 235, 236, 237, 238, 239, 239, 240, 241, 242, 243,
		244, 244, 245, 246, 247, 248, 248, 249, 250, 251, 252, 253, 253, 254,
		255};

int LightenDemo(int *srcPixArray, int w, int h, int sAxis, int lAxis, int centerX, int centerY);
void LightenModify(int* pixels, int w, int h, int* R_Table, int* G_Table,
		int* B_Table, int times);

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

#endif
