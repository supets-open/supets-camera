package cn.jingling.lib.camera;

import android.os.Handler;

/**
 * @Copyright(C) 2013 Baidu.Tech.Co.Ltd. All rights reserved.
 * @Author:Nodin
 * @Description:
 * 
 * @Version:1.0
 * @Update:
 * 
 */
public class DelayCap extends WonderCamCap {
	private static final int DELAY_SHORT = 3;
	private static final int DELAY_MIDDLE = 5;
	private static final int DELAY_LONG = 10;
	
	public enum DelayLevel {
		SHORT, MIDDLE, LONG
	}
	
	private int mDelay = DELAY_SHORT;
	private OnDelayCapListener mListener;
	
	public interface OnDelayCapListener {
		void onDelayCapturing();
	}
	
	protected DelayCap(Handler handler) {
		super(handler);
	}

	public void setOnDelayCapListener(OnDelayCapListener l) {
		mListener = l;
	}
	
	public void setDelayLevel(DelayLevel level) {
		if (DelayLevel.SHORT.equals(level)) {
			mDelay = DELAY_SHORT;
		} else if (DelayLevel.MIDDLE.equals(level)) {
			mDelay = DELAY_MIDDLE;
		} else if (DelayLevel.LONG.equals(level)) {
			mDelay = DELAY_LONG;
		} else {
			mDelay = DELAY_SHORT;
		}
	}
	
	public void takePicture() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (null == mListener) {
					throw new NullPointerException("The OnDelayCapListener is null.Please check whether you have set it.");
				}
				mListener.onDelayCapturing();
			}
		}, mDelay * 1000L);
		
	}
}
