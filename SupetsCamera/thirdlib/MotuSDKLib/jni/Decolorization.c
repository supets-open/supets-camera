#include "Decolorization.h"



#define BLACK   20
#define YELLOW  70
#define CALIB_W 64
#define CALIB_H 64

int count = 0;
int weight[66][3];
int best_weight_id;

void RGB2Lab(double R, double G, double B, double *L, double *a, double *b)
{
	double X, Y, Z, fX, fY, fZ;

	X = 0.412453*R + 0.357580*G + 0.180423*B;
	Y = 0.212671*R + 0.715160*G + 0.072169*B;
	Z = 0.019334*R + 0.119193*G + 0.950227*B;

	X /= (255 * 0.950456);
	Y /=  255;
	Z /= (255 * 1.088754);


	if (Y > 0.008856)
	{
		fY = pow(Y, 1.0/3.0);
		*L = 116.0*fY -16.0 ;
	}
	else
	{
		fY = 7.787*Y + 16.0/116.0;
		*L = 903.3*fY;
	}

	if (X > 0.008856) fX = pow(X, 1.0/3.0);
	else              fX = 7.787*X + 16.0/116.0;

	if (Z > 0.008856) fZ = pow(Z, 1.0/3.0);
	else              fZ =7.787 *Z + 16.0/116.0;

	*a = 500.0*(fX-fY);
	*b = 200.0*(fY-fZ);

	if (*L < BLACK)
	{
		*a *= exp((*L - BLACK) / (BLACK / 4));
		*b *= exp((*L - BLACK) / (BLACK / 4));
		*L = BLACK;
	}
	if (*b > YELLOW) *b = YELLOW;

}

void Permutation(int * arry, int m, int n, int * dst, int dst_num)// 排列 A（m、n）m = arry_num
{
	int i;
	if (n==0)
	{
		int sum = 0;
		for (i =0;i<dst_num;i++)
		{
			sum += dst[i];
		}
		if (sum == m-1)
		{
			memcpy(weight[count],dst,dst_num*sizeof(int));
			count ++;
		}

	}
	else
	{
		for (i =0;i<m;i++)
		{
			dst[dst_num - n] = arry[i];
			Permutation(arry,m,n-1,dst,dst_num);
		}
	}
}
double EnergyCalcu(double * polyGrad, double * wei, double Cg, double sigma)
{
	int i;
	double E ;
	double P=0;
	for (i = 0;i<3;i++)
	{
		P += polyGrad[i]*wei[i];
	}
	E = -1*log(exp(-1* (P - Cg) * (P - Cg)/sigma) + exp(-1* (P + Cg) * (P + Cg)/sigma));
	return E;
}
double CgCalcu(double L,double a,double b,double L1,double a1,double b1)
{
	double Cg = sqrt((L-L1)*(L-L1)+(a-a1)*(a-a1)+(b-b1)*(b-b1))/100.0;
	return Cg;
}
int EnergyMean(int * image,int w, int h, double wei[66][3], double sigma)
{
	int R, G, B, R1, G1, B1;
	double L,a,b,L1,a1,b1;

	double E_mean[66] = {0.0};
	double E;
	double polyGrad[3];
	double Cg;

	double * Lab = (double *)malloc(w*3*sizeof(double));
	int i,j,k;
	double E_min = 11111110;
	int id;

	for ( j = 0;j<w-1;j++)
	{
		getRGB(image[j],&R,&G,&B);
		RGB2Lab((double)R, (double)G, (double)B, &L1, &a1, &b1);
		id = j*3;
		Lab[id ] = L1;
		Lab[id+1] = a1;
		Lab[id+2] = b1;
	}
	for ( i = 1;i < h ;i++)
	{
		for ( j = 0;j<w-1;j++)
		{
			id = i*w+j;
			getRGB(image[id],&R,&G,&B);
			getRGB(image[id+1],&R1,&G1,&B1);


			RGB2Lab((double)R, (double)G, (double)B, &L, &a, &b);
			RGB2Lab((double)R1, (double)G1, (double)B1, &L1, &a1, &b1);


			polyGrad[0] = (double)(R-R1)/255.0;
			polyGrad[1] = (double)(G-G1)/255.0;
			polyGrad[2] = (double)(B-B1)/255.0;

			Cg = CgCalcu(L,a,b,L1,a1,b1);
			for ( k=0;k<66;k++)
			{
				E = EnergyCalcu(polyGrad, &wei[k][0], Cg, sigma);
				E_mean[k] +=  (E);

			}

			R1 = R;
			G1 = G;
			B1 = B;

			getRGB(image[id-w],&R,&G,&B);

			polyGrad[0] = (double)(R-R1)/255.0;
			polyGrad[1] = (double)(G-G1)/255.0;
			polyGrad[2] = (double)(B-B1)/255.0;


			id = j*3;

			Cg = CgCalcu(L,a,b, Lab[id], Lab[id+1], Lab[id+2]);
			for ( k=0;k<66;k++)
			{
				E = EnergyCalcu(polyGrad, &wei[k][0], Cg, sigma);
				E_mean[k] +=  (E);

			}
			Lab[id] = L;
			Lab[id+1] = a;
			Lab[id+2] = b;
		}
	}
	for (k=0;k<66;k++)
	{
		if ( E_mean[k] < E_min)
		{
			E_min = E_mean[k];
			id = k;
		}
	}
	free(Lab);
	return id;

}


void ResizeRGBNN(int * src, int * dst, int w_src, int h_src, int w_dst, int h_dst)
{

	int i , j ;

	for (i = 0 ;i<h_dst;i++)
	{
		for (j = 0;j<w_dst;j++)
		{

			dst[i*w_dst + j] = src[(i * h_src / h_dst) * w_src + (j * w_src / w_dst)];
		}
	}
}
void setupDecolorization(int * image, int w, int h)//rgb2gray主函数入口
{
	LOGW("Decolorization\n");
	count = 0;
	best_weight_id = 0;
	int arry[11];
	int dst[3];
	int i,j;
	for (i = 0;i<11;i++)
	{
		arry[i] = i;
	}
	Permutation(arry,11,3,dst,3);
	double wei[66][3];
	for (i = 0;i<66;i++)
	{
		for (j = 0;j<3;j++)
		{
			wei[i][j] = (double)weight[i][j]/10.0;
		}
	}

	if (w>CALIB_W||h>CALIB_H)
	{
		int * img_small = (int *)malloc(CALIB_W*CALIB_H*sizeof(int));
		ResizeRGBNN(image,img_small,w,h,CALIB_W,CALIB_H);
		best_weight_id = EnergyMean(img_small, CALIB_W,  CALIB_H, wei, 0.02);
		free(img_small);
	}
	else
	{
		best_weight_id = EnergyMean(image, w,  h, wei, 0.02);
	}
	LOGW("id :%d , wei, (%lf, %lf, %lf)\n",best_weight_id, wei[best_weight_id][0],wei[best_weight_id][1],wei[best_weight_id][2]);


 	//printf("best_weight_id :%d\n",best_weight_id);

	//RGB2GRAY1(image,w,h,&weight[best_weight_id][0]);

}
void Decolorization(int * image, int size)
{
	int i;
	int R,G,B,gray ;
	for (i = 0;i<size;i++)
	{
		getRGB(image[i],&R,&G,&B);
		R = R*weight[best_weight_id][0];
		G = G*weight[best_weight_id][1];
		B = B*weight[best_weight_id][2];
		gray = (R+G+B)/10;
		setRGB(&image[i],gray,gray,gray);

	}
}
