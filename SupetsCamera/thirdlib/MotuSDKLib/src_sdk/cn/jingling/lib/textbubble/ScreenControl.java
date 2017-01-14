package cn.jingling.lib.textbubble;

import java.util.ArrayList;
import java.util.List;

import cn.jingling.lib.textbubble.TextBubbleWidget.onEditingCallback;
import cn.jingling.lib.utils.LogUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class ScreenControl implements OnTouchListener {

	private static final String TAG = "ScreenControl";
	public final int PRESSED = 1;
	public final int UP = 2;
	public final int MID = 3;
	

	private static ScreenControl mSingleton = null;

	public GroundImage mGroundImage = null;
//	private ImageControl previewImage = null;
//	private BeautifyRoundView mBeautifyRoundView = null;

	public ArrayList<ImageControl> mImageControlArrayList = null;
	public AccessoryFlag mTextAccessoryFlag = null;

	private Activity mActivity = null;

	public RelativeLayout mRelativeLayout = null;
//	private CropView cropView = null;
//	private CropSymbolImage[] cropSymbolImages = null;
	
	public AccessoryFlag mAccessoryFlag = null;
	public AccessoryMiddle mAccessoryMiddle = null;

	public Boolean mIsAddingAccessory = false;
	public Boolean mIsAddingText = false;
	public Boolean mIsAddingFrame = false;
	public Boolean mIsAddingBlush = false;
	public Boolean mIsShape = false;
	public Boolean mIsShapeShow = true;
	private Boolean mIsMouseDown = false;
	private Boolean mIsShapeShowWhenDown = false;
	
	private Boolean mIsMosaic = false;


	public Boolean mIsEffectMode = false;

	public Boolean mActionIsUp = false;
//	public Effect mCurEffect;
	
	public int currentShow;
	
//	public InkCanvas mMosaicInkCanvas;
//	public InkCanvas mInkCanvas;
//	public BitmapInkCanvas mBitmapInkCanvas;

	public static int mLayoutWidth;
	public static int mLayoutHeight;

	public static final int FINGER_BOUND = 20;

	public Matrix mLastMatrix;
	
	private GestureDetector mGestureDetector = null;

	
	private MyPoint mFirstPoint;
	private boolean mIsBigMove = false;

	private int addValue = 0;
	
	private int mTextDeleteLimit;
	
	private ScreenControlCallback mScreenControlCallback = null;

	protected static PwMotion mEvent = new PwMotionHigh();

	
//	private int mAccessoryoperater = R.drawable.accessoryoperater;
//	private int	mAccessory_del = R.drawable.i_accessory_del;
	
	private int mAccessoryoperater;
	private int	mAccessory_del;
	
	
	private onEditingCallback mOnEditingCallback;
	
	static public ScreenControl getSingleton() {
		if (mSingleton == null) {
			mSingleton = new ScreenControl();
		}
		return mSingleton;
	}

	public void initWithActivity(Activity activity, Bitmap bitmap, ImageView imageView, RelativeLayout imageContainer) {
//		mLayoutWidth = ScreenInfo.getScreenWidth();
//		mLayoutHeight = ScreenInfo.getScreenHeight();

		mLayoutWidth = imageContainer.getWidth();
		mLayoutHeight = imageContainer.getHeight();
		LogUtils.d(TAG, " screen contrl layoutwidth " + mLayoutWidth + " layoutheight " + mLayoutHeight);
		
		mActivity = activity;
//		ImageView view = new ImageView(mActivity);
		
//		ImageView view = (ImageView) mActivity.findViewById(R.id.image);
		
		if (bitmap == null) {
			cn.jingling.lib.utils.LogUtils.e(TAG, "Bitmap NULL");
		}
		mGroundImage = new GroundImage(imageView, bitmap);

//		ImageView previewImageView = new ImageView(mActivity);
		
//		ImageView previewImageView = (ImageView)mActivity.findViewById(R.id.previewImage);
//		previewImage = new GroundImage(previewImageView, mGroundImage.getBitmap());
		
		mRelativeLayout = imageContainer;

		mGroundImage.initializeData();
//		mRelativeLayout.removeView(previewImage.getImageView());
		
		mGestureDetector = mGroundImage.getGestureDetector();
		obtainControl();
		
//		mTextDeleteLimit = (int) (ScreenControl.mLayoutHeight
//				- mActivity.getResources().getDimension(R.dimen.top_menu_height)
//				-mActivity.getResources().getDimension(R.dimen.bottom_menu_height));
	}

	public void obtainControl() {
		try {
			mGroundImage.initializeData();
			mGroundImage.getImageView().setOnTouchListener(this);
			mGestureDetector.setOnDoubleTapListener(mGroundImage);
			mGestureDetector.setIsLongpressEnabled(false);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public GestureDetector getGestureDetector() {
		return mGestureDetector;
	}

	public void initializeAddingAccessory() {
		initializeAccessoryFlag(mAccessoryoperater, mAccessory_del);
		initializeAccessoryMiddle();
	}

	private void initializeAccessoryFlag(int accessoryoperater, int i_accessory_del) {

		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		ImageView viewMove = new ImageView(mActivity);
		viewMove.setLayoutParams(params);
		viewMove.setScaleType(ImageView.ScaleType.MATRIX);
		Drawable picMove = mActivity.getResources().getDrawable(
				accessoryoperater);
		Bitmap bitmapMove = ((BitmapDrawable) picMove).getBitmap();
		
		ImageView btnDel = new ImageView(mActivity);
		btnDel.setLayoutParams(params);
		btnDel.setScaleType(ImageView.ScaleType.MATRIX);
		Drawable picDel = mActivity.getResources().getDrawable(
				i_accessory_del);
		Bitmap bmDel = ((BitmapDrawable) picDel).getBitmap();

		mAccessoryFlag = new AccessoryFlag(viewMove, bitmapMove, btnDel, bmDel);
	}

	private void initializeAccessoryMiddle() {

		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		ImageView imageView = new ImageView(mActivity);
		imageView.setLayoutParams(params);
		imageView.setScaleType(ImageView.ScaleType.MATRIX);

		int width = mLayoutWidth / 30;

		if (width % 2 == 0) {
			width += 1;
		}

		int height = width;

		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = Bitmap.createBitmap(width, height, config);

		for (int i = 0; i < height; i++) {

			bitmap.setPixel(width / 2, i, 0x80000000);
			bitmap.setPixel(width / 2 - 1, i, 0x80000000);
			bitmap.setPixel(width / 2 + 1, i, 0x80000000);

		}

		for (int i = 0; i < width; i++) {
			bitmap.setPixel(i, height / 2, 0x80000000);
			bitmap.setPixel(i, height / 2 - 1, 0x80000000);
			bitmap.setPixel(i, height / 2 + 1, 0x80000000);

		}

		mAccessoryMiddle = new AccessoryMiddle(imageView, bitmap);
	}

	/**
	 * return AccessoryImage object for convient reference
	 * @param bitmap
	 * @return
	 */
	public AccessoryImage addAccessory(Bitmap bitmap) {

		// AccessoryImage accessoryImage = new AccessoryImage(mActivity, bitmap,
		// new Matrix());
		addValue += 40;
		if (addValue >= ScreenControl.getSingleton().getmGroundImage()
				.getImageView().getWidth() / 3) {
			addValue = bitmap.getWidth()
					/ 2
					- ScreenControl.getSingleton().getmGroundImage()
							.getImageView().getWidth() / 2;
		}
		ImageView imageView = new ImageView(mActivity);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(params);
		AccessoryImage accessoryImage = new AccessoryImage(imageView, bitmap);

		addView(imageView);
		mImageControlArrayList.add(accessoryImage);

		int arralyListSize = mImageControlArrayList.size();

		accessoryImage.translateImageView(addValue, addValue);
		mAccessoryFlag.show(arralyListSize - 1);
		mAccessoryMiddle.show(arralyListSize - 1);

		accessoryImage.inValidateImageView();

		firstId = -1;
		viewId = arralyListSize - 1;
		return accessoryImage;
	}
	
	/**
	 * 
	 * @param bitmap
	 * @param para
	 * @param flag 是否需要改变位置，正常状态全部传入false即可
	 * @return
	 */
	public TextBubbleControl addTextBubble(Bitmap bitmap, TextBubbleStyleParameter para) {
		addValue += 40;
		if (addValue >= ScreenControl.getSingleton().getmGroundImage()
				.getImageView().getWidth() / 3) {
			addValue = bitmap.getWidth()/ 2 - ScreenControl.getSingleton().getmGroundImage().getImageView().getWidth() / 2;
		}
		
		ImageView imageView = new ImageView(mActivity);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(params);
		TextBubbleControl bubbleControl = new TextBubbleControl(imageView, bitmap, mRelativeLayout.getWidth(), mRelativeLayout.getHeight(), para);

		addView(imageView);
		addView(bubbleControl.getTextBubble());
		mImageControlArrayList.add(bubbleControl);

		int arralyListSize = mImageControlArrayList.size();
		if(arralyListSize == 1){// 第一个
			int temp = (int) ((mLayoutHeight/2) * 0.4);
			bubbleControl.translateImageView(0, -temp);
			addValue = temp;
		}
		else{
			bubbleControl.translateImageView(addValue, addValue);
		}
		
		mTextAccessoryFlag.show(arralyListSize - 1);
		
		bubbleControl.inValidateImageView();
		
		
		firstId = -1;
		viewId = arralyListSize - 1;
		return bubbleControl;
	}


	


	public LoopForRemoveAcneCanvas addLoopForRemoveAcneCanvas() {
		LoopForRemoveAcneCanvas removeAcneCanvas = new LoopForRemoveAcneCanvas(
				mActivity);
		LayoutParams params = new LayoutParams(mLayoutWidth, mLayoutHeight);
		removeAcneCanvas.setLayoutParams(params);
		removeAcneCanvas.setImageMatrix(mGroundImage.getImageMatrix());
		addView(removeAcneCanvas);
		return removeAcneCanvas;
	}

	public void deleteAccessory(int arrayListId) {
		currentShow = -1;
		ImageControl imageControl = mImageControlArrayList.get(arrayListId);
		ImageView imageView = imageControl.mImageView;

//		mLastMatrix = imageView.getImageMatrix();

		mImageControlArrayList.remove(arrayListId);
		mRelativeLayout.removeView(imageView);

		initialAddingAccessoryState();
		mAccessoryFlag.hide();
		mAccessoryMiddle.hide();
		callBackAccessoryDeleted((AccessoryImage)imageControl);
	}
	
    public void deleteAccessory(ImageControl imageControl) {
        currentShow = -1;
        ImageView imageView = imageControl.mImageView;

        // mLastMatrix = imageView.getImageMatrix();

        if (mImageControlArrayList.remove(imageControl)) {
            mRelativeLayout.removeView(imageView);
        }

        initialAddingAccessoryState();
        mAccessoryFlag.hide();
        mAccessoryMiddle.hide();
        callBackAccessoryDeleted((AccessoryImage) imageControl);
    }

	public void initialAddingAccessoryState() {
		firstId = -1;
		viewId = -1;
	}


	public void removeCutCanvas() {
		mRelativeLayout.removeAllViews();
		mRelativeLayout.addView(getGroundImage().getImageView());
	}

	public void removeLoopForRemoveAcneCanvas() {
		mRelativeLayout.removeAllViews();
		mRelativeLayout.addView(getGroundImage().getImageView());
	}

	public void clearAddingAccessory() {
		try {
		    if (mAccessoryFlag != null) {
		        removeView(mAccessoryFlag.getImageView());
		        mAccessoryFlag = null;
		    }
		    if (mAccessoryMiddle != null) {
		        removeView(mAccessoryMiddle.getImageView());
		        mAccessoryMiddle = null;
		    }

			mRelativeLayout.removeAllViews();
			mRelativeLayout.addView(getGroundImage().getImageView());

			int arrayListSize = mImageControlArrayList.size();

			for (int i = 0; i < arrayListSize; i++) {
				ImageControl imageControl = mImageControlArrayList.get(i);
				imageControl.getBitmap().recycle();
			}

			mImageControlArrayList.clear();
			mImageControlArrayList = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initializeAddingText() {
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ImageView viewMove = new ImageView(mActivity);
		viewMove.setLayoutParams(params);
		viewMove.setScaleType(ImageView.ScaleType.MATRIX);
		Drawable picMove = mActivity.getResources().getDrawable(mAccessoryoperater);
		Bitmap bitmapMove = ((BitmapDrawable) picMove).getBitmap();
		
		ImageView btnDel = new ImageView(mActivity);
		btnDel.setLayoutParams(params);
		btnDel.setScaleType(ImageView.ScaleType.MATRIX);
		Drawable picDel = mActivity.getResources().getDrawable(mAccessory_del);
		Bitmap bmDel = ((BitmapDrawable) picDel).getBitmap();

		mTextAccessoryFlag = new AccessoryFlag(viewMove, bitmapMove, btnDel, bmDel, 
													mRelativeLayout.getWidth(),
													mRelativeLayout.getHeight());
	}

	public void deleteText(int arrayListId) {
		currentShow = -1;

		if (mIsEditingText) {
			mTextBoxId = -1;
			mIsKeepSelected = false;
			leaveEditing();
		}
		TextBubbleControl bubbleControl = (TextBubbleControl)mImageControlArrayList.get(arrayListId);
		ImageView imageView = bubbleControl.mImageView;

		mImageControlArrayList.remove(arrayListId);
		
		mRelativeLayout.removeView(imageView);
		mRelativeLayout.removeView(bubbleControl.getTextBubble());

		initialAddingAccessoryState();
		mTextAccessoryFlag.hide();
		
		selectTextBubbleID = -1;
	}
	
	public void clearAddingText() {
		addValue = 0;
		selectTextBubbleID = -1;
		
		mAccessoryFlag = null;
		
	    if (mTextAccessoryFlag != null) {
	        removeView(mTextAccessoryFlag.getImageView());
	        mTextAccessoryFlag = null;
	    }
		mRelativeLayout.removeAllViews();
		mRelativeLayout.addView(getGroundImage().getImageView());
		int arrayListSize = mImageControlArrayList.size();
		for (int i = 0; i < arrayListSize; i++) {
			ImageControl imageControl = mImageControlArrayList.get(i);
			imageControl.getBitmap().recycle();
		}
		mImageControlArrayList.clear();
		mImageControlArrayList = null;
	}

	public void addView(View view) {
		mRelativeLayout.addView(view);
	}

	public void removeView(View view) {

		if (mRelativeLayout == null)
			return;
		int count = mRelativeLayout.getChildCount();

		int index = mRelativeLayout.indexOfChild(view);

		if (index >= 0 && index < count) {
			mRelativeLayout.removeView(view);
		}
	}

	public void release() {
		if(mGroundImage !=null)
		{
			if(mGroundImage.getBitmap() != null)
			{
				mGroundImage.getBitmap().recycle();
			}
			mGroundImage = null;
		}

		mImageControlArrayList = null;

		mActivity = null;

		mAccessoryFlag = null;

		mIsAddingAccessory = false;
		mIsAddingText = false;
//		cropView = null;
//		mCurEffect = null;
		mSingleton = null;
	}

	public Bitmap getBitmapHasAccessory() {
		return this.getBitmapHasAccessory(mImageControlArrayList, null);
	}
	
	public Bitmap getBitmapHasAccessory(List<ImageControl> accessoryList) {
	    return this.getBitmapHasAccessory(accessoryList, null);
	}
	
   public Bitmap getBitmapHasAccessory(List<ImageControl> accessoryList, MyPaint extraPaint) {
       if (accessoryList == null || accessoryList.size() <= 0) {
           return null;
       }

        Canvas canvas = new Canvas(mGroundImage.getBitmap());
        MyPaint paint = new MyPaint();
        if (extraPaint != null) {
            paint.set(extraPaint);
        }

        int arrayListSize = accessoryList.size();
        Matrix matrix1 = new Matrix();
        mGroundImage.getImageMatrix().invert(matrix1);
        for (int i = 0; i < arrayListSize; i++) {
            try {
                ImageControl imageControl = accessoryList.get(i);
                int alpha = imageControl.getAlpha();
                paint.setAlpha(alpha);
                Matrix matrix = imageControl.getImageMatrix();
                matrix.postConcat(matrix1);
                canvas.drawBitmap(imageControl.getBitmap(), matrix, paint);
            } catch (Exception e) {
                // TODO: handle exception
            	e.printStackTrace();
            }
        }
        return mGroundImage.getBitmap();
    }
   
   	/**
   	 * 获取文字泡泡生成的图片
   	 * @param accessoryList
   	 * @param extraPaint
   	 * @return
   	 */
	public Bitmap getBitmapHasText(List<ImageControl> accessoryList, MyPaint extraPaint) {
		if (accessoryList == null || accessoryList.size() <= 0) {
			return null;
		}

		Canvas canvas = new Canvas(mGroundImage.getBitmap());
		MyPaint paint = new MyPaint();
		if (extraPaint != null) {
			paint.set(extraPaint);
		}

		int arrayListSize = accessoryList.size();
		Matrix matrix1 = new Matrix();
		mGroundImage.getImageMatrix().invert(matrix1);
		TextBubbleControl mTextBubbleControl = null;
		TextBubble tBubble = null;
		for (int i = 0; i < arrayListSize; i++) {
			try {
				mTextBubbleControl = (TextBubbleControl) accessoryList.get(i);
				int alpha = mTextBubbleControl.getAlpha();
				paint.setAlpha(alpha);
				Matrix matrix = mTextBubbleControl.getImageMatrix();
				matrix.postConcat(matrix1);
				if (!mTextBubbleControl.getmTextPara().mFileName.equalsIgnoreCase("bubble01.png")
						&&!mTextBubbleControl.getmTextPara().mFileName.equalsIgnoreCase("bubble02.png")) {
					canvas.drawBitmap(mTextBubbleControl.getBitmap(), matrix, paint);
				}
//				UmengCount.onEvent(mActivity, UmengCount.BUBBLE, mTextBubbleControl.getmTextPara().mFileName);
				tBubble = mTextBubbleControl.getTextBubble();
				if (!tBubble.getText().toString().trim().equals("")) {
					tBubble.setDrawingCacheEnabled(true);
					Bitmap bmp = tBubble.getDrawingCache();
					if (bmp != null) {
						canvas.drawBitmap(bmp, matrix1, paint);
						bmp.recycle();
					}
					tBubble.setDrawingCacheEnabled(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mGroundImage.getBitmap();
	}

	public void drawInkCanvas(Bitmap bitmap) {

		Canvas canvas = new Canvas(mGroundImage.getBitmap());
		MyPaint paint = new MyPaint();
		canvas.drawBitmap(bitmap, new Matrix(), paint);

	}

	public GroundImage getGroundImage() {
		return mGroundImage;
	}

	public Bitmap getGroundImageBitmap() {
		return mGroundImage.getBitmap();
	}

	public void setGroundImageBitmap(Bitmap bitmap) {
		mGroundImage.setBitmap(bitmap);
	}

	public ImageView getGroundImageView() {
		return mGroundImage.getImageView();
	}

	// Mode
	private static final int NONE = 0;
	// private static final int DRAG = 1;
	private static final int ZOOM = 2;

	// The maximal zoom scale for one zoom step
	private static final float MAX_ONE_STEP_SCALE_LIMIT = 1.05f;

	// The minimal distance of two pointers
	private static final float MIN_POINTERS_DIST_LIMIT = 20.0f;

	// The move limit for one move step
	private static final float MAX_ONE_STEP_MOVE_LIMIT = 10.0f;

	// The distance to judge whether the move is jump
	private static final float JUMP_DIST = 50.0f;

	public int AddingMode;
	public static final int ADDINGACCESSORY = 0;
	public static final int ADDINGFRAME = 1;
	public static final int ADDINGTEXT = 2;
	public static final int ADDINGMULTIPICSFRAME = 3;

	// private int mSaveMode = 0;

	// The constant variable one
	static final int ONE = 1;

	private int mPointerCnt = 0;
	private Boolean mFirstOnePointer = false;
	private Boolean mFirstTwoPointer = false;

	// The initial mode
	private int mode = NONE;

	// The two pointers of the last step
	private MyPoint oldPointer0 = new MyPoint();
	private MyPoint oldPointer1 = new MyPoint();

	// The two pointers of this step
	private MyPoint newPointer0 = new MyPoint();
	private MyPoint newPointer1 = new MyPoint();

	private MyPoint oldFirstPointer = new MyPoint();
	private MyPoint newFirstPointer = new MyPoint();

	private MyPoint oldSecondPointer = new MyPoint();
	private MyPoint newSecondPointer = new MyPoint();

	// The distance of the two pointers
	private float oldDist, newDist;
	private double oldAngel,newAngel;
	private boolean isFirstRotate = true;;
	
	public Boolean willRotate = false;
	public Boolean willDelete = false;
	public Boolean willMoveMain = false;
	public Boolean willMoveOutter = false;
	public Boolean isDoAll = false;
	public Boolean isFirstIn = true;

	public Boolean switchView = false;
	public int viewId = -1;
	public int selectTextBubbleID = -1;
	public boolean isRightPosition = true;

	public int firstId = -1;
	private Boolean mSingleTapped = false;
	public void singleTapped() {
		mSingleTapped = true;
	}

	public void updateView(TouchParameter touchParameter) {

		int pointerCnt = touchParameter.pointerCnt;
		 Log.d(TAG, "pointerCnt: " + pointerCnt);
		float dx = 0;
		float dy = 0;
		float scale = 1.0f;
		double rotate = 1.0f;
		MyPoint centerPoint = null;

		if (pointerCnt != mPointerCnt) {

			mPointerCnt = pointerCnt;
			mFirstOnePointer = true;
			mFirstTwoPointer = true;
		}

		if (touchParameter.isUp == true) {

			mPointerCnt = 0;
			mActionIsUp = true;

		} else {
			mActionIsUp = false;
		}

		if (pointerCnt == 1) {

			if (mFirstOnePointer == true) {
				oldPointer0.set(touchParameter.FirstPointer);
			} else {

				dx = touchParameter.FirstPointer.x - oldPointer0.x;
				dy = touchParameter.FirstPointer.y - oldPointer0.y;
			}
			// Log.d(TAG, "dx=" + dx + " dy=" + dy);
			oldFirstPointer.set(oldPointer0);
			newFirstPointer.set(touchParameter.FirstPointer);

			oldPointer0.set(touchParameter.FirstPointer);

		} else {
			
			if (mFirstTwoPointer == true) {
				isFirstRotate=  true;
				oldPointer0.set(touchParameter.FirstPointer);
				oldPointer1.set(touchParameter.secondPointer);
				oldDist = MyPoint.distance(oldPointer0, oldPointer1);
				oldAngel = PointsCaculation.caculateTwoPointsAngle(oldPointer0.x,oldPointer0.y,oldPointer1.x,oldPointer1.y);
				if (oldDist > MIN_POINTERS_DIST_LIMIT) {
					mode = ZOOM;
				}

			} else {
				newPointer0.set(touchParameter.FirstPointer);
				newPointer1.set(touchParameter.secondPointer);

				newDist = MyPoint.distance(newPointer0, newPointer1);
				newAngel = PointsCaculation.caculateTwoPointsAngle(newPointer0.x,newPointer0.y,newPointer1.x,newPointer1.y);

				if(isFirstRotate)
				{
					rotate = 0;
					isFirstRotate = false;
				}
				else
				{
					rotate = newAngel - oldAngel;
				}
				// Log.d(TAG, "" + oldDist + "/" + newDist);

				if (newDist > MIN_POINTERS_DIST_LIMIT) {

					scale = newDist / oldDist;
					if (scale > MAX_ONE_STEP_SCALE_LIMIT) {
						scale = MAX_ONE_STEP_SCALE_LIMIT;
					} else if (scale < 1.0 / MAX_ONE_STEP_SCALE_LIMIT) {
						scale = 1.0f / MAX_ONE_STEP_SCALE_LIMIT;
					}

					// if there is a pointer jumping, make it to
					// MAX_ONE_STEP_MOVE_LIMIT
					if (Math.abs(dx) > JUMP_DIST) {
						dx = MAX_ONE_STEP_MOVE_LIMIT * sig(dx);
					}

					if (Math.abs(dy) > JUMP_DIST) {
						dy = MAX_ONE_STEP_MOVE_LIMIT * sig(dy);
					}
				}
			}

			oldFirstPointer.set(oldPointer0);
			oldSecondPointer.set(oldPointer1);
			newFirstPointer.set(touchParameter.FirstPointer);
			newSecondPointer.set(touchParameter.secondPointer);
			
			dx = - (oldFirstPointer.x + oldSecondPointer.x)/2
			 + (newFirstPointer.x + newSecondPointer.x)/2;
			dy = - (oldFirstPointer.y + oldSecondPointer.y)/2
				+ (newFirstPointer.y + newSecondPointer.y)/2;
			
			centerPoint = new MyPoint();
			centerPoint.x =  (newFirstPointer.x + newSecondPointer.x)/2;
			centerPoint.y =  (newFirstPointer.y + newSecondPointer.y)/2;

			
			oldPointer0.set(newFirstPointer);
			oldPointer1.set(newSecondPointer);
			oldDist = newDist;
			oldAngel =  newAngel;
		}

		if (mFirstOnePointer == true || mFirstTwoPointer == true) {
			mFirstOnePointer = false;
			mFirstTwoPointer = false;
			switchView = true;
		} else {
			switchView = false;
		}

		if (mIsAddingText == true) {
			updateAddingTextBox(dx, dy, scale, switchView, pointerCnt, touchParameter, centerPoint, rotate);
		}
		else {
			updateOnlyGround(dx, dy, scale, pointerCnt,centerPoint);
		}
	}
    boolean isDelete=false;
    boolean isDownVisible=false;
    boolean isFisrtDown=true;


	private void updateAddingAccessory(float dx, float dy, float scale,
			Boolean switchView, int pointerCnt) {

		firstId = findTouchedViewId(oldFirstPointer);
		
		if(mActionIsUp){
			isFisrtDown = false;
		}
		else{
			if(!isFisrtDown)
			{
				isFisrtDown=  true;
				isDownVisible = mAccessoryFlag.mIsVisible;
			}
		}
		
		if(firstId == -3)
		{
			isDelete = true;
		}
		
		if(isDelete)
		{
			firstId = -3;
		}
		
		if(mSingleTapped || mActionIsUp)
		{
			isDelete  = false;
		}
		
		if (mActionIsUp && firstId == -1 && mSingleTapped && mImageControlArrayList.size() > 0) {
			if(isDownVisible)
			{
				mAccessoryFlag.toggleVisibility();
			}
			mAccessoryMiddle.show(mImageControlArrayList.size() - 1);
			return;
		}

		if (firstId == -3 &&  mSingleTapped && mImageControlArrayList.size() > 0) {
//			try
//			{
//				if(UmengCount.ACCE_STRING!= null)
//				{
//					UmengCount.ACCE_STRING.remove(mImageControlArrayList.size() - 1);
//				}
//			}catch(Exception e)
//			{
//				e.printStackTrace();
//			}
			deleteAccessory(mImageControlArrayList.size() - 1);
			return;
		}

		
		if (switchView == true) {

			willRotate = false;

			if (firstId == -2) {
				if (viewId >= 0) {
					willRotate = true;
				} else {
					firstId = -1;
				}
			}
			
			if (firstId == -1) {
				viewId = -1;
			}

			if (firstId >= 0) {
				viewId = firstId;
			}
		}


		if (viewId == -5) {
			mGroundImage.addingAccessoryUpdate(dx, dy, scale, false,
					oldFirstPointer, newFirstPointer);

			if (mActionIsUp == true) {
				mGroundImage.addingAccessoryRebound();
			}
		} else {
			if (viewId == -1) {
				viewId = mImageControlArrayList.size() - 1;
			}
			if (willRotate == true) {

				AccessoryImage accessoryImage = (AccessoryImage) mImageControlArrayList
						.get(viewId);

				accessoryImage.updateImageView(dx, dy, scale, true,
						oldFirstPointer, newFirstPointer);
				mAccessoryMiddle.show(viewId);
			} else {

				int arrayListSize = mImageControlArrayList.size();
				if (viewId >= arrayListSize || viewId < 0) {
					viewId = arrayListSize - 1;
				}
				if (viewId < 0) {
					return;
				}
				AccessoryImage accessoryImage = (AccessoryImage) mImageControlArrayList
						.get(viewId);


				if (viewId != arrayListSize - 1) {

					mImageControlArrayList.remove(viewId);
					mImageControlArrayList.add(accessoryImage);

					ImageView imageView = accessoryImage.mImageView;

					imageView.bringToFront();
				}
				if (viewId != arrayListSize - 1) {
//					if(UmengCount.ACCE_STRING != null
//							&& UmengCount.ACCE_STRING.size() > viewId
//							&& viewId >= 0)
//					{
//						String tempStr = UmengCount.ACCE_STRING.get(viewId);
//						UmengCount.ACCE_STRING.remove(viewId);
//						UmengCount.ACCE_STRING.add(tempStr);
//					}
				}

				viewId = arrayListSize - 1;

				accessoryImage = (AccessoryImage) mImageControlArrayList
						.get(viewId);
				accessoryImage.updateImageView(dx, dy, scale, false,
						oldFirstPointer, newFirstPointer);
			}
		}
		
		mAccessoryFlag.show(viewId);
		mAccessoryMiddle.show(viewId);
	}

	private void updateOnlyGround(float dx, float dy, float scale,
			int pointerCnt,MyPoint center) {
//
//		try {
//			if(mSingleTapped && !ScreenControl.getSingleton().mIsEffectMode)
//			{
//				int[] mButtonIDs = { R.id.edit_button_layout,
//						R.id.cosmesis_button_layout,R.id.add_button_layout, 
//						R.id.effect_button_layout,R.id.frame_button_layout};
//					
//					View bottomMenuView = LayoutController.getSingleton().getBottomLayout();
//					for (int i = 0; i < mButtonIDs.length; i++)
//					{
//						if(bottomMenuView.findViewById(mButtonIDs[i]).isSelected())
//						{
//							bottomMenuView.findViewById(mButtonIDs[i]).setSelected(false);
//							LayoutController.getSingleton().hideBounceGallery();
//						}
//					}
//			}
//			
//			mGroundImage.updateImageView(dx, dy, scale, false, null, null,center);
//
//			if (mActionIsUp == true) {
//				if (!this.mIsAddingFrame) {
//					mGroundImage.rebound();
//				}
//			}
//			
//			
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//
	}
	
	/**
	 * 
	 * @param point
	 * @return index
	 * -1:groundImage
	 * -2:rotate
	 * -3:delete
	 * >=0:index
	 */
	public int findTextTouchedViewId(MyPoint point) {
		if (mIsAddingText == true) {
			if (mTextAccessoryFlag.isContainPoint(point, 10)) {
				return -2;
			}
			if (mTextAccessoryFlag.isContainPointDel(point, 10)) {
				Pwog.d(TAG, "at del btn");
				return -3;
			}
		}

		int arrayListSize = mImageControlArrayList.size();

		for (int i = arrayListSize - 1; i >= 0; i--) {
			ImageControl imageControl = mImageControlArrayList.get(i);
			if (imageControl == null || !imageControl.isControlEnabled()) {
			    continue;
			}
			if (imageControl.isContainPoint(point, 10) == true) {
				return i;
			}
		}

		return -1;
	}

	private int findTouchedViewId(MyPoint point) {

		if (mIsAddingAccessory == true) {
			if (mAccessoryFlag.isContainPoint(point, 10)) {
				return -2;
			}
			if (mAccessoryFlag.isContainPointDel(point, 10)) {
				Pwog.d(TAG, "at del btn");
				return -3;
			}
		}

		int arrayListSize = mImageControlArrayList.size();

		for (int i = arrayListSize - 1; i >= 0; i--) {
			ImageControl imageControl = mImageControlArrayList.get(i);
			if (imageControl == null || !imageControl.isControlEnabled()) {
			    continue;
			}
			if (imageControl.isContainPoint(point, 10) == true) {
				return i;
			}
		}

		return -1;
	}
	
	private int findTouchedViewIdForShape(MyPoint point) {

		int arrayListSize = mImageControlArrayList.size();

		for (int i = arrayListSize - 1; i >= 0; i--) {
			ImageControl imageControl = mImageControlArrayList.get(i);
			if (imageControl.isContainPoint(point, 10) == true) {
				return i;
			}
		}

		return -1;
	}

	private int tempCurrentShow;

	public void hideAllView()
	{
		mIsShapeShow = false;
		if(mImageControlArrayList != null)
		{
			for(ImageControl ic:mImageControlArrayList)
			{
				ic.getImageView().setVisibility(View.GONE);
			}
		}
		callBackHideAllAccessories();
	}
	
	public void showAllView()
	{
		mIsShapeShow = true;
		if(mImageControlArrayList != null)
		{
			for(ImageControl ic:mImageControlArrayList)
			{
				ic.getImageView().setVisibility(View.VISIBLE);
			}
		}
		callBackShowAllAccessories();
	}
	
	
	
	public boolean mIsEditingText = false;
	public boolean mIsCanEnterEditing = false;
//	public boolean mIsSingleTapWithoutMove = true;
	private void updateAddingTextBox(float dx, float dy, float scale,
			Boolean switchView, int pointerCnt, TouchParameter touchParameter,
			MyPoint centerPoint, double rotate) {
		firstId = findTextTouchedViewId(oldFirstPointer);
		mIsKeepSelected = true;
		if(mActionIsUp){
			isFisrtDown = false;
		} else{
			if(!isFisrtDown){
				isFisrtDown=  true;
				isDownVisible = mTextAccessoryFlag.mIsVisible;
			}
		}
		
		
		if (firstId == -1 && mSingleTapped && !willDelete) {
			callBackSingleTapped();
		}

		if (!willMoveOutter) {
			tempCurrentShow = currentShow;
		}
		if (firstId == -1 && !mSingleTapped && tempCurrentShow >= 0) {
			firstId = tempCurrentShow;
			willMoveOutter = true;
		}

		if (firstId == -1) {
			willMoveMain = true;
		}
		if (willMoveMain) {
			firstId = -1;
		}

		if (willRotate) {
			firstId = -2;
		}

		if (willDelete) {
			firstId = -3;
		}

		if (firstId == -3) {
			willDelete = true;
		}
		if (isFirstIn && (firstId >= 0 || willDelete || willRotate)) {
			isFirstIn = false;
			showAllView();
		}

		if (mActionIsUp) {
			isFirstIn = true;
			isDoAll = false;
			willRotate = false;
			willDelete = false;
			willMoveMain = false;
			willMoveOutter = false;
		}

		if (mActionIsUp && firstId == -1 && mSingleTapped
				&& mImageControlArrayList.size() > 0) {
			if(isDownVisible){
				mTextAccessoryFlag.toggleVisibility();
			}
			
			willRotate = false;
			willDelete = false;
			willMoveMain = false;
			willMoveOutter = false;
			viewId = -1;
			selectTextBubbleID = -1;
			return;
		}
		if (mActionIsUp && viewId >= 0) {
			showAllView();
		}

		if (firstId == -3 && mSingleTapped && mImageControlArrayList.size() > 0) {
			ImageControl imageControl = mImageControlArrayList
					.get(mImageControlArrayList.size() - 1);
			ImageView imageView = imageControl.mImageView;

			mLastMatrix = imageView.getImageMatrix();
			deleteText(mImageControlArrayList.size() - 1);
			
			if (mImageControlArrayList.size() == 0) {
				isRightPosition = true;
			}
			willRotate = false;
			willDelete = false;
			willMoveMain = false;
			willMoveOutter = false;
			return;
		}

		if (willDelete) {
			return;
		}
		
		if (switchView == true) {
			if (firstId == -2) {
				if (viewId >= 0) {
					willRotate = true;
				}
			}
			if (!mIsEditingText) {
				if (firstId == -1) {
					viewId = -1;
				}

				if (firstId >= 0) {
					if (viewId!=firstId) {
						viewId = firstId;
						mIsCanEnterEditing = false;
					} else {

						mIsCanEnterEditing = true;
					}
					
					TextBubbleControl bubbleControl = (TextBubbleControl) mImageControlArrayList.get(viewId);
					selectTextBubbleID = bubbleControl.getmTextPara().position;
				}
			}
		}

		if ((firstId == -1 || pointerCnt == 2) && !willRotate) {
			if (pointerCnt == 2) {
				if (viewId >= 0) {
					willRotate = true;
					isDoAll = true;
				} else {
					mGroundImage.addingTextUpdate(dx, dy, scale, false,
							oldFirstPointer, newFirstPointer, centerPoint);
				}
			}
			
			if (pointerCnt == 1) {
				mGroundImage.addingTextUpdate(dx, dy, scale, false,
						oldFirstPointer, newFirstPointer, null);
			}

			if (mActionIsUp == true) {
				mGroundImage.addingTextRebound();
			}
		} else if (viewId == -5 && !willRotate) {
			mGroundImage.addingAccessoryUpdate(dx, dy, scale, false,
					oldFirstPointer, newFirstPointer);
			if (mActionIsUp == true) {
				mGroundImage.addingTextRebound();
			}
		} else {
			if (viewId == -1) {
				viewId = mImageControlArrayList.size() - 1;
				
				TextBubbleControl bubbleControl = (TextBubbleControl) mImageControlArrayList.get(viewId);
				selectTextBubbleID = bubbleControl.getmTextPara().position;
			}
			if (willRotate == true) {
				TextBubbleControl bubbleControl = (TextBubbleControl) mImageControlArrayList.get(viewId);
				bubbleControl.updateImageView(dx, dy, scale, true,
						oldFirstPointer, newFirstPointer, isDoAll, rotate);
			} else {
				int arrayListSize = mImageControlArrayList.size();
				if (viewId >= arrayListSize || viewId < 0) {
					viewId = arrayListSize - 1;
					
					TextBubbleControl bubbleControl = (TextBubbleControl) mImageControlArrayList.get(viewId);
					selectTextBubbleID = bubbleControl.getmTextPara().position;
				}
				if (viewId < 0) {
					return;
				}
				TextBubbleControl bubbleControl = (TextBubbleControl) mImageControlArrayList.get(viewId);
				
				if (viewId != arrayListSize - 1) {
					isRightPosition = !isRightPosition;
					mImageControlArrayList.remove(viewId);
					mImageControlArrayList.add(bubbleControl);

					ImageView imageView = bubbleControl.mImageView;

					imageView.bringToFront();
					bubbleControl.getTextBubble().bringToFront();
				}

				viewId = arrayListSize - 1;
				bubbleControl = (TextBubbleControl) mImageControlArrayList
						.get(viewId);
				
				selectTextBubbleID = bubbleControl.getmTextPara().position;
				
				bubbleControl.updateImageView(dx, dy, scale, false,
						oldFirstPointer, newFirstPointer);
				if (touchParameter.getIsUp()) {
					callBackAccessoryMoved(bubbleControl);
				}

				if (mActionIsUp == true) {
					mGroundImage.addingTextRebound();
				}
			}
		}

		if(viewId >= 0){
			mTextAccessoryFlag.show(viewId);	
		}
	}
	
	// the sign of the float variable
	public float sig(float x) {
		if (x > 0) {
			return 1.0f;
		} else if (x < 0) {
			return -1.0f;
		} else {
			return 0;
		}
	}

	// public static Bitmap drawableToBitmap(Drawable drawable) {
	//
	// int width = drawable.getIntrinsicWidth();
	// int height = drawable.getIntrinsicHeight();
	//
	// Bitmap.Config config = Bitmap.Config.ARGB_8888;
	//
	// Bitmap bitmap = Bitmap.createBitmap(width, height, config);
	// Canvas canvas = new Canvas(bitmap);
	// drawable.setBounds(0, 0, width, height);
	// drawable.draw(canvas);
	//
	// return bitmap;
	// }

	public GroundImage getmGroundImage() {
		return mGroundImage;
	}

	public void setmGroundImage(GroundImage mGroundImage) {
		this.mGroundImage = mGroundImage;
	}

	public ArrayList<ImageControl> getmImageControlArrayList() {
		return mImageControlArrayList;
	}

	public void setmImageControlArrayList(
			ArrayList<ImageControl> mImageControlArrayList) {
		this.mImageControlArrayList = mImageControlArrayList;
	}

	public void setmActivity(Activity mActivity) {
		this.mActivity = mActivity;
	}

	public RelativeLayout getmRelativeLayout() {
		return mRelativeLayout;
	}

	public void setmRelativeLayout(RelativeLayout mRelativeLayout) {
		this.mRelativeLayout = mRelativeLayout;
	}

	public AccessoryFlag getmAccessoryFlag() {
		return mAccessoryFlag;
	}

	public void setmAccessoryFlag(AccessoryFlag mAccessoryFlag) {
		this.mAccessoryFlag = mAccessoryFlag;
	}

	public AccessoryMiddle getmAccessoryMiddle() {
		return mAccessoryMiddle;
	}

	public void setmAccessoryMiddle(AccessoryMiddle mAccessoryMiddle) {
		this.mAccessoryMiddle = mAccessoryMiddle;
	}

	public Boolean getmIsAddingAccessory() {
		return mIsAddingAccessory;
	}

	public void setmIsAddingAccessory(Boolean mIsAddingAccessory) {
		this.mIsAddingAccessory = mIsAddingAccessory;
	}
	public void setmIsAddingBlush(Boolean mIsAddingAccessory) {
		this.mIsAddingBlush = mIsAddingAccessory;
	}
	public void setmIsShape(Boolean mIsShape) {
		this.mIsShape = mIsShape;
	}

	public Boolean getmIsAddingText() {
		return mIsAddingText;
	}

	public void setmIsAddingText(Boolean mIsAddingText) {
		this.mIsAddingText = mIsAddingText;
	}

	public void setmIsAddingFrame(Boolean is) {
		this.mIsAddingFrame = is;
	}

	public Boolean getmIsAddingFrame() {
		return this.mIsAddingFrame;
	}

	public Boolean getmActionIsUp() {
		return mActionIsUp;
	}

	public void setmActionIsUp(Boolean mActionIsUp) {
		this.mActionIsUp = mActionIsUp;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		// Pwog.w("-------------touch------------", "---------ok------------");
		mSingleTapped = false;
		if (mGestureDetector.onTouchEvent(event))
			return true;
		// Pwog.w("-------------touch------------", "---------ok1------------");
		mEvent.setEvent(event);
		int action = mEvent.getAction();
		int pointerCnt = mEvent.getPointerCount();
		// Log.d(TAG, "pointerCnt: " + pointerCnt);
		//
//		if (action == MotionEvent.ACTION_DOWN) {
//			mIsSingleTapWithoutMove = true;
//		} else if (action == MotionEvent.ACTION_MOVE) {
//			mIsSingleTapWithoutMove = false;
//		}
		
		try {
			if (pointerCnt == 1) {
				MyPoint point = new MyPoint(mEvent.getX(0), mEvent.getY(0));
				TouchParameter touchParameter = new TouchParameter(1, point, point,
						action == MotionEvent.ACTION_UP);
				updateView(touchParameter);

			} else {
				MyPoint firstPointer = new MyPoint(mEvent.getX(0), mEvent.getY(0));
				MyPoint secondPointer = new MyPoint(mEvent.getX(1), mEvent.getY(1));

				TouchParameter touchParameter = new TouchParameter(pointerCnt,
						firstPointer, secondPointer,
						action == MotionEvent.ACTION_UP);
				updateView(touchParameter);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			e.printStackTrace();
//			CrashRestart.restartAfterSaveGroundImage();
		}
		return true;
	}

	private int mTextBoxId;
	private boolean mIsKeepSelected = true;
	public void leaveEditing() {

		mIsEditingText = false;

	}
	public void leaveEditing(String str) {

		mIsEditingText = false;
//		LayoutController.getSingleton().getTopBarLayout()
//				.setVisibility(View.VISIBLE);
//		LayoutController.getSingleton().getBottomGallery()
//				.setVisibility(View.VISIBLE);
//		InputMethodManager imm = (InputMethodManager) LayoutController.getSingleton().getActivity()
//				.getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(getmRelativeLayout().getWindowToken(), 0);
//		try {
//			if (mTextBoxId>0) {
				TextBubble tb = ((TextBubbleControl) (mImageControlArrayList
						.get(mTextBoxId))).getTextBubble();
//				tb.mIsEditing = false;
				tb.setText(str);
//				selectTextBubbleID =((TextBubbleControl) (mImageControlArrayList
//						.get(mTextBoxId))).getmTextPara().position;
//				mTextAccessoryFlag.show(mTextBoxId);
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public void onEditing(int id) {
		mIsEditingText = true;
		mTextBoxId = id;
		TextBubble tb = ((TextBubbleControl) (mImageControlArrayList
				.get(id))).getTextBubble();
//		Intent i = new Intent(mActivity,
//				TextInputActivity.class);
//		i.putExtra(TextBubbleActivity.TEXT_BUBBLE_PRE_STR, tb.getText().toString());
//		mActivity.startActivityForResult(i, TextBubbleActivity.EDIT_TEXT);
		
		
		mOnEditingCallback.onEditingNewActivity(tb.getText().toString());
		
//		LayoutController.getSingleton().getTopBarLayout()
//				.setVisibility(View.INVISIBLE);
//		LayoutController.getSingleton().getBottomGallery()
//				.setVisibility(View.INVISIBLE);
//		TextBubble tb = ((TextBubbleControl) (mImageControlArrayList
//				.get(id))).getTextBubble();
//		tb.requestFocus();
//		tb.mIsEditing = true;
//		CharSequence text = tb.getText();
//		if (text instanceof Spannable) {
//			Spannable spanText = (Spannable) text;
//			Selection.setSelection(spanText, text.length());
//		}
//		InputMethodManager imm = (InputMethodManager) LayoutController.getSingleton().getActivity()
//				.getSystemService(Context.INPUT_METHOD_SERVICE);
////		imm.showSoftInputFromInputMethod(getmRelativeLayout().getWindowToken(), InputMethodManager.SHOW_FORCED);
//		imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
	}


//	public ImageControl getPreviewImage() {
//		// TODO Auto-generated method stub
//		return previewImage;
//	}


//	public void setmIsMosaic(Boolean mIsMosaic) {
//		this.mIsMosaic = mIsMosaic;
//	}
//
//	public Boolean getmIsMosaic() {
//		return mIsMosaic;
//	}
//	

	public void addScreenControlCallback(ScreenControlCallback callback) {
		mScreenControlCallback = callback;
	}
	
	private void callBackSingleTapped() {
		if (mScreenControlCallback != null) {
			mScreenControlCallback.onSingleTapped();
		}
	}
	
	private void callBackAccessoryDeleted(AccessoryImage accessoryImage) {
		if (mScreenControlCallback != null) {
			mScreenControlCallback.onAccessoryDeleted(accessoryImage);
		}
	}
	
	private void callBackAccessoryMoved(ImageControl imageControl) {
		if (mScreenControlCallback != null) {
			mScreenControlCallback.onAccessoryMoved(imageControl);
		}
	}
	
	private void callBackShowAllAccessories() {
	    if (mScreenControlCallback != null) {
	        mScreenControlCallback.onShowAllAccessories();
	    }
	}
	   
    private void callBackHideAllAccessories() {
        if (mScreenControlCallback != null) {
            mScreenControlCallback.onHideAllAccessories();
        }
    }
	
	public static interface ScreenControlCallback {
		void onSingleTapped();
		void onAccessoryDeleted(AccessoryImage accessoryImage);
		void onAccessoryMoved(ImageControl accessoryImage);
		void onShowAllAccessories();
		void onHideAllAccessories();
	}
	
	public void setOnEditingCallback(onEditingCallback callback){
		mOnEditingCallback = callback;
	}
	
	public void setAccessoryDrawableRes(int operaterDrawableRes, int delDrawableRes){
		mAccessoryoperater = operaterDrawableRes;
		mAccessory_del = delDrawableRes;
	}
}
