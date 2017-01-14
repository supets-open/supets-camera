package cn.jingling.lib.camera;

import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * @Copyright(C) 2013 Baidu.Tech.Co.Ltd. All rights reserved.
 * @Author:Nodin
 * @Description:
 * 
 * @Version:1.0
 * @Update:
 * 
 */
public class TouchCap extends WonderCamCap {
	public enum TouchCapMode {
		MODE_SINGLETAP, MODE_DOUBLETAP
	}
	private TouchCapMode mMode = TouchCapMode.MODE_SINGLETAP;
	private GestureDetector mGestureDetector;
	private OnTouchCapListener mOnTouchCapListener;
	
	public interface OnTouchCapListener {
		void onTouchCapturing();
	}
	
	public TouchCap(Handler handler) {
		super(handler);
		mGestureDetector = new GestureDetector(new MyGestureDetector());
	}

	public void setOnTouchCapListener(OnTouchCapListener l) {
		mOnTouchCapListener = l;
	}
	
	public void setTouchCapMode(TouchCapMode type) {
		mMode = type;
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	private void takePicture() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (null == mOnTouchCapListener) {
					throw new NullPointerException("The OnTouchCapListener is null.Please check whether you have set it.");
				}
				mOnTouchCapListener.onTouchCapturing();
			}
		});

	}
	
	class MyGestureDetector implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (TouchCapMode.MODE_SINGLETAP == mMode) {
				takePicture();
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (TouchCapMode.MODE_DOUBLETAP == mMode) {
				takePicture();
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}
	}
}
