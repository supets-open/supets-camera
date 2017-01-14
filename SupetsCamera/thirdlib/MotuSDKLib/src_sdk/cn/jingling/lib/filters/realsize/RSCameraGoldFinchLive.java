package cn.jingling.lib.filters.realsize;

import android.content.Context;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.ImageProcessUtils;

public class RSCameraGoldFinchLive extends RSLineFilter {

	private Curve mCurve1;
	private Curve mCurve2;
	
	@Override
	public boolean apply(Context cx, String inPath, String outPath, int[] args) {
		mCurve1 = new Curve(cx, "curves/live-goldfinch-1.dat");
		mCurve2 = new Curve(cx, "curves/live-goldfinch-2.dat");
		return super.apply(cx, inPath, outPath, args);
	}
	
	@Override
	protected void applyLine(Context cx, int[] pixels, int line, int height) {
		// TODO Auto-generated method stub
		int w = pixels.length;
		
		ImageProcessUtils.saturationPs(pixels, w, 1, -30);
		
		CMTProcessor.curveEffect(pixels, mCurve1.getCurveRed(), mCurve1.getCurveGreen(),
				mCurve1.getCurveBlue(), w, 1);
		
		CMTProcessor.curveEffect(pixels, mCurve2.getCurveRed(), mCurve2.getCurveGreen(),
				mCurve2.getCurveBlue(), w, 1);
		
	}

	@Override
	protected void releaseLayers() {
	
	}

}
