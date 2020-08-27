package com.khs.lovelynote.view.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ScrollBarBehavior(
    context: Context?,
    attrs: AttributeSet?
) :
    FloatingActionButton.Behavior(context, attrs) {
    private var isSettingVisible = false
    private var isSettingInvisible = false
    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
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
            if (child.alpha != 0f && !isSettingInvisible) {
                animate(child, false)
            }
        } else {
            if (child.alpha != 1f && !isSettingVisible) {
                animate(child, true)
            }
        }
    }

    private fun animate(child: View, visible: Boolean) {
        child.animate().cancel()
        isSettingVisible = visible
        isSettingInvisible = !visible
        child.animate()
            .withLayer()
            .alpha(if (visible) 1F else 0.toFloat())
            .translationY(if (visible) 0F else child.height.toFloat())
            .setDuration(ANIMATION_DURATION_IN_MS.toLong())
            .withEndAction {
                isSettingVisible = false
                isSettingInvisible = false
            }
    }

    companion object {
        private const val ANIMATION_DURATION_IN_MS = 250
    }
}