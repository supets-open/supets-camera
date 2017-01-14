package cn.jingling.lib.advanceedit.brush;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.ImageView;

/**
 * @Copyright(C)2012,  Baidu.Tech.Co.Ltd. All rights reserved.
 * @Filename: GlowDrawState.java Created On 2012-10-16
 * @Author:pengfei
 * @Description:荧光笔画笔
 * 
 */
public class GlowDrawState extends CompoundDrawState {
	
	private static final int DEFAULT_SHADOW_RADIUS = 15;
	
	private static final float CENTER_PERCENT = 0.5f;
	
	/** 线性画笔的一种。
	 * @param pathBitmap 绘画所用的底图。画笔绘制的涂鸦就是画在这张图上的。
	 */
	public GlowDrawState(Bitmap pathBitmap) {
		super(pathBitmap);
		paint.setColor(this.penColor);
		paint.setShadowLayer(DEFAULT_SHADOW_RADIUS, 0, 0, this.penColor);
		paint.setStrokeWidth(this.penWidth);
		mWholePathPaint.setStrokeWidth((int)(this.penWidth * CENTER_PERCENT));
		mWholePathPaint.setColor(Color.WHITE);
	}

	@Override
	public void setPenWidth(int pWidth) {
		super.setPenWidth(pWidth);
		int width = (int)(CENTER_PERCENT * pWidth);
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
