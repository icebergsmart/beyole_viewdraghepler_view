package com.beyole.view;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class VDLayout extends LinearLayout {

	private ViewDragHelper dragHelper;
	// 普通的view
	private View mDragView;
	// 可恢复位置的view
	private View mAutobackView;
	// 可检测边缘的view
	private View mTrackerView;
	// 存储原来点位置信息
	private Point mAutobackViewPoint = new Point();

	public VDLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		dragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {

			@Override
			public boolean tryCaptureView(View childView, int arg1) {
				// mTrackerView禁止直接移动
				return childView == mDragView || childView == mAutobackView;
			}

			@Override
			public int clampViewPositionHorizontal(View child, int left, int dx) {
				return left;
			}

			@Override
			public int clampViewPositionVertical(View child, int top, int dy) {
				return top;
			}

			/**
			 * 手指释放时的回调
			 */
			@Override
			public void onViewReleased(View releasedChild, float xvel, float yvel) {
				// mAutobackView手指释放时自动回去
				if (releasedChild == mAutobackView) {
					dragHelper.settleCapturedViewAt(mAutobackViewPoint.x, mAutobackViewPoint.y);
					invalidate();
				}
			}

			/**
			 * 在边界拖动时回调
			 */
			@Override
			public void onEdgeDragStarted(int edgeFlags, int pointerId) {
				dragHelper.captureChildView(mTrackerView, pointerId);
			}

			@Override
			public int getViewHorizontalDragRange(View child) {
				return getMeasuredWidth() - child.getMeasuredWidth();
			}

			@Override
			public int getViewVerticalDragRange(View child) {
				return getMeasuredHeight() - child.getMeasuredHeight();
			}

		});
		dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return dragHelper.shouldInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		dragHelper.processTouchEvent(event);
		return true;
	}

	/**
	 * 移动方法需要结合mScroller.startScroll
	 */
	@Override
	public void computeScroll() {
		if (dragHelper.continueSettling(true)) {
			invalidate();
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		mAutobackViewPoint.x = mAutobackView.getLeft();
		mAutobackViewPoint.y = mAutobackView.getTop();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mDragView = getChildAt(0);
		mAutobackView = getChildAt(1);
		mTrackerView = getChildAt(2);
	}
}
