package cn.jingling.lib.utils;


public class ErrorHandleHelper {
	
	private static ErrorMsgMode sGlobalMode = ErrorMsgMode.Log_E;
	
	/** 建议开发调试时，选用ErrorMsgMode.RuntimeException方式，更容易发现定位问题。正式发布时，选用ErrorMsgMode.Log_E方式，更稳定。
	 * @param mode
	 */
	public static void setGlobalErrorMode(ErrorMsgMode mode) {
		sGlobalMode = mode;
		
	}
	
	public static ErrorMsgMode getGlobalErrorMode() {
		return sGlobalMode;
		
	}
	

	/** 根据GlobalErrorMode选取提示错误的方式。或抛Exception，或打印提示log。
	 *  建议开发调试时，选用ErrorMsgMode.RuntimeException方式，更容易发现定位问题。正式发布时，选用ErrorMsgMode.Log_E方式，更稳定。
	 * @param msg
	 * @param tag
	 */
	public static void handleErrorMsg(String msg, String tag) {
		
		if (sGlobalMode == ErrorMsgMode.RuntimeException) {
			LogUtils.e(tag, msg);
			throw new RuntimeException(msg);
		} else if (sGlobalMode == ErrorMsgMode.Log_E){
			LogUtils.e(tag, msg);
			new Throwable().printStackTrace();
		} else if (sGlobalMode == ErrorMsgMode.Log_W){
			LogUtils.w(tag, msg);
			new Throwable().printStackTrace();
		} else if (sGlobalMode == ErrorMsgMode.Log_V){
			LogUtils.v(tag, msg);
			new Throwable().printStackTrace();
		} else if (sGlobalMode == ErrorMsgMode.Log_D){
			LogUtils.d(tag, msg);
			new Throwable().printStackTrace();
		} else if (sGlobalMode == ErrorMsgMode.Log_I){
			LogUtils.i(tag, msg);
			new Throwable().printStackTrace();
		}
	}
	
	public enum ErrorMsgMode {
		RuntimeException, Log_E, Log_W, Log_V, Log_D, Log_I
	}

}
