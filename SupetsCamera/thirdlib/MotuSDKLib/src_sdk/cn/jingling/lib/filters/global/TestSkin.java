package cn.jingling.lib.filters.global;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.GlobalFilter;
import cn.jingling.lib.filters.ImageProcessUtils;

public class TestSkin extends GlobalFilter {

	private Bitmap mPerformedBitmap;
	
	@Override
	public void setup(Context cx, Bitmap bm) {
		super.setup(cx, bm);
		mPerformedBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), bm.getConfig());
		SmoothSkin.setup(bm, mPerformedBitmap, true);
	}
	
	@Override
	public Bitmap apply(Context cx, int degree) {
		// TODO Auto-generated method stub
		Bitmap bm = ImageProcessUtils.mergeBitmap(mPerformedBitmap,
				mOriginalBitmap, degree / 100.0);
		return bm;
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
