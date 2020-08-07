package com.khs.visionboard.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.khs.visionboard.model.Board
import org.jetbrains.annotations.TestOnly
import timber.log.Timber
import java.text.FieldPosition
import java.util.*

class BoardListVM : AndroidViewModel,LifecycleObserver{

    private var boardList:MutableLiveData<List<Board>> = MutableLiveData()

    constructor(application: Application,param1:Int):super(application){

    }

    fun getBoardList(): MutableLiveData<List<Board>> {
        return boardList
    }

    fun addBoard(item:Board){
        var list = mutableListOf<Board>()
        if(boardList.value == null){
            list.add(item)
        }else {
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

}