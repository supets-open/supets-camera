package cn.jingling.lib.filters.onekey;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.CMTProcessor;
import cn.jingling.lib.filters.Curve;
import cn.jingling.lib.filters.OneKeyFilter;

public class CurveFilter extends OneKeyFilter {

	protected String mPath = null;
	protected int[] mPixels;
	protected int mWidth;
	protected int mHeight;

	protected void initial(Bitmap bitmap) {
		mWidth = bitmap.getWidth();
		mHeight = bitmap.getHeight();
		mPixels = new int[mWidth * mHeight];
		bitmap.getPixels(mPixels, 0, mWidth, 0, 0, mWidth, mHeight);

	}

	protected void curvePixels(Context cx) {
		Curve curve = new Curve(cx, mPath);
		CMTProcessor.curveEffect(mPixels, curve.getCurveRed(),
				curve.getCurveGreen(), curve.getCurveBlue(), mWidth, mHeight);
	}

	protected void setPicxels(Bitmap bitmap) {
		bitmap.setPixels(mPixels, 0, mWidth, 0, 0, mWidth, mHeight);
		mPixels = null;
	}

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		initial(bm);
		curvePixels(cx);
		setPicxels(bm);
		return bm;
	}

}