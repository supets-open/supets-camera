package cn.jingling.lib.livefilter;

import java.util.Map;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import cn.jingling.lib.livefilter.GLImageViewportHelper.ImageType;

public abstract class AbsCameraRender implements GLSurfaceView.Renderer,CameraRenderInteface {

	public interface OnFpsListener {
		public void onFpsUpdate(int value);
	}
	
	protected final static boolean DEBUG = true;
	protected Context mAppContext;
	
	protected OnFpsListener mOnFpsListener;
	protected int mFpsCount = 0;
	protected long mFpsTime = System.currentTimeMillis();
	protected Object mMutex = new Object();
	private boolean mTaskFinished = true;
	
	protected LiveFilterInfo mCurrentLiveFilter;
	protected Map<String, LiveFilterInfo> mLiveFilters;
	protected String mUpdateLabel;
	protected String mLastLable;//用于快速点击时，记录最后一次调用的label
	protected boolean mFilterUpdate = false;
	
	// 第一次绘制，DrawYUV时使用的原图宽高。它与mPreviewImageWidth的区别是考虑了Orientation信息。
	protected int mTextureWidth, mTextureHeight;
	protected int mViewWidth, mViewHeight;
	// 原图宽高。FBO的Texture宽高是根据mImageSize做nextPowerof2而得来的。
	protected int mPreviewImageWidth, mPreviewImageHeight;
	// 将Texture输出到屏幕时，使用的ViewPort
	protected ViewportRect mViewportRect;
	
	protected boolean mNeedUpdateViewPort = true;
	protected ImageType mImageType = ImageType.FIT_CENTER;
	
	public void refresh() {
		synchronized (mMutex) {
			mFilterUpdate = true;
		}
	}
	
	/** 设置显示方式，会在绘制下一帧数据时生效。
	 * @param imageType
	 */
	public void setImageType(ImageType imageType) {
		mImageType = imageType;
		mNeedUpdateViewPort = true;
	}
	
	/**
	 * 开启实时滤镜
	 * @param filterLabel 选择的滤镜标签
	 */
	public void setFilter(String filterLabel) {
		if (mCurrentLiveFilter != null
				&& filterLabel.equals(mCurrentLiveFilter.getLabel())) {
			return;
		}
		if (!mLiveFilters.containsKey(filterLabel)) {
			throw new RuntimeException("Filter label " + filterLabel
					+ " does not exsit in LiveFilterInfo!");
		}
		synchronized (mMutex) {
			mUpdateLabel = filterLabel;
			mFilterUpdate = true;
		}
	}
	
	/**
	 * 功能同{@link setFilter},不同之处在于针对layer会有一个异步create bitmap过程
	 * @param filterLabel
	 */
	public void setFilterAsync(final String filterLabel) {
		if (mCurrentLiveFilter != null
				&& filterLabel.equals(mCurrentLiveFilter.getLabel())) {
			return;
		}
		if (!mLiveFilters.containsKey(filterLabel)) {
			throw new RuntimeException("Filter label " + filterLabel
					+ " does not exsit in LiveFilterInfo!");
		}
		//非完全保证并发的处理方式，若此处对并发要求非常高，需开启个HanderThread线程来维护一个队列来保证并发问题
		synchronized (mMutex) {
			mLastLable = filterLabel;
//			LogUtils.d("xxxx", "mLastLable = " + mLastLable);
			if(!mTaskFinished) {
//				LogUtils.d("xxxx", "mTaskFinished" + mTaskFinished);
				return;
			}
//			LogUtils.d("xxxx", "mTaskFinished" + mTaskFinished);
			mTaskFinished = false;
			new AsyncTask<Void, Void, Void>() {
	
				@Override
				protected Void doInBackground(Void... params) {
					/*mUpdateLabel = filterLabel;
					glUpdateFilter();
					mFilterUpdate = true;*/
					
					_updateLable(filterLabel);
					mTaskFinished = true;
					return null;
				}
				
			}.execute();
		}
	}
	
	private void _updateLable(String filterLabel) {
		if (mAppContext == null) {
			return;
		}
		
		LiveFilterInfo liveFilterInfo = mLiveFilters.get(filterLabel);
		liveFilterInfo.prepareBmForTexture(mAppContext, new Point(mPreviewImageWidth,
				mPreviewImageHeight));
		mUpdateLabel = filterLabel;
//		LogUtils.d("xxxx", "_updateLable = " + filterLabel);
		
		if(mLastLable != null) {
			String tempLable = mLastLable;
			mLastLable = null;
			if(!filterLabel.equals(tempLable)) {
//				LogUtils.d("xxxx", "_updateLable mLastLable = " + tempLable);
				_updateLable(tempLable);
			}
		}
		refresh();
	}
	
	protected void glUpdateFilter() {
		if (mAppContext == null) {
			return;
		}
		
		if (mCurrentLiveFilter != null) {
			mCurrentLiveFilter.glRelease();
		}
		mCurrentLiveFilter = mLiveFilters.get(mUpdateLabel);
		mCurrentLiveFilter.glUpdate(mAppContext, new Point(mPreviewImageWidth,
				mPreviewImageHeight));
	}
	
	public void setOnFpsListener(OnFpsListener l) {
		mOnFpsListener = l;
	}
	
	public LiveFilterInfo getFilterInfo() {
		return mCurrentLiveFilter;
	}
	
	protected void markTime() {
		long t = System.currentTimeMillis();
		double interval = (t - mFpsTime) / 1000.0;
		if (mOnFpsListener != null) {
			mOnFpsListener.onFpsUpdate((int) (FPS_INTERVAL / interval));
		}
		mFpsTime = t;
	}
}
