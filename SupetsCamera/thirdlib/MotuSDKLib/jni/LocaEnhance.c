#include "LocaEnhance.h"


static inline void getHistMinMax(int* Histgram, float CutLimit ,float Contrast , int * min, int * max)
{
	int I, Sum = 0, Amount = 0;
	const int Level = 256;
	for (I = 0; I < Level; I++) Amount += Histgram[I];
	for (I = 0; I < Level; I++)
	{
		Sum = Sum + Histgram[I];
		if (Sum >= Amount * CutLimit )
		{
			* min = I;
			break;
		}
	}
	Sum = 0;
	for(I = Level-1; I >= 0; I--)
	{
		Sum = Sum +Histgram[I];
		if (Sum >= Amount * CutLimit )
		{
			* max = I ;
			break;
		}
	}
	int Delta;
	if (Contrast<0)Delta = 0;
	else Delta = ((* max) - (* min)) * Contrast * 0.5;
	(* min) = (* min) - Delta;
	(* max) = (* max) + Delta ;
	if ((* min) < 0)   (* min) = 0;
	if ((* max) > 255) (* max) = 255;
}
static inline void MakeMappingAutoContrast(int* HistR,int* HistG,int* HistB,float CutLimit ,float Contrast )
{
	int I, Sum = 0, Amount = 0;

	const int Level = 256;
	int MinB =0 ,MaxB=255;
	int Min = 0, Max=255;
	int min, max;
	getHistMinMax(HistR,0.0f,Contrast,&min,&max);
	Min = min;
	Max = max;
	getHistMinMax(HistG,0.0f,Contrast,&min,&max);
	Min = getMIN(min,Min);
	Max = getMAX(max,Max);
	getHistMinMax(HistB,0.0f,Contrast,&min,&max);
	Min = getMIN(min,Min);
	Max = getMAX(max,Max);


	getHistMinMax(HistR,CutLimit,-1,&min,&max);
	MinB = min;
	MaxB = max;
	getHistMinMax(HistG,CutLimit,-1,&min,&max);
	MinB = getMIN(min,MinB);
	MaxB = getMAX(max,MaxB);
	getHistMinMax(HistB,CutLimit,-1,&min,&max);
	MinB = getMIN(min,MinB);
	MaxB = getMAX(max,MaxB);

 	if (MaxB>MinB)
	{
		for (I = 0; I < Level; I++)
		{
			if (I<MinB)
				HistR[I] =Min;
			else if(I>MaxB)
				HistR[I]=Max;
			else
				HistR[I] = (Max-Min)* (I - MinB) / (MaxB - MinB) + Min ;
		}
	}
	else
	{
		for (I = 0; I < Level; I++) HistR[I]=MaxB;
	}
}
static inline void MakeMapping(int* Histgram,float CutLimit ,float Contrast )
{
	int I, Sum = 0, Amount = 0;
	const int Level = 256;
	for (I = 0; I < Level; I++) Amount += Histgram[I];
	int MinB =0 ,MaxB=255;
	int Min = 0,Max=255;
	for (I = 0; I < Level; I++)
	{
		if  (Histgram[I]!=0)
		{
			Min = I ;
			break;
		}
	}

	for(I = Level-1; I >= 0; I--)
	{
		if  (Histgram[I]!=0)
		{
			Max = I ;
			break;
		}
	}
	for (I = 0; I < Level; I++)
	{
		Sum = Sum + Histgram[I];
		if (Sum >= Amount * CutLimit )
		{
			MinB = I;
			break;
		}
	}

	Sum = 0;
	for(I = Level-1; I >= 0; I--)
	{
		Sum = Sum +Histgram[I];
		if (Sum >= Amount * CutLimit )
		{
			MaxB = I ;
			break;
		}
	}
	int Delta = (Max - Min) * Contrast * 0.5  ;
	Min = Min - Delta;
	Max = Max +    Delta ;
	if (Min    < 0) Min = 0;
	if (Max > 255) Max = 255;

	if (MaxB>MinB)
	{
		for (I = 0; I < Level; I++)
		{
			if (I<MinB)
				Histgram[I] =Min;
			else if(I>MaxB)
				Histgram[I]=Max;
			else
				Histgram[I] = (Max-Min)* (I - MinB) / (MaxB - MinB) + Min ;
		}
	}
	else
	{
		for (I = 0; I < Level; I++) Histgram[I]=MaxB;
	}
}

