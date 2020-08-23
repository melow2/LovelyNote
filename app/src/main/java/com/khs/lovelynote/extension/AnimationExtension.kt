package com.khs.lovelynote.extension

import android.animation.*
import android.graphics.drawable.AnimatedVectorDrawable
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.khs.lovelynote.extension.Constants.DURATION_FADE_IN
import com.khs.lovelynote.extension.Constants.DURATION_FADE_OUT

/**
 * 애니메이션 fadeOut
 *
 * alpha: 명암
 * 1f: 애니메이션 시작 시 밝기 정도. (1f:최대)
 * 0f  애니메이션 종료 시 밝기 정도. (0f,최소)
 * @author 권혁신
 * @version 1.0.0
 * @since 2020-08-23 오후 12:42
 **/
fun View.fadeOutAnimation() {
    val fadeOut = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
    fadeOut.duration = DURATION_FADE_OUT
    fadeOut.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            // We wanna set the view to GONE, after it's fade out. so it actually disappear from the layout & don't take up space.
            visibility = View.GONE
        }
    })
    fadeOut.start()
}

/**
 * 애니메이션 fadeIn
 *
 * alpha: 명암
 * 0f: 애니메이션 시작 시 밝기 정도. (1f:최대)
 * 1f  애니메이션 종료 시 밝기 정도. (0f,최소)
 * @author 권혁신
 * @version 1.0.0
 * @since 2020-08-23 오후 12:42
 **/
fun View.fadeInAnimation() {
    val fadeIn = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
    fadeIn.duration = DURATION_FADE_IN
    fadeIn.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            super.onAnimationStart(animation)
            // We wanna set the view to VISIBLE, but with alpha 0. So it appear invisible in the layout.
            visibility = View.VISIBLE
        }
    })
    fadeIn.start()
}

/**
 * 애니메이션 투명도와 사이즈 complexOff
 * - 아이템 클릭시 아이템의 크기가 작아지면서 명암 또한 낮아진다.
 * - 리싸이클러뷰 아이템 선택을 구현 .
 * alpha: 명암
 * scaleX: 시작 시 사이즈 x(1f) ~ 종료 시 사이즈x(0.8f)
 * scaleY: 시작 시 사이즈 y(1f) ~ 종료 시 사이즈y(0.8f)
 * @author 권혁신
 * @version 1.0.0
 * @since 2020-08-23 오후 12:42
 **/
fun View.complexOffAnimation() {
    val complexOff = ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat("alpha", 1f, 0.5f),  // 투명도.
        PropertyValuesHolder.ofFloat("scaleX", 1f, 0.8f), // 사이즈x
        PropertyValuesHolder.ofFloat("scaleY", 1f, 0.8f)  // 사이즈y
    )
    complexOff.duration = 200
    complexOff.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
/*            viewToComplexOff.alpha = 0.5f
            viewToComplexOff.scaleX = 0.8f
            viewToComplexOff.scaleY = 0.8f*/
        }
    })
    complexOff.start()
}

/**
 * 애니메이션 투명도와 사이즈 complexOn
 * - 아이템 클릭해제 아이템의 크기와 명암이 원래대로 복귀한다.
 * - 리싸이클러뷰 아이템 선택해제를 구현 .
 * alpha: 명암
 * scaleX: 시작 시 사이즈 x(0.8f) ~ 종료 시 사이즈x(1f)
 * scaleY: 시작 시 사이즈 y(0.8f) ~ 종료 시 사이즈y(1f)
 * @author 권혁신
 * @version 1.0.0
 * @since 2020-08-23 오후 12:42
 **/
fun View.complexOnAnimation() {
    val complexOn = ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat("alpha", 0.5f, 1f),  // 투명도.
        PropertyValuesHolder.ofFloat("scaleX", 0.8f, 1f), // 사이즈x
        PropertyValuesHolder.ofFloat("scaleY", 0.8f, 1f)  // 사이즈y
    )
    complexOn.duration = 200
    complexOn.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
/*            viewToComplexOn.alpha = 1f
            viewToComplexOn.scaleX = 1f
            viewToComplexOn.scaleY = 1f*/
        }
    })
    complexOn.start()
}

/**
 * animated-vector 파일을 적용하는 소스.
 *
 * - 체크 시 에니메이션에 적용되었음. (app:srcCompat="@drawable/anim_ic_check")
 * @author 권혁신
 * @version 1.0.0
 * @since 2020-08-23 오후 12:42
 **/
fun ImageView.startDrawableAnimation() {
    visibility = View.VISIBLE
    val avd: AnimatedVectorDrawableCompat
    val avd2: AnimatedVectorDrawable
    if (drawable is AnimatedVectorDrawableCompat) {
        avd = drawable as AnimatedVectorDrawableCompat
        avd.start()
    } else if (drawable is AnimatedVectorDrawable) {
        avd2 = drawable as AnimatedVectorDrawable
        avd2.start()
    }
}

/**
 *  View를 펼치는 애니메이션.
 *
 * - 뷰를 원하는 height만큼 expand
 * - DecelerateInterpolator: 점점 느리게.
 * - addUpdateListener에서 변경된 애니메이션 뷰를 적용.
 * - 체크 시 에니메이션에 적용되었음. (app:srcCompat="@drawable/anim_ic_check")
 * @author 권혁신
 * @version 1.0.0
 * @since 2020-08-23 오후 12:42
 **/
fun View.expandAnimation(duration: Long, targetHeight: Int) {
    val prevHeight = height
    val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.duration = duration
    valueAnimator.addUpdateListener { animation ->
        layoutParams.height = animation.animatedValue as Int
        requestLayout()
    }
    visibility = View.VISIBLE
    valueAnimator.start()
}


/**
 *  View를 접는 애니메이션.
 *
 * - 뷰를 완전히 접음(height:0)
 * - DecelerateInterpolator: 점점 느리게.
 * - addUpdateListener에서 변경된 애니메이션 뷰를 적용.
 * - 체크 시 에니메이션에 적용되었음. (app:srcCompat="@drawable/anim_ic_check")
 * @author 권혁신
 * @version 1.0.0
 * @since 2020-08-23 오후 12:42
 **/
fun View.collapseAnimation(duration: Long, targetHeight: Int) {
    val prevHeight = height
    val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.duration = duration
    valueAnimator.addUpdateListener { animation ->
        layoutParams.height = animation.animatedValue as Int
        requestLayout()
    }
    visibility = View.INVISIBLE
    valueAnimator.start()
}