package com.show.navigationbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MaterialNavigationBar extends FrameLayout {

    private static final float CONTROL_x1 = 0.3f;
    private static final float CONTROL_x2 = 0.4f;
    private static final float CONTROL_x3 = 0.5f;
    private static final float CONTROL_x4 = 0.6f;
    private static final float CONTROL_x5 = 0.7f;

    private static final float CONTROL_y1 = 0.10f;
    private static final float CONTROL_y2 = 0.15f;
    private static final float CONTROL_y3 = 0.20f;
    private static final float CONTROL_y4 = 0.35f;


    private static final long DURATION = 350;

    private static final int SWITCH_PROGRESS_START = 0;
    private static final int SWITCH_PROGRESS = 1;
    private static final int SWITCH_PROGRESS_END = 2;

    private final Object object = new Object();

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mShaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mBackgroundColor = Color.WHITE;
    private int mCircleBgColor = Color.WHITE;

    private final List<NavigationItem> navigationItemList = new ArrayList<>();
    private final List<OnItemSelectedListener> itemSelectedListeners = new ArrayList<>();

    private final long mDuration = DURATION;

    private boolean alwaysShowText = true;

    private RecyclerView mRecyclerView;
    private MaterialNavigationAdapter adapter;

    private int mSwitchState = SWITCH_PROGRESS_START;
    private final int circleRadius;
    private final float minStroke;
    private int itemSize = 0;

    private float widthSize;
    private float mItemWidth;
    private float mItemHeight;
    private float mItemMiddle;
    private float mSelectedPosition = 0;
    private final Path mPath = new Path();
    private final RectF mRect = new RectF();


    private final AccelerateDecelerateInterpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private final LinearInterpolator mLinearInterpolator = new LinearInterpolator();
    private ValueAnimator valueAnimator;
    private ValueAnimator colorAnimator;
    private final ArgbEvaluator evaluator = new ArgbEvaluator();

    public MaterialNavigationBar(@NonNull Context context) {
        this(context, null);
    }

    public MaterialNavigationBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialNavigationBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        circleRadius = DisplayUtil.dp2px(context, 28);
        minStroke = DisplayUtil.dp2px(context, 15);
        setClipChildren(false);
        setWillNotDraw(false);
        resetBackground();
        initView();
        initAttrs(attrs);
    }


    private void resetBackground() {
        Drawable mBackground = getBackground();
        if (mBackground instanceof ColorDrawable) {
            mBackgroundColor = ((ColorDrawable) mBackground.mutate()).getColor();
        }
        setBackgroundColor(Color.TRANSPARENT);
        mPaint.setColor(mBackgroundColor);
        mPaint.setShadowLayer(5, 0, 0, 0x3c000000);
        mCircleBgColor = mBackgroundColor;
    }

    private void initView() {
        if (mRecyclerView == null) {
            mRecyclerView = new RecyclerView(getContext());
        }
        adapter = new MaterialNavigationAdapter();
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRecyclerView.setClipChildren(false);
        MarginLayoutParams layoutParams = new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.topMargin = DisplayUtil.dp2px(getContext(), 56);
        addViewInLayout(mRecyclerView, 0, layoutParams, false);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                smoothScrollToPosition(position,true);
            }
        });
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.MaterialNavigationBar);
        alwaysShowText = array.getBoolean(R.styleable.MaterialNavigationBar_isAlwaysShowText, true);

        array.recycle();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        switch (MeasureSpec.getMode(widthSpec)) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
                widthSpec = MeasureSpec.makeMeasureSpec(Math.min(MeasureSpec.getSize(widthSpec), circleRadius * 3), MeasureSpec.EXACTLY);
                break;
            case MeasureSpec.UNSPECIFIED:
                widthSpec = MeasureSpec.makeMeasureSpec(circleRadius * 3, MeasureSpec.EXACTLY);
                break;
        }
        switch (MeasureSpec.getMode(heightSpec)) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
                heightSpec = MeasureSpec.makeMeasureSpec(circleRadius * 4 + 15, MeasureSpec.EXACTLY);
                break;
            case MeasureSpec.UNSPECIFIED:
                heightSpec = MeasureSpec.makeMeasureSpec(circleRadius * 4 + 15, MeasureSpec.EXACTLY);
                break;
        }

        float heightSize = MeasureSpec.getSize(heightSpec);
        widthSize = MeasureSpec.getSize(widthSpec);


        mItemWidth = widthSize / itemSize;
        mItemHeight = circleRadius * 2f;
        mItemMiddle = mItemWidth / 2f;
        mRect.set(0f, mItemHeight, widthSize, heightSize);

        super.onMeasure(widthSpec, heightSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (itemSize == 0) {
            canvas.drawRect(mRect.left, mRect.top, mRect.right, mRect.bottom, mPaint);
            return;
        }
        float itemLeft = (float) (mSelectedPosition * mItemWidth);
        float itemRight = (float) (itemLeft + mItemWidth);

        mPath.reset();
        drawBackground(canvas, itemLeft, itemRight);
        mPath.reset();
        drawCircle(canvas, itemLeft);
    }

    private void drawCircle(Canvas canvas, float itemLeft) {
        mPaint.setColor(mCircleBgColor);
        canvas.drawCircle(itemLeft + mItemMiddle, mRect.top - minStroke, circleRadius, mPaint);
    }

    private void drawBackground(Canvas canvas, float itemLeft, float itemRight) {
        mPaint.setColor(mBackgroundColor);
        float top = mRect.top;
        mPath.moveTo(0, mRect.top);
        mPath.lineTo(itemLeft, mRect.top);
        mPath.cubicTo(
                itemLeft + mItemMiddle * CONTROL_x1,
                top,
                itemLeft + mItemMiddle * CONTROL_x2,
                top + mItemHeight * CONTROL_y1,
                itemLeft + mItemMiddle * CONTROL_x3,
                top + mItemHeight * CONTROL_y2);
        mPath.cubicTo(
                itemLeft + mItemMiddle * CONTROL_x4,
                top + mItemHeight * CONTROL_y3,
                itemLeft + mItemMiddle * CONTROL_x5,
                top + mItemHeight * CONTROL_y4,
                itemLeft + mItemMiddle,
                top + mItemHeight * CONTROL_y4);
        mPath.cubicTo(
                itemLeft + mItemMiddle * (2 - CONTROL_x5),
                top + mItemHeight * CONTROL_y4,
                itemLeft + mItemMiddle * (2 - CONTROL_x4),
                top + mItemHeight * CONTROL_y3,
                itemLeft + mItemMiddle * (2 - CONTROL_x3),
                top + mItemHeight * CONTROL_y2);
        mPath.cubicTo(itemLeft + mItemMiddle * (2 - CONTROL_x2),
                top + mItemHeight * CONTROL_y1,
                itemLeft + mItemMiddle * (2 - CONTROL_x1),
                top,
                itemRight,
                top);
        mPath.lineTo(mRect.right, top);
        mPath.lineTo(mRect.right, mRect.bottom);
        mPath.lineTo(0, mRect.bottom);
        canvas.drawPath(mPath, mPaint);


    }





    public boolean smoothToPosition(int position) {
        return smoothScrollToPosition(position,false);
    }

    private boolean smoothScrollToPosition(int position,boolean fromUser){
        if (position >= itemSize) {
            position = itemSize - 1;
        }
        if (this.mSelectedPosition != position && mSwitchState != SWITCH_PROGRESS) {
            switchCircleColor((int) mSelectedPosition, position);
            createItemAnimation(mSelectedPosition, position, true);
            createAnimatorWhenNeed(this.mSelectedPosition, position,fromUser);
            return true;
        } else {
            return false;
        }
    }

    private void switchCircleColor(int lastPosition, int nextPosition) {
        NavigationItem last = navigationItemList.get(lastPosition);
        if (lastPosition == nextPosition) {
            mCircleBgColor = last.getSelectedTintColor();
            postInvalidate();
            return;
        }
        NavigationItem next = navigationItemList.get(nextPosition);
        if (last.getSelectedTintColor() != 0 && next.getSelectedTintColor() != 0) {
            synchronized (object) {
                if (colorAnimator != null) {
                    colorAnimator.cancel();
                    colorAnimator.removeAllListeners();
                    colorAnimator = null;
                }
                colorAnimator = ValueAnimator.ofInt(last.getSelectedTintColor(), next.getSelectedTintColor());
                colorAnimator.setDuration(mDuration);
                colorAnimator.setEvaluator(evaluator);
                colorAnimator.setInterpolator(mLinearInterpolator);
                colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mCircleBgColor = (int) animation.getAnimatedValue();
                    }
                });
                colorAnimator.start();
            }
        }
    }


    private void createItemAnimation(float mSelectedPosition, int position, boolean animated) {
        synchronized (object) {
            int lastPosition = (int) mSelectedPosition;
            GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
            if (layoutManager != null) {
                FrameLayout nextView = (FrameLayout) layoutManager.getChildAt(position);
                if (nextView != null) {
                    starItemViewAnimation(position, nextView, true, animated);
                }
                FrameLayout lastView = (FrameLayout) layoutManager.getChildAt(lastPosition);
                if (lastView != null && lastView != nextView) {
                    starItemViewAnimation(lastPosition, lastView, false, animated);
                }
            }
        }
    }

    private void starItemViewAnimation(int position, FrameLayout layout, boolean forward, boolean animated) {
        ImageView imageView = layout.findViewById(R.id.material_item_image);
        TextView textView = layout.findViewById(R.id.material_item_text);
        float targetY = 0f;
        if (forward) {
            float imageHeight = imageView.getHeight() / 2f;
            float cy = imageView.getTop() + minStroke + imageHeight;
            targetY = -(cy);
        }
        if (animated) {
            imageView.animate().translationY(targetY).setDuration(mDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            if (!forward) {
                                imageView.setSelected(false);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (forward) {
                                imageView.setSelected(true);
                            }
                        }
                    })
                    .start();

            if (!alwaysShowText) {
                float alpha = 0f;
                if (forward) {
                    alpha = 1f;
                }
                textView.animate().alpha(alpha)
                        .setDuration(mDuration)
                        .start();
            }
        } else {
            imageView.setTranslationY(targetY);
            imageView.setSelected(forward);
        }
    }

    private void createAnimatorWhenNeed(float start, float end,boolean fromUser) {
        synchronized (object) {
            if (mSwitchState == SWITCH_PROGRESS) {
                return;
            }
            dispatchSelectedListener((int) start,(int)end,fromUser);
            if (valueAnimator != null && valueAnimator.isRunning()) {
                valueAnimator.cancel();
                valueAnimator.removeAllListeners();
                valueAnimator.removeAllUpdateListeners();
                valueAnimator = null;
            }
            valueAnimator = ValueAnimator.ofFloat(start, end);
            valueAnimator.setDuration(mDuration);
            valueAnimator.setInterpolator(mInterpolator);
            valueAnimator.addUpdateListener(animation -> {
                mSwitchState = SWITCH_PROGRESS;
                mSelectedPosition = (float) animation.getAnimatedValue();
                postInvalidate();
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    mSwitchState = SWITCH_PROGRESS_END;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mSwitchState = SWITCH_PROGRESS_END;

                }

                @Override
                public void onAnimationStart(Animator animation) {
                    mSwitchState = SWITCH_PROGRESS_START;
                }
            });
            valueAnimator.start();
        }
    }

    private void dispatchSelectedListener(int start, int end,boolean fromUser) {
        Iterator<OnItemSelectedListener> iterator = itemSelectedListeners.iterator();
        while (iterator.hasNext()){
            OnItemSelectedListener listener = iterator.next();
            if(listener!=null){
                listener.onUnSelected(start,fromUser);
                listener.onSelected(end,fromUser);
            }
        }
    }


    public NavigationItem getNavigationItem(int position) {
        return navigationItemList.get(position);
    }

    public void setNavigationItem(int position, NavigationItem item) {
        navigationItemList.set(position, item);
        adapter.notifyDataSetChanged();
        postInvalidate();
    }

    public void addItem(List<NavigationItem> items) {
        navigationItemList.clear();
        navigationItemList.addAll(items);
        updateNavigationItem();
        postInvalidate();
    }


    private void updateNavigationItem() {
        itemSize = navigationItemList.size();
        mItemWidth = widthSize / itemSize;
        addItemView();
    }

    private void addItemView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), itemSize));
        adapter.notifyDataSetChanged();
        mRecyclerView.post(() -> {
            switchCircleColor(0, 0);
            createItemAnimation(0, 0, false);
        });
    }


    public static class NavigationItem {

        public NavigationItem(Drawable drawable, String label) {
            this.drawable = drawable;
            this.label = label;
        }

        public NavigationItem(Drawable drawable) {
            this.drawable = drawable;
        }

        private Drawable drawable;
        private String label;
        private int setSelectedTintColor;


        public Drawable getDrawable() {
            return drawable;
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }

        public int getSelectedTintColor() {
            return setSelectedTintColor;
        }

        public NavigationItem setSelectedTintColor(int setSelectedTintColor) {
            this.setSelectedTintColor = setSelectedTintColor;
            return this;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }


    public interface OnItemSelectedListener {

        void onUnSelected(int position, boolean fromUser);

        void onSelected(int position, boolean fromUser);
    }

    public void addOnItemSelectedListener(OnItemSelectedListener navigationItemSelectedListener) {
        if (navigationItemSelectedListener != null) {
            itemSelectedListeners.add(navigationItemSelectedListener);
        }
    }

    public void removeOnItemSelectedListener(OnItemSelectedListener navigationItemSelectedListener) {
        if (navigationItemSelectedListener != null) {
            itemSelectedListeners.add(navigationItemSelectedListener);
        }
    }

    private class MaterialNavigationAdapter extends RecyclerView.Adapter<MaterialNavigationAdapter.ViewHolder> {

        private final int dp_12 = DisplayUtil.dp2px(getContext(), 12);

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.material_item_layout, parent, false);
            if (alwaysShowText) {
                TextView textView = view.findViewById(R.id.material_item_text);
                FrameLayout.LayoutParams params = (LayoutParams) textView.getLayoutParams();
                params.setMargins(0, dp_12, 0, 0);

                ImageView imageView = view.findViewById(R.id.material_item_image);
                params = (LayoutParams) imageView.getLayoutParams();
                params.setMargins(0, 0, 0, dp_12);
            }
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            NavigationItem item = getItemData(position);
            ImageView imageView = holder.itemView.findViewById(R.id.material_item_image);
            imageView.setImageDrawable(item.drawable);
            imageView.setSelected(mSelectedPosition == position);

            TextView textView = holder.itemView.findViewById(R.id.material_item_text);
            textView.setText(item.getLabel());
            textView.setTextColor(item.getSelectedTintColor());

            if (alwaysShowText || mSelectedPosition == position) {
                textView.setAlpha(1f);
            } else {
                textView.setAlpha(0f);
            }


            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(holder.getLayoutPosition());
                    }
                }
            });
        }

        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public int getItemCount() {
            return navigationItemList.size();
        }

        private NavigationItem getItemData(int position) {
            return navigationItemList.get(position);
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }


    private interface OnItemClickListener {
        void onClick(int position);
    }

}
