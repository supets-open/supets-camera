package cn.jingling.lib.filters.realsize;

import android.content.Context;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;


abstract public class RSCurveFilter extends RSLineFilter {

	private Curve mCurve;
	
	@Override
	public boolean apply(Context cx, String inPath, String outPath, int[] args) {
		mCurve = new Curve(cx, getCurvePath());
		return super.apply(cx, inPath, outPath, args);
	}
	
	@Override
	protected void applyLine(Context cx, int[] pixels, int line, int height) {
		// TODO Auto-generated method stub
		int w = pixels.length;
		CMTProcessor.curveEffect(pixels, mCurve.getCurveRed(), mCurve.getCurveGreen(), mCurve.getCurveBlue(), w, 1);
	}
	
	abstract protected String getCurvePath();
	
	@Override
	protected void releaseLayers() {
		// TODO Auto-generated method stub
		// Don't need to release because we don't have any layers.
	}

}
