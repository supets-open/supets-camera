package cn.jingling.lib.filters.partial;

import android.graphics.Bitmap;
import android.graphics.Point;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.PartialFilter;
import cn.jingling.lib.utils.LogUtils;

/** 瘦脸编辑类，继承自PartialFilter。需要输入两个辅助点：推压起始位置、推压终止位置。支持设置degree。
 *  该类提供的编辑图片方法，是基于原图编辑的，会直接修改原图的pixels，返回的bitmap就是原图bitmap。
 *
 */
public class Thin extends PartialFilter {

	private final static String TAG = "Thin";
	
	public Thin() {
		setNeededPointNumber(2);
	}
	
	/** 编辑图片方法。是基于原图编辑的，直接修改原图的pixels，返回的bitmap就是原图bitmap。
	 * @param bm 输入的原图。
	 * @param point 辅助点：推压起始位置、推压终止位置。
	 * @return 修改后的图片。
	 * @see cn.jingling.lib.filters.PartialFilter#apply(android.graphics.Bitmap, android.graphics.Point)
	 */
	@Override
	public Bitmap apply(Bitmap bm, Point[] Points) {
		Point downPoint = Points[0];
		Point upPoint = Points[1];
		applyThinBitmap(bm, Math.min(bm.getWidth(), bm.getHeight()) / 2,
				downPoint.x, downPoint.y, upPoint.x, upPoint.y);
		return bm;
	}

	private void applyThinBitmap(Bitmap bm, int thinSize, int x1, int y1,
			int x2, int y2) {
		int w = bm.getWidth();
		int h = bm.getHeight();

		int top = y1 - thinSize / 2;
		int bottom = y1 + thinSize / 2;
		int left = x1 - thinSize / 2;
		int right = x1 + thinSize / 2;

		if (right > w - 1) {
			right = w - 1;
		}
		if (left < 0) {
			left = 0;
		}
		if (top < 0) {
			top = 0;
		}
		if (bottom > h - 1) {
			bottom = h - 1;
		}

		int tw = right - left;
		int th = bottom - top;

		if (tw % 2 != 0) {
			tw--;
		}
		if (th % 2 != 0) {
			th--;
		}

		int pixels[] = new int[tw * th];
		bm.getPixels(pixels, 0, tw, left, top, tw, th);
		LogUtils.d(TAG, String.format("left:%d  top:%d  tw:%d  th:%d", left, top, tw, th));
		LogUtils.d(TAG, String.format("w:%d  h:%d  cx:%d  cy:%d  x2:%d  y2:%d  r:%d  degree:%f",  tw, th, tw / 2, th / 2, tw / 2 + x2
				- x1, th / 2 + y2 - y1, Math.max(tw, th) / 2, 0.2f));
		CMTProcessor.thinEffect(pixels, tw, th, tw / 2, th / 2, tw / 2 + x2
				- x1, th / 2 + y2 - y1, Math.max(tw, th) / 2, mRadius/100f, 0);
		bm.setPixels(pixels, 0, tw, left, top, tw, th);

	}

}
