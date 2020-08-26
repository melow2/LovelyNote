package com.khs.lovelynote.view.behavior

import android.app.Activity
import android.view.View
import android.view.WindowManager
import com.khs.lovelynote.databinding.FragmentAddBoardBinding

class KeyBoardActionBehavior(
    val mActivity: Activity
){
    val focusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
        if(!hasFocus){
            hideKeyboard();
        }else{
            showKeyboard()
        }
    }

    private fun showKeyboard() {
       // mActivity.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

    }

    private fun hideKeyboard() {
       // mActivity.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }
}