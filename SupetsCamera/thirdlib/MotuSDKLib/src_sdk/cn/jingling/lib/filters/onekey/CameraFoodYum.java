package cn.jingling.lib.filters.onekey;

import cn.jingling.lib.filters.ImageProcessUtils.Type;


public class CameraFoodYum extends CameraFoodFilter {

	public CameraFoodYum() {
		mLayerName = "layers/foodyum";
		mType = Type.BOTTOM;
		mFraction = 0.8;
		mLayerName2 = null;
		mMarginFractionV = 0.10;
	}
}
