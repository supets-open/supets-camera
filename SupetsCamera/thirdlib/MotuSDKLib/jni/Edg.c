#include "Edg.h"


void Normalize(unsigned char * gray, int size)
{
	int i, min=255 , max = 0;
	for (i = 0 ;i<size;i++)
	{
		min = getMIN(min,gray[i]);
		max = getMAX(max,gray[i]);
	}
	for (i = 0 ;i<size;i++)
	{
		gray[i] = (gray[i] -min)*255/(max - min);
	}
	LOGW("min %d\n", min);
	LOGW("max %d\n", max);
}
void Kirsch(int * rgb, int w, int h , int kind)
{
	LOGW("trunk Kirsch 1.23  kind :%d \n",kind);
	unsigned char * gray = (unsigned char * )malloc(w*h*sizeof(unsigned char ));
	unsigned char * tmp  = (unsigned char * )malloc(w*h*sizeof(unsigned char ));
	RGB2GRAY(rgb, gray, w*h);

	int x,y,i;
	float a,b,c,d;
	float p1,p2,p3,p4,p5,p6,p7,p8,p9;
	unsigned char * ps = gray ;
	unsigned char * pd = tmp ;
	int R,G,B;
	int step = w;
	for(x = 0;x<w-2;x++)                                         // 1 4 7
	{                                                            // 2 5 8
		for(y = 0;y<h-2;y++)                                     // 3 6 9
		{
			p1=ps[y*step+x];
			p2=ps[y*step+(x+1)];
			p3=ps[y*step+(x+2)];
			p4=ps[(y+1)*step+x];
			p5=ps[(y+1)*step+(x+1)];
			p6=ps[(y+1)*step+(x+2)];
			p7=ps[(y+2)*step+x];
			p8=ps[(y+2)*step+(x+1)];
			p9=ps[(y+2)*step+(x+2)];

			a = fabs((float)(-5*p1-5*p2-5*p3+3*p4+3*p6+3*p7+3*p8+3*p9));
			b = fabs((float)(3*p1-5*p2-5*p3+3*p4-5*p6+3*p7+3*p8+3*p9));
			c = fabs((float)(3*p1+3*p2-5*p3+3*p4-5*p6+3*p7+3*p8-5*p9));
			d = fabs((float)(3*p1+3*p2+3*p3+3*p4-5*p6+3*p7-5*p8-5*p9));
			a = a>b?a:b;
			a = a>c?a:c;
			a = a>d?a:d;

			pd[(y+1)*w+(x+1)] = getMIN( a/4 ,255);

		}
	}

	Normalize(tmp,w*h);

if(kind == 1)
{
	for (i = 0;i<w*h;i++)
	{
	  tmp[i] = getMIN(getMAX((255-tmp[i]),100),255);//1
	  setRGB(&rgb[i],tmp[i],tmp[i],tmp[i]);
	}
}
else if(kind == 2)
{
	for (i = 0;i<w*h;i++)
	{
		tmp[i] = getMIN(getMAX((200-tmp[i]),100),255);//2
		setRGB(&rgb[i],tmp[i],tmp[i],tmp[i]);
	}
}

	free(gray);
	free(tmp);
}

void GetSobelEdg(int* src, int width, int height, int kind)
{
	LOGW("trunk GetSobelEdg \n");
	unsigned char * gray = (unsigned char * )malloc(width*height*sizeof(unsigned char ));
	int * edg  = (int * )malloc(width*height*sizeof(int));
	RGB2GRAY(src, gray, width*height);
	memset(edg,0,width*height*sizeof(int));
	int i,j,sobel1,sobel2,n,id;
	int x[9] = {-1,0,1,-1,0,1,-1,0,1};
	int y[9] = {-1,-1,-1,0,0,0,1,1,1};
	int Gx[9] = {-1,0,1,-2,0,2,-1,0,1};
	int Gy[9] = {1,2,1,0,0,0,-1,-2,-1};
	int i_end=height-1,j_end=width-1;
	int tmp;

	for ( i = 1;i<i_end;i++)
	{
		for ( j = 1;j<j_end;j++)
		{
			sobel1 = 0;
			sobel2 = 0;
			for ( n = 0;n<9;n++)
			{
				id = (i + y[n])*width + j + x[n];
				sobel1 += gray[id]*Gx[n];
				sobel2 += gray[id]*Gy[n];
			}
			edg[i*width+j] = abs(sobel1)+abs(sobel2);
 		}
	}
//    if(kind == 0)
//    {
    	for (i = 0;i<width*height;i++)
    	{
    		edg[i] = getMAX(getMIN((edg[i]-50)/3,255),0);
    		edg[i] = getMIN(getMAX((200-edg[i]),100),255);//2
    		setRGB(&src[i],edg[i],edg[i],edg[i]);
    	}
//    }

	free(gray);
	free(edg);
}
void LevelsCompression(int * src, int w, int h, int min, int max)
{
	int i,size= w*h;
	int R,G,B;
	for(i =0;i<size;i++)
	{
		getRGB(src[i],&R,&G,&B);
		R = ((R - 0)*(max-min)/(255-0))+min;
		G = ((G - 0)*(max-min)/(255-0))+min;
		B = ((B - 0)*(max-min)/(255-0))+min;
		setRGB(&src[i],R,G,B);
	}

}
void RGB2GRAY3(int * src, int size)
{
	int i,R,G,B;
	for(i=0;i<size;i++)
	{
		getRGB(src[i],&R,&G,&B);
		R = (R+G+B)/3;
		G = R;
		B = R;
		setRGB(&src[i],R,G,B);
	}
}

