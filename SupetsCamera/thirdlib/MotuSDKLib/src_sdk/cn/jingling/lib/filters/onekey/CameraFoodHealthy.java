package cn.jingling.lib.filters.onekey;

import cn.jingling.lib.filters.ImageProcessUtils.Type;


public class CameraFoodHealthy extends CameraFoodFilter {

	public CameraFoodHealthy() {
		mLayerName = "layers/foodhealthy";
		mType = Type.LEFT_TOP;
		mFraction = 0.4;
		mMarginFractionV = 0.05;
		
		mLayerName2 = "layers/foodhealthy2";
		mType2 = Type.LEFT_BOTTOM;
		mFraction2 = 0.7;
		mMarginFractionV2 = 0.07;

	}
}
