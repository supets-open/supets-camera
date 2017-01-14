#include "faceBuffing.h"
#include "mtprocessor.h"
#include "operation.h"
#include <math.h>
#include <time.h>
#include <android/log.h>

#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)

int lutHighLight3[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 6, 6, 7,
		8, 9, 9, 10, 11, 12, 13, 14, 15, 17, 18, 20, 21, 23, 25, 27, 29, 32, 34,
		37, 40, 43, 45, 48, 52, 57, 59, 64, 69, 75, 79, 86, 91, 98, 105, 112,
		120, 128, 136, 143, 151, 156, 163, 170, 174, 178, 184, 188, 192, 198,
		202, 203, 208, 211, 215, 216, 219, 222, 223, 226, 229, 230, 232, 233,
		235, 237, 238, 239, 241, 242, 242, 244, 244, 245, 246, 246, 247, 248,
		248, 249, 250, 250, 250, 251, 251, 251, 252, 252, 252, 252, 252, 253,
		253, 253, 253, 253, 254, 254, 254, 254, 254, 254, 254, 254, 254, 254,
		254, 254, 254, 254, 254, 254, 254, 254, 254, 254, 254, 254, 254, 254,
		254, 254, 254, 254, 254, 254, 254, 254, 254, 254, 254, 254, 254, 254,
		254, 254, 254, 254, 254, 254, 254, 254, 254, 254, 254, 254, 254, 254,
		254, 254, 254, 254, 254, 254, 254, 254, 254, 254, 254, 254, 254, 254,
		254, 254, 255 };

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

/**	use LUT to accelerate 3 times:
 * 	if (x <= 128):
 y = int(2 * x * x / 256)
 else:
 y = int(255 - (255 - 2 * (x - 128)) * (255 - x) / 256)
 */
/*
 * HighLight3 Uses LUT Method to modify the pixel value
 */
void HighLight3(int *arr, int w, int h) {
	int i;
	for (i = 0; i != w * h; ++i)
		arr[i] = lutHighLight3[arr[i]];
}
/**
 * HighPass high pass method on the Frequency Area of the Image
 */
void HighLight(int *arr, int w, int h)
{
	int i;
	for(i = 0; i != w * h; ++i)
		if(arr[i] <= 128)
			arr[i] = 2 * arr[i] * arr[i] / 256;
		else
			arr[i] = 255 - (255 - 2 * (arr[i] - 127)) * (255 - arr[i])/256;
			//arr[i] = 255 - (2 * (255 - arr[i]) * (255 - arr[i]))/256;

			//arr[i] = 255 - (255 - 2 * (arr[i] - 127)) * (255 - arr[i])/256;
}
void HighPass(const double D0, int w, int h) {
	int i, j;
	int state = -1;
	double tempD;
	long width, height;
	width = nLen;
	height = mLen;
	long x, y;
	x = width / 2;
	y = height / 2;
	for (i = 0; i < height; ++i) {
		for (j = 0; j < width; ++j) {
			if (i > y && j > x)
				state = 3;
			else if (i > y)
				state = 1;
			else if (j > x)
				state = 2;
			else
				state = 0;

			switch (state) {
			case 0:
				tempD = (double) (i * i + j * j);
				tempD = sqrt(tempD);
				break;
			case 1:
				tempD = (double) ((height - i) * (height - i) + j * j);
				tempD = sqrt(tempD);
				break;
			case 2:
				tempD = (double) (i * i + (width - j) * (width - j));
				tempD = sqrt(tempD);
				break;
			case 3:
				tempD = (double) ((height - i) * (height - i)
						+ (width - j) * (width - j));
				tempD = sqrt(tempD);
				break;
			default:
				break;

			}
			int p = i * nLen + j;
			tempD = 1 - exp(-0.5 * pow(tempD / D0, 2));
			AIn[p].real = AIn[p].real * tempD;
			AIn[p].image = AIn[p].image * tempD;
		}
	}
}

