package com.chauthai.swipereveallayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.vocabmaster.app.R;

@SuppressLint("RtlHardcoded")
public class SwipeRevealLayout extends ViewGroup {
    protected static final int STATE_CLOSE     = 0;
    protected static final int STATE_CLOSING   = 1;
    protected static final int STATE_OPEN      = 2;
    protected static final int STATE_OPENING   = 3;
    protected static final int STATE_DRAGGING  = 4;

    private static final int DEFAULT_MIN_FLING_VELOCITY = 300;
    private static final int DEFAULT_MIN_DIST_REQUEST_DISALLOW_PARENT = 1;

    public static final int DRAG_EDGE_LEFT =   0x1;
    public static final int DRAG_EDGE_RIGHT =  0x1 << 1;
    public static final int DRAG_EDGE_TOP =    0x1 << 2;
    public static final int DRAG_EDGE_BOTTOM = 0x1 << 3;

    public static final int MODE_NORMAL = 0;
    public static final int MODE_SAME_LEVEL = 1;

    private View mMainView;
    private View mSecondaryView;

    private Rect mRectMainClose = new Rect();
    private Rect mRectMainOpen  = new Rect();
    private Rect mRectSecClose  = new Rect();
    private Rect mRectSecOpen   = new Rect();

    private int mMinDistRequestDisallowParent = 0;
    private boolean mIsOpenBeforeInit = false;
    private volatile boolean mAborted = false;
    private volatile boolean mIsScrolling = false;
    private volatile boolean mLockDrag = false;

    private int mMinFlingVelocity = DEFAULT_MIN_FLING_VELOCITY;
    private int mState = STATE_CLOSE;
    private int mMode = MODE_NORMAL;
    private int mLastMainLeft = 0;
    private int mLastMainTop  = 0;
    private int mDragEdge = DRAG_EDGE_LEFT;
    private float mDragDist = 0;
    private float mPrevX = -1;
    private float mPrevY = -1;

    private ViewDragHelper mDragHelper;
    private GestureDetectorCompat mGestureDetector;
    private DragStateChangeListener mDragStateChangeListener;
    private SwipeListener mSwipeListener;
    private int mOnLayoutCount = 0;

    interface DragStateChangeListener {
        void onDragStateChanged(int state);
    }

    public interface SwipeListener {
        void onClosed(SwipeRevealLayout view);
        void onOpened(SwipeRevealLayout view);
        void onSlide(SwipeRevealLayout view, float slideOffset);
    }

    public SwipeRevealLayout(Context context) {
        super(context);
        init(context, null);
    }

