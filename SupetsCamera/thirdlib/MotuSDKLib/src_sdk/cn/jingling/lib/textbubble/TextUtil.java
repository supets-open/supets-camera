package cn.jingling.lib.textbubble;

import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;

public class TextUtil {

//	private static final String TAG = "TextUtil";
	private int mTextPosx;
	private int mTextPosy;
	private int mTextWidth;
	private int mTextHeight;
	private int mFontHeight;
	private int mLineNumber;
//	private int mTextSize;
	private String mStrText = "";
	private Vector<String> mString = null;
	private Paint mPaint = null;

	public static final String ELLIPSIS = "...";

	public TextUtil(String StrText, Paint paint) {

		mStrText = StrText;
//		mTextSize = (int) paint.getTextSize();
		mPaint = paint;
		mString = new Vector<String>();
		mString.clear();
	}

	public void setText(String text) {
		mStrText = text;
	}

	public void setTextRect(RectF rect) {

		mTextPosx = (int) rect.left;
		mTextPosy = (int) rect.top;
		mTextWidth = (int) (rect.right - rect.left);
		mTextHeight = (int) (rect.bottom - rect.top);

		prepareToDraw();
	}

	public void prepareToDraw() {

		mString.clear();
		mLineNumber = 0;

		FontMetrics fm = mPaint.getFontMetrics();
		mFontHeight = (int) (Math.ceil(fm.descent - fm.top));

		mTextPosy -= (int) Math.ceil(fm.top);

		int length = mStrText.length();

		int width = 0;
		int start = 0;

		float[] w = new float[3];

		mPaint.getTextWidths(ELLIPSIS, w);

		int len = ELLIPSIS.length();

		int ellipsisLength = 0;

		for (int i = 0; i < len; i++) {
			ellipsisLength += w[i];
		}
		for (int i = 0; i < length; i++) {
			//取字符
			char ch = mStrText.charAt(i);
			float[] widths = new float[1];
			String str = String.valueOf(ch);

			//计算字符宽度
			mPaint.getTextWidths(str, widths);
			//累加每行宽度
			width += (int) Math.ceil(widths[0]);
			//遇到换行
			if (ch == '\n') {
				//行数加1
				mLineNumber++;
				//mString是每行的字符，加上这一行
				mString.addElement(mStrText.substring(start, i));
				start = i + 1;
				width = 0;
			}
			//计算有超过宽度就换行
			if (width > mTextWidth) {
				mLineNumber++;
				mString.addElement(mStrText.substring(start, i));
				start = i;
				i--;
				width = 0;
			} else {
				if (i == length - 1) {
					mLineNumber++;
					mString.add(mStrText.substring(start, length));
				}
			}
		}
		//计算能容纳的行数
		int realLineNumber = (int) ((mTextHeight + mFontHeight / 8) / mFontHeight);

		if (realLineNumber < mLineNumber) {
			//不能容纳所有文字
			mString.clear();
			mLineNumber = 0;

			length = mStrText.length();

			width = 0;
			start = 0;

			for (int i = 0; i < length; i++) {

				char ch = mStrText.charAt(i);
				float[] widths = new float[1];
				String str = String.valueOf(ch);

				mPaint.getTextWidths(str, widths);

				width += (int) Math.ceil(widths[0]);

				if (mLineNumber >= realLineNumber) {
					break;
				}
				if (mLineNumber == realLineNumber - 1) {

					int cur = width + ellipsisLength;

					if (ch == '\n') {
						mString.addElement(mStrText.substring(start, i)
								+ ELLIPSIS);
						mLineNumber++;
						break;
					}

					if (cur > mTextWidth) {
						if (i > 0) {

							// Log.d(TAG, "start=" + start + " " + i + " " +
							// length);

							if (start >= length) {
								start = length - 1;
							} else if (start < 0) {
								start = 0;
							}

							if (i >= length) {
								i = length - 1;
							}

							try
							{
								mString.addElement(mStrText.substring(start, i - 1)
										+ ELLIPSIS);
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
							// Log.d(TAG, "mLineNumber= " + mLineNumber + " " +
							// cur + " " + ellipsisLength );
							//
							// Log.d(TAG, "mText= " + mStrText.substring(start,
							// i - 1) + ELLIPSIS);
							//
							mLineNumber++;
						}

						break;
					}
				}

				if (ch == '\n') {
					mLineNumber++;
					mString.addElement(mStrText.substring(start, i));
					start = i + 1;
					width = 0;
				}

				if (width > mTextWidth) {

					mLineNumber++;
					mString.addElement(mStrText.substring(start, i));
					start = i;
					i--;
					width = 0;
				}
			}
		}
	}

	public void drawText(Canvas canvas) {

		for (int i = 0; i < mLineNumber; i++) {

			canvas.drawText((String) (mString.elementAt(i)), mTextPosx,
					mTextPosy + mFontHeight * i, mPaint);
		}
	}

}