void HighPassInit(int *arr, int w, int h, double D0) {
	readData(arr, h, w);
	int i, j;
	A = (ccomplex *) malloc(complexsize* nLen);
	for (i = 0; i < mLen; i++) {
		for (j = 0; j < nLen; j++) {
			A[j].real = AIn[i * nLen + bN[j]].real;
			A[j].image = AIn[i * nLen + bN[j]].image;
		}
		fft(nLen, N, 0);
		for (j = 0; j < nLen; j++) {
			AIn[i * nLen + j].real = A[j].real;
			AIn[i * nLen + j].image = A[j].image;
		}
	}

	free(A);

	A = (ccomplex *) malloc(complexsize* mLen);
	for (i = 0; i < nLen; i++) {
		for (j = 0; j < mLen; j++) {
			A[j].real = AIn[bM[j] * nLen + i].real;
			A[j].image = AIn[bM[j] * nLen + i].image;
		}

		fft(mLen, M, 1);
		for (j = 0; j < mLen; j++) {
			AIn[j * nLen + i].real = A[j].real;
			AIn[j * nLen + i].image = A[j].image;
		}
	}
	free(A);
	HighPass(D0, w, h);
	Ifft();
	double minValue = INT_MAX;
	double maxValue = -INT_MAX;
	for (i = 0; i < h; ++i) {
		for (j = 0; j < w; ++j) {
			arr[i * w + j] = AIn[(i+stepM) * nLen + (j + stepN)].real;
			if (arr[i * w + j] < minValue)
				minValue = arr[i * w + j];
			if (arr[i * w + j] > maxValue)
				maxValue = arr[i * w + j];
		}

	}
	float AverageValue = 0;
	for (i = 0; i < w * h; ++i) {
		arr[i] = arr[i] * 255.0 / (maxValue - minValue)
				- 255.0 * minValue / (maxValue - minValue);
//		arr[i] = arr[i]/2 + 64;
		AverageValue += arr[i];
	}
	AverageValue  /= (w * h);
	for(i = 0; i < w * h; ++i){
		arr[i] = arr[i] * (255.0 - 138 + AverageValue)/(255.0) ;
		arr[i] += (138.0 - AverageValue);
//		arr[i] = arr[i] + (arr[i] - 50) * (1 / (1 - 50/255) -  1);
		if(arr[i] < 0)
			arr[i] = 0;
		if(arr[i] > 255)
			arr[i] = 255;
		//newRGB = RGB + (RGB - Threshold) * (1 / (1 - Contrast / 255) - 1)
	}

	HighLight3(arr, w, h);
}

/**
 * faceBuffing Uses the template calculated by buffingTemplate to do the face buffing
 * weight 0 ~ 100
 */
int ConTable[256] = { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 6, 7, 7, 8, 8, 9, 9,
		10, 10, 11, 11, 12, 12, 13, 13, 14, 15, 15, 16, 16, 17, 17, 18, 19, 19,
		20, 20, 21, 22, 22, 23, 23, 24, 25, 25, 26, 27, 28, 28, 29, 30, 30, 31,
		32, 33, 34, 34, 35, 36, 37, 38, 39, 39, 40, 41, 42, 43, 44, 45, 46, 47,
		48, 49, 50, 51, 52, 53, 54, 55, 56, 58, 59, 60, 61, 62, 63, 65, 66, 67,
		68, 70, 71, 72, 73, 75, 76, 77, 79, 80, 81, 83, 84, 86, 87, 88, 90, 91,
		93, 94, 95, 97, 98, 100, 101, 103, 104, 106, 107, 109, 110, 112, 113,
		115, 116, 118, 119, 121, 122, 124, 125, 127, 128, 130, 131, 133, 134,
		136, 138, 139, 141, 142, 144, 145, 147, 148, 150, 151, 153, 155, 156,
		158, 159, 161, 162, 164, 165, 167, 168, 170, 171, 173, 174, 176, 177,
		178, 180, 181, 183, 184, 185, 187, 188, 189, 191, 192, 193, 194, 196,
		197, 198, 199, 200, 201, 203, 204, 205, 206, 207, 208, 209, 210, 211,
		212, 213, 214, 215, 216, 217, 218, 219, 219, 220, 221, 222, 223, 224,
		225, 225, 226, 227, 228, 229, 229, 230, 231, 232, 232, 233, 234, 235,
		235, 236, 237, 237, 238, 239, 239, 240, 241, 241, 242, 243, 243, 244,
		245, 245, 246, 246, 247, 248, 248, 249, 250, 250, 251, 251, 252, 253,
		253, 254, 254, 255};

