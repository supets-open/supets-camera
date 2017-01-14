#include "NightShoot.h"



void gamaRate(unsigned char * map)//r变换表
{
	int i;
	int x0,y0,a;
	x0 = -255;
	y0 = 255;
	a  = -255;
	for (i = 0;i<256;i++)
	{
		map[i] = y0 + (i+x0)*(i+x0)/a;
		//map[i] = getMIN(i*3/2,255);
	}
}

unsigned char MedianFilter(unsigned char * Y , int num)// 中值滤波
{
	int hist[256]={0};
	int num_sum = 0;
	unsigned char i,median,thre = num/2  ;
	for (i = 0;i<num;i++)
	{
		hist[Y[i]]++;
	}

	for (i = 0;i<256;i++)
	{
		num_sum += hist[i];
		if (num_sum > thre)
		{
			median = i;
			break;
		}
	}

	for (i = 0;i<num;i++)
	{
		if ( Y[i] == median )
		{
			return i;
		}
	}
}
void ImageOverlap1( int ** images,int * dst, unsigned char * smoothBuf,unsigned char * gray0, int w, int h ,int img_num)
{
	int i,size = w*h,num;
	int R,G,B;
	for (i = 0;i<size;i++)
	{
		smoothBuf[0] = gray0[i];
		for (num = 1;num <img_num;num++)
		{
			getRGB(images[num][i],&R,&G,&B);
			smoothBuf[num] = (R+G+B)/3;
		}
		num = MedianFilter(smoothBuf,img_num);
		dst[i] = images[num][i];
	}
}
void gamaRect(int * src, int *dst, int w, int h, unsigned char * map )
{
	int size = w*h;
	int i;
	int R,G,B,gray,gray1;
	//unsigned char * map = (unsigned char*) malloc(256*sizeof(unsigned char));
	//gamaRate(map);
	for (i = 0;i<size;i++)
	{
		getRGB(src[i],&R,&G,&B);
		gray = (R+G+B)/3;
		if (gray>0)
		{
			gray1 = map[gray];
			R = R*gray1/gray;
			G = G*gray1/gray;
			B = B*gray1/gray;
			R = getMIN(R,255);
			G = getMIN(G,255);
			B = getMIN(B,255);

			setRGB(&dst[i],R,G,B);
		}
		else
		{
			dst[i] = src[i];
		}
	}

	//free(map);

}
void GetRectifyRT( unsigned char * gray1, unsigned char * gray2, unsigned char * gray3, int w, int h, float * RT, int * center_x1_src, int * center_y1_src, int center_num1, int image_id)
{

	int i;
	int match_x[4],match_y[4];
	int center_x[100]={0},center_y[100]={0}, center_x1[100]={0},center_y1[100]={0},center_num;

	center_num = center_num1;
	memcpy(center_x1,center_x1_src,center_num1*sizeof(int));
	memcpy(center_y1,center_y1_src,center_num1*sizeof(int));

	float RT1[6];
	RT[0] = 1.0f;
	RT[1] = 0.0f;
	RT[2] = 0.0f;
	RT[3] = 0.0f;
	RT[4] = 1.0f;
	RT[5] = 0.0f;
	memcpy(RT1,RT,6*sizeof(float));
	int search;
	switch(image_id)
	{
	case 1:
		search = h / 20;
		break;
	case 2:
		search = h / 20;
		break;
	default:
		search = h / 20;
		break;
	}
	//	LOGW("GetRectifyRT  8\n");
	for (i = 0;i<center_num;i++)
	{

		FastMatch(gray2,gray1,h,w,1,
			center_x1[i],center_y1[i],center_x1[i],center_y1[i],
			search*5/2,search/2,search,(match_x+0),(match_y+0));

		FastMatch(gray2,gray1,h,w,1,
			center_x1[i],center_y1[i],center_x1[i]+match_x[0],center_y1[i]+match_y[0],
			search*5/4, search/4,search/2,(match_x+1),(match_y+1));
		FastMatch(gray2,gray1,h,w,1,
			center_x1[i],center_y1[i],center_x1[i]+match_x[0]+match_x[1],center_y1[i]+match_y[0]+match_y[1],
			search*5/8, search/8,search/4,(match_x+2),(match_y+2));
		FastMatch(gray2,gray1,h,w,1,
			center_x1[i],center_y1[i],center_x1[i]+match_x[0]+match_x[1]+match_x[2],center_y1[i]+match_y[0]+match_y[1]+match_y[2],
			6, 1,search/8,(match_x+3),(match_y+3));

		center_x[i] = center_x1[i] + (match_x[0]+match_x[1]+match_x[2]+match_x[3]);
		center_y[i] = center_y1[i] + (match_y[0]+match_y[1]+match_y[2]+match_y[3]);
	}
	int id = SelectNum(center_x,center_y,center_x1,center_y1,center_num,w,h,&match_x[0],&match_y[0]);
	//LOGW("GetRectifyRT match points count : %d\n", id);
	if (id>0)
	{
		if (id<4)
		{
			RT[2] = (float)(match_x[0] - 128);
			RT[5] = (float)(match_y[0] - 128);
			RT1[2] = RT[2];
			RT1[5] = RT[5];
		}
		else
		{
			RT[2] = (float)(match_x[0] - 128);
			RT[5] = (float)(match_y[0] - 128);

			for (i = 0;i< id;i++)
			{
				RT1[2] += (float)(center_x[i] - center_x1[i]);
				RT1[5] += (float)(center_y[i] - center_y1[i]);
			}
			RT1[2] /= (float)id;
			RT1[5] /= (float)id;

		}
	}
	getRT_final(gray1,gray2,gray3,w,h,center_x,center_y,center_x1,center_y1,id,RT,RT1);
}
int Image0Points(int * image0,unsigned char * gray0,int w, int h, int * center_x, int * center_y,int *center_num)
{
	RGB2GRAY(image0,gray0,w*h);
	return GetEdgMax(gray0, w ,h , center_x,center_y, center_num,200);
}
int RectifyImages(unsigned char * gray1,unsigned char * gray2, unsigned char * gray3, int * src, int * dst, int w, int h, int * center_x_src, int * center_y_src, int center_num, int image_id)
{
	float RT[6];
	//	memset(gray2,0,w*h*sizeof(unsigned char));
	RGB2GRAY(src,gray2,w*h);

	//	int t3 =   getCurrentTime();
	GetRectifyRT(gray2, gray1, gray3, w, h, RT,center_x_src,center_y_src,center_num,image_id);
	//	int t4 =   getCurrentTime();
	//	LOGW("RectifyImages  %d time  :%d ms\n",image_id, t4-t3);

	memset(dst,0,w*h*sizeof(int));
	rectifyRGB(src,dst,w,h,RT,1);

	//LOGW("%f %f %f \n",RT[0],RT[1],RT[2]);
	//LOGW("%f %f %f \n",RT[3],RT[4],RT[5]);


	memcpy(src,dst,w * h * sizeof(int));
	return 1;
}