static inline  int getLocaHist(unsigned char * gray,unsigned char * edg, int w, int h, int start_x, int start_y, int end_x, int end_y, int * hist)
{
	memset(hist,0,256*sizeof(int));

	int i,j,sum = 0;
	start_x = getMAX(start_x-w/50,0);
	start_y = getMAX(start_y-h/50,0);
	end_x   = getMIN(end_x+w/50,w);
	end_y   = getMIN(end_y+h/50,h);
	for (i = start_y; i<end_y; i ++)
	{
		for (j = start_x;j<end_x;j++)
		{
			if (edg[i*w+j]==255)
			{
				hist[gray[i*w+j]]++;
				sum++;
			}

		}
	}
	return sum;
}

static inline int getVertex2(int* start_x, int * end_x, int * start_y,int * end_y, int block_num, int x , int y,
							 int* x_node, int* y_node,  int x_node_id, int y_node_id,
							 int * hist_id , int * hist_wei)
{
	int i,j,k,count_x=0,count_y=0,tmp;
	int distance[4];//上下左右
	int hist_idx0,hist_idx1;
	int hist_idy0,hist_idy1;

	count_x = x_node[x_node_id+2]==-1?1:2;
	count_y = y_node[y_node_id+2]==-1?1:2;
	hist_idx0 = x_node[x_node_id+1];
	hist_idx1 = x_node[x_node_id+2];
	hist_idy0 = y_node[y_node_id+1]*block_num;
	hist_idy1 = y_node[y_node_id+2]*block_num;


	/****************************************************************************/
	switch ((count_y-1)*2+count_x-1)
	{
	case 0:
		{
			hist_id [0] = hist_idy0+hist_idx0;
			hist_wei[0] = 256;
		}
		break;
	case 1:
		{
			distance[2] = x - x_node[x_node_id];
			distance[3] = x_node[x_node_id+3] - x;
			tmp = (distance[2]+distance[3]);
			hist_id [0] = hist_idy0+hist_idx0;
			hist_wei[0] = 256*distance[3]/tmp;
			hist_id [1] = hist_idy0+hist_idx1;
			hist_wei[1] = 256*distance[2]/tmp;
		}
		break;
	case 2:
		{
			distance[0] = y - y_node[y_node_id];
			distance[1] = y_node[y_node_id+3] - y;

			tmp = (distance[0]+distance[1]);
			hist_id [0] = hist_idy0+hist_idx0;
			hist_wei[0] = 256*distance[1]/tmp;
			hist_id [1] = hist_idy1+hist_idx0;
			hist_wei[1] = 256*distance[0]/tmp;
		}
		break;
	case 3:
		{
			distance[0] = y - y_node[y_node_id];
			distance[1] = y_node[y_node_id+3] - y;
			distance[2] = x - x_node[x_node_id];
			distance[3] = x_node[x_node_id+3] - x;

			hist_id [0] = hist_idy0+hist_idx0;
			hist_id [1] = hist_idy0+hist_idx1;
			hist_id [2] = hist_idy1+hist_idx0;
			hist_id [3] = hist_idy1+hist_idx1;
			tmp = (distance[2]+distance[3])*(distance[0]+distance[1]);
			hist_wei[0] = 256*distance[3]*distance[1]/tmp;
			hist_wei[1] = 256*distance[2]*distance[1]/tmp;
			hist_wei[2] = 256*distance[3]*distance[0]/tmp;
			hist_wei[3] = 256*distance[2]*distance[0]/tmp;
		}
		break;

	}
	return count_x*count_y;
}
void LocaEnhance(unsigned char * gray,unsigned char * dst, int w, int h, int block_num, int edg_thre)
{
	//LOGW("LocaEnhance gray 01.02\n");
	GetEdg(gray,dst,w,h,edg_thre);
	int i,j,k,tmp;
	int * hist = (int *)malloc(256*block_num*block_num*sizeof(int));
	int * start_x = (int *)malloc(block_num*sizeof(int));
	int * end_x = (int *)malloc(block_num*sizeof(int));
	int * start_y = (int *)malloc(block_num*sizeof(int));
	int * end_y = (int *)malloc(block_num*sizeof(int));

	start_x [0] = 0;
	start_y [0] = 0;


	int hist_id [4];
	int hist_wei[4];

	int num, g;



	for (i = 1;i<block_num;i++)
	{
		start_x [i] = w *i/block_num-w/block_num/2;
		start_y [i] = h *i/block_num-h/block_num/2;
	}
	for (i = 0;i<block_num-1;i++)
	{
		end_x [i] = start_x[i] + w/block_num + w/block_num/2;
		end_y [i] = start_y[i] + h/block_num + h/block_num/2;
	}
	end_x[block_num-1] = w;
	end_y[block_num-1] = h;



	for (i = 0;i<block_num;i++)
	{
		for (j = 0;j<block_num;j++)
		{
			getLocaHist(gray,dst, w,h,start_x[j],start_y[i],end_x[j],end_y[i],&hist[(i*block_num+j)*256]);
			MakeMapping(&hist[(i*block_num+j)*256], 0.01, 1);
		}
	}
	/*************************************************************************/
	int i_node[48];
	int j_node[48];
	int node_count = 0;
	i_node[0] = 0;
	j_node[0] = 0;
	i_node[1] = 0;//block i
	j_node[1] = 0;//block j
	i_node[2] = -1;//block i
	j_node[2] = -1;//block j
	node_count++;
	for (i = 0;i<block_num-1;i++)
	{
		i_node[node_count*3+1] = i;//block i
		j_node[node_count*3+1] = i;//block j
		i_node[node_count*3+2] = i+1;//block i
		j_node[node_count*3+2] = i+1;//block j

		i_node[node_count*3+0] = start_y[i+1];
		j_node[node_count*3+0] = start_x[i+1];
		node_count++;

		i_node[node_count*3+1] = i+1;;//block i
		j_node[node_count*3+1] = i+1;//block j
		i_node[node_count*3+2] = -1;//block i
		j_node[node_count*3+2] = -1;//block j

		i_node[node_count*3+0] = end_y[i];
		j_node[node_count*3+0] = end_x[i];
		node_count++;
	}
	i_node[node_count*3+1] = -1;;//block i
	j_node[node_count*3+1] = -1;//block j
	i_node[node_count*3+2] = -1;//block i
	j_node[node_count*3+2] = -1;//block j
	i_node[node_count*3+0] = h;
	j_node[node_count*3+0] = w;



	int x,y;
	for (i = 0;i<node_count;i++)
	{
		for (j = 0;j<node_count;j++)
		{

			for (y = i_node[i*3+0];y<i_node[(i+1)*3+0];y++)
			{
				for (x= j_node[j*3+0];x<j_node[(j+1)*3+0];x++)
				{
					g = gray[y*w+x];
					num = getVertex2(start_x, end_x, start_y, end_y, block_num, x , y,
						j_node,i_node,j*3,i*3,
						hist_id, hist_wei);
					tmp = 0;

					for (k = 0;k<num;k++)
					{
						tmp+=hist[hist_id[k]*256+g]*hist_wei[k];
					}

					dst[y*w+x] = getMIN((tmp>>8),255);

				}
			}


		}
	}

	free(hist);
	free(start_x);
	free(end_x);
	free(start_y);
	free(end_y);
}

