package cn.jingling.lib.advanceedit.brush;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.widget.ImageView;

/**
 * @Copyright(C)2012,  Baidu.Tech.Co.Ltd. All rights reserved.
 * @Filename: EraserState.java Created On 2012-10-16
 * @Author:yangding
 * @Description:橡皮擦画笔。可以认为是绘制透明像素的线性画笔。
 * 
 */
public class EraserState extends DrawState{
	
	/** 橡皮擦画笔。可以认为是绘制透明像素的线性画笔。
	 * @param pathBitmap 绘画所用的底图。画笔绘制的涂鸦就是画在这张图上的。
	 */
	public EraserState(Bitmap pathBitmap)
	{
		super(pathBitmap);
		paint.setXfermode(new PorterDuffXfermode(
                PorterDuff.Mode.CLEAR));
	}
	@Override
	public int getPenWidth()
	{
	    return penWidth;

	}
	
	@Override
	public void setPenWidth(int pWidth) {
		penWidth = pWidth;
		paint.setStrokeWidth(getPenWidth());
	}

	@Override
	public void mouseUp(Point point) {
		int x = point.x;
		int y = point.y;

		mPath.lineTo(x, y);
		mCanvas.drawPath(mPath, paint);

	}
}