void Posterize(int * img , int w, int h ,int levels)//色调分离
{
	LOGW("trunk Posterize 2.17\n");
	int map[256];
	int i,R,G,B;
	int step = (256/levels);
	for (i = 0;i<256;i++)
	{
		map[i] = (i/step)*step;
	}
//	for (i = 0;i<254;i++)
//	{
//		map[i] = (map[i]+map[i+1]+map[i+2])/3;
//	}
	for (i = 0;i<w*h;i++)
	{
		getRGB(img[i],&R,&G,&B);
		setRGB(&img[i],map[R],map[G],map[B]);
	}
}
static inline int pixelOverLay(int srcPixel, int modePixel, int thre0, int thre1, int thre2, int thre3, int rate)//rate=(0,200)
{
	int R,G,B,gray,R1,G1,B1,alpha;
	getRGB(srcPixel,&R,&G,&B);
	getRGB(modePixel,&R1,&G1,&B1);
	gray = (R+G+B)/3;
	//高光区
	alpha = gray>thre2?gray:0;
	alpha = getMIN(rate*alpha/100,255);
	R = (R*(255-alpha)+R1*alpha)/255;
	G = (G*(255-alpha)+R1*alpha)/255;
	B = (B*(255-alpha)+R1*alpha)/255;
	//中间区
	alpha = gray>thre3?0:gray;
	alpha = alpha<thre0?0:alpha;
	alpha = getMIN(rate*alpha/100,255);
	R = (R*(255-alpha)+G1*alpha)/255;
	G = (G*(255-alpha)+G1*alpha)/255;
	B = (B*(255-alpha)+G1*alpha)/255;
	//暗区
	alpha = gray<thre1?gray:0;
	alpha = getMIN(rate*alpha/100,255);
	R = (R*(255-alpha)+B1*alpha)/255;
	G = (G*(255-alpha)+B1*alpha)/255;
	B = (B*(255-alpha)+B1*alpha)/255;
	int pixel = srcPixel;
	setRGB(&pixel,R,G,B);
 	return pixel;
}
void alphaOverLay(int * src, int * mode, int w, int h,
				  int thre0, int thre1, int thre2, int thre3, int rate)//rate=(0%,200%)
{
	LOGW("alphaOverLay 2.24: %d %d %d %d %d \n",thre0,  thre1,  thre2,  thre3, rate);
	int i, size = w*h;

	for (i = 0;i<size ;i++)
	{
		src[i] = pixelOverLay(src[i], mode[i], thre0, thre1, thre2, thre3, rate);
	}

}
/////////////////////////////////////////中值滤波///////////////////////////////////////////////

