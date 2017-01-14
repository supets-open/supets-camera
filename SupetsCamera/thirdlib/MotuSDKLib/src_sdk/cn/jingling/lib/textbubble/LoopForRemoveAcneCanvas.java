package cn.jingling.lib.textbubble;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class LoopForRemoveAcneCanvas extends ImageView implements OnTouchListener, OnDoubleTapListener, OnGestureListener{

	public Bitmap pathBitmap;
	private Canvas canvas;
	private GroundImage mGroundImage = ScreenControl.getSingleton().getGroundImage();
	private LoopForRemoveAcneState mRmoveAcneState;
	private GestureDetector mGestureDetector = null;
	
	protected PwMotion mEvent = new PwMotionHigh();
	
	public LoopForRemoveAcneCanvas(Context context) {
		super(context);
		this.setScaleType(ScaleType.MATRIX);
		pathBitmap = Bitmap.createBitmap(mGroundImage.getBitmap().getWidth(), mGroundImage.getBitmap().getHeight(), Config.ARGB_8888);
		this.setImageBitmap(pathBitmap);
		
		canvas = new Canvas(pathBitmap);
		Matrix matrix = new Matrix();
		mGroundImage.getImageMatrix().invert(matrix);
		canvas.setMatrix(matrix);
		
		mRmoveAcneState = new LoopForRemoveAcneState(canvas,this);
		// TODO Auto-generated constructor stub
		mGestureDetector = new GestureDetector(this);
		mGestureDetector.setOnDoubleTapListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (ScreenControl.getSingleton().getGestureDetector().onTouchEvent(event))
			return true;
		
		mEvent.setEvent(event);
		if(mEvent.getPointerCount() == 1)
		{
			switch(mEvent.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					mRmoveAcneState.mouseDown(mEvent);
					break;
				case MotionEvent.ACTION_MOVE:
					mRmoveAcneState.mouseMove(mEvent);
					break;
				case MotionEvent.ACTION_UP:
					mRmoveAcneState.mouseUp(mEvent);
					break;
			}
		}
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		mGroundImage.onDoubleTap(e);
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
