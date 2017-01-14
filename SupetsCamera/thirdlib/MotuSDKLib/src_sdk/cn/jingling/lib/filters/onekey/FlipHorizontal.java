package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import cn.jingling.lib.filters.OneKeyFilter;

/** 该类提供的编辑图片方法，不是是基于原图编辑的。会直生成修改后的新图，并返回，原图不会被修改。
 *
 */
public class FlipHorizontal extends OneKeyFilter{

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();

		Matrix matrix = new Matrix();
		int w, h;
		float sw = -1, sh = 1;
		w = bm.getWidth();
		h = bm.getHeight();
		matrix.reset();
		matrix.postScale(sw, sh);
		Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, w, h, matrix, true);
		return bitmap;
	}

}
