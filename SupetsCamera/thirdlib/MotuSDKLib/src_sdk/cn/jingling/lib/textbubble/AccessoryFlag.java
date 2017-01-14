package cn.jingling.lib.textbubble;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;


public class AccessoryFlag extends ImageControl {
	private final static String TAG = "AccessoryFlag";

	private ImageView mWrappedSquare;
	private Bitmap mWrappedBitmap;
	private ImageControl mBtnDel;

	public boolean mIsVisible = true;

	private RelativeLayout mRelativeLayout = ScreenControl.getSingleton()
			.getmRelativeLayout();


	public AccessoryFlag(ImageView btnRot, Bitmap bmRot, ImageView btnDel, Bitmap bmDel) {

		super(btnRot, bmRot);
		mBtnDel = new ImageControl(btnDel, bmDel, mRelativeLayout.getWidth(), mRelativeLayout.getHeight());
		mBtnDel.setFlagRotate(false);
		mBtnDel.setFlagZoom(false);
		mRelativeLayout.addView(mBtnDel.mImageView);
		mBtnDel.mImageView.setVisibility(View.INVISIBLE);

		setFlagRotate(false);
		setFlagZoom(false);
	}

	
	public AccessoryFlag(ImageView btnRot, Bitmap bmRot, ImageView btnDel, Bitmap bmDel, int width, int height) {

		super(btnRot, bmRot, width, height);
		mBtnDel = new ImageControl(btnDel, bmDel, width, height);
		mBtnDel.setFlagRotate(false);
		mBtnDel.setFlagZoom(false);
		mRelativeLayout.addView(mBtnDel.mImageView);
		mBtnDel.mImageView.setVisibility(View.INVISIBLE);

		setFlagRotate(false);
		setFlagZoom(false);
	}
	
	
	public boolean initializeData() {

		return super.initializeData();
	}
	
	public boolean isContainPointDel(MyPoint point, int bound) {
		if (mBtnDel.getImageView().getVisibility() == View.VISIBLE) {
			return mBtnDel.isContainPoint(point, bound);
		} else {
			return false;
		}
	}

	public void toggleVisibility() {
		mIsVisible = mIsVisible ? false : true;
		refreshVisibility();
	}

	private void refreshVisibility() {
		if (mIsVisible) {
			show(ScreenControl.getSingleton().mImageControlArrayList.size() - 1);
		} else {
			hide();
		}
	}

	public void show(int viewId) {
		Pwog.d(TAG, "show on " + String.valueOf(viewId));
		ScreenControl.getSingleton().currentShow = viewId;
		mIsVisible = true;
		ImageControl imageControl = ScreenControl.getSingleton()
				.getmImageControlArrayList().get(viewId);
		drawWrappedSquare(imageControl);
		drawBtnDel(imageControl);

		double x = imageControl.getImageRect().p3.x;
		double y = imageControl.getImageRect().p3.y;
		x -= bmpWidth / 2;
		y -= bmpHeight / 2;

		mTransformMatrix.reset();
		mTransformMatrix.postTranslate((float) x, (float) y);

		// Log.d( TAG, "matrix=" + mTransformMatrix.toString() );

		mImageView.setImageMatrix(mTransformMatrix);

		int count = mRelativeLayout.getChildCount();

		int index = mRelativeLayout.indexOfChild(mImageView);

		if (index >= 0 && index < count) {
			mImageView.bringToFront();
		} else {
			mRelativeLayout.addView(mImageView);
		}

		mImageView.invalidate();
	}
	
    public void show(ImageControl imageControl) {
        List<ImageControl> list = ScreenControl.getSingleton().getmImageControlArrayList();
        if (list == null || list.size() <= 0) {
            return;
        }
        int viewId = list.indexOf(imageControl);
        if (viewId >= 0 && viewId < list.size()) {
            show(viewId);
        }
    }

	public void hide() {
		ScreenControl.getSingleton().currentShow = -1;
		int count = mRelativeLayout.getChildCount();

		int index = mRelativeLayout.indexOfChild(mImageView);

		if (index >= 0 && index < count) {
			mRelativeLayout.removeView(mImageView);
		}
		hideWrappedSquare();
		hideBtnDel();
	}
	
	private void drawBtnDel(ImageControl ic) {
		float[] values = new float[9];
		ic.getImageMatrix().getValues(values);
		String tags = "";
		for (int i = 0; i < 9; i++) {
			tags += String.valueOf(values[i]) + "  ";
		}
		Pwog.d(TAG, "matrix: " + tags);
		Matrix mat = new Matrix();
		mat.reset();
		mat.postTranslate((int)values[2] - mBtnDel.bmpWidth / 2, (int)values[5] - mBtnDel.bmpHeight / 2);
		mBtnDel.setImageViewMatrix(mat);
		mBtnDel.mImageView.bringToFront();
		mBtnDel.mImageView.setVisibility(View.VISIBLE);
	}

	private Paint getWrappedSquarePaint() {
		Paint p = new Paint();
		p.setDither(true);
		p.setAntiAlias(true);
		p.setStyle(Style.STROKE);
		p.setColor(Color.argb(216, 255, 255, 255));
		p.setStrokeWidth(8);
		return p;
	}
	private void drawWrappedSquare(ImageControl ic) {
		if (mWrappedSquare == null) {
			mWrappedSquare = new ImageView(mImageView.getContext());
			LayoutParams params = new LayoutParams(mLayoutWidth, mLayoutHeight);
			mWrappedSquare.setLayoutParams(params);
			mWrappedSquare.setScaleType(ScaleType.MATRIX);
			mWrappedBitmap = Bitmap.createBitmap(ic.bmpWidth, ic.bmpHeight,
					Bitmap.Config.ARGB_4444);
			Canvas c = new Canvas(mWrappedBitmap);
			c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			Paint p = getWrappedSquarePaint();
			c.drawRect(new Rect(0, 0, ic.bmpWidth - 1, ic.bmpHeight - 1), p);
			mWrappedSquare.setImageBitmap(mWrappedBitmap);
			mRelativeLayout.addView(mWrappedSquare);
		} else {
			mWrappedBitmap = Bitmap.createBitmap(ic.bmpWidth, ic.bmpHeight,
					Bitmap.Config.ARGB_4444);
			Canvas c = new Canvas(mWrappedBitmap);
			c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			float[] values = new float[9];
			ic.mTransformMatrix.getValues(values);
			float scale = (float) Math.sqrt(values[0] * values[0] + values[1]
					* values[1]);
			Paint p = getWrappedSquarePaint();
			p.setStrokeWidth(8 / scale);
			c.drawRect(new Rect(0, 0, ic.bmpWidth - 1, ic.bmpHeight - 1), p);
			mWrappedSquare.setImageBitmap(mWrappedBitmap);
		}
		mWrappedSquare.setImageMatrix(ic.mTransformMatrix);
		mWrappedSquare.bringToFront();
		mWrappedSquare.setVisibility(View.VISIBLE);
	}

	private void hideWrappedSquare() {
		if (mWrappedSquare != null) {
			mWrappedSquare.setVisibility(View.GONE);
		}
	}
	
	private void hideBtnDel() {
		if (mBtnDel != null) {
			mBtnDel.mImageView.setVisibility(View.GONE);
		}
	}
}