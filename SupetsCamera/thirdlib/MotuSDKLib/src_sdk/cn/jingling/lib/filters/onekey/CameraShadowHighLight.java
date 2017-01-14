package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.OneKeyFilter;
import cn.jingling.lib.filters.SmoothSkinProcessor;

public class CameraShadowHighLight extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		SmoothSkinProcessor.ShadowHighLight(pixels, w, h);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		return bm;
	}
}
