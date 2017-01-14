package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.ImageProcessUtils.Type;
import cn.jingling.lib.filters.Layer;
import cn.jingling.lib.filters.OneKeyFilter;

public class CameraFoodFilter extends OneKeyFilter {

	protected String mLayerName;
	protected Type mType;
	protected double mFraction;
	protected double mMarginFractionH = 0;
	protected double mMarginFractionV = 0;
	
	protected String mLayerName2;
	protected Type mType2;
	protected double mFraction2;
	protected double mMarginFractionH2 = 0;
	protected double mMarginFractionV2 = 0;
	
	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		int w = bm.getWidth();
		int h = bm.getHeight();
		Bitmap layer = Layer.getLayerImage(cx, "layers/foodmask", Layer.Type.NORMAL);
		ImageProcessUtils.mergeBitmapInPlace(bm, layer, 0, ImageProcessUtils.Type.STRENCH);
		layer.recycle();
		int m = Math.min(w, h);
		layer = Layer.getLayerImage(cx, mLayerName, (int)(m * mFraction), Layer.Type.NORMAL);
		ImageProcessUtils.mergeBitmapInPlace(bm, layer, 0, mType, mMarginFractionH, mMarginFractionV);
		layer.recycle();
		if (!TextUtils.isEmpty(mLayerName2)) {
			layer = Layer.getLayerImage(cx, mLayerName2, (int)(m * mFraction2), Layer.Type.NORMAL);
			ImageProcessUtils.mergeBitmapInPlace(bm, layer, 0, mType2, mMarginFractionH2, mMarginFractionV2);
			layer.recycle();
		}
		return bm;
	}
}