    public SwipeRevealLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isDragLocked()) {
            return super.onInterceptTouchEvent(ev);
        }
        mDragHelper.processTouchEvent(ev);
        mGestureDetector.onTouchEvent(ev);
        accumulateDragDist(ev);

        boolean couldBecomeClick = couldBecomeClick(ev);
        boolean settling = mDragHelper.getViewDragState() == ViewDragHelper.STATE_SETTLING;
        boolean idleAfterScrolled = mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE && mIsScrolling;

        mPrevX = ev.getX();
        mPrevY = ev.getY();
        return !couldBecomeClick && (settling || idleAfterScrolled);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() >= 2) {
            mSecondaryView = getChildAt(0);
            mMainView = getChildAt(1);
        } else if (getChildCount() == 1) {
            mMainView = getChildAt(0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAborted = false;
        for (int index = 0; index < getChildCount(); index++) {
            final View child = getChildAt(index);
            int left, right, top, bottom;
            left = right = top = bottom = 0;
            final int minLeft = getPaddingLeft();
            final int maxRight = Math.max(r - getPaddingRight() - l, 0);
            final int minTop = getPaddingTop();
            final int maxBottom = Math.max(b - getPaddingBottom() - t, 0);
            int measuredChildHeight = child.getMeasuredHeight();
            int measuredChildWidth = child.getMeasuredWidth();
            final LayoutParams childParams = child.getLayoutParams();

            if (childParams != null) {
                if (childParams.height == LayoutParams.MATCH_PARENT) measuredChildHeight = maxBottom - minTop;
                if (childParams.width == LayoutParams.MATCH_PARENT) measuredChildWidth = maxRight - minLeft;
            }

            switch (mDragEdge) {
                case DRAG_EDGE_RIGHT:
                    left = Math.max(r - measuredChildWidth - getPaddingRight() - l, minLeft);
                    top = Math.min(getPaddingTop(), maxBottom);
                    right = Math.max(r - getPaddingRight() - l, minLeft);
                    bottom = Math.min(measuredChildHeight + getPaddingTop(), maxBottom);
                    break;
                case DRAG_EDGE_LEFT:
                    left = Math.min(getPaddingLeft(), maxRight);
                    top = Math.min(getPaddingTop(), maxBottom);
                    right = Math.min(measuredChildWidth + getPaddingLeft(), maxRight);
                    bottom = Math.min(measuredChildHeight + getPaddingTop(), maxBottom);
                    break;
                default:
                    left = Math.min(getPaddingLeft(), maxRight);
                    top = Math.min(getPaddingTop(), maxBottom);
                    right = Math.min(measuredChildWidth + getPaddingLeft(), maxRight);
                    bottom = Math.min(measuredChildHeight + getPaddingTop(), maxBottom);
            }
            child.layout(left, top, right, bottom);
        }
        initRects();
        if (mIsOpenBeforeInit) open(false); else close(false);
        mLastMainLeft = mMainView.getLeft();
        mLastMainTop = mMainView.getTop();
        mOnLayoutCount++;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() < 2) throw new RuntimeException("Layout must have two children");
        for (int i = 0; i < getChildCount(); i++) measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) ViewCompat.postInvalidateOnAnimation(this);
    }

    public void open(boolean animation) {
        mIsOpenBeforeInit = true; mAborted = false;
        if (animation) {
            mState = STATE_OPENING;
            mDragHelper.smoothSlideViewTo(mMainView, mRectMainOpen.left, mRectMainOpen.top);
        } else {
            mState = STATE_OPEN; mDragHelper.abort();
            mMainView.layout(mRectMainOpen.left, mRectMainOpen.top, mRectMainOpen.right, mRectMainOpen.bottom);
            mSecondaryView.layout(mRectSecOpen.left, mRectSecOpen.top, mRectSecOpen.right, mRectSecOpen.bottom);
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void close(boolean animation) {
        mIsOpenBeforeInit = false; mAborted = false;
        if (animation) {
            mState = STATE_CLOSING;
            mDragHelper.smoothSlideViewTo(mMainView, mRectMainClose.left, mRectMainClose.top);
        } else {
            mState = STATE_CLOSE; mDragHelper.abort();
            mMainView.layout(mRectMainClose.left, mRectMainClose.top, mRectMainClose.right, mRectMainClose.bottom);
            mSecondaryView.layout(mRectSecClose.left, mRectSecClose.top, mRectSecClose.right, mRectSecClose.bottom);
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void setLockDrag(boolean lock) { mLockDrag = lock; }
    public boolean isDragLocked() { return mLockDrag; }
    protected boolean shouldRequestLayout() { return mOnLayoutCount < 2; }
    protected void abort() { mAborted = true; mDragHelper.abort(); }

    private void initRects() {
        mRectMainClose.set(mMainView.getLeft(), mMainView.getTop(), mMainView.getRight(), mMainView.getBottom());
        mRectSecClose.set(mSecondaryView.getLeft(), mSecondaryView.getTop(), mSecondaryView.getRight(), mSecondaryView.getBottom());
        int openLeft = (mDragEdge == DRAG_EDGE_LEFT) ? mRectMainClose.left + mSecondaryView.getWidth() : mRectMainClose.left - mSecondaryView.getWidth();
        mRectMainOpen.set(openLeft, mRectMainClose.top, openLeft + mMainView.getWidth(), mRectMainClose.top + mMainView.getHeight());
        mRectSecOpen.set(mRectSecClose.left, mRectSecClose.top, mRectSecClose.left + mSecondaryView.getWidth(), mRectSecClose.top + mSecondaryView.getHeight());
    }

    private boolean couldBecomeClick(MotionEvent ev) {
        return (mMainView.getLeft() <= ev.getX() && ev.getX() <= mMainView.getRight() && mMainView.getTop() <= ev.getY() && ev.getY() <= mMainView.getBottom()) && mDragDist < mDragHelper.getTouchSlop();
    }

    private void accumulateDragDist(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) { mDragDist = 0; return; }
        mDragDist += Math.abs(ev.getX() - mPrevX);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SwipeRevealLayout, 0, 0);
            mDragEdge = a.getInteger(R.styleable.SwipeRevealLayout_dragEdge, DRAG_EDGE_LEFT);
            a.recycle();
        }
        mDragHelper = ViewDragHelper.create(this, 1.0f, mDragHelperCallback);
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float dX, float dY) {
                if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private final ViewDragHelper.Callback mDragHelperCallback = new ViewDragHelper.Callback() {
        @Override public boolean tryCaptureView(View child, int id) { return !mLockDrag && child == mMainView; }
        @Override public int clampViewPositionHorizontal(View child, int left, int dx) {
            return (mDragEdge == DRAG_EDGE_LEFT) ? Math.max(Math.min(left, mRectMainClose.left + mSecondaryView.getWidth()), mRectMainClose.left) : Math.max(Math.min(left, mRectMainClose.left), mRectMainClose.left - mSecondaryView.getWidth());
        }
        @Override public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (xvel > mMinFlingVelocity) open(true); else close(true);
        }
    };

    void setDragStateChangeListener(DragStateChangeListener listener) { mDragStateChangeListener = listener; }
}
