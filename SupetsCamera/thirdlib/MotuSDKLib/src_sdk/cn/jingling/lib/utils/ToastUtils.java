package cn.jingling.lib.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtils {
	private static Context sAppContext;
	private static Toast sToastMiddle;
	private static Toast sToast;

	public static void init(Context cx) {
		sAppContext = cx.getApplicationContext();
	}

	public static void show(String s) {
		if (sToast == null) {
			sToast = Toast.makeText(sAppContext, s, Toast.LENGTH_SHORT);
		} else {
			sToast.setText(s);
		}
		sToast.show();
	}

	public static void show(int resId) {
		if (sToast == null) {
			sToast = Toast.makeText(sAppContext, resId, Toast.LENGTH_SHORT);
		} else {
			sToast.setText(resId);
		}
		
		sToast.show();
	}

	public static void showToastShortMiddle(int textID) {
		if (sToastMiddle == null) {
			sToastMiddle = Toast.makeText(sAppContext.getApplicationContext(),
					sAppContext.getString(textID), Toast.LENGTH_SHORT);
		}
		sToastMiddle.setGravity(Gravity.CENTER, 0, 0);
		sToastMiddle.setDuration(Toast.LENGTH_SHORT);
		sToastMiddle.setText(sAppContext.getString(textID));
		sToastMiddle.show();
	}
}
