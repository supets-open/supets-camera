#include "hdr.h"
#define EPS 0.000000001
#define PI  3.1415926

int istart,iend,jstart,jend;
static inline int GetMatchValue(unsigned char * src1, unsigned char * src2,
								int w,int h, int channel, int match_width, int match_height, int skip,
								int x1, int y1, int x2, int y2)
{

	int value=0;
	int i,j,k,id1,id2;

	if ((0+y1)<0||(match_height+y1)>h||(0+y2)<0||(match_height+y2)>h||
		(0+x1)<0||(match_width+x1) >w||(0+x2)<0||(match_width+x2) >w)
	{
		//printf("GetMatchValue 越界\n");
		return match_height*match_width*255;

	}
	if (1 == channel)
	{
		for  (i = 0;i<match_height;i+=skip)
		{
			id1 = (i+y1)*w+x1;
			id2 = (i+y2)*w+x2;
			for  (j = 0 ;j<match_width;j+=skip)
			{
				value += abs(src1[id1+j]-src2[id2+j]);
			}
		}
	}
	if (3 == channel)
	{
		for  (i = 0;i<match_height;i+=skip)
		{
			for  (j = 0 ;j<match_width;j+=skip)
			{
				id1 = (i+y1)*w*channel+(j+x1)*channel;
				id2 = (i+y2)*w*channel+(j+x2)*channel;
				for  (k = 0;k<channel;k++)
				{
					value += abs(src1[id1+k]-src2[id2+k]);
				}
			}
		}
	}


	return value;
}
inline  int FastMatch(unsigned char * src1, unsigned char * src2,
					  int height, int width, int channel,
					  int center_x ,int center_y,
					  int center_x1 ,int center_y1,
					  int match_block_size,int skip, int match_scale,
					  int * position_x, int * position_y)
{

	int flag = 0;
	int i , j;
	int start_x = center_x - match_block_size/2;
	int start_y = center_y - match_block_size/2;
	int start_x1 = center_x1 - match_block_size/2;
	int start_y1 = center_y1 - match_block_size/2;

	start_x = start_x<0?0:start_x;
	start_x = (start_x+match_block_size)>(width-1)?(width-1-match_block_size):start_x;
	start_y = start_y<0?0:start_y;
	start_y = (start_y+match_block_size)>(height-1)?(height-1-match_block_size):start_y;

	start_x1 = start_x1<0?0:start_x1;
	start_x1 = (start_x1+match_block_size)>(width-1)?(width-1-match_block_size):start_x1;
	start_y1 = start_y1<0?0:start_y1;
	start_y1 = (start_y1+match_block_size)>(height-1)?(height-1-match_block_size):start_y1;

	int value,value_min = match_block_size*match_block_size*channel*255;
	for ( i = match_scale*-1;i<match_scale;i+=skip)
	{
		for ( j = match_scale*-1;j<match_scale;j+=skip)
		{
			if( start_x+j<0||start_y+i<0||start_x+j>=width||start_y+i>=height) continue;

			value = GetMatchValue(src1,src2,width,height,channel,match_block_size,match_block_size,skip,
				start_x,start_y,
				start_x1+j,start_y1+i);
			if (value<value_min)
			{
				value_min = value;
				*position_x = j;
				*position_y = i;
				flag = 1;
			}

		}
	}
	//printf("match_value_min:%d\n",value_min);
	return flag;
}
static inline int GetImageMax(int * img, int w, int start_x, int start_y, int end_x, int end_y, int * max_x, int * max_y)
{
	//printf("bolck:%d,%d,%d,%d\n",start_x,end_x,start_y,end_y);
	int i,j,max = 0,id;
	for (i = start_y;i<end_y;i++)
	{
		id = i*w+start_x;
		for (j = start_x;j<end_x;j++)
		{
			if (img[id]>=max)
			{
				max = img[id];
				*max_x = j;
				*max_y = i;
			}
			id++;
		}
	}

	//printf("sobel max,%d\n",max);


	return max;
}
void Sobel(unsigned char * Y, int * edg, int width, int height)
{
	int i,j,sobel1,sobel2,n,id;
	int x[9] = {-1,0,1,-1,0,1,-1,0,1};
	int y[9] = {-1,-1,-1,0,0,0,1,1,1};
	int Gx[9] = {-1,0,1,-2,0,2,-1,0,1};
	int Gy[9] = {1,2,1,0,0,0,-1,-2,-1};
	int i_end=height*9/10,j_end=width*9/10;
	for ( i = height/10;i<i_end;i++)
	{
		for ( j = width/10;j<j_end;j++)
		{
			sobel1 = 0;
			sobel2 = 0;
			for ( n = 0;n<9;n++)
			{
				id = (i + y[n])*width + j + x[n];
				sobel1 += Y[id]*Gx[n];
				sobel2 += Y[id]*Gy[n];
			}
			edg[id] = abs(sobel1)+abs(sobel2);
			id++;
		}
	}
}
int GetEdgMax(unsigned char * Y, int width, int height,int * max_x, int * max_y, int * num, int thre)
{
	int * edg = (int * )malloc(width * height * sizeof(int));
	int block_num = 10;//10*10
	int * start_x, * start_y;
	int i, j,count=0;
	int value;
	int max = -999999,max_id = -1;
	start_x = (int *)malloc(block_num*sizeof(int));
	start_y = (int *)malloc(block_num*sizeof(int));

	Sobel(Y,edg,width,height);
	//showGRAY(Y,width,height,"Y",1,0,0,0);

	for (i = 0;i<block_num;i++)
	{
		start_y [i] = height*i/block_num;
		start_x [i] = width *i/block_num;
	}
	for (i = 1;i<block_num-1;i++)
	{
		for (j = 1;j<block_num-1;j++)
		{
			value = GetImageMax(edg,width,start_x[j],start_y[i],start_x[j]+width/block_num,start_y[i]+height/block_num,&max_x[count],&max_y[count]);

			if (value>thre)
			{
				//showGRAY(Y,width,height,"sobel center",4,max_x[count],max_y[count],30);
				if (value>= max)
				{
					max = value;
					max_id = count;
				}
				count ++;

			}

		}
	}
	*num = count;
	free(edg);
	free(start_x);
	free(start_y);
	return max_id;
}
void getLinePara(int x1, int y1, int x2, int y2, float * a, float * b)
{
	if (x1!=x2)
	{
		*a = (float)(y1-y2)/(float)(x1-x2);
		*b = (float)y1 - (*a)*(float)x1;
	}
	else
	{


	}

}
void HDRImageGenerate(int *image1,int *image2, int * dst_rgb, int w, int h)
{
	int i,size = w * h;
	int R,G,B;
	int R1,G1,B1;
	int gray_mean,gray ,gray1;
	double H,S,I;
	float a1,b1,a2,b2;
	getLinePara(0,0,85,128,&a1,&b1);
	getLinePara(170,128,255,255,&a2,&b2);

	for ( i = 0;i<size;i++)
	{

		getRGB(image1[i],&R,&G,&B);//dark
		getRGB(image2[i],&R1,&G1,&B1);//bright

		gray_mean = (R+G+B + R1+G1+B1)/6;
		if (gray_mean<85)
		{
			gray = (int)((float)gray_mean * a1 + b1);
			//gray = getMAX(gray,0);

		}
		else if (gray_mean < 170)
		{
			gray = 128;
		}
		else
		{
			gray = (int)((float)gray_mean * a2 + b2);
		}

		gray1 = 255 - gray;
		R = (R*gray+R1*gray1)/255;
		G = (G*gray+G1*gray1)/255;
		B = (B*gray+B1*gray1)/255;
		setRGB(&dst_rgb[i],R,G,B);
	}


}
void HDRImageGenerate1(int *image1,int *image2, int * dst_rgb, int w, int h)
{
	int i,size = w * h;
	int R,G,B;
	int R1,G1,B1;
	int gray_mean,gray ,gray1;
	double H,S,I;
	for ( i = 0;i<size;i++)
	{
		getRGB(image1[i],&R,&G,&B);//dark
		getRGB(image2[i],&R1,&G1,&B1);//bright

		gray_mean = (R+G+B + R1+G1+B1)/6;


		//gray_mean = getMIN(gray_mean,100);
		R = (R*gray_mean+R1*(255-gray_mean))/255;
		G = (G*gray_mean+G1*(255-gray_mean))/255;
		B = (B*gray_mean+B1*(255-gray_mean))/255;
		setRGB(&dst_rgb[i],R,G,B);
	}
}


