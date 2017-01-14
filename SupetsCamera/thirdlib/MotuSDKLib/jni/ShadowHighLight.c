#include "ShadowHighLight.h"
#include "DeHaze.h"
void gamaRateShadow(unsigned char * map)//r变换表
{
	int i;
	int x0,y0,a;
	x0 = -128;
	y0 = 128;
	a  = -128;
	for (i = 0;i<128;i++)
	{
		map[i] = y0 + (i+x0)*(i+x0)/a;
		//map[i] = getMIN(i*3/2,255);
	}
	for (i = 128;i<256;i++)
	{
		map[i] = i;
	}
}
static inline void ShadowHighLight_shader(int * color)
{
	int r = ((* color >> 16) & 0xFF);
	int g = ((* color >> 8) & 0xFF);
	int b = ((* color) & 0xFF);
	int gray_src = (r+g+b)/3;
	int gray_dst = gray_src>128?gray_src:128 - (gray_src-128)*(gray_src-128)/128;//1/2抛物线
	//int gray_dst = (int)sqrt((double)(gray_src*510 - gray_src * gray_src));//1/4 圆
	//int gray_dst = 255 - (gray_src-255)*(gray_src-255)/255;//抛物线
	//int gray_dst = getMIN((gray_src*3),255);

	if (gray_src>0)
	{
		r = r*gray_dst/gray_src;
		r = r>255?255:r;

		g = g*gray_dst/gray_src;
		g = g>255?255:g;

		b = b*gray_dst/gray_src;
		b = b>255?255:b;
	}
	int r_src = ((* color >> 16) & 0xFF);
	int g_src = ((* color >> 8) & 0xFF);
	int b_src = ((* color) & 0xFF);

	int gray_mean = (r+g+b + r_src+g_src+b_src)/6;
	r = (r_src*gray_mean+r*(255-gray_mean))/255;
	g = (g_src*gray_mean+g*(255-gray_mean))/255;
	b = (b_src*gray_mean+b*(255-gray_mean))/255;

	(*color) = (*color) & 0xFF000000;
	(*color) = (*color) |((r << 16)+(g << 8)+ b );
}
static inline void ShadowHighLight_shader1(int * color)
{
	int r = ((* color >> 16) & 0xFF);
	int g = ((* color >> 8) & 0xFF);
	int b = ((* color) & 0xFF);
	int gray_src = (r+g+b)/3;
	//int gray_dst = gray_src>128?gray_src:128 - (gray_src-128)*(gray_src-128)/128;//1/2抛物线
	//int gray_dst = (int)sqrt((double)(gray_src*510 - gray_src * gray_src));//1/4 圆
	//int gray_dst = 255 - (gray_src-255)*(gray_src-255)/255;//抛物线
	int gray_dst = getMIN((gray_src*3),255);

	if (gray_src>0)
	{
		r = r*gray_dst/gray_src;
		r = r>255?255:r;

		g = g*gray_dst/gray_src;
		g = g>255?255:g;

		b = b*gray_dst/gray_src;
		b = b>255?255:b;
	}
	int r_src = ((* color >> 16) & 0xFF);
	int g_src = ((* color >> 8) & 0xFF);
	int b_src = ((* color) & 0xFF);

	int gray_mean = (r+g+b + r_src+g_src+b_src)/6;
	r = (r_src*gray_mean+r*(255-gray_mean))/255;
	g = (g_src*gray_mean+g*(255-gray_mean))/255;
	b = (b_src*gray_mean+b*(255-gray_mean))/255;

	(*color) = (*color) & 0xFF000000;
	(*color) = (*color) |((r << 16)+(g << 8)+ b );
}
void ShadowHighLight(int * img, int w, int h)
{
	LOGW("ShadowHighLight 01.13\n");
	int * tmp_img = (int *)malloc(w*h*sizeof(int));
	unsigned char * map = (unsigned char*) malloc(256*sizeof(unsigned char));
	unsigned char * gray = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * gray1 = (unsigned char * )malloc(w*h*sizeof(unsigned char));

	gamaRateShadow(map);
	gamaRect(img, tmp_img, w, h, map);
	HDRImageGenerate1(img,tmp_img,img,w,h);
	// 	RGB2GRAY(img,gray,w*h);
	//	LocaEnhance(gray,gray1, w, h, 2,10);
	//	ImageChangeY1(img, gray1, w*h);
	free(tmp_img);
	free(map);
	free(gray);
	free(gray1);

	 CLAHERGB3(img , w, h, 5, 8);
}

void ShadowHighLight1(int * img, int w, int h)
{
 	int i ,size = w*h;
	for (i =0;i<size;i++)
	{
		ShadowHighLight_shader(&img[i]);
	}
}
void ShadowHighLight2(int * img, int w, int h)
{
 	int i ,size = w*h;
 	sceneProcess(img,  w,  h);
	for (i =0;i<size;i++)
	{
		ShadowHighLight_shader(&img[i]);
	}

	unsigned char * gray = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * gray1 = (unsigned char * )malloc(w*h*sizeof(unsigned char));
 	RGB2GRAY(img,gray,w*h);
	LocaEnhance(gray,gray1, w, h, 2, 10);
	ImageChangeY1(img, gray1, w*h);
	free(gray);
	free(gray1);
}