void faceBuffingBackup(int *srcPixArray, int w, int h, int *R_Table, int *G_Table,
		int *B_Table, int weight) {
	float nValue;
	int i, c, v;
	for (i = 0; i != w * h; ++i) {
		c = getR(srcPixArray[i]);
		nValue = R_Table[c];
		v = (c * green[i] + nValue * (255 - green[i]))
								/ 255;
		setR(&srcPixArray[i], v);

		c = getG(srcPixArray[i]);
		nValue = G_Table[c];
		v = (getG(srcPixArray[i]) * green[i] + nValue * (255 - green[i]))
								/ 255;
		setG(&srcPixArray[i], v);
		c = getB(srcPixArray[i]);
		nValue = B_Table[c];
		v = (getB(srcPixArray[i]) * green[i] + nValue * (255 - green[i]))
								/ 255;
		setB(&srcPixArray[i], v);
	}
}

void faceBuffing(int *srcPixArray, int w, int h, int *R_Table, int *G_Table,
		int *B_Table, int weight) {
	float nValue;
	int i, c, v;
	for (i = 0; i != w * h; ++i) {
		c = getR(srcPixArray[i]);
		nValue = R_Table[c];
		v = (c * green[i] + nValue * (255 - green[i]))
								/ 255;
		setR(&srcPixArray[i], v);

		c = getG(srcPixArray[i]);
		nValue = G_Table[c];
		v = (getG(srcPixArray[i]) * green[i] + nValue * (255 - green[i]))
								/ 255;
		setG(&srcPixArray[i], v);
		c = getB(srcPixArray[i]);
		nValue = B_Table[c];
		v = (getB(srcPixArray[i]) * green[i] + nValue * (255 - green[i]))
								/ 255;
		setB(&srcPixArray[i], v);
	}

}


void preProcessImage(int *srcPixArray, int w, int h)
{
	//对原图做下预处理
	int *tmpPixArray = (int *)malloc(sizeof(int) * w * h);
	memcpy(tmpPixArray, srcPixArray, sizeof(int) * w * h);

	brightEffect(tmpPixArray, w, h, 60);
	int tmpRadius = 13;
//	fastAverageBlurWithThreshold(tmpPixArray, w, h, tmpRadius, 7);

	int ii;
	for(ii = 0; ii != w * h; ++ii)
	{
		setR(&srcPixArray[ii], getR(srcPixArray[ii]) * 0.2 + getR(tmpPixArray[ii]) * 0.8);
		setG(&srcPixArray[ii], getG(srcPixArray[ii]) * 0.2 + getG(tmpPixArray[ii]) * 0.8);
		setB(&srcPixArray[ii], getB(srcPixArray[ii]) * 0.2 + getB(tmpPixArray[ii]) * 0.8);
	}

	free(tmpPixArray);
	//////////
}
void buffingTemplate(int* srcPixArray, int w, int h, int bb, int flag) {
//	if(flag == 1)
//	{
//		preProcessImage(srcPixArray, w, h);
//	}
	int i, j, k, index, col, row;
	float tmp;
	green = (int *) malloc(sizeof(int) * w * h);
	int *backPixArray = (int*)malloc(sizeof(int) * w * h);
	if (flag) {
//the entrance of Gauss Filter Face Buffing Method
//		float *tmpGreen = (float *)malloc(sizeof(float) * w * h);
//		for(i = 0; i != w * h; ++i)
//		{
//			green[i] = getG(srcPixArray[i]);
//			tmpGreen[i] = green[i];
//		}
//
//		GaussSmoothMethod(tmpGreen, w, h, 9);
//
//		float minVa = 999999, maxVa = -999999;
//		for (i = 0; i != w * h; ++i) {
//			tmpGreen[i] = getG(srcPixArray[i]) - tmpGreen[i];
//			if (tmpGreen[i] > maxVa)
//				maxVa = tmpGreen[i];
//			if (tmpGreen[i] < minVa)
//				minVa = tmpGreen[i];
//		}
//
//		for (i = 0; i != w * h; ++i) {
//			tmpGreen[i] = (tmpGreen[i] - minVa) / (maxVa - minVa) * 127 + 128;
//			if(tmpGreen[i] > 255)
//				tmpGreen[i] = 255;
//			green[i] = (int)(tmpGreen[i]);
//			LOGW("The Value of Green %d", green[i]);
//		}
//		free(tmpGreen);
		for (i = 0; i != h * w; ++i) {
			green[i] = srcPixArray[i];
			setR(&green[i], 0);
			setB(&green[i], 0);
		}

		fastAverageBlur(green, w, h, 7);
		fastAverageBlur(green, w, h, 7);
		fastAverageBlur(green, w, h, 7);

		double minValue = INT_MAX;
		double maxValue = -INT_MAX;
		for (i = 0; i < h * w; ++i) {
			green[i] = getG(green[i]);
			green[i] = (getG(srcPixArray[i]) - green[i]) / 2;
			green[i] += 128.0;
			if (green[i] < 0) {
				green[i] = 0;
			}
			if (green[i] > 255) {
				green[i] = 255;
			}
		}
		HighLight3(green, w, h);

	} else {   //the entrance of FFT Face Buffing Method
		for (i = 0; i != w * h; ++i) {
			green[i] = getG(srcPixArray[i]);
		}
		HighPassInit(green, w, h, bb);
		for (i = 0; i != w * h; ++i) {
			if (green[i] > 255) {
				green[i] = 255;
			}
		}

		free(cosTableM);
		free(sinTableM);
		free(cosTableN);
		free(sinTableN);
		free(aN);
		free(bN);
		free(aM);
		free(bM);
		free(AIn);
	}

//	free(tmpsrcPixArray);
}

