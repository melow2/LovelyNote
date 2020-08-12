package com.khs.visionboard.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


/**
 *  라이브 데이터를 한번만 호출후에 옵저버를 삭제하는 메소드.
 * */

fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
    observeForever(object: Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer.onChanged(value)
        }
    })
}

fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
    observe(owner, object : Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer.onChanged(value)
        }
    })
}