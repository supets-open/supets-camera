package com.supets.commons.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Span相关工具类
 *
 * <p>
 *     提供一个{@link com.supets.commons.utils.SpanUtils.Builder}类来构建SpannableString
 * </p>
 *
 * @author Created by FengZuyan on 2015/12/23.
 */
public class SpanUtils {

    /**
     * 用来构建SpannableString的构建器
     */
    public static class Builder {

        private CharSequence mText;
        private String mRegex;
        private int mStart;
        private int mEnd;

        private SpanParams mParams;

        /**
         * 用正则表达式创建Builder的构造函数
         *
         * @param text 用来生成SpannableString的原始文本串
         * @param regex 要设置span的正则表达式
         */
        public Builder(CharSequence text, String regex) {
            this.mText = text;
            this.mRegex = regex;
            mParams = new SpanParams();
        }

        /**
         * 用开始索引创建Builder的构造函数
         *
         * @param text 用来生成SpannableString的原始文本串
         * @param start 要设置span的开始索引，结束索引为原始文本串的末尾
         */
        public Builder(CharSequence text, int start) {
            this(text, start, text == null ? 0 : text.length());
        }

        /**
         * 用起止索引创建Builder的构造函数
         *
         * @param text 用来生成SpannableString的原始文本串
         * @param start 要设置span的开始索引
         * @param end 要设置span的结束索引
         */
        public Builder(CharSequence text, int start, int end) {
            this.mText = text;
            this.mStart = start;
            this.mEnd = end;
            mParams = new SpanParams();
        }

        /**
         * 根据指定size(px)添加一个AbsoluteSizeSpan
         *
         * @param size Px size
         * @return {@link com.supets.commons.utils.SpanUtils.Builder}
         */
        public Builder setSize(int size) {
            mParams.size = size;
            return this;
        }

        /**
         * 根据指定size添加一个AbsoluteSizeSpan
         *
         * @param size Size for span
         * @param dip True if dp, otherwise
         * @return {@link com.supets.commons.utils.SpanUtils.Builder}
         */
        public Builder setSize(int size, boolean dip) {
            mParams.size = dip ? com.supets.commons.utils.UIUtils.dp2px(size) : size;
            return this;
        }

        /**
         * 根据指定size(dp)添加一个AbsoluteSizeSpan
         *
         * @param size Dip size
         * @return {@link com.supets.commons.utils.SpanUtils.Builder}
         */
        public Builder setSizeDip(int size) {
            return setSize(size, true);
        }

        /**
         * 根据指定size res添加一个AbsoluteSizeSpan
         *
         * @param sizeRes Size resource id
         * @return {@link com.supets.commons.utils.SpanUtils.Builder}
         */
        public Builder setSizeRes(int sizeRes) {
            mParams.size = com.supets.commons.utils.UIUtils.getDimension(sizeRes);
            return this;
        }

        /**
         * 根据指定<code>color</code>添加一个ForegroundColorSpan
         *
         * @param color Color for 添加一个ForegroundColorSpan
         * @return {@link com.supets.commons.utils.SpanUtils.Builder}
         */
        public Builder setColor(int color) {
            mParams.color = color;
            return this;
        }

        /**
         * 根据指定<code>colorRes</code>添加一个ForegroundColorSpan
         *
         * @param colorRes Color resource id
         * @return {@link com.supets.commons.utils.SpanUtils.Builder}
         */
        public Builder setColorRes(int colorRes) {
            mParams.color = com.supets.commons.utils.UIUtils.getColor(colorRes);
            return this;
        }

        /**
         * 根据指定<code>style</code>添加一个{@link StyleSpan}
         *
         * @param style 描述Style的一个整型常量. 例如加粗、倾斜、正常。这些常量值定义在 {@link android.graphics.Typeface}.
         * @return {@link com.supets.commons.utils.SpanUtils.Builder}
         */
        public Builder setStyle(int style) {
            mParams.style = style;
            return this;
        }

        /**
         * 添加一个带删除线的Span
         *
         * @return {@link com.supets.commons.utils.SpanUtils.Builder}
         */
        public Builder withDeleteLine() {
            return withDeleteLine(true);
        }

        /**
         * 根据<code>deleteLine</code>参数决定是否添加一个带删除线的Span
         *
         * @return {@link com.supets.commons.utils.SpanUtils.Builder}
         */
        public Builder withDeleteLine(boolean deleteLine) {
            mParams.deleteLine = deleteLine;
            return this;
        }

        /**
         * 指定一个Span
         * @param span
         * @return {@link com.supets.commons.utils.SpanUtils.Builder}
         */
        public Builder setSpan(Object span) {
            mParams.span = span;
            return this;
        }

        /**
         * 根据指定的设置构建一个SpannableString
         *
         * @return SpannableString
         */
        public SpannableString build() {
            if (mText == null) {
                return null;
            }

            SpannableString spanString = mText instanceof SpannableString ? (SpannableString) mText : new SpannableString(mText);

            int start = 0;
            int end = 0;

            if (!TextUtils.isEmpty(mRegex)) {
                Matcher matcher = Pattern.compile(mRegex).matcher(mText);
                if (matcher.find()) {
                    start = matcher.start();
                    end = matcher.end();
                }
            } else if (mStart != mEnd && mEnd > mStart) {
                start = mStart;
                end = mEnd;
            }

            if (start != end && end > start) {
                int flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

                if (mParams.color != 0) {
                    spanString.setSpan(new ForegroundColorSpan(mParams.color), start, end, flag);
                }
                if (mParams.size > 0) {
                    spanString.setSpan(new AbsoluteSizeSpan(mParams.size), start, end, flag);
                }
                if (mParams.deleteLine) {
                    spanString.setSpan(new StrikethroughSpan(), start, end, flag);
                }
                if (mParams.style >= 0) {
                    spanString.setSpan(new StyleSpan(mParams.style), start, end, flag);
                }
                if (mParams.span != null) {
                    spanString.setSpan(mParams.span, start, end, flag);
                }
            }

            return spanString;
        }

    }

    private static class SpanParams {
        int size; // px
        int color;// color value
        int style;// bold...
        boolean deleteLine;
        Object span;
    }

}
