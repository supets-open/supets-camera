#include "layer.h"

#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)

int sqrt256[] = { 0, 100, 141, 173, 200, 223, 244, 264, 282, 300, 316, 331, 346,
		360, 374, 387, 400, 412, 424, 435, 447, 458, 469, 479, 489, 500, 509,
		519, 529, 538, 547, 556, 565, 574, 583, 591, 600, 608, 616, 624, 632,
		640, 648, 655, 663, 670, 678, 685, 692, 700, 707, 714, 721, 728, 734,
		741, 748, 754, 761, 768, 774, 781, 787, 793, 800, 806, 812, 818, 824,
		830, 836, 842, 848, 854, 860, 866, 871, 877, 883, 888, 894, 900, 905,
		911, 916, 921, 927, 932, 938, 943, 948, 953, 959, 964, 969, 974, 979,
		984, 989, 994, 1000, 1004, 1009, 1014, 1019, 1024, 1029, 1034, 1039,
		1044, 1048, 1053, 1058, 1063, 1067, 1072, 1077, 1081, 1086, 1090, 1095,
		1100, 1104, 1109, 1113, 1118, 1122, 1126, 1131, 1135, 1140, 1144, 1148,
		1153, 1157, 1161, 1166, 1170, 1174, 1178, 1183, 1187, 1191, 1195, 1200,
		1204, 1208, 1212, 1216, 1220, 1224, 1228, 1232, 1236, 1240, 1244, 1248,
		1252, 1256, 1260, 1264, 1268, 1272, 1276, 1280, 1284, 1288, 1292, 1296,
		1300, 1303, 1307, 1311, 1315, 1319, 1322, 1326, 1330, 1334, 1337, 1341,
		1345, 1349, 1352, 1356, 1360, 1363, 1367, 1371, 1374, 1378, 1382, 1385,
		1389, 1392, 1396, 1400, 1403, 1407, 1410, 1414, 1417, 1421, 1424, 1428,
		1431, 1435, 1438, 1442, 1445, 1449, 1452, 1456, 1459, 1462, 1466, 1469,
		1473, 1476, 1479, 1483, 1486, 1489, 1493, 1496, 1500, 1503, 1506, 1509,
		1513, 1516, 1519, 1523, 1526, 1529, 1532, 1536, 1539, 1542, 1545, 1549,
		1552, 1555, 1558, 1562, 1565, 1568, 1571, 1574, 1577, 1581, 1584, 1587,
		1590, 1593, 1596 };

void Darken(int* pixels, int* pixelsLayer, int w, int h) { //�䰵(Darken)
	int i;
	for (i = 0; i < w * h; i++) {
		int colorT = pixelsLayer[i];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;
		// int aT = (colorT >> 24) & 0xFF;

		int color = pixels[i];
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		int a = (color >> 24) & 0xFF;

		r = r < rT ? r : rT;
		g = g < gT ? g : gT;
		b = b < bT ? b : bT;

		pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
	}
}

void Lighten(int* pixels, int* pixelsLayer, int w, int h) { //����(Lighten)
	int i;
	for (i = 0; i < w * h; i++) {
		int colorT = pixelsLayer[i];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;
		int aT = (colorT >> 24) & 0xFF;

		int color = pixels[i];
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		int a = (color >> 24) & 0xFF;

		r = ((r < rT ? rT : r) * aT + r * (255 - aT)) / 255;
		g = ((g < gT ? gT : g) * aT + g * (255 - aT)) / 255;
		b = ((b < bT ? bT : b) * aT + b * (255 - aT)) / 255;

		pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
	}
}