int GetImageMEAN(unsigned char * image,int width,int height)
{
	int i, j;
	int mean = 0;

	for ( i = 0;i<height;i+=16)
	{
		for ( j = 0;j<width;j+=16)
		{
			mean += image[i*width+j];
		}
	}
	mean/=(width*height)>>8;
	return mean;
}
void ChangeImageY(unsigned char * image,int src_mean,int dst_mean,int size)
{

	int i,gray;
	int rate;
	if (src_mean >0) rate = (dst_mean<<16)/src_mean;
	else             rate = (1<<16);
	for ( i = 0;i<size;i++)
	{
		gray = (rate*image[i])>>16;
		image[i] = getMIN(gray,255);
	}

}
int SelectNum(int * x1, int * y1,int * x2, int * y2, int n,int w, int h, int * max_x, int * max_y)
{
	int i,j;
	int hist[256][256] = {0};
	int delta_x, delta_y,count;
	for (i = 0;i<n;i++)
	{

		delta_x = x1[i]-x2[i]+128;
		//printf("delta_x:%d ",x1[i]-x2[i]);
		delta_x = getMIN(delta_x,255);
		delta_x = getMAX(delta_x,0);

		delta_y = y1[i]-y2[i]+128;
		//printf("delta_y:%d\n ",y1[i]-y2[i]);
		delta_y = getMIN(delta_y,255);
		delta_y = getMAX(delta_y,0);
		hist[delta_x][delta_y]++;

	}
	int max = 1;
	*max_x=9999999;
	*max_y=9999999;
	for (i = 1;i<255;i++)
	{
		for (j = 1;j<255;j++)
		{
			if (hist[j][i]>=max)
			{
				if ((abs(i-128)+abs(j-128))<(abs((*max_x)-128)+abs((*max_y)-128)))//选择最靠中心的点
				{
					max = hist[j][i];
					*max_x = j;
					*max_y = i;
				}
			}
		}
	}
	count =0;
	for (i = 0;i<n;i++)
	{
		delta_x = x1[i]-x2[i]+128;
		delta_y = y1[i]-y2[i]+128;
		if (abs(delta_x - (*max_x))<(w/64)&&abs(delta_y-(*max_y))<(h/48))
		{
			x1[count] = x1[i];
			x2[count] = x2[i];
			y1[count] = y1[i];
			y2[count] = y2[i];
			count++;
		}
	}
	return  count;
}
void ResizeNN(unsigned char * src, unsigned char * dst, int w_src, int h_src, int w_dst, int h_dst)
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

