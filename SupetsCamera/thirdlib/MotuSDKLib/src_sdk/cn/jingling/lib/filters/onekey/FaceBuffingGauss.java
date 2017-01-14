package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.OneKeyFilter;
import cn.jingling.lib.filters.SmoothSkinProcessor;

public class FaceBuffingGauss extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		SmoothSkinProcessor.buffingTemplate(pixels, w, h, 10, 1);
		Curve c = new Curve(cx, "curves/mopitest4.dat");
		SmoothSkinProcessor.faceBuffing(pixels, w, h, c.getCurveRed(),
				c.getCurveGreen(), c.getCurveBlue());
		SmoothSkinProcessor.releaseSource();
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		return bm;
	}

}