void MultiplyAlpha(int* pixels, int* pixelsLayer, int w, int h, int alpha) { //Multiply(��Ƭ����)
	//RSMultiplyAlpha(pixels, pixelsLayer, w, h, w, h, alpha);
	int i;
	for (i = 0; i < w * h; i++) {
		int colorT = pixelsLayer[i];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;
		int aT = (colorT >> 24) & 0xFF;

		int color = pixels[i];
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		int a = (color >> 24) & 0xFF;

		int rs = (rT - 255) * r * aT / 65025 + r;
		int gs = (gT - 255) * g * aT / 65025 + g;
		int bs = (bT - 255) * b * aT / 65025 + b;

		if (rs < 0)
			rs = 0;
		if (rs > 255)
			rs = 255;
		if (gs < 0)
			gs = 0;
		if (gs > 255)
			gs = 255;
		if (bs < 0)
			bs = 0;
		if (bs > 255)
			bs = 255;

		r = (rs * alpha + r * (100 - alpha)) / 100;
		g = (gs * alpha + g * (100 - alpha)) / 100;
		b = (bs * alpha + b * (100 - alpha)) / 100;
		pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
	}

}

//interpolation if pixels width and height(pw ph) is different from w and h
void RSMultiplyAlpha(int* pixels, int* pixelsLayer, int w, int h, int pw, int ph, int alpha) { //Multiply(��Ƭ����)
	int i, j;
	for (i = 0; i < h; i++) {
		for (j = 0; j < w; j++) {
			int p = i * w + j;
			int pi = i * ph / h;
			int pj = j * pw / w;
			int colorT = pixelsLayer[pi * pw + pj];
			int rT = (colorT >> 16) & 0xFF;
			int gT = (colorT >> 8) & 0xFF;
			int bT = colorT & 0xFF;
			int aT = (colorT >> 24) & 0xFF;
			//Premultiplied
			//        rT = rT*255.0/aT;
			//        gT = gT*255.0/aT;
			//        bT = bT*255.0/aT;

			int color = pixels[p];
			int r = (color >> 16) & 0xFF;
			int g = (color >> 8) & 0xFF;
			int b = (color) & 0xFF;
			int a = (color >> 24) & 0xFF;

			//		LOGW("%d  %d  %d  %d    %d  %d  %d  %d", r, g, b, a, rT, gT, bT, aT);

			//        float alpha = aT/255.0;
			//		int tempr = r * rT / 255;
			//        r = tempr*alpha + r*(1-alpha);

			//        int tempg = g * gT / 255;
			//        g = tempg*alpha + g*(1-alpha);
			//
			//        int tempb = b * bT / 255;
			//        b = tempb*alpha + b*(1-alpha);

			int rs = (rT - 255) * r * aT / 65025 + r;
			int gs = (gT - 255) * g * aT / 65025 + g;
			int bs = (bT - 255) * b * aT / 65025 + b;

			if (rs < 0)
				rs = 0;
			if (rs > 255)
				rs = 255;
			if (gs < 0)
				gs = 0;
			if (gs > 255)
				gs = 255;
			if (bs < 0)
				bs = 0;
			if (bs > 255)
				bs = 255;

			r = (rs * alpha + r * (100 - alpha)) / 100;
			g = (gs * alpha + g * (100 - alpha)) / 100;
			b = (bs * alpha + b * (100 - alpha)) / 100;
			pixels[p] = (a << 24) | (r << 16) | (g << 8) | b;

		}
	}
}

void Multiply(int* pixels, int* pixelsLayer, int w, int h) { //Multiply(��Ƭ����)
	int i;
	for (i = 0; i < w * h; i++) {
		int colorT = pixelsLayer[i];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;
		int aT = (colorT >> 24) & 0xFF;

		int color = pixels[i];
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		int a = (color >> 24) & 0xFF;

		r = (rT - 255) * r * aT / 65025 + r;
		g = (gT - 255) * g * aT / 65025 + g;
		b = (bT - 255) * b * aT / 65025 + b;

		if (r < 0)
			r = 0;
		if (r > 255)
			r = 255;
		if (g < 0)
			g = 0;
		if (g > 255)
			g = 255;
		if (b < 0)
			b = 0;
		if (b > 255)
			b = 255;

		pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
	}
}

