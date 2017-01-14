package cn.jingling.lib.camera;

import java.io.IOException;

import cn.jingling.lib.livefilter.AbsCameraRender.OnFpsListener;
import cn.jingling.lib.livefilter.CameraSTRenderer;
import cn.jingling.lib.utils.LogUtils;
import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

/**
 * 通过SurfaceTexture绑定数据的预览方式
 * @Copyright(C)2013,  Baidu.Tech.Co.Ltd. All rights reserved.
 * @Filename: STPreViewFlow.java Created On 2013-12-18
 * @Author:zhuchen
 * @Description:TODO
 * 
 * @Version:1.0
 * @Update:
 */
public class STPreViewFlow extends AbsPreViewFlow {

	private RendererObserver mObserverRenderer = new RendererObserver();
	
	public STPreViewFlow(Context context, CameraManager cameraManager) {
		super(context, cameraManager);
	}
	
	@Override
	public void initCameraView() {
		mCameraViewBean.cameraGLSurfaceView.createRender(true, true);
		mCameraViewBean.cameraGLSurfaceView.setOnFpsListener(new OnFpsListener() {

			@Override
			public void onFpsUpdate(int value) {
				if(mCameraControl != null) {
					mCameraControl.onFpsUpdate(value);
				}
			}
		});
	}

	@Override
	public boolean retake(Activity activity) {
		boolean openResult = super.retake(activity);
		/*if(mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		boolean openResult = openCamera(activity, isCurOpenFront);
		LogUtils.d(TAG, "retake open camera " + openResult);
		initCameraGlSurfaceView();*/
		try {
			cameraBindtexture();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return openResult;
	}
	
	@Override
	public boolean openCamera(Activity activity, boolean isFront) {
		boolean isOpenSuc = super.openCamera(activity, isFront);
		if(isOpenSuc) {
			mCameraViewBean.cameraGLSurfaceView.setObserver(mObserverRenderer);
		}
		return isOpenSuc;
	}
	
	@Override
	public boolean switchCamera(Activity activity) {
//		mCameraViewBean.cameraGLSurfaceView.onPause();
		boolean isOpenSuc = super.switchCamera(activity);
		if(isOpenSuc) {
			/*try {
				cameraBindtexture();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		}
		return isOpenSuc;
	}

	@Override
	public void initCameraGlSurfaceView() {
		Parameters params = mCameraManager.getCamera().getParameters();
		mPreviewSize = params.getPreviewSize();
		mCameraViewBean.cameraGLSurfaceView.initSTRender(mPreviewSize.width, mPreviewSize.height, 
				CameraUtils.getGLRenderDirection(mCameraDisplayOrientation, isCurOpenFront)
				, mCameraManager.isCurOpenFront());
	}
	
	private class RendererObserver implements CameraSTRenderer.Observer {
		@Override
		public void onSurfaceTextureCreated(SurfaceTexture surfaceTexture) {
			// Once we have SurfaceTexture try setting it to Camera.
			try {
				Camera camera = mCameraManager.getCamera();
				if(camera != null) {
					camera.stopPreview();
					camera.setPreviewTexture(surfaceTexture);
					camera.startPreview();
				} else {
					LogUtils.e("xxxx", "warn!!!onSurfaceTextureCreated camera == null!!!");
				}
			} catch (final Exception ex) {
				LogUtils.e("xxxx", ex.toString() + "msg = " + ex.getMessage());
			}
		}
	}
	
	private void cameraBindtexture() throws IOException {
		if(mCameraViewBean.cameraGLSurfaceView.getSurfaceTexture() != null) {
			mCamera.setPreviewTexture(mCameraViewBean.cameraGLSurfaceView.getSurfaceTexture());
			mCamera.startPreview();
		}
	}

	@Override
	public Camera getCamera() {
		// TODO Auto-generated method stub
		return mCamera;
	}
	
	@Override
	public void free() {
		super.free();
		mObserverRenderer = null;
	}
	
	/*private boolean _openCamera(Activity activity, boolean isFront) {
		int cameraId = getCameraId(isFront);
		if (-1 != cameraId) {
			mCamera = Camera.open(cameraId);
		} else {
			return false;
		}

		setDisplayOrientation(activity, cameraId);
		Log.d("xxxx", "mCameraDisplayOrientation = " + mCameraDisplayOrientation);
		mCamera.setDisplayOrientation(mCameraDisplayOrientation);
		updatePreviewAndPictureSize();
		return true;
	}*/

}
