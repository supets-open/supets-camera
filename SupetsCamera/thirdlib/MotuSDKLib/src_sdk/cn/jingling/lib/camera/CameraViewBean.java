package cn.jingling.lib.camera;

import android.view.View;
import cn.jingling.lib.livefilter.CameraGLSurfaceViewEx;
import cn.jingling.lib.livefilter.CameraPreview;

/**
 * 相机预览需要用的控件
 * @Copyright(C)2013,  Baidu.Tech.Co.Ltd. All rights reserved.
 * @Filename: CameraViewBean.java Created On 2013-12-13
 * @Author:zhuchen
 * @Description:TODO
 * 
 * @Version:1.0
 * @Update:
 */
public class CameraViewBean {

	public CameraPreview cameraPreview;
	public CameraGLSurfaceViewEx cameraGLSurfaceView;
	
	public CameraViewBean(CameraPreview cameraPreview, CameraGLSurfaceViewEx cameraGLSurfaceView) {
		this.cameraPreview = cameraPreview;
		this.cameraGLSurfaceView = cameraGLSurfaceView;
	}
	
	public void onViewResume() {
		if(cameraPreview != null && cameraPreview.getVisibility()
				!= View.VISIBLE){
			cameraPreview.setVisibility(View.VISIBLE);
		}
		
		if(cameraGLSurfaceView != null){
			if(cameraGLSurfaceView.getVisibility() != View.VISIBLE){
				cameraGLSurfaceView.setVisibility(View.VISIBLE);
			}
			cameraGLSurfaceView.onResume();
		}
	}
	
	public boolean isVisible() {
		boolean visible = false;
		/*if(cameraPreview != null) {
			if(cameraPreview.getVisibility() == View.VISIBLE) {
				visible = true;
			}
		} else {
			visible = true;
		}
		
		if(cameraGLSurfaceView != null) {
			if(cameraGLSurfaceView.getVisibility() == View.VISIBLE) {
				visible &= true;
			}
		} else {
			visible &= false;
		}*/
		
		if(cameraGLSurfaceView != null) {
			if(cameraGLSurfaceView.getVisibility() == View.VISIBLE) {
				visible = true;
			}
		} else {
			visible = false;
		}
		return visible;
	}

	public void onViewPause() {
		if(cameraPreview != null){
			cameraPreview.setVisibility(View.GONE);
		}
		
		if(cameraGLSurfaceView != null){
			cameraGLSurfaceView.onPause();
		}
	}
	
	/*public CameraPreview getCameraPreview() {
		return mCameraPreview;
	}

	public void setCameraPreview(CameraPreview cameraPreview) {
		this.mCameraPreview = cameraPreview;
	}

	public CameraGLSurfaceView getCameraGLSurfaceView() {
		return mCameraGLSurfaceView;
	}

	public void setCameraGLSurfaceView(CameraGLSurfaceView cameraGLSurfaceView) {
		this.mCameraGLSurfaceView = cameraGLSurfaceView;
	}*/
}
