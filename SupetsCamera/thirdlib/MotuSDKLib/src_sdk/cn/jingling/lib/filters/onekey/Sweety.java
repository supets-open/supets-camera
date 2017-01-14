package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.OneKeyFilter;

public class Sweety extends OneKeyFilter {

	private void RGB2Lab(int[] pixels, int w, int h) {
		int color;
		int alpha;
		int[] RGB = new int[3];
		int[] Lab = new int[3];
		for (int i = 0; i < h; i++)
			for (int j = 0; j < w; j++) {
				color = pixels[i * w + j];
				alpha = (color>>24) & 0xFF;
				RGB[0] = (color >> 16) & 0xFF;
				RGB[1] = (color >> 8) & 0xFF;
				RGB[2] = color & 0xFF;

				Lab[0] = (13933 * RGB[0] + 46871 * RGB[1] + 4732 * RGB[2]) >> 16;
				Lab[1] = ((377 * (14503 * RGB[0] - 22218 * RGB[1] + 7714 * RGB[2])) >> 24) + 128;
				Lab[2] = ((160 * (12773 * RGB[0] + 39695 * RGB[1] - 52468 * RGB[2])) >> 24) + 128;

				// ////////////////////////////////////////
				// Lab[2] = Lab[1];
				// ////////////////////////////////////////
				pixels[i * w + j] = (alpha<<24 | (Lab[0]) << 16
						| (Lab[1]) << 8 | (Lab[2]));
			}
		RGB = null;
		Lab = null;
	}

	private void Lab2RGB(int[] pixels, int w, int h) {
		int color;
		int alpha;
		int L1;
		int a1;
		int b1;
		int[] RGB = new int[3];
		int[] Lab = new int[3];
		for (int i = 0; i < h; i++)
			for (int j = 0; j < w; j++) {
				color = pixels[i * w + j];
				alpha = (color>>24) & 0xFF;
				Lab[0] = (color >> 16) & 0xFF;
				Lab[1] = (color >> 8) & 0xFF;
				Lab[2] = color & 0xFF;

				L1 = Lab[0];
				a1 = (Lab[1] - 128) * 174;
				b1 = (Lab[2] - 128) * 410;

				RGB[0] = L1 + ((a1 * 100922 + b1 * 17790) >> 23);
				RGB[1] = L1 - ((a1 * 30176 + b1 * 1481) >> 23);
				RGB[2] = L1 + ((a1 * 1740 - b1 * 37719) >> 23);

				if (RGB[0] < 0)
					RGB[0] = 0;
				else if (RGB[0] > 255)
					RGB[0] = 255;
				if (RGB[1] < 0)
					RGB[1] = 0;
				else if (RGB[1] > 255)
					RGB[1] = 255;
				if (RGB[2] < 0)
					RGB[2] = 0;
				else if (RGB[2] > 255)
					RGB[2] = 255;

				pixels[i * w + j] = ((alpha<<24) | (RGB[0]) << 16
						| (RGB[1]) << 8 | (RGB[2]));
			}
		RGB = null;
		Lab = null;
	}

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		RGB2Lab(pixels, w,h);
		Curve curve = new Curve(cx, "curves/sweety.dat");
		CMTProcessor.curveEffect(pixels, curve.getCurveRed(), curve.getCurveGreen(), curve.getCurveBlue(), w, h);
		Lab2RGB(pixels, w, h);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		pixels = null;
		return bm;
	}
}
