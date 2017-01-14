package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.OneKeyFilter;

public class EffectSmoothSkinAuto extends OneKeyFilter{

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		ImageProcessUtils.effectSmoothSkin(cx, bm, 100);
		return bm;
	}
}
