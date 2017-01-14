package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.OneKeyFilter;

public class CameraFood extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
//		ImageProcessUtils.saturationPs(bm, 10);
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		
//		CMTProcessor.autoColor(pixels, w, h, 20, 20);
//		CMTProcessor.colorLevel(pixels, w, h, 0, 0.9f, 245, 0, 255);
//		CMTProcessor.brightEffect(pixels, w, h, 57);
//		CMTProcessor.contrastEffect(pixels, w, h, 67);
		Curve c = new Curve(cx, "curves/cfood.dat");
		CMTProcessor.curveEffect(pixels, c.getCurveRed(), c.getCurveGreen(), c.getCurveBlue(), w, h);
		ImageProcessUtils.saturationPs(pixels, w, h, 12);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		pixels = null;
		return bm;
	}

}
