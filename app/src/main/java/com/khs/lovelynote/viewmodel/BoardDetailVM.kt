package com.khs.lovelynote.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.khs.lovelynote.model.LovelyNote
import timber.log.Timber

class BoardDetailVM(application: Application, private val param1: Int) :
    AndroidViewModel(application), LifecycleObserver {
    private val mContext = application.applicationContext
    private var board: MutableLiveData<LovelyNote> = MutableLiveData()


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onCreate() {
        Timber.d("onCreate()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        Timber.d("onPause()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Timber.d("onDestroy()")
    }

}