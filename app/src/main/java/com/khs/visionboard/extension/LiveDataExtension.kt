package com.khs.visionboard.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


/**
* 라이브데이터를 한번만 읽은뒤 옵저버를 바로 삭제하는 메소드.
* @author 권혁신
* @version 1.0.0
* @since 2020-08-21 오후 1:36
**/

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