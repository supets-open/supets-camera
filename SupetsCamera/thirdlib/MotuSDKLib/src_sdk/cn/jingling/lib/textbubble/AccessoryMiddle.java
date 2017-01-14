package cn.jingling.lib.textbubble;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class AccessoryMiddle extends ImageControl {
	// private static final double EXTEND_DIST = 20;
	// private static final float WIDTH_BOUND_DIST =
	// ScreenControl.getSingleton().mLayoutWidth / 8;
	// private static final float HEIGHT_BOUND_DIST =
	// ScreenControl.getSingleton().mLayoutHeight / 6;
	//
	// private static final float EXTEND_STEP = 15;

	public AccessoryMiddle(ImageView imageView, Bitmap bitmap) {
		super(imageView, bitmap);

		setFlagRotate(false);
		setFlagZoom(false);
	}

	public boolean initializeData() {

		return super.initializeData();

	}

	public void show(int viewId) {
		ImageControl imageControl = ScreenControl.getSingleton()
				.getmImageControlArrayList().get(viewId);
		show(imageControl);
	}
	
    public void show(ImageControl imageControl) {
        if (imageControl == null) {
            return;
        }

        float width = imageControl.bmpWidth;
        float height = imageControl.bmpHeight;

        MyPoint point = new MyPoint(width / 2, height / 2);

        point = point.givePointAfterTransform(imageControl.mTransformMatrix);

        float x = point.x;
        float y = point.y;

        x -= bmpWidth / 2;
        y -= bmpHeight / 2;

        mTransformMatrix.reset();
        mTransformMatrix.postTranslate((float) x, (float) y);

        mImageView.setImageMatrix(mTransformMatrix);

        int count = ScreenControl.getSingleton().getmRelativeLayout()
                .getChildCount();

        int index = ScreenControl.getSingleton().getmRelativeLayout()
                .indexOfChild(mImageView);

        if (index >= 0 && index < count) {
            mImageView.bringToFront();
        } else {
            ScreenControl.getSingleton().getmRelativeLayout()
                    .addView(mImageView);
        }

        mImageView.invalidate();
    }

	public void hide() {

		int count = ScreenControl.getSingleton().getmRelativeLayout()
				.getChildCount();

		int index = ScreenControl.getSingleton().getmRelativeLayout()
				.indexOfChild(mImageView);

		if (index >= 0 && index < count) {
			ScreenControl.getSingleton().getmRelativeLayout()
					.removeView(mImageView);
		}
	}
}
