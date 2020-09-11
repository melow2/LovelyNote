package com.khs.lovelynote.view.behavior

import android.app.Activity
import android.view.View
import android.view.WindowManager
import com.khs.lovelynote.databinding.FragmentAddBoardBinding
import com.khs.lovelynote.view.activity.MainActivity

class KeyBoardActionBehavior():View.OnFocusChangeListener{

    lateinit var listener:FocusChangeListenerEvent

    interface FocusChangeListenerEvent{
        fun focusOn()
        fun focusOff()
    }

    fun addFocusChangeListenerEvent(listener:FocusChangeListenerEvent){
        this.listener = listener
    }

    override fun onFocusChange(view: View?, focus: Boolean){
        if(focus){
            listener.focusOn()
        }else
            listener.focusOff()
    }
}