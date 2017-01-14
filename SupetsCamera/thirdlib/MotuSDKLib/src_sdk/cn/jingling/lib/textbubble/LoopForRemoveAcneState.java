package cn.jingling.lib.textbubble;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;

import cn.jingling.lib.filters.CMTProcessor;


public class LoopForRemoveAcneState {

	private int last_X;
	private int last_Y;
	private int refresh_LastX;
	private int refresh_LastY;
	protected Paint paint =  new Paint();
	private Path mPath =  new  Path();
	private Canvas mCanvas;
	private ImageView mImageView;
	private static int penWidth = 1;
	private static int penColor = Color.rgb(103, 186, 19);
	//private Dialog mDialog = null;
	private RectF rectF;
	private GroundImage mGroundImage;
	
	public LoopForRemoveAcneState(Canvas canvas,ImageView imageView)
	{
		mCanvas = canvas;
		mImageView = imageView;
		//mDialog = LayoutController.getSingleton().getEffectProcessingDialog();
		
		initPaint();
		mGroundImage = ScreenControl.getSingleton().mGroundImage;
		rectF = new RectF();
		//mCanvas.drawBitmap(ScreenControl.getSingleton().getGroundImage().getBitmap(), ScreenControl.getSingleton().getGroundImage().getImageMatrix(), null);
		//ScreenControl.getSingleton().getGroundImage().getImageView().setVisibility(View.GONE);
	}
	
