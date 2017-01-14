package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.Layer;
import cn.jingling.lib.filters.OneKeyFilter;


public class Sunny extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		int[] layerPixels = new int[w * h];

		Bitmap layer;
		if(w <200)
		{
			layer = Layer.getLayerImage(cx, "layers/sunnysmall", w, h, Layer.Type.ROTATABLE);
		}
		else
		{
			layer = Layer.getLayerImage(cx, "layers/sunny", w, h, Layer.Type.ROTATABLE);
		}
		layer.getPixels(layerPixels, 0, w, 0, 0, w, h);
		CMTProcessor.screenEffect(pixels, layerPixels, w, h);
		
		Curve curve = new Curve(cx, "curves/sunny.dat");
		CMTProcessor.curveEffect(pixels, 
				curve.getCurveRed(), curve.getCurveGreen(),
				curve.getCurveBlue(), w, h);

		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		pixels = null;
		layerPixels = null;
		return bm;
	}
}
