#include "mtprocessor.h"

#define min(a,b) ((a)<(b)?(a):(b))
#define max(a,b) ((a)>(b)?(a):(b))

// -----------------------------edit functions----------------------------------
//-------直方图均衡化 -----------------------------
void equlizeHist(int* pixels, int w, int h) {
	int cdf[256], ht[256];
	int size = w * h;
	int y, u, v, a;
	int i;
	int min;
	memset(cdf, 0, 256 * sizeof(int));
	convertRgb2Yuv(pixels, w, h);
	for (i = 0; i < size; i ++) {
		getRGBA(pixels[i], &y, &u, &v, &a);
		cdf[y] ++;
	}
	min = cdf[0];
	for (i = 1; i < 256; i ++) {
		cdf[i] += cdf[i - 1];
		if (cdf[i] < min) {
			min = cdf[i];
		}
	}
	for (i = 0; i < 256; i ++) {
		ht[i] = (cdf[i] - min) * 255 / (size - min);
	}
	for (i = 0; i < size; i ++) {
		getRGBA(pixels[i], &y, &u, &v, &a);
		y = ht[y];
		pixels[i] = (a << 24) + (y << 16) + (u << 8) + v;
	}
	convertYuv2Rgb(pixels, w, h);
}

//-------亮度正规化-----------------------------
void normalization(int* pixels, int w, int h, int clipShadows, int clipHighlights) {
	int hY[256], hU[256], hV[256];
	int size = w * h;
	int y, u, v, a;
	int i;
	int minY = -1, maxY = 256;
	int shadows = size * clipShadows / 10000;
	int highlights = size * clipHighlights / 10000;
	convertRgb2Yuv(pixels, w, h);
	LOGW("shadows:%d  highlights:%d", shadows, highlights);
	makeHistogram(pixels, w, h, hY, hU, hV);
	for (i = 0; i < 127; i ++) {
		if (minY == -1 && hY[i] > shadows) {
			minY = i;
		}
		if (maxY == 256 && hY[255 - i] > highlights) {
			maxY = 255 - i;
		}
	}
	minY = gan(minY);
	maxY = gan(maxY);
	LOGW("%d %d", minY, maxY);
	for (i = 0; i < size; i ++) {
		getRGBA(pixels[i], &y, &u, &v, &a);
		if (y < minY) {
			y = minY;
		}
		if (y > maxY) {
			y = maxY;
		}
		y = (y - minY) * 255 / (maxY - minY);
		pixels[i] = (a << 24) + (y << 16) + (u << 8) + v;
	}
	convertYuv2Rgb(pixels, w, h);
}

//-------自动对比度 -----------------------------
void autoContrast2(int* pixels, int w, int h, int clipShadows, int clipHighlights) {
	int hR[256], hG[256], hB[256];
	int size = w * h;
	int r, g, b, a;
	int i;
	int minR = -1, maxR = 256, minG = -1, maxG = 256, minB = -1, maxB = 256;
	int shadows = size * clipShadows / 10000;
	int highlights = size * clipHighlights / 10000;
	LOGW("shadows:%d  highlights:%d", shadows, highlights);
	makeHistogram(pixels, w, h, hR, hG, hB);
	for (i = 0; i < 127; i ++) {
		if (minR == -1 && hR[i] > shadows) {
			minR = i;
		}
		if (maxR == 256 && hR[255 - i] > highlights) {
			maxR = 255 - i;
		}
		if (minG == -1 && hG[i] > shadows) {
			minG = i;
		}
		if (maxG == 256 && hG[255 - i] > highlights) {
			maxG = 255 - i;
		}
		if (minB == -1 && hB[i] > shadows) {
			minB = i;
		}
		if (maxB == 256 && hB[255 - i] > highlights) {
			maxB = 255 - i;
		}
	}
	minR = gan(minR);
	maxR = gan(maxR);
	minG = gan(minG);
	maxG = gan(maxG);
	minB = gan(minB);
	maxB = gan(maxB);
	LOGW("%d %d %d %d %d %d", minR, maxR, minG, maxG, minB, maxB);
	for (i = 0; i < size; i ++) {
		getRGBA(pixels[i], &r, &g, &b, &a);
		if (r < minR) {
			r = minR;
		}
		if (r > maxR) {
			r = maxR;
		}
		if (g < minG) {
			g = minG;
		}
		if (g > maxG) {
			g = maxG;
		}
		if (b < minB) {
			b = minB;
		}
		if (b > maxB) {
			b = maxB;
		}
		r = (r - minR) * 255 / (maxR - minR);
		g = (g - minG) * 255 / (maxG - minG);
		b = (b - minB) * 255 / (maxB - minB);
		pixels[i] = (a << 24) + (r << 16) + (g << 8) + b;
	}
}

//-------自动颜色 -----------------------------
void autoColor(int* pixels, int w, int h, int clipShadows, int clipHighlights) {
	int hY[256], hU[256], hV[256];
	int size = w * h;
	int r, g, b, a, y;
	int i;
	int minY = -1, maxY = 256, midY;
	int aveMinR = 0, aveMinG = 0, aveMinB = 0, nMin = 0;
	int aveMaxR = 0, aveMaxG = 0, aveMaxB = 0, nMax = 0;
	int aveMidR = 0, aveMidG = 0, aveMidB = 0, nMid = 0;
	int shadows = size * clipShadows / 10000;
	int highlights = size * clipHighlights / 10000;
	LOGW("shadows:%d  highlights:%d", shadows, highlights);
	convertRgb2Yuv(pixels, w, h);
	makeHistogram(pixels, w, h, hY, hU, hV);
	for (i = 0; i < 127; i ++) {
		if (minY == -1 && hY[i] > shadows) {
			minY = i;
		}
		if (maxY == 256 && hY[255 - i] > highlights) {
			maxY = 255 - i;
		}
	}
	minY = gan(minY);
	maxY = gan(maxY);
	midY = (minY + maxY) / 2;
	LOGW("%d %d %d", minY, maxY, midY);
	convertYuv2Rgb(pixels, w, h);
	for (i = 0; i < size; i ++) {
		getRGBA(pixels[i], &r, &g, &b, &a);
		y = 0.299 * r + 0.587 * g + 0.114 * b;
		if (y <= minY) {
			aveMinR += r;
			aveMinG += g;
			aveMinB += b;
			nMin ++;
		} else if (y >= maxY) {
			aveMaxR += r;
			aveMaxG += g;
			aveMaxB += b;
			nMax ++;
		} else if (y == midY) {
			aveMidR += r;
			aveMidG += g;
			aveMidB += b;
			nMid ++;
		}
	}
	if (nMin <= 0) {
		nMin = 1;
	}
	if (nMax <= 0) {
		nMax = 1;
	}
	LOGW("%d %d", nMin, nMax);
	aveMinR /= nMin;
	aveMinG /= nMin;
	aveMinB /= nMin;
	aveMaxR /= nMax;
	aveMaxG /= nMax;
	aveMaxB /= nMax;
	aveMaxR = max(aveMaxR, 255);
	aveMaxG = max(aveMaxG, 255);
	aveMaxB = max(aveMaxB, 255);
	if (nMid <= 0) {
		aveMidR = (aveMinR + aveMaxR) / 2;
		aveMidG = (aveMinG + aveMaxG) / 2;
		aveMidB = (aveMinB + aveMaxB) / 2;
	} else {
		aveMidR = aveMidR / nMid;
		aveMidG = aveMidG / nMid;
		aveMidB = aveMidB / nMid;
	}
	LOGW("%d %d %d %d %d %d %d %d %d", aveMinR, aveMaxR, aveMinG, aveMaxG, aveMinB, aveMaxB, aveMidR, aveMidG, aveMidB);
	int rh0, rh1, gh0, gh1, bh0, bh1;
	double rz1, gz1, bz1;
	initCubicSplineInterpolation(aveMinR, 0, aveMidR, 127, aveMaxR, 255, &rh0, &rh1, &rz1);
	initCubicSplineInterpolation(aveMinG, 0, aveMidG, 127, aveMaxG, 255, &gh0, &gh1, &gz1);
	initCubicSplineInterpolation(aveMinB, 0, aveMidB, 127, aveMaxB, 255, &bh0, &bh1, &bz1);
	LOGW("h0:%d  h1:%d  z1:%f", rh0, rh1, rz1);
	LOGW("h0:%d  h1:%d  z1:%f", gh0, gh1, gz1);
	LOGW("h0:%d  h1:%d  z1:%f", bh0, bh1, bz1);
	int cr[256], cg[256], cb[256];
	for (i = 0; i < 256; i ++) {
		if (i < aveMinR) {
			cr[i] = aveMinR;
		} else if (i > aveMaxR) {
			cr[i] = aveMaxR;
		} else {
			cr[i] = i;
		}
		if (i < aveMinG) {
			cg[i] = aveMinG;
		} else if (i > aveMaxG) {
			cg[i] = aveMaxG;
		} else {
			cg[i] = i;
		}
		if (i < aveMinB) {
			cb[i] = aveMinB;
		} else if (i > aveMaxB) {
			cb[i] = aveMaxB;
		} else {
			cb[i] = i;
		}
		cr[i] = (cr[i] - aveMinR) * 255 / (aveMaxR - aveMinR);
		cg[i] = (cg[i] - aveMinG) * 255 / (aveMaxG - aveMinG);
		cb[i] = (cb[i] - aveMinB) * 255 / (aveMaxB - aveMinB);
//		cr[i] = computeCubicSplineInterpolation(aveMinR, 0, aveMidR, 127, aveMaxR, 255, rh0, rh1, rz1, cr[i]);
//		cg[i] = computeCubicSplineInterpolation(aveMinG, 0, aveMidG, 127, aveMaxG, 255, gh0, gh1, gz1, cg[i]);
//		cb[i] = computeCubicSplineInterpolation(aveMinB, 0, aveMidB, 127, aveMaxB, 255, bh0, bh1, bz1, cb[i]);
		LOGW("r:%d    g:%d    b:%d", cr[i], cg[i], cb[i]);
	}
	for (i = 0; i < size; i ++) {
		getRGBA(pixels[i], &r, &g, &b, &a);
		pixels[i] = (a << 24) + (cr[r] << 16) + (cg[g] << 8) + cb[b];
	}
}

//Adjust the bright-------亮度调节 -----------------------------
void brightEffect(int* srcPixArray, int w, int h, int bb) {
	int i, j;
	int trans[256];
	float degree=bb/100.0;
	// 算出转换式
	//degree/=100a
	if (degree<0.5)
	{
		degree/=2;
		degree+=0.25;
		for (i = 5; i < 246; i++) {
			trans[i] = (int) (255 * pow(i / 255.0f, (log(degree) / log(0.5))));  
		}
		for (i = 0; i < 5; i++) {
			trans[i] = i;
		}
		for (i = 246; i < 256; i++) {
			trans[i] = i;
		}
	}
	else if (degree<0.6)
	{
		float kk=(degree-0.5)/0.15+0.3;
		for (i=0;i<256;i++)
		{
			trans[i]=(int) (tanh(kk*i/255)/tanh(kk)*255);
		}
	}
	else
	{
		float kk=(degree-0.6)*5+1.3;
		for (i=0;i<256;i++)
		{
			trans[i]=(int) (tanh(kk*i/255)/tanh(kk)*255);
		}
	}
	
    transARGB(srcPixArray,w,h,trans);
}

//Adjust the contrast---------对比度调节 --------------------------
void contrastEffect(int* srcPixArray, int w, int h, int bb) {
	int i, j;
	int trans[256];
	
	float degree=bb/10000.0*3+0.005;
	
	if (degree==0) degree=0.01;
	
	for (i = 0; i < 256; i++) {
		trans[i] = (int) (255.0/(1+exp(-degree*(i-127))) ); 		
	}
	
	transARGB(srcPixArray,w,h,trans);
}