//interpolation if pixels width and height(pw ph) is different from w and h
void RSMultiply(int* pixels, int* pixelsLayer, int w, int h, int pw, int ph) { //Multiply(��Ƭ����)
	int i, j;
	for (i = 0; i < h; i++) {
		for (j = 0; j < w; j++) {
			int p = i * w + j;
			int pi = i * ph / h;
			int pj = j * pw / w;
			int colorT = pixelsLayer[pi * pw + pj];
			int rT = (colorT >> 16) & 0xFF;
			int gT = (colorT >> 8) & 0xFF;
			int bT = colorT & 0xFF;
			int aT = (colorT >> 24) & 0xFF;
			//Premultiplied
			//        rT = rT*255.0/aT;
			//        gT = gT*255.0/aT;
			//        bT = bT*255.0/aT;

			int color = pixels[p];
			int r = (color >> 16) & 0xFF;
			int g = (color >> 8) & 0xFF;
			int b = (color) & 0xFF;
			int a = (color >> 24) & 0xFF;

			//		LOGW("%d  %d  %d  %d    %d  %d  %d  %d", r, g, b, a, rT, gT, bT, aT);

			//        float alpha = aT/255.0;
			//		int tempr = r * rT / 255;
			//        r = tempr*alpha + r*(1-alpha);

			//        int tempg = g * gT / 255;
			//        g = tempg*alpha + g*(1-alpha);
			//
			//        int tempb = b * bT / 255;
			//        b = tempb*alpha + b*(1-alpha);

			r = (rT - 255) * r * aT / 65025 + r;
			g = (gT - 255) * g * aT / 65025 + g;
			b = (bT - 255) * b * aT / 65025 + b;

			if (r < 0)
				r = 0;
			if (r > 255)
				r = 255;
			if (g < 0)
				g = 0;
			if (g > 255)
				g = 255;
			if (b < 0)
				b = 0;
			if (b > 255)
				b = 255;

			pixels[p] = (a << 24) | (r << 16) | (g << 8) | b;
		}
	}
}

//ps lv se 滤色
void Screen(int* pixels, int* pixelsLayer, int w, int h) {	//Screen(��ɫ_��Ļ)
	int i;
	for (i = 0; i < w * h; i++) {
		int colorT = pixelsLayer[i];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;

		int color = pixels[i];
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		int a = (color >> 24) & 0xFF;

		r = (int) 255 - (255 - r) * (255 - rT) / 255;
		g = (int) 255 - (255 - g) * (255 - gT) / 255;
		b = (int) 255 - (255 - b) * (255 - bT) / 255;

		pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
	}
}

//ps die jia 叠加
void Overlay(int* pixels, int* pixelsLayer, int w, int h) {
	OverlayAlpha(pixels, pixelsLayer, w, h,100);
}

void OverlayAlpha(int* pixels, int* pixelsLayer, int w, int h, int alpha) {
	//LOGW("overlayalpha: %d", alpha);
	int i;
	int rs, gs, bs;
	for (i = 0; i < w * h; i++) {
		int colorT = pixelsLayer[i];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;

		int color = pixels[i];
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		int a = (color >> 24) & 0xFF;

		if (r < 128)
			rs = (int) 2 * r * rT / 255;
		else
			rs = (int) 255 - 2 * (255 - r) * (255 - rT) / 255;

		if (g < 128)
			gs = (int) 2 * g * gT / 255;
		else
			gs = (int) 255 - 2 * (255 - g) * (255 - gT) / 255;

		if (b < 128)
			bs = (int) 2 * b * bT / 255;
		else
			bs = (int) 255 - 2 * (255 - b) * (255 - bT) / 255;
		r = (rs * alpha + r * (100 - alpha)) / 100;
		g = (gs * alpha + g * (100 - alpha)) / 100;
		b = (bs * alpha + b * (100 - alpha)) / 100;
		pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
	}
}

void RSOverlay(int* pixels, int* pixelsLayer, int w, int h, int pw, int ph) {
	RSOverlayAlpha(pixels, pixelsLayer, w, h, pw, ph, 100);
}