void LocaEnhanceRGB (int * img , int w, int h, int block_num, int edg_thre)//1
{
	 LOGW("LocaEnhanceRGB1 01.13\n");

	unsigned char * gray = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * edg  = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	RGB2GRAY(img,gray,w*h);
	LocaEnhance(gray,edg, w, h, block_num, edg_thre);
	ImageChangeY1(img, edg, w*h);
	free(gray);
	free(edg);

}
void LocaEnhanceRGB3(int * img , int w, int h, int block_num, int edg_thre)//3
{
	 LOGW("LocaEnhanceRGB3 01.13\n");
	unsigned char * R = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * G = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * B = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * dst  = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * gray = (unsigned char * )malloc(w*h*sizeof(unsigned char));

	int tmpR,tmpG,tmpB,num;
	int i,j,k,tmp,size = w*h;
	int r,g,b;
	for (i = 0;i<size;i++)
	{
		getRGB(img[i],&r,&g,&b);
		R[i] = r;
		G[i] = g;
		B[i] = b;
		gray[i] = (r+g+b)/3;
	}

	GetEdg(gray,dst,w,h,edg_thre);

	int * histR = (int *)malloc(256*block_num*block_num*sizeof(int));
	int * histG = (int *)malloc(256*block_num*block_num*sizeof(int));
	int * histB = (int *)malloc(256*block_num*block_num*sizeof(int));
	int * start_x = (int *)malloc(block_num*sizeof(int));
	int * end_x = (int *)malloc(block_num*sizeof(int));
	int * start_y = (int *)malloc(block_num*sizeof(int));
	int * end_y = (int *)malloc(block_num*sizeof(int));

	start_x [0] = 0;
	start_y [0] = 0;

	int hist_id [4];
	int hist_wei[4];

	for (i = 1;i<block_num;i++)
	{
		start_x [i] = w *i/block_num-w/block_num/2;
		start_y [i] = h *i/block_num-h/block_num/2;
	}
	for (i = 0;i<block_num-1;i++)
	{
		end_x [i] = start_x[i] + w/block_num + w/block_num/2;
		end_y [i] = start_y[i] + h/block_num + h/block_num/2;
	}
	end_x[block_num-1] = w;
	end_y[block_num-1] = h;



	for (i = 0;i<block_num;i++)
	{
		for (j = 0;j<block_num;j++)
		{
			getLocaHist(R,dst, w,h,start_x[j],start_y[i],end_x[j],end_y[i],&histR[(i*block_num+j)*256]);
			getLocaHist(G,dst, w,h,start_x[j],start_y[i],end_x[j],end_y[i],&histG[(i*block_num+j)*256]);
			getLocaHist(B,dst, w,h,start_x[j],start_y[i],end_x[j],end_y[i],&histB[(i*block_num+j)*256]);
			MakeMappingAutoContrast(&histR[(i*block_num+j)*256],
				                    &histG[(i*block_num+j)*256],
									&histB[(i*block_num+j)*256],0.01 ,1 );
 		}
	}
	/*************************************************************************/
	int i_node[48];
	int j_node[48];
	int node_count = 0;
	i_node[0] = 0;
	j_node[0] = 0;
	i_node[1] = 0;//block i
	j_node[1] = 0;//block j
	i_node[2] = -1;//block i
	j_node[2] = -1;//block j
	node_count++;
	for (i = 0;i<block_num-1;i++)
	{
		i_node[node_count*3+1] = i;//block i
		j_node[node_count*3+1] = i;//block j
		i_node[node_count*3+2] = i+1;//block i
		j_node[node_count*3+2] = i+1;//block j

		i_node[node_count*3+0] = start_y[i+1];
		j_node[node_count*3+0] = start_x[i+1];
		node_count++;

		i_node[node_count*3+1] = i+1;;//block i
		j_node[node_count*3+1] = i+1;//block j
		i_node[node_count*3+2] = -1;//block i
		j_node[node_count*3+2] = -1;//block j

		i_node[node_count*3+0] = end_y[i];
		j_node[node_count*3+0] = end_x[i];
		node_count++;
	}
	i_node[node_count*3+1] = -1;;//block i
	j_node[node_count*3+1] = -1;//block j
	i_node[node_count*3+2] = -1;//block i
	j_node[node_count*3+2] = -1;//block j
	i_node[node_count*3+0] = h;
	j_node[node_count*3+0] = w;



	int x,y;
	for (i = 0;i<node_count;i++)
	{
		for (j = 0;j<node_count;j++)
		{

			for (y = i_node[i*3+0];y<i_node[(i+1)*3+0];y++)
			{
				for (x= j_node[j*3+0];x<j_node[(j+1)*3+0];x++)
				{
					r = R[y*w+x];
					g = G[y*w+x];
					b = B[y*w+x];
					num = getVertex2(start_x, end_x, start_y, end_y, block_num, x , y,
						             j_node,i_node,j*3,i*3,
						             hist_id, hist_wei);
					tmpR = 0;tmpG = 0;tmpB = 0;

					for (k = 0;k<num;k++)
					{
						tmpR+=histR[hist_id[k]*256+r]*hist_wei[k];
						tmpG+=histR[hist_id[k]*256+g]*hist_wei[k];
						tmpB+=histR[hist_id[k]*256+b]*hist_wei[k];
					}

					tmpR = getMIN((tmpR>>8),255);
					tmpG = getMIN((tmpG>>8),255);
					tmpB = getMIN((tmpB>>8),255);
					setRGB(&img[y*w+x],tmpR,tmpG,tmpB);

				}
			}


		}
	}


	free(start_x);
	free(end_x);
	free(start_y);
	free(end_y);

}
void LocaEnhanceRGB2(int * img , int w, int h, int block_num, int edg_thre)//2
{
	LOGW("LocaEnhanceRGB2 01.13\n");

	unsigned char * R = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * G = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * B = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * edg  = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * tmp;

	int i,size = w*h;
	int r,g,b;
	for (i = 0;i<size;i++)
	{
		getRGB(img[i],&r,&g,&b);
		R[i] = r;
		G[i] = g;
		B[i] = b;
	}
	LocaEnhance(R,edg, w, h, block_num, edg_thre);
	tmp = R;
	R = edg;
	edg = tmp;
	LocaEnhance(G,edg, w, h, block_num, edg_thre);
	tmp = G;
	G = edg;
	edg = tmp;
	LocaEnhance(B,edg, w, h, block_num, edg_thre);
	tmp = B;
	B = edg;
	edg = tmp;

	for (i = 0;i<size;i++)
	{
		setRGB(&img[i],R[i],G[i],B[i]);
	}

	free(R);
	free(G);
	free(B);
	free(edg);

}
/*******************************************************************************/
int get_hist_sum(int *hist)
{
	int sum = 0;

	int i;
	for (i = 0;i<256;i++)
	{
		sum += hist[i];

	}
	return sum;
}

