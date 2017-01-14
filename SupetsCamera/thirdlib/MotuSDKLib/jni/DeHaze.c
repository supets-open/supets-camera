#include "mtprocessor.h"
#include "DeHaze.h"
#include <android/log.h>
#include "operation.h"
#include "MSRCR.h"

#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)
#define min(x,y) (((x)>(y))?(y):(x))
#define max(x,y) (((x)>(y))?(x):(y))

float rat = 0.95;
float ratE = 0.950;
float ratL = 1.250;

void getMinRGB(int *srcPixArray, int *minRGB, int w, int h) {
	int i, j, tmp;
	for (i = 0; i != h; ++i) {
		for (j = 0; j != w; ++j) {
			tmp =
					min(getR(srcPixArray[i * w + j]), getG(srcPixArray[i * w + j]));
			tmp = min(tmp, getB(srcPixArray[i * w + j]));
			minRGB[i * w + j] = tmp;
		}
	}
}

void getDarkCh(int *minRGB, int *darkCh, int w, int h, int len) {
	LOGW("len Value: %d", len);
	LOGW("w Value: %d", w);
	LOGW("h Value: %d", h);
	int *p = (int *) malloc(sizeof(int) * (max(w,h) + len * 2));
	int *q = (int *) malloc(sizeof(int) * (max(w,h) + len * 2));
	int i, j, s, t, tmp;
	for (i = 0; i != w; ++i) {
		s = 0;
		t = 0;
		for (j = 0; j != h + len; ++j) {
			if (j < h) {
				tmp = minRGB[j * w + i];
				while (t > s && q[t - 1] >= tmp) {
					t--;
				}
				q[t] = tmp;
				p[t++] = j;
			}
			if (j >= len) {
				darkCh[(j - len) * w + i] = q[s];
			}
			if (p[s] + len * 2 == j) {
				s++;
			}
		}
	}
	LOGW("firstOne Pass: %d", 1);
	for (j = 0; j != h; ++j) {
		s = 0;
		t = 0;
		for (i = 0; i != w + len; ++i) {
			if (i < w) {
				tmp = darkCh[j * w + i];
				while (t > s && q[t - 1] >= tmp) {
					t--;
				}
				if (i >= len) {
					darkCh[j * w + i - len] = q[s];
				}
				if (p[s] + len * 2 == i) {
					s++;
				}
			}
		}
	}
	LOGW("secondOne Pass: %d", 2);
	for (i = 0; i != w; ++i) {
		for (j = 0; j != h; ++j) {
			if (darkCh[j * w + i] > 255)
				darkCh[j * w + i] = min(255, ratE * darkCh[j * w + i]);
		}
	}
	LOGW("final Pass: %d", 3);

	free(p);
	LOGW("p Free: %d", 4);

	free(q);
	LOGW("q Free: %d", 5);
}

void getAtmosphere(int *srcPixArray, int *darkCh, int *atm, int w, int h,
		int len) {
	int i, j, k;
	int tot = 0;
	int best = 0;
	for (i = 0; i != w; ++i) {
		for (j = 0; j != h; ++j) {
			if (i < len * 5 || i + len * 5 >= w || j < len * 5
					|| j + len * 5 >= h) {
				if (darkCh[j * w + i] > best)
					best = darkCh[j * w + i];
			}
		}
	}

	for (i = 0; i != w; ++i) {
		for (j = 0; j != h; ++j) {
			if (i < len * 5 || i + len * 5 >= w || j < len * 5
					|| j + len * 5 >= h) {
				if (darkCh[j * w + i] > best - 10) {
					atm[0] += getR(srcPixArray[j * w + i]);
					atm[1] += getG(srcPixArray[j * w + i]);
					atm[2] += getB(srcPixArray[j * w + i]);
					tot++;
				}
			}
		}
	}

	for (k = 0; k != 3; ++k) {
		atm[k] /= tot;
	}
}

void hazeRemove(int *srcPixArray, float *x1, int w, int h, int *atm) {
	int i, j;
	float tmp;
	int tmpValue;
	for (i = 0; i != w; ++i) {
		for (j = 0; j != h; ++j) {

			tmp = min(0.98, x1[j * w + i]);
//			LOGW("tmpValue: %f", tmp);
			tmpValue =
					0.4
							+ min(255.0, max(0, ratL * (getR(srcPixArray[j * w + i]) - tmp * atm[0]) / (1.00 - tmp)));
			setR(&srcPixArray[j * w + i], min(tmpValue, 255));
			tmpValue =
					0.4
							+ min(255.0, max(0, ratL * (getG(srcPixArray[j * w + i]) - tmp * atm[1]) / (1.00 - tmp)));
			setG(&srcPixArray[j * w + i], min(tmpValue, 255));
			tmpValue =
					0.4
							+ min(255.0, max(0, ratL * (getB(srcPixArray[j * w + i]) - tmp * atm[2]) / (1.00 - tmp)));
			setB(&srcPixArray[j * w + i], min(tmpValue, 255));
		}
	}
}