static inline int getMiddle(int * array, int * hist, int length)
{
	memset(hist,0,256*sizeof(int));
	int i,sum=0,size=length/2;
	for (i=0;i<length;i++)
	{
		hist[array[i]]++;
	}
	for (i=0;i<256;i++)
	{
		sum+=hist[i];
		if (sum>size)
		{
			break;
		}
	}

	return i;
}
static inline int getMiddle1(int * array_old,int * array_new, int * hist, int length)
{
	int i,sum=0,size =(length*length)/2;
	for (i=0;i<length;i++)
	{
		hist[array_old[i]]--;
		hist[array_new[i]]++;
	}
	for (i=0;i<256;i++)
	{
		sum+=hist[i];
		if (sum>size)
		{
			break;
		}
	}
	return i;
}
void medianFilter(int * src, int w, int h, int radius)
{
	int * dst = (int *)malloc(w*h*sizeof(int));
	int i,j,ii,jj,R,G,B;
	int i_start = radius,j_start = radius;
	int i_end = h-radius,j_end = w-radius;
	int * array_R = (int *)malloc((radius*2+1)*(radius*2+1)*sizeof(int));
	int * array_G = (int *)malloc((radius*2+1)*(radius*2+1)*sizeof(int));
	int * array_B = (int *)malloc((radius*2+1)*(radius*2+1)*sizeof(int));

	int hist_R[256],hist_G[256],hist_B[256];
	int count;

	memcpy(dst,src,w*h*sizeof(int));

	for (i=i_start;i<i_end;i++)
	{
		count=0;
		for (ii = 0-radius;ii<=radius;ii++)
		{
			for (jj = 0-radius;jj<=radius;jj++)
			{
				getRGB(src[(i+ii)*w+j_start+jj],&R,&G,&B);
				array_R[count] = R;
				array_G[count] = G;
				array_B[count] = B;
				count++;
			}
		}
		R = getMiddle(array_R,hist_R,count);
		G = getMiddle(array_G,hist_G,count);
		B = getMiddle(array_B,hist_B,count);
		setRGB(&dst[i*w+j_start],R,G,B);


		for (j=j_start+1;j<j_end;j++)
		{
			for (ii = 0-radius;ii<=radius;ii++)
			{
				getRGB(src[(i+ii)*w+j-radius-1],&R,&G,&B);
				array_B[ii+radius] = B;
				array_G[ii+radius] = G;
				array_R[ii+radius] = R;
				getRGB(src[(i+ii)*w+j+radius],&R,&G,&B);
				array_B[ii+(radius*3+1)] = B;
				array_G[ii+(radius*3+1)] = G;
				array_R[ii+(radius*3+1)] = R;
			}
			R = getMiddle1(array_R,&array_R[radius*2+1],hist_R,(radius*2+1));
			G = getMiddle1(array_G,&array_G[radius*2+1],hist_G,(radius*2+1));
			B = getMiddle1(array_B,&array_B[radius*2+1],hist_B,(radius*2+1));
			setRGB(&dst[i*w+j],R,G,B);
		}
	}
	memcpy(src,dst,w*h*sizeof(int));
	free(array_B);
	free(array_G);
	free(array_R);
	free(dst);
}
/////////////////////////////////////////end///////////////////////////////////////////////
/******************************************/
void WaterColor(int * src , int * model, int w, int h)
{
	LOGW("trunk WaterColor \n");
	int * edg = (int *)malloc(w*h*sizeof(int));
	memcpy(edg,src,w*h*sizeof(int));
	Kirsch(edg,  w,  h , 2);
//	GetSobelEdg(edg, w, h, 0);
	Posterize(src, w, h,12);//色调分离
	LevelsCompression(src, w, h, 50, 180);

	OverlayAlpha(src, edg, w, h, 75);
	SoftLight(src, model,  w,  h) ;
	free(edg);
}
//mode1 : stroke 笔触
//mode2 : paper  画纸
void Pencil(int * src, int * model1, int * model2, int w, int h)
{
	LOGW("trunk Pencil 2.18\n");
	int * edg = (int *)malloc(w*h*sizeof(int));
    memcpy(edg,src,w*h*sizeof(int));
	Kirsch(edg,  w,  h , 2);
	RGB2GRAY3(src,w*h);
	Posterize(src, w, h,12);//色调分离
	LevelsCompression(src, w, h, 50, 180);

	OverlayAlpha(src, edg, w, h, 70);
//	OverlayAlpha(src, model1, w, h, 20);
//	OverlayAlpha(src, model2, w, h, 30);
	alphaOverLay(src, model1, w, h, 80, 90, 165, 175,100);
	OverlayAlpha(src, model2, w, h, 50);
	free(edg);
}
//mode1 : stroke 笔触
//mode2 : paper  画纸
void ColorPencil(int * src, int * model1, int * model2, int w, int h)
{
	LOGW("trunk ColorPencil 2.18\n");
	int * edg = (int *)malloc(w*h*sizeof(int));
    memcpy(edg,src,w*h*sizeof(int));
	Kirsch(edg,  w,  h , 2);
	Posterize(src, w, h,12);//色调分离
	LevelsCompression(src, w, h, 50, 180);

	OverlayAlpha(src, edg, w, h, 70);
//	OverlayAlpha(src, model1, w, h, 20);
//	OverlayAlpha(src, model2, w, h, 30);
	alphaOverLay(src, model1, w, h, 80, 90, 165, 175,100);
	OverlayAlpha(src, model2, w, h, 50);
	free(edg);
}
void GenerateImg(int* img,int w, int h, int R, int G, int B)
{
	int i;
	for(i = 0;i<w*h;i++)
	{
		setRGB(&img[i],R,G,B);
	}
}
void BlueEdg(int * src, int * model1, int * model2, int w, int h)
{
	LOGW("trunk BlueEdg \n");
	int * edg = (int *)malloc(w*h*sizeof(int));
	int * blue = (int *)malloc(w*h*sizeof(int));
    memcpy(edg,src,w*h*sizeof(int));
	GetSobelEdg(edg, w, h, 0);
	GenerateImg(blue, w,  h, 40, 45, 147);
	Overlay(edg, blue, w, h);
	RGB2GRAY3(src,w*h);
	Posterize(src, w, h,12);//色调分离
	LevelsCompression(src, w, h, 50, 180);

	OverlayAlpha(src, edg, w, h, 70);
	OverlayAlpha(src, model1, w, h, 20);
	OverlayAlpha(src, model2, w, h, 30);
	free(edg);
	free(blue);
}
void MeanFilter(int * src , int * dst , int w, int h, int scale_r)
{

	int i,j,ii,jj,R,G,B,meanR,meanG,meanB;
	int block_size = (scale_r*2+1)*(scale_r*2+1);
	memcpy(dst,src,w*h*sizeof(int));
	for(i=scale_r;i<h-scale_r;i++)
	{
		for(j =scale_r;j<w-scale_r;j++)
		{
			meanR = 0;
			meanG = 0;
			meanB = 0;
			for(ii = -1*scale_r;ii<=scale_r;ii++)
			{
				for(jj = -1*scale_r;jj<=scale_r;jj++)
				{
					getRGB(src[(i+ii)*w+j+jj],&R,&G,&B);
					meanR += R;
					meanG += G;
					meanB += B;
				}
			}
			meanR /= block_size;
			meanG /= block_size;
			meanB /= block_size;
			meanR = getMIN(getMAX(meanR,0),255);
			meanG = getMIN(getMAX(meanG,0),255);
			meanB = getMIN(getMAX(meanB,0),255);
			setRGB(&dst[i*w+j],meanR,meanG,meanB);
		}
	}
}
//void OilPainting(int * src, int * model1, int * model2, int w, int h)
//{
//	LOGW("trunk OilPainting \n");
//	int * edg = (int *)malloc(w*h*sizeof(int));
//    memcpy(edg,src,w*h*sizeof(int));
//	GetSobelEdg(edg, w, h, 0);
//
//    Posterize(src, w, h,12);//色调分离
//	LevelsCompression(src, w, h, 30, 180);
// //    GaussSmoothMethodAllChannel(src, w, h, 5);
//    medianFilter(src, w,h,8);
//	OverlayAlpha(src, edg, w, h, 85);
//	OverlayAlpha(src, model1, w, h,70);
//	OverlayAlpha(src, model2, w, h,50);
//	free(edg);
//
//}


