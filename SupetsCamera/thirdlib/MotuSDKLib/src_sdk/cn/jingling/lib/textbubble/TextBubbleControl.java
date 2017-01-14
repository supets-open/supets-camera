package cn.jingling.lib.textbubble;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.text.InputType;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;


public class TextBubbleControl extends ImageControl {

	private TextBubble mTextBubble;

	private TextBubbleStyleParameter mTextPara;

	protected static float MAX_SCALE_LIMIT = 4.0f;
	protected static float MIN_SCALE_LIMIT = 0.1f;
	protected static final int DELETE_DIST_LIMIT = 80;
	private float mInitialGroudScale;
	private static float defaultZoom;
	
	private float mTextHeight;
	private float mTextWidth;

	public TextBubbleControl(ImageView imageView, Bitmap bitmap,
			TextBubbleStyleParameter para) {
		super(imageView, bitmap);
		// TODO Auto-generated constructor stub
		mTextPara = para;
		calcTextSize();
		mTextBubble = new TextBubble(imageView.getContext(), mTextPara, mTextWidth, mTextHeight, "点击编辑文字");
		mTextBubble.setLayoutParams(new LayoutParams(mLayoutWidth,
				mLayoutHeight));
//		mTextBubble.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE|InputType.TYPE_CLASS_TEXT);
//		mTextBubble.setFocusable(true);
//		mTextBubble.setFocusableInTouchMode(true);
		updateTextMatrix();
	}

