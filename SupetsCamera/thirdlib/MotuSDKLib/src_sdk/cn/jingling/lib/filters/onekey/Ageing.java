package cn.jingling.lib.filters.onekey;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.OneKeyFilter;

public class Ageing extends OneKeyFilter {

	private void AddNoise(int[] pixels, int w, int h) {
		Random random = new Random();

		int i, j;
		int n = w * h;
		int ran;
		int[] color = new int[3];
		int[] gauss = new int[60];

		double t;
		for (i = 0; i < 60; i++) {
			t = random.nextGaussian();
			gauss[i] = (int) (t * 10);
		}

		for (i = 0; i < n; i++) {
			ran = gauss[random.nextInt(60)];
			// ran = (int)(random.nextGaussian()/3*24);
			// ran = random.nextInt(40)-20;
			color[0] = (pixels[i] & 0x00FF0000) >> 16;
			color[1] = (pixels[i] & 0x0000FF00) >> 8;
			color[2] = pixels[i] & 0x000000FF;

			pixels[i] &= 0xFF000000;

			for (j = 0; j < 3; j++) {
				color[j] += ran;
				if (color[j] < 0)
					color[j] = 0;
				else if (color[j] > 255)
					color[j] = 255;
			}

			pixels[i] |= (color[0] << 16 | color[1] << 8 | color[2]);

		}
	}

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);

		// Gray&Blue
		Curve curve = new Curve();
		CMTProcessor.blueEffect(pixels, curve.getCurveRed(),
				curve.getCurveGreen(), curve.getCurveBlue(), w, h);

		// Contrast
		CMTProcessor.contrastEffect(pixels, w, h, 57);
		CMTProcessor.brightEffect(pixels, w, h, 57);

		// Add Noise
		AddNoise(pixels, w, h);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);

		// Adjust hue
		ImageProcessUtils.hue(bm, 40);

		return bm;
	}

}