void OilPainting(int * src, int * model1, int * model2, int w, int h)
{
	LOGW("trunk OilPainting \n");
	int * edg = (int *)malloc(w*h*sizeof(int));
    memcpy(edg,src,w*h*sizeof(int));
	GetSobelEdg(edg, w, h, 0);

    medianFilter(src, w,h,8);
	LevelsCompression(src, w, h, 30, 180);
 //    GaussSmoothMethodAllChannel(src, w, h, 5);

	OverlayAlpha(src, edg, w, h, 85);
	OverlayAlpha(src, model1, w, h,70);
	OverlayAlpha(src, model2, w, h,50);
	free(edg);

}

void setHopeEffectTexturePixels(int *texArray, int w, int h) {
	int rArray[3] = { 215, 26, 33 };
	int wArray[3] = { 252, 228, 168 };
	int dArray[3] = { 0, 50, 59 };
	int bArray[3] = { 124, 164, 174 };
	//    Posterize(srcPixArray, w, h, 10);
	//生成一张图条纹的纹理图，先估算一下条纹的宽度
	int texW = 10, texLabel;
	int i, j = 0;
	memset(texArray, 0x00000000, sizeof(int) * w * h);
	for (i = 0; i != h; i++) {
		texLabel = i / texW;
		for (j = 0; j != w; j++) {
			if (texLabel % 2 == 0) {
				setA(&texArray[i * w + j], 255);
				setR(&texArray[i * w + j], wArray[0]);
				setG(&texArray[i * w + j], wArray[1]);
				setB(&texArray[i * w + j], wArray[2]);
			} else {
				setA(&texArray[i * w + j], 255);
				setR(&texArray[i * w + j], bArray[0]);
				setG(&texArray[i * w + j], bArray[1]);
				setB(&texArray[i * w + j], bArray[2]);
			}
		}
	}

}

