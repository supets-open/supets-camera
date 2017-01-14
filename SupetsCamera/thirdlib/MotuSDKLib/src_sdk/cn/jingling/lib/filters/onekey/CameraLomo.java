package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.Layer;
import cn.jingling.lib.filters.OneKeyFilter;

public class CameraLomo extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		Curve c = new Curve(cx, "curves/camera_lomo.dat");
		CMTProcessor.curveEffect(pixels, c.getCurveRed(), c.getCurveGreen(), c.getCurveBlue(), w, h);
		int[] layer = Layer.getLayerPixels(cx, "layers/camera_lomo_mul", w, h, Layer.Type.NORMAL, 50);
		CMTProcessor.multiplyEffect(pixels, layer, w, h);
		layer = Layer.getLayerPixels(cx, "layers/camera_lomo_lb", w, h, Layer.Type.NORMAL, 100);
		CMTProcessor.linearBurn(pixels, layer, w, h, 100);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		pixels = null;
		return bm;
	}

}
