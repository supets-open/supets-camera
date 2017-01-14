package cn.jingling.lib.filters.realsize;

import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.Layer;
import android.content.Context;
import android.graphics.Bitmap;

public class RSCameraLiunian extends RSLineFilter{

	private Curve mCurve;
	private Bitmap mLayerMul;
	private Bitmap mLayerlb;
	
	@Override
	public boolean apply(Context cx, String inPath, String outPath, int[] args) {
		mCurve = new Curve(cx, "curves/camera_liunian.dat");
		mLayerMul = Layer.getLayerImage(cx,
				"layers/camera_liunian_mul", Layer.Type.NORMAL, 40);
		mLayerlb = Layer.getLayerImage(cx,
				"layers/camera_liunian_lb", Layer.Type.NORMAL, 100);
		return super.apply(cx, inPath, outPath, args);
	}
	
	
	@Override
	protected void applyLine(Context cx, int[] pixels, int line, int height) {
		// TODO Auto-generated method stub
	    // 此时处理的是单行数据
		int w = pixels.length;
		int[] layerPixels;
		
		ImageProcessUtils.saturationPs(pixels, w, 1, -55);
		
		//
		CMTProcessor.curveEffect(pixels, mCurve.getCurveRed(), mCurve.getCurveGreen(), mCurve.getCurveBlue(), w, 1);
		// 
		layerPixels = getLayerPixels(mLayerMul, line, height);
		CMTProcessor.rsMultiplyEffect(pixels, layerPixels, w, 1, mLayerMul.getWidth(), 1);
		
		//		
		layerPixels = getLayerPixels(mLayerlb, line, height);
		CMTProcessor.rsLinearBurn(pixels, layerPixels, w, 1, mLayerlb.getWidth(), 1, 100);
	}

	@Override
	protected void releaseLayers() {
		// TODO Auto-generated method stub
		mLayerMul.recycle();
		mLayerlb.recycle();	
	}

}