void RSOverlayAlpha(int* pixels, int* pixelsLayer, int w, int h, int pw, int ph, int alpha) {	//Overlay(����)
	//LOGW("overlayalpha: %d", alpha);
	int i;
	int rs, gs, bs;
	for (i = 0; i < w * h; i++) {
		int pIndex = switchIndex(w, h, pw, ph, i);
		int colorT = pixelsLayer[pIndex];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;

		int color = pixels[i];
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		int a = (color >> 24) & 0xFF;

		if (r < 128)
			rs = (int) 2 * r * rT / 255;
		else
			rs = (int) 255 - 2 * (255 - r) * (255 - rT) / 255;

		if (g < 128)
			gs = (int) 2 * g * gT / 255;
		else
			gs = (int) 255 - 2 * (255 - g) * (255 - gT) / 255;

		if (b < 128)
			bs = (int) 2 * b * bT / 255;
		else
			bs = (int) 255 - 2 * (255 - b) * (255 - bT) / 255;
		r = (rs * alpha + r * (100 - alpha)) / 100;
		g = (gs * alpha + g * (100 - alpha)) / 100;
		b = (bs * alpha + b * (100 - alpha)) / 100;
		pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
	}
}

void RSCoverage(int* pixels, int* pixelsLayer, int w, int h, int pw, int ph) {
	int i;
	int rs, gs, bs;
	for (i = 0; i < w * h; i++) {
		int pIndex = switchIndex(w, h, pw, ph, i);
		int colorT = pixelsLayer[pIndex];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;
		int aT = (colorT >> 24) & 0xFF;

		int color = pixels[i];
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		int a = (color >> 24) & 0xFF;

		r = rT*aT/255 + r*(255-aT)/255;
		g = gT*aT/255 + g*(255-aT)/255;
		b = bT*aT/255 + b*(255-aT)/255;

		pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
	}
}

static int getSoftValue(int base, int blend, int alpha) {
	int x = base, y = blend, a = alpha;
	int v;
	if (blend <= 128) {
		v = a * x * y / 32512 - a * x * x * y / 8290687 + a * x * x / 65205 + x
				- a * x / 255;
	} else {
		v = a * (y - 127) * sqrt256[x] / 203600 - a * x * y / 32512
				+ a * x / 255 + x;
	}
	if (v > 255)
		v = 255;
	if (v < 0)
		v = 0;
	return v;
}

void SoftLight(int* pixels, int* pixelsLayer, int w, int h) {	//Soft Light(柔光)
	int i, j;
	int colorBlendArr[3];
	int colorBaseArr[3];
	for (i = 0; i < w * h; i++) {

		int colorBlend = pixelsLayer[i];
		colorBlendArr[0] = (colorBlend >> 16) & 0xFF;
		colorBlendArr[1] = (colorBlend >> 8) & 0xFF;
		colorBlendArr[2] = colorBlend & 0xFF;
		int blenda = (colorBlend >> 24) & 0xFF;
		//Premultiplied
//        colorBlendArr[0] = colorBlendArr[0]*255.0/blenda;
//        colorBlendArr[1] = colorBlendArr[1]*255.0/blenda;
//        colorBlendArr[2] = colorBlendArr[2]*255.0/blenda;

		int color = pixels[i];

		colorBaseArr[0] = (color >> 16) & 0xFF;
		colorBaseArr[1] = (color >> 8) & 0xFF;
		colorBaseArr[2] = (color) & 0xFF;
		int a = (color >> 24) & 0xFF;

		for (j = 0; j < 3; j++) {
			colorBlendArr[j] = getSoftValue(colorBaseArr[j], colorBlendArr[j],
					blenda);
		}

		pixels[i] = (a << 24) | (colorBlendArr[0] << 16)
				| (colorBlendArr[1] << 8) | colorBlendArr[2];
	}
}

