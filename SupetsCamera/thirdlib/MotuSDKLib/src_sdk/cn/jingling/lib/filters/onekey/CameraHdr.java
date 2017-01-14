package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.IntelligentBeautify;
import cn.jingling.lib.filters.OneKeyFilter;

public class CameraHdr extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		bm = IntelligentBeautify.apply(cx, bm, 40, 40, 40);
		ImageProcessUtils.saturationPs(bm, 10);
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		Curve c = new Curve(cx, "curves/chdr.dat");
		CMTProcessor.curveEffect(pixels, c.getCurveRed(), c.getCurveGreen(), c.getCurveBlue(), w, h);
		CMTProcessor.brightEffect(pixels, w, h, 53);
		CMTProcessor.contrastEffect(pixels, w, h, 60);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		Bitmap sharpenBitmap = Bitmap.createBitmap(pixels, w, h, bm.getConfig());
		ImageProcessUtils.mergeBitmapInPlace(bm, sharpenBitmap, 0.15);
		sharpenBitmap.recycle();
		sharpenBitmap = null;
		pixels = null;
		return bm;
	}

}