int getResult_2_2(int * src1, int * src2, float * dst)
{
	if ((src1[0]*src2[1]-src2[0]*src1[1])!=0&&(src1[1]*src2[0]-src2[1]*src1[0])!=0)
	{
		dst[0] = (float)(src1[2]*src2[1]-src2[2]*src1[1])/(float)(src1[0]*src2[1]-src2[0]*src1[1]);
		dst[1] = (float)(src1[2]*src2[0]-src2[2]*src1[0])/(float)(src1[1]*src2[0]-src2[1]*src1[0]);
		return 1;
	}
	else
	{
		return -1;
	}
}
int getResult_3_3(int * src1, int * src2, int * src3, float * dst)
{
	int i;
	int src11[3];
	int src22[3];

	for (i =0;i<3;i++)
	{
		src11[i] = src1[i] - src2[i];
		src22[i] = src1[i] - src3[i];
	}

	if (getResult_2_2(src11,src22,dst)<0)
	{
		return -1;
	}
	dst[2] = (float)src1[2] - (float)src1[0]*dst[0] - (float)src1[1]*dst[1];
	return 1;
}
int getRT(int * x1, int * y1, int *x2, int * y2, float * RT)
{
	int i;
	int src[3][3];
	float dst [3];

	for (i = 0;i<3;i++)
	{
		src[i][0] = x2[i];
		src[i][1] = y2[i];
		src[i][2] = x1[i];
	}

	getResult_3_3(src[0],src[1],src[2],dst);
	for (i = 0;i<3;i++)
	{
		RT[i] = dst[i];
	}

	for (i = 0;i<3;i++)
	{
		src[i][0] = x2[i];
		src[i][1] = y2[i];
		src[i][2] = y1[i];
	}


	getResult_3_3(src[0],src[1],src[2],dst);
	for (i = 0;i<3;i++)
	{
		RT[i+3] = dst[i];
	}
	return 1;
}