//Adjust the hue-----------色相调节 --------------------------
void hue(int* srcPixArray, int w, int h, int bb) {
	int i, j, index, r, g, b, t,a;
	//float degree=(float)((bb-50)/50.0*3.1415926);
	
	//double colh = degree * 180 / 3.1415927; //将弧度Hindex换为角度colh
	double colh = bb;
	double cos0 = cos(colh) / cos(60 - h);
	double cos1 = cos(h - 120) / cos(180 - h);
	double cos2 = cos(h - 240) / cos(300 - h);
	double cols, coli, colisqrt3;
	int range = (int) (colh / 120);
		
	index = 0;
	for (i = 0; i < h; i++) {
		for (j = 0; j < w; j++) {
            getRGBA(srcPixArray[index],&r,&g,&b,&a);
			coli = (r + g + b) * 0.3333;
			cols = 1 - min(min(r, g), b) / coli;
			colisqrt3 = coli * 0.57735;
			switch (range) {
			case 0: {
					r = (int) (colisqrt3 * (1 + cols * cos0));
					b = (int) (colisqrt3 * (1 - cols));
					g = (int) (1.73205 * coli - r - b);
					break;
				}
			case 1: {
					g = (int) (colisqrt3 * (1 + cols * cos1));
					r = (int) (colisqrt3 * (1 - cols));
					b = (int) (1.73205 * coli - r - g);
					break;
				}
			case 2: {
					b = (int) (colisqrt3 * (1 + cols * cos2));
					g = (int) (colisqrt3 * (1 - cols));
					r = (int) (1.73205 * coli - g - b);
					break;
				}
			}
			adjustRGB(&r,&g,&b);
			srcPixArray[index++] = (a<<24) | (r << 16) | (g << 8) | b;
		}
	}
}
// -----------------------------edit functions ends----------------------------------


// -----------------------------beautify functions----------------------------------

// ---------美肤---------高斯卷积 ------------------------
void smoothEffect(int* srcPixArray, int w, int h)
{
	int i;
	//LOGW("smoothEffect w: %d    h: %d", w, h);
	double GaussMode[25] =  {
		1,4,7,4,1,
		4,16,26,16,4,
		7,26,41,26,7,
		4,16,26,16,4,
		1,4,7,4,1};
//	double GaussMode[9] = {
//		1,2,1,
//		2,4,2,
//		1,2,1};
	double s = 0;
	for (i = 0; i < 25; i ++)
	{
		s += GaussMode[i];
	}
	for (i = 0; i < 25; i ++)
	{
		GaussMode[i] /= s;
	}
	FilterMode(srcPixArray, w, h, GaussMode, 10);
	
}
// ---------单色---------
void singleColor(int* srcPixArray,int w, int h,float* matrix, float targetRed,float targetGreen,float targetBlue,float threshold,float maxup)
{
    int n = w*h;
    int srcRed, srcGreen, srcBlue,srcAlpha;
    int red,green,blue,alpha;
    int i;
    for(i = 0;i<n;i++)
    {
        getRGBA(srcPixArray[i],&srcRed,&srcGreen,&srcBlue,&srcAlpha);
        float sum = srcBlue + srcGreen + srcRed;
        if (sum==0)
        {
            continue;
        }

        int distanceTemp = (srcRed-targetRed*sum)*(srcRed-targetRed*sum) + (srcBlue-targetBlue*sum)*(srcBlue-targetBlue*sum) + (srcGreen-targetGreen*sum)*(srcGreen-targetGreen*sum);
        distanceTemp = distanceTemp*1024;


        float distance = (distanceTemp/(sum*sum))/1024.0;

//        srcRed*1000
//        float distance = (srcRed/sum-targetRed)*(srcRed/sum-targetRed) + (srcBlue/sum-targetBlue)*(srcBlue/sum-targetBlue) + (srcGreen/sum-targetGreen)*(srcGreen/sum-targetGreen);

        //float threshold = 0.375;
        //float maxup = 0.45;
        if(distance<threshold)
        {
            red = srcRed;
            green = srcGreen;
            blue = srcBlue;
            alpha = srcAlpha;
        }
        else
        {
            red   = matrix[0] * srcRed + matrix[1] * srcGreen + matrix[2] * srcBlue + matrix[3] * srcAlpha + matrix[4];
            green = matrix[5] * srcRed + matrix[6] * srcGreen + matrix[7] * srcBlue + matrix[8] * srcAlpha + matrix[9];
            blue  = matrix[10] * srcRed + matrix[11] * srcGreen + matrix[12] * srcBlue + matrix[13] * srcAlpha + matrix[14];
            alpha = matrix[15] * srcRed + matrix[16] * srcGreen + matrix[17] * srcBlue + matrix[18] * srcAlpha + matrix[19];
            if (distance<maxup)
            {
                red = red*(distance-threshold)/(maxup-threshold) + srcRed*(maxup-distance)/(maxup-threshold);
                blue = blue*(distance-threshold)/(maxup-threshold) + srcBlue*(maxup-distance)/(maxup-threshold);
                green = green*(distance-threshold)/(maxup-threshold) + srcGreen*(maxup-distance)/(maxup-threshold);
                alpha = alpha*(distance-threshold)/(maxup-threshold) + srcAlpha*(maxup-distance)/(maxup-threshold);
            }
        }

        srcPixArray[i] = (alpha<<24) | (red << 16) | (green << 8) | blue;
    }
}

// 局部美白--------------其实就是触点处亮度提高 --------------------
void skinWhitePointEffect(int* oldPixels,int *pixels, int w0, int w, int x, int y, int ra)
{
	//LOGW("w: %d. h: %d. x: %d. y: %d. ra: %d.", w, h, x, y, ra);
	int r, g, b, r0, g0, b0,a;
	int color;
	int pos,pos1;
	int i, j;
	int d = ra<<1;
	double dr, rate;

	pos = 0;
	for (i = 0; i < d; i ++)
	{
		for (j = 0; j < d; j ++)
		{
			dr = sqrt((ra - i) * (ra - i) + (ra - j) * (ra - j));
			if (dr < ra) {
    			rate = (ra - dr) / ra * 0.2 + 1;

    			color = pixels[pos];
    			a = ((color>>24) & 255);
    			r = ((color>>16) & 255) * rate;
    			g = ((color>>8) & 255) * rate;
    			b = (color & 255) * rate;

                pos1 = (i+y)*w+(j+x);
    			color = oldPixels[pos1];

    			r0 = ((color>>16) & 255) * 1.2;
    			g0 = ((color>>8) & 255) * 1.2;
    			b0 = (color & 255) * 1.2;
    			if (r > r0) r = r0;
    			if (g > g0) g = g0;
    			if (b > b0) b = b0;

    			if (r > 255) r = 255;
    			if (g > 255) g = 255;
    			if (b > 255) b = 255;

    			pixels[pos] = (a << 24) + (r << 16) + (g << 8) + b;
            }
			pos++;
		}
	}
}

// 牙齿美白
void skinWhiteTeethPointEffect(int* oldPixels,int *pixels, int w0, int w, int x, int y, int ra)
{

	int r, g, b, r0, g0, b0,a;
	int gb;
	int rate0;
	int color;
	int pos,pos1;
	int i, j;
	int d = ra<<1;
	double dr, rate,ratetemp;
	
	pos = 0;
	for (i = 0; i < d; i ++) 
	{
		for (j = 0; j < d; j ++)
		{
			dr = sqrt((ra - i) * (ra - i) + (ra - j) * (ra - j));
			if (dr < ra) {
    			rate = (ra - dr) / ra * 0.1 + 1;
    			
    			color = pixels[pos];
    			a = ((color>>24) & 255);
    			r = ((color>>16) & 255);
    			g = ((color>>8) & 255);
    			b = (color & 255);

    			gb = (b + g)/2;

    			if(r > gb)
    			{
    				ratetemp = (1 - (r - gb) /255)*rate;
        			if(ratetemp<=1)
        			{
        				rate = 1;
        			}
        			else
        			{
        				rate = ratetemp;
        			}
    			}


    			r = r * rate;
    			g = g * rate;
    			b = b * rate;

    			pos1 = (i+y)*w+(j+x);
    			color = oldPixels[pos1];
    
    			r0 = ((color>>16) & 255);
    			g0 = ((color>>8) & 255);
    			b0 = (color & 255);

    			r0 = r0*( 1+(1.0)*(255-r0)/300.0);
    			g0 = g0*( 1+(1.0)*(255-g0)/300.0);
    			b0 = b0*( 1+(1.0)*(255-b0)/300.0);

				if (r > r0)
				{
					r = r0;
				}
				if (g > g0) g = g0;
				if (b > b0) b = b0;


    
    			if (r > 255) r = 255;
    			if (g > 255) g = 255;
    			if (b > 255) b = 255;
    
    			pixels[pos] = (a << 24) + (r << 16) + (g << 8) + b;
            }
			pos++;
		}
	}
}


// 祛痘----------- 触点处3x3均值滤波 -------------------------
void skinSmoothPointEffect(int *pixels, int w, int h, int x, int y, int ra)
{
	//LOGW("w: %d. h: %d. x: %d. y: %d. ra: %d.", w, h, x, y, ra);

	int RN = 3;
	int r, g, b, r0, g0, b0;
	int color;
	int pos;
	int i, j;
    for (j = y - ra; j <= y + ra; j ++)
	{
        pos = j*w + x - ra;
        for (i = x - ra; i <= x + ra; i ++) 
        {	
			if (i < 4 || i > w - 5) continue;
			if (j < 4 || j > h - 5) continue;
			if (distanceSquare(x,y,i,j) > ra*ra){ pos++; continue;}
			//LOGW("i: %d. j: %d", i, j);
			pixels[pos++] = getAvarage(pixels, w, h, i, j, RN);
		}
	}
}


//Enlarge the eye------------------眼睛放大 -----------------------
void eyeEnlarge(int *pixels, int w, int h, int x, int y, int r, float scale)
{
    int i,j;
	
	int n = w*h;
	int *buffer = (int*)malloc(n*sizeof(int));
	memcpy(buffer,pixels,n*sizeof(int));

	double k,ux,uy;
	double px,py;
	int ll,lh,hl,hh,sr,sg,sb,a;
	double d,r2= r*r;	
	
	n = 0;
	for (j=-r;j<r;j++)
	{
        for (i=-r;i<r;i++)
	    {
			d = i*i+j*j;
			if (d<r2)
			{
                // the formula
                k = (r2+d-2*r*sqrt(d))/r2;
			    ux = (1-k*scale)* i + r;
                uy = (1-k*scale)* j + r;		    		    
				
				if (ux < 0) ux = 0;				
				if (uy < 0) uy = 0;
				if (ux >= w - 1) ux = w - 2;
				if (uy >= h - 1) uy = h - 2;

				//interpolation
				px=ux-trunc(ux);
				py=uy-trunc(uy);
				
				ll=buffer[(int)((int)(uy)*w+(int)(ux))];
				lh=buffer[(int)((int)(uy)*w+(int)(ux+1))];
				hl=buffer[(int)((int)(uy+1)*w+(int)(ux))];
				hh=buffer[(int)((int)(uy+1)*w+(int)(ux+1))];
				if ((ll==0)||(hl==0)||(hh==0)||(lh==0)) {
					pixels[n]=ll;
				} else{
					sr=interpolation(px,py,((ll>>16)& 255),((lh>>16)& 255),((hl>>16)& 255),((hh>>16)& 255));
					sg=interpolation(px,py,((ll>>8)& 255),((lh>>8)& 255),((hl>>8)& 255),((hh>>8)& 255));
					sb=interpolation(px,py,(ll& 255),(lh& 255),(hl& 255),(hh& 255));
					a = ((pixels[n]>>24)&255);
					pixels[n]=(a << 24) + (sr << 16) + (sg << 8) + sb;
				}
			}
            n++;	
		}
	}
    
	free(buffer);
}

void getPos(int dx, int dy, int* x, int* y, int* r, float* scale, int num, double* outx, double* outy) {
	int i;
	double k, d, r2;
	*outx = dx + x[num - 1];
	*outy = dy + y[num - 1];
//	LOGW("%d %d  %f %f", dx, dy, *outx, *outy);
	for (i = num - 1; i >= 0; i --) {
		d = (*outx - x[i]) * (*outx - x[i]) + (*outy - y[i]) * (*outy - y[i]);
		r2 = r[i] * r[i];
//		LOGW("%f %f", d, r2);
		if (d < r2) {
			k = (r2 + d - 2 * r[i] * sqrt(d)) / r2;
			*outx = (1-k*scale[i])* (*outx - x[i]) + x[i];
			*outy = (1-k*scale[i])* (*outy - y[i]) + y[i];
//			LOGW("%f %f %f", k, *outx, *outy);
		}
	}
}

