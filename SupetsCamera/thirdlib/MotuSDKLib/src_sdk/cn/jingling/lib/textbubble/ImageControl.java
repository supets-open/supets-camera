package cn.jingling.lib.textbubble;

import cn.jingling.lib.utils.LogUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
	

public class ImageControl {

	protected ImageView mImageView = null;
	protected Bitmap mBitmap = null;
	protected Matrix mTransformMatrix = null;

	protected Boolean mFlagMove = true;
	protected Boolean mFlagZoom = true;
	protected Boolean mFlagRotate = true;

	public int bmpWidth, bmpHeight;
	protected static int mLayoutWidth;
	protected static int mLayoutHeight;
	protected static int mMarginX;
	protected static int mMarginY;
	
	private LayoutParams mTopMenuLayout;
	private LayoutParams mBottomMenuLayout;

	public float mScale = 1.0f;
	// public MyPoint mTranslate = new MyPoint(0.0f,0.0f);
	// public MyPoint mRotate = new MyPoint(1.0f, 0.0f); //cos & sin

	public float mMaxScaleLimit;
	public float mMinScaleLimit;
	public float mMaxOneStepScaleLimit = 1.05f;
	public float mMinOneStepScaleLimit = 0.95f;
	private int mAlpha = 255;
	
	private boolean mControlEnabled = true;

