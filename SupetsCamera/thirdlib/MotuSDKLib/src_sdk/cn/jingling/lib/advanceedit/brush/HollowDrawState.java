package cn.jingling.lib.advanceedit.brush;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.widget.ImageView;

public class HollowDrawState extends CompoundDrawState {
	
	/** 空心笔中，空心部分占整个画笔宽度的50% */
	private static final float HOLLOW_PERCENT = 0.5f;
	
	private static final int DEFAULT_SHADOW_RADIUS = 15;

	/** 线性画笔的一种。
	 * @param pathBitmap 绘画所用的底图。画笔绘制的涂鸦就是画在这张图上的。
	 */
	public HollowDrawState(Bitmap pathBitmap) {
		super(pathBitmap);
		paint.setColor(this.penColor);
		paint.setShadowLayer(DEFAULT_SHADOW_RADIUS, 0, 0, this.penColor);
		paint.setStrokeWidth(this.penWidth);
		mWholePathPaint.setStrokeWidth((int)(this.penWidth * HOLLOW_PERCENT));
		mWholePathPaint.setXfermode(new PorterDuffXfermode(
                PorterDuff.Mode.CLEAR));
	}

	@Override
	public void setPenWidth(int pWidth) {
		super.setPenWidth(pWidth);
		int width = (int)(HOLLOW_PERCENT * pWidth);
		mWholePathPaint.setStrokeWidth(width);
	}
	
	
	@Override
	public void setPenColor(int color) {
		this.penColor = color;
		paint.setColor(color);
		paint.setShadowLayer(DEFAULT_SHADOW_RADIUS, 0, 0, color);
	}
	
	@Override
	public int getPenWidth()
	{
		return  penWidth + 2;
	}
	
	@Override
	protected int getShaderWidth()
	{
		return DEFAULT_SHADOW_RADIUS;
	}	
}