//the area of pixels must be square
void eyeBrighten(int* pixels, int w, int bb) {
	int c = w / 2;
	int i, j, p, radius, color;
	int r, g, b, a;
	int trans[256];

	float degree=bb/10000.0*3+0.005;

	if (degree==0) degree=0.01;

	for (i = 0; i < 256; i++) {
		trans[i] = (int) (255.0/(1+exp(-degree*(i-127))) );
	}
	for (i = 0; i < w; i ++) {
		for (j = 0; j < w; j ++) {
			radius = sqrt((i - c) * (i - c) + (j - c) * (j - c));
			if (radius < c) {
				p = i * w + j;
				float ratio = radius * 1.0 / c;
				color = pixels[p];
				getRGBA(color, &r, &g, &b, &a);
				r = r * ratio + trans[r] * (1 - ratio);
				g = g * ratio + trans[g] * (1 - ratio);
				b = b * ratio + trans[b] * (1 - ratio);
				pixels[p] = (a << 24) + (r << 16) + (g << 8) + b;
			}
		}
	}
}

//Enlarge the eye------------------眼睛放大 -----------------------
void eyeEnlargeWithTags(int* in, int* out, int w, int h, int* x, int* y, int* r, float* scale, int num)
{
	LOGW("eyeEnlargeWithTags");
	int i,j,ii;

	int n = w*h;

	double k,ux,uy;
	double px,py;
	int ll,lh,hl,hh,sr,sg,sb,a,p,q;
	double d,r2= r[num - 1]*r[num - 1];
	for (j=-r[num-1];j<r[num-1];j++)
	{
//		LOGW("%d", j);
        for (i=-r[num-1];i<r[num-1];i++)
	    {
			d = i*i+j*j;
			p = (j + y[num - 1]) * w + (i + x[num - 1]);
			if (j + y[num - 1] < 0 || j + y[num - 1] >= h || i + x[num - 1] < 0 || i + x[num - 1] >= w) {
				continue;
			}
			if (d<r2)
			{
				getPos(i, j, x, y, r, scale, num, &ux, &uy);
//				LOGW("%d %d %d  %f %f", x[num - 1], y[num - 1], r[num - 1], ux, uy);
				if (ux < 0) ux = 0;
				if (uy < 0) uy = 0;
				if (ux >= w - 1) ux = w - 2;
				if (uy >= h - 1) uy = h - 2;

				//interpolation
				px=ux-trunc(ux);
				py=uy-trunc(uy);

				ll=in[(int)((int)(uy)*w+(int)(ux))];
				lh=in[(int)((int)(uy)*w+(int)(ux+1))];
				hl=in[(int)((int)(uy+1)*w+(int)(ux))];
				hh=in[(int)((int)(uy+1)*w+(int)(ux+1))];
				if ((ll==0)||(hl==0)||(hh==0)||(lh==0)) {
					out[p]=ll;
				} else{
					sr=interpolation(px,py,((ll>>16)& 255),((lh>>16)& 255),((hl>>16)& 255),((hh>>16)& 255));
					sg=interpolation(px,py,((ll>>8)& 255),((lh>>8)& 255),((hl>>8)& 255),((hh>>8)& 255));
					sb=interpolation(px,py,(ll& 255),(lh& 255),(hl& 255),(hh& 255));
					a = ((in[p]>>24)&255);
					out[p]=(a << 24) + (sr << 16) + (sg << 8) + sb;
				}
			}
		}
	}
}

// Thin  -----------------瘦脸瘦身 ------------------------
void thinEffect(int *pixels, int w, int h, int x, int y, int x2, int y2, int r, float scale, int fix)
{
    int i,j,a;
	
	int n = w*h;
	int *buffer = (int*)malloc(n*sizeof(int));
	
	memcpy(buffer,pixels,n*sizeof(int));
	
	double disx = (x2-x);
	double disy = (y2-y);
	double dis2 = r*r*0.3/scale;
	
	if (fix == 0) {
		if(disx>r*0.1) disx = r*0.1 + (disx-r*0.1)*0.01;
			else if(disx/r<-0.1) disx = -(r*0.1 + (-disx-r*0.1)*0.01);
		if(disy>r*0.1) disy = r*0.1 + (disy-r*0.1)*0.01;
			else if(disy/r<-0.1) disy = -(r*0.1 + (-disy-r*0.1)*0.01);
	}
	double k,ux,uy;
	double px,py;
	int ll,lh,hl,hh,sr,sg,sb,d;	
	double r2= r*r;	
	
//	n = 0;
	for (j=-r;j<r;j++)
	{
        for (i=-r;i<r;i++)
	    {		
			//if ((x+i>=0)&&(y+j>=0)&&(x+i<w)&&(y+j<h)&&(i*i+j*j<r2))
        	if (x + i > w - 1 || x + i < 0 || y + j > h - 1 || y + j < 0) {
        		continue;
        	}
        	n = (y + j) * w + x + i;
			d = i*i+j*j;
			if (d<r2)
			{
                // the formula
				k=((r2-d)/((r2-d)+dis2));
				ux=(x+i)-k*k*disx;
				uy=(y+j)-k*k*disy;
				
				if (ux < 0) ux = 0;				
				if (uy < 0) uy = 0;
				if (ux >= w - 1) ux = w - 2;
				if (uy >= h - 1) uy = h - 2;

//				LOGW("w:%d  h:%d  i:%d  j:%d  ux:%d  uy:%d", w, h, i, j, (int)(ux), (int)(uy));
				//interpolation
				px=ux-trunc(ux);
				py=uy-trunc(uy);
				
				ll=buffer[(int)((int)(uy)*w+(int)(ux))];
				lh=buffer[(int)((int)(uy)*w+(int)(ux+1))];
				hl=buffer[(int)((int)(uy+1)*w+(int)(ux))];
				hh=buffer[(int)((int)(uy+1)*w+(int)(ux+1))];
				if ((ll==0)||(hl==0)||(hh==0)||(lh==0)) {
					pixels[n]=ll;
				} else{
					sr=interpolation(px,py,((ll>>16)& 255),((lh>>16)& 255),((hl>>16)& 255),((hh>>16)& 255));
					sg=interpolation(px,py,((ll>>8)& 255),((lh>>8)& 255),((hl>>8)& 255),((hh>>8)& 255));
					sb=interpolation(px,py,(ll& 255),(lh& 255),(hl& 255),(hh& 255));
					a = ((pixels[n]>>24)&255);
					pixels[n]=(a << 24) + (sr << 16) + (sg << 8) + sb;
				}
			}
//            n++;
		}
	}
    
	free(buffer);
}

void thinEffectWholeFace(int *pixels, int w, int h, int faceLeft, int faceRight,
		int faceTop, int faceBottom, float scale) {
	int r = (faceRight - faceLeft) / 2;
	int top = faceTop - r;
	int bottom = faceBottom + r;
	int left = faceLeft - r;
	int right = faceRight + r;

	if (right > w - 1)
		right = w - 1;
	if (left < 0)
		left = 0;
	if (top < 0)
		top = 0;
	if (bottom > h - 1)
		bottom = h - 1;

	int tw = right - left;
	int th = bottom - top;
	if (tw % 2 != 0)
		tw--;
	if (th % 2 != 0)
		th--;

	double  dis2, disx, disy;
	double k, ux, uy;
	double px, py;
	int ll, lh, hl, hh, sr, sg, sb, d2;
	int a, index;
	float r2 = r * r;
	float size2 = r * r ;
	int n = tw * th;
	int m = 0, i = 0, j = 0;
	int *buffer = (int*) malloc(n * sizeof(int));
	for (m = 0; m < th; m++) {
			memcpy(buffer + m * tw, pixels + (top + m) * w + left,
					tw * sizeof(int));
		}

	int centerX = (faceLeft + faceRight) / 2 - left;
	int centerY = (faceTop + faceBottom) / 2 - top;



	for (j = 0; j < th; j++) {
		for (i = 0; i < tw; i++) {
			//for every pixel
			float scaleTmp = scale;
			index = (j + top) * w + left + i;
			scaleTmp = ((float) j / (float) th) * ((float) j / (float) th)
					+ 0.5;
			scaleTmp = scaleTmp + 4.0 * (float) ((i - centerX) * (i - centerX))
					/ ((float) tw*tw) + 0.5;
			scaleTmp = scaleTmp * scale / 2;

			dis2 = r * r * 0.3 / scaleTmp;

			float disx = centerX - i;
			float disy = centerY - j;
			float tmp;
			d2 = (disx * disx + disy * disy);
			if (d2 > r2) {
				tmp = (float) r / sqrt(disx * disx + disy * disy);
				d2 = d2 * (1 - tmp) * (1 - tmp);
			} else {
				tmp = sqrt(disx * disx + disy * disy) / (float) r;
				d2 = r2 * (1 - tmp) * (1 - tmp);
			}
//		    if (i%10==0 && j%10==0)
//		 	{
//			   	LOGW("tw: %d  th:%d i:%d  j:%d  d2: %d  r2:%f, size:%f", tw,th,i,j,d2,r2,size2);
//	   	   }
			if (disx > r * 0.1)
				disx = r * 0.1 + (disx - r * 0.1) * 0.01;
			else if (disx / r < -0.1)
				disx = -(r * 0.1 + (-disx - r * 0.1) * 0.01);
			if (disy > r * 0.1)
				disy = r * 0.1 + (disy - r * 0.1) * 0.01;
			else if (disy / r < -0.1)
				disy = -(r * 0.1 + (-disy - r * 0.1) * 0.01);

			if (d2 < size2) {
				// the formula
				k = ((size2 - d2) / ((size2 - d2) + dis2));
				ux = i - k * k * disx;
				uy = j - k * k * disy;

				if (ux < 0)
					ux = 0;
				if (uy < 0)
					uy = 0;
				if (ux >= tw - 1)
					ux = tw - 2;
				if (uy >= th - 1)
					uy = th - 2;

				//interpolation
				px = ux - trunc(ux);
				py = uy - trunc(uy);

				ll = buffer[(int) ((int) (uy) * tw + (int) (ux))];
				lh = buffer[(int) ((int) (uy) * tw + (int) (ux + 1))];
				hl = buffer[(int) ((int) (uy + 1) * tw + (int) (ux))];
				hh = buffer[(int) ((int) (uy + 1) * tw + (int) (ux + 1))];

				if ((ll == 0) || (hl == 0) || (hh == 0) || (lh == 0)) {
						pixels[index]=ll;
				} else {
					sr = interpolation(px, py, ((ll >> 16) & 255),
							((lh >> 16) & 255), ((hl >> 16) & 255),
							((hh >> 16) & 255));
					sg = interpolation(px, py, ((ll >> 8) & 255),
							((lh >> 8) & 255), ((hl >> 8) & 255),
							((hh >> 8) & 255));
					sb = interpolation(px, py, (ll & 255), (lh & 255),
							(hl & 255), (hh & 255));
					a = ((pixels[index] >> 24) & 255);
					pixels[index] = (a << 24) + (sr << 16) + (sg << 8) + sb;
				}
			}
		}
	}
	free(buffer);
}
void thinEffectAuto(int *pixels, int w, int h, int faceLeft, int faceRight,
		int faceTop, int faceBottom, int degree) {
	int faceWidth = faceRight - faceLeft;
	int faceHeight = faceTop - faceBottom;
	void thin(int *pixels, int thinSize, int x1, int y1, int x2, int y2,
			int degree) {
		int top = y1 - thinSize;
		int bottom = y1 + thinSize;
		int left = x1 - thinSize;
		int right = x1 + thinSize;

		if (right > w - 1)
			right = w - 1;
		if (left < 0)
			left = 0;
		if (top < 0)
			top = 0;
		if (bottom > h - 1)
			bottom = h - 1;

		int tw = right - left;
		int th = bottom - top;
		if (tw % 2 != 0)
			tw--;
		if (th % 2 != 0)
			th--;
		int* tempPixels = (int*) malloc(tw * th * sizeof(int));
		int m = 0;
		for (m = 0; m < th; m++) {
			memcpy(tempPixels + m * tw, (pixels + (top + m) * w + left),
					tw * sizeof(int));
		}
		float scale = degree / 100.0;
		thinEffect(tempPixels, tw, th, x1 - left, y1 - top, x2 - left, y2 - top,
				tw / 2, scale, 0);
		for (m = 0; m < th; m++) {
			memcpy(pixels + ((top + m) * w + left), tempPixels + tw * m,
					tw * sizeof(int));
		}
	}
	degree = degree / 2;
	int c1 = (faceLeft + faceRight) / 2;
	int c2 = (faceTop + faceBottom) / 2;
	thin(pixels, faceWidth, faceLeft, faceBottom, c1, c2, degree * 3 / 2); //left bottom
	thin(pixels, faceWidth, faceRight, faceBottom, c1, c2, degree * 3 / 2); //right bottom
	thin(pixels, faceWidth, faceLeft, faceTop, c1, c2, degree / 2); // left top
	thin(pixels, faceWidth, faceRight, faceTop, c1, c2, degree / 2); //right top
	thin(pixels, faceWidth, faceLeft, c2, c1, c2, 2 * degree / 3); //left side mid
	thin(pixels, faceWidth, faceRight, c2, c1, c2, 2 * degree / 3); //right side mid
	thin(pixels, faceWidth, c1, faceBottom, c1, c2, 2 * degree / 3); // top mid
	thin(pixels, faceWidth, c1, faceTop, c1, c2, degree / 2); //bottom mid
}

