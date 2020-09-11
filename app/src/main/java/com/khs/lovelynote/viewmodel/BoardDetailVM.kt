package com.khs.lovelynote.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.khs.lovelynote.databinding.BoardItemMediaAudioBinding
import com.khs.lovelynote.databinding.BoardItemMediaImageBinding
import com.khs.lovelynote.databinding.BoardItemMediaVideoBinding
import com.khs.lovelynote.extension.Constants
import com.khs.lovelynote.extension.complexOnAnimation
import com.khs.lovelynote.model.LovelyNote
import com.khs.lovelynote.model.mediastore.*
import com.khs.lovelynote.repository.NoteRepository
import com.khs.lovelynote.viewmodel.factory.MediaAudioSourceFactory
import com.khs.lovelynote.viewmodel.factory.MediaImageSourceFactory
import com.khs.lovelynote.viewmodel.factory.MediaVideoSourceFactory
import kotlinx.coroutines.*
import timber.log.Timber

class BoardDetailVM(application: Application, private val noteId: Long) :
    AndroidViewModel(application), LifecycleObserver {
    private var mImageList: LiveData<PagedList<MediaStoreImage>>
    private var mAudioList: LiveData<PagedList<MediaStoreAudio>>
    private var mVideoList: LiveData<PagedList<MediaStoreVideo>>
    private val mImageSourceFactory: MediaImageSourceFactory
    private val mAudioSourceFactory: MediaAudioSourceFactory
    private val mVideoSourceFactory: MediaVideoSourceFactory
    private val mPagedListConfig: PagedList.Config
    private val mSelectedMediaStoreItemList: MutableLiveData<List<SelectedMediaStoreItem>> = MutableLiveData()
    private val mContext = application.applicationContext
    private var mLovelyNote: MutableLiveData<LovelyNote> = MutableLiveData()
    private val noteRepository: NoteRepository = NoteRepository.getInstance(application)
    private val mJob = Job()
    private val mScope = CoroutineScope(Dispatchers.Main+mJob)

    init {
        mLovelyNote = noteRepository.getItem(noteId) as MutableLiveData<LovelyNote>
        mImageSourceFactory = MediaImageSourceFactory(mContext)
        mAudioSourceFactory = MediaAudioSourceFactory(mContext)
        mVideoSourceFactory = MediaVideoSourceFactory(mContext)
        mPagedListConfig = PagedList.Config.Builder()
            .setPageSize(Constants.PAGE_SIZE)
            .setInitialLoadSizeHint(Constants.INITIAL_LOAD_SIZE_HINT)
            .setPrefetchDistance(Constants.PREFETCH_DISTANCE)
            .setEnablePlaceholders(false)
            .build()
        mImageList = LivePagedListBuilder(mImageSourceFactory, mPagedListConfig).build()
        mAudioList = LivePagedListBuilder(mAudioSourceFactory, mPagedListConfig).build()
        mVideoList = LivePagedListBuilder(mVideoSourceFactory, mPagedListConfig).build()
    }

    fun getLovelyNote(): MutableLiveData<LovelyNote> {
        return mLovelyNote
    }

    fun deleteNote(noteId:Long) {
        mScope.launch {
            withContext(Dispatchers.IO) {
                noteRepository.delete(noteId)
            }
        }
    }

    fun updateNote(item: LovelyNote) {
        mScope.launch {
            withContext(Dispatchers.IO) {
                noteRepository.update(item)
            }
        }
    }


    fun getImages(): LiveData<PagedList<MediaStoreImage>> {
        return mImageList
    }

    fun getAudios(): LiveData<PagedList<MediaStoreAudio>> {
        return mAudioList
    }

    fun getVideos(): LiveData<PagedList<MediaStoreVideo>> {
        return mVideoList
    }

    fun getSelectedObjectList(): LiveData<List<SelectedMediaStoreItem>> {
        return mSelectedMediaStoreItemList
    }

    fun getSelectedIndexOf(item: SelectedMediaStoreItem): Int? {
        return mSelectedMediaStoreItemList.value?.indexOf(item)
    }

    fun getAllSelectedItemList(): ArrayList<SelectedItem> {
        val list = arrayListOf<SelectedItem>()
        mSelectedMediaStoreItemList.value?.run {
            for (temp in this)
                list.add(temp.selectedItem)
        }
        return list
    }

    /**
     * 선택한 이미지를 추가하는 메소드. (이미지 선택 추가)
     *
     * 1) Diff를 위해 기존의 리스트가 있다면, 기존의 리스트 데이터를 그대로 사용하여 새로운 리스트 생성.
     * 2) 선택된 이미지를 추가.
     * */
    fun addSelectedItem(selectedMediaStoreItem: SelectedMediaStoreItem?) {
        var list = mutableListOf<SelectedMediaStoreItem>()
        mSelectedMediaStoreItemList.run {
            value?.let {
                list = it.toMutableList()
            }
        }
        selectedMediaStoreItem?.let { list.add(it) }
        mSelectedMediaStoreItemList.value = list
    }

    /**
     * 선택한 이미지를 해제하는 메소드. (이미지 선택 해제)
     *
     * 1) Diff를 위해 기존의 선택된 리스트를 새롭게 초기화.
     * 2) 선택된 이미지를 제거.
     * */
    fun removeSelectedItem(selectedImageMediaStoreItem: SelectedMediaStoreItem?) {
        val list = (mSelectedMediaStoreItemList.value as MutableList).toMutableList()
        list.remove(selectedImageMediaStoreItem)
        mSelectedMediaStoreItemList.value = list
    }

    /**
     * 선택한 이미지를 모두 삭제하는 메소드. ( 새로고침 버튼을 눌렀을 경우)
     *
     * 1) 데이터 바인딩된 이미지에서 애니메이션을 모두 제거한다.
     * 2) 선택된 이미지 데이터를 초기화 한다.
     * */
    fun removeAllSelectedItemAnimation() {
        for (selected in mSelectedMediaStoreItemList.value!!) {
            selected.itemBinding?.let {
                when (selected.selectedItem.type) {
                    MediaStoreFileType.IMAGE -> {
                        (selected.itemBinding as BoardItemMediaImageBinding).run {
                            ivGallery.complexOnAnimation()
                            ivSelected.visibility = View.GONE

                        }
                    }
                    MediaStoreFileType.AUDIO -> {
                        (selected.itemBinding as BoardItemMediaAudioBinding).run {
                            rootAudioLyt.complexOnAnimation()
                            ivSelected.visibility = View.GONE
                        }
                    }
                    MediaStoreFileType.VIDEO -> {
                        (selected.itemBinding as BoardItemMediaVideoBinding).run {
                            rootVideoLyt.complexOnAnimation()
                            ivSelected.visibility = View.GONE
                        }
                    }
                    else -> return
                }
            }
        }
        mSelectedMediaStoreItemList.value = mutableListOf()
    }

    fun removelSelectedItemAnimation(target: SelectedItem) {
        for (selected in mSelectedMediaStoreItemList.value!!) {
            if (selected.selectedItem.contentUri == target.contentUri && selected.itemBinding != null) {
                when (target.type) {
                    MediaStoreFileType.IMAGE -> {
                        (selected.itemBinding as BoardItemMediaImageBinding).run {
                            ivGallery.complexOnAnimation()
                            ivSelected.visibility = View.GONE
                        }
                    }
                    MediaStoreFileType.AUDIO -> {
                        (selected.itemBinding as BoardItemMediaAudioBinding).run {
                            rootAudioLyt.complexOnAnimation()
                            ivSelected.visibility = View.GONE
                        }
                    }
                    MediaStoreFileType.VIDEO -> {
                        (selected.itemBinding as BoardItemMediaVideoBinding).run {
                            rootVideoLyt.complexOnAnimation()
                            ivSelected.visibility = View.GONE
                        }
                    }
                    else -> return
                }
            }
        }
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