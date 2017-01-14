package cn.jingling.lib.advanceedit.brush;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.widget.ImageView;

public class AlphaDrawState extends CompoundDrawState {
	
	private static final int DEFAULT_ALPHA = 153;

	/** 线性画笔的一种。
	 * @param pathBitmap 绘画所用的底图。画笔绘制的涂鸦就是画在这张图上的。
	 */
	public AlphaDrawState(Bitmap pathBitmap) {
		super(pathBitmap);
		paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		mWholePathPaint.setAlpha(DEFAULT_ALPHA);
	}

	@Override
	protected void doDraw(Canvas canvas, Path path, Paint paint) {
		if (path == mWholePath) { // only draw when it is whole path
			canvas.drawPath(path, this.paint);
			canvas.drawPath(path, mWholePathPaint);
			//invalidateWhenMove(getPenWidth()/2);
		}
	}

	@Override
	public void setPenWidth(int pWidth) {
		paint.setStrokeWidth(pWidth);
		mWholePathPaint.setStrokeWidth(pWidth);
	}

	@Override
	public void setPenColor(int color) {
		super.setPenColor(color);
		mWholePathPaint.setColor(color);
		mWholePathPaint.setAlpha(DEFAULT_ALPHA);
	}
	

}