void deHaze(int *srcPixArray, int w, int h, int level, float Rat, float RatE,
		float RatL) {
	LOGW("deHaze start: %f", 1.0);
	int i, j, k;
	int len = (int) (min(w,h) * 1.0 / 200.0 + 0.50);
	LOGW("len Value: %d", len);
	int *minRGB = (int *) malloc(sizeof(int) * w * h);
	LOGW("getMinRGB start: %f", 2.0);
	getMinRGB(srcPixArray, minRGB, w, h);
	int *darkCh = (int *) malloc(sizeof(int) * w * h);
	LOGW("getDarkCh start: %f", 3.0);
	getDarkCh(minRGB, darkCh, w, h, len);

	int atm[3];
	memset(atm, 0, sizeof(int) * 3);
	LOGW("getAtmosphere start: %f", 4.0);
	getAtmosphere(srcPixArray, darkCh, atm, w, h, len);
	LOGW("getAtmosphere start: %f", 4.1);
	for (i = 0; i != 3; ++i)
		atm[i] = (int) (atm[i] * rat + 0.50);
	float *x1 = (float *) malloc(sizeof(float) * w * h);
	for (i = 0; i != h; ++i) {
		for (j = 0; j != w; ++j) {
			x1[i * w + j] = minRGB[i * w + j] * 1.0 / 255.0;
		}
	}
	LOGW("hazeRemove start: %f", 5.0);
	hazeRemove(srcPixArray, x1, w, h, atm);

//	if(x1)
//		free(x1);
//	if(minRGB)
//		free(minRGB);
//	if(darkCh)
//		free(darkCh);
	free(x1);
	free(minRGB);
	free(darkCh);
}

//////////////////////////////////////////////////////////
//#############################################################

void highPassForEachChannel(int *arr, int w, int h) {
//	int *oriArr = (int *) malloc(sizeof(int) * w * h);
//	memcpy(oriArr, arr, sizeof(int) * w * h);
//	fastAverageBlur(arr, w, h, 29);
//	fastAverageBlur(arr, w, h, 11);
//	fastAverageBlur(arr, w, h, 11);

//	int i;
//	for (i = 0; i < h * w; ++i) {
//		arr[i] = (oriArr[i] - arr[i]) / 2;
//		arr[i] += 128.0;
//		if (arr[i] < 0) {
//			arr[i] = 0;
//		}
//		if (arr[i] > 255) {
//			arr[i] = 255;
//		}
//	}

	float *tmpData = (float *) malloc(sizeof(float) * w * h);
	int i, j;
	int sigma = 20;
	for (j = 0; j != 3; ++j) {
//		switch (j) {
//		case 0:
//			setColor = setR;
//			getColor = getR;
//			break;
//		case 1:
//			setColor = setG;
//			getColor = getG;
//			break;
//		case 2:
//			setColor = setB;
//			getColor = getB;
//			break;
//		default:
//			break;
//		}

		for (i = 0; i != w * h; ++i) {
			tmpData[i] = (arr[i] >> (8 * (2 - j)) & 0xFF) ;
		}

		GaussSmoothMethod(tmpData, w, h, sigma);

		float minVa = 999999, maxVa = -999999;
		for (i = 0; i != w * h; ++i) {

			tmpData[i] = (arr[i] >> (8 * (2 - j)) & 0xFF) - tmpData[i];

			if (tmpData[i] > maxVa)
				maxVa = tmpData[i];
			if (tmpData[i] < minVa)
				minVa = tmpData[i];
		}

		for (i = 0; i != w * h; ++i) {
			tmpData[i] = (tmpData[i] - minVa) / (maxVa - minVa) * 255;
		}

		for (i = 0; i != w * h; ++i)
		{
			if(j == 0)
				setR(&arr[i], (int) (tmpData[i]));
			if(j == 1)
				setG(&arr[i], (int) (tmpData[i]));
			if(j == 2)
				setB(&arr[i], (int) (tmpData[i]));
		}
	}

//	free(oriArr);
}

