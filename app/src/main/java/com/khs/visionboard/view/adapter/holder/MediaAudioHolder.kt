package com.khs.visionboard.view.adapter.holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.khs.visionboard.databinding.BoardItemMediaAudioSlidingBinding
import com.khs.visionboard.databinding.BoardItemMediaImageSlidingBinding
import com.khs.visionboard.model.mediastore.MediaStoreAudio
import com.khs.visionboard.model.mediastore.MediaStoreImage
import com.khs.visionboard.module.glide.GlideImageLoader
import com.khs.visionboard.module.glide.ProgressAppGlideModule


class MediaAudioHolder(private val mContext:Context,
                       private val mBinding: BoardItemMediaAudioSlidingBinding) :
    RecyclerView.ViewHolder(mBinding.root) {

    init {

    }

    fun bind(item: MediaStoreAudio?) {

    }
}