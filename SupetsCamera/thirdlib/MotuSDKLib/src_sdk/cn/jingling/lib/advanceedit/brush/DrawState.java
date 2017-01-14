package cn.jingling.lib.advanceedit.brush;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Path;
import android.graphics.Point;

/**
 * @Copyright(C)2012,  Baidu.Tech.Co.Ltd. All rights reserved.
 * @Filename: DrawState.java Created On 2012-10-16
 * @Author:yangding
 * @Description:涂鸦绘制绘图基本类
 * 
 */
public  class DrawState {
	public static final int DEFAULT_COLOR = Color.rgb(255, 106, 106);
	public static final int DEFAULT_PEN_WIDTH = 30;
	
	protected int last_X;
	protected int last_Y;
	protected int refresh_X;
	protected int refresh_Y;
	protected int refresh_LastX;
	protected int refresh_LastY;
	protected MyPaint paint =  new MyPaint();
	protected Path mPath =  new  Path();
	// 辅助canvas，用于在mBitmap上绘制brush
	protected Canvas mCanvas;
	protected Bitmap mBitmap;
	
	//protected ImageView mImageView;
	protected int penWidth = DEFAULT_PEN_WIDTH;
	protected int penColor = DEFAULT_COLOR;
	
    protected static final int TOUCH_TOLERANCE = 4;

	/** 画笔基本类。
	 * @param pathBitmap 绘画所用的底图。画笔绘制的涂鸦就是画在这张图上的。
	 */
	public DrawState(Bitmap pathBitmap)
	{
		setPathBitmap(pathBitmap);
		
		paint.setStrokeWidth(getPenWidth());
		paint.setColor(getPenColor());
		paint.setStrokeJoin(Join.ROUND);
		paint.setStrokeMiter(90);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		
		this.statisticEvent();
	}
	
	/**
	 * 鼠标按下逻辑的处理
	 * @param point 按下位置,使用的是bitmap坐标，而不是View坐标
	 */
	public void mouseDown(Point point)
	{
        int x = point.x;
        int y = point.y;

		last_X = x;
		last_Y = y;
		mPath.reset();
		mPath.moveTo(last_X,last_Y);
        refresh_X = x;
        refresh_Y = y;
        refresh_LastX = refresh_X;
        refresh_LastY = refresh_Y;
        mCanvas.drawPoint(x, y, paint);
       // mImageView.invalidate(x-getPenWidth()/2-1,y-getPenWidth()/2-1,x+getPenWidth()/2+1,y+getPenWidth()/2+1);
	}
	
	/**
	 * 鼠标移动逻辑的处理
	 * @param point 鼠标位置，使用的是bitmap坐标，而不是View坐标
	 */
	public void mouseMove(Point point) {
		int x = (int)point.x;
		int y = (int)point.y;

		float dx = Math.abs(x - last_X);
		float dy = Math.abs(y - last_Y);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {

			refresh_X = (x + last_X) / 2;
			refresh_Y = (y + last_Y) / 2;
			mPath.quadTo(last_X, last_Y, refresh_X, refresh_Y);
			mCanvas.drawPath(mPath, paint);

			//invalidateWhenMove(getPenWidth() / 2);

			last_X = x;
			last_Y = y;
			refresh_LastX = refresh_X;
			refresh_LastY = refresh_Y;

		}
	}
	
	/**
	 * 鼠标抬起逻辑的处理
	 * @param point 鼠标位置，使用的是bitmap坐标，而不是View坐标
	 */
	public void mouseUp(Point point) {
		int x = (int) point.x;
		int y = (int) point.y;

		mPath.lineTo(x, y);
		mCanvas.drawPath(mPath, paint);
	}

	/**
	 * 设置笔的宽度
	 * @param pWidth 必须大于等于0。
	 */
	public void setPenWidth(int pWidth) {
		this.penWidth = pWidth;
		paint.setStrokeWidth(getPenWidth());
	}
	
	/**
	 * 获取笔的宽度
	 * @param
	 */
	public int getPenWidth()
	{
		return penWidth;
	}

	/**
	 * 设置笔的颜色
	 * @param color 颜色值
	 */
	public void setPenColor(int color) {
		this.penColor = color;
		paint.setColor(color);
	}

	/**
	 * 获取笔的颜色
	 * @param
	 */
	public int getPenColor() {
		return penColor;
	}

	/**
	 * 设置绘制的图片
	 * @param mBitmap
	 */
	public void setPathBitmap(Bitmap mBitmap) {
		this.mBitmap = mBitmap;
		if (mCanvas == null) {
			mCanvas = new Canvas(mBitmap);
		} else {
			mCanvas.setBitmap(mBitmap);
		}
	}

	/**
	 * 获取绘制的图片
	 * @param
	 */
	public Bitmap getPathBitmap() {
		return mBitmap;
	}
	
	/**
	 * 重置绘制路径
	 */
	public void resetPath()
	{
		mPath.reset();	
	}
	
	protected void statisticEvent() {
		String label = this.getClass().getSimpleName();
		label = "Brush:"+label;
	}

//	public rect getInvalidateRect(int strokeWidth)
//	{
//        int left = MathUtils.findMin(refresh_LastX, last_X, refresh_X)- strokeWidth;
//        int right = MathUtils.findMax(refresh_LastX, last_X, refresh_X)+ strokeWidth;
//        int top = MathUtils.findMin(refresh_LastY, last_Y, refresh_Y)- strokeWidth;
//        int bottom = MathUtils.findMax(refresh_LastY, last_Y, refresh_Y)+ strokeWidth;
//
////        tlPoint.set(left, top);
////       // tlPoint = tlPoint.givePointAfterTransform(mImageView.getImageMatrix());
////        rbPoint.set(right,bottom);
////       // rbPoint = rbPoint.givePointAfterTransform(mImageView.getImageMatrix());
//
//       // mImageView.invalidate((int)tlPoint.x , (int)tlPoint.y  , (int)rbPoint.x, (int)rbPoint.y);
//	}
	
}
