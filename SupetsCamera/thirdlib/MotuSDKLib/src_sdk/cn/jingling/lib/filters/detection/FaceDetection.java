package cn.jingling.lib.filters.detection;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.media.FaceDetector;
import cn.jingling.lib.filters.detection.FaceDetectorResults.Human;

public class FaceDetection {

	/** 检测单一人脸并返回结果。相当于detect(bm, 1)
	 * @param bm
	 * @return
	 */
	public static FaceDetectorResults detect(Bitmap bm) {
		return detect(bm, 1);
	}
	
	/** 检测人脸并返回结果。
	 * @param bm
	 * @param numOfFaces the maximum number of faces to identify
	 * @return 返回检测结果，包括左右眼睛位置点、左右眼睛距离、嘴巴位置点、人脸位置矩形
	 */
	public static FaceDetectorResults detect(Bitmap bm, int numOfFaces) {
		PointF eyeMidPoint = new PointF();
		float eyesDistance = 0;
		FaceDetectorResults faceDetectorResults = new FaceDetectorResults();
		
//		Bitmap myBitmap = bm.copy(Config.RGB_565, true);
		Bitmap myBitmap = Bitmap.createBitmap(bm.getWidth() % 2 == 0 ? bm.getWidth() : bm.getWidth() + 1, bm.getHeight(), Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(myBitmap);
		canvas.drawBitmap(bm, 0, 0, new Paint());
		FaceDetector faceDetector = new FaceDetector(myBitmap.getWidth(),
				myBitmap.getHeight(), numOfFaces);
		FaceDetector.Face[] face = new FaceDetector.Face[numOfFaces];

		numOfFaces = faceDetector.findFaces(myBitmap, face);

		// Log.v("------------->", "" + numOfFaces);
		faceDetectorResults.humans = new Human[numOfFaces];
		if (numOfFaces > 0) {
			for (int i = 0; i < numOfFaces; i++) {
				face[i].getMidPoint(eyeMidPoint);
				eyesDistance = face[i].eyesDistance();
				Human human = new Human();
				// position
				// float eulerX=face[i].pose(EULER_X);
				// float eulerY=face[i].pose(EULER_Y);
				// float eulerZ=face[i].pose(EULER_Z);
				human.leftEye = new Point();
				human.rightEye = new Point();
				human.leftEye.x = (int)(eyeMidPoint.x
						- eyesDistance / 2);
				human.leftEye.y = (int)eyeMidPoint.y;
				human.rightEye.x = (int)(eyeMidPoint.x
						+ eyesDistance / 2);
				human.rightEye.y = (int)eyeMidPoint.y;
				human.mouth = new Point();
				human.mouth.x = (human.leftEye.x + human.rightEye.x) / 2;
				human.mouth.y = human.leftEye.y + Math.abs(human.leftEye.x - human.rightEye.x) * 6 / 5;
				human.eyeDistance = (int)eyesDistance;
				faceDetectorResults.humans[i] = human;
			}
		}
		return faceDetectorResults;
	}

}