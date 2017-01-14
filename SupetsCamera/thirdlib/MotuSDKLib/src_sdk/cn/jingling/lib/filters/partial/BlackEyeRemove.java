package cn.jingling.lib.filters.partial;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import cn.jingling.lib.filters.PartialFilter;
import cn.jingling.lib.filters.SmoothSkinProcessor;

public class BlackEyeRemove extends PartialFilter{

	public BlackEyeRemove() {
		setNeededPointNumber(2);
	}
	
	@Override
	public Bitmap apply(Bitmap bm, Point[] point) {

		int w = bm.getWidth();
		int h = bm.getHeight();
		Point left = point[0];
		Point right = point[1];
		int radius = Math.abs((right.x - left.x)) / 3;

		int[] pixels = new int[w * h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);

		SmoothSkinProcessor.InitializeCircle(left.y, left.x, radius, right.y,
				right.x, radius, pixels, w, h, mRadius);
		bm.setPixels(pixels, 0, w, 0, 0, w, h);

		return bm;
	}

}
