package cn.jingling.lib.textbubble;

import java.io.BufferedInputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TextBubbleWidget {

	private static final String label = "文字泡泡";
	private ScreenControl mScreenControl;
	private Activity mActivity;
	private AddingBubbleEffect mAddingBubbleEffect;
	
	private onEditingCallback mOnEditingCallback;

	private int bubbleConfigArrayRes;
	
	
	public TextBubbleWidget(Activity activity, int operaterDrawableRes, int delDrawableRes, int bubbleConfigArray){
		mScreenControl = ScreenControl.getSingleton();
		mScreenControl.setAccessoryDrawableRes(operaterDrawableRes, delDrawableRes);
		mActivity = activity;
		bubbleConfigArrayRes = bubbleConfigArray;
	}

	public void onClick(int index){
		performAdd(mActivity, index);
	}
	
	

	
	public void init(Bitmap bm, ImageView imageView, RelativeLayout imageContainer){
		ScreenInfo.setScreenInfo(mActivity);
		mScreenControl.initWithActivity(mActivity, bm, imageView, imageContainer);
		mAddingBubbleEffect = new AddingBubbleEffect();
		mAddingBubbleEffect.perform();
		
	}
	
	public void release() {
		mScreenControl.release();
		mAddingBubbleEffect.release();
		setOnEditingCallback(null);
	}
	

	/**
	 * 添加默认的文字泡泡
	 * @param mContext
	 * @param position
	 */
	public void performAdd(Context mContext, int position){
		try{
			Resources resources = mContext.getResources();
			TypedArray mIds = resources.obtainTypedArray(bubbleConfigArrayRes);
			TypedArray itemArray = resources.obtainTypedArray(mIds.getResourceId(position, 0));
			
			
			String pngName = itemArray.getString(0);
			AssetManager mAssetManager = mContext.getAssets();
			BufferedInputStream buf = new BufferedInputStream(
					mAssetManager.open("images/bubble_hq/"
							+ pngName));
			Bitmap bitmap = BitmapFactory.decodeStream(buf);
			buf.close();
			
			TextBubbleStyleParameter mTextBubbleStyleParameter = new TextBubbleStyleParameter(itemArray);
			mTextBubbleStyleParameter.position = position;
			
			if(mTextBubbleStyleParameter.position != ScreenControl.getSingleton().selectTextBubbleID){
				if (mAddingBubbleEffect != null){
					mAddingBubbleEffect.onAdding(bitmap, mTextBubbleStyleParameter);
				}
			}
			
			if(mIds != null){
				mIds.recycle();	
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public Bitmap onSave(){
		if (mAddingBubbleEffect != null) {
			mAddingBubbleEffect.onOk();
		}
		mScreenControl.mIsEffectMode = false;
		return mScreenControl.getGroundImageBitmap();
	}
	
	public interface onEditingCallback{
		void onEditingNewActivity(String editString);
	}
	
	public void setOnEditingCallback(onEditingCallback callback){
		mScreenControl.setOnEditingCallback(callback);
	}
	
	
	public void leaveEditing(){
		mScreenControl.leaveEditing();
	}
	
	public void leaveEditing(String str){
		mScreenControl.leaveEditing(str);
	}
}