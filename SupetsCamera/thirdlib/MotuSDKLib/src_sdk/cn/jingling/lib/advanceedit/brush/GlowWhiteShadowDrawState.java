package cn.jingling.lib.advanceedit.brush;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.ImageView;

public class GlowWhiteShadowDrawState extends CompoundDrawState {
	private static final int DEFAULT_SHADOW_RADIUS = 10;
	
	private static final float CENTER_PERCENT = 0.6f;

	public GlowWhiteShadowDrawState(Bitmap pathBitmap) {
		super(pathBitmap);
		
		paint.setColor(Color.WHITE);
		paint.setShadowLayer(DEFAULT_SHADOW_RADIUS, 0, 0, Color.WHITE);
		paint.setStrokeWidth(this.penWidth);
		mWholePathPaint.setStrokeWidth((int)(this.penWidth * CENTER_PERCENT));
		mWholePathPaint.setColor(this.penColor);
	}

	@Override
	public void setPenWidth(int pWidth) {
		super.setPenWidth(pWidth);
		int width = (int)(CENTER_PERCENT * pWidth);
		mWholePathPaint.setStrokeWidth(width);
	}

	@Override
	public void setPenColor(int color) {
		mWholePathPaint.setColor(color);
	}
	
	@Override
	protected int getShaderWidth()
	{
		return DEFAULT_SHADOW_RADIUS;
	}	
	

}
