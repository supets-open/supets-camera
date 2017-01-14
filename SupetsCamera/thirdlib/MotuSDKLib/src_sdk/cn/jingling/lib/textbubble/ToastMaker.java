package cn.jingling.lib.textbubble;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastMaker {
	private static Toast toast;
	private static Toast toastMiddle;
	private static Toast toastMidUp;
	
	private static Context mAppContext;
	
	public static void init(Context context)
	{
		mAppContext = context.getApplicationContext();
	}

	public static void showToastLong(String text)
	{
		showToast(mAppContext, text,Toast.LENGTH_LONG);
	}
	
	public static void showToastLong(int textID)
	{
		showToast(mAppContext, mAppContext.getString(textID),Toast.LENGTH_LONG);
	}
	
	public static void showToastShort(String text)
	{
		showToast(mAppContext, text,Toast.LENGTH_SHORT);
	}
	
	public static void showToastShort(int textID)
	{
		showToast(mAppContext, mAppContext.getString(textID),Toast.LENGTH_SHORT);
	}
	
	private static void showToast(Context cx, String text,int duration)
	{
		if(toast == null)
		{
			toast = Toast.makeText(cx, text,duration);
		}
		toast.setDuration(duration);
		toast.setText(text);
		toast.show();
	}
	
	public static void showToastShortMiddle(int textID)
	{
		if(toastMiddle == null)
		{
			toastMiddle = Toast.makeText(mAppContext.getApplicationContext(), mAppContext.getString(textID),Toast.LENGTH_SHORT);
		}
		toastMiddle.setGravity(Gravity.CENTER, 0, 0);
		toastMiddle.setDuration(Toast.LENGTH_SHORT);
		toastMiddle.setText(mAppContext.getString(textID));
		toastMiddle.show();
	}
	
	public static void showToastLongMiddle(int textID)
	{
		if(toastMiddle == null)
		{
			toastMiddle = Toast.makeText(mAppContext.getApplicationContext(), mAppContext.getString(textID),Toast.LENGTH_LONG);
		}
		toastMiddle.setGravity(Gravity.CENTER, 0, 0);
		toastMiddle.setDuration(Toast.LENGTH_SHORT);
		toastMiddle.setText(mAppContext.getString(textID));
		toastMiddle.show();
	}
	
	public static void showToastShortMidUp(int textID)
	{
		if(toastMidUp == null)
		{
			toastMidUp = Toast.makeText(mAppContext.getApplicationContext(), mAppContext.getString(textID),Toast.LENGTH_LONG);
		}
		toastMidUp.setGravity(Gravity.TOP, 0, 0);
		toastMidUp.setMargin(0, 0.25f);
		toastMidUp.setDuration(Toast.LENGTH_SHORT);
		toastMidUp.setText(mAppContext.getString(textID));
		toastMidUp.show();
	}

	public static void showToastShortMidUpLong(int textID)
	{
		if(toastMidUp == null)
		{
			toastMidUp = Toast.makeText(mAppContext.getApplicationContext(), mAppContext.getString(textID),Toast.LENGTH_LONG);
		}
		toastMidUp.setGravity(Gravity.TOP, 0, 0);
		toastMidUp.setMargin(0, 0.25f);
		toastMidUp.setDuration(Toast.LENGTH_LONG);
		toastMidUp.setText(mAppContext.getString(textID));
		toastMidUp.show();
	}
	
	public static void hideToastMidUp(){
		if (toastMidUp!=null) {
			toastMidUp.cancel();
		}
	}
}