void getStart(int * rt, int w, int h, int * x_start, int * y_start, int * x_end, int * y_end, int skip)
{
	int points[4][2];
	int src1[3];
	int src2[3];
	float dst[2];

	src1[0] = rt[0];
	src1[1] = rt[1];
	src1[2] = 0 - rt[2];
	src2[0] = rt[3];
	src2[1] = rt[4];
	src2[2] = 0 - rt[5];
	getResult_2_2(src1,src2,dst);
	points[0][0] = (int)(dst[0]+0.5);
	points[0][1] = (int)(dst[1]+0.5);

	src1[0] = rt[0];
	src1[1] = rt[1];
	src1[2] = (w-1)*512 - rt[2];
	src2[0] = rt[3];
	src2[1] = rt[4];
	src2[2] = 0 - rt[5];
	getResult_2_2(src1,src2,dst);
	points[1][0] = (int)(dst[0]+0.5);
	points[1][1] = (int)(dst[1]+0.5);

	src1[0] = rt[0];
	src1[1] = rt[1];
	src1[2] = 0 - rt[2];
	src2[0] = rt[3];
	src2[1] = rt[4];
	src2[2] = (h-1)*512 - rt[5];
	getResult_2_2(src1,src2,dst);
	points[2][0] = (int)(dst[0]+0.5);
	points[2][1] = (int)(dst[1]+0.5);

	src1[0] = rt[0];
	src1[1] = rt[1];
	src1[2] = (w-1)*512 - rt[2];
	src2[0] = rt[3];
	src2[1] = rt[4];
	src2[2] = (h-1)*512 - rt[5];
	getResult_2_2(src1,src2,dst);
	points[3][0] = (int)(dst[0]+0.5);
	points[3][1] = (int)(dst[1]+0.5);
	*x_start = getMAX(getMAX(points[0][0],points[2][0]),skip);
	*y_start = getMAX(getMAX(points[0][1],points[1][1]),skip);
	*x_end   = getMIN(getMIN(points[1][0],points[3][0]),w-1)-1;
	*y_end   = getMIN(getMIN(points[2][1],points[3][1]),h-1)-1;


	*x_start = (*x_start)/skip*skip+skip;
	*y_start = (*y_start)/skip*skip+skip;
}
void rectifyGRAY(unsigned char * src, unsigned char * dst,int w,int h, float * RT, int skip)
{

	int x,y,i,j;

	int rt[6];
	for (i = 0;i<6;i++)
	{
		rt[i] = (int)(RT[i]*512+0.5);
	}
	getStart(rt,w,h,&jstart,&istart,&jend,&iend,skip);
	for (i = istart;i<iend;i += skip)
	{
		for (j = jstart;j<jend;j +=skip)
		{
			x = (rt[0]* j+rt[1]* i+rt[2])>>9;
			y = (rt[3]* j+rt[4]* i+rt[5])>>9;
			dst[i*w+j] = src[y*w+x];

		}
	}
}
void rectifyRGB(int * src, int * dst,int w,int h, float * RT, int skip)
{
	int x,y,i,j;

	int rt[6];
	for (i = 0;i<6;i++)
	{
		rt[i] = (int)(RT[i]*512+0.5);
	}
	getStart(rt,w,h,&jstart,&istart,&jend,&iend,skip);
	for (i = istart;i<iend;i += skip)
	{
		for (j = jstart;j<jend;j +=skip)
		{
			x = (rt[0]* j+rt[1]* i+rt[2])>>9;
			y = (rt[3]* j+rt[4]* i+rt[5])>>9;
			dst[i*w+j] = src[y*w+x];
		}
	}


}
static inline unsigned int imageSub(unsigned char * src1 , unsigned char * src2, int w, int h, int channel, int skip)
{
	unsigned int sub_sum =0, id,i,j;
	for (i = istart;i<iend;i += skip)
	{
		for (j = jstart;j<jend;j +=skip)
		{
			id = i*w+j;
			sub_sum += abs(src1[id]-src2[id]);
		}
	}
	return sub_sum*skip*skip/((iend-istart)>>2)/((jend-jstart)>>2);
}
void select3Points(int * x,int * y,int n,int * id )
{

	int distance, distance_max=0,i;
	for (i = 0;i<n;i++)
	{
		distance = (x[id[0]]-x[i])*(x[id[0]]-x[i])+(y[id[0]]-y[i])*(y[id[0]]-y[i]);
		if (distance>=distance_max)
		{
			distance_max = distance;
			id[1] = i;
		}
	}
	distance_max=0;
	for (i = 0;i<n;i++)
	{
		distance =  abs(x[id[0]]*y[id[1]] + x[id[1]]*y[i] + y[id[0]]*x[i] - y[id[1]]*x[i] - y[id[0]]*x[id[1]] - x[id[0]]*y[i])/2;
		if (distance>=distance_max)
		{
			distance_max = distance;
			id[2] = i;
		}
	}

}

