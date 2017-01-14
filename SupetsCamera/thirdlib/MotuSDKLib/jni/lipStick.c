#include "lipstick.h"

#define WIDTH 480
#define HEIGHT 360
#define EPS 0.000000001
#define PI  3.1415926

unsigned char *	mask_src;
unsigned char *	mask_small;
static inline int BilinearInsertGRAY(unsigned char *  src, int w, int h, int _x,int _y, unsigned char * g)
{

	int x,y;
	int i,j;
	if (_x<0||_y<0||_x>=w ||_y>=h ) return -1;//边界保护
	i = (int)(_y);
	j = (int)(_x);
	x = _x - (float)j;
	y = _y - (float)i;


	int i_next = getMIN(i+1,h-1);
	int j_next = getMIN(j+1,w-1);

	int tmp =   src[i*w+j]          *(1-x)  *(1-y)
		      + src[i*w+j_next]     *   x   *(1-y)
		      + src[i_next*w+j]     *(1-x)  *   y
		      + src[i_next*w+j_next]*   x   *   y +0.5 ;
	tmp = getMIN(tmp,255);
	tmp = getMAX(tmp,0);
	*g= tmp;

	return 1;

}
void ResizeGRAYBilinear(unsigned char * src, unsigned char * dst, int w_src, int h_src, int w_dst, int h_dst)
{
	int i , j ,flag;
	float x , y;
	unsigned char gray;
	float rate_x = (float)w_src /(float) w_dst;
	float rate_y = (float)h_src / (float)h_dst;

	for (i = 0 ;i<h_dst;i++)
	{
		for (j = 0;j<w_dst;j++)
		{
			x = (float)j * rate_x +0.5;
			y = (float)i * rate_y +0.5;
			//printf("%d,%d,%f,%f\n",j,i,x,y);
			flag = BilinearInsertGRAY(src, w_src, h_src, x,y, &gray);
			if (flag>0)
			{
				dst[i*w_dst+j] = gray;
			}
		}
	}
}
void ResizeGRAYNN(unsigned char * src, unsigned char * dst, int w_src, int h_src, int w_dst, int h_dst)
{
	int i , j ;
	int x , y;
	int gray,flag;
	float rate_x = (float)w_src /(float) w_dst;
	float rate_y = (float)h_src / (float)h_dst;

	for (i = 0 ;i<h_dst;i++)
	{
		for (j = 0;j<w_dst;j++)
		{
			x = (int)((float)j * rate_x +0.5);
			y = (int)((float)i * rate_y +0.5);
			x = getMIN(getMAX(x,0),w_src-1);
			y = getMIN(getMAX(y,0),h_src-1);

			dst[i*w_dst+j] = src[y*w_src + x];
		}
	}
}
///////////////////////////////////////快速均值滤波////////////////////////////////////////////////
static inline void horizontalBlurGRAY(unsigned char * img, unsigned char * tmp, int w, int i, int radius)
{
	int sum = 0;
	int size = radius*2+1;
	int j;
	for (j=0;j<size;j++)
	{
		sum   += img[i*w+j];
	}
	tmp[radius] = sum/size;
	for (j=radius+1;j<w-radius;j++)
	{
		sum    = sum - img[i*w + j - radius - 1]+img[i*w + j+radius];
		tmp[j] = sum/size;
	}
	for (j=radius;j<w-radius;j++)
	{
		img[i*w+j] = tmp[j];
	}
}
static inline void verticalBlurGRAY(unsigned char * img, unsigned char * tmp, int h,int w, int j, int radius)
{
	int sum = 0;
	int size = radius*2+1;
	int i;
	for (i=0;i<size;i++)
	{
		sum   += img[i*w+j];
	}
	tmp[radius] = sum/size;
	for (i=radius+1;i<h-radius;i++)
	{
		sum    = sum - img[(i-radius-1)*w + j]+img[(i+radius)*w + j];
		tmp[i] = sum/size;
	}
	for (i=radius;i<h-radius;i++)
	{
		img[i*w+j] = tmp[i];
	}
}
void fastAverageBlurGRAY(unsigned char * img, int w, int h, int radius)
{
	int i,j;
	int size = getMAX(w,h);
	unsigned char * tmp = (unsigned char*)malloc(size*sizeof(unsigned char));
	for (i = 0;i<h;i++)
	{
		horizontalBlurGRAY(img,tmp,w,i,radius);
	}
	for (j = 0;j<w;j++)
	{
		verticalBlurGRAY(img,tmp,h,w,j,radius);
	}

	free(tmp);
}
///////////////////////////////////////快速均值滤波////////////////////////////////////////////////
static inline void RGB2HSI(int r, int g, int b, double *H, double *S, double *I)
{

	double num   = 0.5*(double)((r-g)+(r-b));
	double den   = sqrt((double)(r-g)*(r-g)+(double)(r-b)*(g-b));
	double theta = acos(num/(den+EPS));

	*H=theta;
	if (b>g) *H=2*PI-(*H);
	*H=*H/(2*PI);

	num=getMIN(getMIN(r,g),b);
	den = r+g+b;
	den =  EPS>den?EPS:den;
	*S=1-3*num/den;
	if (*S == 0) *H = 0;
	*I=(r+g+b)/3;

}
static inline void HSI2RGB(int *r, int *g, int *b, double H, double S, double I)
{

	double R,G,B;
	H *=  2*PI;

	if ((0 <= H) && (H < 2*PI/3))
	{
		B  = I* (1 - S);
		R  = I* (1 + S* cos(H)/cos(PI/3 - H));
		G  = 3*I - (R  + B );
	}
	if ((2*PI/3 <= H) && (H < 4*PI/3) )
	{

		R  = I * (1 - S );
		G  = I * (1 + S * cos(H  - 2*PI/3) /  cos(PI - H ));
		B  = 3*I  - (R  + G );
	}
	if ((4*PI/3 <= H) && (H <= 2*PI))
	{
		G  = I * (1 - S );
		B  = I * (1 + S * cos(H  - 4*PI/3)/cos(5*PI/3 - H));
		R  = 3*I  - (G  + B );
	}

	*r = getMIN((int)(R+0.5),255);
	*g = getMIN((int)(G+0.5),255);
	*b = getMIN((int)(B+0.5),255);
	*r = getMAX(*r,0);
	*g = getMAX(*g,0);
	*b = getMAX(*b,0);
}
void imageRGB2Lab_a(int * image, unsigned char * image_a, int w, int h)
{
	int i, size = w*h;
	int R,G,B,a;
	for (i=0;i<size;i++)
	{
		getRGB(image[i],&R,&G,&B);
 		a = ((377 * (14503 * R - 22218 * G + 7714 * B))>>24) + 128;
		image_a[i] = getMIN(getMAX(a,0),255);
	}
}
int getCircle(int a1,int b1,int a2, int b2, int a3, int b3,
			   int * x0, int * y0, int * r2)
{
	int flag;
	float a,b;//y = ax+b
	if (a3 == a1 || a1 == a2) return -2;
	a = (float)(b3-b1)/(float)(a3-a1);
	b = b1 - a*a1;
	int b2_line = (int)(a*(float)a2 + b+0.5);
	if (b2<b2_line) flag = 1;
	else if (b2>b2_line) flag = -1;
	else  return 0;


	/*************************************************/


	float u,v,k1,k2;


	u = (float)(a1*a1 - a2*a2 + b1*b1 - b2*b2)/2.0f/(float)(a1-a2);
	v = (float)(a1*a1 - a3*a3 + b1*b1 - b3*b3)/2.0f/(float)(a1-a3);

	k1= (float)(b1-b2)/(float)(a1-a2);
	k2= (float)(b1-b3)/(float)(a1-a3);

	if ((k1-k2)==0) return -2;

	*y0 = (int)((u-v)/(k1-k2));
	*x0 = (int)(v-(u-v)*k2/(k1-k2));

	*r2 = (*x0-a1)*(*x0-a1)+(*y0-b1)*(*y0-b1);
	return flag;
}
int getY1(int h, int x, int x0,int y0,int r2, int flag)
{
	int y;
	if (flag == 1)//上半弧
	{
		y = y0 + (int)sqrt((double)(r2 - (x-x0)*(x-x0)));
	}
	else if (flag == -1)
	{
		y = y0 - (int)sqrt((double)(r2 - (x-x0)*(x-x0)));
	}

	y = getMIN(getMAX(y,0),h-1);
	return y;

}

