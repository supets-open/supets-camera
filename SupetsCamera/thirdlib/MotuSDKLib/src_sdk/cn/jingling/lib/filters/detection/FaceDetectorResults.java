package cn.jingling.lib.filters.detection;

import java.io.Serializable;

import cn.jingling.lib.utils.MathUtils;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * To store the result of face detection, humans[i] for the ith face information.
 * @author jiankun.zhi
 *
 */
public class FaceDetectorResults implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7929083474709336922L;
	
	public Human[] humans;
	
	/** 记录单一人脸信息数据，包括左右眼睛位置点、左右眼睛距离、嘴巴位置点、人脸位置矩形
	 *
	 */
	public static class Human {
		public Point leftEye;
		public Point rightEye;
		public int eyeDistance;
		public Rect face;
		public Point mouth;
	}
	
	public FaceDetectorResults() {
		
	}
	
	public FaceDetectorResults(Point left, Point right, Point mouth) {
		humans = new Human[1];
		humans[0] = new Human();
		humans[0].leftEye = left;
		humans[0].rightEye = right;
		humans[0].mouth = mouth;
		humans[0].eyeDistance = MathUtils.dist(left, right);
	}
	
	public void copy(FaceDetectorResults result) {
		humans = result.humans;
	}
}