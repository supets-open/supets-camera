package cn.jingling.lib.filters.onekey;

import cn.jingling.lib.filters.ImageProcessUtils.Type;


public class CameraFoodFine extends CameraFoodFilter {

	public CameraFoodFine() {
		mLayerName = "layers/foodfine";
		mType = Type.LEFT_BOTTOM;
		mFraction = 0.75;
		mMarginFractionV = 0.07;
		mMarginFractionH = 0.05;
	}
}