int getY (int h, int x,int x0, int y0, float a)//y-y0 = a*(x-x0)^2
{
	int y;
	y = (int)(a*(float)(x-x0)*(float)(x-x0))+y0;
	y = getMIN(y,h-1);
	y = getMAX(y,0);

	return y;
}
void getParabola(int * p1, int * p3,int x0, int y0, float * a)//y-y0 = a*(x-x0)^2
{
	float a1 = (float)(p1[1]-y0)/(float)(p1[0]-x0)/(float)(p1[0]-x0);
	float a2 = (float)(p3[1]-y0)/(float)(p3[0]-x0)/(float)(p3[0]-x0);
	int y11 = getY(999999,p1[0],x0,y0,a1);
	int y12 = getY(999999,p3[0],x0,y0,a1);
	int diff1 = (y11 - p1[1])*(y11 - p1[1]) + (y12 - p3[1])*(y12 - p3[1]);
	int y21 = getY(999999,p1[0],x0,y0,a2);
	int y22 = getY(999999,p3[0],x0,y0,a2);
	int diff2 = (y21 - p1[1])*(y21 - p1[1]) + (y22 - p3[1])*(y22 - p3[1]);
	if (diff1>=diff2) *a = a1;
	else              *a = a2;
}



void getLip2(int * Pixels, unsigned char * mask, int w, int h , int * p, int * x0, int * y0 , int *r2, int * arc_flag)
{
	int i,j,y[4];
	int hist[256] = {0};
	imageRGB2Lab_a(Pixels,mask,w,h);


	for (i = 0;i<h;i++)
	{
		for (j = 0;j<w;j++)
		{
			hist[mask[i*w+j]]++;
		}
	}
	int gray_min, gray_max;

	int thre_low =w*h - abs((p[7] - p[11] + p[9] - p[5]) * (p[2] - p[0])) ;
	int thre_high = abs((p[7] - p[11] + p[9] - p[5]) * (p[2] - p[0]))*9/10;


	getHist_Min_Max(hist,thre_low ,thre_high , &gray_min, &gray_max);
	contrastMap(hist, gray_min,gray_max);
	for (i = 0;i<w*h;i++)
	{
		mask[i] = hist[mask[i]];
		mask[i] = mask[i]>128?255:0;
	}

	int sub1=0,sub2=0,sub3=0, count1=0, count2=0, count3=0;
	for(j = 0;j<p[0];j++)
	{
		for (i = 0;i<h;i++)
		{
			mask[i*w+j] = 0;
		}
	}
	for(j = p[2]+1;j<w;j++)
	{
		for (i = 0;i<h;i++)
		{
			mask[i*w+j] = 0;
		}
	}
	for (j = p[0];j<=p[2];j++)
	{

		y[0] = getY1(h,j,x0[0],y0[0],r2[0],arc_flag[0]);
		y[1] = getY1(h,j,x0[1],y0[1],r2[1],arc_flag[1]);
		y[2] = getY1(h,j,x0[2],y0[2],r2[2],arc_flag[2]);
		y[3] = getY1(h,j,x0[3],y0[3],r2[3],arc_flag[3]);
		for (i=0;i<y[0];i++) mask[i*w+j] = 0;
		for (i=y[3] +1;i<h;i++)mask[i*w+j] = 0;

		for (i = y[0];i<y[1];i++)
		{
			sub1 += 255 - mask[i*w+j];
			count1 ++;
		}
		for (i = y[1];i<y[2];i++)
		{
			sub2 += mask[i*w+j] - 0;
			count2 ++;
		}
		for (i = y[2];i<=y[3];i++)
		{
			sub3 += 255 - mask[i*w+j];
			count3 ++;
		}

	}
	if (count1>0) sub1 = sub1/count1;
	if (count2>0) sub2 = sub2/count2;
	if (count3>0) sub3 = sub3/count3;


	if (sub1 > 40)
	{
		for (j = p[0];j<=p[2];j++)
		{
			y[0] = getY1(h,j,x0[0],y0[0],r2[0],arc_flag[0]);
			y[1] = getY1(h,j,x0[1],y0[1],r2[1],arc_flag[1]);
			for (i = y[0];i<=y[1];i++)
			{
				mask[i*w+j] = 255;
			}

		}
	}
	if (sub2 > 45)
	{
		for (j = p[0];j<=p[2];j++)
		{
			y[1] = getY1(h,j,x0[1],y0[1],r2[1],arc_flag[1]);
			y[2] = getY1(h,j,x0[2],y0[2],r2[2],arc_flag[2]);

			for (i = y[1];i<=y[2];i++)
			{
				mask[i*w+j] = 0;
			}
		}
	}
	if (sub3 > 55)
	{
		for (j = p[0];j<=p[2];j++)
		{
			y[2] = getY1(h,j,x0[2],y0[2],r2[2],arc_flag[2]);
			y[3] = getY1(h,j,x0[3],y0[3],r2[3],arc_flag[3]);

			for (i = y[2];i<=y[3];i++)
			{
				mask[i*w+j] = 255;
			}
		}
	}
}

