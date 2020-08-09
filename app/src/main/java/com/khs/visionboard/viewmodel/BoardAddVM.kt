package com.khs.visionboard.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.khs.visionboard.model.gallery.PhotoItem
import com.khs.visionboard.viewmodel.factory.GalleryDataSourceFactory
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

class BoardAddVM(application: Application, private val param1: Int) :
    AndroidViewModel(application), LifecycleObserver{

    private val mContext = application.applicationContext

    fun getImages(): LiveData<PagedList<PhotoItem>> {
        val dataSourceFactory =
            GalleryDataSourceFactory(
                mContext.contentResolver
            )
        val pagedListConfig = PagedList.Config.Builder()
            .setPageSize(20)
            .setInitialLoadSizeHint(60)     // default : page size * 3
            // .setPrefetchDistance(20)        // default : page size
            .setEnablePlaceholders(false)   // default : true
            .build()
        return LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()
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