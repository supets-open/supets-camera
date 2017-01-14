package cn.jingling.lib.advanceedit.brush;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.widget.ImageView;

/**
 * @Copyright(C)2012,  Baidu.Tech.Co.Ltd. All rights reserved.
 * @Filename: CompoundDrawState.java Created On 2012-10-16
 * @Author:pengfei
 * @Description:画笔需要画两条线组合时候的画笔基本类
 * 
 */
public class CompoundDrawState extends DrawState {
	
	protected Path mWholePath = null;
    
    protected MyPaint mWholePathPaint = null;

	public CompoundDrawState(Bitmap pathBitmap) {
		super(pathBitmap);
		mWholePathPaint = new MyPaint(paint);
	}

	@Override
	public void mouseDown(Point point) {
		mWholePath = new Path();
		mWholePath.reset();
		mWholePath.moveTo(point.x, point.y);
        int x = (int)point.x;
        int y = (int)point.y;
		last_X = x;
		last_Y = y;
        refresh_X = x;
        refresh_Y = y;
        refresh_LastX = refresh_X;
        refresh_LastY = refresh_Y;
	}

	@Override
	public void mouseMove(Point point) {
        int x = (int)point.x;
        int y = (int)point.y;
        
        int dx =  Math.abs(x - last_X);
        int dy =  Math.abs(y - last_Y);
        Path path = new Path();
        path.moveTo(refresh_X, refresh_Y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
         	refresh_Y = (y + last_Y)/2;
         	refresh_X = (x + last_X)/2;

        	path.quadTo(last_X, last_Y, refresh_X, refresh_Y);

            doDraw(mCanvas, path, paint);
            mWholePath.addPath(path);
            doDraw(mCanvas, mWholePath, mWholePathPaint);
            //invalidateWhenMove(getPenWidth()/2 + getShaderWidth());
        	last_X = x;
        	last_Y = y;
	        refresh_LastX = refresh_X;
	        refresh_LastY = refresh_Y;
        }
	}

	@Override
	public void mouseUp(Point point) {
		Path path = new Path();
		path.moveTo(refresh_X, refresh_Y);
		path.lineTo(point.x, point.y);
		doDraw(mCanvas, path, paint);
		
		mWholePath.lineTo(point.x, point.y);
		doDraw(mCanvas, mWholePath, mWholePathPaint);
		mWholePath.reset();
	}
	
	
	protected void doDraw(Canvas canvas, Path path, Paint paint) {
		if (canvas == null || path == null || paint == null) {
			return;
		}
		canvas.drawPath(path, paint);
	}
	
	protected int getShaderWidth()
	{
		return 0;
	}
	

}
