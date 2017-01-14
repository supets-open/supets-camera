package cn.jingling.lib.advanceedit.brush;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.widget.ImageView;

public class BlurDrawState extends CompoundDrawState {
	
	private static final int DEFAULT_BLUR_RADIUS = 6;
	BlurMaskFilter mBlurMaskFilter = 
			new BlurMaskFilter(DEFAULT_BLUR_RADIUS, BlurMaskFilter.Blur.NORMAL);

	/** 线性画笔的一种。
	 * @param pathBitmap 绘画所用的底图。画笔绘制的涂鸦就是画在这张图上的。
	 */
	public BlurDrawState(Bitmap pathBitmap) {
		super(pathBitmap);
		paint.setMaskFilter(mBlurMaskFilter);
		mWholePathPaint.setColor(paint.getColor());
	}

	@Override
	public void setPenWidth(int pWidth) {
		super.setPenWidth(pWidth);
		mWholePathPaint.setStrokeWidth(pWidth);
	}

	@Override
	public void setPenColor(int color) {
		super.setPenColor(color);
		mWholePathPaint.setColor(color);
	}

	@Override
	protected void doDraw(Canvas canvas, Path path, Paint paint) {
		if (path == mWholePath) {
			float width = paint.getStrokeWidth();
			paint.setStrokeWidth(paint.getStrokeWidth() + DEFAULT_BLUR_RADIUS * 2);
			paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			canvas.drawPath(path, paint);
			paint.setXfermode(null);
			paint.setStrokeWidth(width);
			paint.setMaskFilter(mBlurMaskFilter);
			canvas.drawPath(path, paint);
			//invalidateWhenMove((int)paint.getStrokeWidth()/2 + DEFAULT_BLUR_RADIUS);
		}
	}
	
	@Override
	protected int getShaderWidth()
	{
		return DEFAULT_BLUR_RADIUS;
	}	

}
