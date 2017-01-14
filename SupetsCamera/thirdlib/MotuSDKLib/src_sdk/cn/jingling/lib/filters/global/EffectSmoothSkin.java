package cn.jingling.lib.filters.global;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.GlobalFilter;
import cn.jingling.lib.filters.ImageProcessUtils;

public class EffectSmoothSkin extends GlobalFilter{
	
	public Bitmap apply(Context cx, int degree) {
		Bitmap bm = mOriginalBitmap.copy(mOriginalBitmap.getConfig(), true);
		
		ImageProcessUtils.effectSmoothSkin(cx, bm, degree);
		
		return bm;
	}
}
