package cn.jingling.lib.filters.global;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.GlobalFilter;

public class LomoDrag extends GlobalFilter {
	
	private Bitmap mPerformedBitmap;

	@Override
	public void setup(Context cx, Bitmap bm) {
		super.setup(cx, bm);
		int w = bm.getWidth();
		int h = bm.getHeight();
		mPerformedBitmap = Bitmap.createBitmap(w, h, bm.getConfig());
		setSeekBarNumber(2);
	}


	@Override
	public Bitmap apply(Context cx, int[] degrees) {
		// TODO Auto-generated method stub
		int w = mOriginalBitmap.getWidth();
		int h = mOriginalBitmap.getHeight();
		int[] pixels = new int[w * h];
		mOriginalBitmap.getPixels(pixels, 0, w, 0, 0, w, h);
		CMTProcessor.lomo(pixels, w, h, Color.BLACK, 150, degrees[1], degrees[0]);
		mPerformedBitmap.setPixels(pixels, 0, w, 0, 0, w, h);
		pixels = null;

		return mPerformedBitmap;
	}

	@Override
	public void release() {
		super.release();
		if (mPerformedBitmap != null) {
			mPerformedBitmap.recycle();
			mPerformedBitmap = null;
		}
	}

}