void smileWholeMouth(int *pixels, int w, int h, int mouthLeft, int mouthRight,
		int mouthTop, int mouthBottom, float scale) {
	//LOGW("scale: %f", scale);
	int tw = mouthRight - mouthLeft;
	int th = mouthBottom - mouthTop;
	if (tw % 2 != 0)
		tw--;
	if (th % 2 != 0)
		th--;
	int n = tw * th;
	int *buffer = (int*) malloc(n * sizeof(int));

	int x1 = tw / 8;
	int y1 = th / 2;
	int x2 = tw * 7 / 8;
	int y2 = th / 2;
	int r = tw / 4;

	int l = 0, i = 0, j = 0;
	int x, y;
	for (l = 0; l < th; l++) {
		memcpy(buffer + l * tw, pixels + (mouthTop + l) * w + mouthLeft,
				tw * sizeof(int));
	}
	double disx,disy, dis2;
	double k, ux, uy;
	double px, py;
	int ll, lh, hl, hh, sr, sg, sb, a, index , d2, r2;
	r2 = r * r;
	dis2 = r * r * 0.3 / scale;

	for (j = 0; j < th; j++) {
		for (i = 0; i < tw; i++) {
			index = (mouthTop + j) * w + mouthLeft + i;
			if (i <= (x1+x2) / 2) {
				disx = -tw / 4;
				disy = -th / 2;
				d2 = (i - x1) * (i - x1) + (j - y1) * (j - y1);
			} else if (i > (x1 + x2) / 2) {
				disx = tw / 4;
				disy = -th / 2;
				d2 = (i - x2) * (i - x2) + (j - y2) * (j - y2);
			}
			k = ((r2 - d2) / ((r2 - d2) + dis2));

			if (disx > r * 0.1)
				disx = r * 0.1 + (disx - r * 0.1) * 0.01;
			else if (disx / r < -0.1)
				disx = -(r * 0.1 + (-disx - r * 0.1) * 0.01);
			if (disy > r * 0.1)
				disy = r * 0.1 + (disy - r * 0.1) * 0.01;
			else if (disy / r < -0.1)
				disy = -(r * 0.1 + (-disy - r * 0.1) * 0.01);

//				LOGW("tw: %d  th:%d i:%d  j:%d  d2: %d  r2:%d", tw,th,i,j,d2,r2);

			if (d2 < r2)
			{
				ux = i - k * disx;
				uy = j - k * disy;

				if (ux < 0)
					ux = 0;
				if (uy < 0)
					uy = 0;
				if (ux >= tw - 1)
					ux = tw - 2;
				if (uy >= th - 1)
					uy = th - 2;

				//LOGW("w:%d  h:%d  i:%d  j:%d  ux:%d  uy:%d", w, h, i, j, (int)(ux), (int)(uy));
				//interpolation
				px = ux - trunc(ux);
				py = uy - trunc(uy);

				ll = buffer[(int) ((int) (uy) * tw + (int) (ux))];
				lh = buffer[(int) ((int) (uy) * tw + (int) (ux + 1))];
				hl = buffer[(int) ((int) (uy + 1) * tw + (int) (ux))];
				hh = buffer[(int) ((int) (uy + 1) * tw + (int) (ux + 1))];
				if ((ll == 0) || (hl == 0) || (hh == 0) || (lh == 0)) {
					pixels[index] = ll;
				} else {
					sr = interpolation(px, py, ((ll >> 16) & 255),
							((lh >> 16) & 255), ((hl >> 16) & 255),
							((hh >> 16) & 255));
					sg = interpolation(px, py, ((ll >> 8) & 255),
							((lh >> 8) & 255), ((hl >> 8) & 255),
							((hh >> 8) & 255));
					sb = interpolation(px, py, (ll & 255), (lh & 255),
							(hl & 255), (hh & 255));
					a = ((pixels[index] >> 24) & 255);
					pixels[index] = (a << 24) + (sr << 16) + (sg << 8) + sb;
				}
			}
		}
	}
	free(buffer);
}

// red eye removal
void redeye(int *pixels, int w, int h, int x, int y, int r) 
{
     int i,j;
     int red, green, blue,alpha;
     int i2;
     int r2 = r*r;
     int exceed;
     
     int pos = 0;
     for(i=0;i<h;i++) {
         i2 = (i-y)*(i-y);
         for(j=0;j<w;j++) {
             if(i2+(j-x)*(j-x)<r2) {
                  getRGBA(pixels[pos],&red,&green,&blue,&alpha);
                  //if(green>128 || blue>128) continue;
                  //if((red<<1)<(green+blue)) continue;
                                  
                  transRgb2Lab(&red,&green,&blue);
                  exceed = green-128;
                  if(exceed>=15 && (blue-exceed)<123) {
                               red -= 5;
                               green = 128;
                               blue = 128;
                  }
                  transLab2Rgb(&red,&green,&blue);
                  pixels[pos] = (alpha << 24) | (red << 16) | (green << 8) | blue;
             }
             pos ++;
         }
     }
     
}
// -----------------------------beautify functions ends----------------------------------


// -----------------------------effect functions------------------------------------------ 
// ------------- 曲线调节-------------------蓝调，秋色，糖水片
void curve(int* srcPixArray,int* transr,int* transg,int* transb, int w,int h){
	int i, j, k, r, g, b,a;
	
	k = 0;
	for (i = 0; i < h; i++) {
		for (j = 0; j < w; j++) {            
			getRGBA(srcPixArray[k],&r,&g,&b,&a);
			r = transr[r];
			g = transg[g];
			b = transb[b];
			srcPixArray[k++] = (a << 24) | (r << 16) | (g << 8) | b;
		}
	}
}

// adjust the pic to the blue hue------------ 曲线调节之前先变灰度
void blue(int* srcPixArray,int* transr,int* transg,int* transb, int w,int h){
	int i, j, k, r, g, b,gray,alpha;
	char buffer[200];
	
	k = 0;
	for (i = 0; i < h; i++) {
		for (j = 0; j < w; j++) {
      getRGBA(srcPixArray[k],&r,&g,&b,&alpha);
			gray = (r * 19660 + g * 38666 + b * 7208) >> 16;
			r = transr[gray];
			g = transg[gray];
			b = transb[gray];
			srcPixArray[k++] = (alpha << 24) | (r << 16) | (g << 8) | b;
		}
	}
}

// gray the pic---------------黑白效果 ---------------
void gray(int* srcPixArray, int w,int h)
{
	int i, j, k, r, g, b, gray,alpha;
	k=0;
	for (i = 0; i < h; i++) {
		for (j = 0; j < w; j++) {
            getRGBA(srcPixArray[k],&r,&g,&b,&alpha);
			gray = (r * 19660 + g * 38666 + b * 7208) >> 16;
			srcPixArray[k++] = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
		}
	}
}

// blur the pic(all pic)------------------模糊------------3x3滤波 ----------好像没有用到
void blur(int* srcPixArray, int w, int h, int x, int y, int r) {
     
	int i, j, k, red, green, blue, rt, gt, bt,alpha;
	int mid;
	int filterIndex[9];
	int filter[9] = {0,1,0,
                  1,0,1,
                  0,1,0};

	for (i = 1 ; i < h-1; i++) {
		for (j = 1 ; j < w-1; j++) {
            
            mid = i*w+j;            
            get3x3FilterIndex(filterIndex,w,mid);
            
            rt = gt = bt = 0;
            for (k=0;k<9;k++)
            {
                getRGB(srcPixArray[filterIndex[k]],&red,&green,&blue);
                rt += (red<<filter[k]);
                gt += (green<<filter[k]);
                bt += (blue<<filter[k]);
            }
            getRGBA(srcPixArray[mid],&red,&green,&blue,&alpha);
            
            red = (rt<<1)/13 - red;
            green = (gt<<1)/13 - green;
            blue = (bt<<1)/13 - blue;
            
            adjustRGB(&red,&green,&blue);
			
			srcPixArray[mid] = (alpha << 24) | (red << 16) | (green << 8) | blue;
            
			
		}
	}
}

void lomo(int* srcPixArray, int w, int h, int color, int brightRatioI, int darkRatioI, int scope)
{
	int cx = w / 2;
	int cy = h / 2;
	int r = sqrt(cx * cx + cy * cy);
	int dx, dy, dist, pos, i, j;
    int red, green, blue, alpha;
    float brightRatio = brightRatioI / 100.0;
    float darkRatio = darkRatioI / 100.0;
    int dstR, dstG, dstB, dstA;
    getRGBA(color, &dstR, &dstG, &dstB, &dstA);
    pos = 0;
    int distScope = r * (100 - scope) / 100;
    r -= distScope;
	for (i = 0; i < h; i ++)
	{
		for (j = 0; j < w; j ++)
		{
			dx = j - cx;
			dy = i - cy;
			dist = sqrt(dx * dx + dy * dy);
			getRGBA(srcPixArray[pos],&red,&green,&blue,&alpha);
			if (dist > distScope) {
				float ratio = 1.0 * (dist - distScope) / r * darkRatio;
				red = red * (1 - ratio) + dstR * ratio;
				green = green * (1 - ratio) + dstG  * ratio;
				blue = blue * (1 - ratio) + dstB  * ratio;
			}
            red *= brightRatio;
            green *= brightRatio;
            blue *= brightRatio;
			adjustRGB(&red,&green,&blue);

			srcPixArray[pos] = (alpha<<24) | (red << 16) | (green << 8) | blue;

			pos++;
		}
	}
}


// lomo the pic with dark --------------LOMO效果 --------------------------
void dlomo(int* srcPixArray, int w, int h , int x, int y, int bound) 
{
     int i,j,pos;
     int r;
     int red, green, blue,alpha;
     
     double d,t,tt;
     
     //r = sqrt((w*w>>2)+(h*h>>2));
     r = (w*w>>2)+(h*h>>2);
     t = (255.0 - bound) / (r*sqrt(r));
      
     
     pos = 0;     
     for( i=0;i<h;i++)
     {
          for (j = 0;j<w;j++)
          {
              //d = sqrt(distanceSquare(i,j,y,x));
              d = distanceSquare(i,j,y,x);
              d = d*sqrt(d);
              tt = 1 - t*d/255;

              getRGBA(srcPixArray[pos],&red,&green,&blue,&alpha);
              red = red*tt;
              green = green*tt;
              blue = blue*tt;
              
              adjustRGB(&red,&green,&blue);
              
              srcPixArray[pos] = (alpha<<24) | (red << 16) | (green << 8) | blue;
              
              pos++;
          }
     }

}
// lomo the pic with dark ----------- 柔光 -----------------------------------
void llomo(int* srcPixArray, int w, int h, int x, int y, int  bound) 
{
     int i,j,pos;
     int r;
     int red, green, blue,alpha;
     
     float d,t,tt;
     
     r = (w*w>>2)+(h*h>>2);
     t = ((float)bound) / r;
      
     
     pos = 0;     
     for( i=0;i<h;i++)
     {
          for (j= 0;j<w;j++)
          {
              //d = sqrt(distanceSquare(i,j,y,x));
              d = distanceSquare(i,j,y,x);
              tt = t*d;

              getRGBA(srcPixArray[pos],&red,&green,&blue,&alpha);
              red = red + tt*(1-red/255.0);
              green = green + tt*(1-green/255.0);
              blue = blue + tt*(1-blue/255.0);
              adjustRGB(&red,&green,&blue);
              
              srcPixArray[pos] = (alpha<<24) | (red << 16) | (green << 8) | blue;
              
              pos++;              
          }
     }
}

