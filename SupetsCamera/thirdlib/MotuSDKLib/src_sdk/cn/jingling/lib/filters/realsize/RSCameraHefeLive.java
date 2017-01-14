package cn.jingling.lib.filters.realsize;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.Layer;

public class RSCameraHefeLive extends RSLineFilter {

	private Curve mCurve;
	private Bitmap mLayer_m;
	private Bitmap mLayer_o;
	
	@Override
	public boolean apply(Context cx, String inPath, String outPath, int[] args) {
		mCurve = new Curve(cx, "curves/live_hefe.dat");
		mLayer_o = Layer.getLayerImage(cx, "layers/live_hefe_o", Layer.Type.NORMAL);
		mLayer_m = Layer.getLayerImage(cx, "layers/live_hefe_m", Layer.Type.NORMAL, 80*255/100);
		return super.apply(cx, inPath, outPath, args);
	}
	
	@Override
	protected void applyLine(Context cx, int[] pixels, int line, int height) {
		// TODO Auto-generated method stub
		int w = pixels.length;
		int[] layerPixels;
		
		//
		layerPixels = getLayerPixels(mLayer_m, line, height);
		CMTProcessor.rsMultiplyEffect(pixels, layerPixels, w, 1, mLayer_m.getWidth(), 1);
		
		layerPixels = getLayerPixels(mLayer_o, line, height);
		CMTProcessor.rsOverlayAlphaEffect(pixels, layerPixels, w, 1, mLayer_o.getWidth(), 1, 20);
		
		CMTProcessor.curveEffect(pixels, mCurve.getCurveRed(), mCurve.getCurveGreen(),
				mCurve.getCurveBlue(), w, 1);
	}

	@Override
	protected void releaseLayers() {
		mLayer_m.recycle();	
		mLayer_o.recycle();	
	}

}
