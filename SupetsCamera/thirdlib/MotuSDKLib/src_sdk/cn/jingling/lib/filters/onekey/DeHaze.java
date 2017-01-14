package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.OneKeyFilter;
import cn.jingling.lib.filters.SmoothSkinProcessor;

public class DeHaze extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);

		float Rat = (float) 1.0;
		float RatE = (float) 1.0;
		float RatL = (float) 1.0;
		int level = -1;
		
		SmoothSkinProcessor.deHaze(pixels, w, h, level, Rat, RatE, RatL);
		
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		return bm;
	}

}
