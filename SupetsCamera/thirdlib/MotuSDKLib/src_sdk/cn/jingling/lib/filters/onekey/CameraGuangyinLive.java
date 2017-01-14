package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.Layer;
import cn.jingling.lib.filters.OneKeyFilter;

public class CameraGuangyinLive extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		// pixels = ImageProcessUtils.saturationPs(pixels, w, h, -100);
		
		Curve c1 = new Curve(cx, "curves/live_guangyin1.dat");
		CMTProcessor.curveEffect(pixels, c1.getCurveRed(), c1.getCurveGreen(),
				c1.getCurveBlue(), w, h);

		CMTProcessor.overlayAlphaEffect(pixels, Layer.getLayerPixels(cx,
				"layers/live_guangyin", w, h, Layer.Type.NORMAL), w, h, 30);
		ImageProcessUtils.saturationPs(pixels, w, h, -55);
		Curve c2 = new Curve(cx, "curves/live_guangyin2.dat");
		CMTProcessor.curveEffect(pixels, c2.getCurveRed(), c2.getCurveGreen(),
				c2.getCurveBlue(), w, h);

		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		pixels = null;
		return bm;
	}

}