//void sceneHighlight(int *srcPixArray, int *mModel, int w, int h, int sig) {
//	int i;
//	int tmpValue;
//	for (i = 0; i != w * h; ++i) {
//		//依靠原来的基色判断是用哪个公式做处理，关键是要做3次高反差？ 再拼合？
//		switch (sig) {
//		case 0:
//			setColor = setR;
//			getColor = getR;
//			break;
//		case 1:
//			setColor = setG;
//			getColor = getG;
//			break;
//		case 2:
//			setColor = setB;
//			getColor = getB;
//			break;
//		default:
//			break;
//		}
//
////			tmpValue = mModel[i];
//
//		if ((*getColor)(srcPixArray[i]) <= 127) {
//			tmpValue = (*getColor)(srcPixArray[i]) * mModel[i] * 2.0 / 255.0;
//			(*setColor)(&srcPixArray[i], max(min(tmpValue, 255), 0));
//		} else {
//			tmpValue = 255
//					- (255 - (*getColor)(srcPixArray[i])) * (255 - mModel[i])
//							* 2.0 / 255.0;
//			(*setColor)(&srcPixArray[i], max(min(tmpValue, 255), 0));
//		}
//
////			LOGW("the Value of tmpValue %d", tmpValue);
//	}
//}

void sceneHighlight(int *srcPixArray, int *mModel, int w, int h) {
	int i, j;
	int tmpValue;
	for (j = 0; j != 3; ++j) {
		for (i = 0; i != w * h; ++i) {
//			switch (j) {
//			case 0:
//				setColor = setR;
//				getColor = getR;
//				break;
//			case 1:
//				setColor = setG;
//				getColor = getG;
//				break;
//			case 2:
//				setColor = setB;
//				getColor = getB;
//				break;
//			default:
//				break;
//			}
			int tmpR = (srcPixArray[i] >> (8 * (2 - j)) & 0xFF);
			int tmpD = (mModel[i] >> (8 * (2 - j)) & 0xFF);
			if ( tmpR  <= 127 ) {
				tmpValue = tmpR * tmpD * 2.0 / 255.0;
			} else {
				tmpValue = 255 - (255 - tmpR) * (255 - tmpD) * 2.0 / 255.0;
			}

			if(j == 0)
				setR(&srcPixArray[i], max(min(tmpValue, 255), 0));
			if(j == 1)
				setG(&srcPixArray[i], max(min(tmpValue, 255), 0));
			if(j == 2)
				setB(&srcPixArray[i], max(min(tmpValue, 255), 0));

		}
	}

}
void sceneProcess(int *srcPixArray, int w, int h) {
	int *backup = (int *) malloc(sizeof(int) * w * h);
	memcpy(backup, srcPixArray, sizeof(int) * w * h);
	highPassForEachChannel(backup, w, h);
	sceneHighlight(srcPixArray, backup, w, h);
//	int i, j;
//	int *colorArr = (int *) malloc(sizeof(int) * w * h);
//	for (j = 0; j != 3; ++j) {
//		switch (j) {
//		case 0:
//			getColor = getR;
//			break;
//		case 1:
//			getColor = getG;
//			break;
//		case 2:
//			getColor = getB;
//			break;
//		}
//		for (i = 0; i != w * h; ++i) {
//			colorArr[i] = (*getColor)(srcPixArray[i]);
//		}
//
//		highPassForChannel(colorArr, w, h);
//
//		sceneHighlight(srcPixArray, colorArr, w, h, j);
//	}
//
//	free(colorArr);
}
/////////////////////////////////

//int LAB[256] = { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 6, 7, 7, 8, 8, 9, 9,
//		10, 10, 11, 11, 12, 12, 13, 13, 14, 15, 15, 16, 16, 17, 17, 18, 19, 19,
//		20, 20, 21, 22, 22, 23, 23, 24, 25, 26, 26, 27, 28, 28, 29, 30, 31, 31,
//		32, 33, 34, 34, 35, 36, 37, 38, 39, 40, 40, 41, 42, 43, 44, 45, 46, 47,
//		48, 49, 50, 51, 52, 53, 55, 56, 57, 58, 59, 60, 61, 63, 64, 65, 66, 68,
//		69, 70, 72, 73, 74, 76, 77, 79, 80, 82, 83, 85, 86, 88, 89, 91, 93, 94,
//		96, 98, 99, 101, 103, 105, 107, 108, 110, 112, 114, 116, 118, 120, 122,
//		124, 126, 128, 130, 132, 134, 137, 139, 141, 143, 145, 148, 150, 152,
//		154, 157, 159, 161, 163, 166, 168, 170, 172, 174, 177, 179, 181, 183,
//		185, 187, 189, 191, 193, 195, 197, 199, 201, 203, 204, 206, 208, 209,
//		211, 213, 214, 216, 217, 218, 220, 221, 223, 224, 225, 226, 227, 229,
//		230, 231, 232, 233, 234, 235, 236, 237, 238, 238, 239, 240, 241, 242,
//		242, 243, 244, 244, 245, 246, 246, 247, 247, 248, 248, 249, 249, 250,
//		250, 250, 251, 251, 251, 252, 252, 252, 253, 253, 253, 253, 254, 254,
//		254, 254, 254, 254, 254, 255, 255, 255, 255, 255, 255, 255, 255, 255,
//		255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
//		255, 255, 255, 255 };

