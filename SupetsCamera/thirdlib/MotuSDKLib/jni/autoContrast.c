#include "autoContrast.h"

void getRGBhist(int * img, int size, int * histR, int * histG, int * histB)
{
	int i;
	int R,G,B;
	memset(histR,0,256*sizeof(int));
	memset(histG,0,256*sizeof(int));
	memset(histB,0,256*sizeof(int));

	for (i = 0;i<size;i++)
	{
		getRGB(img[i],&R,&G,&B);
		histR[R]++;
		histG[G]++;
		histB[B]++;
	}
}
void getHist_Min_Max(int * hist, int thre_low, int thre_high, int * gray_min, int * gray_max)
{
	int i ;
	int sum;
	sum = 0;
	for (i = 0;i<255;i++)
	{
		sum += hist[i];
		if (sum>=thre_low)
		{
			*gray_min = i;
			break;
		}
	}

	sum = 0;
	for (i = 255;i>=0;i--)
	{
		sum += hist[i];
		if (sum>=thre_high)
		{
			*gray_max = i;
			break;
		}
	}
}

void contrastMap(int * map, int min , int max)
{
	int i;
	for (i = 0;i<=min;i++)
	{
		map[i] = 0;
	}
	for (i = max; i<256;i++)
	{
		map[i] = 255;
	}
	for (i = min; i <max ; i++)
	{
		map[i] = (i - min)* 255 / (max - min) ;
	}
}
void ImageMap(int * img, int size, int * map)
{
	int i ;
	int R,G,B;
	for (i = 0;i<size; i++)
	{
		getRGB(img[i],&R,&G,&B);
		R = map[R];
		G = map[G];
		B = map[B];
		setRGB(&img[i],R,G,B);
	}
}
void autoContrast(int * img, int w, int h, float thre_low, float thre_high)
{
	int histR[256];
	int histG[256];
	int histB[256];
	int size = w*h;
	int i,j;

	int low  = (int)((float)size * thre_low);
	int high = (int)((float)size * thre_high);

	int min[3];
	int max[3];
    getRGBhist(img, size, histR, histG, histB);


    getHist_Min_Max(histR, low, high, &min[0],  &max[0]);
	getHist_Min_Max(histG, low, high, &min[1],  &max[1]);
	getHist_Min_Max(histB, low, high, &min[2],  &max[2]);

	min[0] = getMIN(min[0],min[1]);
	min[0] = getMIN(min[0],min[2]);

	max[0] = getMAX(max[0],max[1]);
	max[0] = getMAX(max[0],max[2]);

	if(max[0]>min[0])
	{
		contrastMap(histB,min[0],max[0]);
		ImageMap(img,size,histB);
	}
}

