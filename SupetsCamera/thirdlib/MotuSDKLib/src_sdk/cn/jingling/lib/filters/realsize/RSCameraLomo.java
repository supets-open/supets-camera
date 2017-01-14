package cn.jingling.lib.filters.realsize;

import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.Layer;
import android.content.Context;
import android.graphics.Bitmap;

public class RSCameraLomo extends RSLineFilter{

	private Curve mCurve;
	private Bitmap mLayerMul;
	private Bitmap mLayerLb;
	
	@Override
	public boolean apply(Context cx, String inPath, String outPath, int[] args) {
		mCurve = new Curve(cx, "curves/camera_lomo.dat");
		mLayerMul = Layer.getLayerImage(cx,
				"layers/camera_lomo_mul", Layer.Type.NORMAL, 50);
		mLayerLb = Layer.getLayerImage(cx,
				"layers/camera_lomo_lb", Layer.Type.NORMAL, 100);
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
		layerPixels = getLayerPixels(mLayerMul, line, height);
		CMTProcessor.rsMultiplyEffect(pixels, layerPixels, w, 1, mLayerMul.getWidth(), 1);
		
		//		
		layerPixels = getLayerPixels(mLayerLb, line, height);
		CMTProcessor.rsLinearBurn(pixels, layerPixels, w, 1, mLayerLb.getWidth(), 1, 100);
	}

	@Override
	protected void releaseLayers() {
		// TODO Auto-generated method stub
		mLayerMul.recycle();
		mLayerLb.recycle();	
	}

}