void sceneEnhanceBackup(int *srcPixArray, int w, int h, int para1, int para2) {
	int i;
	int l, a, b;
	float realpara1 = para1 * 1.0 / 100.0;
	float realpara2 = para2 * 1.0 / 100.0;

	for (i = 0; i != w * h; ++i) {
		l = getR(srcPixArray[i]);
		a = getG(srcPixArray[i]);
		b = getB(srcPixArray[i]);

		transRgb2Lab(&l, &a, &b);
		l = 127;
		transLab2Rgb(&l, &a, &b);

		setR(&srcPixArray[i],
				getR(srcPixArray[i])
						* (1 - realpara1)+ realpara1 * min((int)(getR(srcPixArray[i]) * l * 1.0 / 255.0), 255));
		setG(&srcPixArray[i],
				getG(srcPixArray[i])
						* (1 - realpara1)+ realpara1 * min((int)(getG(srcPixArray[i]) * a * 1.0 / 255.0), 255));
		setB(&srcPixArray[i],
				getB(srcPixArray[i])
						* (1 - realpara1)+ realpara1 * min((int)(getB(srcPixArray[i]) * b * 1.0 / 255.0), 255));
	}

	int *sceneBackup = (int *) malloc(sizeof(int) * w * h);
	memcpy(sceneBackup, srcPixArray, sizeof(int) * w * h);

//	for(i = 0; i != 3; ++i)
//	{
//		sceneHighlight(srcPixArray, WhiteLayer, w, h, i);
//	}

	for (i = 0; i != w * h; ++i) {
		int c = getR(srcPixArray[i]);
		if (c <= 127)
			c *= 2;
		else
			c = 255;
		setR(&srcPixArray[i], c);

		c = getG(srcPixArray[i]);
		if (c <= 127)
			c *= 2;
		else
			c = 255;
		setG(&srcPixArray[i], c);

		c = getB(srcPixArray[i]);
		if (c <= 127)
			c *= 2;
		else
			c = 255;
		setB(&srcPixArray[i], c);
	}

	for (i = 0; i != w * h; ++i) {
		setR(&srcPixArray[i],
				getR(sceneBackup[i]) * (1 - realpara2)
						+ getR(srcPixArray[i]) * realpara2);
		setG(&srcPixArray[i],
				getG(sceneBackup[i]) * (1 - realpara2)
						+ getG(srcPixArray[i]) * realpara2);
		setB(&srcPixArray[i],
				getB(sceneBackup[i]) * (1 - realpara2)
						+ getB(srcPixArray[i]) * realpara2);
	}

	free(sceneBackup);
//	free(WhiteLayer);
}

void sceneEnhance(int *srcPixArray, int w, int h, int para1, int para2, int *LABa, int *LABb) {

	int i;
	int l, a, b, c;

	for (i = 0; i != w * h; ++i) {

		l = getR(srcPixArray[i]);
		a = getG(srcPixArray[i]);
		b = getB(srcPixArray[i]);

		transRgb2Lab(&l, &a, &b);
		if(l < 0)
			l = 0;
		if(l > 255)
			l = 255;

		if(a < 0)
			a = 0;
		if(a > 255)
			a = 255;

		if(b < 0)
			b = 0;
		if(b > 255)
			b = 255;

		a = LABa[a];
		b = LABb[b];

		transLab2Rgb(&l, &a, &b);

		if(l < 0)
			l = 0;
		if(l > 255)
			l = 255;

		if(a < 0)
			a = 0;
		if(a > 255)
			a = 255;

		if(b < 0)
			b = 0;
		if(b > 255)
			b = 255;

		setR(&srcPixArray[i], l);
		setG(&srcPixArray[i], a);
		setB(&srcPixArray[i], b);

	}
}