void releaseSource() {
	free(green);
}
/**
 *  readData  Initialize the data matrix with the input srcPixelArray
 */
void readData(int *arr, int h, int w) {
	int i, j;

	initMLen = h;
	initNLen = w;
	M = calculate_M(h);
	N = calculate_M(w);
	nLen = 1 << N;
	mLen = 1 << M;
	stepM = (mLen - initMLen)/2;
	stepN = (nLen - initNLen)/2;
	AIn = (ccomplex *) malloc(complexsize* nLen * mLen);

	sinTableN = (float *) malloc(sizeof(float) * nLen / 2);
	cosTableN = (float *) malloc(sizeof(float) * nLen / 2);
	sinTableM = (float *) malloc(sizeof(float) * mLen / 2);
	cosTableM = (float *) malloc(sizeof(float) * mLen / 2);

	for (i = 0; i < nLen / 2; i++) {
		cosTableN[i] = cos(2 * PI * i / nLen);
		sinTableN[i] = sin(2 * PI * i / nLen);
	}

	for (i = 0; i < mLen / 2; i++) {
		cosTableM[i] = cos(2 * PI * i / mLen);
		sinTableM[i] = sin(2 * PI * i / mLen);
	}

	aN = (int *) malloc(intsize* N);
	bN = (int *) malloc(intsize* nLen);

	aM = (int *) malloc(intsize* M);
	bM = (int *) malloc(intsize* mLen);
//////////////////////////////////////Initialize the factor matrix including aN, aM, bN, bM//////////////////
	for (i = 0; i < N; i++) {
		aN[i] = 0;
	}

	bN[0] = 0;
	for (i = 1; i < nLen; i++) {
		j = 0;
		while (aN[j] != 0) {
			aN[j] = 0;
			j++;
		}

		aN[j] = 1;
		bN[i] = 0;
		for (j = 0; j < N; j++) {
			bN[i] = bN[i] + aN[j] * (1 << (N - 1 - j));
		}
	}
	//////////////////////////////////////
	for (i = 0; i < M; i++) {
		aM[i] = 0;
	}

	bM[0] = 0;
	for (i = 1; i < mLen; i++) {
		j = 0;
		while (aM[j] != 0) {
			aM[j] = 0;
			j++;
		}

		aM[j] = 1;
		bM[i] = 0;
		for (j = 0; j < M; j++) {
			bM[i] = bM[i] + aM[j] * (1 << (M - 1 - j));
		}
	}

/////////////////////////////////
	for(i = 0; i != mLen; ++i)
		for(j = 0; j != nLen; ++j)
		{
			AIn[i*nLen+j].real = 0.0;
			AIn[i*nLen+j].image = 0.0;
		}
	for(i = 0; i != initMLen; ++i)
		for(j = 0; j != initNLen; ++j)
		{
			AIn[(stepM + i) * nLen + (stepN + j)].real = arr[i * initNLen + j];
			AIn[(stepM + i) * nLen + (stepN + j)].image = 0.0;
		}

	int realstep = 10 /*min(stepM, stepN) / 3*/;
	for(i = stepM; i != initMLen + stepM; ++i)
		for(j = stepN - realstep; j != stepN; ++j)
		{
			if(j < 0)
				continue;
			AIn[i * nLen + j].real = AIn[i * nLen + 2 * stepN - j].real;
		}
	for(i = stepM; i != initMLen + stepM; ++i)
		for(j = stepN + initNLen; j != stepN + initNLen + realstep; ++j)
		{
			if(j >= nLen)
				break;
			AIn[i * nLen + j].real = AIn[i * nLen + 2 * (initNLen + stepN) - j].real;
		}
	for(i = stepM - realstep; i != stepM; ++i)
	{
		if(i < 0)
			continue;
		for(j = stepN; j != stepN + initNLen; ++j)
		{
			AIn[i * nLen + j].real = AIn[ nLen * (2 * stepM - i) + j].real;
		}
	}
	for(i = stepM + initMLen; i != stepM + initMLen + realstep; ++i)
	{
		if(i >= mLen)
			break;
		for(j = stepN; j != stepN + initNLen; ++j)
		{
			AIn[i * nLen + j].real = AIn[ nLen * (2 * (initMLen + stepM) - i) + j].real;
		}
	}
}

