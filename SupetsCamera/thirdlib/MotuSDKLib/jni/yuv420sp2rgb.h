#include <string.h>
#include <jni.h>
#include <android/log.h>

#ifndef max
#define max(a,b) ({typeof(a) _a = (a); typeof(b) _b = (b); _a > _b ? _a : _b; })
#define min(a,b) ({typeof(a) _a = (a); typeof(b) _b = (b); _a < _b ? _a : _b; })
#endif

#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "Native", __VA_ARGS__)

const int bytes_per_pixel = 2;

//ith row, jth column
static inline unsigned char *pGetY(unsigned char *y, int w, int i, int j) {
	return (y + i * w + j);
}

static inline unsigned char *pGetV(unsigned char *uv, int w, int i, int j) {
	return uv + (i / 2) * w + bytes_per_pixel * (j / 2);
}

static inline unsigned char *pGetU(unsigned char *uv, int w, int i, int j) {
	return uv + (i / 2) * w + bytes_per_pixel * (j / 2) + 1;
}

static inline void yuv2rgb(int y, int u, int v, int *r, int *g, int *b) {
	y -= 16;
	u -= 128;
	v -= 128;

	if (y < 0)
		y = 0;

	*r = (int) (1192 * y + 1634 * v);
	*g = (int) (1192 * y - 400 * u - 833 * v);
	*b = (int) (1192 * y + 2066 * u);

	*r = min(262143, max(0, *r));
	*g = min(262143, max(0, *g));
	*b = min(262143, max(0, *b));

	*r >>= 10;
	*r &= 0xff;
	*g >>= 10;
	*g &= 0xff;
	*b >>= 10;
	*b &= 0xff;
}

static inline void color_convert_common(unsigned char *pY, unsigned char *pUV,
		int width, int height, unsigned char *buffer) {
	int i, j;
	int nR, nG, nB;
	int nY, nU, nV;
	unsigned char *out = buffer;
	int offset = 0;
	// YUV 4:2:0
	for (i = 0; i < height; i++) {
		for (j = 0; j < width; j++) {
//			LOGW("i:%d  j:%d  h:%d  w:%d", i, j, height, width);
			nY = *pGetY(pY, width, i, j);
			nU = *pGetU(pUV, width, i, j);
			nV = *pGetV(pUV, width, i, j);
			yuv2rgb(nY, nU, nV, &nR, &nG, &nB);
			// Yuv Convert
			out[offset++] = (unsigned char) nR;
			out[offset++] = (unsigned char) nG;
			out[offset++] = (unsigned char) nB;
		}
	}
}

static inline void rgbResize(unsigned char *ori, int ow, int oh,
		unsigned char *dst, int dw, int dh) {
	int i, dr, dc, orr, oc, di;
	for (i = 0; i < dw * dh; i++) {
		dr = i / dw;
		dc = i % dw;
		orr = dr * oh / dh;
		oc = dc * ow / dw;
		di = orr * ow + oc;
		dst[i * 3] = ori[di * 3];
		dst[i * 3 + 1] = ori[di * 3 + 1];
		dst[i * 3 + 2] = ori[di * 3 + 2];
	}
}

static inline void rotateArray(unsigned char *a, unsigned char *b,
		unsigned char *c, unsigned char *d, int n) {
	unsigned char temp;
	switch (n) {
	case 1:
		temp = *a;
		*a = *d;
		*d = *c;
		*c = *b;
		*b = temp;
		break;
	case 2:
		temp = *a;
		*a = *c;
		*c = temp;
		temp = *b;
		*b = *d;
		*d = temp;
		break;
	case 3:
		temp = *a;
		*a = *b;
		*b = *c;
		*c = *d;
		*d = temp;
		break;
	}
}

/**
 * rotate a square image in place.
 * direction from 0 ~ 3, indicating CW rotate degree 0, 90, 180, 270.
 */
static inline void rgbRotate(unsigned char *pixels, int size, int direction) {
	int i, j;
	int ll, lh, hl, hh;
	unsigned char temp;
	for (i = 0; i < (size + 1) / 2; i ++) {
		for (j = 0; j < size / 2; j ++) {
			ll = i * size + j;
			lh = j * size + size - i - 1;
			hh = (size - i - 1) * size + size - j - 1;
			hl = (size - j - 1) * size + i;
			rotateArray(pixels + ll * 3, pixels + lh * 3, pixels + hh * 3,
					pixels + hl * 3, direction);
			rotateArray(pixels + ll * 3 + 1, pixels + lh * 3 + 1,
					pixels + hh * 3 + 1, pixels + hl * 3 + 1, direction);
			rotateArray(pixels + ll * 3 + 2, pixels + lh * 3 + 2,
					pixels + hh * 3 + 2, pixels + hl * 3 + 2, direction);
		}
	}
}

static inline void yuvResize(unsigned char *oy, unsigned char *ouv, int ow, int oh,
		unsigned char *dy, unsigned char *duv, int dw, int dh) {
	int i, j, oi, oj;
	for (i = 0; i < dh; i ++) {
		for (j = 0; j < dw; j ++) {
			oi = i * oh / dh;
			oj = j * ow / dw;
			*pGetY(dy, dw, i, j) = *pGetY(oy, ow, oi, oj);
			*pGetU(duv, dw, i, j) = *pGetU(ouv, ow, oi, oj);
			*pGetV(duv, dw, i, j) = *pGetV(ouv, ow, oi, oj);
		}
	}
}

static inline void yuv2rgbResize(unsigned char *oy, unsigned char *ouv, int ow, int oh,
		unsigned char *rgb, int dw, int dh) {
	int i, j, oi, oj;
	int y, u, v, r, g, b;
	int offset = 0;
	for (i = 0; i < dh; i ++) {
		for (j = 0; j < dw; j ++) {
			oi = i * oh / dh;
			oj = j * ow / dw;
			y = *pGetY(oy, ow, oi, oj);
			u = *pGetU(ouv, ow, oi, oj);
			v = *pGetV(ouv, ow, oi, oj);
			yuv2rgb(y, u, v, &r, &g, &b);
			rgb[offset++] = (unsigned char) r;
			rgb[offset++] = (unsigned char) g;
			rgb[offset++] = (unsigned char) b;
		}
	}
}

static inline void yuv2rgbBitmap(unsigned char *pY, unsigned char *pUV,
		int width, int height, int* pixels) {
	int i, j;
	int nR, nG, nB;
	int nY, nU, nV;
	int offset = 0;
	// YUV 4:2:0
	for (i = 0; i < height; i++) {
		for (j = 0; j < width; j++) {
//			LOGW("i:%d  j:%d  h:%d  w:%d", i, j, height, width);
			nY = *pGetY(pY, width, i, j);
			nU = *pGetU(pUV, width, i, j);
			nV = *pGetV(pUV, width, i, j);
			yuv2rgb(nY, nU, nV, &nR, &nG, &nB);
			pixels[offset++] = 0xff000000 | (nR << 16) | (nG << 8) | nB;
		}
	}
}
