package com.khs.visionboard.view.adapter.holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.khs.visionboard.databinding.BoardItemMediaImageSlidingBinding
import com.khs.visionboard.databinding.BoardItemMediaVideoSlidingBinding
import com.khs.visionboard.model.mediastore.MediaStoreImage
import com.khs.visionboard.model.mediastore.MediaStoreVideo
import com.khs.visionboard.module.glide.GlideImageLoader
import com.khs.visionboard.module.glide.ProgressAppGlideModule


class MediaVideoHolder(private val mContext:Context,
                       private val mBinding: BoardItemMediaVideoSlidingBinding) :
    RecyclerView.ViewHolder(mBinding.root) {

    init {

    }

    fun bind(item: MediaStoreVideo?) {
        GlideImageLoader(mBinding.ivSliding, null).load(
            (item?.contentUri).toString(),
            ProgressAppGlideModule.requestOptions(mContext)
        )
    }
}