package com.khs.visionboard.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.khs.visionboard.R
import com.khs.visionboard.model.Board
import timber.log.Timber
import java.util.*

class BoardListVM(application: Application, private val param1: Int) :
    AndroidViewModel(application),
    LifecycleObserver {
    private var boardList: MutableLiveData<List<Board>> = MutableLiveData()

    fun getBoardList(): MutableLiveData<List<Board>> {
        return boardList
    }

    fun addBoard(item: Board) {
        var list = mutableListOf<Board>()
        boardList.value?.let {
            list = (boardList.value as MutableList).toMutableList() // for DiffUtil
        }
        list.add(item)
        boardList.value = list
    }

    fun getBoardItem(position: Int): Board? {
        return boardList.value?.get(position)
    }


    fun deleteBoardItem(position: Int) {
        val list = (boardList.value as MutableList).toMutableList()
        list.removeAt(position)
        boardList.value = list
    }

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