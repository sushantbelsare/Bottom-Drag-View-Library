package com.savantech.bottomdragview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Sushant on 9/5/2017.
 */

public class BottomView extends LinearLayout {

    private ViewDragHelper mDragHelper;
    private View view;
    private int mDragBorder,verticalRange,mDragState,peekHeight,mDragHeight;
    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;
    private boolean inflate=false,isExpanded=false,isDragHeightSet=false;
    private MotionEvent globalEvent;
    private dragListener listener;

    public BottomView(Context context) {
        super(context);
    }
    public BottomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    public BottomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }

    void initView(Context context,AttributeSet attrs)
    {
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.BottomView);
        peekHeight = array.getDimensionPixelOffset(R.styleable.BottomView_peekHeight,0);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDragHelper = ViewDragHelper.create(this,1.0f,new DragHelperCallback());
        view = getChildAt(0);
    }

    @Override
    protected void onLayout(boolean b, int left, int top, int right, int bottom) {
        verticalRange = getMeasuredHeight() - peekHeight;
        if(!inflate)
        {
            mDragBorder = verticalRange;
            inflate = true;
        }
        view.layout(left,mDragBorder,right,bottom+mDragBorder);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        int action = MotionEventCompat.getActionMasked(ev);

        if((action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) && !isDraggingAllowed(ev))
        {
            mDragHelper.cancel();
            return false;
        }

        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(isDraggingAllowed(event) || isMoving())
        {
            mDragHelper.processTouchEvent(event);
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        globalEvent = ev;
        return super.dispatchTouchEvent(ev);
    }

    boolean isDraggingAllowed(MotionEvent event)
    {
        int[] viewLocations = new int[2];
        view.getLocationOnScreen(viewLocations);
        int upperLimit = viewLocations[1] + (isDragHeightSet?mDragHeight:peekHeight);
        int lowerLimit = viewLocations[1];
        int y = (int)event.getRawY();
        return (y > lowerLimit && y < upperLimit);
    }

    boolean isMoving() {
        return (mDragState == ViewDragHelper.STATE_DRAGGING ||
                mDragState == ViewDragHelper.STATE_SETTLING);
    }

    class DragHelperCallback extends ViewDragHelper.Callback
    {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            mDragState = state;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return verticalRange;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            boolean settleToOpen = false;
            if (yvel > AUTO_OPEN_SPEED_LIMIT && xvel<yvel) {
                settleToOpen = true;
            } else if (yvel < -AUTO_OPEN_SPEED_LIMIT && xvel>yvel) {
                settleToOpen = false;
            } else if (mDragBorder > (2*verticalRange / 3)) {
                settleToOpen = true;
            } else if (mDragBorder < (verticalRange / 3)) {
                settleToOpen = false;
            }

            final int settleDestY = settleToOpen ? verticalRange : 0;
            isExpanded = settleToOpen ? false : true;
            if(mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), settleDestY)) {
                ViewCompat.postInvalidateOnAnimation(BottomView.this);
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            mDragBorder = top < 0 ? 0 : top > verticalRange ? verticalRange : top;
            float offset = 1-((float)mDragBorder/verticalRange);
            if(listener!=null) listener.onDrag(offset);
            setBackgroundColor(ColorUtils.setAlphaComponent(Color.BLACK,Math.round(120*offset)));
            requestLayout();
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public boolean expandOnTouchView()
    {
        if(isDraggingAllowed(globalEvent)&& mDragHelper.smoothSlideViewTo(view,0,0)) {
            isExpanded = true;
            ViewCompat.postInvalidateOnAnimation(BottomView.this);
            return true;
        }
        return false;
    }

    public boolean expandView()
    {
        if(mDragHelper.smoothSlideViewTo(view,0,0)) {
            isExpanded = true;
            ViewCompat.postInvalidateOnAnimation(BottomView.this);
            return true;
        }
        return false;
    }

    public boolean collapseOnTouchView()
    {
        if(isDraggingAllowed(globalEvent)&& mDragHelper.smoothSlideViewTo(view,0,verticalRange)) {
            isExpanded = false;
            ViewCompat.postInvalidateOnAnimation(BottomView.this);
            return true;
        }
        return false;
    }

    public boolean collapseView()
    {
        if(mDragHelper.smoothSlideViewTo(view,0,verticalRange)) {
            isExpanded = false;
            ViewCompat.postInvalidateOnAnimation(BottomView.this);
            return true;
        }
        return false;
    }

    public boolean isViewExpanded()
    {
        return isExpanded;
    }

    public void setPeekHeight(int peekHeight)
    {
        isDragHeightSet = false;
        this.peekHeight = peekHeight;
        if(mDragHelper!=null && mDragHelper.smoothSlideViewTo(view,0,getMeasuredHeight()-peekHeight))
        {
            ViewCompat.postInvalidateOnAnimation(this);
        }
        else
        {
            invalidate();
        }
    }

    public void dragHeight(int mDragHeight)
    {
        isDragHeightSet = true;
        this.mDragHeight = mDragHeight;
    }

    public void setDragListener(dragListener listener)
    {
        this.listener = listener;
    }

    public interface dragListener
    {
        void onDrag(float offset);
    }
}
