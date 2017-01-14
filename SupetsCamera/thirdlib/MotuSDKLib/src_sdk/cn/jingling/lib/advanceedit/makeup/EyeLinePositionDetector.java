package cn.jingling.lib.advanceedit.makeup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import cn.jingling.lib.filters.ImageProcessUtils;
import cn.jingling.lib.filters.detection.FaceDetection;
import cn.jingling.lib.filters.detection.FaceDetectorResults;
import cn.jingling.lib.utils.MathUtils;

public class EyeLinePositionDetector {

	Bitmap mGroundBitmap, mBitmapRight;
	Context mContext;

	Point mLeft = new Point();
	Point mRight = new Point();
	Point mMouth = new Point();
	
	int mEyeDistance;
	
	
	/**
	 * @param groundBm 需检测的图片。
	 * @param eyeLineBm 需添加的眼线Icon图片。
	 */
	public EyeLinePositionDetector(Bitmap groundBm, Bitmap eyeLineBm) {
		mGroundBitmap = groundBm;
		mBitmapRight = eyeLineBm;
	}
	
	/** 检测眼线位置并返回位置参数。若检测失败，返回null。
	 * @return
	 */
	public MakeupBitmapPosition detectEyelinePosition() {
		if (detectFace()) {
			return adjustEyeLinePosition();
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
	
	/**  精细调整眼线位置。
	 * 
	 */
	private MakeupBitmapPosition adjustEyeLinePosition() {
		Bitmap bitmapRight = mBitmapRight;
		Matrix matrix = new Matrix();
		float sw = -1, sh = 1;
		matrix.postScale(sw, sh);
		Bitmap bitmapLeft = Bitmap.createBitmap(bitmapRight, 0, 0,
				bitmapRight.getWidth(), bitmapRight.getHeight(), matrix, true);
		float scale = 1;
		if (mEyeDistance != 0) {
			scale = (float) ((mEyeDistance) * 2 / 3) * 5 / 6
					/ (float) bitmapRight.getWidth();
			matrix.reset();
			matrix.postScale(scale, scale);
		}
		bitmapRight = Bitmap.createBitmap(bitmapRight, 0, 0,
				bitmapRight.getWidth(), bitmapRight.getHeight(), matrix, true);
		bitmapLeft = Bitmap.createBitmap(bitmapLeft, 0, 0,
				bitmapLeft.getWidth(), bitmapLeft.getHeight(), matrix, true);
		Point eyeLeftTopPoint = new Point();
		Point eyeRightDownPoint = new Point();
		Bitmap bitmapGround = mGroundBitmap;
		int elw = bitmapLeft.getWidth();
		int elh = bitmapLeft.getHeight();
		int w = bitmapGround.getWidth();
		int h = bitmapGround.getHeight();

		// 底图和新眼线图做灰度变换
		byte[] groundGray = ImageProcessUtils.getGrayImage(bitmapGround);
		byte[] eyelineRightGray = ImageProcessUtils.getGrayImage(bitmapRight);
		byte[] eyelineLeftGray = ImageProcessUtils.getGrayImage(bitmapLeft);

		// 调用眼线位置定位的函数
		// left eye
		eyeLeftTopPoint.x = mLeft.x - mEyeDistance / 3;
		eyeLeftTopPoint.y = mLeft.y - mEyeDistance / 6;
		eyeRightDownPoint.x = mLeft.x + mEyeDistance / 3;
		eyeRightDownPoint.y = mLeft.y + mEyeDistance / 6;
		Point leftEyeLinePosition = eyelinePosition(eyeLeftTopPoint, eyeRightDownPoint, groundGray,
				eyelineLeftGray, w, h, elw, elh, 0);
		// right eye
		eyeLeftTopPoint.x = mRight.x - mEyeDistance / 3;
		eyeLeftTopPoint.y = mRight.y - mEyeDistance / 6;
		eyeRightDownPoint.x = mRight.x + mEyeDistance / 3;
		eyeRightDownPoint.y = mRight.y + mEyeDistance / 6;
		Point rightEyeLinePosition = eyelinePosition(eyeLeftTopPoint, eyeRightDownPoint,
				groundGray, eyelineRightGray, w, h, elw, elh, 1);
		
		return new MakeupBitmapPosition(leftEyeLinePosition, rightEyeLinePosition, scale);

	}
	
	private MakeupBitmapPosition adjustBlushPosition() {
		
		Point leftBlushPosition = new Point();
		Point rightBlushPosition = new Point();
		
		leftBlushPosition.x = mLeft.x - (mEyeDistance) / 2;
		// mLeft.y = mLeft.y+(mFaceDetectorResults.humans[0].eyeDistance)/4;
		rightBlushPosition.x = mRight.x - (mEyeDistance)/ 2;
		
		float scale = (float) (mEyeDistance) / (float) mGroundBitmap.getWidth();
		
		float downShift = (mMouth.y - mLeft.y- (float)mGroundBitmap.getHeight()*scale)/2;
		downShift = Math.abs(downShift);
		
		leftBlushPosition.y = mLeft.y + (int)downShift;
		rightBlushPosition.y = mRight.y + (int)downShift;
		
		return new MakeupBitmapPosition(leftBlushPosition, rightBlushPosition, scale);

	}

	private Point eyelinePosition(Point eyeLeftTopPoint,
			Point eyeRightDownPoint, byte[] mGray, byte[] mEyelineGray, int w,
			int h, int elw, int elh, int position) {
		Point eyelinePosit = new Point();
		int startH = 0, startW = 0;
		int m, n;
		int maxCount = 0;
		int count;
		int temp, temp1, temp2;
		// 用眼睛范围找出二值化阈值
		int maxGray = MathUtils.toInt(mGray[0]);
		int minGray = MathUtils.toInt(mGray[0]);
		for (m = eyeLeftTopPoint.y; m < eyeRightDownPoint.y; m++) {
			for (n = eyeLeftTopPoint.x; n < eyeRightDownPoint.x; n++) {
				temp = MathUtils.toInt(mGray[m * w + n]);
				if (maxGray < temp)
					maxGray = temp;
				if (minGray > temp)
					minGray = temp;
			}
		}
		int thres = (maxGray + minGray) / 3;
		int step = 1;
		if (elw > 75)
			step = 2;
		eyelinePosit.x = eyeLeftTopPoint.x;
		eyelinePosit.y = eyeLeftTopPoint.y;
		int UP, DOWN, LEFT, RIGHT;

		if (position == 0) {
			UP = (eyeLeftTopPoint.y - elh / 6);
			if (UP < 0) {
				UP = 0;
			}
			DOWN = (eyeRightDownPoint.y - elh / 3);
			if (DOWN > h) {
				DOWN = h;
			}
			LEFT = eyeLeftTopPoint.x - elw / 6;
			if (LEFT < 0) {
				LEFT = 0;
			}
			RIGHT = eyeRightDownPoint.x - elw;
			if (RIGHT > w) {
				RIGHT = w;
			}
			for (startH = UP; startH < DOWN; startH = startH + step) {
				for (startW = LEFT; startW < RIGHT; startW = startW + step) {
					count = 0;
					for (m = 0; m < elh / 3; m++) {
						for (n = 0; n < elw - elw / 6; n++) {
							temp1 = MathUtils.toInt(mEyelineGray[(m + elh / 3)
									* elw + n + elw / 6]);
							int index = (startH + m) * w + startW + n;
							if (index > w * h - 1) {
								index = w * h - 1;
							}
							temp2 = MathUtils.toInt(mGray[index]);
							if (temp1 < thres && temp2 < thres) {
								count++;
							}
						}
					}

					if (count > maxCount) {
						maxCount = count;
						eyelinePosit.y = startH - elh / 3;
						eyelinePosit.x = startW - elw / 6;
					}
				}
			}
		} else {
			UP = (eyeLeftTopPoint.y - elh / 6);
			if (UP < 0) {
				UP = 0;
			}
			DOWN = (eyeRightDownPoint.y - elh / 3);
			if (DOWN > h) {
				DOWN = h;
			}
			LEFT = eyeLeftTopPoint.x + elw / 6;
			if (LEFT < 0) {
				LEFT = 0;
			}
			RIGHT = eyeRightDownPoint.x + elw / 6 - elw;
			if (RIGHT > w) {
				RIGHT = w;
			}
			for (startH = (eyeLeftTopPoint.y - elh / 6); startH < (eyeRightDownPoint.y - elh / 3); startH = startH
					+ step) {
				for (startW = eyeLeftTopPoint.x + elw / 6; startW < eyeRightDownPoint.x
						+ elw / 6 - elw; startW = startW + step) {
					count = 0;
					for (m = 0; m < elh / 3; m++) {
						for (n = 0; n < elw - elw / 6; n++) {
							temp1 = MathUtils.toInt(mEyelineGray[(m + elh / 3)
									* elw + n]);
							int index = (startH + m) * w + startW + n;
							if (index > w * h - 1) {
								index = w * h - 1;
							}
							temp2 = MathUtils.toInt(mGray[index]);
							if (temp1 < thres && temp2 < thres) {
								count++;
							}
						}
					}

					if (count > maxCount) {
						maxCount = count;
						eyelinePosit.y = startH - elh / 3;
						eyelinePosit.x = startW;
					}
				}
			}

		}

		return eyelinePosit;
	}
	

}
