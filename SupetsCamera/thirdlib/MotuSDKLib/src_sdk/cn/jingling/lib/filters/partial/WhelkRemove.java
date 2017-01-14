package cn.jingling.lib.filters.partial;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.PartialFilter;

public class WhelkRemove extends PartialFilter {
	
	private int FIX_BOUND = 10;
	
	@Override
	public Bitmap apply(Bitmap bm, Point point) {
		
		/*int mWidth = bm.getWidth();
		int mHeight = bm.getHeight();
		
		int offset = 5;
		int left = Math.max(point.x - mRadius - offset, 0);
		int right = Math.min(point.x + mRadius + offset, mWidth);
		int bottom = Math.min(point.y + mRadius + offset, mHeight);
		int top = Math.max(point.y - mRadius - offset, 0);
		
		int width = right - left;
		int height = bottom - top;

		int[] pixels = new int[width * height];
		bm.getPixels(pixels, 0, width, left, top, width, height);

		CMTProcessor.blur(pixels, width, height, point.x, point.y, mRadius);
		
		bm.setPixels(pixels, 0, width, left, top, width, height);
		
		return bm;*/
		
		Bitmap bitmap = bm;
		/*float[] val = new float[9];
		((Matrix)obj[0]).getValues(val);
		double scale = Math.sqrt(val[0] * val[0] + val[1] * val[1]);*/

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
//		mRadius = (int) (mRadius / scale) + FIX_BOUND;
		mRadius = mRadius + FIX_BOUND;
		int x0 = (int) point.x - mRadius;
		int y0 = (int) point.y - mRadius;
		int x1 = (int) point.x + mRadius;
		int y1 = (int) point.y + mRadius;

		if (x0 < 0)
			return bitmap;
		if (y0 < 0)
			return bitmap;
		if (x1 >= w)
			return bitmap;
		if (y1 >= h)
			return bitmap;

		int xr0 = (int) point.x - x0;
		int yr0 = (int) point.y - y0;
		int h0 = y1 - y0;
		int w0 = x1 - x0;

		try {
			int[] pixels = new int[w0 * h0];
			bitmap.getPixels(pixels, 0, w0, x0, y0, w0, h0);
//			showRoundView(point.givePointAfterTransform(mGroundImage.getImageMatrix()), SMOOTH_POINT_RADIUS);
			CMTProcessor.skinSmoothPointEffect(pixels, w0, h0, xr0, yr0, mRadius
					- FIX_BOUND);
			bitmap.setPixels(pixels, 0, w0, x0, y0, w0, h0);
//			mGroundImage.refresh();
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			e.printStackTrace();
//			CrashRestart.restartAfterSaveGroundImage();

		}
		
		return bitmap;
	}

}
