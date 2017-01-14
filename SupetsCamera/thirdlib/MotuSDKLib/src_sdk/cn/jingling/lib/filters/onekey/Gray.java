package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.OneKeyFilter;

public class Gray extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		Canvas canvas = new Canvas();
		canvas.setBitmap(bm);//
		float[] a = { (float) 0.3, (float) 0.59, (float) 0.11, 0, 0,
				(float) 0.3, (float) 0.59, (float) 0.11, 0, 0, (float) 0.3,
				(float) 0.59, (float) 0.11, 0, 0, 0, 0, 0, 1, 0 };
		ColorMatrix cMatrix = new ColorMatrix();
		cMatrix.set(a);
		ColorMatrixColorFilter cFilter = new ColorMatrixColorFilter(cMatrix);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColorFilter(cFilter);
		canvas.drawBitmap(bm, 0, 0, paint);
		canvas.save();

			int w = bm.getWidth();
			int h = bm.getHeight();
			int[] pixels = new int[w * h];
			bm.getPixels(pixels, 0, w, 0, 0, w, h);
			CMTProcessor.contrastEffect(pixels, w, h, 60);
			bm.setPixels(pixels, 0, w, 0, 0, w, h);
			pixels = null;
			return bm;

	}

}
