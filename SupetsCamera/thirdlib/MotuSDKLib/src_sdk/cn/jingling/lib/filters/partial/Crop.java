package cn.jingling.lib.filters.partial;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import cn.jingling.lib.filters.PartialFilter;


/** 剪裁编辑类，继承自PartialFilter。需要输入两个个辅助点：剪裁矩形左上角、剪裁矩形右下角。不支持设置degree。
 *  该类提供的编辑图片方法，不是是基于原图编辑的。会生成修改后的新图，并返回，原图不会被修改。
*
*/
public class Crop extends PartialFilter{
	
	public Crop() {
		setNeededPointNumber(2);
	}
	
	/** 编辑图片方法。不是是基于原图编辑的。会生成修改后的新图，并返回，原图不会被修改。
	 * @param bm 输入的原图。
	 * @param point 辅助点：剪裁矩形左上角、剪裁矩形右下角。
	 * @return 修改后的图片。
	 * @see cn.jingling.lib.filters.PartialFilter#apply(android.graphics.Bitmap, android.graphics.Point)
	 */
	@Override
	public Bitmap apply(Bitmap bm, Point[] point) {
		Point leftup = point[0];
		Point rightdown = point[1];
		
		return apply(bm, leftup, rightdown );
		
	}
	
	private Bitmap apply(Bitmap bm, Point leftup, Point rightdown ) {
		int w, h;
		w = bm.getWidth();
		h = bm.getHeight();
		if(leftup.x >= 0  && leftup.y >=0 && rightdown.x <= w &&  rightdown.y<= h ){
			int left = Math.min(leftup.x, rightdown.x);
			int right = Math.max(leftup.x, rightdown.x);
			int top = Math.min(leftup.y, rightdown.y);
			int bottom = Math.max(leftup.y, rightdown.y);
			
			Bitmap bitmap = Bitmap.createBitmap(bm, left, top, right-left, bottom - top);
			// 原图是上层传过来的引用，不能确定是否还有用，这里不能释放。
			//bm.recycle();	
			return bitmap;
		}
		else{
			return null;
			
		}
	}

}
