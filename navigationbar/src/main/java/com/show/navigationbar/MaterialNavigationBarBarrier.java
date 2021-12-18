package com.show.navigationbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


public class MaterialNavigationBarBarrier extends View {

    private final int circleRadius;
    private final float minStroke;

    public MaterialNavigationBarBarrier(Context context) {
        this(context,null);
    }

    public MaterialNavigationBarBarrier(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MaterialNavigationBarBarrier(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        circleRadius = DisplayUtil.dp2px(context, 28);
        minStroke = DisplayUtil.dp2px(context, 15);
        setFocusable(false);
        setClickable(false);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        switch (MeasureSpec.getMode(heightSpec)) {
            case MeasureSpec.EXACTLY:
                int heightSize = MeasureSpec.getSize(heightSpec);
                heightSize = (int) (heightSize - circleRadius *  2 - minStroke);
                heightSpec = MeasureSpec.makeMeasureSpec(heightSize,MeasureSpec.EXACTLY);
                break;
            case MeasureSpec.AT_MOST:
                heightSpec = MeasureSpec.makeMeasureSpec((int) (circleRadius * 2 - minStroke), MeasureSpec.EXACTLY);
                break;
            case MeasureSpec.UNSPECIFIED:
                heightSpec = MeasureSpec.makeMeasureSpec((int) (circleRadius * 2 - minStroke), MeasureSpec.EXACTLY);
                break;
        }
        super.onMeasure(widthSpec, heightSpec);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) { }

}
