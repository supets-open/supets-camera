package cn.jingling.lib.advanceedit.brush;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class DrawStateFactory {
	
	//public static final int 
	
	
	
	
//	public DrawState createDrawState(int brushType, Canvas canvas, ImageView imageView, Bitmap pathBitmap, boolean mIsMosaic) {
//		
//		DrawState mDrawState;
//		
//		if(brushType == 1)
//		{
//			mDrawState = new PenState(imageView,pathBitmap,mIsMosaic);
//		}
//		else if(brushType == 2)
//		{
//			mDrawState = new GlowDrawState(imageView,pathBitmap);
//		}
//		else if(brushType == 3)
//		{
//			mDrawState = new HollowDrawState(imageView,pathBitmap);
//		}
//		else if (brushType == 4)
//		{
//			//mDrawState = new AlphaDrawState(canvas, imageView, pathBitmap);
//		}
//		else if (brushType == 5)
//		{
//			mDrawState = new BlurDrawState( imageView, pathBitmap);
//		}
//		else if (brushType == 6)
//		{
//			mDrawState = new GlowWhiteShadowDrawState(imageView, pathBitmap);
//		}
//		else if(brushType == 100)
//		{
//			int iconId = brush.getResourceId(4, 0);
//			TypedArray iconArray = mContext.getResources().obtainTypedArray(iconId);
//			if (iconArray != null && iconArray.length() > 0) {
//				int length = iconArray.length();
//				Drawable[] drawables = new Drawable[length];
//				for (int i = 0; i < length; i ++) {
//					drawables[i] = iconArray.getDrawable(i);
//				}
//				int space =  brush.getInt(3, 10);
//				mDrawState = new IconDrawState(drawables,space,canvas,imageView,pathBitmap);
//				mDrawState.setPenWidth(mPenWidth);
//			}
//		}
//		
//		return null;
//	}

}
