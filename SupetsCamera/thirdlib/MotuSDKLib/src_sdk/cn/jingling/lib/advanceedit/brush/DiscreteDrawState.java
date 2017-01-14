package cn.jingling.lib.advanceedit.brush;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * @Copyright(C)2012,  Baidu.Tech.Co.Ltd. All rights reserved.
 * @Filename: MosaicState.java Created On 2012-10-16
 * @Author:pengfei
 * @Description:与Path的连续性无关的画笔父类，在画笔划过的地方离散地画出特定的元素
 * 
 */
public abstract class DiscreteDrawState extends DrawState {
	
	protected int DEFAULT_DRAW_SPACE = 80;
	
	protected int mDrawSpace = DEFAULT_DRAW_SPACE;
	
	private float mLastX, mLastY;
	
	private Point mPoint = new Point();

	public DiscreteDrawState(Bitmap pathBitmap) {
		super(pathBitmap);
	}

	@Override
	public void mouseDown(Point point) {
		mLastX = point.x;
		mLastY = point.y;
		doDraw(mCanvas, point, paint);
	}

	@Override
	public void mouseMove(Point point) {

		float x = point.x;
		float y = point.y;
		float dx = Math.abs(x - mLastX);
		float dy = Math.abs(y - mLastY);
		if (dx > mDrawSpace || dy > mDrawSpace) {
			mPoint.x = (int)x;
			mPoint.y = (int)y;
			doDraw(mCanvas, mPoint, paint);
			mLastX = x;
			mLastY = y;
		}
	}

	@Override
	public void mouseUp(Point point) {
    	//ScreenControl.getSingleton().mBitmapInkCanvas.drawBitmap(getmBitmap());
    	//getPathBitmap().eraseColor(Color.TRANSPARENT);
	}

	protected abstract void doDraw(Canvas canvas, Point point, Paint paint);
}