void ImageOverlapAndEnhance(int ** srcImages, unsigned char * gray1,unsigned char * gray2 , int * dst, int w, int h , int img_num)
{
	unsigned char * smoothBuf = (unsigned char *)malloc( img_num * sizeof(unsigned char));
	ImageOverlap1(  srcImages,  dst,  smoothBuf,gray1, w, h, img_num );
	unsigned char * map = (unsigned char*) malloc(256*sizeof(unsigned char));
	gamaRate(map);
	gamaRect(dst,srcImages[0],w,h,map);
	HDRImageGenerate1(dst, srcImages[0],dst, w, h);
	free(smoothBuf);
	free(map);
}

void NightGenerate(int ** srcImages, int * dst, int w, int h, int img_total)
{
	LOGW("NightGenerate 12.27\n");
	int i;
	//int t1 =   getCurrentTime();
	unsigned char * gray1;
	unsigned char * gray2;
	unsigned char * gray3;
	int center_x_src[100]={0},center_y_src[100]={0} ,center_num_src;

	gray1 = (unsigned char*)malloc( w * h * sizeof(unsigned char));
	gray2 = (unsigned char*)malloc( w * h * sizeof(unsigned char));
	gray3 = (unsigned char*)malloc( w * h * sizeof(unsigned char));
	memcpy(dst,srcImages[0],w * h * sizeof(int));
	Image0Points(srcImages[0], gray1, w,  h, center_x_src, center_y_src, &center_num_src);


	for(i = 1;i<img_total;i++)
	{
		RectifyImages(gray1,gray2,gray3,srcImages[i],dst,w,h,center_x_src,center_y_src,center_num_src,i);
	}

	//int t3 =   getCurrentTime();
	//LOGW("step 1 time  :%d ms\n", t3-t1);
	ImageOverlapAndEnhance(srcImages, gray1,gray2, dst, w, h, img_total);
		//usmProcess(dst,w,h,6,0,50);
		//usmProcess(dst,w,h,6,0,50);
	free(gray1);
	free(gray2);
	free(gray3);

	//int t4 =   getCurrentTime();
	//LOGW("step 2 time  :%d ms\n", t4-t3);


}
////////////////////////////////////////////YUV////////////////////////////////////////////////////////