// lomo the pic with dark ----------- 正片负冲 -----------------------------------
//Etoc steps
//step 1: multiply with channel Blue (Inverse && 50%)
//step 2: multiply with channel Green (Inverse && 20%)
//step 3: Color burn Red Channel (100%)
//step 4: Change the Color levels;
//          Blue 21 0.76 151
//          Green 46 1.37 221
//          Red 51 1.28 255
//step 5: contrast +16
//step 6: bright -3
//step 7: sat +17
void etoc(int* srcPixArray, int w, int h)
{
    int i;
    int temp;
    for (i = 0; i < w * h; i++) {
		int colorT = srcPixArray[i];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;
        int aT = (colorT >> 24) & 0xFF;
        
        //////step 1 
        temp = bT*(255-bT)/255;
        bT=temp*0.5+bT*0.5;
        
        ///step 2
        temp = gT*(255-gT)/255;
        gT = temp*0.2+gT*0.8;
        ///step 3
        if (rT>0) {
            rT = rT - (255-rT)* (255-rT)/rT;
        }
        if (rT<0)rT=0;
        
        ///step 4
        bT = colorLevel(bT, 21, 0.96, 151, 0, 255);
        gT = colorLevel(gT, 46, 1.37, 221, 0, 255);
        rT = colorLevel(rT, 51, 1.28, 255, 0, 255);
        
        /*
        //step 5
        int contrast = 10;
        bT = bT + (bT-128)*contrast/100;
        gT = gT + (gT-128)*contrast/100;
        rT = rT + (rT-128)*contrast/100;
        
        //step 6
        int bright =-3;
        bT +=bright;
        gT +=bright;
        rT +=bright;
        
        //step 7
        int sat = 10;
        bT = (bT<128)?(bT-sat):(bT+sat);
        gT = (gT<128)?(gT-sat):(gT+sat);
        rT = (rT<128)?(rT-sat):(rT+sat);
        */
        if (bT<0)bT=0;
        if (bT>255)bT=255;
        if (gT<0)gT=0;
        if (gT>255)gT=255;
        if (rT<0)rT=0;
        if (rT>255)rT=255;
		srcPixArray[i] = (aT << 24) | (rT << 16) | (gT << 8) | bT;
	}
}

// lomo the pic with dark ----------- 正片 -----------------------------------
void postivefilter(int* srcPixArray, int w, int h)
{
    float sValue;
    int i;
    int a,r,g,b;
    int max, min, t, p, l;
    for (i = 0; i < w * h; i++) {
		int colorT = srcPixArray[i];
		r = (colorT >> 16) & 0xFF;
		g = (colorT >> 8) & 0xFF;
		b = colorT & 0xFF;
        a = (colorT >> 24) & 0xFF;
        
//        sValue = 0.3;
//        // 利用HSL模式求得颜色的S和L
//        float rgbMax = r / 255.0;
//        float rgbMin = g / 255.0;
//        float rgbC = b / 255.0;
//        if (rgbMax < rgbC)
//            Swap(&rgbMax, &rgbC);
//        if (rgbMax < rgbMin)
//            Swap(&rgbMax, &rgbMin);
//        if (rgbMin> rgbC)
//            Swap(&rgbMin, &rgbC);
//        float delta = rgbMax - rgbMin;
//        // 如果delta=0，S=0，所以不能调整饱和度
//        if (delta == 0) continue;
//        float value = rgbMax + rgbMin;
//        float S, L = value / 2;
//        if (L < 0.5)
//            S = delta / value;
//        else  S = delta  / (2 - value);
//        // 具体的饱和度调整，sValue为饱和度增减量
//        // 如果增减量>0，饱和度呈级数增强，否则线性衰减
//        if (sValue> 0)
//        {
//            // 如果增减量+S > 1，用S代替增减量，以控制饱和度的上限
//            // 否则取增减量的补数
//            sValue = sValue + S >= 1? S : (1 - sValue);
//            // 求倒数 - 1，实现级数增强
//            sValue = 1 / sValue - 1;
//        }
//        // L在此作饱和度下限控制
//        r = r + (r - L * 255) * sValue +3;
//        g = g + (g - L * 255) * sValue +3;
//        b = b + (b - L * 255) * sValue +3;

        if (r > g) {
        	max = r;
        	min = g;
        } else {
        	max = g;
        	min = r;
        }

        if (max < b) {
        	max = b;
        }

        if (min > b) {
        	min = b;
        }

        t = max + min;
        p = max - min;
        if (p == 0) continue;
        if (t >= 255) {
        	if (p * 10 > 7 * t) {
        		l = t * 7 / 10;
        		r = (2 * r - p) * (t - l) / l / 2 + 3 + r;
        		g = (2 * g - p) * (t - l) / l / 2 + 3 + g;
        		b = (2 * b - p) * (t - l) / l / 2 + 3 + b;
        	} else {
        		r = (20 * r - 3 * t) / 14 + 3;
        		g = (20 * g - 3 * t) / 14 + 3;
        		b = (20 * b - 3 * t) / 14 + 3;
        	}
        } else {
        	if (p * 10 > 3570 - 7 * t) {
        		l = 357 - t * 7 / 10;
        		r = (2 * r - p) * (t - l) / l / 2 + 3 + r;
        		g = (2 * g - p) * (t - l) / l / 2 + 3 + g;
        		b = (2 * b - p) * (t - l) / l / 2 + 3 + b;
        	} else {
        		r = (20 * r - 3 * t) / 14 + 3;
        		g = (20 * g - 3 * t) / 14 + 3;
        		b = (20 * b - 3 * t) / 14 + 3;
        	}
        }

        if (b<0)b=0;
        if (b>255)b=255;
        if (g<0)g=0;
        if (g>255)g=255;
        if (r<0)r=0;
        if (r>255)r=255;
		srcPixArray[i] = (a << 24) | (r << 16) | (g << 8) | b;
    }
}



// ------------------------------ 梦幻效果 -----------------------------------
void dreamy(int* pixels, int w, int h, int radius)
{
     int* layerPixels = (int*)malloc(sizeof(int)*w*h);
     memcpy(layerPixels,pixels,w*h*sizeof(int));
     
     fastAverageBlur(layerPixels, w,h,radius);
     Screen(pixels,layerPixels,w,h);
     
     free(layerPixels);
}

//
void greenToblue(int *pixels, int w, int h) 
{
     int i,j;
     int red, green, blue,alpha;
     
     int pos = 0;
     for(i=0;i<h;i++) {
         for(j=0;j<w;j++) {
                  getRGBA(pixels[pos],&red,&green,&blue,&alpha);
                                                   
                  transRgb2Lab(&red,&green,&blue);
                  green = blue;
                  transLab2Rgb(&red,&green,&blue);
                  pixels[pos] = (alpha << 24) | (red << 16) | (green << 8) | blue;
             pos ++;
         }
     }
     
}

// -----------------------------effect functions ends------------------------------------------

// ------------------------------------未整理-----------------------------------------------
void gaussBlur(int* srcPixArray, int width, int height,int radius,float sigma)
{
	//LOGW("smoothEffect w: %d    h: %d", width, height);
    //LOGW("parameters r%d    sigma%f",radius,sigma);
	/* Set gauss matrix according to parameters*/
	int size = 2 * radius + 1;
	float* gaussMatrix = (float *)malloc(size * size * sizeof(float));

	float gaussSum = 0, value;
	float _2sigma2 = 2 * sigma * sigma;

	int x,y,k;

	k=0;
	for(y = -radius; y <= radius; y++){
		for(x = -radius; x<=radius; x++) {
			value = exp(-(x*x+y*y)/_2sigma2);
			gaussMatrix[k++] = value;
			gaussSum +=value;
		}
	}
	
	for(k=0;k<size*size;k++)
		gaussMatrix[k]/=gaussSum;

    //LOGW("parameters r%d    sigma%f",radius,sigma);

	/* convolution */
    int color;
	float a,r,g,b;
	float d;
	int xx,yy;
	for( y = 0; y < height; y++){
		for( x = 0; x < width; x++){
			 //LOGW("coordinates x %d ",x);
			a = r = g = b = 0;

			for( yy = -radius; yy <= radius; yy++ ){
				int yyy = y + yy;
				if( yyy < 0 || yyy >= height)
					continue;
				for(xx = -radius; xx <= radius; xx++){
					int xxx = x + xx;
					if(xxx < 0 || xxx >= width)
						continue;

					d = gaussMatrix[xx+radius+size*(yy+radius)];
					color = srcPixArray[xxx+width*yyy];
					r += ((color >> 16) & 0xFF) * d;
					g += ((color >> 8) & 0xFF) * d;
					b += ((color) & 0xFF) * d;
				}
			}
			srcPixArray[x + width * y] = ((int) a << 24)
				| ((int) r << 16) | ((int) g << 8) | (int) b;
		}
	}

	free(gaussMatrix);

}

// 自动色阶
void autoLevels(int* srcPixArray,int w, int h)
{
     int n = w*h;
     int red, green, blue,alpha;
     int del = n*0.055;
     int levels[3][256];
     int low[3]={0,0,0},high[3]={0,0,0};
     int i,j;
     memset(levels,0,sizeof(int)*256*3);
     for(i = 0;i<n;i++)
     {
           getRGB(srcPixArray[i],&red,&green,&blue);
           levels[0][red]++;
           levels[1][green]++;
           levels[2][blue]++;
     }
     for(j=0;j<3;j++)
     {
           for(i=0;;i++)
           {
            low[j] += levels[j][i];
            if(low[j]>del) break;
           }
           low[j]=i;
           for(i=255;;i--)
           {
             high[j]+=levels[j][i];
             if(high[j]>del) break;
           }
           high[j]=i;
     }
     float t[3];
     for(j=0;j<3;j++) t[j]=255.0/(high[j]-low[j]);
     for(i=0;i<n;i++)
     {
           getRGBA(srcPixArray[i],&red,&green,&blue,&alpha);
           red = (red-low[0])*t[0];
           green = (green-low[1])*t[1];
           blue = (blue-low[2])*t[2];
           adjustRGB(&red,&green,&blue);
           srcPixArray[i] = (alpha<<24) | (red << 16) | (green << 8) | blue;
     }
}

void setGaussModel(double* model, double sigma2, int r)
{
	int i,j;
	double d;
	double sum = 0;
	int s = 2*r + 1;
	for(i=-r; i<=r; i++)
		for(j=-r; j<=r; j++)
		{
			double v;
			d = i*i + j*j;
			v = exp(-d/(2*sigma2));
			model[(i+r)*s+ j+r] = v;
			sum += v;
		}

	for(i=-r; i<=r; i++)
		for(j=-r; j<=r; j++)
		{
			model[(i+r)*s+ j+r] /= sum;
		}
}

void averageBlur(int* srcPixArray,int width, int height, int radius)
{
	int x,y;

	/* convolution */
	int color;
	int a,r,g,b;
	int xx,yy;

	for( y = 0; y < height; y++){
		for( x = 0; x < width; x++){
			//LOGW("coordinates x %d ",x);
			a = r = g = b = 0;
			for( yy = -radius; yy <= radius; yy++ )
			{
				int yyy = y + yy;
				if(yyy<0) yyy = -yyy;
				if(yyy>=height) yyy = 2*height-yyy-2;

				for(xx = -radius; xx <= radius; xx++)
				{
					int xxx = x + xx;
					if(xxx<0) xxx = -xxx;
					if(xxx>width) xxx = 2*width-xxx-2;

					color = srcPixArray[xxx+width*yyy];
					r += ((color >> 16) & 0xFF);
					g += ((color >> 8) & 0xFF);
					b += ((color) & 0xFF);
				}
			}
			r /= 9;
			g /= 9;
			b /= 9;
			srcPixArray[x + width * y] = ((int) a << 24)
							| ((int) r << 16) | ((int) g << 8) | (int) b;
		}
	}
}

