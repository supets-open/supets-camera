package cn.jingling.lib.textbubble;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

public class RectImage extends ImageControl {

	// public RectImage( ImageView imageView, Bitmap bitmap ){
	// super( imageView, bitmap );
	// }
	private Bitmap rawBitmap;
	private Matrix mPreTransMatrix;

	public RectImage(Context context, Bitmap bitmap, Matrix matrix) {
		super(context, bitmap, matrix);
		rawBitmap = bitmap;
	}

	public void updatePositions(double x0, double x1, double y0, double y1) {

		double width = x1 - x0;
		double height = y1 - y0;

		double widthScale = 1.0 * width / bmpWidth;
		double heightScale = 1.0 * height / bmpHeight;

		double dx = x0;
		double dy = y0;

		mTransformMatrix = new Matrix();

		mTransformMatrix.postScale((float) widthScale, (float) heightScale);
		mTransformMatrix.postTranslate((float) dx, (float) dy);

		mImageView.setImageMatrix(mTransformMatrix);

		mImageView.invalidate();
	}

	public void updatePositions(MyPoint myPoint, MyPoint myPoint2, double sideLen) {
		// TODO Auto-generated method stub
	//	reset();
		mTransformMatrix = new Matrix();
	//	mPreTransMatrix = new Matrix();
		
		double widthScale = 1.0 * sideLen / bmpWidth;
		double heightScale = 1.0 * sideLen / bmpHeight;

		mTransformMatrix.postTranslate((float) myPoint2.x, (float) myPoint2.y);
	//	mPreTransMatrix.preTranslate((float) myPoint2.x, (float) myPoint2.y);
		
		mTransformMatrix.postScale((float) widthScale, (float) heightScale,
				myPoint2.x, myPoint2.y);
//		mPreTransMatrix.preScale((float) widthScale, (float) heightScale,
//				myPoint2.x, myPoint2.y);
		
		double angle = PointsCaculation.caculateTwoPointsAngle(myPoint2.x, 0,
				myPoint2.x - myPoint.x, myPoint2.y - myPoint.y);
		
		mTransformMatrix.postRotate((float) angle, myPoint2.x, myPoint2.y);
	//	mPreTransMatrix.preRotate((float) angle, myPoint2.x, myPoint2.y);
		
		mImageView.setImageMatrix(mTransformMatrix);

		mImageView.invalidate();
	}

	private void reset() {
		// TODO Auto-generated method stub
		mImageView.setImageMatrix(mPreTransMatrix);
		mImageView.invalidate();
	}
}
