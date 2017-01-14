package cn.jingling.lib.livefilter;

import cn.jingling.lib.livefilter.AbsCameraRender.OnFpsListener;
import cn.jingling.lib.livefilter.GLImageViewportHelper.ImageType;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class CameraGLSurfaceView extends GLSurfaceView {

	private Context mContext;
	private CameraRenderer mRender;
	
	public CameraGLSurfaceView(Context context) {
		super(context);
		mContext = context;
		// TODO Auto-generated constructor stub
		setEGLContextClientVersion(2);
	}
	
	public CameraGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		// TODO Auto-generated constructor stub
		setEGLContextClientVersion(2);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		if (null != mRender) {
			mRender.refresh();
		}
		super.onResume();
	}

	@Override
	public void setRenderer(Renderer renderer) {
		// TODO Auto-generated method stub
		super.setRenderer(renderer);
		setRenderMode(RENDERMODE_WHEN_DIRTY);
	}
	
	public void createRender(boolean smooth){
		mRender = new CameraRenderer(mContext, smooth);
		setRenderer(mRender);
	}
	
	public void setOnFpsListener(OnFpsListener l){
		mRender.setOnFpsListener(l);
	}
	
	/**
	 * 调用此函数，默认预览是全屏方式显示
	 * @param viewWidth
	 * @param viewHeight
	 * @param previewWidth
	 * @param previewHeight
	 * @param direction
	 * @param format
	 * @param flip
	 */
	public void initRender(int viewWidth, int viewHeight,
			int previewWidth, int previewHeight, 
			int direction, int format, boolean flip) {
		initRender(viewWidth, viewHeight, previewWidth, previewHeight, direction, format, flip, ImageType.FIT_CENTER);
	}
	
	/**
	 * 
	 * @param viewWidth
	 * @param viewHeight
	 * @param previewWidth
	 * @param previewHeight
	 * @param direction
	 * @param format
	 * @param flip
	 * @param isFullScreen 相机预览图是否全屏显示
	 */
	public void initRender(int viewWidth, int viewHeight,
			int previewWidth, int previewHeight, 
			int direction, int format, boolean flip, ImageType type){
		mRender.init(viewWidth, viewHeight, previewWidth, previewHeight, direction, format, flip, type);
		onPause();
		onResume();
	}
	
	/** 设置显示方式，会在绘制下一帧数据时生效。
	 * @param imageType
	 */
	public void setImageType(ImageType imageType) {
		mRender.setImageType(imageType);
	}
	
	public void setFilter(String filterLabel){
		mRender.setFilterAsync(filterLabel);
	}
	
	public LiveFilterInfo getFitlerInfo() {
		return mRender.getFilterInfo();
	}
	
	public void setFrame(byte[] frame){
		mRender.setFrame(frame);
	}
	
	
}
