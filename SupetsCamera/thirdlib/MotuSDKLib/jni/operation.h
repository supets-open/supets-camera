#ifndef OPERATION_H
#define OPERATION_H

typedef struct {
	int expect;
	int var;
}ExpVar;

/* 
enum{
     EYE_ENLARG_EFFECT = 1,
     THIN_EFFECT = 2
};
*/
static inline void scopeLimit(int* v) {
	if (*v < 0) {
		*v = 0;
	}
	if (*v > 255) {
		*v = 255;
	}
}

static inline int interpolation(double px,double py,int ll,int lh,int hl,int hh)
{
    return (ll*(1-py) + hl*py) * (1-px)	+(lh*(1-py) + hh*py) * px;
}

static inline float distanceSquare(int x,int y,int x2,int y2)
{
       return (y2-y)*(y2-y)+(x2-x)*(x2-x);
}

static inline void getRGB(int color, int* red ,int* green, int* blue)
{
       *red = ((color >> 16) & 0xFF);
       *green = ((color >> 8) & 0xFF);
       *blue = ((color) & 0xFF);       
}

static inline void getRGBA(int color, int* red ,int* green, int* blue,int *alpha)
{
	   *alpha = ((color >> 24) & 0xFF);
       *red = ((color >> 16) & 0xFF);
       *green = ((color >> 8) & 0xFF);
       *blue = ((color) & 0xFF);
}

static inline int getG(int color) {
	return ((color >> 8) & 0xFF);
}

static inline int getR(int color) {
	return ((color >> 16) & 0xFF);
}

static inline int getB(int color) {
	return (color & 0xFF);
}

static inline int setA(int *color, int a) {
	(*color) = (*color) & 0x00FFFFFF;
	(*color) = (*color) | (a << 24);
	return (*color);
}

static inline int setG(int *color, int c) {
	(*color) = (*color) & 0xFFFF00FF;
	(*color) = (*color) | (c << 8);
	return (*color);
}

static inline int setR(int *color, int c) {
	*color = (*color) & 0xFF00FFFF;
	(*color) = (*color) | (c << 16);
	return (*color);
}

static inline int setB(int *color, int c) {
	(*color) = (*color) & 0xFFFFFF00;
	(*color) = (*color) | c;
	return (*color);
}

static inline void adjustRGB(int* red, int* green, int* blue)
{
       *red = (*red < 0) ? 0 : ((*red > 255) ? 255 : *red);
       *green = (*green < 0) ? 0 : ((*green > 255) ? 255 : *green);
       *blue = (*blue < 0) ? 0 : ((*blue > 255) ? 255 : *blue);
}

static inline void get3x3FilterIndex(int* filterIndex, int width, int mid)
{       
       filterIndex[1] = mid-width;
       filterIndex[0] = filterIndex[1]-1;
       filterIndex[2] = filterIndex[1]+1;
       
       filterIndex[4] = mid;
       filterIndex[3] = mid-1;
       filterIndex[5] = mid+1;
       
       filterIndex[7] = mid + width;
       filterIndex[6] = filterIndex[7]-1;
       filterIndex[8] = filterIndex[7]+1;
}

static inline void transRgb2Lab(int* red, int* green,int* blue)
{
       int l,a,b;
       l = (13933 * (*red) + 46871 * (*green) + 4732 * (*blue)) >> 16;
       a = ((377 * (14503 * (*red) - 22218 *(*green) + 7714 * (*blue))) >> 24) + 128;
       b = ((160 * (12773 * (*red) + 39695 * (*green) - 52468 * (*blue))) >> 24) + 128;
       *red = l;
       *green = a;
       *blue = b;
}

static inline void transLab2Rgb(int* red, int* green,int* blue)
{
       int l,a,b;
	   l = (*red);
       a = (*green-128)*174;
       b = (*blue-128)*410;
    	    	
       *red = l + ((a * 100922 + b * 17790) >> 23);
   	   *green = l - ((a * 30176 + b * 1481) >> 23);
       *blue = l + ((a * 1740 - b * 37719) >> 23);
       adjustRGB(red,green,blue);       
}

static inline void blendTwoPixels(int* pixelBase, int* pixelTop)
{
	int rb, gb, bb,
		rt, gt, bt, at;
	rb = ((*pixelBase)>>16) & 0xff;
	gb = ((*pixelBase)>>8) & 0xff;
	bb = (*pixelBase) & 0xff;

	at = ((*pixelTop)>>24) & 0xff;
	rt = ((*pixelTop)>>16) & 0xff;
	gt = ((*pixelTop)>>8) & 0xff;
	bt = (*pixelTop) & 0xff;

	rb = rb + (rt-rb)*at/255;
	gb = gb + (gt-gb)*at/255;
	bb = bb + (bt-bb)*at/255;
	*pixelBase = (255<<24) | ((unsigned char)rb << 16) |
		((unsigned char)gb << 8) |
		(unsigned char)bb;
}

static inline void Swap(float* a, float* b)
{
    *a += *b;
    *b = *a - *b;
    *a -= *b;
}

static inline int gan(int n) {
	if (n > 255) {
		return 255;
	}
	if (n < 0) {
		return 0;
	}
	return n;
}

extern void convertRgb2Yuv(int* pixels, int w, int h);
extern void convertYuv2Rgb(int* pixels, int w, int h);
extern void makeHistogram(int* pixels, int width, int height, int* hR, int* hG, int* hB);
extern int cubic(int x);
extern int computeCubicSplineInterpolation(int x0, int y0, int x1, int y1, int x2, int y2, int h0, int h1, double z1, int x);
extern void initCubicSplineInterpolation(int x0, int y0, int x1, int y1, int x2, int y2, int* outh0, int* outh1, double* outz1);

extern void transARGB(int* pixels, int width, int height, int* transfunc);
extern void transRGB(int* pixels, int width, int height, int* transfunc);

extern int getAvarage(int *pixels, int w, int h, int x, int y, int ra);

extern void FilterMode(int *dst, int lWidth, int lHeight, double *dMode, int threshold);

extern void fastAverageBlur1(int* srcPixArray, int width, int height,int radius);

extern void LinearGradient(int* pixels, int colorFrom, int colorTo, int w, int h, int x, int y, int r);

extern void AdjustBrightness(int* srcPixArray, int w, int h, float IncRate);

extern void minfilter(int* srcPixArray, int w, int h, int rad);

extern void Invert(int* srcPixArray, int w, int h);

extern int colorLevel(int input,float minInput,float mid, float maxInput,float minOutput, float maxOutput);

extern void yuv420sp2rgb(int* rgb, unsigned char* yuv420sp, int w, int h);

extern void transToGray(int* src, int width, int height, unsigned char* dst);
extern int max(int a, int b, int c);
extern int min(int a, int b, int c);
extern void transHSV(int r, int g, int b, int* h, int* s, int* v);
extern void transReversedBGR(int* src, int width, int height, unsigned char* dst);
extern ExpVar computeHueExpectationAndVariance(int* src, int width, int height);
#endif
