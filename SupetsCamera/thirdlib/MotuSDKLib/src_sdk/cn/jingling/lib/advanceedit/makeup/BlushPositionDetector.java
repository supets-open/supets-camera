package cn.jingling.lib.advanceedit.makeup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import cn.jingling.lib.filters.detection.FaceDetection;
import cn.jingling.lib.filters.detection.FaceDetectorResults;

public class BlushPositionDetector {

	Bitmap mGroundBitmap, mBitmapRight;
	Context mContext;

	Point mLeft = new Point();
	Point mRight = new Point();
	Point mMouth = new Point();
	
	int mEyeDistance;
	
	
	/**
	 * @param groundBm 需检测的图片。
	 * @param BlushBm 需添加的腮红Icon图片。
	 */
	public BlushPositionDetector(Bitmap groundBm, Bitmap BlushBm) {
		mGroundBitmap = groundBm;
		mBitmapRight = BlushBm;
	}
	
	/** 检测腮红位置并返回位置参数。若检测失败，返回null。
	 * @return
	 */
	public MakeupBitmapPosition detectBlushPosition() {
		if (detectFace()) {
			return adjustBlushPosition();
		} else {
			return null;
		}
	}

	/** 粗略检测人脸参数。
	 * 
	 */
	private boolean detectFace() {
		
		FaceDetectorResults results = null;
		results = new FaceDetectorResults();
		results = FaceDetection.detect(mGroundBitmap);
		
		if (results.humans.length == 0) {
			return false;
		}

		FaceDetectorResults.Human human = results.humans[0];

		if (human != null) {
			mLeft.x = human.leftEye.x;
			mLeft.y = human.leftEye.y;
			mRight.x = human.rightEye.x;
			mRight.y = human.rightEye.y;
			mEyeDistance = human.eyeDistance;
			mMouth = human.mouth;
			return true;
		} else {
			return false;
		}

	}
	
	
	private MakeupBitmapPosition adjustBlushPosition() {
		
		Point leftBlushPosition = new Point();
		Point rightBlushPosition = new Point();
	
		
		float scale = (float) (mEyeDistance) / (float) mBitmapRight.getWidth();
		
		
		leftBlushPosition.x = mLeft.x - (int)(mBitmapRight.getWidth()*scale/2);
		// mLeft.y = mLeft.y+(mFaceDetectorResults.humans[0].eyeDistance)/4;
		rightBlushPosition.x = mRight.x - (int)(mBitmapRight.getWidth()*scale/2);
		
//		float downShift = (mMouth.y - mLeft.y- (float)mBitmapRight.getHeight()*scale)/2;
//		downShift = Math.abs(downShift);		
		leftBlushPosition.y = mLeft.y;
		rightBlushPosition.y = mRight.y;
		
		return new MakeupBitmapPosition(leftBlushPosition, rightBlushPosition, scale);

	}
	
}
