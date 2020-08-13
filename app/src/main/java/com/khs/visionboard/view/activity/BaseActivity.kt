package com.khs.visionboard.view.activity

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.snackbar.Snackbar
import com.khs.visionboard.view.fragment.BaseFragment


abstract class BaseActivity<B : ViewDataBinding?> : AppCompatActivity() {
    var mBinding: B? = null
    lateinit var toolbar: Toolbar

    protected fun bindView(layout: Int) {
        mBinding = DataBindingUtil.setContentView<B>(this, layout)
    }

    protected fun setToolbar(
        toolbar: Toolbar,
        backBtnVisible: Boolean,
        toolbarTitle: String,
        tvToolbarTitle: TextView
    ) {
        this.toolbar = toolbar
        toolbar.title = "" // 기존의 툴바 타이틀 제거.
        toolbar.setContentInsetsAbsolute(0, 0) // 좌우 여백 제거.
        tvToolbarTitle.text = toolbarTitle
        if (backBtnVisible) { // 뒤로가기 버튼 보이기.
            toolbar.setNavigationOnClickListener { v: View? -> onBackPressed() }
        }
        setSupportActionBar(toolbar)
    }

    override fun onBackPressed() {
        val fragmentList: List<*> = supportFragmentManager.fragments
        var handled = false
        for (f in fragmentList) {
            if (f is BaseFragment<*>) {
                handled = f.onBackPressed()
                if (handled) {
                    break
                }
            }
        }
        if (!handled) {
            super.onBackPressed()
        }
        // overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left)
    }

    companion object {
        private val TAG = BaseActivity::class.java.simpleName
        protected fun startActivityAnimation(context: Context) {
            if (context is AppCompatActivity) {
                context.overridePendingTransition(
                    android.R.anim.slide_out_right,
                    android.R.anim.slide_in_left
                )
            }
        }

        fun printLog(tag: String, msg: String) {
            Log.d(tag, msg)
        }

        fun showSnackBar(v: View, @StringRes stringResID: Int) {
            Snackbar.make(v, stringResID, Snackbar.LENGTH_LONG).show()
        }

        fun showToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
}