	public ImageControl(Context context, Bitmap bitmap, Matrix matrix) {

		try {
			//int topMenuHeight = 
//			mLayoutHeight = ScreenInfo.getScreenHeight();
//			mLayoutWidth = ScreenInfo.getScreenWidth();
//			14:27
//			LayoutController layoutController = LayoutController.getSingleton();
//			mTopMenuLayout = layoutController.getTopMenuLayout().getLayoutParams();
//			mBottomMenuLayout = layoutController.getBottomLayout().getLayoutParams();
			
//			int margin = context.getResources().getDimensionPixelSize(R.dimen.imageViewMargin);
//			int marginTop = context.getResources().getDimensionPixelSize(R.dimen.imageViewMarginVertical);

			int margin = 0;
			int marginTop = 0;
			
			mMarginX = margin;
			mMarginY = marginTop;

			mLayoutHeight = ScreenInfo.getScreenHeight() 
					- mTopMenuLayout.height
					- mBottomMenuLayout.height
					- marginTop;
			
			mLayoutWidth = ScreenInfo.getScreenWidth() - 2*margin;
			
			LayoutParams params = new LayoutParams(mLayoutWidth, mLayoutHeight);

			mImageView = new ImageView(context);

			mImageView.setLayoutParams(params);
			mImageView.setImageBitmap(bitmap);
			mImageView.setScaleType(ScaleType.MATRIX);

			if (mBitmap != bitmap) {
				releaseBitmap();
			}
			mBitmap = bitmap;
			bmpWidth = mBitmap.getWidth();
			bmpHeight = mBitmap.getHeight();

			mImageView.invalidate();

			init(matrix);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	

	public void init(Matrix matrix) {
		mTransformMatrix = matrix;
		mImageView.setImageMatrix(mTransformMatrix);
	}

	
	public ImageControl(ImageView imageView, Bitmap bitmap, int width, int height) {
//		mLayoutHeight = ScreenInfo.getScreenHeight();
//		mLayoutWidth = ScreenInfo.getScreenWidth();
//		14:27 comment
//		LayoutController layoutController = LayoutController.getSingleton();
//		mTopMenuLayout = layoutController.getTopMenuLayout().getLayoutParams();
//		mBottomMenuLayout = layoutController.getBottomLayout().getLayoutParams();
		Context context = imageView.getContext();
//		int margin = context.getResources().getDimensionPixelSize(R.dimen.imageViewMargin);
//		int marginTop = context.getResources().getDimensionPixelSize(R.dimen.imageViewMarginVertical);

		int margin = 0;
		int marginTop = 0;
		
		mMarginX = margin;
		mMarginY = marginTop;

//		mLayoutHeight = ScreenInfo.getScreenHeight();
				
//		mLayoutWidth = ScreenInfo.getScreenWidth();
		
		mLayoutHeight = height;
		mLayoutWidth = width;
		
		LogUtils.d("imagecontrol", "111 bitmap " + bitmap + "construct mlayoutheight " + mLayoutHeight + "width " + mLayoutWidth);
		
		

		mImageView = imageView;
		if (mBitmap != bitmap) {
			releaseBitmap();
		}
		mBitmap = bitmap;
		
//		mLayoutHeight = mImageView.getHeight();
//		mLayoutWidth = mImageView.getWidth();

		mImageView.setScaleType(ScaleType.MATRIX);
		initializeData();
	}
	
	
	public ImageControl(ImageView imageView, Bitmap bitmap) {
//		mLayoutHeight = ScreenInfo.getScreenHeight();
//		mLayoutWidth = ScreenInfo.getScreenWidth();
//		14:27 comment
//		LayoutController layoutController = LayoutController.getSingleton();
//		mTopMenuLayout = layoutController.getTopMenuLayout().getLayoutParams();
//		mBottomMenuLayout = layoutController.getBottomLayout().getLayoutParams();
		Context context = imageView.getContext();
//		int margin = context.getResources().getDimensionPixelSize(R.dimen.imageViewMargin);
//		int marginTop = context.getResources().getDimensionPixelSize(R.dimen.imageViewMarginVertical);

		int margin = 0;
		int marginTop = 0;
		
		mMarginX = margin;
		mMarginY = marginTop;

//		mLayoutHeight = ScreenInfo.getScreenHeight();
				
//		mLayoutWidth = ScreenInfo.getScreenWidth();
		
		mLayoutHeight = imageView.getHeight();
		mLayoutWidth = imageView.getWidth();
		LogUtils.d("imagecontrol", "222 bitmap " + bitmap + "construct mlayoutheight " + mLayoutHeight + "width " + mLayoutWidth);
		
		

		mImageView = imageView;
		if (mBitmap != bitmap) {
			releaseBitmap();
		}
		mBitmap = bitmap;
		
//		mLayoutHeight = mImageView.getHeight();
//		mLayoutWidth = mImageView.getWidth();

		mImageView.setScaleType(ScaleType.MATRIX);
		initializeData();
	}
	public ImageControl(ImageView imageView, Bitmap bitmap,Point p) {
//		mLayoutHeight = ScreenInfo.getScreenHeight();
//		mLayoutWidth = ScreenInfo.getScreenWidth();
		
//		LayoutController layoutController = LayoutController.getSingleton();
//		mTopMenuLayout = layoutController.getTopMenuLayout().getLayoutParams();
//		mBottomMenuLayout = layoutController.getBottomLayout().getLayoutParams();
		Context context = imageView.getContext();
//		int margin = context.getResources().getDimensionPixelSize(R.dimen.imageViewMargin);
//		int marginTop = context.getResources().getDimensionPixelSize(R.dimen.imageViewMarginVertical);

		int margin = 0;
		int marginTop = 0;
		
		mMarginX = margin;
		mMarginY = marginTop;

		mLayoutHeight = ScreenInfo.getScreenHeight() 
				- mTopMenuLayout.height
				- mBottomMenuLayout.height
				- marginTop;
		
		mLayoutWidth = ScreenInfo.getScreenWidth() - 2*margin;

		mImageView = imageView;
		if (mBitmap != bitmap) {
			releaseBitmap();
		}
		mBitmap = bitmap;
		
//		mLayoutHeight = mImageView.getHeight();
//		mLayoutWidth = mImageView.getWidth();

		mImageView.setScaleType(ScaleType.MATRIX);
		initializeData(p);
	}
	
	public boolean initializeData(Point p) {
		return true;
	}
	public boolean initializeData() {

		try {
			mImageView.setImageBitmap(mBitmap);

			bmpWidth = mBitmap.getWidth();
			bmpHeight = mBitmap.getHeight();
			mTransformMatrix = new Matrix();
			
			LayoutParams params = (mImageView.getLayoutParams());

			params.height = mLayoutHeight;
			params.width = mLayoutWidth;

			mImageView.setLayoutParams(params);

			mImageView.invalidate();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public void refresh() {
		mImageView.postInvalidate();
	}

	public void setFlagMove(Boolean flagMove) {
		mFlagMove = flagMove;
	}

	public void setFlagZoom(Boolean flagZoom) {
		mFlagZoom = flagZoom;
	}

	public void setFlagRotate(Boolean flagRotate) {
		mFlagRotate = flagRotate;
	}

	public Matrix getImageMatrix() {
		return mTransformMatrix;
	}

	public ImageView getImageView() {
		return mImageView;
	}

	public void setBitmap(Bitmap bitmap) {
//			releaseBitmap();
		mBitmap = bitmap;
		bmpWidth = mBitmap.getWidth();
		bmpHeight = mBitmap.getHeight();
		mImageView.setImageBitmap(mBitmap);
		mImageView.invalidate();
	}
	
	public void setAlpha(int alpha) {
		mAlpha = alpha;
		mImageView.setAlpha(alpha);
	}
	
	public int getAlpha() {
		return mAlpha;
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}
	
	private boolean checkMatrix(Matrix matrix) {
		float[] values= new float[9];
		matrix.getValues(values);
		// 图片4个顶点的坐标
        float x1 = values[0] * 0 + values[1] * 0 + values[2];  
        float y1 = values[3] * 0 + values[4] * 0 + values[5];  
        float x2 = values[0] * bmpWidth + values[1] * 0 + values[2];  
        float y2 = values[3] * bmpWidth + values[4] * 0 + values[5];  
        float x3 = values[0] * 0 + values[1] * bmpHeight + values[2];  
        float y3 = values[3] * 0 + values[4] * bmpHeight + values[5];  
        float x4 = values[0] * bmpWidth + values[1] * bmpHeight + values[2];  
        float y4 = values[3] * bmpWidth + values[4] * bmpHeight + values[5];  

        // 出界判断  
        int range = 40;
        if ((x1 < range && x2 < range && x3 < range && x4 < range)  
            || (x1 > mLayoutWidth-range && x2 > mLayoutWidth-range && x3 > mLayoutWidth-range && x4 > mLayoutWidth-range)  
            || (y1 < range && y2 < range && y3 < range && y4 < range)  
            || (y1 > mLayoutHeight-range && y2 > mLayoutHeight-range && y3 > mLayoutHeight-range && y4 > mLayoutHeight-range)) {  
            return true;  
        }  
		return false;
	}
	 
	public void changeImageViewHasRange(Matrix moveMatrix, Matrix zoomMatrix,
			Matrix rotateMatrix) {
		
		Matrix matrix = new Matrix();
		matrix.set(mTransformMatrix);
		matrix.postConcat(zoomMatrix);
		matrix.postConcat(moveMatrix);
		matrix.postConcat(rotateMatrix);
		
		boolean tempFlag = checkMatrix(matrix);
		
		if(tempFlag == false){
			mTransformMatrix.postConcat(zoomMatrix);
			mTransformMatrix.postConcat(moveMatrix);
			mTransformMatrix.postConcat(rotateMatrix);
			
			mImageView.setImageMatrix(mTransformMatrix);
			mImageView.invalidate();
		}
	}

	public void changeImageView(Matrix moveMatrix, Matrix zoomMatrix,
			Matrix rotateMatrix) {

		mTransformMatrix.postConcat(zoomMatrix);
		mTransformMatrix.postConcat(moveMatrix);
		mTransformMatrix.postConcat(rotateMatrix);
		
		mImageView.setImageMatrix(mTransformMatrix);

		mImageView.invalidate();

	}
	
	public MyPoint getMidPoint()
	{
		MyPoint midPoint = new MyPoint();
		midPoint.set(bmpWidth / 2, bmpHeight / 2);
		midPoint = midPoint.givePointAfterTransform(mTransformMatrix);
		return midPoint;
	}
	
	public void setImageViewMatrix(Matrix matrix) {

		mTransformMatrix.set(matrix);

		mImageView.setImageMatrix(mTransformMatrix);

		mImageView.invalidate();

	}

	public void translateImageView(float dx, float dy) {
		mTransformMatrix.postTranslate(dx, dy);
	}

	public void rotateImageView(float sinValue, float cosValue) {
		MyPoint point = new MyPoint(bmpWidth / 2, bmpHeight / 2);
		point = point.givePointAfterTransform(mTransformMatrix);

		Matrix matrix = new Matrix();
		matrix.setSinCos(sinValue, cosValue, point.x, point.y);
		mTransformMatrix.postConcat(matrix);
	}

	public void inValidateImageView() {
		mImageView.setImageMatrix(mTransformMatrix);
		mImageView.invalidate();
	}
	
	public void updateImageView(float dx, float dy, float scale,
			boolean willRotate, MyPoint oldFirstPointer, MyPoint newFirstPointer) {
		updateImageView(dx,dy,scale,willRotate,oldFirstPointer,newFirstPointer,false,0);
	}

	public void updateImageView(float dx, float dy, float scale,
			boolean willRotate, MyPoint oldFirstPointer, MyPoint newFirstPointer,boolean isDoAll,double rotate) {

		Matrix moveMatrix = new Matrix();
		Matrix zoomMatrix = new Matrix();
		Matrix rotateMatrix = new Matrix();

		if (mFlagZoom == true) {
			MyPoint point = new MyPoint(bmpWidth / 2, bmpHeight / 2);
			point = point.givePointAfterTransform(mTransformMatrix);
			zoomMatrix.postScale((float) scale, (float) scale, (float) point.x,
					(float) point.y);
		}

		if (mFlagRotate == true && willRotate == true) {
			MyPoint midPoint = new MyPoint(bmpWidth / 2, bmpHeight / 2);
			midPoint = midPoint.givePointAfterTransform(mTransformMatrix);

			MyPoint angle = MyPoint.getSinCos(oldFirstPointer, newFirstPointer,
					midPoint);
			rotateMatrix.setSinCos(angle.x, angle.y, midPoint.x, midPoint.y);

		} else if (mFlagMove == true) {
			moveMatrix.postTranslate((float) dx, (float) dy);
		}

		changeImageView(moveMatrix, zoomMatrix, rotateMatrix);
	}

	public Boolean isContainPoint(MyPoint point, int bound) {

		MyPoint p = point.givePointBeforTransform(mTransformMatrix);
//		for (int x = (int) (p.x) - bound; x <= p.x + bound; x++) {
//			for (int y = (int) (p.y) - bound; y <= p.y + bound; y++) {
//
//				if (x >= 0 && x < bmpWidth && y >= 0 && y < bmpHeight) {
//					int color = mBitmap.getPixel(x, y);
//					int alpha = Color.alpha(color);
//					if (alpha != 0) {
//						return true;
//					}
//				}
//			}
//		}
		if (p.x >= 0 && p.x < bmpWidth && p.y >= 0 && p.y < bmpHeight) {
			return true;
		}

		return false;
	}

	public void rebound() {

		/**
		 * the values of the transform matrix [scale * cos( alpha ) -scale *
		 * sin( alpha ) dx; scale * sin( alpha ) scale * cos( alpha ) dy; 0 0
		 * 1.0]
		 */

		float values[] = new float[9];
		mTransformMatrix.getValues(values);

		double xscale = values[0];
		double yscale = values[4];

		double dx = values[2];
		double dy = values[5];

		if (dx > 0 && xscale * bmpWidth + dx > mLayoutWidth) {
			if (xscale * bmpWidth > mLayoutWidth) {
				dx = 0;
			} else {
				dx = (mLayoutWidth - bmpWidth * xscale) / 2;
			}
		}

		if (dx < 0 && xscale * bmpWidth + dx < mLayoutWidth) {
			if (xscale * bmpWidth > mLayoutWidth) {
				dx = mLayoutWidth - xscale * bmpWidth;
			} else {
				dx = (mLayoutWidth - bmpWidth * xscale) / 2;
			}
		}

		if (dy > 0 && yscale * bmpHeight + dy > mLayoutHeight) {
			if (yscale * bmpHeight > mLayoutHeight) {
				dy = 0;
			} else {
				dy = (mLayoutHeight - bmpHeight * yscale) / 2;
			}
		}

		if (dy < 0 && yscale * bmpHeight + dy < mLayoutHeight) {
			if (yscale * bmpHeight > mLayoutHeight) {
				dy = mLayoutHeight - yscale * bmpHeight;
			} else {
				dy = (mLayoutHeight - bmpHeight * yscale) / 2;
			}
		}

		if (dx > 0 && xscale * bmpWidth + dx < mLayoutWidth) {
			dx = (mLayoutWidth - bmpWidth * xscale) / 2;
		}

		if (dy > 0 && yscale * bmpHeight + dy < mLayoutHeight) {
			dy = (mLayoutHeight - bmpHeight * yscale) / 2;
		}

		values[0] = (float) xscale;
		values[4] = (float) yscale;
		values[2] = (float) dx;
		values[5] = (float) dy;

		mTransformMatrix.setValues(values);

		inValidateImageView();

	}
	
	protected MyRect getImageRect() {
		MyRect rect = new MyRect();
		rect.p1 = new MyPoint(0, 0);
		rect.p2 = new MyPoint(0, bmpHeight);
		rect.p3 = new MyPoint(bmpWidth, bmpHeight);
		rect.p4 = new MyPoint(bmpWidth, 0);
		return rect.giveRectAfterTransform(mTransformMatrix);
	}
	
	public void release()
	{
		mImageView.setImageBitmap(null);
		mImageView = null;
		releaseBitmap();
	}
	
	public void releaseBitmap()
	{
		if(mBitmap !=null)
		{
			mBitmap.recycle();
			mBitmap = null;
		}
	}

    public boolean isControlEnabled() {
        return mControlEnabled;
    }

    public void setControlEnabled(boolean enabled) {
        this.mControlEnabled = enabled;
    }

}
