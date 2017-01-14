package cn.jingling.lib.camera;

import cn.jingling.lib.camera.CameraManager.ICameraControl;
import android.app.Activity;
import android.hardware.Camera;

/**
 * 相机调用预览流程接口
 * @Copyright(C)2013,  Baidu.Tech.Co.Ltd. All rights reserved.
 * @Filename: ICameraFlow.java Created On 2013-12-13
 * @Author:zhuchen
 * @Description:TODO
 * 
 * @Version:1.0
 * @Update:
 */
public interface IPreViewFlow {

	/**
	 * 初始化相关相机预览需要用的view
	 */
	public void initCameraView();
	
	/**
	 * 打开相机
	 * @param activity
	 * @param isFront
	 * @return
	 */
	public boolean openCamera(Activity activity, boolean isFront);
	
	/**
	 * 初始化GlSurfaceView相关
	 */
	public void initCameraGlSurfaceView();
	
	/**
	 * 
	 * @param activity
	 */
	public void onResume(Activity activity);
	
	/**
	 * 
	 */
	public void onPause();
	
	/**
	 * 
	 * @param activity
	 * @return
	 */
	public boolean retake(Activity activity);
	
	/**
	 * get camera device
	 * @return
	 */
	public Camera getCamera();
	
	/**
	 * 切换相机
	 * @return
	 */
	public boolean switchCamera(Activity activity);
	
	/**
	 * 打开的相机是否是前置相机摄像头
	 * @return
	 */
	public boolean isOpenFront();
	
	/**
	 * 立即拍照
	 * @param cameraControl
	 */
	public void takePictureImmediately(ICameraControl cameraControl);
	
	/**
	 * 释放资源
	 */
	public void free();
}
