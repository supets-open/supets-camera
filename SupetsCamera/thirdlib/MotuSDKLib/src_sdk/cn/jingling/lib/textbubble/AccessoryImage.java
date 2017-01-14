package cn.jingling.lib.textbubble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.widget.ImageView;

public class AccessoryImage extends ImageControl {

	protected Boolean mEnter = false;
	protected Boolean mLeave = false;

	protected static float MAX_SCALE_LIMIT = 4.0f;
	protected static float MIN_SCALE_LIMIT = 0.1f;
	protected static final int DELETE_DIST_LIMIT = 80;

	private static float defaultZoom;
		
	private boolean mEnableRotate = true;
	
	private float mInitialGroudScale;

	public AccessoryImage(Context context, Bitmap bitmap, Matrix matrix) {
		super(context, bitmap, matrix);
	}

	@Override
	public void init(Matrix matrix) {
		// TODO Auto-generated method stub
		super.init(matrix);
	}

	public AccessoryImage(ImageView imageView, Bitmap bitmap) {
		super(imageView, bitmap);
	}
	
	public AccessoryImage(ImageView imageView, Bitmap bitmap,Point point) {
		super(imageView, bitmap,point);
	}
	@Override
	public boolean initializeData(Point p) {

		boolean b = super.initializeData();

		if (b == false) {
			return false;
		}

		int dx=p.x;
		int dy=p.y;

		// refer to screen height to change the zoom
		mTransformMatrix.postTranslate((float) dx, (float) dy);
		
		mImageView.setImageMatrix(mTransformMatrix);

		defaultZoom = mLayoutHeight / 800.0f;

		zoom(defaultZoom);

		float hScale = (float) mLayoutHeight / bmpHeight;
		float wScale = (float) mLayoutWidth / bmpWidth;
		MAX_SCALE_LIMIT = hScale < wScale ? hScale : wScale;
		MIN_SCALE_LIMIT = hScale < wScale ? hScale * 0.1f : wScale * 0.1f;
		float[] groudValues = new float[9];
		ScreenControl.getSingleton().getGroundImage().getImageMatrix().getValues(groudValues);
		mInitialGroudScale = groudValues[0];

		mImageView.invalidate();

		return true;
	}
	@Override
	public boolean initializeData() {

		boolean b = super.initializeData();

		if (b == false) {
			return false;
		}

		float dx = (mLayoutWidth - bmpWidth) / 2;
		float dy = (mLayoutHeight - bmpHeight) / 2;
	

		// refer to screen height to change the zoom
		mTransformMatrix.postTranslate((float) dx, (float) dy);
		mImageView.setImageMatrix(mTransformMatrix);

		defaultZoom = mLayoutHeight / 800.0f;

		zoom(defaultZoom);

		float hScale = (float) mLayoutHeight / bmpHeight;
		float wScale = (float) mLayoutWidth / bmpWidth;
		MAX_SCALE_LIMIT = hScale < wScale ? hScale : wScale;
		MIN_SCALE_LIMIT = hScale < wScale ? hScale * 0.1f : wScale * 0.1f;
		float[] groudValues = new float[9];
		ScreenControl.getSingleton().getGroundImage().getImageMatrix().getValues(groudValues);
		mInitialGroudScale = groudValues[0];
		
		mImageView.invalidate();

		return true;
	}

	public Matrix zoom(float scale) {

		MyPoint midPoint = new MyPoint(mLayoutWidth / 2, mLayoutHeight / 2);

		Matrix zoomMatrix = new Matrix();
		zoomMatrix.postScale((float) scale, (float) scale,
				(float) (midPoint.x), (float) (midPoint.y));
		mTransformMatrix.postConcat(zoomMatrix);
		mImageView.setImageMatrix(mTransformMatrix);
		mImageView.invalidate();

		return zoomMatrix;
	}

