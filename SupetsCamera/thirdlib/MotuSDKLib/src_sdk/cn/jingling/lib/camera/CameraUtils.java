package cn.jingling.lib.camera;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.view.Surface;
import cn.jingling.lib.utils.LogUtils;

public class CameraUtils {
	
	public static Size getOptimalSize(List<Size> list, int pixelNum) {
		Size ret = list.get(0);
		int delta = Integer.MAX_VALUE;
		for (Size size : list) {
			if (Math.abs((double) size.width / size.height - 4.0 / 3) > 0.01) {
				continue;
			}
			int diff = Math.abs(size.width * size.height - pixelNum);
			if (diff < delta) {
				ret = size;
				delta = diff;
			}
		}
		return ret;
	}
	
	/**
	 * This method will be useless when using CameraGLSurfaceView to display camera preview
	 * @param activity
	 * @param cameraId
	 * @param camera
	 */
	public static void setCameraDisplayOrientation(Activity activity,
			int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}

		camera.setDisplayOrientation(result);
	}

	/**
	 * 
	 * @param screenOrientation value from CameraOrientationListener.getOrientation
	 * @param cameraId
	 * @param camera
	 */
	public static void setCameraPictureOrientation(int screenOrientation, int cameraId, Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int orientation = screenOrientation * 90;
		int rotation = 0;
		if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
			rotation = (info.orientation - orientation + 360) % 360;
		} else { // back-facing camera
			rotation = (info.orientation + orientation) % 360;
		}
		Parameters params = camera.getParameters();
		params.setRotation(rotation);
		camera.setParameters(params);

	}
	
	/**
	 * Get the direction of the camera render that GLSurfaceView uses.
	 * 
	 * @return
	 */
	public static int getGLRenderDirection(int cameraDisplayOrientation, boolean isFrontCamera) {
		int direction = 0;
		switch (cameraDisplayOrientation) {
		case 0:
			direction = isFrontCamera ? 3 : 3;
			break;
		case 90:
			direction = isFrontCamera ? 2 : 0;
			break;
		case 180:
			direction = isFrontCamera ? 1 : 1;
			break;
		case 270:
			direction = isFrontCamera ? 0 : 2;
			break;
		}

		return direction;
	}
	
	public static int getCameraId(boolean front) {
		int numberOfCameras = android.hardware.Camera.getNumberOfCameras();
		CameraInfo[] cameraInfo = new CameraInfo[numberOfCameras];
		for (int i = 0; i < numberOfCameras; i++) {
			cameraInfo[i] = new CameraInfo();
			android.hardware.Camera.getCameraInfo(i, cameraInfo[i]);
		}

		int backCameraId = -1, frontCameraId = -1;
		// get the first (smallest) back and first front camera id
		for (int i = 0; i < numberOfCameras; i++) {
			if (backCameraId == -1
					&& cameraInfo[i].facing == CameraInfo.CAMERA_FACING_BACK) {
				backCameraId = i;
			} else if (frontCameraId == -1
					&& cameraInfo[i].facing == CameraInfo.CAMERA_FACING_FRONT) {
				frontCameraId = i;
			}
		}

		if (frontCameraId != -1 && front) {
			return frontCameraId;
		} else if (backCameraId != -1 && !front) {
			return backCameraId;
		} else {
			return -1;
		}
	}
	
	public static int getDisplayOrientation(int degrees, int cameraId) {
		// See android.hardware.Camera.setDisplayOrientation for
		// documentation.
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		LogUtils.d("Orientation", "info.orientation==" + info.orientation);
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		return result;
	}
	
	public static int getCameraOrientation(int cameraId) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		return info.orientation;
	}
	
	public static int getDisplayRotation(Activity activity) {
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		switch (rotation) {
		case Surface.ROTATION_0:
			return 0;
		case Surface.ROTATION_90:
			return 90;
		case Surface.ROTATION_180:
			return 180;
		case Surface.ROTATION_270:
			return 270;
		}
		return 0;
	}
	
	
	public static int getPictureRotation(Context cx, boolean front, int screenDirection,
			int previewRotation) {
		LogUtils.d("getPictureRotation", "front[" + front + "]--screenDirection[" + screenDirection + "]--previewRotation[" + previewRotation + "]");
		boolean cameraFilp = false;
		
//		if (front) {
//			cameraFilp = CameraAttrs.flipFront;
//		} else {
//			cameraFilp = CameraAttrs.flipBack;
//		}
		
		int direction = (screenDirection  - previewRotation / 90 + 5) % 4;
		LogUtils.e("getPictureRotation", "init direction[" + direction + "]");
		int pictureOrientation = 0;
		if (front) {
			// Add by dyj to explain an alternative expression
			// Here we can replace (4 - direction + 3) with (3 - direction)
			pictureOrientation = (4 - direction + 3) * 90 % 360;
		} else {
			pictureOrientation = (direction + 1) * 90 % 360;
		}
		LogUtils.e("getPictureRotation", "result pictureOrientation[" + pictureOrientation + "]");
		
		return pictureOrientation;
	}
	
	public static Matrix getViewMatrix(boolean front, int displayOrientation,
			int viewWidth, int viewHeight) {
		Matrix matrix = new Matrix();
		// Need mirror for front camera.
		boolean mirror = front;
		matrix.setScale(mirror ? -1 : 1, 1);
		// This is the value for android.hardware.Camera.setDisplayOrientation.
		matrix.postRotate(displayOrientation);
		// Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
		// UI coordinates range from (0, 0) to (width, height).
		matrix.postScale(viewWidth / 2000f, viewHeight / 2000f);
		matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
		return matrix;
	}

	public static Rect makeRect(RectF rect) {
		return new Rect((int) rect.left, (int) rect.top, (int) rect.right,
				(int) rect.bottom);
	}
	
}
