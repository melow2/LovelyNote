package com.khs.lovelynote.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.khs.lovelynote.model.LovelyNote
import com.khs.lovelynote.model.mediastore.MediaStoreItem
import com.khs.lovelynote.model.mediastore.MediaStoreVideo
import com.khs.lovelynote.repository.NoteRepository
import timber.log.Timber

class BoardListVM(application: Application, private val param1: Int) :
    AndroidViewModel(application),
    LifecycleObserver {
    private val mContext = application.applicationContext
    private val noteRepository: NoteRepository = NoteRepository.getInstance(application)
    private var mNoteList: MutableLiveData<List<LovelyNote>> = MutableLiveData()

    init {
        mNoteList = noteRepository.getAll() as MutableLiveData<List<LovelyNote>>
    }

    fun getNoteList(): LiveData<List<LovelyNote>>? {
        return mNoteList
    }

    suspend fun removeItem(item: LovelyNote) {
        noteRepository.delete(item)
    }


    suspend fun insertItem(item: LovelyNote) {
        noteRepository.insert(item)
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