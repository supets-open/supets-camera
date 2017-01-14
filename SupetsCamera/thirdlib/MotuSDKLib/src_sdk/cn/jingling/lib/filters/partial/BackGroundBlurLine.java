package cn.jingling.lib.filters.partial;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.PartialFilter;

public class BackGroundBlurLine extends PartialFilter{
	private Bitmap mOriginBitmap;
	private Bitmap mBlurBitmap;
	
	private int mWidth;
	private int mHeight;
	
	private float mAngle = 0;
	
	/**
	 * @param angle from 0 to 2*PI
	 */
	public void setAngle(float angle) {
		mAngle = angle;
	}
	
	@Override
	public void setup(Context cx, Bitmap bm) {
		super.setup(cx, bm);
		mWidth = bm.getWidth();
		mHeight = bm.getHeight();
		mOriginBitmap = bm;
		
		int[] pixels = new int[mWidth * mHeight];
		mOriginBitmap.getPixels(pixels, 0, mWidth, 0, 0, mWidth, mHeight);
		CMTProcessor.fastAverageBlur(pixels, mWidth, mHeight, 5);
		mBlurBitmap = Bitmap.createBitmap(pixels, mWidth, mHeight,
				Bitmap.Config.ARGB_8888);
	}
	
	
	public Bitmap apply(Bitmap bm, Point point) {

		int[] pixels = new int[mWidth * mHeight];
		mBlurBitmap.getPixels(pixels, 0, mWidth, 0, 0, mWidth, mHeight);

		CMTProcessor.blurBackgroundEffectByLine(
				pixels,
				mWidth,
				mHeight,
				point.x,
				point.y,
				(int) mRadius,
				(int) (mRadius + getGradualSize(mWidth, mHeight,
						mRadius)), mAngle);

		Bitmap mergeBitmap = Bitmap.createBitmap(pixels, mWidth, mHeight,
				Bitmap.Config.ARGB_8888);

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);

		Bitmap perfBitmap = mOriginBitmap.copy(mOriginBitmap.getConfig(), true);

		Canvas canvas = new Canvas(perfBitmap);
		canvas.drawBitmap(mergeBitmap, 0, 0, paint);
		return perfBitmap;
	};
	
	
	private int getGradualSize(int w, int h, int r) {
		int m = Math.max(w, h);
		int gradualSize = (m / 2 - r) / 2;
		return Math.max(gradualSize, 1);
	}
	

}
