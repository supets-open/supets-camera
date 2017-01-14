package cn.jingling.lib.utils;

import android.util.Log;

/**
 * @author liuhao04
 *
 */
public class LogUtils {
	private static boolean isLog = true;
	
	public static void close() {
		isLog = false;
	}
	
	public static void d(String tag,String msg){
		if(isLog){
			Log.d(tag, msg);
		}
	}
	
	public static void e(String tag,String msg){
		if(isLog){
			Log.e(tag, msg);
		}
	}
	
	public static void v(String tag,String msg){
		if(isLog){
			Log.v(tag, msg);
		}
	}
	
	public static void w(String tag,String msg){
		if(isLog){
			Log.w(tag, msg);
		}
	}
	
	public static void i(String tag,String msg){
		if(isLog){
			Log.i(tag, msg);
		}
	}
	
	public static boolean ISDEBUG() {
		return isLog;
	}
}
