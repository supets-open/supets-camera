package cn.jingling.lib.filters.onekey;

import cn.jingling.lib.filters.CMTProcessor;

public class SingleColorRed extends SingleColor{

	@Override
	public void singleColorEffect(int[] pixels, int w, int h, float[] matrix) {
		this.statisticEvent();
		CMTProcessor.singleColorEffect(pixels, w, h, matrix, 1.0f,0.0f,0.0f,0.25f,0.35f);
	}

}
