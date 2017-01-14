package cn.jingling.lib.filters.partial;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.PartialFilter;

public class RedEyeRemove extends PartialFilter{
	
	@Override
	public Bitmap apply(Bitmap bm, Point point) {

		int w = bm.getWidth();
		int h = bm.getHeight();
		int x0 = (int) point.x - mRadius;
		int y0 = (int) point.y - mRadius;
		int x1 = (int) point.x + mRadius;
		int y1 = (int) point.y + mRadius;

		if (x0 < 0)
			return bm;
		if (y0 < 0)
			return bm;
		if (x1 >= w)
			return bm;
		if (y1 >= h)
			return bm;

		int xr0 = (int) point.x - x0;
		int yr0 = (int) point.y - y0;
		int h0 = y1 - y0;
		int w0 = x1 - x0;

		int[] pixels = new int[w0 * h0];
		bm.getPixels(pixels, 0, w0, x0, y0, w0, h0);
		CMTProcessor.redeyeEffect(pixels, w0, h0, xr0, yr0, mRadius);
		bm.setPixels(pixels, 0, w0, x0, y0, w0, h0);
	
		return bm;

	}

}
