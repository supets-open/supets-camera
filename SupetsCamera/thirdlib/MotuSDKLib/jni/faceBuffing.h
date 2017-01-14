#ifndef FACEBUFFING_H
#define FACEBUFFING_H

#include <stdio.h>
#include <stdlib.h>

#define intsize sizeof(int)
#define complexsize sizeof(ccomplex)
#define PI 3.1415926

/**
 * init_nLen and init_mLen  means the initial height and width of the image
 * nLen and mLen means the new height and width which are the pow(2,N), pow(2,M)
 * ccomplex simulates the calculation of plural
 * */
int nLen, initNLen, mLen, initMLen, N, M;
int stepM, stepN;

typedef struct {
	float real;
	float image;
} ccomplex;

/**
 * aM,aN,bM,bN the transformation matrix during the fft
 * Table pre-calculate the  factor during the fft
 * green means template calculated by the fft , utilized to modify the original image
 * A and A_In means the result calculated during the fft process
 */
int *aM, *aN, *bM, *bN;
float *cosTableN;
float *sinTableN;
float *cosTableM;
float *sinTableM;
int *green;

ccomplex *A, *AIn;

void buffingTemplate(int* srcPixArray, int w, int h, int bb, int flag);
void faceBuffing(int *srcPixArray, int w, int h, int *R_Table, int *G_Table,
		int *B_Table, int weight);
void faceBuffingBackup(int *srcPixArray, int w, int h, int *R_Table, int *G_Table,
		int *B_Table, int weight);
void releaseSource();
extern void HighPassInit(int *arr, int w, int h, double D0);
extern void HighPass(const double D0, int w, int h);
extern void HighLight3(int *arr, int w, int h);
extern void HighLight(int *arr, int w, int h);
extern void readData(int *arr, int h, int w);
extern ccomplex Add(ccomplex, ccomplex);
extern ccomplex Sub(ccomplex, ccomplex);
extern ccomplex Mul(ccomplex, ccomplex);
extern int calculate_M(int);
extern void fft(int, int, int);
extern void Ifft();

#endif
