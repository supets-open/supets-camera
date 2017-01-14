package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.OneKeyFilter;

public class LomoCode extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		CMTProcessor.lomo(pixels, w, h, Color.BLACK, 150, 80, 50);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		pixels = null;
		return bm;
	}

}
