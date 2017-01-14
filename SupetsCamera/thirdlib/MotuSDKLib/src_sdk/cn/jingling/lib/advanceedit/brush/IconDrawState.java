package cn.jingling.lib.advanceedit.brush;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * @Copyright(C)2012,  Baidu.Tech.Co.Ltd. All rights reserved.
 * @Filename: IconDrawState.java Created On 2012-10-16
 * @Author:pengfei
 * @Description:图案画笔
 * 
 */
public class IconDrawState extends DiscreteDrawState {
	
	private static final int DEFAULT_DRAW_WIDTH = 80;
		
	private int mIconWidth = DEFAULT_DRAW_WIDTH;
	
	private int mIconHeight = DEFAULT_DRAW_WIDTH;
	
	private Bitmap [] mIconList = null;
	
	private int mIndex = -1;
	
	private RectF mRect = new RectF();

	/** Icon画笔。
	 * @param iconList 所需绘制的icon。请确保list中所有icon的大小一致。
	 * @param pathBitmap 绘画所用的底图。画笔绘制的涂鸦就是画在这张图上的。
	 */
	public IconDrawState(Bitmap[] iconList, Bitmap pathBitmap) {
		super(pathBitmap);
		mIconList = iconList;

		if (iconList != null && iconList.length > 0) {
			mIconWidth = mIconList[0].getWidth();
			mIconHeight = mIconList[0].getHeight();
		}

		this.setPenWidth(this.penWidth);
	}
	
	/** Icon画笔。
	 * @param iconList 所需绘制的icon。请确保list中所有icon的大小一致。
	 * @param pathBitmap 绘画所用的底图。画笔绘制的涂鸦就是画在这张图上的。
	 */
	public IconDrawState(Drawable[] iconList, Bitmap pathBitmap) {
		super(pathBitmap);
		if (iconList != null && iconList.length > 0) {
			int length = iconList.length;
			mIconList = new Bitmap[length];
			for (int i = 0; i < length; i++) {
				mIconList[i] = ((BitmapDrawable)iconList[i]).getBitmap();
			}
			
			mIconWidth = mIconList[0].getWidth();
			mIconHeight = mIconList[0].getHeight();
		}
		
		this.setPenWidth(this.penWidth);
	}

	@Override
	protected void doDraw(Canvas canvas, Point point, Paint paint) {
		if (canvas == null) {
			return;
		}
		mRect.set(point.x - mIconWidth/2, point.y - mIconHeight/2, point.x + mIconWidth/2, point.y + mIconHeight/2);
		canvas.drawBitmap(getBitmapToDraw(), null, mRect, paint);
	}

	@Override
	public void setPenWidth(int pWidth) {
		super.setPenWidth(pWidth);
		int iconSize = Math.max(mIconWidth, mIconHeight);
		float ratio = (float)pWidth/(float)iconSize;
		mIconWidth = (int) (mIconWidth*ratio);
		mIconHeight = (int) (mIconHeight*ratio);
		mDrawSpace = pWidth;
		
	}
	
	private Bitmap getBitmapToDraw() {
		mIndex ++;
		if (mIndex >= mIconList.length) {
			mIndex = 0;
		}
		return mIconList[mIndex];
	}
	
	
	

}
