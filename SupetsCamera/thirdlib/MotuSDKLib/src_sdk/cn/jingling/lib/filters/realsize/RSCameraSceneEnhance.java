package cn.jingling.lib.filters.realsize;

import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.SmoothSkinProcessor;
import android.content.Context;

public class RSCameraSceneEnhance extends RSLineFilter {

	@Override
	protected void applyLine(Context cx, int[] pixels, int line, int height) {
		// TODO Auto-generated method stub
		int w = pixels.length;
		Curve c;
		c = new Curve(cx, "curves/color_enhance.dat");
		SmoothSkinProcessor.sceneEnhance(pixels, w, 1, 100, 100, c.getCurveRed());
	}

	@Override
	protected void releaseLayers() {
		// TODO Auto-generated method stub
		
	}

}
