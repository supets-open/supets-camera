package cn.jingling.lib.textbubble;

import android.content.res.TypedArray;

public class TextBubbleStyleParameter {

	public String mFileName;
	public int mTextColor;
	public float mLeftScale;
	public float mTopScale;
	public float mRightScale;
	public float mBottomScale;
	
	public float mShadowRadius;
	public float mShadowY;
	public float mShadowX;
	public int mShadowColor;
	
	
	public int position;
	

	public TextBubbleStyleParameter() {
	}

	
	public TextBubbleStyleParameter(TypedArray array) {
		mFileName = array.getString(0);
		mTextColor = (array.getColor(1, 0));

		mShadowColor = (array.getColor(2, 0));
		mShadowRadius = (array.getFloat(3, 0.0f));
		mShadowX = (array.getFloat(4, 0.0f));
		mShadowY = (array.getFloat(5, 0.0f));

		mLeftScale = (array.getFloat(6, 0.0f));
		mTopScale = (array.getFloat(7, 0.0f));
		mRightScale = (array.getFloat(8, 0.0f));
		mBottomScale = (array.getFloat(9, 0.0f));
	}

	
}
