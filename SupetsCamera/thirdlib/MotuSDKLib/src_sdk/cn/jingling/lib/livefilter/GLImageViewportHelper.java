package cn.jingling.lib.livefilter;


public class GLImageViewportHelper {
	
	public static ViewportRect getGLViewPort(int mImageWidth, int mImageHeight, int mViewWidth, int mViewHeight, ImageType type) {
		
		ViewportRect mViewportRect = new ViewportRect(0,0,0,0);
		
		if (mViewWidth == 0 || mViewHeight == 0 || mImageWidth == 0
				|| mImageHeight == 0) {
			return mViewportRect;
		}
		
		switch (type) {
		case FIT_CENTER:
			mViewportRect = getGLViewPortFitCenter(mImageWidth, mImageHeight, mViewWidth, mViewHeight);
			break;
		case CENTER_CROP:
			mViewportRect = getGLViewPortCenterCrop(mImageWidth, mImageHeight, mViewWidth, mViewHeight);
			break;
		case FIT_TOP:
			mViewportRect = getGLViewPortFitTop(mImageWidth, mImageHeight, mViewWidth, mViewHeight);
			break;
		case FIT_BOTTOM:
			mViewportRect = getGLViewPortFitBottom(mImageWidth, mImageHeight, mViewWidth, mViewHeight);
			break;

		default:
			break;
		}
		
		return mViewportRect;
	}
	

	private static ViewportRect getGLViewPortFitCenter(int mImageWidth, int mImageHeight, int mViewWidth, int mViewHeight) {
		
		
		float scaleRatio = 1.0f;
		float traslatex = 0;
		float traslatey = 0;
		
		
		float scaleRatioH = (float)mViewHeight/(float)mImageHeight;
		float scaleRatioW = (float)mViewWidth/(float)mImageWidth;
		
		
		if (scaleRatioH > scaleRatioW) {
			scaleRatio = scaleRatioW;
			traslatex = 0;
			traslatey = (mViewHeight - (float)mImageHeight*scaleRatio)/2.0f;	
		} else {
			scaleRatio = scaleRatioH;
			traslatey = 0;
			traslatex = (mViewWidth - (float)mImageWidth*scaleRatio)/2.0f;	
		}
		
		int x = (int) traslatex;
		int y = (int) traslatey;
		int width = (int) (scaleRatio * mImageWidth);
		int height = (int) (scaleRatio * mImageHeight);
		
		return new ViewportRect(x, y, width, height);

	}
	
	private static ViewportRect getGLViewPortCenterCrop(int mImageWidth, int mImageHeight, int mViewWidth, int mViewHeight) {
		
		
		float scaleRatio = 1.0f;
		float traslatex = 0;
		float traslatey = 0;
		
		
		float scaleRatioH = (float)mViewHeight/(float)mImageHeight;
		float scaleRatioW = (float)mViewWidth/(float)mImageWidth;
		
		
		if (scaleRatioW > scaleRatioH) {
			scaleRatio = scaleRatioW;
			traslatex = 0;
			traslatey = (mViewHeight - (float)mImageHeight*scaleRatio)/2.0f;	
		} else {
			scaleRatio = scaleRatioH;
			traslatey = 0;
			traslatex = (mViewWidth - (float)mImageWidth*scaleRatio)/2.0f;	
		}
		
		int x = (int) traslatex;
		int y = (int) traslatey;
		int width = (int) (scaleRatio * mImageWidth);
		int height = (int) (scaleRatio * mImageHeight);
		
		return new ViewportRect(x, y, width, height);

	}
	
	private static ViewportRect getGLViewPortFitTop(int mImageWidth, int mImageHeight, int mViewWidth, int mViewHeight) {
		
		
		float scaleRatio = 1.0f;
		float traslatex = 0;
		float traslatey = 0;
		
		scaleRatio = (float)mViewWidth/(float)mImageWidth;
		traslatey = (float)mViewHeight - (float)mImageHeight*scaleRatio;
		
		int x = (int) traslatex;
		int y = (int) traslatey;
		int width = (int) (scaleRatio * mImageWidth);
		int height = (int) (scaleRatio * mImageHeight);
		
		return new ViewportRect(x, y, width, height);

	}
	
	private static ViewportRect getGLViewPortFitBottom(int mImageWidth, int mImageHeight, int mViewWidth, int mViewHeight) {
		
		
		float scaleRatio = 1.0f;
		float traslatex = 0;
		float traslatey = 0;
		
		scaleRatio = (float)mViewWidth/(float)mImageWidth;
		
		int x = (int) traslatex;
		int y = (int) traslatey;
		int width = (int) (scaleRatio * mImageWidth);
		int height = (int) (scaleRatio * mImageHeight);
		
		return new ViewportRect(x, y, width, height);

	}
	
	
	public enum ImageType {
		FIT_CENTER, CENTER_CROP, FIT_TOP, FIT_BOTTOM
	}
	

}
