package cn.jingling.lib.filters;

import android.content.Context;
import android.graphics.Bitmap;

/** GlobalFilter的修改效果可以设置为不同的程度。可以设置Degree来调整Filter的效果。这类Filter有：饱和度、亮度、对比度、锐度等。
 *  
 */
abstract public class GlobalFilter extends Filter {
	
	protected Bitmap mOriginalBitmap;
	
	private int mSeekBarNumber = 1;
	private static int DefaultSeekBarValue = 50;
	
	public void setup(Context cx, Bitmap bm) {
		this.statisticEvent();
		mOriginalBitmap = bm.copy(bm.getConfig(), true);
	}
	
	protected void setSeekBarNumber(int n) {
		mSeekBarNumber = n;
	}
	
	/** 某些DegreeFilter可能需要多种Degree组合效果。一般情况下，该值为1。
	 * 
	 */
	public int getSeekBarNumber() {
		return mSeekBarNumber;
	}
	
	public int getDefaultSeekBarValue() {
		return DefaultSeekBarValue;
	}

	/**
	 * 
	 * @param cx
	 * @param degree from 0 to 100
	 * @return result Bitmap
	 */
	public Bitmap apply(Context cx, int degree) {
		return null;
	}
	
	/**
	 * 
	 * @param cx
	 * @param degree from 0 to 100
	 * @return result Bitmap
	 */
	public Bitmap apply(Context cx, int[] degrees) {
		return null;
	}
	
	public void release() {
		mOriginalBitmap.recycle();
		mOriginalBitmap = null;
	}
}