	private void initPaint()
	{
		paint =  new Paint();
		paint.setStrokeWidth(penWidth);
		paint.setColor(penColor);
		paint.setDither(true);
		paint.setStrokeJoin(Join.ROUND);
		paint.setStrokeMiter(90);
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);
		paint.setStrokeCap(Paint.Cap.ROUND);
		
	}
	
	public void mouseDown(PwMotion mEvent)
	{
        int x = (int)mEvent.getX();
        int y = (int)mEvent.getY();

		last_X = x;
		last_Y = y;
		mPath.reset();
		mPath.moveTo(last_X,last_Y);
        refresh_LastX = x;
        refresh_LastY = y;
        
        mCanvas.drawPoint(x, y, paint);
        //mImageView.invalidate(x-getPenWidth()/2-1,y-getPenWidth()/2-1,x+getPenWidth()/2+1,y+getPenWidth()/2+1);
	}
	
	public void mouseMove(PwMotion mEvent)
	{
        int x = (int)mEvent.getX();
        int y = (int)mEvent.getY();
        
		float dx = Math.abs(x - last_X);
	    float dy = Math.abs(y - last_Y);
	    if (dx >= 3 || dy >= 3) 
	    {
			if(mEvent.getPointerCount() == 1)
			{
		    	Rect tempRect =  PointSToRect(last_X,last_Y, (x + last_X)/2,(y + last_Y)/2,refresh_LastX,refresh_LastY);
		    	refresh_LastX = (x + last_X)/2;
			    refresh_LastY = (y + last_Y)/2;
		        mPath.quadTo(last_X, last_Y, refresh_LastX, refresh_LastY);	
		        mCanvas.drawPath(mPath, paint);
		        mImageView.invalidate(tempRect.left-penWidth/2-1,tempRect.top-penWidth/2-1,tempRect.right+penWidth/2+1,tempRect.bottom+penWidth/2+1);
				last_X = x;
				last_Y = y;
			}
	    }
	}
	
	public void mouseUp(PwMotion mEvent)
	{
        int x = (int)mEvent.getX();
        int y = (int)mEvent.getY();
        
		if(mEvent.getPointerCount() == 1)
		{
			//完成圈定，擦除轨迹
			mPath.lineTo(x, y);
	        paint.setXfermode(new PorterDuffXfermode(
	                PorterDuff.Mode.CLEAR));
	        mCanvas.drawPaint(paint);
	        initPaint();
	        //call for removeAcne
	        removeAcne();
		}
	}
	
	private void removeAcne()
	{
        //获取Path矩形
		mPath.computeBounds(rectF, true);
		
		
		
		Bitmap bitmap = mGroundImage.getBitmap();
		float[] val = new float[9];
		mGroundImage.getImageMatrix().getValues(val);
		float scale = (float) Math.sqrt(val[0] * val[0] + val[1] * val[1]);
		rectF.left   = rectF.left/scale-val[2]/scale;
		rectF.right  = rectF.right/scale-val[2]/scale;
		rectF.top    = rectF.top/scale-val[5]/scale;
		rectF.bottom = rectF.bottom/scale-val[5]/scale;
		
		mGroundImage.refresh();
		if(rectF.left<0)return;
		if(rectF.top<0)return;
		if(rectF.right>=mGroundImage.getBitmap().getWidth())return;//rectF.right = mGroundImage.getBitmap().getWidth()-1;
		if(rectF.bottom>=mGroundImage.getBitmap().getHeight())return;//rectF.bottom = mGroundImage.getBitmap().getHeight()-1;

/*		int w = bitmap.getWidth();
		int h = bitmap.getHeight();*/
//		int r = (int) (10 / scale) + 10;
		int x0 = (int) rectF.left;
		int y0 = (int) rectF.top;
		int x1 = (int) rectF.right;
		int y1 = (int) rectF.bottom;
		
		int xr0 = (x1-x0)/2;
		int yr0 = (y1-y0)/2;
		int h0 = y1 - y0;
		int w0 = x1 - x0;
		
		if(h0>w0*2)h0=w0*2;
		if(w0>h0*2)w0=h0*2;
		
		int r = h0>w0?(w0/2):(h0/2);
		try {
			int[] pixels = new int[w0 * h0];
			for(int i = 0 ;i<1;i++)
			{
				bitmap.getPixels(pixels, 0, w0, x0, y0, w0, h0);
				if(w0<6||h0<6)
				{
					miniSmooth(pixels,w0,h0);
				}
				else
				{
					skinSmooth(pixels,w0,h0,xr0,yr0,r);
				}
				for(int j = 0;j<3;j++)
				CMTProcessor.skinSmoothPointEffect(pixels, w0, h0, xr0, yr0, r
				);
				bitmap.setPixels(pixels, 0, w0, x0, y0, w0, h0);
			}
			mGroundImage.refresh();
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			e.printStackTrace();
			

		}
		
        mGroundImage.refresh();
        
//		SingleOperationQueue.getSingleton().addCheckPoint(mGroundImage.getBitmap(), false);

        //替换pixels
	}
	private void miniSmooth(int [] pixels,int width,int height)
	{
		if(width*height<9||width<3||height<3)return;
		int pos = 0;
		int size = width*2+height*2-4;
		int[] values = new int[size];	//边缘
		for(int i = 0;i<width;i++)
		{
			values[pos++] = pixels[i];
			values[pos++] = pixels[(height-1)*width+i];
		}
		for(int i = 1;i<height-1;i++)
		{
			values[pos++] = pixels[i*width];
			values[pos++] = pixels[i*width+width-1];
		}
		for(int i = 0;i<size;i++)
		{
			for(int j = i+1;j<size;j++)
			{
				if(values[j]>values[i])
				{
					int a = values[i];
					values[i] = values[j];
					values[j] = a;
				}
			}
		}
		int mid = values[size/2];
		for(int i = 0;i<width * height;i++)
		{
			pixels[i] = mid;
		}
	}
	
	private void skinSmooth(int [] pixels,int width,int height,int centerX,int centerY,int r)
	{
		for(int i = 1;i<width/2;i++)
		{
			for(int j = height/4;j<height/2;j++)
			{
				pixels[j*width+i] = getMidPixel(pixels[(j-1)*width+i-1],pixels[(j-1)*width+i],pixels[(j-1)*width+i-1]);
			}
			for(int j=height-height/4-1;j>=height/2;j--)
			{
				pixels[j*width+i] = getMidPixel(pixels[(j+1)*width+i-1],pixels[(j+1)*width+i],pixels[(j-1)*width+i-1]);
			}
		}
		for(int i = width-2;i>=width/2;i--)
		{
			for(int j = height/4;j<height/2;j++)
			{
				pixels[j*width+i] = getMidPixel(pixels[(j-1)*width+i+1],pixels[(j-1)*width+i],pixels[(j-1)*width+i+1]);
			}
			for(int j=height-height/4-1;j>=height/2;j--)
			{
				pixels[j*width+i] = getMidPixel(pixels[(j+1)*width+i+1],pixels[(j+1)*width+i],pixels[(j-1)*width+i+1]);
			}
		}
	}
	
	private int getMidPixel(int p1,int p2,int p3)
	{
		int[] p = new int[3];
		p[0] = p1;
		p[1] = p2;
		p[2] = p3;
		for(int j=0;j<2;j++)
		for(int i = 0;i<2;i++)
		{
			if(p[i]>p[i+1])
			{
				int c = p[i];
				p[i] = p[i+1];
				p[i+1] = c;
			}
		}
		return p[1];
	}
	
	
	
	private Rect PointSToRect(int x1,int y1,int x2,int y2,int x3,int y3)
    {
    	Rect rect =  new Rect();
    	if(x1<=x2)
    	{
    		rect.left=x1;
    		rect.right=x2;
    	}
    	else
    	{
    		rect.left=x2;
    		rect.right=x1;
    	}
    	if(x3<=rect.left)
    	{
    		rect.left = x3;
    	}
    	if(x3>=rect.right)
    	{
    		rect.right = x3;
    	}
       	if(y1<=y2)
    	{
    		rect.top=y1;
    		rect.bottom=y2;
    	}
    	else
    	{
    		rect.top=y2;
    		rect.bottom=y1;
    	}
    	if(y3<=rect.top)
    	{
    		rect.top = y3;
    	}
    	if(y3>=rect.bottom)
    	{
    		rect.bottom = y3;
    	}
    	return rect;
    }
	
}
