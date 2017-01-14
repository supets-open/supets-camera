package cn.jingling.lib.textbubble;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;

public class GroundImage extends ImageControl implements OnDoubleTapListener,
		OnGestureListener {
	
	private GestureDetector mGestureDetector = null;

	public static final float MAX_SCALE_LIMIT = 2.0f;
	private static final float DOUBLE_TAP_SCALE = 1.9f;

//	private boolean mDoubleTapState = false;

	public GroundImage(ImageView imageView, Bitmap bitmap) {
		super(imageView, bitmap);
	}
	
	public boolean isLand()
	{
		return (bmpWidth > bmpHeight);
	}

	@Override
	public boolean initializeData() {
		if (!super.initializeData()) {
			return false;
		}

		double widthScale = 1.0 * mLayoutWidth / bmpWidth;
		double heightScale = 1.0 * mLayoutHeight / bmpHeight;
		double scale = heightScale < widthScale ? heightScale : widthScale;

		double dx = (mLayoutWidth - bmpWidth * scale) / 2;
		double dy = (mLayoutHeight - bmpHeight * scale) / 2;

		mTransformMatrix.postScale((float) scale, (float) scale);
		mTransformMatrix.postTranslate((float) dx, (float) dy);
		mImageView.setImageMatrix(mTransformMatrix);
		mImageView.invalidate();
		
		// mImageView.getLayoutParams()

		// mImageView.setOnTouchListener(this);

		mGestureDetector = new GestureDetector(this);
		mGestureDetector.setOnDoubleTapListener(this);

		setFlagMove(true);
		setFlagZoom(true);
		setFlagRotate(false);

		return true;
	}

	public void updateImageView(float dx, float dy, float scale,
			boolean willRotate, MyPoint oldFirstPointer, MyPoint newFirstPointer,MyPoint centerPoint) {

		Matrix iMatrix = new Matrix();
		mTransformMatrix.invert(iMatrix);
		MyPoint point = new MyPoint(bmpWidth / 2, bmpHeight / 2)
				.givePointAfterTransform(mTransformMatrix);
		if (mFlagZoom == true) {
			float[] values = new float[9];
			mTransformMatrix.getValues(values);
			float zoomScale = (float) (Math.sqrt(values[0] * values[0]
					+ values[1] * values[1]) * scale);
			if (bmpWidth * zoomScale > 2 * mLayoutWidth
					|| bmpHeight * zoomScale > 2 * mLayoutHeight) {
				scale = 1.0f;
			}
			if (bmpWidth * zoomScale < mLayoutWidth / 3
					|| bmpHeight * zoomScale < mLayoutHeight / 3) {
				scale = 1.0f;
			}
			if(centerPoint != null)
			{
				mTransformMatrix.postScale(scale, scale, centerPoint.x, centerPoint.y);
			}
			else
			{
				mTransformMatrix.postScale(scale, scale, point.x, point.y);

			}
		}
		if (mFlagRotate == true && willRotate == true) {
			MyPoint angle = MyPoint.getSinCos(oldFirstPointer, newFirstPointer,
					point);
			rotateImageView(angle.x, angle.y);
		} else if (mFlagMove == true) {
//			Log.d(TAG, "dx=" + dx + " dy=" + dy);
			mTransformMatrix.postTranslate(dx, dy);
		}
		else
		{
//			if(ScreenControl.getSingleton().getmIsMosaic())
//			{
//				mTransformMatrix.postTranslate(dx, dy);
//			}
		}

		mImageView.setImageMatrix(mTransformMatrix);
		mImageView.invalidate();
		
		// updateScreenLayout();

		iMatrix.postConcat(mTransformMatrix);
		updateObjects(iMatrix);
	}

	
//	private void updateScreenLayout()
//	{
//		RelativeLayout relativeLayout = ScreenControl.getSingleton().mRelativeLayout;
//		Matrix matrix = mImageView.getImageMatrix();
//		Log.d(TAG, "2: " + matrix.toShortString());
//		float[] values = new float[9];
//		matrix.getValues(values);
//		
//		int lw = mLayoutWidth;
//		int lh = mLayoutHeight;
//		int iw = (int) (mBitmap.getWidth() * values[0]);
//		int ih = (int) (mBitmap.getHeight() * values[4]);
//		int padX = (lw - iw)>0 ? (lw - iw)/2 : 0;
//		int padY = (lh - ih)>0 ? (lh - ih)/2 : 0;
//		
//		relativeLayout.setPadding(padX, padY, padX, padY);
//		
//		Matrix m = new Matrix();
//		m.setTranslate(-padX, -padY);
//		
//		matrix.setTranslate(0, 0);
//		mImageView.setImageMatrix(matrix);
//		mImageView.invalidate();
//		
//		// addMatrix(m);
//	}

	protected void addMatrix(Matrix matrix) {

		mTransformMatrix.postConcat(matrix);
		mImageView.setImageMatrix(mTransformMatrix);
		mImageView.invalidate();

		int arrayListSize = ScreenControl.getSingleton().mImageControlArrayList
				.size();

		for (int i = 0; i < arrayListSize; i++) {
			AccessoryImage accessoryImage = (AccessoryImage) ScreenControl
					.getSingleton().mImageControlArrayList.get(i);
			accessoryImage.addMatrix(matrix);
		}
	}

	public void postConcat(Matrix matrix) {

		mTransformMatrix.postConcat(matrix);
		mImageView.setImageMatrix(mTransformMatrix);
		mImageView.invalidate();
	}

	public void addingAccessoryRebound() {
		
		// updateScreenLayout();

		Matrix matrix = new Matrix(mTransformMatrix);
		Matrix inverseMatrix = new Matrix();
		matrix.invert(inverseMatrix);

		rebound();

		Matrix reMatrix = new Matrix(inverseMatrix);
		reMatrix.postConcat(mTransformMatrix);

		int arrayListSize = ScreenControl.getSingleton().mImageControlArrayList
				.size();

		for (int i = 0; i < arrayListSize; i++) {
			AccessoryImage accessoryImage = (AccessoryImage) ScreenControl
					.getSingleton().mImageControlArrayList.get(i);
			accessoryImage.addMatrix(reMatrix);
		}
	}

	public void addingAccessoryUpdate(float dx, float dy, float scale,
			Boolean willRotate, MyPoint oldFirstPointer, MyPoint newFirstPointer) {

		Matrix moveMatrix = new Matrix();
		Matrix zoomMatrix = new Matrix();
		// Matrix rotateMatrix = new Matrix();

		mTransformMatrix = mImageView.getImageMatrix();
		Matrix matrix = new Matrix(mTransformMatrix);

		if (mFlagZoom == true) {

			MyPoint point = new MyPoint();
			point.set(bmpWidth / 2, bmpHeight / 2);

			point = point.givePointAfterTransform(matrix);

			zoomMatrix.postScale((float) scale, (float) scale, (float) point.x,
					(float) point.y);
			
			
		}

		if (mFlagMove == true) {

			moveMatrix.postTranslate((float) dx, (float) dy);
		}

		zoomMatrix.postConcat(moveMatrix);

		addMatrix(zoomMatrix);
		
		// updateScreenLayout();
	}

	protected void addingTextRebound() {

//		Matrix matrix = new Matrix(mTransformMatrix);
//		Matrix inverseMatrix = new Matrix();
//		matrix.invert(inverseMatrix);
//
//		rebound();
//
//		Matrix reMatrix = new Matrix(inverseMatrix);
//		reMatrix.postConcat(mTransformMatrix);

//		ScreenControl.getSingleton().getmCustomTextView()
//				.transformAllTextBox(reMatrix);
		
		
		Matrix matrix = new Matrix(mTransformMatrix);
		Matrix inverseMatrix = new Matrix();
		matrix.invert(inverseMatrix);

		rebound();

		Matrix reMatrix = new Matrix(inverseMatrix);
		reMatrix.postConcat(mTransformMatrix);

		int arrayListSize = ScreenControl.getSingleton().mImageControlArrayList
				.size();

		for (int i = 0; i < arrayListSize; i++) {
			TextBubbleControl bubbleControl = (TextBubbleControl) ScreenControl
					.getSingleton().mImageControlArrayList.get(i);
//			bubbleControl.setImageViewMatrix(reMatrix);
			bubbleControl.addMatrix(reMatrix);
			
//			AccessoryImage accessoryImage = (AccessoryImage) ScreenControl
//					.getSingleton().mImageControlArrayList.get(i);
//			accessoryImage.addMatrix(reMatrix);
		}

	}

	public void addingTextUpdate(float dx, float dy, float scale,
			Boolean willRotate, MyPoint oldFirstPointer, MyPoint newFirstPointer,MyPoint centerPoint) {

//		Matrix moveMatrix = new Matrix();
//		Matrix zoomMatrix = new Matrix();
//		// Matrix rotateMatrix = new Matrix();
//
//		Matrix matrix = new Matrix(mTransformMatrix);
//
//		if (mFlagZoom == true) {
//
//			MyPoint point = new MyPoint();
//			point.set(bmpWidth / 2, bmpHeight / 2);
//
//			point = point.givePointAfterTransform(matrix);
//
//			zoomMatrix.postScale((float) scale, (float) scale, (float) point.x,
//					(float) point.y);
//		}
//
//		if (mFlagMove == true) {
//
//			moveMatrix.postTranslate((float) dx, (float) dy);
//		}
//
//		zoomMatrix.postConcat(moveMatrix);
//
//		mTransformMatrix.postConcat(zoomMatrix);
//		mImageView.setImageMatrix(mTransformMatrix);
//		mImageView.invalidate();
//
//
//		int arrayListSize = ScreenControl.getSingleton().mImageControlArrayList
//				.size();
//
//		for (int i = 0; i < arrayListSize; i++) {
//			TextBubbleControl bubbleControl = (TextBubbleControl) ScreenControl
//					.getSingleton().mImageControlArrayList.get(i);
//			bubbleControl.setImageViewMatrix(zoomMatrix);
//		}
		


		Matrix moveMatrix = new Matrix();
		Matrix zoomMatrix = new Matrix();
		// Matrix rotateMatrix = new Matrix();

		mTransformMatrix = mImageView.getImageMatrix();
		Matrix matrix = new Matrix(mTransformMatrix);

		if (mFlagZoom == true) {

			MyPoint point = new MyPoint();
			point.set(bmpWidth / 2, bmpHeight / 2);

			point = point.givePointAfterTransform(matrix);

			if(centerPoint == null){
				zoomMatrix.postScale((float) scale, (float) scale, (float) point.x,
						(float) point.y);
			}
			else{
				zoomMatrix.postScale((float) scale, (float) scale, (float) centerPoint.x,
						(float) centerPoint.y);
			}
		}

		if (mFlagMove == true) {

			moveMatrix.postTranslate((float) dx, (float) dy);
		}

		
		
		zoomMatrix.postConcat(moveMatrix);

		mTransformMatrix.postConcat(zoomMatrix);
		mImageView.setImageMatrix(mTransformMatrix);
		mImageView.invalidate();

		int numOfAccessory = ScreenControl.getSingleton()
				.getmImageControlArrayList().size();
		for (int i = 0; i < numOfAccessory; i++) {
			TextBubbleControl bubbleControl = (TextBubbleControl) ScreenControl.getSingleton().mImageControlArrayList.get(i);
			Matrix m = bubbleControl.getImageMatrix();
			m.postConcat(zoomMatrix);
			bubbleControl.setImageViewMatrix(m);
		}
	
	}

	protected void addingBlushRebound(float dx, float dy, float scale,
			Boolean willRotate, MyPoint oldFirstPointer, MyPoint newFirstPointer) {

		Matrix matrix = new Matrix(mTransformMatrix);
		Matrix inverseMatrix = new Matrix();
		matrix.invert(inverseMatrix);

		rebound();

		Matrix reMatrix = new Matrix(inverseMatrix);
		reMatrix.postConcat(mTransformMatrix);

		int numOfAccessory = ScreenControl.getSingleton()
				.getmImageControlArrayList().size();
		for (int i = 0; i < numOfAccessory; i++) {
			ScreenControl
					.getSingleton()
					.getmImageControlArrayList()
					.get(i)
					.updateImageView(dx, dy, scale, willRotate,
							oldFirstPointer, newFirstPointer);
		}

	}




	public Matrix rotate(float degrees) {
		MyPoint midPoint = new MyPoint(mLayoutWidth / 2, mLayoutHeight / 2);

		Matrix rotateMatrix = new Matrix();
		rotateMatrix.postRotate((float) degrees, (float) (midPoint.x),
				(float) (midPoint.y));
		mTransformMatrix.postConcat(rotateMatrix);
		mImageView.setImageMatrix(mTransformMatrix);
		mImageView.invalidate();

		return rotateMatrix;
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

	public Matrix translate(float dx, float dy) {
		Matrix moveMatrix = new Matrix();
		moveMatrix.postTranslate((float) dx, (float) dy);
		mTransformMatrix.postConcat(moveMatrix);
		mImageView.setImageMatrix(mTransformMatrix);
		mImageView.invalidate();

		return moveMatrix;
	}

	public MyPoint giveValidPoint(MyPoint newPoint) {

		MyPoint p2 = new MyPoint(newPoint);

		p2 = p2.givePointBeforTransform(mTransformMatrix);

		float x = p2.x;
		float y = p2.y;

		int width = mBitmap.getWidth();
		int height = mBitmap.getHeight();

		if (x < 0) {
			x = 0;
		}

		if (x > width) {
			x = width;
		}

		if (y < 0) {
			y = 0;
		}

		if (y > height) {
			y = height;
		}

		p2.set(x, y);

		// Log.d( TAG, "p2=" + p2.toString() );
		// Log.d( TAG, "width= " + width + " " + height );

		MyPoint point = p2.givePointAfterTransform(mTransformMatrix);

		return point;
	}

	public MyPoint intersectionValidPoint(MyPoint oldPoint, MyPoint newPoint) {

		MyPoint p1 = oldPoint.givePointBeforTransform(mTransformMatrix);
		MyPoint p2 = newPoint.givePointBeforTransform(mTransformMatrix);

		MyPoint validPoint = new MyPoint();

		if (inRect(p2) == true) {
			return new MyPoint(newPoint);
		} else if (inRect(p1) == false) {
			return new MyPoint(oldPoint);
		} else {

			int width = mBitmap.getWidth();
			int height = mBitmap.getHeight();

			MyPoint u1 = new MyPoint();
			MyPoint u2 = new MyPoint();

			u1.set(0, 0);
			u2.set(width, 0);

			if (p2.y < 0
					&& MyPoint.intersection(u1, u2, p1, p2, validPoint) == true) {
				return validPoint.givePointAfterTransform(mTransformMatrix);
			}

			u1.set(0, 0);
			u2.set(0, height);

			if (p2.x < 0
					&& MyPoint.intersection(u1, u2, p1, p2, validPoint) == true) {
				return validPoint.givePointAfterTransform(mTransformMatrix);
			}

			u1.set(width, 0);
			u2.set(width, height);

			if (p2.x > width
					&& MyPoint.intersection(u1, u2, p1, p2, validPoint) == true) {
				return validPoint.givePointAfterTransform(mTransformMatrix);
			}

			u1.set(0, height);
			u2.set(width, height);

			if (p2.y > height
					&& MyPoint.intersection(u1, u2, p1, p2, validPoint) == true) {
				return validPoint.givePointAfterTransform(mTransformMatrix);
			}

			return validPoint.givePointAfterTransform(mTransformMatrix);
		}

	}

	public Boolean inRect(MyPoint point) {
		float x = point.x;
		float y = point.y;

		int width = mBitmap.getWidth();
		int height = mBitmap.getHeight();

		// if(x<0 || y<0 || x>width || y>height) return false;
		if (x < -0.001 || y < -0.001 || x > width + 0.001 || y > height + 0.001) {
			return false;
		}
		return true;
	}

	public MyPoint shouldMoveVector(MyPoint leftTop, MyPoint rightTop,
			MyPoint rightBottom, MyPoint leftBottom, MyPoint oldMove) {
		// compute the move vector(dx,dy) in the beforeTransformed coordinate
		// system
		MyPoint zeroPoint = new MyPoint(0, 0);
		zeroPoint = zeroPoint.givePointBeforTransform(mTransformMatrix);
		oldMove = oldMove.givePointBeforTransform(mTransformMatrix);
		float dx = oldMove.x - zeroPoint.x;
		float dy = oldMove.y - zeroPoint.y;

		// crop frame's coordinate before transformed
		MyPoint lt = leftTop.givePointBeforTransform(mTransformMatrix);
		MyPoint rt = rightTop.givePointBeforTransform(mTransformMatrix);
		MyPoint lb = leftBottom.givePointBeforTransform(mTransformMatrix);
		MyPoint rb = rightBottom.givePointBeforTransform(mTransformMatrix);

		// compute the move vector(dx,dy) that is not out of range
		float t;
		if (dx < 0) {
			t = -Math.min(Math.min(lt.x, rt.x), Math.min(lb.x, rb.x));
			dx = dx > t ? dx : t;
		} else {
			t = mBitmap.getWidth()
					- Math.max(Math.max(lt.x, rt.x), Math.max(lb.x, rb.x));
			dx = dx < t ? dx : t;
		}

		if (dy < 0) {
			t = -Math.min(Math.min(lt.y, rt.y), Math.min(lb.y, rb.y));
			dy = dy > t ? dy : t;
		} else {
			t = mBitmap.getHeight()
					- Math.max(Math.max(lt.y, rt.y), Math.max(lb.y, rb.y));
			dy = dy < t ? dy : t;
		}

		// compute the move vector(dx,dy) in the Transformed coordinate system
		// and return
		return new MyPoint(zeroPoint.x + dx, zeroPoint.y + dy)
				.givePointAfterTransform(mTransformMatrix);
	}

	private void updateObjects(Matrix matrix) {
		if (ScreenControl.getSingleton().getmIsAddingAccessory() == true) {
			int arrayListSize = ScreenControl.getSingleton().mImageControlArrayList
					.size();

			for (int i = 0; i < arrayListSize; i++) {
				AccessoryImage accessoryImage = (AccessoryImage) ScreenControl
						.getSingleton().mImageControlArrayList.get(i);
				accessoryImage.addMatrix(matrix);
			}
			
//			if(ScreenControl.getSingleton().mIsShape)
//			{
//				float[] values =  new float[9];
//				matrix.getValues(values);
//				float translate = 1.0f+(values[0] - 1.0f)/PartialShapeEffect.SCALE_REALTIME_RATE;
//				translate = translate/values[0];
//				
//				for (int i = 0; i < arrayListSize; i++) {
//					AccessoryImage accessoryImage = (AccessoryImage) ScreenControl
//							.getSingleton().mImageControlArrayList.get(i);
//					accessoryImage.updateImageView(0, 0, translate, false, null, null);
//				}
//			}
			

		} else if (ScreenControl.getSingleton().getmIsAddingText() == true) {
			int arrayListSize = ScreenControl.getSingleton().mImageControlArrayList.size();

			for (int i = 0; i < arrayListSize; i++) {
				TextBubbleControl accessoryImage = (TextBubbleControl) ScreenControl
						.getSingleton().mImageControlArrayList.get(i);
				accessoryImage.addMatrix(matrix);
			}
		}
	}

	/*
	 * // private MyPoint oldPoint; // private float oldDistance; // //
	 * @Override // public boolean onTouch(View v, MotionEvent event) { // //
	 * TODO Auto-generated method stub // if
	 * (mGestureDetector.onTouchEvent(event)) return true; // //
	 * mEvent.setEvent(event); // if(mEvent.getAction() ==
	 * MotionEvent.ACTION_UP) { // rebound(); // return true; // } // // int
	 * pointerCnt = mEvent.getPointerCount();; // // if (pointerCnt == 1) { //
	 * int pid = mEvent.getPointerId(0); // if(mEvent.getAction() !=
	 * MotionEvent.ACTION_DOWN) { // updateImageView((float)mEvent.getX(pid) -
	 * oldPoint.x, // (float)mEvent.getY(pid) - oldPoint.y, 1.0f, false, null,
	 * null); // } // oldPoint = new MyPoint(mEvent.getX(pid),
	 * mEvent.getY(pid)); // // } else { // int pid0 = mEvent.getPointerId(0);
	 * // int pid1 = mEvent.getPointerId(1); // float newDistance =
	 * MyPoint.distance(new MyPoint(mEvent.getX(pid0), mEvent.getY(pid0)), //
	 * new MyPoint(mEvent.getX(pid1),mEvent.getY(pid1))); //
	 * if(mEvent.getAction() != MotionEvent.ACTION_DOWN) // { //
	 * updateImageView(0, 0, newDistance/oldDistance, false, null, null); // }
	 * // oldDistance = newDistance; // } // return true; // // }
	 */

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		/**
		 * @author lijing24
		 * ZoomFlag为false时，不进行双击缩放
		 */
		if (mFlagZoom == false) {
			return false;
		}
		/*end modified*/
		Matrix iMatrix = new Matrix();
		
		
		mTransformMatrix.invert(iMatrix);

		MyPoint midPoint = new MyPoint(mLayoutWidth / 2,
				mLayoutHeight / 2);
		
		double widthScale = 1.0 * mLayoutWidth / bmpWidth;
		double heightScale = 1.0 * mLayoutHeight / bmpHeight;
		double scale1 = heightScale < widthScale ? heightScale : widthScale;
		double dx = (mLayoutWidth - bmpWidth*scale1)/2;
		double dy = (mLayoutHeight - bmpHeight*scale1)/2;
		
		Matrix matrix = getImageMatrix();
		float [] values=new float[9];
		matrix.getValues(values);
		double scale2=values[0];
		float scale=(float) 1.0;
		
		if(Math.abs(scale1-scale2)<=0.01)
		{
			scale = DOUBLE_TAP_SCALE;
			mTransformMatrix.postScale(scale, scale, e.getX(), e.getY());
			mTransformMatrix.postTranslate((midPoint.x - e.getX()),
					(midPoint.y - e.getY()));
			mImageView.setImageMatrix(mTransformMatrix);
		}
		else
		{
			scale = (float) scale1;
			mTransformMatrix = new Matrix();
			mTransformMatrix.postScale(scale, scale);
			mTransformMatrix.postTranslate((float)dx, (float)dy);
			mImageView.setImageMatrix(mTransformMatrix);
		}

		rebound();
		iMatrix.postConcat(mTransformMatrix);

		updateObjects(iMatrix);
		

		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		ScreenControl.getSingleton().singleTapped();
	
		return false;
	}

	public GestureDetector getGestureDetector() {
		// TODO Auto-generated method stub
		return mGestureDetector;
	}
	
	public Rect getGrounImageRect()
	{
//		int w = mImageView.getWidth();
//		int h = mImageView.getHeight();
//		int x = mImageView.getLeft();
//		int y = mImageView.getTop();
//		Matrix m = mImageView.getImageMatrix();
		return new Rect(mMarginX, mMarginY, mMarginX+mLayoutWidth, mLayoutHeight+mMarginY);
	}
}
