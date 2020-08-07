package com.khs.visionboard.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.khs.visionboard.model.Board
import timber.log.Timber
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

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onCreate() {
        Timber.d("onCreate()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        Timber.d("onPause()")
    }

}