package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import cn.jingling.lib.filters.OneKeyFilter;


public class RotateAntiClockwise extends OneKeyFilter {
	
	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		
		Matrix matrix = new Matrix();
		float angle = -90f;
		int w, h;
		w = bm.getWidth();
		h = bm.getHeight();
		matrix.reset();
		matrix.setRotate(angle);
		Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, w, h, matrix, true);		
		bm.recycle();	
		return bitmap;
	}
	
}
