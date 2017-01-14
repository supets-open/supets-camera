package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.OneKeyFilter;
import cn.jingling.lib.filters.SmoothSkinProcessor;

public class BackLight extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		//这里要弄成传进去两个curve，暂时不算
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
//		Curve c = new Curve(cx, "curves/test4.amp");
		Curve darkerCurve = new Curve(cx, "curves/Untitled1.amp");
		
		Curve lighterCurve = new Curve(cx, "curves/Untitled2.amp");
		
//		SmoothSkinProcessor.backLight(pixels, w, h, 0, 0, 0, 0, 0, 0, darkerCurve.getCurveRed(), lighterCurve.getCurveRed());
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		return bm;
	}

}
