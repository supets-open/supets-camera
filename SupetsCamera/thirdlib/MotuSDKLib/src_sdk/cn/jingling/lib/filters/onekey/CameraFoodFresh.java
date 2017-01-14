package cn.jingling.lib.filters.onekey;

import cn.jingling.lib.filters.ImageProcessUtils.Type;


public class CameraFoodFresh extends CameraFoodFilter {

	public CameraFoodFresh() {
		mLayerName = "layers/foodfresh";
		mType = Type.BOTTOM;
		mFraction = 0.8;
		mMarginFractionV = 0.13;
	}
}