void Cover(int* basePixels, int* topPixels, int w, int h) {
	int row, col, ind;
	int colorB, colorT;
	int rB, gB, bB, aB, rT, gT, bT, aT, r, g, b;
	ind = 0;
	for (row = 0; row < h; row++) {
		for (col = 0; col < w; col++) {
			colorB = basePixels[ind];
			colorT = topPixels[ind];
			aB = (colorB >> 24) & 0xFF;
			rB = (colorB >> 16) & 0xFF;
			gB = (colorB >> 8) & 0xFF;
			bB = (colorB) & 0xFF;

			aT = (colorT >> 24) & 0xFF;
			rT = (colorT >> 16) & 0xFF;
			gT = (colorT >> 8) & 0xFF;
			bT = (colorT) & 0xFF;

			r = rB + (rT - rB) * aT / 255;
			g = gB + (gT - gB) * aT / 255;
			b = bB + (bT - bB) * aT / 255;
			basePixels[ind++] = (aB << 24) | (r << 16) | (g << 8) | b;
		}
	}
}

void AlphaComposite(int* basePixels, int* topPixels, int w, int h, float alpha) {
	int row, col, ind;
	int colorB, colorT;
	int rB, gB, bB, aT, rT, gT, bT, r, g, b;
	ind = 0;
	for (row = 0; row < h; row++) {
		for (col = 0; col < w; col++) {
			colorB = basePixels[ind];
			colorT = topPixels[ind];

			aT = (colorB >> 24) & 0xFF;
			rB = (colorB >> 16) & 0xFF;
			gB = (colorB >> 8) & 0xFF;
			bB = (colorB) & 0xFF;

			rT = (colorT >> 16) & 0xFF;
			gT = (colorT >> 8) & 0xFF;
			bT = (colorT) & 0xFF;

			r = rB + (rT - rB) * alpha;
			g = gB + (gT - gB) * alpha;
			b = bB + (bT - bB) * alpha;
			basePixels[ind++] = (aT << 24) | (r << 16) | (g << 8) | b;
		}
	}
}

//ps xian xing jian dan 线性减淡
void LinearDodge(int* pixels, int* pixelsLayer, int w, int h) {
	int i;
	for (i = 0; i < w * h; i++) {
		int colorT = pixelsLayer[i];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;

		int color = pixels[i];
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		int a = (color >> 24) & 0xFF;

		/*		r = (int) r-(256-rT);
		 g = (int) g-(256-gT);
		 b = (int) b-(256-bT);*/
		r = r + rT;
		g = g + gT;
		b = b + bT;

		if (r > 255)
			r = 255;
		if (g > 255)
			g = 255;
		if (b > 255)
			b = 255;
		if (r < 0)
			r = 0;
		if (g < 0)
			g = 0;
		if (b < 0)
			b = 0;

		pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
	}
}

void Dodge(int* pixels, int* pixelsLayer, int w, int h) {
	int i;
	for (i = 0; i < w * h; i++) {
		int colorT = pixelsLayer[i];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;

		int color = pixels[i];
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		int a = (color >> 24) & 0xFF;

		r = (int) rT * 245 / (256 - r);
		g = (int) gT * 245 / (256 - g);
		b = (int) bT * 245 / (256 - b);

		if (r > 255)
			r = 255;
		if (g > 255)
			g = 255;
		if (b > 255)
			b = 255;

		pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
	}
}
void ColorBurn(int* pixels, int* pixelsLayer, int w, int h) {
	int i;
	for (i = 0; i < w * h; i++) {
		int colorT = pixelsLayer[i];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;
		int aT = (colorT >> 24) & 0xFF;
		//Premultiplied
//		rT = rT*255.0/aT;
//		gT = gT*255.0/aT;
//		bT = bT*255.0/aT;

		int color = pixels[i];
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		int a = (color >> 24) & 0xFF;

//		float alpha = aT/255.0 ;
		int tempr = 0, tempg = 0, tempb = 0;

		if (rT > 0)
			tempr = 255 - (255 - r) * 255 / rT;
		if (tempr < 0)
			tempr = 0;
		r = (tempr * aT + r * (255 - aT)) / 255;

		if (gT > 0)
			tempg = 255 - (255 - g) * 255 / gT;
		if (tempg < 0)
			tempg = 0;
		g = (tempg * aT + g * (255 - aT)) / 255;
		;

		if (bT > 0)
			tempb = 255 - (255 - b) * 255 / bT;
		if (tempb < 0)
			tempb = 0;
		b = (tempb * aT + b * (255 - aT)) / 255;
		;

		if (r < 0)
			r = 0;
		if (r > 255)
			r = 255;
		if (g < 0)
			g = 0;
		if (g > 255)
			g = 255;
		if (b < 0)
			b = 0;
		if (b > 255)
			b = 255;

		pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
	}
}

