package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.Layer;
import cn.jingling.lib.filters.OneKeyFilter;

public class Lomo extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		ImageProcessUtils.saturationPs(pixels, w, h, -15);
		Curve curve = new Curve(cx, "curves/lomo.dat");
		CMTProcessor.curveEffect(pixels, 
				curve.getCurveRed(), curve.getCurveGreen(),
				curve.getCurveBlue(), w, h);
		CMTProcessor.linearBurn(pixels, Layer.getLayerPixels(cx, "layers/lomo", w, h, Layer.Type.ROTATABLE), w, h, 50);

		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		pixels = null;
		return bm;
	}

}
