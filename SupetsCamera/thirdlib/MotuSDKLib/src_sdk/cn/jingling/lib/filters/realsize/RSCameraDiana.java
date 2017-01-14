package cn.jingling.lib.filters.realsize;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.Layer;

public class RSCameraDiana extends RSLineFilter {


	private Curve mCurve;
	private Bitmap mLayerLb;
	
	@Override
	public boolean apply(Context cx, String inPath, String outPath, int[] args) {
		mCurve = new Curve(cx, "curves/camera_diana.dat");
		mLayerLb = Layer.getLayerImage(cx,
				"layers/camera_diana_lb", Layer.Type.NORMAL, 100);
		return super.apply(cx, inPath, outPath, args);
	}
	
	@Override
	protected void applyLine(Context cx, int[] pixels, int line, int height) {
		// TODO Auto-generated method stub
	    // 此时处理的是单行数据
		int w = pixels.length;
		int[] layerPixels;
		//
		CMTProcessor.curveEffect(pixels, mCurve.getCurveRed(), mCurve.getCurveGreen(), mCurve.getCurveBlue(), w, 1);
		// 	
		layerPixels = getLayerPixels(mLayerLb, line, height);
		CMTProcessor.rsLinearBurn(pixels, layerPixels, w, 1, mLayerLb.getWidth(), 1, 100);
	}

	@Override
	protected void releaseLayers() {
		// TODO Auto-generated method stub
		mLayerLb.recycle();	
	}


}
