package cn.jingling.lib.textbubble;

import java.util.Vector;

import cn.jingling.lib.PackageSecurity;
import cn.jingling.lib.utils.LogUtils;


import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.text.Editable;
import android.text.TextUtils.TruncateAt;
import android.text.InputType;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class TextBubble extends TextView {

	public final int FREE_PROPORTION = 1;
	public final int FIXED_PROPORTION = 10;
	private TextBubbleStyleParameter mPara;
	private Matrix mTextMatrix;

	private float mAreaWidth;
	private float mAreaHeight;
	private String initStr;
//	public boolean mIsEditing;
	
	private int mInitTextSize = 30;

//	private Paint mTextPaint;
//	private int mFontSize;

	public TextBubble(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		PackageSecurity.check(context);
	}

	public TextBubble(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		PackageSecurity.check(context);
	}

	public TextBubble(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		PackageSecurity.check(context);
	}

	public TextBubble(Context context, TextBubbleStyleParameter para, float w, float h) {
		super(context);
		// TODO Auto-generated constructor stub
		PackageSecurity.check(context);
		mPara = para;
		mAreaWidth = w;
		mAreaHeight = h;
		getPaint().setShadowLayer(mPara.mShadowRadius, mPara.mShadowX, mPara.mShadowY, mPara.mShadowColor);
		getPaint().setColor(mPara.mTextColor);
		initStr = "点击编辑文字";
//		setCursorVisible(true);
		fitInitText();
	}

	
	public TextBubble(Context context, TextBubbleStyleParameter para, float w, float h, String initString) {
		super(context);
		// TODO Auto-generated constructor stub
		PackageSecurity.check(context);
		mPara = para;
		mAreaWidth = w;
		mAreaHeight = h;
		getPaint().setShadowLayer(mPara.mShadowRadius, mPara.mShadowX, mPara.mShadowY, mPara.mShadowColor);
		getPaint().setColor(mPara.mTextColor);
		initStr = initString;
//		setCursorVisible(true);
		fitInitText();
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		LogUtils.d("textbubble", "111 ondraw");
		canvas.concat(mTextMatrix);
		if (mString!=null && mStrWidth!=null) {
			FontMetrics fm = getPaint().getFontMetrics();
			for (int i = 0; i < mString.size(); i++) {
				//如果有左右对齐，在此计算后设置起始偏移量
				canvas.drawText(mString.get(i), (mAreaWidth-mStrWidth.get(i))/2, 0-fm.top-i*(fm.top-fm.bottom), getPaint());
			}
		} else {
//			if (!mIsEditing) {
				TextPaint tp = new TextPaint(getPaint());
				tp.setTextSize(mInitTextSize);
				FontMetrics fm = tp.getFontMetrics();
				for (int i = 0; i < mInitString.size(); i++) {
					canvas.drawText(mInitString.get(i), (mAreaWidth-mInitStrWidth.get(i))/2, 0-fm.top-i*(fm.top-fm.bottom), tp);
				}
//			}
		}
	}
	public Matrix getmTextMatrix() {
		if (mTextMatrix == null) {
			mTextMatrix = new Matrix();
		}
		return mTextMatrix;
	}

	public void setmTextMatrix(Matrix mTextMatrix) {
		LogUtils.d("textbubble", "000 setmTextMatrix");
		this.mTextMatrix = mTextMatrix;
		invalidate();
		
	}
	
	private Vector<String> mInitString = null;
	private Vector<Float> mInitStrWidth = null;
	private void fitInitText(){
		int textSize = 30;
		if (mInitString!=null) {
			mInitString.clear();
			mInitString = null;
		}
		if (mInitStrWidth!=null) {
			mInitStrWidth.clear();
			mInitStrWidth = null;
		}
		while (isTextSizeFit(initStr, textSize+1, true)) {
			textSize++;
		}
		mInitTextSize = textSize;
//		invalidate();
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		// TODO Auto-generated method stub
		refitText(text.toString(), getText().toString().length(), text.toString().length());
		super.setText(text, type);
	}

	@Override
	protected void onTextChanged(CharSequence text, int start,
			int lengthBefore, int lengthAfter) {
		// TODO Auto-generated method stub
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
	}
	
	
	private Vector<String> mString = null;
	private Vector<Float> mStrWidth = null;
	private void refitText(String str, int before, int after){
		int textSize = (int) this.getPaint().getTextSize();
		boolean mIsFitted = false;
		if (mString!=null) {
			mString.clear();
			mString = null;
		}
		if (mStrWidth!=null) {
			mStrWidth.clear();
			mStrWidth = null;
		}
//		if (str.length() == 0) {
//			return;
//		}
		if (new String(str).trim().length() == 0) {
			return;
		}
		if (before >= after || !mIsFitted) {
			//文字减少，检查现有textSize+1是否会缩小
			while (isTextSizeFit(str, textSize+1, false)) {
				textSize++;
				mIsFitted = true;
			}
		} 
		if(before < after || !mIsFitted){
			//文字增多，检查当前字号能否容纳
			while (!isTextSizeFit(str, textSize, false)){
				textSize --;
				mIsFitted = true;
//				Log.i(TAG, "textSize--:"+textSize);
			}
		}
		if (textSize!=0) {
			this.getPaint().setTextSize(textSize);
		} else {
			TextSizeFit(str, 0.5f, false);
			getPaint().setTextSize(0.5f);
		}
		invalidate();//刷新时考虑只刷新文字所在区域
	}
	

	
	private boolean isTextSizeFit(String string, int textSize, boolean isInitText){
		TextPaint txtPaint = new TextPaint(this.getPaint());
		txtPaint.setTextSize(textSize);
		int length = string.length();

		float width = 0;
		int start = 0;
		
		Vector<String> str = null;
		Vector<Float> strWidth = null;
		int mLineNumber =0;
		str = new Vector<String>();
		strWidth = new Vector<Float>();
		
		FontMetrics fm = txtPaint.getFontMetrics();
		float mFontHeight = (fm.bottom - fm.top);
		for (int i = 0; i < length; i++) {

			char ch = string.charAt(i);
			float[] widths = new float[1];

			String singleStr = String.valueOf(ch);

			txtPaint.getTextWidths(singleStr, widths);

			width += (widths[0]);

			if (ch == '\n') {
				mLineNumber++;
				str.addElement(string.substring(start, i));
				strWidth.addElement((width)-(widths[0]));
				start = i + 1;
				width = 0;
			}
			if (width > mAreaWidth) {
				mLineNumber++;
				str.addElement(string.substring(start, i));
				strWidth.addElement((width-(widths[0])));
				start = i;
				i--;
				width = 0;
			} else {
				if (i == length - 1) {
					mLineNumber++;
					str.add(string.substring(start, length));
					strWidth.addElement((width));
				}
			}
			if (mFontHeight*mLineNumber > mAreaHeight) {
				if (str!=null) {
					str.clear();
					str = null;
				}
				if (strWidth!=null) {
					strWidth.clear();
					strWidth = null;
				}
				return false;
			}
		}

		if (isInitText && str!=null && strWidth!=null) {
			mInitString = str;
			mInitStrWidth = strWidth;
		} else {
			mString = str;
			mStrWidth = strWidth;
		}
		return true;
	}
	private void TextSizeFit(String string, float f, boolean isInitText){
		TextPaint txtPaint = new TextPaint(this.getPaint());
		txtPaint.setTextSize(f);
		int length = string.length();

		int width = 0;
		int start = 0;
		
		Vector<String> str = null;
		Vector<Float> strWidth = null;
		str = new Vector<String>();
		strWidth = new Vector<Float>();
		
		for (int i = 0; i < length; i++) {

			char ch = string.charAt(i);
			float[] widths = new float[1];
			String singleStr = String.valueOf(ch);

			txtPaint.getTextWidths(singleStr, widths);

			width += (widths[0]);

			if (ch == '\n') {
				str.addElement(string.substring(start, i));
				strWidth.addElement(Float.valueOf(width));
				start = i + 1;
				width = 0;
			} else

			if (width > mAreaWidth) {
				i--;
				str.addElement(string.substring(start, i));
				strWidth.addElement(Float.valueOf(width-(widths[0])));
				start = i;
				width = 0;
			} else {
				if (i == length - 1) {
					str.add(string.substring(start, length));
					strWidth.addElement(Float.valueOf(width));
				}
			}
		}

		if (isInitText && str!=null && strWidth!=null) {
			mInitString = str;
			mInitStrWidth = strWidth;
		} else {
			mString = str;
			mStrWidth = strWidth;
		}
		return;
	}
	
	public void setSize(float preTextWidth, float preTextHeight) {
		// TODO Auto-generated method stub
		mAreaHeight = preTextHeight;
		mAreaWidth = preTextWidth;
		
	}
//
//    String inputString="";
//	class MyInputConnection extends BaseInputConnection{
//
//
//        public MyInputConnection(TextBubble targetView, boolean fullEditor) { 
//            super(targetView, fullEditor); 
//            // TODO Auto-generated constructor stub 
//        } 
//        
//        @Override
//        public boolean commitText(CharSequence text, int newCursorPosition){ 
//        	Log.i(TAG, "$$$commitText:"+text);
//            inputString=inputString+(String) text; 
//        	Log.i(TAG, "$$$commitToatalText:"+inputString);
//            setText(inputString);
//            return true; 
//        } 
//        
//    }

//	@Override
//	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
//		// TODO Auto-generated method stub
////		return super.onCreateInputConnection(outAttrs);
//		return new MyInputConnection(this, false);
//	}

}
