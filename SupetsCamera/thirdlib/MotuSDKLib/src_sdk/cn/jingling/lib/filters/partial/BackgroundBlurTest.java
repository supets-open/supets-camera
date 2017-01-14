package cn.jingling.lib.filters.partial;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.view.MotionEvent;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.ImageSelection;
import cn.jingling.lib.filters.ImageSelection.Align;
import cn.jingling.lib.filters.PartialFilter;

public class BackgroundBlurTest extends PartialFilter {

	private final static String TAG = "BackgroundBlur";

	private final static int MODE_NORMAL = 0;
	private final static int MODE_MOVE = 1;
	private final static int MODE_ZOOM = 2;
	private Bitmap mPerformedBitmap;
	private ImageSelection mSelection;
	private int mRadius;
	private int mCenterX, mCenterY;
	private int mMode = MODE_NORMAL;
	private int mX0, mY0, mX1, mY1;
	private int mMinRadius, mMaxRadius;
	private Bitmap mOriginalBitmap;

	@Override
	public void setup(Context cx, Bitmap bm) {
		super.setup(cx, bm);
		mOriginalBitmap = bm.copy(bm.getConfig(), true);
		int w = bm.getWidth();
		int h = bm.getHeight();
		mMinRadius = Math.min(w, h) / 4;
		mMaxRadius = Math.max(w, h) / 2;
		mPerformedBitmap = Bitmap.createBitmap(w, h, bm.getConfig());
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		CMTProcessor.fastAverageBlur(pixels, w, h, 8);
		mPerformedBitmap.setPixels(pixels, 0, w, 0, 0, w, h);
		mSelection = new ImageSelection(bm.getWidth(), bm.getHeight());
		mRadius = Math.min(bm.getWidth(), bm.getHeight()) / 2;
		mCenterX = bm.getWidth() / 2;
		mCenterY = bm.getHeight() / 2;
		mSelection.selectRound(mRadius, Align.CENTER);
	}

	
//	/**
//	 * Adjust the radius of the blur circle
//	 * @param degree the relative value of the radius (from 0 to 100)
//	 */
//	public void setRelativeRadius(Bitmap bm, int degree) {
//		mRadius = degree * (mMaxRadius - mMinRadius) / 100 + mMinRadius;
//		if (mRadius < mMinRadius) {
//			mRadius = mMinRadius;
//		} else if (mRadius > mMaxRadius) {
//			mRadius = mMaxRadius;
//		}
//		reDraw(bm);
//	}
//	
//	public int getRelativeRadius() {
//		return (mRadius - mMinRadius) * 100 / (mMaxRadius - mMinRadius);
//	}
	
	/**
	 * Set the blur center
	 * @param p center point
	 */
	private void setCenter(Point p) {
		mCenterX = p.x;
		mCenterY = p.y;
	}
	
	public Point getCenter() {
		return new Point(mCenterX, mCenterY);
	}
	
	@Override
	public Bitmap apply(Bitmap bm, Point point) {
		mCenterX = point.x;
		mCenterY = point.y;

		int w = bm.getWidth();
		int h = bm.getHeight();
		mSelection.selectRound(mCenterX, mCenterY, mRadius, mRadius / 2);
		int[] pixels = new int[w * h];
		int[] layerPixels = new int[w * h];
		mOriginalBitmap.getPixels(pixels, 0, w, 0, 0, w, h);
		mPerformedBitmap.getPixels(layerPixels, 0, w, 0, 0, w, h);
		CMTProcessor.mergeSelection(pixels, layerPixels,
				mSelection.getPixels(), w, h);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		return bm;

	}
	

	private void setCoord(MotionEvent event) {
		mX0 = (int) event.getX(0);
		mY0 = (int) event.getY(0);
		mX1 = (int) event.getX(1);
		mY1 = (int) event.getY(1);
	}

	private int dist(float x0, float y0, float x1, float y1) {
		double d = Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1));
		return (int) d;
	}

	private void drawCircle(Bitmap bm) {
		Paint p = new Paint();
		p.setStyle(Style.STROKE);
		Canvas c = new Canvas(bm);
		c.drawCircle(mCenterX, mCenterY, mRadius, p);
	}

	@Override
	public void release() {
		super.release();
		mPerformedBitmap.recycle();
	}


}
