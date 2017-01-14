package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.OneKeyFilter;

abstract public class SingleColor extends OneKeyFilter {

	public abstract void singleColorEffect(int[] pixels ,int w,int h,float[] matrix);

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		float matrix[]={ 0.3f, 0.59f, 0.11f, 0, 0,
				0.3f, 0.59f, 0.11f, 0, 0,
				0.3f,0.59f, 0.11f, 0, 0,
				0, 0, 0, 1, 0 };
		
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		singleColorEffect(pixels,w,h,matrix);
		
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		return bm;
	}

}