////-----------背景虚化-----------------
/* blurBackgrounByCircle: 背景虚化，
 *      内圆内作为前景，不虚化；
 *      内外圆之间，逐渐虚化；
 *      外圆之外：最大虚化
 * input:
 *     srcPixArray 原图像像素
 *     w 图像宽度
 *     h 图像高度
 *     x 圆的中心点x
 *     y 圆的中心点y
 *     r0 内圆半径
 *     r1 外圆半径
 */
//void blurBackgrounByCircle(int* srcPixArray, int w, int h, int x, int y, int r0, int r1)
//{
//	/*
//	 * 使用高斯滤波，5x5滤波模板
//	 */
//	float sigmaMax = 0.99;
//	float sigma;
//	const int size = 5;
//	int i, j, row, col;
//	int ii, jj;
//	double sr, sg, sb;
//	int r, g, b;
//	double d;
//	double model[5*5];
//
//	int* tmpPixels = (int*)malloc(sizeof(int)*w*h);
//	memcpy(tmpPixels,srcPixArray,w*h*sizeof(int));
//
//	if(r1 <= r0)
//		return;
//
//	for(row=0; row<h; row++)
//	{
//		for(col=0; col<w; col++)
//		{
//			d = distanceSquare(row,col,y,x);
//			if(d>r0)
//			{
//				if(d>r0 && d<=r1)
//				{
//					double dr = (d-r0)/(r1-r0);
//					sigma = sigmaMax*dr*dr;
//				}
//				else if(d > r1)
//					sigma = sigmaMax;
//				setGaussModel(model, sigma*sigma, size/2);
//				sr = 0; sg = 0; sb = 0;
//				for(ii=-size/2; ii<=size/2; ii++)
//					for(jj=-size/2; jj<=size/2; jj++)
//					{
//						double mm;
//						i = row+ii;
//						j = col+jj;
//						if(i<0) i = -i;
//						if(i>=h) i = 2*h-i;
//						if(j<0) j = -j;
//						if(j>=w) j = 2*w-j;
//						getRGB(tmpPixels[i*w+j],&r,&g,&b);
//						mm = model[(ii+2)*size + jj+2];
//						sr += r * mm;
//						sg += g * mm;
//						sb += b * mm;
//					}
//				srcPixArray[(i+ii)*w+j+jj] = (255<<24) | ((int)sr << 16) | ((int)sg << 8) | (int)sb;
//			}
//		}
//	}
//	free(tmpPixels);
//}
//
//
//
///* blurBackgrounByLine: 背景虚化，
// *      内线之间，不虚化；
// *      线条之间，逐渐虚化；
// *      外线之外：最大虚化
// * input:
// *     srcPixArray 原图像像素
// *     w 图像宽度
// *     h 图像高度
// *     x 中心线条的中心点x
// *     y 中心线条的中心点y
// *     theta 斜线角度
// *     r0 内线距离
// *     r1 外线距离
// */
//void blurBackgrounByLine(int* srcPixArray, int w, int h, int x, int y, double theta, int r0, int r1)
//{
//	/*
//	 * 使用高斯滤波，5x5滤波模板
//	 */
//	float sigmaMax = 0.99;
//	float sigma;
//	const int size = 5;
//	int i, j, row, col;
//	int ii, jj;
//	double sr, sg, sb;
//	int r, g, b;
//	double d, dr;
//	double model[5*5];
//	double th = theta*3.14159265/180; /*转换成弧度*/
//
//	int* tmpPixels = (int*)malloc(sizeof(int)*w*h);
//	memcpy(tmpPixels,srcPixArray,w*h*sizeof(int));
//
//	if(r1 <= r0)
//		return;
//
//	for(row=0; row<h; row++)
//	{
//		for(col=0; col<w; col++)
//		{
//			d = abs(cos(th)*(row-y) + sin(th)*(col-x));
//			if(d>r0)
//			{
//				if(d>r0 && d<=r1)
//				{
//					dr = (d-r0)/(r1-r0);
//					sigma = sigmaMax*dr*dr;
//				}
//				else if(d > r1)
//					sigma = sigmaMax;
//				setGaussModel(model, sigma*sigma, size/2);
//				sr = 0; sg = 0; sb = 0;
//				for(ii=-size/2; ii<=size/2; ii++)
//					for(jj=-size/2; jj<=size/2; jj++)
//					{
//						double mm;
//						i = row+ii;
//						j = col+jj;
//						if(i<0) i = -i;
//						if(i>=h) i = 2*h-i;
//						if(j<0) j = -j;
//						if(j>=w) j = 2*w-j;
//						getRGB(tmpPixels[i*w+j],&r,&g,&b);
//						mm = model[(ii+2)*size + jj+2];
//						sr += r * mm;
//						sg += g * mm;
//						sb += b * mm;
//					}
//				srcPixArray[(i+ii)*w+j+jj] = (255<<24) | ((int)sr << 16) | ((int)sg << 8) | (int)sb;
//			}
//		}
//	}
//	free(tmpPixels);
//}

// void ImageFilter(int* src, int* dst, int* model)

//// ---------- HDR effect -----------------
/* hdreffect: HDR effect
 * input:
 * 		srcPixArray	输入原始图像
 * 		w	图像宽度
 * 		h	图像高度
 * 		alpha1	明暗参数，建议值0.9
 * 		alpha2	细节参数，建议值0.2
 */
void hdrEffect(int* srcPixArray, int w, int h, double alpha1, double alpha2)
{
	int row, col, i, j, ii, jj;
	int	*gray; // 灰度图
	int r, g, b,a;
	int srcPx, dstPx;
	int ind;

	double model[5*5] = {
		    0.0030, 0.0133, 0.0219, 0.0133, 0.0030,
		    0.0133, 0.0596, 0.0983, 0.0596, 0.0133,
		    0.0219, 0.0983, 0.1621, 0.0983, 0.0219,
		    0.0133, 0.0596, 0.0983, 0.0596, 0.0133,
		    0.0030, 0.0133, 0.0219, 0.0133, 0.0030};

	gray = (int*)malloc(sizeof(int)*w*h);

	// RGB to Gray Image
	ind = 0;
	for(row=0; row<h; row++)
	{
		for(col=0; col<w; col++)
		{
			srcPx = srcPixArray[ind];
			getRGB(srcPx, &r,&g,&b);
			gray[ind] = (int)(0.30*r + 0.59*g + 0.11*b);
			ind++;
		}
	}

	// layer1 gauss blur (remember that layer1 is gray image)
	// and overlay with original image
	ind = 0;
	for(row=0; row<h; row++)
	{
		for(col=0; col<w; col++)
		{
			// low pass
			float lp = 0;
			float l1, l2; // layer1 and layer2
			for(ii=-2; ii<=2; ii++)
			{
				for(jj=-2; jj<=2; jj++)
				{
					i = row+ii;
					j = col+jj;
					if(i<0) i = -i;
					if(i>=h) i = 2*h-i;
					if(j<0) j = -j;
					if(j>=w) j = 2*w-j;
					lp += gray[i*w + j] * model[(ii+2)*5 + 2+jj];
				}
			}
			l1 = 255-lp;
			l2 = max(100+gray[ind]-lp, 255);

//			r = g = b = (int)lp;
//			srcPixArray[ind] = (255<<24) | (r << 16) | (g << 8) | b;
//			ind++;
//			continue;

			srcPx = srcPixArray[ind];
			getRGBA(srcPx, &r,&g,&b,&a);
			// overlay
			if(r<128)
				r = 2.0*r*l1*l2/(255*255);
			else
				r = 255-2.0*(255-r)*(255-l1)*(255-l2)/(255*255);
			if(g<128)
				g = 2.0*g*l1*l2/(255*255);
			else
				g = 255-2.0*(255-g)*(255-l1)*(255-l2)/(255*255);
			if(b<128)
				b = 2.0*b*l1*l2/(255*255);
			else
				b = 255-2.0*(255-b)*(255-l1)*(255-l2)/(255*255);
			srcPixArray[ind++] = (a<<24) | (r << 16) | (g << 8) | b;
		}
	}
	free(gray);
}

//void hdrEffect(int* srcPixArray, int w, int h, double alpha1, double alpha2)
//{
//	int row, col, i, j, ii, jj;
//	int	*layer1, *layer2; // 两个图层
//	int r, g, b;
//	double *model; // 高斯模板
//	int srcPx, dstPx;
//	int ind;
//
//	layer1 = (int*)malloc(sizeof(int)*w*h);
//	layer2 = (int*)malloc(sizeof(int)*w*h);
//	memcpy(layer2,srcPixArray,w*h*sizeof(int));
//
//	// RGB to Gray Image and inert
//	ind = 0;
//	for(row=0; row<h; row++)
//	{
//		for(col=0; col<w; col++)
//		{
//			srcPx = srcPixArray[ind];
//			getRGB(srcPx, &r,&g,&b);
//			layer1[ind] = 255 - (int)(0.30*r + 0.59*g + 0.11*b);
//			ind++;
//		}
//	}
//
//	// layer1 gauss blur (remember that layer1 is gray image)
//	// and overlay with original image
//
//	model = (double*)malloc(sizeof(double)*5*5);
//	setGaussModel(model, 10, 2);
//	ind = 0;
//	for(row=0; row<h; row++)
//	{
//		for(col=0; col<w; col++)
//		{
//			// blur
//			float blur = 0;
//			for(ii=-2; ii<=2; ii++)
//			{
//				for(jj=-2; jj<=2; jj++)
//				{
//					i = row+ii;
//					j = col+jj;
//					if(i<0) i = -i;
//					if(i>=h) i = 2*h-i;
//					if(j<0) j = -j;
//					if(j>=w) j = 2*w-j;
//					blur += layer1[i*w + j] * model[(ii+2)*5 + 2+jj];
//				}
//			}
//			srcPx = srcPixArray[ind];
//			getRGB(srcPx, &r,&g,&b);
//			// overlay
//			if(r<128)
//				r = 2.0*r*blur/255;
//			else
//				r = 255-2.0*(255-r)*(255-blur)/255;
//			if(g<128)
//				g = 2.0*g*blur/255;
//			else
//				g = 255-2.0*(255-g)*(255-blur)/255;
//			if(b<128)
//				b = 2.0*b*blur/255;
//			else
//				b = 255-2.0*(255-b)*(255-blur)/255;
//			srcPixArray[ind++] = (255<<24) | (r << 16) | (g << 8) | b;
//		}
//	}
//
//	free(layer1);
//	free(model);
//	// layer2 high pass filter
//	model = (double*)malloc(sizeof(double)*7*7);
//	setGaussModel(model, 20, 3);
//	ind = 0;
//	for(row=0; row<h; row++)
//	{
//		for(col=0; col<w; col++)
//		{
//			// blur
//			float br, bg, bb, mm;
//			br = bg = bb = 0;
//			for(ii=-3; ii<=3; ii++)
//			{
//				for(jj=-3; jj<=3; jj++)
//				{
//					i = row+ii;
//					j = col+jj;
//					if(i<0) i = -i;
//					if(i>=h) i = 2*h-i;
//					if(j<0) j = -j;
//					if(j>=w) j = 2*w-j;
//					srcPx = layer2[i*w + j];
//					getRGB(srcPx, &r,&g,&b);
//					mm = model[(ii+3)*7 + 3+jj];
//					br += r * mm;
//					bg += g * mm;
//					bb += b * mm;
//				}
//			}
//			srcPx = layer2[ind];
//			getRGB(srcPx, &r,&g,&b);
//			br = min((100+r-br), 255);
//			bg = min((100+g-bg), 255);
//			bb = min((100+b-bb), 255);
//
//			// overlay with alpha2
//			if(r<128)
//				br = 2.0*r*br/255;
//			else
//				br = 255-2.0*(255-r)*(255-br)/255;
//			r = (1-alpha2)*r + alpha2*br;
//			if(g<128)
//				bg = 2.0*g*bg/255;
//			else
//				bg = 255-2.0*(255-g)*(255-bg)/255;
//			g = (1-alpha2)*g + alpha2*bg;
//			if(b<128)
//				bb = 2.0*b*bb/255;
//			else
//				bb = 255-2.0*(255-b)*(255-bb)/255;
//			b = (1-alpha2)*b + alpha2*bb;
//			srcPixArray[ind++] = (255<<24) | (r << 16) | (g << 8) | b;
//		}
//	}
//	free(model);
//	free(layer2);
//}


