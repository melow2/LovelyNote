package com.khs.visionboard.viewmodel.factory

import android.content.ContentResolver
import android.content.Context
import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import com.khs.visionboard.extension.getMediaStoreVideoFiles
import com.khs.visionboard.model.mediastore.MediaStoreFileType
import com.khs.visionboard.model.mediastore.MediaStoreVideo

/**
 * DataSource.Factory:
 * DataSource를 생성하는 역할
 */

/**
 * 참고 싸이트 : https://codechacha.com/ko/android-jetpack-paging/
 *
 * DataSource
 * "데이터를 로딩"하는 객체"로 로컬 또는 Backend의 데이터를 가져오는 역할입니다.
 *
 * DataSource의 파생클래스
 * PositionalDataSource: 위치기반의 데이터를 로딩하는 DataSource입니다.
 *      ** 셀 수 있는 데이터, 고정된 사이즈의 데이터를 로딩할 때 사용됩니다.
 *      만약 끝을 알 수 없는 무한대의 아이템이라면, ItemKeyedDataSource 또는 PageKeyedDataSource이 적합합니다.
 *      ** Room은 PositionalDataSource 타입의 소스를 제공합니다.
 * ItemKeyedDataSource: 키 기반의 아이템을 로딩하는 DataSource입니다.
 * PageKeyedDataSource: 페이지 기반의 아이템을 로딩하는 DataSource입니다.
 *
 * 공통점 : 데이터를 가져온다는 것
 * 차이점 : 데이터 덩어리를 가져오는 방식이 다르다는 것
 *
 * PositionalDataSource
 * 특정 위치(index)에서 원하는 개수만큼 데이터를 가져올 수 있다면 PositionalDataSource 를 적용
 * loadInitial : 처음 데이터를 가져올 때 호출되는 함수
 * loadRange : 다음 데이터를 가져올 때 호출
 */


class MediaVideoSourceFactory(
    private val mContext: Context
) :
    DataSource.Factory<Int, MediaStoreVideo>() {

    override fun create(): DataSource<Int, MediaStoreVideo> {
        return GalleryDataSource(mContext.contentResolver)
    }

    inner class GalleryDataSource(private val contentResolver: ContentResolver) :
        PositionalDataSource<MediaStoreVideo>() {

        override fun loadInitial(
            params: LoadInitialParams,
            callback: LoadInitialCallback<MediaStoreVideo>
        ) {
            // Timber.d("loadInitial start: ${params.requestedStartPosition}, size: ${params.requestedLoadSize}")
            callback.onResult(
                mContext.getMediaStoreVideoFiles(
                    params.requestedLoadSize,
                    params.requestedStartPosition,
                    MediaStoreFileType.VIDEO
                ), 0
            )
        }

        override fun loadRange(
            params: LoadRangeParams,
            callback: LoadRangeCallback<MediaStoreVideo>
        ) {
            // Timber.d("loadRange start: ${params.startPosition}, size: ${params.loadSize}")
            callback.onResult(
                mContext.getMediaStoreVideoFiles(
                    params.loadSize,
                    params.startPosition,
                    MediaStoreFileType.VIDEO
                )
            )
        }
    }
}