// 将图片像素的index，转化成对应行列的layer像素的index。
// 当图片与layer的尺寸不同时，你需要这个方法。
int switchIndex(int w, int h, int pw, int ph, int index) {
	// 当前index对应于原图的第几行
	int currentLine = index / w;
	// 对应于layer的第几行
	int correspondingLine = currentLine * ph / h;

	// 当前index对应于原图的第几列
	int currentColumn = index % w;
	// 对应于layer的第几列
	int correspondingColumn = currentColumn * pw / w;

	int correspondingIndex = correspondingLine * pw + correspondingColumn;
	return correspondingIndex;
}

void RSScreenWithLimitedLayer(int* pixels, int* pixelsLayer, int w, int h, int lw,
		int lh) {
	int i;
	for (i = 0; i < w * h; i++) {
		int pIndex = switchIndex(w, h, lw, lh, i);
		int colorT = pixelsLayer[pIndex];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;

		int color = pixels[i];
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		int a = (color >> 24) & 0xFF;

		r = (int) 255 - (255 - r) * (255 - rT) / 255;
		g = (int) 255 - (255 - g) * (255 - gT) / 255;
		b = (int) 255 - (255 - b) * (255 - bT) / 255;

		pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
	}
}

void LinearBurn(int* pixels, int* pixelsLayer, int w, int h, int alpha) {
	//RSLinearBurn(pixels, pixelsLayer, w, h, w, h, alpha);
	int i;
	for (i = 0; i < w * h; i++) {
		int colorT = pixelsLayer[i];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;
		int aT = (colorT >> 24) & 0xFF;
		//Premultiplied
		rT = rT * aT / 255;
		gT = gT * aT / 255;
		bT = bT * aT / 255;

		int color = pixels[i];
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		int a = (color >> 24) & 0xFF;

		r = r + (rT - aT) * alpha / 100;
		g = g + (gT - aT) * alpha / 100;
		b = b + (bT - aT) * alpha / 100;

		if (r < 0)
			r = 0;
		if (r > 255)
			r = 255;
		if (g < 0)
			g = 0;
		if (g > 255)
			g = 255;
		if (b < 0)
			b = 0;
		if (b > 255)
			b = 255;

		pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
	}
}

/**
 *
 * @param pixels 原图待处理像素。并非原图全部像素。
 * @param layerPixels layer待处理像素。并非layer全部像素。
 * @param w 原图宽度
 * @param h pixels对应的原图高度。如果是严格逐行处理，h=1.
 * @param pw layer的宽度
 * @param ph layerPixels对应的layer高度。如果是严格逐行处理，ph=1.
 */
void RSLinearBurn(int* pixels, int* pixelsLayer, int w, int h, int pw, int ph,
		int alpha) {
	int i;
	//LOGW("LinearBurn: %d", alpha);
	int layeralpha = alpha;
	for (i = 0; i < w * h; i++) {
		int pIndex = switchIndex(w, h, pw, ph, i);
		int colorT = pixelsLayer[pIndex];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;
		int aT = (colorT >> 24) & 0xFF;
		//Premultiplied
		rT = rT * aT / 255;
		gT = gT * aT / 255;
		bT = bT * aT / 255;

		int color = pixels[i];
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		int a = (color >> 24) & 0xFF;

//		int alpha = aT * layeralpha;
//		int revAlpha = 255000 - alpha;
//
//		int tempr = r + rT - 255;
//		if (tempr<0)tempr=0;
//		r = (tempr*alpha + r*revAlpha) / 255000;
//
//		int tempg =  g + gT - 255;
//		if (tempg<0)tempg=0;
//		g = (tempg*alpha + g*revAlpha) / 255000;
//
//		int tempb =  b + bT - 255;
//		if (tempb<0)tempb=0;
//		b = (tempb*alpha + b*revAlpha) / 255000;
		r = r + (rT - aT) * alpha / 100;
		g = g + (gT - aT) * alpha / 100;
		b = b + (bT - aT) * alpha / 100;

		if (r < 0)
			r = 0;
		if (r > 255)
			r = 255;
		if (g < 0)
			g = 0;
		if (g > 255)
			g = 255;
		if (b < 0)
			b = 0;
		if (b > 255)
			b = 255;

		pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
	}
}

