#include "whiteBalance.h"
void GetWhiteBalancePara2(int * img, int w, int h, int * para)
{
	memset(para,0,256*3*sizeof(int));
	int smooth_size = 1;
	int hist[256][4] = {0};//r _sum,g_sum,b_sum,count
	int size = w*h;
	int i,j,k,count,start,end,id;
	int R,G,B,gray,mean;
	for(i = 0; i<size; i++)
	{
		getRGB(img[i],&R,&G,&B);
		gray = (R+G+B)/3;
		hist[gray][0] += R;
		hist[gray][1] += G;
		hist[gray][2] += B;
		hist[gray][3] ++;
	}

	for (i = 0; i<256;i++)
	{
		start = getMIN(i,smooth_size) * -1;
		end   = getMIN(256-i,smooth_size);

		count = 0;
		for (j = start; j<end;j++)
		{
			for (k = 0;k<3;k++)
			{
				para[i*3 + k] += hist[i+j][k];
			}
			count += hist[i+j][3];
		}
		if (count > 0)
		{
			for (k = 0;k<3;k++)
			{
				para[i*3 + k] /= count;
			}
		}

	}

	for (i = 0; i<256;i++)
	{
		id = i*3;

		mean = ((para[id + 0] + para[id + 1] + para[id + 2]))/3;
		if (para[id + 0]>0) para[id + 0] = mean-para[id + 0];
		if (para[id + 1]>0) para[id + 1] = mean-para[id + 1];
		if (para[id + 2]>0) para[id + 2] = mean-para[id + 2];
	}
}
void ImgWhiteBalance1(int * img, int w, int h, int * para)
{
	//	LOGW("WhiteBalance para, (%d, %d, %d)", para[0],para[1],para[2]);
	int size = w*h;
	int i;
	int R,G,B,gray;

	for(i = 0; i<size; i++)
	{
		getRGB(img[i],&R,&G,&B);
		gray = (R+G+B)/3*3;

		R = (R + para[gray+0]);
		G = (G + para[gray+1]);
		B = (B + para[gray+2]);

		R = getMIN(R,255);
		G = getMIN(G,255);
		B = getMIN(B,255);
		R = getMAX(R,0);
		G = getMAX(G,0);
		B = getMAX(B,0);
		setRGB(&img[i],R,G,B);

 	}
}
void whiteBalance(int * img, int w, int h)
{
	LOGW("11.14");
	int para[256*3]={0};

	GetWhiteBalancePara2(img,w,h,para);
	ImgWhiteBalance1(img, w, h, para);

}
