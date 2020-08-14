package com.khs.visionboard.extension

import android.animation.*
import android.graphics.drawable.AnimatedVectorDrawable
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.khs.visionboard.extension.Constants.DURATION_FADE_IN
import com.khs.visionboard.extension.Constants.DURATION_FADE_OUT

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

// 축소(크기+투명도)
fun View.complexOffAnimation() {
    val viewToComplexOff = this
    val complexOff = ObjectAnimator.ofPropertyValuesHolder(
        viewToComplexOff,
        PropertyValuesHolder.ofFloat("alpha", 1f),  // 투명도.
        PropertyValuesHolder.ofFloat("scaleX", 0.7f), // 사이즈x
        PropertyValuesHolder.ofFloat("scaleY", 0.7f)  // 사이즈y
    )
    complexOff.duration = 200
    complexOff.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            viewToComplexOff.alpha = 0.5f
            viewToComplexOff.scaleX = 0.8f
            viewToComplexOff.scaleY = 0.8f
        }
    })
    complexOff.start()
}

// 확대(크기+투명도)
fun View.complexOnAnimation() {
    val viewToComplexOn = this
    val complexOn = ObjectAnimator.ofPropertyValuesHolder(
        viewToComplexOn,
        PropertyValuesHolder.ofFloat("alpha", 0.5f),  // 투명도.
        PropertyValuesHolder.ofFloat("scaleX", 1f), // 사이즈x
        PropertyValuesHolder.ofFloat("scaleY", 1f)  // 사이즈y
    )
    complexOn.duration = 200
    complexOn.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            viewToComplexOn.alpha = 1f
            viewToComplexOn.scaleX = 1f
            viewToComplexOn.scaleY = 1f
        }
    })
    complexOn.start()
}

fun ImageView.startDrawableAnimation() {
    val viewToAnimation = this
    viewToAnimation.visibility = View.VISIBLE

    val avd: AnimatedVectorDrawableCompat
    val avd2: AnimatedVectorDrawable

    val drawable = viewToAnimation.drawable
    if (drawable is AnimatedVectorDrawableCompat) {
        avd = drawable as AnimatedVectorDrawableCompat
        avd.start()
    } else if (drawable is AnimatedVectorDrawable) {
        avd2 = drawable as AnimatedVectorDrawable
        avd2.start()
    }
}

// 펼치기
// 접기
fun View.expandAnimation(duration: Long, targetHeight: Int) {
    val v = this
    val prevHeight = v.height
    v.visibility = View.VISIBLE
    val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.duration = duration
    valueAnimator.addUpdateListener { animation ->
        v.layoutParams.height = animation.animatedValue as Int
        v.requestLayout()
    }
    valueAnimator.start()
}


// 접기
fun View.collapseAnimation(duration: Long, targetHeight: Int) {
    val v = this
    val prevHeight = v.height
    val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.duration = duration
    valueAnimator.addUpdateListener { animation ->
        v.layoutParams.height = animation.animatedValue as Int
        v.requestLayout()
    }
    valueAnimator.start()
}