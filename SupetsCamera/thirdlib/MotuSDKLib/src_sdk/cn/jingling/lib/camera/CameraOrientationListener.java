package cn.jingling.lib.camera;

import android.content.Context;
import android.view.OrientationEventListener;

/**
 * This Class extends OrientationEventListener, so should be enabled in onResume and disabled in onPause
 * @author jiankun.zhi
 *
 */
public class CameraOrientationListener extends OrientationEventListener {
	private int mScreenDirection;
	
	/**
	 * 
	 * @param context
	 */
	public CameraOrientationListener(Context context) {
		super(context);
	}
	
	/**
	 * 
	 * @return From 0 to 3, equals to ((orientation + 45) % 360) / 90 from OrientationEventListener
	 */
	public int getOrientation() {
		return mScreenDirection;
	}
	
	@Override
	public void onOrientationChanged(int orientation) {
		mScreenDirection = ((orientation + 45) % 360) / 90;
	}
}