void fft(int fft_nLen, int fft_M, int ff) {
	int i;
	int lev, dist, p, t, distA;

	ccomplex B, T, W;

	for (lev = 1; lev <= fft_M; lev++) {
		dist = (1 << (lev - 1));
		distA = 1 << lev;
		for (t = 0; t < dist; t++) {

			p = t * (1 << (fft_M - lev));
			if (ff == 0) {
				W.real = cosTableN[p];
				W.image = -sinTableN[p];
			} else {
				W.real = cosTableM[p];
				W.image = -sinTableM[p];
			}
			for (i = t; i < fft_nLen; i += distA) {
				T = Mul(A[i + dist], W);
				B = Add(A[i], T);
				A[i + dist] = Sub(A[i], T);
				A[i].real = B.real;
				A[i].image = B.image;
			}
		}
	}
}

int calculate_M(int len) {
	int i;
	int k;

	i = 0;
	k = 1;
	while (k < len) {
		k = k << 1;
		i++;
	}

	return i;
}

inline ccomplex Add(ccomplex c1, ccomplex c2) {
	ccomplex c;
	c.real = c1.real + c2.real;
	c.image = c1.image + c2.image;
	return c;
}

inline ccomplex Sub(ccomplex c1, ccomplex c2) {
	ccomplex c;
	c.real = c1.real - c2.real;
	c.image = c1.image - c2.image;
	return c;
}

inline ccomplex Mul(ccomplex c1, ccomplex c2) {
	ccomplex c;
	c.real = c1.real * c2.real - c1.image * c2.image;
	c.image = c1.real * c2.image + c2.real * c1.image;
	return c;
}

void Ifft() {
	int i, j;

	for (i = 0; i < mLen; i++) {
		for (j = 0; j < nLen; j++) {
			AIn[i * nLen + j].image = -AIn[i * nLen + j].image;
		}
	}

	A = (ccomplex *) malloc(complexsize* nLen);
	for (i = 0; i < mLen; i++) {
		for (j = 0; j < nLen; j++) {
			A[j].real = AIn[i * nLen + bN[j]].real;
			A[j].image = AIn[i * nLen + bN[j]].image;
		}

		fft(nLen, N, 0);
		for (j = 0; j < nLen; j++) {
			AIn[i * nLen + j].real = A[j].real / nLen;
			AIn[i * nLen + j].image = A[j].image / nLen;
		}
	}
	free(A);

	A = (ccomplex *) malloc(complexsize* mLen);
	for (i = 0; i < nLen; i++) {
		for (j = 0; j < mLen; j++) {
			A[j].real = AIn[bM[j] * nLen + i].real;
			A[j].image = AIn[bM[j] * nLen + i].image;
		}

		fft(mLen, M, 1);
		for (j = 0; j < mLen; j++) {
			AIn[j * nLen + i].real = A[j].real / mLen;
			AIn[j * nLen + i].image = A[j].image / mLen;
		}
	}
	free(A);
}
