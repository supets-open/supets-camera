package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.OneKeyFilter;
import cn.jingling.lib.filters.SmoothSkinProcessor;

public class SmoothBrightSkin extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		SmoothSkinProcessor.buffingTemplate(pixels, w, h, 10, 0);
		Curve curve = new Curve(cx, "curves/skin_smooth.dat");
		SmoothSkinProcessor.faceBuffingWeight(pixels, w, h,
				curve.getCurveRed(), curve.getCurveGreen(),
				curve.getCurveBlue(), 50);
		SmoothSkinProcessor.releaseSource();

//		CMTProcessor.brightEffect(pixels, w, h, 2 * 30 / 100 + 40); // 40 ~ 70
//		ImageProcessUtils.saturationPs(pixels, w, h, -10);
		
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		
		return bm;

	}
}