int Image0PointsYUV(unsigned char * Y,int w, int h, int * center_x, int * center_y,int *center_num)
{
	return GetEdgMax(Y, w ,h , center_x,center_y, center_num,200);
}
void rectifyYUV(unsigned char * Y_src,unsigned char * UV_src,
				unsigned char * Y_dst,unsigned char * UV_dst,
				int w, int h,float * RT,int skip)
{
	/////////////////////rect y ///////////////////////////////
	rectifyGRAY(Y_src,Y_dst,w,h,RT,skip);
	/////////////////////rect uv///////////////////////////////
	int x,y,i,j,jstart,istart,jend,iend;
	int rt[6];
	for (i = 0;i<6;i++)
	{
		rt[i] = (int)(RT[i]*512+0.5);
	}
	getStart(rt,w,h,&jstart,&istart,&jend,&iend,skip);
	istart=istart/2+1;
	iend  =iend/2-1;
	jstart=jstart/2+1;
	jend  =jend/2-1;
	for (i = istart ;i<iend;i += skip)
	{
		for (j = jstart;j<jend;j +=skip)
		{
			x = (rt[0]* j*2+rt[1]* i*2+rt[2])>>10;
			y = (rt[3]* j*2+rt[4]* i*2+rt[5])>>10;


			UV_dst[i*w + j*2 +1] = UV_src[y*w + x*2 + 1];//u
			UV_dst[i*w + j*2 +0] = UV_src[y*w + x*2 + 0];//v

		}
	}
}
int RectifyImagesYUV(unsigned char * Y1,unsigned char * Y2, unsigned char * UV2,
					 unsigned char * Y2_rect, unsigned char * UV2_rect,
					 int w, int h, int * center_x_src, int * center_y_src, int center_num, int image_id)
{
	float RT[6];
	GetRectifyRT(Y2, Y1, Y2_rect, w, h, RT,center_x_src,center_y_src,center_num,image_id);
	LOGW("%f %f %f \n",RT[0],RT[1],RT[2]);
	LOGW("%f %f %f \n",RT[3],RT[4],RT[5]);

	memset(Y2_rect,-16,w*h*sizeof(unsigned char));
	memset(UV2_rect,128,w*h*sizeof(unsigned char)/2);
	rectifyYUV(Y2,UV2,Y2_rect,UV2_rect,w,h,RT,1);
	return 1;
}
void ImageOverlapYUV( unsigned char ** YUV,
					 unsigned char *  Y_dst, unsigned char * UV_dst,
					 unsigned char * smoothBuf, int w, int h ,int img_num)
{
	int i,j,id,num;

	for (i = 0;i<h;i++)
	{
		for (j = 0;j<w;j++)
		{
			id = i*w+j;
			for (num = 0;num <img_num;num++)
			{
				smoothBuf[num] = YUV[num][id];
			}
			num = MedianFilter(smoothBuf,img_num);
			Y_dst[id] = YUV[num][id];
			//UV_dst[i] = UV[num][i];

			id = (i / 2) * w + 2 * (j / 2);
			UV_dst[id + 1] = YUV[num][w*h + id+1];
			UV_dst[id + 0] = YUV[num][w*h + id+0];
		}
	}
}


//ith row, jth column

static inline void rgb2yuv(int *y, int *u, int *v, int r, int g, int b) {
	*y = ((263*r + 516*g + 100*b)>>10)  + 16;
	*u = ((-152*r - 298*g + 450*b)>>10)  + 128;
	*v = ((450*r - 377*g - 73)>>10)  + 128;

	//*y = getMIN(255, getMAX(0, *y));
	//*u = getMIN(255, getMAX(0, *u));
	//*v = getMIN(255, getMAX(0, *v));
}
static inline void yuv2rgb(int y, int u, int v, int *r, int *g, int *b) {
	y -= 16;
	u -= 128;
	v -= 128;

	if (y < 0)
		y = 0;

	*r = (getMAX(0, (1192 * y + 1634 * v)))>>10;
	*g = (getMAX(0, (1192 * y - 400 * u - 833 * v)))>>10;
	*b = (getMAX(0, (1192 * y + 2066 * u)))>>10;

	*r = getMIN(255, *r );
	*g = getMIN(255, *g );
	*b = getMIN(255, *b );

}

