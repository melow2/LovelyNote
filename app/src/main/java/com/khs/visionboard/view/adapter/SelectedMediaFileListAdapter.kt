package com.khs.visionboard.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.khs.visionboard.R
import com.khs.visionboard.databinding.BoardItemMediaSelectedBinding
import com.khs.visionboard.model.mediastore.MediaStoreFileType
import com.khs.visionboard.model.mediastore.SelectedMediaStoreItem
import com.khs.visionboard.model.mediastore.SelectedMediaStoreItem.Companion.diffCallback
import com.khs.visionboard.module.glide.GlideImageLoader
import com.khs.visionboard.module.glide.ProgressAppGlideModule

class SelectedMediaFileListAdapter(
    val mContext: Context
) : ListAdapter<SelectedMediaStoreItem, SelectedMediaFileListAdapter.SelectedImageViewHolder>(
    diffCallback
) {

    private lateinit var mBinding: BoardItemMediaSelectedBinding
    private var listener: SelectedImageListEvent? = null

    interface SelectedImageListEvent {
        fun onClick(position: Int)
        fun onDelete(position: Int)
    }

    fun addEventListener(listener: SelectedImageListEvent) {
        this.listener = listener
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImageViewHolder {
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.board_item_media_selected, parent, false
        )
        return SelectedImageViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: SelectedImageViewHolder, position: Int) {
        val board = getItem(position)
        holder.bind(board)              // bind item
    }

    inner class SelectedImageViewHolder(private val mBinding: BoardItemMediaSelectedBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        init {
            mBinding.ivMediaItem.setOnClickListener {
                listener?.apply {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onClick(adapterPosition)
                    }
                }
            }
        }

        fun bind(selected: SelectedMediaStoreItem) {
            selected.apply {
                when (item?.type) {
                    MediaStoreFileType.IMAGE -> {
                        GlideImageLoader(
                            mBinding.ivMediaItem, null
                        ).load(
                            (contentUri).toString(),
                            ProgressAppGlideModule.requestOptions(mContext)
                        )
                    }
                    MediaStoreFileType.AUDIO ->{
                        GlideImageLoader(
                            mBinding.ivMediaItem, null
                        ).load(
                            (contentUri).toString(),
                            ProgressAppGlideModule.requestOptions(mContext)
                        )
                    }
                    MediaStoreFileType.VIDEO ->{
                        Glide.with(mBinding.root)
                            .load(R.drawable.ic_baseline_videocam)
                            .into(mBinding.ivMediaItem)
                    }
                }
            }
        }
    }
}

