package com.khs.visionboard.view.adapter

import android.content.Context
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.khs.visionboard.R
import com.khs.visionboard.databinding.BoardItemMediaAudioSlidingBinding
import com.khs.visionboard.databinding.BoardItemMediaImageSlidingBinding
import com.khs.visionboard.databinding.BoardItemMediaVideoSlidingBinding
import com.khs.visionboard.model.mediastore.MediaStoreAudio
import com.khs.visionboard.model.mediastore.MediaStoreImage
import com.khs.visionboard.model.mediastore.MediaStoreVideo
import com.khs.visionboard.model.mediastore.SelectedItem
import com.khs.visionboard.view.adapter.holder.MediaAudioHolder
import com.khs.visionboard.view.adapter.holder.MediaImageHolder
import com.khs.visionboard.view.adapter.holder.MediaVideoHolder


class MediaStoreItemAdapter(
    private val mContext: Context,
    private val mList: ArrayList<SelectedItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mImageBinding: BoardItemMediaImageSlidingBinding
    private lateinit var mAudioBinding: BoardItemMediaAudioSlidingBinding
    private lateinit var mVideoBinding: BoardItemMediaVideoSlidingBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> {
                mImageBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.board_item_media_image_sliding,
                    parent,
                    false
                )
                return MediaImageHolder(mContext,mImageBinding)
            }
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO ->{
                mVideoBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.board_item_media_video_sliding,
                    parent,
                    false
                )
                return MediaVideoHolder(mContext,mVideoBinding)
            }
            else -> {
                mAudioBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.board_item_media_audio_sliding,
                    parent,
                    false
                )
                return MediaAudioHolder(mContext,mAudioBinding)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MediaImageHolder -> {
                holder.bind(mList[position].item as MediaStoreImage)
            }
            is MediaVideoHolder ->{
                holder.bind(mList[position].item as MediaStoreVideo)
            }
            is MediaAudioHolder ->{
                holder.bind(mList[position].item as MediaStoreAudio)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return mList[position].type.typeCode
    }


}