void getRT_final(unsigned char * gray1,unsigned char * gray2,unsigned char * gray_rectify, int w, int h,
				 int * x1, int * y1, int * x2, int * y2, int num,
				 float * RT_final,float * RT_final2)
{

	int i,k;
	int id[3];
	int x11[3],x22[3],y11[3],y22[3];
	float RT[6];
	int skip =getMAX(w/640,1);
	//	unsigned char * gray_rectify = (unsigned char *)malloc(w*h*sizeof(unsigned char));
	unsigned int sub, sub_min ;
	rectifyGRAY( gray1, gray_rectify,w,h,RT_final,skip);
	sub_min = imageSub(gray_rectify,gray2,w,h,1,skip);

	rectifyGRAY( gray1, gray_rectify,w,h,RT_final2,skip);
	sub = imageSub(gray_rectify,gray2,w,h,1,skip);

	if (sub_min > sub)
	{
		memcpy(RT_final,RT_final2,6*sizeof(float));
		sub_min = sub;
	}
	sub_min = sub_min - sub_min / 50;
	if (num>2)
	{

		for (k = 0;k<num;k++)
		{
			id[0] = k;
			select3Points(x1,y1,num,id);
			for (i = 0;i<3;i++)
			{
				x11[i] = x1[id[i]];
				x22[i] = x2[id[i]];
				y11[i] = y1[id[i]];
				y22[i] = y2[id[i]];
			}
			getRT(x11,y11,x22,y22,RT);
			rectifyGRAY( gray1, gray_rectify,w,h,RT,skip);
			sub = imageSub(gray_rectify,gray2,w,h,1,skip);
			if (sub<=sub_min)
			{
				sub_min = sub;
				memcpy(RT_final,RT,6*sizeof(float));
			}
		}
	}
	//	free(gray_rectify);

}
void EdgCut(int * src, int * dst,int w, int h, float * RT)
{
	int i,j;
	int x,y;
	iend -= istart;
	jend -= jstart;
	for (i = 0;i<h;i++)
	{
		y = i*iend/h+istart;
		for (j = 0;j<w;j++)
		{
			x = j*jend/w+jstart;
			dst[i*w+j] = src[y*w+x];
		}
	}
}
void GetEdg(unsigned char * Y, unsigned char * edg, int width, int height, int thre)
{
	int i,j,sobel1,sobel2,n,id;
	int x[9] = {-1,0,1,-1,0,1,-1,0,1};
	int y[9] = {-1,-1,-1,0,0,0,1,1,1};
	int Gx[9] = {-1,0,1,-2,0,2,-1,0,1};
	int Gy[9] = {1,2,1,0,0,0,-1,-2,-1};
	int i_end=height-1,j_end=width-1;
	for ( i = 1;i<i_end;i++)
	{
		for ( j = 1;j<j_end;j++)
		{
			sobel1 = 0;
			sobel2 = 0;
			for ( n = 0;n<9;n++)
			{
				id = (i + y[n])*width + j + x[n];
				sobel1 += Y[id]*Gx[n];
				sobel2 += Y[id]*Gy[n];
			}
			if ((abs(sobel1)+abs(sobel2))>thre)
			{
				edg[i*width+j]=255;
			}
			else
			{
				edg[i*width+j]=0;
			}
		}
	}
}


