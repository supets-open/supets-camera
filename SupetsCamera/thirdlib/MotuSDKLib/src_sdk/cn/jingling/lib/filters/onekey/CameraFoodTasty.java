package cn.jingling.lib.filters.onekey;

import cn.jingling.lib.filters.ImageProcessUtils.Type;


public class CameraFoodTasty extends CameraFoodFilter {

	public CameraFoodTasty() {
		mLayerName = "layers/foodtasty";
		mType = Type.LEFT_TOP;
		mFraction = 0.5;
		
		mLayerName2 = "layers/foodtasty2";
		mType2 = Type.BOTTOM;
		mFraction2 = 0.6;
		mMarginFractionV2 = 0.1;
	}
}
