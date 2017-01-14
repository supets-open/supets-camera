package com.supets.commons.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * GridView的子类，一般来用来作为ListView/GridView/ScrollView的子View
 */
public class GridCellView extends GridView {

    public GridCellView(Context context) {
        super(context);
    }

    public GridCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridCellView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
