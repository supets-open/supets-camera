#include "skin.h"
static inline int skindetect0(int R, int G, int B)
{
	/*if (R>95 && G>40 && B>20 && R>G && R>B && abs(R-G)>15)*/
	if (R>70 && G>20 && B>10 && R>G && R>B && abs(R-G)>7)
	{
		int Min = getMIN(getMIN(R,G),B);
		int Max = getMAX(getMAX(R,G),B);
		if ((Max - Min)>7)
		{
			return 1;
		}
		else return 0;
	}
	return 0;

}
static inline int skindetect1(int R, int G, int B)
{
	if ((R-G)<15) return 0;
	if (!((R>G)&&(G>B)))return 0;
	int sum = R+G+B;
	if (((156*R-52*sum)*(156*R-52*sum)+(156*G-52*sum)*(156*G-52*sum))<((sum * sum) >> 4))return 0;

	int T1 = 10000 * G* sum;
	int Lower = - 7760 * R * R + 5601 * R * sum + 1766 * sum * sum;
	if (T1<=Lower) return 0;
	int Upper = - 13767 * R * R + 10743 * R * sum + 1452 * sum * sum ;
	if (T1>=Upper) return 0;
 	return 1;
}
void meanSmooth(unsigned char * src,unsigned char * dst, int w, int h, int scale)
{
	int i,j,ii,jj,mean;

	for (i = scale;i<h-scale;i++)
	{
		for (j = scale;j<w-scale;j++)
		{
			mean = 0;
			for (ii = -1*scale;ii<=scale;ii++)
			{
				for (jj = -1*scale;jj<=scale;jj++)
				{
					mean += src[(i+ii)*w+j+jj];
				}
			}
			dst[i*w+j]= mean /(scale*2+1)/(scale*2+1);
		}
	}
}
void skinDetect(int * src, unsigned char * skin, int w, int h)
{
	unsigned char * skin1 = (unsigned char*)malloc(w*h*sizeof(unsigned char));
	int i,j,R,G,B,size = w*h;

	for (i=0;i<size;i++)
	{
		getRGB(src[i],&R,&G,&B);
		if (1 == skindetect0(R, G, B))      skin1[i] = 255;
		else if (1 == skindetect1(R, G, B)) skin1[i] = 255;
		else                                   skin1[i] = 0;
	}
	meanSmooth(skin1,skin,w,h,1);
	free(skin1);
}

void skinOverLay(int * src, int * dst, int w, int h)
{
	LOGW("skinOverLay 4.2\n");
	int t1 =  getCurrentTime();
	int i, size = w*h, R1,G1,B1 , R2,G2,B2 ;
	unsigned char * skin = (unsigned char*)malloc(w*h*sizeof(unsigned char));
	skinDetect(src, skin, w, h);

	for (i=0;i<size;i++)
	{
		if(255==skin[i]) {}
		else if(0==skin[i]) dst[i] = src[i];
		else
		{
			getRGB(src[i],&R1,&G1,&B1);
			getRGB(dst[i],&R2,&G2,&B2);
			R1 = (R1 * (255-skin[i]) + R2 * skin[i])/255;
			G1 = (G1 * (255-skin[i]) + G2 * skin[i])/255;
			B1 = (B1 * (255-skin[i]) + B2 * skin[i])/255;
			setRGB(&dst[i],R1,G1,B1);
		}
	}
	free(skin);
	int t2 =  getCurrentTime();
	LOGW("time  :%d ms\n",t2-t1);
}
