package cn.jingling.lib.textbubble;

import android.app.Activity;

public class ScreenInfo {
	public static int mScreenWidth;
	public static int mScreenHeight;

	public static void setScreenInfo(Activity activity) {
		mScreenWidth = activity.getWindowManager().getDefaultDisplay()
				.getWidth();
		mScreenHeight = activity.getWindowManager().getDefaultDisplay()
				.getHeight();
//		LayoutInfo.initLayoutParams(activity);
	}

	public static int getScreenWidth() {
		return mScreenWidth;
	}

	public static int getScreenHeight() {
		return mScreenHeight;
	}

	public static int getScreenWidth(Activity activity) {
		if (getScreenWidth() <= 0) {
			mScreenWidth = activity.getWindowManager().getDefaultDisplay()
					.getWidth();
		}
		return getScreenWidth();

	}

	public static int getScreenHeight(Activity activity) {
		if (mScreenHeight <= 0) {
			mScreenHeight = activity.getWindowManager().getDefaultDisplay()
					.getHeight();
		}
		return mScreenHeight;

	}

}