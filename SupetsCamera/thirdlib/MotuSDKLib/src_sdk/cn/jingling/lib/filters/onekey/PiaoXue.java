package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.Layer;
import cn.jingling.lib.filters.OneKeyFilter;

public class PiaoXue extends OneKeyFilter{

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		pixels = ImageProcessUtils.saturationPs(pixels, w, h, -30);
		
//		CMTProcessor.saturation(pixels, w, h, (float)-0.3);
		Curve c = new Curve(cx, "curves/piao_xue.dat");
		CMTProcessor.curveEffect(pixels, c.getCurveRed(), c.getCurveGreen(), c.getCurveBlue(), w, h);
		CMTProcessor.screenEffect(pixels, Layer.getLayerPixels(cx, "layers/piao_xue", w, h, Layer.Type.CROP), w, h);
		
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		pixels = null;
		return bm;
	}

}