void lip_feather(unsigned char * mask_small, int w, int h, int w_src,  int h_src, int * p, int * x0, int * y0 , int *r2, int * arc_flag)
{
	int i,j,y[4];
	int radius = (int)sqrt((double)((p[9] - p[5])*(p[9] - p[5])   + (p[8] - p[4])*(p[8] - p[4])
		+(p[7] - p[11])*(p[7] - p[11]) + (p[6] - p[10])*(p[6] - p[10]))) * h /4/ h_src;


	if ((p[11]-p[9])<7)//close
	{

		fastAverageBlurGRAY(mask_small, w, h, radius);
		fastAverageBlurGRAY(mask_small, w, h, radius);
		fastAverageBlurGRAY(mask_small, w, h, radius);
	}
	else//open
	{
		radius = radius /2 ;
		fastAverageBlurGRAY(mask_small, w, h, radius);

		for (j = p[0]*w/ w_src;j<=p[2]*w/w_src;j++)
		{
			y[1] = getY1(h_src,j * w_src/w,x0[1],y0[1],r2[1],arc_flag[1]) * h / h_src;
			y[2] = getY1(h_src,j * w_src/w,x0[2],y0[2],r2[2],arc_flag[2]) * h / h_src;
			y[0] = getMIN(y[1]+3,y[2]);
			y[3] = getMAX(y[2]-3,y[1]);
			for (i = y[0];i<y[3];i++)
			{
				mask_small[i*w+j] = 0;
			}
		}
		fastAverageBlurGRAY(mask_small, w, h, radius);

		for (j = p[0]*w/ w_src;j<=p[2]*w/w_src;j++)
		{
			y[1] = getY1(h_src,j * w_src/w,x0[1],y0[1],r2[1],arc_flag[1]) * h / h_src;
			y[2] = getY1(h_src,j * w_src/w,x0[2],y0[2],r2[2],arc_flag[2]) * h / h_src;
			y[0] = getMIN(y[1]+3,y[2]);
			y[3] = getMAX(y[2]-3,y[1]);
			for (i = y[0];i<y[3];i++)
			{
				mask_small[i*w+j] = 0;
			}
		}
		fastAverageBlurGRAY(mask_small, w, h, radius);
	}


	int  size = w*h;
	int p1[2] = {10,0};
	int p2[2] = {500,0};
	float a;
	getParabola(p1,p2,255,255,&a);
	for (i = 0;i<size;i++)
	{
		mask_small[i] = getY(99999,mask_small[i],255,155,a);
		mask_small[i] = getMAX(mask_small[i]-10,0);
	}
}
void lip_color(int * Pixels, unsigned char * mask , int w, int h, int * color )
{
	int i, R0,G0,B0 , R,G,B , R1,G1,B1;
	double H0,S0,I0, H1,S1,I1;

	for (  i =0;i<w*h;i++)
	{
		getRGB(color[i],&R0,&G0,&B0);
		RGB2HSI(R0,G0,B0, &H0,&S0,&I0);
		if (mask[i]>0)
		{
			getRGB(Pixels[i],&R,&G,&B);
			RGB2HSI(R,G,B, &H1,&S1,&I1);
  			HSI2RGB(&R1, &G1, &B1, H0, (S1+S0)/2, (I1+I0)/2);

			R = (R*(255-mask[i]) + R1*mask[i])/255;
			G = (G*(255-mask[i]) + G1*mask[i])/255;
			B = (B*(255-mask[i]) + B1*mask[i])/255;
			setRGB(&Pixels[i],R,G,B);
		}
	}
}

