package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.OneKeyFilter;

public class Pop extends OneKeyFilter {
	
	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		
		int[] pixels = new int[w * h];

		bm.getPixels(pixels, 0, w, 0, 0, w, h);

		CMTProcessor.popstyle(pixels, w, h,0);

		bm.setPixels(pixels, 0, w, 0, 0, w, h);

		pixels = null;
		return bm;
	}


}
