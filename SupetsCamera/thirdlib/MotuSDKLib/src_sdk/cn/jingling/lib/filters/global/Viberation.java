package cn.jingling.lib.filters.global;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.GlobalFilter;
import cn.jingling.lib.filters.SmoothSkinProcessor;

public class Viberation extends GlobalFilter {
	
	private Bitmap mPerformedBitmap;

	@Override
	public void setup(Context cx, Bitmap bm) {
		super.setup(cx, bm);
//		mPerformedBitmap =  bm.copy(bm.getConfig(), true);
		int w = mOriginalBitmap.getWidth();
		int h = mOriginalBitmap.getHeight();
		int[] pixels = new int[w * h];
		mOriginalBitmap.getPixels(pixels, 0, w, 0, 0, w, h);
		SmoothSkinProcessor.ViberationInitial(pixels, w, h);
//		bm.setPixels(pixels, 0, w, 0, 0, w, h);
	}


	@Override
	public Bitmap apply(Context cx, int degree) {
		// TODO Auto-generated method stub
		int w = mOriginalBitmap.getWidth();
		int h = mOriginalBitmap.getHeight();
		int[] pixels = new int[w * h];
		Bitmap bmm = mOriginalBitmap.copy(mOriginalBitmap.getConfig(), true);
		float scale = (float) (degree / 100.0);
		bmm.getPixels(pixels, 0, w, 0, 0, w, h);
		SmoothSkinProcessor.ViberationControl(pixels, w, h, scale);
		bmm.setPixels(pixels, 0, w, 0, 0, w, h);
		return bmm;
	}

	@Override
	public void release() {
		super.release();
		SmoothSkinProcessor.ViberationRelease();
	}

}
