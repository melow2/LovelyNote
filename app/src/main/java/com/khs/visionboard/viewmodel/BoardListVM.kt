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
        if (boardList.value == null) {
            list.add(item)
        } else {
            list = (boardList.value as MutableList).toMutableList() // for DiffUtil
            list.add(item)
        }
        boardList.value = list
    }

    fun getBoardItem(position: Int): Board? {
        return boardList.value?.get(position)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onCreate() {
        Timber.d("onCreate()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        Timber.d("onPause()")
    }

    fun deleteBoardItem(position: Int) {
        val list = (boardList.value as MutableList).toMutableList()
        list.removeAt(position)
        boardList.value = list
    }

    fun setBoard() {
        var list = mutableListOf<Board>()
        list.add(Board(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "설명",R.drawable.image_01))
        list.add(Board(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "설명", R.drawable.image_02))
        list.add(Board(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "설명", R.drawable.image_03))
        list.add(Board(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "설명", R.drawable.image_04))
        list.add(Board(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "설명", R.drawable.image_05))
        boardList.value = list
    }

}