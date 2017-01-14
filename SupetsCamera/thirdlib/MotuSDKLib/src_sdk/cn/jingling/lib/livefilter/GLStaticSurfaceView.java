package cn.jingling.lib.livefilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import cn.jingling.lib.PackageSecurity;
import cn.jingling.lib.livefilter.GLImageViewportHelper.ImageType;

public class GLStaticSurfaceView extends GLSurfaceView {

	
	private Context mContext;
	
	private GLStaticSurfaceViewRender mGLStaticSurfaceViewRender;
	
	public GLStaticSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		PackageSecurity.check(context);
		setEGLContextClientVersion(2);
		mContext = context;
	}

	public GLStaticSurfaceView(Context context) {
		super(context);
		PackageSecurity.check(context);
		setEGLContextClientVersion(2);
		mContext = context;
	}
	
	/** 初始化GLStaticSurfaceView及其render。
	 * 
	 */
	public void initRenderer() {
		mGLStaticSurfaceViewRender = new GLStaticSurfaceViewRender();
		mGLStaticSurfaceViewRender.initRender(mContext);
		this.setRenderer(mGLStaticSurfaceViewRender);
		setRenderMode(RENDERMODE_WHEN_DIRTY);
	}
	
	/** 释放资源。调用release()后，下次使用时需再次init()。会在下一次requestRender时生效。
	 * 
	 */
	public void releaseRender() {
		mGLStaticSurfaceViewRender.releaseRender();
		this.requestRender();
		mContext = null;
	}
	
	/** 切换图片时调用。根据图片重新设置openGL相关参数。会在下一次requestRender时生效。
	 * @param originalBm
	 */
	public void setBitmap(Bitmap originalBm) {
		mGLStaticSurfaceViewRender.setBitmap(originalBm);
	}
	
    /** 设置所使用的FilterLabel。会在下一次requestRender时生效。
     * @param filterLabel
     */
    public void setFilter(String filterLabel) {
    	mGLStaticSurfaceViewRender.setFilter(filterLabel);
	}
    
    /** 设置显示方式。会在下一次requestRender时生效。
     * @param type
     */
    public void setImageType(ImageType type) {
    	mGLStaticSurfaceViewRender.setImageType(type);
   	}

}