void img_YUV2RGB(unsigned char *pY, unsigned char *pUV,int * RGB, int width, int height)
{
	int i, j;
	int nR, nG, nB;
	int nY, nU, nV;

	// YUV 4:2:0
	for (i = 0; i < height; i++)
	{
		for (j = 0; j < width; j++)
		{
			nY = pY[i * width + j];
			nU = pUV[(i / 2) * width + 2 * (j / 2) + 1] ;
			nV = pUV[(i / 2) * width + 2 * (j / 2)];


			yuv2rgb(nY, nU, nV, &nR, &nG, &nB);
			setRGBA(&RGB[i*width+j],nR,nG,nB,0xff);
		}
	}
}
void img_RGB2YUV(unsigned char *pY, unsigned char *pUV,int * RGB, int width, int height)
{
	//showRGB(RGB,width,height,"src",1,0,0,0);
	int i, j;
	int nR, nG, nB;
	int nY, nU, nV;

	// YUV 4:2:0
	for (i = 0; i < height; i++)
	{
		for (j = 0; j < width; j++)
		{
			getRGB(RGB[i*width+j],&nR,&nG,&nB);
			rgb2yuv(&nY, &nU, &nV, nR, nG, nB);

			pY[i * width + j] = nY;
			pUV[(i / 2) * width + 2 * (j / 2) + 1] = nU;
			pUV[(i / 2) * width + 2 * (j / 2)] = nV;
		}
	}
	//showGRAY(pY,width,height,"Y",4,0,0,0);

}
void ImageOverlapAndEnhanceYUV(unsigned char ** YUV, int * dstRGB, int w, int h , int img_num)
{
	unsigned char * smoothBuf = (unsigned char *)malloc( img_num * sizeof(unsigned char));
	int * RGB_tmp = (int *)malloc( w*h * sizeof(int));
	ImageOverlapYUV(YUV,YUV[0],YUV[0]+w*h, smoothBuf, w, h, img_num);

		//showYUV(YUV[0],&YUV[0][w*h], w,h,"ImageOverlapYUV", 1, 0, 0, 0);
	unsigned char * map = (unsigned char*) malloc(256*sizeof(unsigned char));
	gamaRate(map);
	img_YUV2RGB(YUV[0],YUV[0]+w*h,dstRGB, w, h);
	gamaRect(dstRGB,RGB_tmp,w,h,map);
	HDRImageGenerate1(dstRGB, RGB_tmp,dstRGB, w, h);


	free(smoothBuf);
	free(RGB_tmp);
	free(map);
}
void RGBResizeNN(int * src, int * dst, int w_src, int h_src, int w_dst, int h_dst)
{
	int i , j, id_src, id_dst ;

	for (i = 0 ;i<h_dst;i++)
	{
		for (j = 0;j<w_dst;j++)
		{
			id_dst = i*w_dst + j;
			id_src = (i * h_src / h_dst) * w_src + (j * w_src / w_dst);
			dst[id_dst] = src[id_src];
		}
	}
}
void NightGenerateYUV(unsigned char ** YUV, int * dstRGB, int w_src, int h_src, int w_dst, int h_dst, int img_total)
{
//	LOGW("NightGenerateYUV 1 \n");
//	LOGW("%d %d %d %d %d\n", w_src, h_src, w_dst, h_dst, img_total);
	int i;
	int center_x_src[100]={0},center_y_src[100]={0} ,center_num_src;
	unsigned char * Y_rect = (unsigned char*)malloc( w_src * h_src * sizeof(unsigned char));
	unsigned char * UV_rect= (unsigned char*)malloc( w_src * h_src * sizeof(unsigned char)/2);
	int * dst_src= (int*)malloc( w_src * h_src * sizeof(int));

//	LOGW("NightGenerateYUV 2 \n");
	Image0PointsYUV(YUV[0], w_src,  h_src, center_x_src, center_y_src, &center_num_src);


	for(i = 1;i<img_total;i++)
	{
//		LOGW("NightGenerateYUV %d \n", i+2);
		RectifyImagesYUV(YUV[0],YUV[i], YUV[i]+w_src*h_src, Y_rect, UV_rect, w_src,h_src,center_x_src,center_y_src,center_num_src,i);

		memcpy(YUV[i],Y_rect,w_src*h_src*sizeof(unsigned char));
		memcpy(YUV[i]+w_src*h_src,UV_rect,w_src*h_src*sizeof(unsigned char)/2);
		//showYUV(YUV[i],&YUV[i][w*h], w,h,"YUV_rect", 1, 0, 0, 0);
	}
	//			LOGW("NightGenerateYUV %d \n", i+2);
	ImageOverlapAndEnhanceYUV(YUV, dst_src, w_src, h_src ,img_total);
	//			LOGW("NightGenerateYUV %d \n", i+3);
	usmProcess(dst_src,w_src,h_src,2,0,50);
//	usmProcess(dst_src,w_src,h_src,2,0,50);
	//		LOGW("NightGenerateYUV %d \n", i+4);
	RGBResizeNN(dst_src, dstRGB, w_src, h_src, w_dst, h_dst);
	//			LOGW("NightGenerateYUV %d \n", i+5);
	free(Y_rect);
	free(UV_rect);
	free(dst_src);
	//			LOGW("NightGenerateYUV %d \n", i+6);
}
