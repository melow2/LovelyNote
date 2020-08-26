package com.khs.lovelynote.view.adapter

import android.content.Context
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.khs.lovelynote.R
import com.khs.lovelynote.databinding.BoardSelectedAudioBinding
import com.khs.lovelynote.databinding.BoardSelectedFileBinding
import com.khs.lovelynote.databinding.BoardSelectedImageBinding
import com.khs.lovelynote.databinding.BoardSelectedVideoBinding
import com.khs.lovelynote.model.mediastore.*
import com.khs.lovelynote.model.mediastore.SelectedMediaStoreItem.Companion.diffCallback
import com.khs.lovelynote.module.glide.GlideImageLoader
import com.khs.lovelynote.module.glide.ProgressAppGlideModule

class SelectedMediaFileListAdapter(
    val mContext: Context
) : ListAdapter<SelectedMediaStoreItem, RecyclerView.ViewHolder>(
    diffCallback
) {

    private lateinit var mImageBinding: BoardSelectedImageBinding
    private lateinit var mAudioBinding: BoardSelectedAudioBinding
    private lateinit var mVideoBinding: BoardSelectedVideoBinding
    private lateinit var mFileBinding: BoardSelectedFileBinding

    private var listener: SelectedImageListEvent? = null

    interface SelectedImageListEvent {
        fun onClickSelectedItem(item: SelectedMediaStoreItem)
        fun onDeleteSelectedItem(item: SelectedMediaStoreItem)
        fun onPlayMediaItem(item: SelectedMediaStoreItem)
        fun onOpenFile(item:SelectedMediaStoreItem)
    }

    fun addEventListener(listener: SelectedImageListEvent) {
        this.listener = listener
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).selectedItem.type.typeCode
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> {
                mImageBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.board_item_media_selected_image, parent, false
                )
                return MediaImageHolder(mImageBinding)
            }
            MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO -> {
                mAudioBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.board_item_media_selected_audio, parent, false
                )
                return MediaAudioHolder(mAudioBinding)
            }
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO->{
                mVideoBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.board_item_media_selected_video, parent, false
                )
                return MediaVideoHolder(mVideoBinding)
            }
            else -> {
                mFileBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.board_item_media_selected_file, parent, false
                )
                return MediaFileHolder(mFileBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position).selectedItem.item
        when (holder) {
            is MediaImageHolder -> {
                holder.bind(item as MediaStoreImage)
            }
            is MediaAudioHolder -> {
                holder.bind(item as MediaStoreAudio)
            }
            is MediaVideoHolder -> {
                holder.bind(item as MediaStoreVideo)
            }
            is MediaFileHolder ->{
                holder.bind(item as MediaStoreFile)
            }
        }
    }

    inner class MediaImageHolder(private val mBinding: BoardSelectedImageBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        init {
            mBinding.run {
                ivMediaImage.setOnClickListener {
                    listener?.run {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            onClickSelectedItem(getItem(adapterPosition))
                        }
                    }
                }
                btnDelete.setOnClickListener {
                    listener?.run {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            onDeleteSelectedItem(getItem(adapterPosition))
                        }
                    }
                }
            }
        }

        fun bind(selected: MediaStoreImage) {
            GlideImageLoader(
                mBinding.ivMediaImage, mBinding.pgbLoading
            ).load(
                (selected.contentUri).toString(),
                ProgressAppGlideModule.requestOptions(mContext)
            )
        }
    }

    inner class MediaAudioHolder(private val mBinding: BoardSelectedAudioBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        init {
            mBinding.run {
                ivMediaAudio.setOnClickListener {
                    listener?.run {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            onClickSelectedItem(getItem(adapterPosition))
                        }
                    }
                }
                btnDelete.setOnClickListener {
                    listener?.run {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            onDeleteSelectedItem(getItem(adapterPosition))
                        }
                    }
                }
                btnPlay.setOnClickListener {
                    listener?.run {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            onPlayMediaItem(getItem(adapterPosition))
                        }
                    }
                }
            }
        }

        fun bind(item: MediaStoreAudio?) {
            mBinding.audio = item
        }
    }

    inner class MediaVideoHolder(private val mBinding: BoardSelectedVideoBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        init {
            mBinding.run {
                rootVideoLyt.setOnClickListener {
                    listener?.run {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            onClickSelectedItem(getItem(adapterPosition))
                        }
                    }
                }
                btnDelete.setOnClickListener {
                    listener?.run {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            onDeleteSelectedItem(getItem(adapterPosition))
                        }
                    }
                }
                btnPlay.setOnClickListener {
                    listener?.run {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            onPlayMediaItem(getItem(adapterPosition))
                        }
                    }
                }
            }
        }

        fun bind(item: MediaStoreVideo) {
            mBinding.video = item
            GlideImageLoader(
                mBinding.ivVideo, null
            ).load(
                (item.contentUri).toString(),
                ProgressAppGlideModule.requestOptions(mContext)
            )
        }
    }

    inner class MediaFileHolder(private val mBinding: BoardSelectedFileBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        init {
            mBinding.run {
                rootFileLyt.setOnClickListener {
                    listener?.run {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            onClickSelectedItem(getItem(adapterPosition))
                        }
                    }
                }
                btnDelete.setOnClickListener {
                    listener?.run {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            onDeleteSelectedItem(getItem(adapterPosition))
                        }
                    }
                }
                btnOpen.setOnClickListener {
                    listener?.run {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            onOpenFile(getItem(adapterPosition))
                        }
                    }
                }
            }
        }

        fun bind(item: MediaStoreFile) {
            mBinding.file = item
        }
    }
}




