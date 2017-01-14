package cn.jingling.lib.textbubble;

import android.util.Log;

public class Pwog {
	private static boolean is = true;

	public static void closeAllLogs() {
		is = false;
	}

	public static void openAllLogs() {
		is = true;
	}

	public static void d(String tag, String msg) {
		if (is) {
			Log.d(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (is) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (is) {
			Log.e(tag, msg);
		}
	}
	
	public static void i(String tag, String msg) {
		if (is) {
			Log.i(tag, msg);
		}
	}

}