void HistMap(int * hist, int * map, int hist_sum )
{
	int i;
	float p[256],c[256];

	for (i = 0;i<256;i++)
	{
		p[i] = (float)(hist[i])/(float)hist_sum;
	}
	c[0] = p[0];
	for (i =1;i<256;i++)
	{
		c[i] = c[i-1] + p[i];
	}
	for (i = 0;i<256;i++)
	{
		map[i] = 255*(c[i]-c[0])/(c[255]-c[0]);
	}
}
void EdgHistEnhance(unsigned char * Y,unsigned char * edg, int w, int h )
{
	int i,size = w*h;
	int hist_thre = size >>11;
	int hist[256]={0};
	int hist_sum = 0;

	for (i = 0;i<size;i++)
	{
		if (255 == edg[i] )
		{
			hist[Y[i]]++;
		}
	}

	for (i = 0;i<256;i++)
	{
		if ( hist[i]>hist_thre) hist[i]=hist_thre;
		hist_sum += hist[i];
	}
	HistMap(hist,hist,hist_sum );
	for (i = 0;i<size;i++)
	{
		Y[i] = hist[Y[i]];
	}

}
void ImageChangeY1(int * image, unsigned char * Y_dst, int size )
{
	int Y_src,r,g,b,id;
	for (id = 0;id < size;id++)
	{
		getRGB(image[id],&r,&g,&b);

		Y_src = (r+g+b)/3;
		if (Y_src > 0)
		{
			b = b*Y_dst[id]/Y_src;
			g = g*Y_dst[id]/Y_src;
			r = r*Y_dst[id]/Y_src;
			b = getMIN(b,255);
			g = getMIN(g,255);
			r = getMIN(r,255);
			setRGB(&image[id],r,g,b);
		}
	}
}
void gamaRateHDR(unsigned char * map)//r变换表
{
	int i;
	int x0,y0,a;
	x0 = -80;
	y0 = 80;
	a  = -80;
	for (i = 0;i<80;i++)
	{
		map[i] = y0 + (i+x0)*(i+x0)/a;
		//map[i] = getMIN(i*3/2,255);
	}
	for (i = 80;i<256;i++)
	{
		map[i] = i;
	}
}
void HDR(int * imgDark, int * imgBright, int * dst, int w, int h)
{
	LOGW("HDR 12.20\n");
	//	int t3 =  getCurrentTime();
	int i;
	int match_x[3],match_y[3];
	int center_x[100]={0},center_y[100]={0},center_x1[100]={0},center_y1[100]={0},center_num;
	int gray_mean[2];

	float RT[6];
	float RT1[6];
	RT[0] = 1.0f;
	RT[1] = 0.0f;
	RT[2] = 0.0f;
	RT[3] = 0.0f;
	RT[4] = 1.0f;
	RT[5] = 0.0f;
	memcpy(RT1,RT,6*sizeof(float));
	unsigned char * dark_gray = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * bright_gray =  (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * gray_rectify = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * small_gray =  (unsigned char * )malloc(w*h/16*sizeof(unsigned char));
	//step1:

	RGB2GRAY(imgDark,dark_gray,w*h);
	RGB2GRAY(imgBright,bright_gray,w*h);
	//step2:

	gray_mean[0]=GetImageMEAN(dark_gray,w,h);
	gray_mean[1]=GetImageMEAN(bright_gray,w,h);
	ChangeImageY(dark_gray,gray_mean[0],(gray_mean[0]+gray_mean[1])/2,w*h);
	ChangeImageY(bright_gray,gray_mean[1],(gray_mean[0]+gray_mean[1])/2,w*h);
	//step3:

	ResizeNN(dark_gray,small_gray,w,h,w/4,h/4);

	int edgMax_id = GetEdgMax(small_gray, w/4,h/4, center_x,center_y,&center_num,300);

	for (i =0;i<center_num;i++)
	{

		center_x[i] *= 4;
		center_y[i] *= 4;

	}

	//step4:
	int w1 = getMAX(h,w);
	int h1 = getMIN(h,w);
	for (i = 0;i<center_num;i++)
	{

		FastMatch(dark_gray,bright_gray,h,w,1,
			center_x[i],center_y[i],center_x[i],center_y[i],
			w1/16,11,h1/10,(match_x+0),(match_y+0));

		FastMatch(dark_gray,bright_gray,h,w,1,
			center_x[i],center_y[i],center_x[i]+match_x[0],center_y[i]+match_y[0],
			w1/16, 3,11,(match_x+1),(match_y+1));
		FastMatch(dark_gray,bright_gray,h,w,1,
			center_x[i],center_y[i],center_x[i]+match_x[0]+match_x[1],center_y[i]+match_y[0]+match_y[1],
			w1/16, 1,3,(match_x+2),(match_y+2));


		center_x1[i] = center_x[i] + (match_x[0]+match_x[1]+match_x[2]);
		center_y1[i] = center_y[i] + (match_y[0]+match_y[1]+match_y[2]);
	}

	int id = SelectNum(center_x,center_y,center_x1,center_y1,center_num,w,h,&match_x[0],&match_y[0]);

	//printf("match count :%d\n",id);
	//for (i = 0;i<id;i++)
	//{
	//	showRGB(imgDark,w,h,"dark",4,center_x[i],center_y[i],10);
	//	showRGB(imgBright,w,h,"bright",4,center_x1[i],center_y1[i],10);
	//}
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
	getRT_final(dark_gray,bright_gray,gray_rectify,w,h,center_x,center_y,center_x1,center_y1,id,RT,RT1);

	//printf("%f %f %f \n",RT[0],RT[1],RT[2]);
	//printf("%f %f %f \n",RT[3],RT[4],RT[5]);

	rectifyRGB(imgDark,dst,w,h,RT,1);

	HDRImageGenerate(dst,imgBright,imgDark,w,h);
	EdgCut(imgDark,dst,w,h,RT);


	unsigned char * map = (unsigned char*) malloc(256*sizeof(unsigned char));
	gamaRateHDR(map);
	gamaRect(dst, imgDark, w, h, map);
	HDRImageGenerate1(dst,imgDark,dst,w,h);

	free(map);

	//	int t4 =   getCurrentTime();
	//	LOGW("HDR time  :%d ms\n",t4-t3);
	//enhance
	RGB2GRAY(dst,dark_gray,w*h);
	LocaEnhance(dark_gray,bright_gray, w, h, 2,10);
	ImageChangeY1(dst, bright_gray, w*h);


//	HSI_adjust(dst, w*h, 0.0, 1.5, 1.0);
	usmProcess(dst,w,h,6,0,50);
	//usmProcess(dst,w,h,2,0,50);

	//FilterRGB(dst , imgDark , w, h , laplace, 3);
	//FilterRGB(imgDark , dst , w, h , laplace, 3);
	//FilterRGB(dst , imgDark , w, h , laplace, 3);
	//FilterRGB(imgDark , dst , w, h , laplace, 3);

	//	int t3 =  getCurrentTime();
	//	HSI_adjust(dst, w*h, 0.0, 1.8, 1.0);

	free(dark_gray);
	free(bright_gray);
	free(gray_rectify);
	free(small_gray);

	//	int t4 =   getCurrentTime();
	//	LOGW("HDR time  :%d ms\n",t4-t3);
}
/******************************************************************************/
void gamaRateHDRSimple1(unsigned char * map)//r变换表
{
	int i;
	for (i = 0;i<256;i++)
	{
		map[i] = getMIN(i +50,255);
	}
}
void gamaRateHDRSimple2(unsigned char * map)//r变换表
{
	int i;
	for (i = 0;i<256;i++)
	{
		map[i] = getMAX(i - 0,0);
	}
}
void gamaRectRGB(int * src, int * dst, int w, int h, unsigned char * map )
{
	int size = w*h;
	int i;
	int R,G,B;

	for (i = 0;i<size;i++)
	{
		getRGB(src[i],&R,&G,&B);
		R = map[R];
		G = map[G];
		B = map[B];
		setRGB(&dst[i],R,G,B);
 	}
}

void HDRsimple(int * img, int w, int h, int block , int edg_thre)
{
	LOGW("HDRsimple 1.22\n");
	unsigned char * map = (unsigned char*) malloc(256*sizeof(unsigned char));
	int * img_shadow = (int *) malloc(w*h*sizeof(int));
	int * img_bright = (int *) malloc(w*h*sizeof(int));
 	gamaRateHDRSimple1(map);
	gamaRectRGB(img, img_shadow, w, h, map);
	gamaRateHDRSimple2(map);
	gamaRectRGB(img,img_bright, w, h, map);


	HDRImageGenerate1(img,img_shadow,img_shadow,w,h);
	HDRImageGenerate1(img_bright,img,img_bright,w,h);

	HDRImageGenerate1(img_bright,img_shadow,img,w,h);

	CLAHERGB3(img , w, h, block ,edg_thre);
	free(map);
	free(img_shadow);
	free(img_bright);
}
