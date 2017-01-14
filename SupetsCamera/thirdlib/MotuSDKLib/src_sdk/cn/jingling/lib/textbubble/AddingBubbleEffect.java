package cn.jingling.lib.textbubble;

import java.util.ArrayList;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.Selection;
import android.text.Spannable;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;


/**
 * 文字泡泡
 */
public class AddingBubbleEffect extends AddingEffect implements
		OnDoubleTapListener {
	GestureDetector mGestureDetector;

	public AddingBubbleEffect() {
//		mToastID = R.string.accessoriesToast;
	}

	public AddingBubbleEffect(int toastId) {
//		mToastID = toastId;
	}
	
	@Override
	public void perform() {
		super.perform();

		setNewStateBack();
		

		mScreenControl.setmIsAddingText(true);
		mScreenControl.getGroundImage().setFlagMove(true);
		mScreenControl.getGroundImage().setFlagZoom(true);
		mScreenControl.mImageControlArrayList = new ArrayList<ImageControl>();
		mScreenControl.initializeAddingText();

		// mScreenControl.setmIsAddingAccessory(true);
		// mScreenControl.mImageControlArrayList = new
		// ArrayList<ImageControl>();
		// mScreenControl.initializeAddingAccessory();
		// mScreenControl.initialAddingAccessoryState();

		//显示底部文字泡泡操作栏
//		BottomGalleryAction mBottomGalleryAction = new BottomGalleryAction(AddingEffectType.Text, this);

//		mGroundImage.getImageView().setOnTouchListener(this);
		mGestureDetector = mScreenControl.getGestureDetector();
		mGestureDetector.setOnDoubleTapListener(this);
		
		//点击文字泡泡种类1
//		mBottomGalleryAction.performAdd(mContext, 0);
	};
	
	public void release() {
		mGestureDetector.setOnDoubleTapListener(null);
		mGestureDetector = null;
	}

	private void setNewStateBack() {
		// TODO Auto-generated method stub
//		SettingUtil.setTextBubbleNewShowed(true);

//		try{		
//			if(SettingUtil.getTextBubbleNewShowed()){
//				View bottomMenuView = LayoutController.getSingleton().getBottomLayout();
//				((BottomItemLayout)bottomMenuView.findViewById(R.id.add_button_layout)).setNew(false);//setCompoundDrawables(null, drawable, null, null);
//			}
//			
//			BounceGalleryAdapter bounceGalleryAdapter = null;
//			bounceGalleryAdapter = new BounceGalleryAdapter(LayoutController.getSingleton().getActivity(),
//					R.array.decoration_catelog_conf, false);
//			LayoutController.getSingleton().getBounceGallery().setAdapter(bounceGalleryAdapter);
//			bounceGalleryAdapter.notifyDataSetChanged();
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
	}

	
	public void onAdding(Bitmap bitmap, Object Param) {
		String oldText = "";
		boolean tempFlag = false;
		Matrix oldMatrix = new Matrix();
		TextBubbleStyleParameter mTextBubbleStyleParameter = (TextBubbleStyleParameter)Param;
		try {
			if (mScreenControl.viewId >= 0
					&& mScreenControl.viewId <= mScreenControl.mImageControlArrayList.size() - 1) {
				// 如果当前选中了一个，记录该文字泡泡中的文字后将其删除。
				tempFlag = true;
				TextBubbleControl mTextBubbleControl = (TextBubbleControl) mScreenControl.mImageControlArrayList
						.get(mScreenControl.viewId);
				oldMatrix = mTextBubbleControl.getImageMatrix();
				if (mTextBubbleControl != null
						&& mTextBubbleControl.getTextBubble() != null
						&& mTextBubbleControl.getTextBubble().getText() != null) {
					oldText = mTextBubbleControl.getTextBubble().getText().toString();
				}
				mScreenControl.deleteText(mScreenControl.viewId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TextBubbleControl mTextBubbleControl = mScreenControl.addTextBubble(
				bitmap, (TextBubbleStyleParameter) mTextBubbleStyleParameter);
		if(tempFlag){
//			Matrix newMatrix = mTextBubbleControl.getImageMatrix();
//			float[] newValues = new float[9];
//			newMatrix.getValues(newValues);
//			
//			float[] oldValues = new float[9];
//			oldMatrix.getValues(oldValues);
//			
//			newValues[2] = oldValues[2];
//			newValues[5] = oldValues[5];
//			newMatrix.setValues(newValues);
//			mTextBubbleControl.setImageViewMatrix(newMatrix);
			mTextBubbleControl.setImageViewMatrix(oldMatrix);
			int arralyListSize = mScreenControl.mImageControlArrayList.size();
			mScreenControl.mTextAccessoryFlag.show(arralyListSize - 1);
			mTextBubbleControl.inValidateImageView();
		}
		
		mScreenControl.selectTextBubbleID = mTextBubbleControl.getmTextPara().position;
		
		
		if (oldText != null && !oldText.equals("")) {
			mTextBubbleControl.getTextBubble().setText(oldText);
		}
//		return null;
	}

	@Override
	public boolean onCancel() {
		if (mScreenControl.mIsEditingText == false) {
			mScreenControl.clearAddingText();
			mScreenControl.setmIsAddingText(false);
			return true;
		} else {
			mScreenControl.leaveEditing();
			return false;
		}
	}

	@Override
	public boolean onOk() {
		// TODO Auto-generated method stub
		try {
			if (mScreenControl.mIsEditingText == false) {
				if (mScreenControl.mImageControlArrayList.size() == 0) {
//					ToastMaker.showToastShort(R.string.no_meterial_added);
					return false;
				}

				

				Bitmap oldBitmap = mGroundImage.getBitmap();
				Bitmap bitmap = mScreenControl.getBitmapHasText(
						mScreenControl.mImageControlArrayList, null);
				
				mGroundImage.setBitmap(bitmap);
				if (oldBitmap != bitmap) {
					oldBitmap.recycle();
				}

				mScreenControl.clearAddingText();
				mScreenControl.setmIsAddingText(false);

				return true;

			} else {
				mScreenControl.leaveEditing();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

//	private Boolean mEditingText = false;

	// private EditText mEditText;
	// private String preText;
	// private CharSequence title;

//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		// TODO Auto-generated method stub
//
////		if (mEditingText == true) {
////			mScreenControl.onTouch(v, event);
////			if (mGestureDetector.onTouchEvent(event))
////				return true;
////		} else {
//			mScreenControl.onTouch(v, event);
////		}
//		return true;
//	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		if (mScreenControl.mImageControlArrayList == null
				|| mScreenControl.mImageControlArrayList.size() == 0) {
			mGroundImage.onDoubleTap(e);
			return true;
		}
		if (!mScreenControl.mIsEditingText) {
			MyPoint point = new MyPoint(e.getX(), e.getY());
			int id = mScreenControl.findTextTouchedViewId(point);
			//
			if (id == -1) {
				mGroundImage.onDoubleTap(e);
			} else {
				if (id>=0 && id<mScreenControl.mImageControlArrayList.size()) {
					mScreenControl.onEditing(id);
				}
			}
		} 
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

//	@Override
//	public boolean onSingleTapConfirmed(MotionEvent e) {
//		// TODO Auto-generated method stub
////		if (!mScreenControl.mIsEditingText) {
////			MyPoint point = new MyPoint(e.getX(), e.getY());
////			int id = mScreenControl.findTextTouchedViewId(point);
////			//
////			if (id == mScreenControl.currentShow && id >=0 && mScreenControl.mIsCanEnterEditing == true) {
////				mScreenControl.onEditing(id);
////			}
////		}
////		else {
////			if (mScreenControl.mIsSingleTapWithoutMove) {
////				mScreenControl.leaveEditing();
////			}
////		}
//		return true;
//	}

	@Override
	public boolean onCallBack() {
		// TODO Auto-generated method stub
		if (mScreenControl.mIsEditingText == true) {
			mScreenControl.leaveEditing();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		if (!mScreenControl.mIsEditingText) {
			MyPoint point = new MyPoint(e.getX(), e.getY());
			int id = mScreenControl.findTextTouchedViewId(point);
			if (mScreenControl.mIsCanEnterEditing && id>=0 
					&& id<mScreenControl.mImageControlArrayList.size()) {
				mScreenControl.onEditing(id);
			}
		} 
		return true;
	}

}
