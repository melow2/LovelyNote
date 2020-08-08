package com.khs.visionboard.view.behavior

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ScrollAwareFABBehavior : CoordinatorLayout.Behavior<FloatingActionButton> {
    private var mHandler: Handler? = null

    constructor(context: Context?, attrs: AttributeSet?) : super() {}
    constructor() : super() {}

    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        type: Int
    ) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
        if (mHandler == null) mHandler = Handler()
        mHandler?.postDelayed(Runnable {
            child.animate().translationY(0f).setInterpolator(LinearInterpolator()).start()
        }, 1000)
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type
        )
        if (dyConsumed > 0) {

            val layoutParams =
                child.layoutParams as CoordinatorLayout.LayoutParams
            val fab_bottomMargin = layoutParams.bottomMargin
            child.animate().translationY(child.height + fab_bottomMargin.toFloat())
                .setInterpolator(LinearInterpolator()).start()
        } else if (dyConsumed < 0) {

            child.animate().translationY(0f).setInterpolator(LinearInterpolator()).start()
        }
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        if (mHandler != null) {
            mHandler?.removeMessages(0)
        }
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    companion object {
        private const val TAG = "ScrollingFABBehavior"
    }
}