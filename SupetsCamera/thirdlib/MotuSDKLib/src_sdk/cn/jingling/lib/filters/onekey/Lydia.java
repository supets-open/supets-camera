package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.Layer;
import cn.jingling.lib.filters.OneKeyFilter;

public class Lydia extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		// TODO Auto-generated method stub
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		CMTProcessor.multiplyEffect(pixels, Layer.getLayerPixels(cx,
				"layers/fu_gu", w, h, Layer.Type.ROTATABLE, 60 * 255 / 100), w, h);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		return null;
	}

}