void ScreenWithLimitedLayer(int* pixels, int* pixelsLayer, int w, int h, int lw,
		int lh) {
	int i, j;
	for (j = 0; j < h; j++) {
		for (i = 0; i < w; i++) {
			int index = j * w + i;

			int layerw = (i >= lw) ? lw - 1 : i, layerh =
					(j >= lh) ? lh - 1 : j;

			int colorT = pixelsLayer[layerh * lw + layerw];
			int rT = (colorT >> 16) & 0xFF;
			int gT = (colorT >> 8) & 0xFF;
			int bT = colorT & 0xFF;

			int color = pixels[index];
			int r = (color >> 16) & 0xFF;
			int g = (color >> 8) & 0xFF;
			int b = (color) & 0xFF;
			int a = (color >> 24) & 0xFF;

			r = (int) 255 - (255 - r) * (255 - rT) / 255;
			g = (int) 255 - (255 - g) * (255 - gT) / 255;
			b = (int) 255 - (255 - b) * (255 - bT) / 255;

			pixels[index] = (a << 24) | (r << 16) | (g << 8) | b;
		}
	}
}

void MergeSelection(int* pixels, int* pixelLayer, int* selection, int w, int h) {
	//LOGW("w:%d  h:%d", w, h);
	int i;
	for (i = 0; i < w * h; i++) {

		//	LOGW(" i:%d  sel:%d",  i, selection[i]);
		//LOGW("w: %d h:%d i:%d  sel:%d", w, h, i, selection[i]);

		int colorT = pixelLayer[i];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;
		int aT = (colorT >> 24) & 0xFF;

		//	LOGW("rT: %d gt: %d bt: %d at: %d", rT, gT,bT,aT);
//		LOGW("got layer color");
		//LOGW("got layer color");

		int color = pixels[i];
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		int a = (color >> 24) & 0xFF;

		//	LOGW("%d,%d,%d,%d   %d,%d,%d,%d",rT,gT,bT,aT, r, g,b,a);
		//	selection[i]=selection[i] & 0xFF;
//		LOGW("got original color");

		//LOGW("got original color");
		r = (r * selection[i + 4] + rT * (255 - selection[i + 4])) / 255;
		g = (g * selection[i + 4] + gT * (255 - selection[i + 4])) / 255;
		b = (b * selection[i + 4] + bT * (255 - selection[i + 4])) / 255;
		a = (a * selection[i + 4] + aT * (255 - selection[i + 4])) / 255;
		//LOGW("got new color");
		//	LOGW("sel: %d r: %d g: %d b: %d a: %d",selection[i+1], r, g,b,a);
		pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
		//LOGW("pixels set");
	}
}

//To avoid float computing, the sum of weight and weightLayer should be 255
void MergeWeight(int* pixels, int* pixelLayer, int w, int h, int weight) {
	int i;
	int weightOri = 255 - weight;
	for (i = 0; i < w * h; i++) {

		int colorT = pixelLayer[i];
		int rT = (colorT >> 16) & 0xFF;
		int gT = (colorT >> 8) & 0xFF;
		int bT = colorT & 0xFF;
		int aT = (colorT >> 24) & 0xFF;

		int color = pixels[i];
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		int a = (color >> 24) & 0xFF;

		r = (r * weightOri + rT * weight) >> 8;
		g = (g * weightOri + gT * weight) >> 8;
		b = (b * weightOri + bT * weight) >> 8;
		a = (a * weightOri + aT * weight) >> 8;

		scopeLimit(&r);
		scopeLimit(&g);
		scopeLimit(&b);
		scopeLimit(&a);

		pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
	}
}