void HopeEffect(int *srcPixArray, int w, int h)
{
	int rArray[3] = { 215, 26, 33 };
	int wArray[3] = { 252, 228, 168 };
	int dArray[3] = { 0, 50, 59 };
	int bArray[3] = { 124, 164, 174 };
    int *texArray = (int *)malloc(sizeof(int) * w * h);
    //memcpy(texArray, srcPixArray, sizeof(int) * w * h);


    setHopeEffectTexturePixels(texArray, w, h);

    int i;
    int r, g, b;
    for (i = 0; i != w * h; ++i)
    {
        r = getR(srcPixArray[i]);
        g = getG(srcPixArray[i]);
        b = getB(srcPixArray[i]);

        transRgb2Lab(&r, &g, &b);

		if (r < 60)
		{
			setR(&srcPixArray[i], dArray[0]);
			setG(&srcPixArray[i], dArray[1]);
			setB(&srcPixArray[i], dArray[2]);

		}
		else if (r >= 60 && r < 100)
		{
			setR(&srcPixArray[i], rArray[0]);
			setG(&srcPixArray[i], rArray[1]);
			setB(&srcPixArray[i], rArray[2]);

		}
		else if(r >= 100 && r < 150)
		{
			setR(&srcPixArray[i], bArray[0]);
			setG(&srcPixArray[i], bArray[1]);
			setB(&srcPixArray[i], bArray[2]);

		}
		else if(r >=  150 && r < 178)
		{

			srcPixArray[i] = texArray[i];

		}
		else if(r >= 178 && r < 256)
		{
			setR(&srcPixArray[i], wArray[0]);
			setG(&srcPixArray[i], wArray[1]);
			setB(&srcPixArray[i], wArray[2]);

		}
    }
    free(texArray);
}

