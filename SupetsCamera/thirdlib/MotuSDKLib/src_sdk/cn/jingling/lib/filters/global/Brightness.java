package cn.jingling.lib.filters.global;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.GlobalFilter;

public class Brightness extends GlobalFilter {

	@Override
	public Bitmap apply(Context cx, int degree) {
		// TODO Auto-generated method stub
		int w = mOriginalBitmap.getWidth();
		int h = mOriginalBitmap.getHeight();
		int[] pixels = new int[w * h];
		mOriginalBitmap.getPixels(pixels, 0, w, 0, 0, w, h);
		CMTProcessor.brightEffect(pixels, w, h, degree);
		return Bitmap.createBitmap(pixels, w, h, mOriginalBitmap.getConfig());
	}

}
