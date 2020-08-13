package com.khs.visionboard.view.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import timber.log.Timber

abstract class BaseFragment<B : ViewDataBinding?> : Fragment() {
    var mBinding: B? = null

    protected fun bindView(
        inflater: LayoutInflater,
        container: ViewGroup,
        layout: Int
    ) {
        mBinding = DataBindingUtil.inflate<B>(inflater, layout, container, false)
    }

    abstract fun onBackPressed(): Boolean

    companion object {
        fun printLog(tag: String, msg: String) {
            Timber.d(msg)
        }

        fun showToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
}
