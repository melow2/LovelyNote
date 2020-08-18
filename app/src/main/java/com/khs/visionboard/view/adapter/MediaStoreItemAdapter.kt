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
import com.khs.visionboard.module.glide.GlideImageLoader
import com.khs.visionboard.module.glide.ProgressAppGlideModule


class MediaStoreItemAdapter(
    private val mContext: Context,
    private val mList: ArrayList<SelectedItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private lateinit var mImageBinding: BoardItemMediaImageSlidingBinding
    private lateinit var mAudioBinding: BoardItemMediaAudioSlidingBinding
    private lateinit var mVideoBinding: BoardItemMediaVideoSlidingBinding

    private lateinit var mListener:MediaStoreItemEvent

    interface MediaStoreItemEvent{
        fun onPlayAudio(item:MediaStoreAudio)
        fun onPlayVideo(item:MediaStoreVideo)
    }

    fun addListener(listener:MediaStoreItemEvent){
        this.mListener = listener
    }

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
                return MediaImageSlideHolder(mImageBinding)
            }
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO ->{
                mVideoBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.board_item_media_video_sliding,
                    parent,
                    false
                )
                return MediaVideoSlideHolder(mVideoBinding)
            }
            else -> {
                mAudioBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.board_item_media_audio_sliding,
                    parent,
                    false
                )
                return MediaAudioSlideHolder(mAudioBinding)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    override fun getItemViewType(position: Int): Int {
        return mList[position].type.typeCode
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MediaImageSlideHolder -> {
                holder.bind(mList[position].item as MediaStoreImage)
            }
            is MediaVideoSlideHolder ->{
                holder.bind(mList[position].item as MediaStoreVideo)
            }
            is MediaAudioSlideHolder ->{
                holder.bind(mList[position].item as MediaStoreAudio)
            }
        }
    }

    inner class MediaImageSlideHolder(private val mBinding: BoardItemMediaImageSlidingBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        init {

        }

        fun bind(item: MediaStoreImage?) {
            GlideImageLoader(mBinding.ivSliding, null).load(
                (item?.contentUri).toString(),
                ProgressAppGlideModule.requestOptions(mContext)
            )
        }
    }

    inner class MediaAudioSlideHolder(private val mBinding: BoardItemMediaAudioSlidingBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        init {
            mBinding.run {
                btnSlidingAudioPlay.setOnClickListener {
                    mListener.onPlayAudio(mList[adapterPosition].item as MediaStoreAudio)
                }
            }
        }

        fun bind(item: MediaStoreAudio?) {
            mBinding.tvSlidingDuration.text = item?.duration
        }
    }

    inner class MediaVideoSlideHolder(private val mBinding: BoardItemMediaVideoSlidingBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        init {
            mBinding.run{
                btnSlidingVideoPlay.setOnClickListener {
                    mListener.onPlayVideo(mList[adapterPosition].item as MediaStoreVideo)
                }
            }
        }

        fun bind(item: MediaStoreVideo?) {
            mBinding.tvSlidingDuration.text = item?.duration
            GlideImageLoader(mBinding.ivSliding, null).load(
                (item?.contentUri).toString(),
                ProgressAppGlideModule.requestOptions(mContext)
            )
        }
    }



}