int inputCheck(int *p, int w, int h)
{
	int i, flag=0;
	for (  i =0;i<6;i++)
	{
		if (p[i*2]>=0&&p[i*2+1]>=0)flag = 1;
		if (p[i*2]<w&&p[i*2+1]<h)flag = 1;
	}

	if (p[0]<p[4] && p[4]<p[2]) flag = 1;
	if (p[0]<p[8] && p[8]<p[2]) flag = 1;
	if (p[0]<p[10] && p[10]<p[2]) flag = 1;
	if (p[0]<p[6] && p[6]<p[2]) flag = 1;

    if (p[5]<=p[9] && p[9]<=p[11] && p[11]<=p[7]) flag = 1;


	mask_src = (unsigned char*)malloc(w*h*sizeof(unsigned char));
	mask_small = (unsigned char*)malloc(WIDTH*HEIGHT*sizeof(unsigned char));

	return flag;
}
void releaseLIP()
{
	free(mask_src);
	free(mask_small);
}

void getLIP_src(int * p, int * x0,int * y0,int * r2,int * arc_flag)
{

	arc_flag[0] = getCircle(p[0],p[1],p[2],p[3],p[4],p[5],&x0[0],&y0[0],&r2[0]);
	if (arc_flag[0] == 0 )
	{
		p[5]-=3;
		arc_flag[0] = getCircle(p[0],p[1],p[2],p[3],p[4],p[5],&x0[0],&y0[0],&r2[0]);
	}
	arc_flag[1] = getCircle(p[0],p[1],p[2],p[3],p[8],p[9],&x0[1],&y0[1],&r2[1]);
	if (arc_flag[1] == 0 )
	{
		p[9]+=3;
		arc_flag[1] = getCircle(p[0],p[1],p[2],p[3],p[8],p[9],&x0[1],&y0[1],&r2[1]);
	}
	arc_flag[2] = getCircle(p[0],p[1],p[2],p[3],p[10],p[11],&x0[2],&y0[2],&r2[2]);
	if (arc_flag[2] == 0 )
	{
		p[11]-=3;
		arc_flag[2] = getCircle(p[0],p[1],p[2],p[3],p[10],p[11],&x0[2],&y0[2],&r2[2]);
	}
	arc_flag[3] = getCircle(p[0],p[1],p[2],p[3],p[6],p[7],&x0[3],&y0[3],&r2[3]);
	if (arc_flag[3] == 0 )
	{
		p[7]+=3;
		arc_flag[3] = getCircle(p[0],p[1],p[2],p[3],p[6],p[7],&x0[3],&y0[3],&r2[3]);
	}

}
int lipstick(int * Pixels, int * color, int w, int h, int *p)
{
	int t1 = getCurrentTime();
	int flag = inputCheck(p,w,h);
	if (flag == 0)
	{
		releaseLIP();
		return flag;
	}


	int x0[4];
	int y0[4];
	int r2[4];
	int arc_flag[4];
	getLIP_src(p, x0, y0, r2, arc_flag);

	getLip2(Pixels, mask_src, w,h , p, x0, y0 , r2,arc_flag);
	ResizeGRAYNN(mask_src,mask_small,w,h,WIDTH,HEIGHT);

	lip_feather(mask_small,WIDTH,HEIGHT,w,h,p,x0,y0,r2,arc_flag);
	ResizeGRAYBilinear(mask_small,mask_src,WIDTH,HEIGHT,w,h);

	lip_color(Pixels, mask_src , w,h, color );

	releaseLIP();

	int t2 =   getCurrentTime();
	LOGW("lipstick 6.6 time  :%d ms\n",t2-t1);
	return 1;


}
