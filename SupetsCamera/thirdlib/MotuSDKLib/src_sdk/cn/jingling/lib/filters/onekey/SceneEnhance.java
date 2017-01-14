package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.OneKeyFilter;
import cn.jingling.lib.filters.SmoothSkinProcessor;

public class SceneEnhance extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		Curve c;
		c = new Curve(cx, "curves/color_enhance.dat");
		
		SmoothSkinProcessor.sceneEnhance(pixels, w, h, 100, 100, c.getCurveRed());
		
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		return bm;
	}

}
