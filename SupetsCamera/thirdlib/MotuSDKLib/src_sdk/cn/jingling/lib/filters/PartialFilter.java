package cn.jingling.lib.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;

/** 抽象类，继承自Filter类。提供了基于图片中某些局部輔助点而进行编辑的图像编辑接口。
 *  需要用户在屏幕选取图片中的辅助点（如眼睛位置、剪裁矩形位置）才能完成编辑功能。
 *  其继承类主要有Thin（瘦脸类）、EyeLarge（眼睛放大类）等。
 *
 */
abstract public class PartialFilter extends Filter {
//	protected Bitmap mOriginalBitmap;
	protected int mRadius = 10;
	private int mNeededPointNumber = 1;

	/** 
	 * @return 该PartialFilter实例需要几个辅助点，才可以完成其编辑功能。
	 */
	public int getNeededPointNumber() {
		return mNeededPointNumber;
	}

	protected void setNeededPointNumber(int neededPointNumber) {
		this.mNeededPointNumber = neededPointNumber;
	}

	public void setup(Context cx, Bitmap bm) {
		this.statisticEvent();
	}
	
	public void release() {
		
	}
	
	/** 设置PartialFilter的radius。不设置则为默认值：20。
	 * 有些PartialFilter不需要radius，则该函数的调用不会影响编辑结果。
	 * @param radius
	 */
	public void setRadius(int radius) {
		mRadius = radius;
	}

	public Bitmap apply(Bitmap bm, Point point) {
		return null;
	};
	
	public Bitmap apply(Bitmap bm, Point[] point) {
		return apply(bm, point[0]);
	};

	
}
