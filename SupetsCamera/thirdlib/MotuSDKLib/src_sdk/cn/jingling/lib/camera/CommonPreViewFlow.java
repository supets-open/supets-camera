package cn.jingling.lib.camera;

import java.io.IOException;

import cn.jingling.lib.livefilter.AbsCameraRender.OnFpsListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;

/**
 * 直接解析camera byte[]数据来做预览
 * @Copyright(C)2013,  Baidu.Tech.Co.Ltd. All rights reserved.
 * @Filename: CommonPreViewFlow.java Created On 2013-12-18
 * @Author:zhuchen
 * @Description:TODO
 * 
 * @Version:1.0
 * @Update:
 */
public class CommonPreViewFlow extends AbsPreViewFlow implements SurfaceHolder.Callback {

	private SurfaceHolder mCameraSurfaceHolder;
	
	public CommonPreViewFlow(Context context, CameraManager cameraManager) {
		super(context, cameraManager);
	}
	
	@Override
	public void initCameraView() {
		mCameraViewBean.cameraPreview.getHolder().addCallback(this);
		mCameraViewBean.cameraGLSurfaceView.createRender(false, true);
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
	public boolean switchCamera(Activity activity) {
		boolean isOpenSuc = super.switchCamera(activity);
		if(isOpenSuc) {
			startPreview(mCameraSurfaceHolder);
		}
		return isOpenSuc;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.mCameraSurfaceHolder = holder;
		initCameraAndGLView();
		startPreview(mCameraSurfaceHolder);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(mCameraSurfaceHolder != null){
			mCameraSurfaceHolder.getSurface().release();
			mCameraSurfaceHolder = null;
		}
	}
	
	private void startPreview(SurfaceHolder holder) {
		try {
			Camera camera = mCameraManager.getCamera();
			camera.setPreviewDisplay(holder);
			camera.startPreview();
			HDRHelper.clearExposure(camera);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initCameraGlSurfaceView() {
		// TODO Auto-generated method stub
		
	}
	
	protected void initCameraAndGLView() {
		Parameters params = mCamera.getParameters();
		mPreviewSize = params.getPreviewSize();

		// Be careful, please call the initRender(...) method to initialize
		// render
		// if the preview size has changed.
		mCameraViewBean.cameraGLSurfaceView.initCommonRender(mCameraViewBean.cameraGLSurfaceView.getWidth(),
				mCameraViewBean.cameraGLSurfaceView.getHeight(), mPreviewSize.width,
				mPreviewSize.height, CameraUtils.getGLRenderDirection(mCameraDisplayOrientation, isCurOpenFront), ImageFormat.NV21,
				isCurOpenFront);

		mCamera.addCallbackBuffer(new byte[mPreviewSize.width
				* mPreviewSize.height
				* ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8]);
		mCamera.setPreviewCallbackWithBuffer(new PreviewCallback() {
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				mCameraViewBean.cameraGLSurfaceView.setFrame(data);
				mCameraViewBean.cameraGLSurfaceView.requestRender();
				camera.addCallbackBuffer(data);
			}
		});
	}

	@Override
	public Camera getCamera() {
		return mCamera;
	}

}
