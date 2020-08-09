package com.khs.visionboard.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import com.khs.visionboard.model.Constants.DURATION_FADE_IN
import com.khs.visionboard.model.Constants.DURATION_FADE_OUT


fun View.fadeOutAnimation() {
    val viewToFadeOut = this
    val fadeOut = ObjectAnimator.ofFloat(viewToFadeOut, "alpha", 1f, 0f)
    fadeOut.duration = DURATION_FADE_OUT
    fadeOut.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            // We wanna set the view to GONE, after it's fade out. so it actually disappear from the layout & don't take up space.
            viewToFadeOut.visibility = View.GONE
        }
    })
    fadeOut.start()
}

fun View.fadeInAnimation() {
    val viewToFadeIn = this
    val fadeIn = ObjectAnimator.ofFloat(viewToFadeIn, "alpha", 0f, 1f)
    fadeIn.duration = DURATION_FADE_IN
    fadeIn.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            super.onAnimationStart(animation)
            // We wanna set the view to VISIBLE, but with alpha 0. So it appear invisible in the layout.
            viewToFadeIn.visibility = View.VISIBLE
        }
    })
    fadeIn.start()
}

// 펼치기
fun View.expand() {
    val v = this
    v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    val targtetHeight: Int = v.measuredHeight
    v.layoutParams.height = 0
    v.visibility = View.VISIBLE
    val a: Animation = object : Animation() {
        override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation?
        ) {
            v.getLayoutParams().height =
                if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targtetHeight * interpolatedTime).toInt()
            v.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }
    a.duration = (targtetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
    v.startAnimation(a)
}

// 접기
fun View.collapse() {
    val v = this
    val initialHeight = v.measuredHeight
    val a: Animation = object : Animation() {
        override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation
        ) {
            if (interpolatedTime == 1f) {
                v.visibility = View.GONE
            } else {
                v.layoutParams.height =
                    initialHeight - (initialHeight * interpolatedTime).toInt()
                v.requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }
    a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
    v.startAnimation(a)
}