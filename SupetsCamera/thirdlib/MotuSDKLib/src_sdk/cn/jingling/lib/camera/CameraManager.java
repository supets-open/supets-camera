package cn.jingling.lib.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.os.Build;

/**
 * 相机逻辑流程控制类
 * @Copyright(C)2013,  Baidu.Tech.Co.Ltd. All rights reserved.
 * @Filename: CameraManager.java Created On 2013-12-13
 * @Author:zhuchen
 * @Description:TODO
 * 
 * @Version:1.0
 * @Update:
 */
public class CameraManager {

	private Context mContext;
	private IPreViewFlow mIPreViewFlow;
	private CameraViewBean mCameraViewBean;
	private ICameraControl mCameraControl;
	
	public CameraManager(Activity activity, CameraViewBean cameraViewBean, ICameraControl cameraControl) {
		this.mCameraViewBean = cameraViewBean;
		this.mContext = activity.getApplicationContext();
		this.mCameraControl = cameraControl;
		if(CameraManager.supportSurfaceTexture()) {
			mIPreViewFlow = new STPreViewFlow(mContext, this);
		} else {
			mIPreViewFlow = new CommonPreViewFlow(mContext, this);
		}
//		initCameraView();
	}
	
	public void onResume(Activity activity) {
		mIPreViewFlow.onResume(activity);
	}
	
	public void onPause() {
		mIPreViewFlow.onPause();
	}
	
	public boolean openCamera(Activity activity, boolean isFront) {
		return mIPreViewFlow.openCamera(activity, isFront);
	}
	
	public boolean retake(Activity activity) {
		return mIPreViewFlow.retake(activity);
	}
	
	/*private void initCameraView() {
		mIPreViewFlow.initCameraView();
	}*/
	
	public void setFilter(String filterLabel) {
		mCameraViewBean.cameraGLSurfaceView.setFilter(filterLabel);
	}
	
	public final void autoFocus(AutoFocusCallback cb) {
		mIPreViewFlow.getCamera().autoFocus(cb);
	}
	
	public boolean switchCamera(Activity activity) {
		return mIPreViewFlow.switchCamera(activity);
	}
	
	public void takePictureImmediately() {
		mIPreViewFlow.takePictureImmediately(mCameraControl);
	}
	
	public Camera getCamera() {
		return mIPreViewFlow.getCamera();
	}
	
	public boolean isCurOpenFront() {
		return mIPreViewFlow.isOpenFront();
	}
	
	public CameraViewBean getCameraViewBean() {
		return mCameraViewBean;
	}
	
	public ICameraControl getCameraControl() {
		return mCameraControl;
	}
	
	public void setCameraControl(ICameraControl control) {
		this.mCameraControl = control;
	}
	
	public interface ICameraControl {
		
		/**
		 * 相机拍照后获取数据时，会回调此函数，
		 * @param data
		 * @param camera
		 */
		public void didTakePicture(byte[] data, Camera camera);
		
		public void onFpsUpdate(int value);
		
		/**
		 * 设置camera预览数据大小和拍照保存后的数据大小
		 */
		public void updatePreviewAndPictureSize(Camera camera);
	}
	
	public void freee() {
		mContext = null;
		mCameraViewBean = null;
		mCameraControl = null;
		if(mIPreViewFlow != null) {
			mIPreViewFlow.free();
		}
		mIPreViewFlow = null;
	}
	
	/**
	 * 对于这样的策略函数，希望在下次结构大调整时，把超级相机里的CameraAttrs放在SDK里，同时就不需要在此处定义该函数了
	 * @return
	 */
	public static boolean supportSurfaceTexture() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return true;
		} else {
			return false;
		}
	}
}