	@Override
	public void updateImageView(float dx, float dy, float scale,
			boolean willRotate, MyPoint oldFirstPointer, MyPoint newFirstPointer,boolean isDoAll,double rotate) {

		mEnter = false;
		mLeave = false;

		Matrix moveMatrix = new Matrix();
		Matrix zoomMatrix = new Matrix();
		Matrix rotateMatrix = new Matrix();

		Matrix matrix = mTransformMatrix;

		MyPoint midPoint = new MyPoint();
		
		midPoint.set(bmpWidth / 2, bmpHeight / 2);
		midPoint = midPoint.givePointAfterTransform(matrix);
		
		float[] values = new float[9];
		mTransformMatrix.getValues(values);

		float hasScale = (float) Math.sqrt(values[0] * values[0]
				+ values[1] * values[1]);
		
		float[] groudValues = new float[9];
		ScreenControl.getSingleton().getGroundImage().getImageMatrix().getValues(groudValues);
		float curGroudScale = groudValues[0];
		
		float rate = curGroudScale/mInitialGroudScale;
		
		
		boolean isRotete = false;
		if (mFlagRotate == true && willRotate == true) {

			isRotete = true;

			float oldDist = MyPoint.distance(midPoint, oldFirstPointer);
			float newDist = MyPoint.distance(midPoint, newFirstPointer);

			float zoomScale = newDist / oldDist;

			if(isDoAll)
			{
				zoomScale = scale;
			}
			

			// Pwog.w("scale:",
			// hasScale+"-----------"+String.valueOf(zoomScale*hasScale));
			float scale2 = zoomScale * hasScale;
			

			if ((scale2 > MAX_SCALE_LIMIT *rate&& zoomScale > 1.0f)
					|| (scale2 < MIN_SCALE_LIMIT *rate&& zoomScale < 1.0f))
				zoomScale = 1.0f;
			// if(String.valueOf(scale2)=="NaN")
			// Pwog.w("---------------isNaN--------------", "isNaN");

			zoomMatrix.postScale((float) zoomScale, (float) zoomScale,
					(float) midPoint.x, (float) midPoint.y);

			MyPoint angle = MyPoint.getSinCos(oldFirstPointer, newFirstPointer,
					midPoint);
			if (mEnableRotate) {
				if(isDoAll)
				{
					rotateMatrix.setRotate((float) rotate, midPoint.x, midPoint.y);
				}
				else
				{
					rotateMatrix.setSinCos(angle.x, angle.y, midPoint.x, midPoint.y);
				}
			}

		}
		if(isDoAll)
		{
			isRotete = false;
		}
		
		if (!isRotete && mFlagMove == true) {

			moveMatrix.postTranslate((float) dx, (float) dy);
			


			MyPoint point = new MyPoint(bmpWidth / 2, bmpHeight / 2);
			point = point.givePointAfterTransform(mTransformMatrix);
			
			float y = point.y;

			if (y < mLayoutHeight - DELETE_DIST_LIMIT
					&& y + dy > mLayoutHeight - DELETE_DIST_LIMIT) {
				mEnter = true;
			}

			if (y > mLayoutHeight - DELETE_DIST_LIMIT
					&& y + dy < mLayoutHeight - DELETE_DIST_LIMIT) {
				mLeave = true;
			}
			
//			changeGellaryColor();
		}
		
		float scale3 = scale * hasScale;

		if ((scale3 > MAX_SCALE_LIMIT *rate&& scale > 1.0f)
				|| (scale3 < MIN_SCALE_LIMIT *rate&& scale < 1.0f))
		{
			scale = 1.0f;
		}
		
		zoomMatrix.postScale(scale, scale, midPoint.x, midPoint.y);

		changeImageView(moveMatrix, zoomMatrix, rotateMatrix);
	}
	
	public void scaleImageViewNoLimit(float scale) {
		MyPoint point = new MyPoint(bmpWidth / 2, bmpHeight / 2);
		point = point.givePointAfterTransform(mTransformMatrix);
		mTransformMatrix.postScale(scale, scale, point.x, point.y);
	}
	


	public void addMatrix(Matrix matrix) {

		mTransformMatrix.postConcat(matrix);
		mImageView.setImageMatrix(mTransformMatrix);
		mImageView.invalidate();
	}
	
	public void setEnableRotate(boolean enableRotate) {
		mEnableRotate = enableRotate;
	}

//	private void changeGellaryColor() {
//
//		if (mEnter == true) {
//			int color = Color.argb(128, 255, 0, 0);
//			LayoutController.getSingleton().getBottomGallery()
//					.setBackgroundColor(color);
//		}
//
//		if (mLeave == true
//				|| ScreenControl.getSingleton().getmActionIsUp() == true) {
//			int color = Color.argb(128, 0, 0, 0);
//			LayoutController.getSingleton().getBottomGallery()
//					.setBackgroundColor(color);
//		}
//	}
}
