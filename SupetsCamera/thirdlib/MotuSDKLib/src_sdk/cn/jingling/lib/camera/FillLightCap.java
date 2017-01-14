package cn.jingling.lib.camera;

import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * @Copyright(C) 2013 Baidu.Tech.Co.Ltd. All rights reserved.
 * @Author:Nodin
 * @Description:
 * 
 * @Version:1.0
 * @Update:
 * 
 */
public class FillLightCap extends WonderCamCap {
	private static final long DELAY = 100L;

	private View mFillLightView;

	private OnFillLightCapListener mListener;
	
	public interface OnFillLightCapListener {
		void onFillLightCapturing();
	}
	
	public void setOnFillLightCapListener(OnFillLightCapListener l) {
		mListener = l;
	}
	
	protected FillLightCap(Handler handler) {
		super(handler);
	}

	public void setFillLightView(View view) {
		if (!(view instanceof FillLightView)) {
			throw new ClassCastException("The android.view.View cannot be casted to cn.jingling.lib.camera.FillLightView.");
		}
		mFillLightView = view;
		mFillLightView.setVisibility(View.GONE);
	}
	
	public void close() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				startLightingTransition();
			}
		});
	}

	public void takePicture() {
		mFillLightView.setVisibility(View.VISIBLE);
		
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (null == mListener) {
					throw new NullPointerException("The OnFillLightCapListener is null.Please check whether you have set it.");
				}
				mListener.onFillLightCapturing();
			}
		});

	}
	
	private void startLightingTransition() {
		AlphaAnimation alphaAnim = new AlphaAnimation(1.0f,0.0f);
		alphaAnim.setDuration(DELAY);
		alphaAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				mFillLightView.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationStart(Animation animation) { }

			@Override
			public void onAnimationRepeat(Animation animation) { }
		});
		mFillLightView.startAnimation(alphaAnim);
	}
}