void HalfTone(int *srcPixArray, int w, int h, int *tTexture, int tw, int th, int flag)
{
    int i , j, ii, jj;
    int r, g, b;

    if(flag == 0)
    {
        for(i = 0; i != w * h; ++i)
        {
        	r = ( getR(srcPixArray[i]) + getG(srcPixArray[i]) + getB(srcPixArray[i]) ) / 3;
        	setR(&srcPixArray[i], r);
        	setG(&srcPixArray[i], r);
        	setB(&srcPixArray[i], r);
        }
    }
    int *edgeImage = (int *)malloc(sizeof(int) * w * h);
    memcpy(edgeImage, srcPixArray, sizeof(int) * w * h);
    Kirsch(edgeImage, w, h, 1);

    LevelsCompression(srcPixArray, w, h, 45, 192);
    //Luminance Contrast Saturation + 60
    //黑色边 白色底的边缘 叠加方式darken
    //LOGW("The value of baseHeight and baseWidth %d %d", baseHeight, baseWidth);
    //LOGW("Posterize and LevelsCompression OK");

    int *lArray = (int *)malloc(sizeof(int) * w * h);


    //Luminance
    for(i = 0; i != w * h; ++i)
    {
    	r = getR(srcPixArray[i]);
        g = getG(srcPixArray[i]);
    	b= getB(srcPixArray[i]);

    	transRgb2Lab(&r, &g, &b);

    	r *= 1.6;
    	if(r >255)
    		r = 255;
    	lArray[i] = r;
    	transLab2Rgb(&r, &g, &b);
    	setR(&srcPixArray[i], r);
    	setG(&srcPixArray[i], g);
    	setB(&srcPixArray[i], b);
    }

    //Contrast
    contrastEffect(srcPixArray, w , h, 60);

    //Saturation
    saturationfilter(srcPixArray, w, h, 0.6);

    Darken(srcPixArray, edgeImage, w, h);

    for(i = 0; i != w * h; ++i)
    {
    	r = getR(srcPixArray[i]);
    	g = getG(srcPixArray[i]);
    	b = getB(srcPixArray[i]);

    	transRgb2Lab(&r, &g, &b);

    	lArray[i] = r;
    }

	int *tArray = (int *)malloc(sizeof(int) * tw * tw);
	memset(tArray, 255, sizeof(int) * tw * tw);

	for(i = 0; i != tw * tw; ++i)
	{
		tArray[i] = getB(tTexture[i]);
	}
    //根据亮暗生成不同大小点

    int meshDis = 14;
    float scaleRatio;
    int realr;
    fastAverageBlur(lArray, w, h, meshDis / 2);

//    LOGW("fastAverageBlur has ended");

    int baseWidth = tw;

    for(i = 0; i != baseWidth * baseWidth; ++i)
    {
    	if(tArray[i] < 160)
    		tArray[i] = 0;
    	else
    		tArray[i] = 255;
    }
    int *tmpSrc = (int *)malloc(sizeof(int) * w * h);
    memcpy(tmpSrc, srcPixArray, sizeof(int) * w * h);
    int reali, realj;

   for(i = 0; i < h; i += meshDis)
   {
	   for(j = 0; j < w; j += meshDis)
	   {
		   scaleRatio = 1.0 - lArray[i * w + j] * 1.0 / 255.0;
		   float realRatio = scaleRatio;
		   if(realRatio < 0.1)
			   realRatio = 0.1;
		   scaleRatio = scaleRatio > 0.2 ? scaleRatio : 0;
		   realr = baseWidth / 2 * scaleRatio;
		   for(ii = -realr + baseWidth / 2; ii < baseWidth / 2 + realr; ++ii)
		   {
			   reali = i + ii - baseWidth / 2;
			   if(reali < 0)
				   reali = 0;
			   if(reali >= h)
				   reali = h - 1;
			   for(jj = -realr + baseWidth / 2; jj < baseWidth / 2 + realr; ++jj)
			   {
				   realj = j + jj - baseWidth / 2;
				   if(realj < 0)
					   realj = 0;
				   if(realj >= w)
					   realj= w - 1;

				   if((ii - baseWidth / 2) * (ii - baseWidth / 2) + (jj - baseWidth / 2) * (jj - baseWidth / 2) < realr * realr && tArray[ii * baseWidth + jj] < 200)
				   {
					   int tmpVa = 10 / realRatio;
					   tmpVa = (tmpVa < getR(srcPixArray[reali * w + realj])) ? tmpVa : getR(srcPixArray[reali * w + realj]);
					   setR(&srcPixArray[reali * w + realj], tmpVa);// * 0.4 + getR(srcPixArray[(i + ii - baseWidth / 2) * w + j + jj - baseWidth / 2]) * 0.6);
					   tmpVa = (tmpVa < getG(srcPixArray[reali * w + realj])) ? tmpVa : getG(srcPixArray[reali * w + realj]);
					   setG(&srcPixArray[reali * w + realj], tmpVa);// * 0.4 + getG(srcPixArray[(i + ii - baseWidth / 2) * w + j + jj - baseWidth / 2]) * 0.6);
					   tmpVa = (tmpVa < getB(srcPixArray[reali * w + realj])) ? tmpVa : getB(srcPixArray[reali * w + realj]);
					   setB(&srcPixArray[reali * w + realj], tmpVa);// * 0.4 + getB(srcPixArray[(i + ii - baseWidth / 2) * w + j + jj - baseWidth / 2]) * 0.6);
				   }
			   }
		   }
//		   return;
	   }
   }

   for(i = 0; i != w * h; ++i)
   {
	   setR(&srcPixArray[i], getR(srcPixArray[i]) * 0.6 + getR(tmpSrc[i]) * 0.4);
	   setG(&srcPixArray[i], getG(srcPixArray[i]) * 0.6 + getG(tmpSrc[i]) * 0.4);
	   setB(&srcPixArray[i], getB(srcPixArray[i]) * 0.6 + getB(tmpSrc[i]) * 0.4);
   }


    free(tmpSrc);
    free(lArray);
    free(tArray);
    free(edgeImage);
}
