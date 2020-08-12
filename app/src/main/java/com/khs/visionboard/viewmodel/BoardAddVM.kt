package com.khs.visionboard.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.khs.visionboard.databinding.BoardItemMediaAudioBinding
import com.khs.visionboard.databinding.BoardItemMediaImageBinding
import com.khs.visionboard.extension.complexOnAnimation
import com.khs.visionboard.model.mediastore.MediaStoreAudio
import com.khs.visionboard.model.mediastore.MediaStoreFileType
import com.khs.visionboard.model.mediastore.MediaStoreImage
import com.khs.visionboard.model.mediastore.MediaStoreItemSelected
import com.khs.visionboard.viewmodel.factory.MediaAudioSourceFactory
import com.khs.visionboard.viewmodel.factory.MediaImageSourceFactory
import timber.log.Timber

/**
 * PagedList :
 * DataSource 에서 가져온 데이터는 모두 PagedList 로 전달됩니다.
 * 데이터 로딩이 필요하면 DataSource 를 통해 가져옵니다.
 * 또한, UI에 데이터를 제공하는 역할을 합니다.
 *
 * LivePagedListBuilder :
 * PagedList를 생성하는 빌더입니다. 빌더는 LiveData로 리턴합니다.
 *
 * Placeholders :
 * 데이터가 로딩되지 않아 화면에 보여지지 않을 때, 가상의 객체를 미리 그리고 데이터 로딩이 완료될 때 실제 데이터를 보여주는 것을 말합니다.
 *
 * 장점 :
 * 1. 빠르게 스크롤 할 수 있다
 * 2. 스크롤바 위치가 정확하다
 * 3. 스피너 등으로 더 보기 같은 기능을 만들 필요가 없다
 * 조건 :
 * 1. 아이템이 보여지는 View의 크기가 동일해야 한다
 * 2. Adapter가 null을 처리해야 한다
 * 3. DataSource에서 제공하는 아이템의 개수가 정해져 있어야 한다
 */


/**
 * todo 1) MediaStore 파일들을 항상 모니터링 할 수 없다는 것이 기술적 결함.
 *
 * */
class BoardAddVM(application: Application, private val param1: Int) :
    AndroidViewModel(application), LifecycleObserver {

    private lateinit var mImageList: LiveData<PagedList<MediaStoreImage>>
    private lateinit var mAudioList: LiveData<PagedList<MediaStoreAudio>>
    private val mContext = application.applicationContext
    private val mImageSourceFactory: MediaImageSourceFactory
    private val mAudioSourceFactory: MediaAudioSourceFactory

    private val mPagedListConfig: PagedList.Config
    private val mSelectedMediaStoreItemList: MutableLiveData<List<MediaStoreItemSelected>> =
        MutableLiveData()

    init {
        mImageSourceFactory = MediaImageSourceFactory(mContext.contentResolver)
        mAudioSourceFactory = MediaAudioSourceFactory(mContext.contentResolver)
        mPagedListConfig = PagedList.Config.Builder()
            .setPageSize(20)
            .setInitialLoadSizeHint(60)     // default : page size * 3
            // .setPrefetchDistance(20)        // default : page size
            .setEnablePlaceholders(false)   // default : true
            .build()

    }

    fun getImages(): LiveData<PagedList<MediaStoreImage>> {
        mImageList = LivePagedListBuilder(mImageSourceFactory, mPagedListConfig).build()
        return mImageList
    }

    fun getAudios(): LiveData<PagedList<MediaStoreAudio>> {
        mAudioList = LivePagedListBuilder(mAudioSourceFactory, mPagedListConfig).build()
        return mAudioList
    }


    fun getSelectedImages(): LiveData<List<MediaStoreItemSelected>> {
        return mSelectedMediaStoreItemList
    }

    /**
     * 선택한 이미지를 추가하는 메소드. (이미지 선택 추가)
     *
     * 1) Diff를 위해 기존의 리스트가 있다면, 기존의 리스트 데이터를 그대로 사용하여 새로운 리스트 생성.
     * 2) 선택된 이미지를 추가.
     * */
    fun addSelectedItem(selectedMediaStoreItem: MediaStoreItemSelected?) {
        var list = mutableListOf<MediaStoreItemSelected>()
        mSelectedMediaStoreItemList.run {
            value?.let {
                list = (it as MutableList).toMutableList()
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
    fun removeSelectedItem(selectedImageMediaStoreItem: MediaStoreItemSelected?) {
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
    fun removeAllSelectedImages() {
        for (item in mSelectedMediaStoreItemList.value!!) {
            when (item.type) {
                MediaStoreFileType.IMAGE -> {
                    (item.itemBinding as BoardItemMediaImageBinding).apply {
                        ivGallery.complexOnAnimation()
                        ivSelected.visibility = View.GONE

                    }
                }
                MediaStoreFileType.AUDIO -> {
                    (item.itemBinding as BoardItemMediaAudioBinding).apply {
                        ivAudio.complexOnAnimation()
                        ivSelected.visibility = View.GONE
                    }
                }
            }
        }
        mSelectedMediaStoreItemList.value = mutableListOf()
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