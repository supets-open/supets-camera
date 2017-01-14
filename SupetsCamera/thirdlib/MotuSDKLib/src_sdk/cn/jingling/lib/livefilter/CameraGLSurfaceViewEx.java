package cn.jingling.lib.livefilter;

import cn.jingling.lib.livefilter.AbsCameraRender.OnFpsListener;
import cn.jingling.lib.livefilter.CameraSTRenderer.Observer;
import cn.jingling.lib.livefilter.GLImageViewportHelper.ImageType;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * 对{@link CameraGLSurfaceView}进行增强，当android api>=11时，使用新的预览方式(surfaceTexture)
 * @Copyright(C)2013,  Baidu.Tech.Co.Ltd. All rights reserved.
 * @Filename: CameraGLSurfaceViewEx.java Created On 2013-12-17
 * @Author:zhuchen
 * @Description:TODO
 * 
 * @Version:1.0
 * @Update:
 */
public class CameraGLSurfaceViewEx extends GLSurfaceView {

	private Context mContext;
	private AbsCameraRender mCameraRenderer;
	
	private boolean isUseSTRender = false;
	
	private final static boolean DEBUG = false;
	private final String TAG = getClass().getSimpleName();
	
	public CameraGLSurfaceViewEx(Context context) {
		super(context);
		init(context);
	}
	
	public CameraGLSurfaceViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context) {
		mContext = context;
		setEGLContextClientVersion(2);
		setEGLConfigChooser(8, 8, 8, 8, 0, 0);
	}
	
	@Override
	public void onResume() {
		if (null != mCameraRenderer) {
			mCameraRenderer.refresh();
		}
		super.onResume();
	}

	@Override
	public void setRenderer(Renderer renderer) {
		super.setRenderer(renderer);
		setRenderMode(RENDERMODE_WHEN_DIRTY);
		getHolder().setFormat(1);
	}
	
	/**
	 * 默认使用{@link CameraRenderer}来做渲染
	 * @param smooth 是否开启磨皮的实时滤镜
	 */
	@Deprecated
	public void createRender(boolean smooth){
		this.createRender(false, smooth);
	}
	
	/**
	 * 
	 * @param isUseSTRender 是否使用{@link CameraSTRenderer}，一种效果更好的预览方式,但SDK API>=11
	 * @param smooth 是否开启磨皮的实时滤镜
	 */
	public void createRender(boolean isUseSTRender, boolean smooth){
		this.isUseSTRender = isUseSTRender;
		if(isUseSTRender) {
			mCameraRenderer = new CameraSTRenderer(mContext, this, smooth);
		} else {
			mCameraRenderer = new CameraRenderer(mContext, smooth);
		}
		setRenderer(mCameraRenderer);
	}
	
	public void setOnFpsListener(OnFpsListener l){
		mCameraRenderer.setOnFpsListener(l);
	}
	
	public void initSTRender(int previewWidth, int previewHeight, int direction, boolean flip){
		initSTRender(previewWidth, previewHeight, direction, flip, ImageType.FIT_CENTER);
	}
	
	public void initSTRender(int previewWidth, int previewHeight, int direction, boolean flip, ImageType imageType){
		((CameraSTRenderer)mCameraRenderer).init(previewWidth, previewHeight, direction, flip, imageType);
		onPause();
		onResume();
	}
	
	/**
	 * 调用此函数，默认预览是全屏方式显示
	 */
	public void initCommonRender(int viewWidth, int viewHeight,
			int previewWidth, int previewHeight, 
			int direction, int format, boolean flip) {
		initCommonRender(viewWidth, viewHeight, previewWidth, previewHeight, direction, format, flip, ImageType.FIT_CENTER);
	}
	

	public void initCommonRender(int viewWidth, int viewHeight,
			int previewWidth, int previewHeight, 
			int direction, int format, boolean flip, ImageType type){
		((CameraRenderer)mCameraRenderer).init(viewWidth, viewHeight, previewWidth, previewHeight, direction, format, flip, type);
		onPause();
		onResume();
	}
	
	private void resetViewSize(int viewWidth, int viewHeight) {
		//CameraSTRenderer这种下需要
		if(mCameraRenderer != null && isUseSTRender) {
			((CameraSTRenderer)mCameraRenderer).resetViewSize(viewWidth, viewHeight);
		}
	}
	
	/** 设置显示方式，会在绘制下一帧数据时生效。
	 * @param imageType
	 */
	public void setImageType(ImageType imageType) {
		mCameraRenderer.setImageType(imageType);
	}
	
	public void setFilter(String filterLabel){
		if(DEBUG) {
			Log.d("xxxx", "LiveFilter: " + filterLabel);
		}
		mCameraRenderer.setFilterAsync(filterLabel);
	}
	
	public LiveFilterInfo getFitlerInfo() {
		return mCameraRenderer.getFilterInfo();
	}
	
	public SurfaceTexture getSurfaceTexture() {
		return ((CameraSTRenderer)mCameraRenderer).getSurfaceTexture();
	}
	
	public void setObserver(Observer observer) {
		((CameraSTRenderer)mCameraRenderer).setObserver(observer);
	}
	
	public void setFrame(byte[] frame){
		((CameraRenderer)mCameraRenderer).setFrame(frame);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);
		if(DEBUG) {
			Log.d(TAG, "surfaceCreated");
			Log.d(TAG, "getWidth = " + getWidth() + ", getHeight = " + getHeight());
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		super.surfaceChanged(holder, format, w, h);
		if(DEBUG) {
			Log.d(TAG, "surfaceChanged");
		}
		resetViewSize(getWidth(), getHeight());
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		super.surfaceDestroyed(holder);
		if(DEBUG) {
			Log.d(TAG, "surfaceDestroyed");
		}
	}
}
