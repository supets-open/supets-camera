package cn.jingling.lib.filters.global;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.GlobalFilter;
import cn.jingling.lib.filters.ImageProcessUtils;

public class Sharpen extends GlobalFilter {

	private Bitmap mPerformedBitmap;

	@Override
	public void setup(Context cx, Bitmap bm) {
		super.setup(cx, bm);
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		CMTProcessor.sharpenEffect(pixels, w, h, 6);
		mPerformedBitmap = Bitmap.createBitmap(pixels, w, h, bm.getConfig());	
	}

	@Override
	public Bitmap apply(Context cx, int degree) {
		// TODO Auto-generated method stub
		Bitmap bm = ImageProcessUtils.mergeBitmap(mPerformedBitmap, mOriginalBitmap, degree / 100.0);
		return bm;
	}

	@Override
	public void release() {
		super.release();
		mPerformedBitmap.recycle();
		mPerformedBitmap = null;
	} 
}