void getDynamicFrame(int* frame, int* oriFrame, int w, int h, int oriW, int oriH)
{
	int row, col;
	int *ps, *pf, *pf1;

	if(w<oriW || h<oriH)
		return;

	memset(frame, 0, sizeof(int)*w*h);

	//
	for(row=0; row<oriH/2; ++row)
		for(col=0; col<oriW/2; ++col)
		{
			//
			ps = frame+row*w+col;
			pf = oriFrame+(row+1)*oriW+col+1;
			*ps = *pf;
			//
			ps = frame+row*w + w-col-1;
			pf = oriFrame+(row+1)*oriW + oriW-col-2;
			*ps = *pf;
			//
			ps = frame+(h-row-1)*w + col;
			pf = oriFrame+(oriH-row-2)*oriW + col+1;
			*ps = *pf;
			//
			ps = frame+(h-row-1)*w + w-col-1;
			pf = oriFrame+(oriH-row-2)*oriW + oriW-col-2;
			*ps = *pf;
		}

	//
	for(row=0; row<=oriH/2; ++row)
	{
		pf = oriFrame+(row+1)*oriW + oriW/2;
		pf1 = oriFrame+(oriH-row-2)*oriW + oriW/2;
		for(col=oriW/2; col<=w-oriW/2; ++col)
		{
			//
			ps = frame+row*w+col;
			*ps = *pf;
			//
			ps = frame+(h-row-1)*w+col;
			*ps = *pf;
		}
	}

	//
	for(col=0; col<=oriW/2; ++col)
	{
		pf = oriFrame+(oriH/2)*oriW + col+1;
		pf1 = oriFrame+(oriH/2)*oriW + oriW-col-2;
		for(row=oriW/2; row<=h-oriH/2; ++row)
		{
			//
			ps = frame+row*w+col;
			*ps = *pf;
			//
			ps = frame+row*w + w-col-1;
			*ps = *pf;
		}
	}
}


void cover(int* basePixel, int* topPixel, int w, int h)
{
	Cover(basePixel, topPixel, w, h);
}

/*****************sketch effect**********************/

void sketch(int* srcPixArray, int w, int h)
{
	int n = w*h;
	int x,y,xx,yy,i,j,min_r;
	int r,a,color;
	int rOld,gOld,bOld,gray,grayFinal;
	int rad = 2;

	int *butter = (int*)malloc(n*sizeof(int));
	for( i = 0; i < n; i++)
	{
           getRGBA(srcPixArray[i],&rOld,&gOld,&bOld,&a);
	   gray = (rOld * 19660 + gOld * 38666 + bOld * 7208) >> 16;
	   srcPixArray[i] =  (a << 24) | (gray << 16) | (gray << 8) | gray;
           butter[i] = 255 - gray;
	}


	for( y = 0; y < h; y++)
	{
		for( x = 0; x < w; x++)
		{
			min_r = 255;
			for(i=-rad;i<=rad;i++)
			{
				yy = y+i;
			    if(yy< 0||yy>= h)
					continue;
			   for(j=-rad;j<=rad;j++)
			   {
				   xx = x+j;
			       if(xx<0||xx>=w)
					   continue;

				r = butter[xx+w*yy] ;

		         	if(min_r > r)
			  	{
					min_r = r;
			 	}

		          }
		      }
			color = srcPixArray[x+w*y] ;
			a = (color >> 24) & 0xFF;
			gray = (color >> 16) & 0xFF;

			grayFinal= (int) gray*245/(256-min_r);


			if(grayFinal>255)
			{
				grayFinal=255;
			}

		   srcPixArray[x+w*y] = (a << 24) | (grayFinal << 16) | (grayFinal << 8) | grayFinal;
	     }
    }
	free(butter);

}

void popstyle(int* srcPixArray, int w, int h,int type)
{
    int i, j, k, r, g, b, gray;
    float sum=0,avg;
    float ratio;
	k=0;
    int * grays = malloc(sizeof(int)*w*h);
	for (i = 0; i < h; i++) {
		for (j = 0; j < w; j++) {
            getRGB(srcPixArray[i*w+j],&r,&g,&b);
			gray = (r * 19660 + g * 38666 + b * 7208) >> 16;
            grays[i*w+j] = gray;
            sum+=gray;
		}
	}
    avg= sum/(w*h) * 0.72;
    for (i = 0; i < h; i++) {
		for (j = 0; j < w; j++) {
            
            if (grays[i*w+j]<avg)
            {
                r = 0;
                g = 0;
                b = 0;
            }
            else
            {
                ratio = i*1.0/h;
                if (type==0)
                {
                    r = 0x01*ratio +0xFF*(1-ratio);
                    g = 0xDB*ratio +0xEF*(1-ratio);
                    b = 0xDD*ratio +0x1C*(1-ratio);
                }
            }
            
            srcPixArray[i*w+j] =(255 << 24) | (r << 16) | (g << 8) | b;
		}
	}
    free(grays);
}


void blurBackgroundByCircle(int* srcPixArray, int w, int h, int x, int y, int r0, int r1)
{

	int row, col;
	int color;
	int r, g, b,alpha;
	double d;
	int ind=0;

	if(r1 <= r0)
		return;

	for(row=0; row<h; row++)
	{
		for(col=0; col<w; col++)
		{
			color = srcPixArray[ind];

			r = (color >> 16) & 0xFF;
			g = (color >> 8) & 0xFF;
			b = (color) & 0xFF;

			d = distanceSquare(row,col,y,x);
			d=sqrt(d);

			if(d>r0)
			{
				if(d<=r1)
					alpha = (int)((d-r0)/(r1-r0)*255);

				else
					alpha = 255;

			}
			else
				alpha = 0;
			srcPixArray[ind] = (alpha << 24)|(r << 16) | (g << 8) | b;
			ind++;
		}

	}
}

void blurBackgroundByLine(int* srcPixArray, int w, int h, int x, int y, int d0, int d1, double theta)
{
	float A, B, C;
	int row, col;
	int color;
	int r, g, b,alpha;
	double d;
	int ind=0;
	LOGW("blurBackgroundByLine: theta: %f  w: %d  h: %d  x: %d  y: %d  d0: %d  d1: %d", theta, w, h, x, y, d0, d1);
	if(d1 <= d0)
		return;

	A = sin(theta);
	B = -cos(theta);
	C = -(A*x+B*y);

	for(row=0; row<h; row++)
	{
		for(col=0; col<w; col++)
		{
			color = srcPixArray[ind];

			r = (color >> 16) & 0xFF;
			g = (color >> 8) & 0xFF;
			b = (color) & 0xFF;

			d = abs(A*col+B*row+C);

			if(d>d0)
			{
				if(d<=d1)
					alpha = (int)((d-d0)/(d1-d0)*255);

				else
					alpha = 255;

			}
			else
				alpha = 0;
			srcPixArray[ind] = (alpha << 24)|(r << 16) | (g << 8) | b;
			ind++;
		}

	}
}



void unsharp(int* srcPixArray,int* smoothImg ,int w, int h, int rad, int thresh, float amount)
{
	int convolution(int* color, int dDim , int drift)
	{

		int i,j;
		int all = 0;
		for(i = 0;i<dDim;i++)
		{
			all += ((color[i] >> drift) & 0xFF);
		}
		all = (((color[dDim>>1]>>drift) & 0xFF)<<1)  - all/(dDim);
		return all;
	}
	int dim = amount;
	int dDim = amount * amount;
	int pos;
	int row,col;
	int xDim,yDim;
	int alpha,red,green,blue;
	int color[200];
	for(row = 0; row < h - dim; row++)
	{
		for(col = 0; col < w - dim; col++)
		{
			int dimNum = 0;
			for(xDim = row*w+col;xDim<(row+dim)*w+col+dim;xDim+=w)
			{
				for(yDim = 0;yDim<dim;yDim++)
				{
					color[dimNum++] = smoothImg[xDim + yDim];
				}
			}
			pos = (row+(dim>>1))*w+col+(dim>>1);
			alpha = (smoothImg[col+(dim>>1)+1+w*(row+(dim>>1)+1)] >> 24) & 0xFF;
			red   = convolution(color,dDim,16);
			green = convolution(color,dDim, 8);
			blue  = convolution(color,dDim, 0);

			red=red>255?255:red;
			green=green>255?255:green;
			blue=blue>255?255:blue;

			red=red<0?0:red;
			green=green<0?0:green;
			blue=blue<0?0:blue;
			srcPixArray[pos] = (alpha << 24)|(red << 16) | (green << 8) | blue;
		}
	}
}

void sharpen(int* srcPixArray, int w, int h, int r)
{
	int d = r * 2 - 1;
	int dDim = d * d;
	int pos, p;
	int row,col;
	int x,y;
	int alpha,red,green,blue;
	int* tmpPixArray= (int *)malloc(w * h * sizeof(int));
	for(row = 0; row < h; row++)
	{
		for(col = 0; col < w; col++)
		{
			int allR=0,allG=0,allB=0;
			pos = row * w + col;
			if (row < r - 1 || row > h - r || col < r - 1 || col > w - r) {
				tmpPixArray[pos] = srcPixArray[pos];
				continue;
			}
			for(y = row - r + 1; y < row + r; y ++)
			{
				for(x = col - r + 1; x < col + r; x ++)
				{
					p = y * w + x;
					allR += ((srcPixArray[p] >> 16) & 0xFF);
					allG += ((srcPixArray[p]>> 8) & 0xFF);
					allB += ((srcPixArray[p] >> 0) & 0xFF);
				}
			}

			red = (((srcPixArray[pos]>>16) & 0xFF)<<1)  - allR/(dDim);
			green = (((srcPixArray[pos]>> 8) & 0xFF)<<1)  - allG/(dDim);
			blue = (((srcPixArray[pos]>> 0) & 0xFF)<<1)  - allB/(dDim);

			alpha = (srcPixArray[pos] >> 24) & 0xFF;

			red=red>255?255:red;
			green=green>255?255:green;
			blue=blue>255?255:blue;

			red=red<0?0:red;
			green=green<0?0:green;
			blue=blue<0?0:blue;
			tmpPixArray[pos] = (alpha << 24)|(red << 16) | (green << 8) | blue;
		}
	}
	memcpy(srcPixArray,tmpPixArray, w * h * sizeof(int));
	free(tmpPixArray);

}

void fastAverageBlurWithThreshold(int *srcPixArray, int width, int height, int radius, int threshold) {
	fastAverageBlurWithThresholdAndWeight(srcPixArray, width, height, radius, threshold, 256);
}

void fastAverageBlurWithThresholdAndWeight(int *srcPixArray, int width, int height,
		int radius, int threshold, int weight) {
	fastAverageBlurWithThresholdWeightSkinDetection(srcPixArray, width, height, radius, threshold, weight, -1, -1, -1, -1, -1, -1);
}

