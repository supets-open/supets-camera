package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.OneKeyFilter;
import cn.jingling.lib.filters.SmoothSkinProcessor;

public class BlackEyeArtificial extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		int []modifyColor = new int [1];
		modifyColor[0] = 0;
		SmoothSkinProcessor.produceArea(pixels, null, w, h, modifyColor);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);
		return bm;
	}

}
