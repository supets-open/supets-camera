package cn.jingling.lib.filters.realsize;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.Layer;

public class RSCameraGuangyinLive extends RSLineFilter {

	private Curve mCurve1, mCurve2;
	private Bitmap mLayer;
	
	@Override
	public boolean apply(Context cx, String inPath, String outPath, int[] args) {
		mCurve1 = new Curve(cx, "curves/live_guangyin1.dat");
		mCurve2 = new Curve(cx, "curves/live_guangyin2.dat");
		mLayer = Layer.getLayerImage(cx, "layers/live_guangyin", Layer.Type.NORMAL);
		return super.apply(cx, inPath, outPath, args);
	}
	
	@Override
	protected void applyLine(Context cx, int[] pixels, int line, int height) {
		// TODO Auto-generated method stub
		int w = pixels.length;
		int[] layerPixels;
		
		CMTProcessor.curveEffect(pixels, mCurve1.getCurveRed(), mCurve1.getCurveGreen(),
				mCurve1.getCurveBlue(), w, 1);
		//
		layerPixels = getLayerPixels(mLayer, line, height);
		CMTProcessor.rsOverlayAlphaEffect(pixels, layerPixels, w, 1, mLayer.getWidth(), 1, 30);
		
		//
		ImageProcessUtils.saturationPs(pixels, w, 1, -55);
		
		CMTProcessor.curveEffect(pixels, mCurve2.getCurveRed(), mCurve2.getCurveGreen(),
				mCurve2.getCurveBlue(), w, 1);
	}

	@Override
	protected void releaseLayers() {
		mLayer.recycle();	
	}

}