void fastAverageBlurWithThresholdWeightSkinDetection(int *srcPixArray, int width, int height,
		int radius, int threshold, int weight, int hmin, int hmax, int smin, int smax, int vmin, int vmax) {
	int size = 2 * radius + 1; //模板直径
	int area = size * size; //模板面积

	int i,k,y; //下标
	int row,col; //行列下标
	int color,r,g,b;
	int r1, g1, b1, a1, r2, g2, b2, a2;
	int index,index1,index2;
	int h, s, v;

	int * tValues = (int *)malloc(3*width * sizeof(int)); //一行元素的模板单列和
	int* tmp= (int *)malloc(width * height * sizeof(int)); //像素rgb临时存储

	memset(tValues, 0, 3*width * sizeof(int) );

	LOGW("radius:%d threshold:%d weight:%d", radius, threshold, weight);

	for( y = 0,index = 0; y < width; y++ ){
		color = srcPixArray[y];
		tValues[index++] += (color >> 16) & 0xFF;
		tValues[index++] += (color >> 8) & 0xFF;
		tValues[index++] += (color) & 0xFF;
	}

	for( k = 1; k <= radius; k++ ){
		for( y = 0,index = 0; y < width; y++ ){
			color = srcPixArray[k*width+y];
			tValues[index++] += ((color >> 16) & 0xFF)<<1;
			tValues[index++] += ((color >> 8) & 0xFF)<<1;
			tValues[index++] += ((color) & 0xFF)<<1;
		}
	}

	for(row = 0; row < height; row++){

		index = 0;
		r = tValues[index++];
		g = tValues[index++];
		b = tValues[index++];
		for(k = 0; k < radius; k++){
			r += tValues[index++] << 1;
			g += tValues[index++] << 1;
			b += tValues[index++] << 1;
		}

		col = 0;

		tmp[row * width + col] = ((int) 255 << 24)
							| ((int) r/area << 16) | ((int) g/area << 8) | (int) b/area;

		for(col = 1; col < width; col++){
			index1 = (col - radius - 1);
			index2 = (col + radius);

			if (index1<0)
				index1 = -index1;
			if (index1>=width)
				index1 = 2*width - index1 - 1;

			if (index2<0)
				index2 = -index2;
			if (index2>=width)
				index2 = 2*width - index2 - 1;

			index1 *= 3;
			index2 *= 3;

			r = r - tValues[ index1 + 0 ] + tValues[ index2 + 0];
			g = g - tValues[ index1 + 1 ] + tValues[ index2 + 1];
			b = b - tValues[ index1 + 2 ] + tValues[ index2 + 2];

			tmp[row * width + col] = ((int) 255 << 24)
							| ((int) r/area << 16) | ((int) g/area << 8) | (int) b/area;
		}

		index1 = row-radius;
		index2 = row + radius + 1;

		if (index1<0)
			index1 = -index1;
		if (index1>=height)
			index1 = 2*height - index1 - 1;

		if (index2<0)
			index2 = -index2;
		if (index2>=height)
			index2 = 2*height - index2 - 1;

		index1 *= width;
		index2 *= width;

		for(y = 0; y < width; y++){
			color = srcPixArray[index1++];
			index = y * 3;
			tValues[index++] -= (color >> 16) & 0xFF;
			tValues[index++] -= (color >> 8) & 0xFF;
			tValues[index] -= (color) & 0xFF;

			color = srcPixArray[index2++];
			index = y * 3;
			tValues[index++] += (color >> 16) & 0xFF;
			tValues[index++] += (color >> 8) & 0xFF;
			tValues[index] += (color) & 0xFF;

		}
	}

	for (i = 0; i < width * height; i ++) {
		getRGBA(srcPixArray[i],&r1,&g1,&b1,&a1);
		if (hmin >= 0) {
			transHSV(r1, g1, b1, &h, &s, &v);
			if (h >= hmin && h <= hmax && s >= smin && s <= smax && v >= vmin && v <= vmax) {
				getRGBA(tmp[i],&r2,&g2,&b2,&a2);
				if (abs(r1 - r2) < threshold) {
					r1 = (r1 * (256 - weight) + r2 * weight) >> 8;
				}
				if (abs(g1 - g2) < threshold) {
					g1 = (g1 * (256 - weight) + g2 * weight) >> 8;
				}
				if (abs(b1 - b2) < threshold) {
					b1 = (b1 * (256 - weight) + b2 * weight) >> 8;
				}
				srcPixArray[i] = ((int) a1 << 24)
									| ((int) r1 << 16) | ((int) g1 << 8) | (int) b1;
			} else {
				if (vmax == 254) {
					srcPixArray[i] = 0;
				}
			}
		} else {
			getRGBA(tmp[i],&r2,&g2,&b2,&a2);
			if (abs(r1 - r2) < threshold) {
				r1 = (r1 * (256 - weight) + r2 * weight) >> 8;
			}
			if (abs(g1 - g2) < threshold) {
				g1 = (g1 * (256 - weight) + g2 * weight) >> 8;
			}
			if (abs(b1 - b2) < threshold) {
				b1 = (b1 * (256 - weight) + b2 * weight) >> 8;
			}
			srcPixArray[i] = ((int) a1 << 24)
								| ((int) r1 << 16) | ((int) g1 << 8) | (int) b1;
		}
	}
//	memcpy(srcPixArray,tmp,width * height * sizeof(int));

	free(tValues);
	free(tmp);
}

void fastAverageBlur(int* srcPixArray, int width, int height,int radius)
{
	LOGW("fastAverageBlur: w: %d  h: %d  radius: %d", width, height, radius);
	fastAverageBlurWithThreshold(srcPixArray, width, height, radius, 256);
}
void getPixelData(int pixelValue,int* argb)
{
	argb[0] = (pixelValue >> 24) & 0xFF;	//alpha
	argb[1] = (pixelValue >> 16) & 0xFF;	//red
	argb[2] = (pixelValue >> 8) & 0xFF;	//green
	argb[3] = (pixelValue >> 0) & 0xFF;	//blue
}

void relief(int* srcPixArray, int width, int height,int increment)
{
	int dataLenth = width*height;
	int x,y;
	int pixel1[4],pixel2[4];
	int * tmpBmp = (int *) malloc(dataLenth*sizeof(int));
	memcpy(tmpBmp, srcPixArray, dataLenth*sizeof(int));
	for(x = 0;x<width-1;x++)
	{
		for(y=0;y<height-1;y++)
		{
			int a = 0,r = 0,g=0,b=0;
			getPixelData(tmpBmp[x+y*width],pixel1);
			getPixelData(tmpBmp[x+1+(y+1)*width],pixel2);
			r = abs(pixel1[1]-pixel2[1] + increment);
			g = abs(pixel1[2]-pixel2[2] + increment);
			b = abs(pixel1[3]-pixel2[3] + increment);

			a = pixel1[0];
			r=r>255?255:r;
			g=g>255?255:g;
			b=b>255?255:b;
			r=r<0?0:r;
			g=g<0?0:g;
		    b=b<0?0:b;

		    srcPixArray[x+y*width] = (a << 24)|(r << 16) | (g << 8) | b;
		}
	}
	free(tmpBmp);
}

void emission(int* srcPixArray, int w, int h)
{
    int section = 360;
    int maxSection = 10;
    int ratio = 10;
	float zoom=0.3;
	float bright = 0.6;
	float zoomchangerate = 2;// 1.7
    float baser = min(w, h)*zoom;
    float centerX = w/2;
    float centerY = h/2;
    int *sectionR = (int*) malloc(section*sizeof(int));
    int sum =0;
    float dx,dy,d,angel,eratio;
    int ex,ey,index;
    int i,j,r,g,b,er,eg,eb;
	unsigned int a,rr;
	float brightrate;
    while(sum<360)
    {
        a = rand()%maxSection+5;
        rr = rand()%ratio+1;
        for(i=sum;i<a+sum && i< section;i++)
        {
            sectionR[i]=rr;
        }
        sum += a;
    }

    for (i = 0; i < h; i++) {
    	for (j = 0; j < w; j++) {
			dx = j - centerX;
			dy = i - centerY;
			d = sqrtf(dx*dx+dy*dy);

			getRGBA(srcPixArray[i*w+j],&r,&g,&b,&a);
			ex = (int)(baser/d *dx + centerX);
			ey = (int)(baser/d *dy + centerY);
			getRGB(srcPixArray[ey*w+ex],&er,&eg,&eb);
			if (d>baser)//800ms
			{
				//if  (dx <0) j = centerX *2- j;

				// we need to do the emission
				if (dx==0)
				{
					angel = M_PI_2;
				}
				else if (dx>0&&dy<=0)
				{
					angel=-atanf(dy/dx);
				}
				else if (dx<0)
				{
					angel=M_PI-atanf(dy/dx);
				}
				else if (dx>0&&dy>0)
				{
					angel=2*M_PI-atanf(dy/dx);
				}
				index = (int)(angel/M_PI*section/2);
				if (index<0) index=0;
				if  (index>section-1) index = section-1;
				eratio= 1 - (ratio-sectionR[index]*0.5)* baser/(d*ratio-sectionR[index]* baser*0.5)*0.9;
				//emission function
				r = eratio * er +(1-eratio)*r;
				g = eratio * eg +(1-eratio)*g;
				b = eratio * eb +(1-eratio)*b;
			}
			// bright emission center

			brightrate = dx*dx/(w*w*zoom*zoom*zoomchangerate)+dy*dy/(h*h*zoom*zoom*zoomchangerate);//200ms
			if (brightrate<1)//100ms
			{
				float bright = 0.6;
				r = r+(1-brightrate)*bright*r;
				if (r>255)r=255;
				g = g+(1-brightrate)*bright*g;
				if (g>255)g=255;
				b = b+(1-brightrate)*bright*b;
				if (b>255)b=255;
			}
			srcPixArray[i*w+j] = (a<<24) | (r << 16) | (g << 8) | b;
		}
	 }
    free(sectionR);
}

void colorLevelFilter(int* srcPixArray, int w, int h, int min, float gray, int max, int outMin, int outMax) {
    int i;
    int temp;
    for (i = 0; i < w * h; i++) {
		int colorT = srcPixArray[i];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;
        int aT = (colorT >> 24) & 0xFF;

        bT = colorLevel(bT, min, gray, max, outMin, outMax);
        gT = colorLevel(gT, min, gray, max, outMin, outMax);
        rT = colorLevel(rT, min, gray, max, outMin, outMax);

        if (bT<0)bT=0;
        if (bT>255)bT=255;
        if (gT<0)gT=0;
        if (gT>255)gT=255;
        if (rT<0)rT=0;
        if (rT>255)rT=255;
		srcPixArray[i] = (aT << 24) | (rT << 16) | (gT << 8) | bT;
	}
}

void saturationfilter(int* srcPixArray, int w, int h , float sValue)
{
	LOGW("saturationfilter: sValue: %f", sValue);
    int i;
    int a,r,g,b;
    for (i = 0; i < w * h; i++) {
		int colorT = srcPixArray[i];
		r = (colorT >> 16) & 0xFF;
		g = (colorT >> 8) & 0xFF;
		b = colorT & 0xFF;
        a = (colorT >> 24) & 0xFF;

        // 利用HSL模式求得颜色的S和L
        float rgbMax = r / 255.0;
        float rgbMin = g / 255.0;
        float rgbC = b / 255.0;
        if (rgbMax < rgbC)
            Swap(&rgbMax, &rgbC);
        if (rgbMax < rgbMin)
            Swap(&rgbMax, &rgbMin);
        if (rgbMin> rgbC)
            Swap(&rgbMin, &rgbC);
        float delta = rgbMax - rgbMin;
        // 如果delta=0，S=0，所以不能调整饱和度
        if (delta == 0) continue;
        float value = rgbMax + rgbMin;
        float S, L = value / 2;
        if (L < 0.5)
            S = delta / value;
        else  S = delta  / (2 - value);
        /*
        // 具体的饱和度调整，sValue为饱和度增减量
        // 如果增减量>0，饱和度呈级数增强，否则线性衰减
        if (sValue> 0)
        {
            // 如果增减量+S > 1，用S代替增减量，以控制饱和度的上限
            // 否则取增减量的补数
            sValue = sValue + S >= 1? S : (1 - sValue);
            // 求倒数 - 1，实现级数增强
            sValue = 1 / sValue - 1;
        }*/
        // L在此作饱和度下限控制
        r = r + (r - L * 255) * sValue +3;
        g = g + (g - L * 255) * sValue +3;
        b = b + (b - L * 255) * sValue +3;

        if (b<0)b=0;
        if (b>255)b=255;
        if (g<0)g=0;
        if (g>255)g=255;
        if (r<0)r=0;
        if (r>255)r=255;
		srcPixArray[i] = (a << 24) | (r << 16) | (g << 8) | b;
    }
}
