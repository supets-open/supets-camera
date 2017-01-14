package cn.jingling.lib.textbubble;

import java.util.ArrayList;



import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.widget.RelativeLayout;



//public interface OnAddingListener {
//	public ImageControl onAdding(Bitmap bitmap, Object Param);
//}


public abstract class AddingEffect extends Effect {
	protected ScreenControl mScreenControl = ScreenControl.getSingleton();
	protected GroundImage mGroundImage = mScreenControl.getGroundImage();
	
//	protected int mToastID = R.string.accessoriesToast;

	protected Context mContext = mGroundImage.getImageView().getContext();
	protected RelativeLayout mRelativeLayout;
	
//	protected RelativeLayout mRelativeLayout = (RelativeLayout) ((Activity) mContext)
//			.findViewById(R.id.screenLayout);

	protected static ArrayList<ImageControl> mImageList = new ArrayList<ImageControl>();
	
	protected PwMotion mEvent = new PwMotionHigh();
	
	protected static ImageControl mFrame = null;

	@Override
	public void perform() {

		mGroundImage.initializeData();
		mGroundImage.setFlagZoom(true);
		mGroundImage.setFlagMove(true);
		try {
//			if (mToastID != 0)
//				LayoutController.getSingleton().showToast(mToastID);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	};

	@Override
	public boolean onCancel() {
		// TODO Auto-generated method stub
		mScreenControl.clearAddingAccessory();
		mScreenControl.setmIsAddingAccessory(false);
		mScreenControl.setmIsAddingBlush(false);

		return true;
	}

	@Override
	public boolean onOk() {
		// TODO Auto-generated method stub
		if (mScreenControl.mImageControlArrayList.size() > 0) {
			mScreenControl.getBitmapHasAccessory();
			mScreenControl.clearAddingAccessory();
			mScreenControl.setmIsAddingAccessory(false);
			mScreenControl.setmIsAddingBlush(false);
			return true;
		}
		else
		{
//			ToastMaker.showToastShort(R.string.no_meterial_added);
		}
		return false;
	}

//	@Override
//	public ImageControl onAdding(Bitmap bitmap, Object Param) {
//		// TODO Auto-generated method stub
//		mScreenControl.addAccessory(bitmap);
//		return null;
//	}

	protected void merge(Canvas canvas) {

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);

		int size = mImageList.size();
		Matrix invertMatrix = getInvertMatrix();

		for (int i = 0; i < size; i++) {
			try {
				ImageControl imageControl = mImageList.get(i);
				Matrix matrix = imageControl.getImageMatrix();
				matrix.postConcat(invertMatrix);
				canvas.drawBitmap(imageControl.getBitmap(), matrix, paint);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	protected Matrix getInvertMatrix() {
		// TODO Auto-generated method stub
		Matrix matrix = new Matrix();
		mGroundImage.getImageMatrix().invert(matrix);
		return matrix;
	}

	protected void clear() {

		int size = mImageList.size();
		if (size == 0)
			return;

		mRelativeLayout.removeAllViews();
		mRelativeLayout.addView(mGroundImage.getImageView());

		for (int i = 0; i < size; i++) {
			try {
				ImageControl imageControl = mImageList.get(i);
				Bitmap bitmap = imageControl.getBitmap();
				if (imageControl != mGroundImage) {
					bitmap.recycle();
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		mImageList.clear();
	}

}