	public TextBubbleControl(ImageView imageView, Bitmap bitmap, int width, int height,
			TextBubbleStyleParameter para) {
		super(imageView, bitmap, width, height);
		// TODO Auto-generated constructor stub
		mTextPara = para;
		calcTextSize();
		mTextBubble = new TextBubble(imageView.getContext(), mTextPara, mTextWidth, mTextHeight, "点击编辑文字");
		mTextBubble.setLayoutParams(new LayoutParams(mLayoutWidth,
				mLayoutHeight));
		mTextBubble.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE|InputType.TYPE_CLASS_TEXT);
		mTextBubble.setFocusable(true);
		mTextBubble.setFocusableInTouchMode(true);
		updateTextMatrix();
	}
	
	
	private void calcTextSize(){
		float txtWidth = mBitmap.getWidth()* (mTextPara.mRightScale - mTextPara.mLeftScale);
		float txtHeight = mBitmap.getHeight()* (mTextPara.mBottomScale - mTextPara.mTopScale);
	
		mTextWidth = mLayoutWidth;
		mTextHeight = txtHeight * mTextWidth / txtWidth;
		
	}

	public void updateImageView(float dx, float dy, float scale,
			boolean willRotate, MyPoint oldFirstPointer, MyPoint newFirstPointer) {
		super.updateImageView(dx, dy, scale, willRotate, oldFirstPointer, newFirstPointer);
		updateTextMatrix();
	}
	
	
	@Override
	public boolean initializeData(Point p) {
		boolean b = super.initializeData();

		if (b == false) {
			return false;
		}

		int dx=p.x;
		int dy=p.y;

		mTransformMatrix.postTranslate((float) dx, (float) dy);
		mImageView.setImageMatrix(mTransformMatrix);
		defaultZoom = mLayoutHeight / 850.0f;
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

		mTransformMatrix.postTranslate((float) dx, (float) dy);
		mImageView.setImageMatrix(mTransformMatrix);

		defaultZoom = mLayoutHeight / 850.0f;

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
		zoomMatrix.postScale((float) scale, (float) scale, (float) (midPoint.x), (float) (midPoint.y));
		mTransformMatrix.postConcat(zoomMatrix);
		mImageView.setImageMatrix(mTransformMatrix);
		mImageView.invalidate();
		return zoomMatrix;
	}
	
	@Override
	public void updateImageView(float dx, float dy, float scale,
			boolean willRotate, MyPoint oldFirstPointer, MyPoint newFirstPointer,boolean isDoAll,double rotate) {
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

			if(isDoAll){
				zoomScale = scale;
			}
			float scale2 = zoomScale * hasScale;
			
			if ((scale2 > MAX_SCALE_LIMIT *rate&& zoomScale > 1.0f)
					|| (scale2 < MIN_SCALE_LIMIT *rate&& zoomScale < 1.0f))
				zoomScale = 1.0f;

			zoomMatrix.postScale((float) zoomScale, (float) zoomScale,
					(float) midPoint.x, (float) midPoint.y);

			MyPoint angle = MyPoint.getSinCos(oldFirstPointer, newFirstPointer,
					midPoint);
			if(isDoAll){
				rotateMatrix.setRotate((float) rotate, midPoint.x, midPoint.y);
			}
			else{
				rotateMatrix.setSinCos(angle.x, angle.y, midPoint.x, midPoint.y);
			}

		}
		if(isDoAll){
			isRotete = false;
		}
		
		if (!isRotete && mFlagMove == true) {
			
//			if(newFirstPointer.x < 80){
//				dx = 0;
//			}
//			if(newFirstPointer.x >= mLayoutWidth){
//				dx = mLayoutWidth;
//			}
//			
//			if(newFirstPointer.y < 0){
//				dy = 0;
//			}
//			if(newFirstPointer.y >= mLayoutHeight){
//				dy = mLayoutHeight;
//			}
			
			moveMatrix.postTranslate((float) dx, (float) dy);

			MyPoint point = new MyPoint(bmpWidth / 2, bmpHeight / 2);
			point = point.givePointAfterTransform(mTransformMatrix);
			float y = point.y;
		}

		
		float scale3 = scale * hasScale;

		if ((scale3 > MAX_SCALE_LIMIT *rate&& scale > 1.0f)
				|| (scale3 < MIN_SCALE_LIMIT *rate&& scale < 1.0f))
		{
			scale = 1.0f;
		}
		zoomMatrix.postScale(scale, scale, midPoint.x, midPoint.y);

		changeImageViewHasRange(moveMatrix, zoomMatrix, rotateMatrix);
		
		updateTextMatrix();
	}
	
	public TextBubble getTextBubble() {
		return mTextBubble;
	}

	@Override
	public void translateImageView(float dx, float dy) {
		// TODO Auto-generated method stub
		super.translateImageView(dx, dy);
		updateTextMatrix();
	}

	@Override
	public void inValidateImageView() {
		// TODO Auto-generated method stub
		super.inValidateImageView();
	}

	@Override
	public void setImageViewMatrix(Matrix matrix) {
		// TODO Auto-generated method stub
		super.setImageViewMatrix(matrix);
		 updateTextMatrix();

	}
	
	public void addMatrix(Matrix matrix) {
		mTransformMatrix.postConcat(matrix);
		mImageView.setImageMatrix(mTransformMatrix);
		mImageView.invalidate();
		
		updateTextMatrix();
	}

	@Override
	public void setBitmap(Bitmap bitmap) {
		// TODO Auto-generated method stub
		super.setBitmap(bitmap);
	}

	public TextBubbleStyleParameter getmTextPara() {
		return mTextPara;
	}

	/**
	 * 更新文字泡泡样式后重新调用
	 */
	public void updateTextMatrix() {
		Matrix m = new Matrix();
		Point txtLTPoint = MyPoint.givePointAfterTransform(new Point(
				(int) (mBitmap.getWidth() * mTextPara.mLeftScale),
				(int) (mBitmap.getHeight() * mTextPara.mTopScale)),
				mTransformMatrix);
		float[] values = new float[9];
		mTransformMatrix.getValues(values);
		values[2] = 0;
		values[5] = 0;
		m.setValues(values);

		
		float dfScale = 1.0f *mBitmap.getWidth()/mLayoutWidth;
		
		m.postScale((mTextPara.mRightScale - mTextPara.mLeftScale)*dfScale, (mTextPara.mRightScale - mTextPara.mLeftScale)*dfScale);
		m.postTranslate(txtLTPoint.x, txtLTPoint.y);
		mTextBubble.setmTextMatrix(m);
	}

}