int ClipHist(int * hist,int alpha, int Smax, int stop_thre)//限制对比度自适应直方图均衡化
{
	int sum = 0;
	int max = 0;
	int i;
	for (i = 0;i<256;i++)
	{
		sum += hist[i];
		max = getMAX(hist[i],max);
	}
	int clip = sum *(1+alpha*(Smax-1)/100)/256;//截断受限值
	if ((max - clip)<stop_thre)
	{
		return sum;
	}
	else
	{
		sum = 0 ;
		for (i = 0;i<256;i++)
		{
			if (hist[i]>clip)
			{
				sum += (hist[i]- clip);
				hist[i] = clip;

			}
		}
		sum /= 256;
		for (i = 0;i<256;i++)
		{
			hist[i] += sum;
		}
		ClipHist(hist,alpha,Smax,stop_thre);

	}
}

void AHE_hist_reset(int * hist, int block_num )
{
	int i,j,k,ii,jj,x,y;
	int * hist_dst = (int*)malloc(block_num * block_num * 256*sizeof (int));
	memset(hist_dst,0,block_num * block_num * 256*sizeof (int));
	for (i = 0;i<block_num;i++)
	{
		for (j = 0;j<block_num;j++)
		{
			for (ii = -1;ii<2;ii++)
			{
				if ((i+ii)<0||(i+ii)>=block_num)
				{
					continue;
				}
				for (jj = -1;jj<2;jj++)
				{
					if ((j+jj)<0||(j+jj)>=block_num)
					{
						continue;
					}
					x = j+jj;
					y = i+ii;
					for ( k = 0;k<256;k++)
					{
						hist_dst[(i*block_num+j)*256+k]+=hist[(y*block_num+x)*256+k];
					}
				}
			}
		}
 	}
	memcpy(hist,hist_dst,block_num * block_num * 256*sizeof (int));
	free(hist_dst);
}
void CLAHE_GRAY(unsigned char * gray,unsigned char * dst, int w, int h, int block_num, int edg_thre)
{
	//LOGW("LocaEnhance gray 01.02\n");
	if (edg_thre>0)
	{
		GetEdg(gray,dst,w,h,edg_thre);
	}
 	int i,j,k,tmp;
	int * hist = (int *)malloc(256*block_num*block_num*sizeof(int));
	int * hist_sum = (int *)malloc(block_num*block_num*sizeof(int));
	int * start_x = (int *)malloc(block_num*sizeof(int));
	int * end_x = (int *)malloc(block_num*sizeof(int));
	int * start_y = (int *)malloc(block_num*sizeof(int));
	int * end_y = (int *)malloc(block_num*sizeof(int));

	start_x [0] = 0;
	start_y [0] = 0;


	int hist_id [4];
	int hist_wei[4];

	int num, g;



	for (i = 1;i<block_num;i++)
	{
		start_x [i] = w *i/block_num-w/block_num/2;
		start_y [i] = h *i/block_num-h/block_num/2;
	}
	for (i = 0;i<block_num-1;i++)
	{
		end_x [i] = start_x[i] + w/block_num + w/block_num/2;
		end_y [i] = start_y[i] + h/block_num + h/block_num/2;
	}
	end_x[block_num-1] = w;
	end_y[block_num-1] = h;



	for (i = 0;i<block_num;i++)
	{
		for (j = 0;j<block_num;j++)
		{
			getLocaHist(gray,dst, w,h,start_x[j],start_y[i],end_x[j],end_y[i],&hist[(i*block_num+j)*256]);
			hist_sum[i*block_num+j] = ClipHist(&hist[(i*block_num+j)*256],70,3,2);
			//hist_sum[i*block_num+j] = get_hist_sum(&hist[(i*block_num+j)*256]);
			//if (hist_sum[i*block_num+j] >0)
			//{
			//	ClipHistogram (&hist[(i*block_num+j)*256], 256, 0.01,hist_sum[i*block_num+j] );

				HistMap(&hist[(i*block_num+j)*256],&hist[(i*block_num+j)*256],hist_sum[i*block_num+j]);
			//}

		}
	}
	AHE_hist_reset(hist, block_num );
	/*************************************************************************/
	int i_node[48];
	int j_node[48];
	int node_count = 0;
	i_node[0] = 0;
	j_node[0] = 0;
	i_node[1] = 0;//block i
	j_node[1] = 0;//block j
	i_node[2] = -1;//block i
	j_node[2] = -1;//block j
	node_count++;
	for (i = 0;i<block_num-1;i++)
	{
		i_node[node_count*3+1] = i;//block i
		j_node[node_count*3+1] = i;//block j
		i_node[node_count*3+2] = i+1;//block i
		j_node[node_count*3+2] = i+1;//block j

		i_node[node_count*3+0] = start_y[i+1];
		j_node[node_count*3+0] = start_x[i+1];
		node_count++;

		i_node[node_count*3+1] = i+1;;//block i
		j_node[node_count*3+1] = i+1;//block j
		i_node[node_count*3+2] = -1;//block i
		j_node[node_count*3+2] = -1;//block j

		i_node[node_count*3+0] = end_y[i];
		j_node[node_count*3+0] = end_x[i];
		node_count++;
	}
	i_node[node_count*3+1] = -1;;//block i
	j_node[node_count*3+1] = -1;//block j
	i_node[node_count*3+2] = -1;//block i
	j_node[node_count*3+2] = -1;//block j
	i_node[node_count*3+0] = h;
	j_node[node_count*3+0] = w;



	int x,y;
	for (i = 0;i<node_count;i++)
	{
		for (j = 0;j<node_count;j++)
		{

			for (y = i_node[i*3+0];y<i_node[(i+1)*3+0];y++)
			{
				for (x= j_node[j*3+0];x<j_node[(j+1)*3+0];x++)
				{
					g = gray[y*w+x];
					num = getVertex2(start_x, end_x, start_y, end_y, block_num, x , y,
						j_node,i_node,j*3,i*3,
						hist_id, hist_wei);
					tmp = 0;

					for (k = 0;k<num;k++)
					{
						tmp+=hist[hist_id[k]*256+g]*hist_wei[k];
					}

					gray[y*w+x] = getMIN((tmp>>8),255);
					gray[y*w+x] = getMAX(gray[y*w+x],0);

				}
			}


		}
	}

	free(hist);
	free(start_x);
	free(end_x);
	free(start_y);
	free(end_y);
}

