package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.Layer;
import cn.jingling.lib.filters.OneKeyFilter;

public class CameraRiseLive extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		// TODO Auto-generated method stub
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		// pixels = ImageProcessUtils.saturationPs(pixels, w, h, -100);

		CMTProcessor.overlayAlphaEffect(pixels, Layer.getLayerPixels(cx,
				"layers/live_rise", w, h, Layer.Type.NORMAL), w, h, 60);

		Curve c = new Curve(cx, "curves/live_rise.dat");
		CMTProcessor.curveEffect(pixels, c.getCurveRed(), c.getCurveGreen(),
				c.getCurveBlue(), w, h);

		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		pixels = null;
		return bm;
	}

}
