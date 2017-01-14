package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.OneKeyFilter;

public class CameraScenery extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		CMTProcessor.autoColor(pixels, w, h, 10, 10);
		CMTProcessor.sharpenEffect(pixels, w, h, 2);
		Bitmap sharpenBitmap = Bitmap.createBitmap(pixels, w, h, bm.getConfig());
		ImageProcessUtils.mergeBitmapInPlace(bm, sharpenBitmap, 0.70);
		sharpenBitmap.recycle();
		sharpenBitmap = null;
		Curve c = new Curve(cx, "curves/cscenery.dat");
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		CMTProcessor.curveEffect(pixels, c.getCurveRed(), c.getCurveGreen(), c.getCurveBlue(), w, h);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		pixels = null;
		return bm;
	}

}
