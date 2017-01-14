package cn.jingling.lib.filters.global;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.GlobalFilter;
import cn.jingling.lib.filters.ImageProcessUtils;

public class Saturation extends GlobalFilter {

	@Override
	public Bitmap apply(Context cx, int degree) {
		// TODO Auto-generated method stub
		Bitmap bm = mOriginalBitmap.copy(mOriginalBitmap.getConfig(), true);
		ImageProcessUtils.saturation(bm, degree);
		return bm;
	}

}