void CLAHE_RGB1(int * img , int w, int h, int block_num, int edg_thre)//1
{
	//LOGW("LocaEnhanceRGB 01.02\n");

	unsigned char * gray = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * edg  = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	RGB2GRAY(img,gray,w*h);
	GetEdg(gray,edg,w,h,edg_thre);
 	CLAHE_GRAY(gray,edg, w, h, block_num, -1);
	ImageChangeY1(img, gray, w*h);
	free(gray);
	free(edg);

}
void CLAHE_RGB2(int * img , int w, int h, int block_num, int edg_thre)//2
{
	//LOGW("LocaEnhanceRGB 01.02\n");

	unsigned char * R = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * G = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * B = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * edg  = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * gray = (unsigned char * )malloc(w*h*sizeof(unsigned char));

	RGB2GRAY(img,gray,w*h);
	GetEdg(gray,edg,w,h,edg_thre);

	int i,size = w*h;
	int r,g,b;
	for (i = 0;i<size;i++)
	{
		getRGB(img[i],&r,&g,&b);
		R[i] = r;
		G[i] = g;
		B[i] = b;
	}

	CLAHE_GRAY(R,edg, w, h, block_num, -1);
	//showGRAY(edg,w,h,"edg",1,0,0,0);
 	CLAHE_GRAY(G,edg, w, h, block_num, -1);
	//showGRAY(edg,w,h,"edg",1,0,0,0);
 	CLAHE_GRAY(B,edg, w, h, block_num, -1);
 //showGRAY(edg,w,h,"edg",1,0,0,0);
	for (i = 0;i<size;i++)
	{
		setRGB(&img[i],R[i],G[i],B[i]);
	}

	free(R);
	free(G);
	free(B);
	free(edg);

}
void smooth(unsigned char * src, unsigned char * dst, int w, int h, int thre)
{
	int i,j,ii,jj,sum;
	for (i = 1; i<h-1;i++)
	{
		for (j = 1;j<w-1;j++)
		{
			sum = 0;
			for (ii = -1;ii<2;ii++)
			{
				for (jj = -1;jj<2;jj++)
				{
					sum += src[(i+ii)*w+j+jj];
				}
			}
			if (sum<thre)
			{
				dst[i*w+j] = 0;
			}
			else
				dst[i*w+j] = src[i*w+j];
		}
	}
}
void CLAHERGB3(int * img , int w, int h, int block_num, int edg_thre)//3
{
	LOGW("CLAHERGB3 01.22\n");
	unsigned char * gray = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * edg  = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	unsigned char * edg1  = (unsigned char * )malloc(w*h*sizeof(unsigned char));
	RGB2GRAY(img,gray,w*h);
	GetEdg(gray,edg1,w,h,edg_thre);
	smooth(edg1,edg,w,h,255*3);
	free(edg1);


	int i,j,k,tmp,hist_sum;
	int * hist = (int *)malloc(256*block_num*block_num*sizeof(int));

	int * start_x = (int *)malloc(block_num*sizeof(int));
	int * end_x = (int *)malloc(block_num*sizeof(int));
	int * start_y = (int *)malloc(block_num*sizeof(int));
	int * end_y = (int *)malloc(block_num*sizeof(int));

	start_x [0] = 0;
	start_y [0] = 0;


	int hist_id [4];
	int hist_wei[4];

	int num;
	for (i = 1;i<block_num;i++)
	{
		start_x [i] = w *i/block_num-w/block_num/2;
		start_y [i] = h *i/block_num-h/block_num/2;
	}
	for (i = 0;i<block_num-1;i++)
	{
		end_x [i] = start_x[i] + w/block_num + w/block_num/2;
		end_y [i] = start_y[i] + h/block_num + h/block_num/2;
	}
	end_x[block_num-1] = w;
	end_y[block_num-1] = h;

	for (i = 0;i<block_num;i++)
	{
		for (j = 0;j<block_num;j++)
		{
			 getLocaHist(gray,edg, w,h,start_x[j],start_y[i],end_x[j],end_y[i],&hist[(i*block_num+j)*256]);
 		}
	}
	free(gray);
	free(edg);
	AHE_hist_reset(hist, block_num);
	for (i = 0;i<block_num;i++)
	{
		for (j = 0;j<block_num;j++)
		{
			hist_sum = ClipHist(&hist[(i*block_num+j)*256],80,4,2);
			HistMap(&hist[(i*block_num+j)*256],&hist[(i*block_num+j)*256],hist_sum);//hdr,histmap有改动
		}
	}


	/*************************************************************************/
	int i_node[48];
	int j_node[48];
	int node_count = 0;
	i_node[0] = 0;
	j_node[0] = 0;
	i_node[1] = 0;//block i
	j_node[1] = 0;//block j
	i_node[2] = -1;//block i
	j_node[2] = -1;//block j
	node_count++;
	for (i = 0;i<block_num-1;i++)
	{
		i_node[node_count*3+1] = i;//block i
		j_node[node_count*3+1] = i;//block j
		i_node[node_count*3+2] = i+1;//block i
		j_node[node_count*3+2] = i+1;//block j

		i_node[node_count*3+0] = start_y[i+1];
		j_node[node_count*3+0] = start_x[i+1];
		node_count++;

		i_node[node_count*3+1] = i+1;;//block i
		j_node[node_count*3+1] = i+1;//block j
		i_node[node_count*3+2] = -1;//block i
		j_node[node_count*3+2] = -1;//block j

		i_node[node_count*3+0] = end_y[i];
		j_node[node_count*3+0] = end_x[i];
		node_count++;
	}
	i_node[node_count*3+1] = -1;;//block i
	j_node[node_count*3+1] = -1;//block j
	i_node[node_count*3+2] = -1;//block i
	j_node[node_count*3+2] = -1;//block j
	i_node[node_count*3+0] = h;
	j_node[node_count*3+0] = w;

	free(start_x);
	free(end_x);
	free(start_y);
	free(end_y);

	int x,y;
	int r,g,b;
	int tmp_r,tmp_g,tmp_b;
	for (i = 0;i<node_count;i++)
	{
		for (j = 0;j<node_count;j++)
		{

			for (y = i_node[i*3+0];y<i_node[(i+1)*3+0];y++)
			{
				for (x= j_node[j*3+0];x<j_node[(j+1)*3+0];x++)
				{
					//g = gray[y*w+x];
					getRGB(img[y*w+x],&r,&g,&b);
					num = getVertex2(start_x, end_x, start_y, end_y, block_num, x , y,
						             j_node,i_node,j*3,i*3,
						             hist_id, hist_wei);
					tmp_r = 0;
					tmp_g = 0;
					tmp_b = 0;

					for (k = 0;k<num;k++)
					{
						tmp_r+=hist[hist_id[k]*256+r]*hist_wei[k];
						tmp_g+=hist[hist_id[k]*256+g]*hist_wei[k];
						tmp_b+=hist[hist_id[k]*256+b]*hist_wei[k];
					}

					tmp_r = getMIN((tmp_r>>8),255);
					//tmp_r = getMAX(tmp_r,0);
					tmp_g = getMIN((tmp_g>>8),255);
					//tmp_g = getMAX(tmp_g,0);
					tmp_b = getMIN((tmp_b>>8),255);
					//tmp_b = getMAX(tmp_b,0);
					setRGB(&img[y*w+x],tmp_r,tmp_g,tmp_b);

				}
			}


		}
	}
   	free(hist);
}
