package cn.jingling.lib.camera;

import cn.jingling.lib.camera.CameraManager.ICameraControl;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.View;

public abstract class AbsPreViewFlow implements IPreViewFlow {

	protected final String TAG = this.getClass().getSimpleName();
	
	protected Camera mCamera;
	protected Context mContext;
	protected boolean isCurOpenFront; //当前选择的摄像头是否是前置的
	protected CameraManager mCameraManager;
	protected CameraViewBean mCameraViewBean;
	protected ICameraControl mCameraControl;
	
	protected Size mPreviewSize;
	protected int mDisplayRotation;
	protected int mDisplayOrientation;
	protected int mJpegRotation;
	protected int mCameraDisplayOrientation;
	
	private CameraOrientationListener mOrientationEventListener;// Orientation
	
	public AbsPreViewFlow(Context context, CameraManager cameraManager) {
		this.mContext = context.getApplicationContext();
		mOrientationEventListener = new CameraOrientationListener(mContext);
		this.mCameraManager = cameraManager;
		this.mCameraViewBean = mCameraManager.getCameraViewBean();
		this.mCameraControl = mCameraManager.getCameraControl();
	}
	
	@Override
	public void onResume(Activity activity) {
		if(!mCameraViewBean.isVisible()) return;
		retake(activity);
		mOrientationEventListener.enable();
		mCameraViewBean.onViewResume();
	}
	
	@Override
	public void onPause() {
		mOrientationEventListener.disable();
		
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		mCameraViewBean.onViewPause();
	}
	
	protected void setDisplayOrientation(Activity activity, int cameraId) {
        mDisplayRotation = CameraUtils.getDisplayRotation(activity);
        mDisplayOrientation = CameraUtils.getDisplayOrientation(mDisplayRotation, cameraId);
        mCameraDisplayOrientation = CameraUtils.getDisplayOrientation(0, cameraId);
    }
	
	protected void updatePreviewAndPictureSize() {
		/*Parameters params = mCamera.getParameters();
		Size previewSize = CameraUtils.getOptimalSize(
				params.getSupportedPreviewSizes(), 960 * 640);
		params.setPreviewSize(previewSize.width, previewSize.height);
		Size pictureSize = CameraUtils.getOptimalSize(
				params.getSupportedPictureSizes(), 1280 * 960);
		params.setPictureSize(pictureSize.width, pictureSize.height);
		mCamera.setParameters(params);*/
		mCameraControl.updatePreviewAndPictureSize(mCamera);
	}

	/**
	 * 打开相机,若第一次打开失败，会再尝试打开一次
	 * @param isFront 是否是前置摄像头
	 * @return 是否打开相机成功
	 */
	public boolean openCamera(Activity activity, boolean isFront) {
		boolean isOpen = _openCameraTwice(activity, isFront);
		if(isOpen) {
			initCameraView();
			initCameraGlSurfaceView();
		}
		return isOpen;
	}
	
	public boolean retake(Activity activity) {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		boolean openResult = _openCameraTwice(activity, isCurOpenFront);
		if(mCameraViewBean.cameraPreview != null){
			mCameraViewBean.cameraPreview.setVisibility(View.VISIBLE);
		}
		if(openResult) {
			initCameraGlSurfaceView();
		}
		return openResult;
	}
	
	@Override
	public boolean switchCamera(Activity activity) {
		boolean openResult = false;
		
		if(mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		if (isCurOpenFront) {
			openResult = _openCameraTwice(activity, false);
			isCurOpenFront = false;
		} else {
			openResult = _openCameraTwice(activity, true);
			isCurOpenFront = true;
		}
		if(openResult) {
			initCameraGlSurfaceView();
		}
		return openResult;
	}
	
	@Override
	public void takePictureImmediately(final ICameraControl cameraControl) {
		ShutterCallback scb = new ShutterCallback() {
			@Override
			public void onShutter() {
			}
		};
		// You must call Camera.setPreviewCallback(null) or
		// Camera.setPreviewCallbackWithBuffer(null) before take-picture
		// Otherwise, the Camera won't call PictureCallback.After take-picture,
		// you must call Camera.addCallbackBuffer
		// and Camera.setPreviewCallbackWithBuffer() to reinitialize the buffer
		// that the render of CameraGLSurfaceView uses.
		mCamera.setPreviewCallbackWithBuffer(null);
		mCamera.setPreviewCallback(null);
		updateRotation();
		// CameraUtils.setCameraPictureOrientation(mOrientationEventListener.getOrientation(),
		// mCameraId, mCamera);
		mCamera.takePicture(scb, null, null, new PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				if(cameraControl != null) {
					cameraControl.didTakePicture(data, camera);
				}
			}
		});
	}
	
	@Override
	public boolean isOpenFront() {
		return isCurOpenFront;
	}
	
	/**
	 * 若第一次打开相机失败，会再尝试开启一次
	 * @param activity
	 * @param isFront
	 * @return
	 */
	private boolean _openCameraTwice(Activity activity, boolean isFront) {
		boolean isOpen = _openCamera(activity, isFront);
		if(!isOpen) {
			isOpen = _openCamera(activity, isFront);
		}
		return isOpen;
	}
	
	private boolean _openCamera(Activity activity, boolean isFront) {
		int cameraId = CameraUtils.getCameraId(isFront);
		if (-1 != cameraId) {
			isCurOpenFront = isFront;
			mCamera = Camera.open(cameraId);
		} else {
			return false;
		}

		setDisplayOrientation(activity, cameraId);
		Log.d("xxxx", "mCameraDisplayOrientation = " + mCameraDisplayOrientation);
		mCamera.setDisplayOrientation(mCameraDisplayOrientation);
		updatePreviewAndPictureSize();
		return true;
	}
	
	private void updateRotation() {
		mJpegRotation = CameraUtils.getPictureRotation(mContext, isCurOpenFront,
				mOrientationEventListener.getOrientation(), mDisplayOrientation);
		Parameters params = mCamera.getParameters();
		params.setRotation(mJpegRotation);
		// params.setPictureFormat(ImageFormat.JPEG);
		mCamera.setParameters(params);
	}
	
	@Override
	public void free() {
		if(mCamera!=null){
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		mContext = null;
		mCameraControl = null;
		mCameraViewBean = null;
		mCameraManager = null;
	}
}
