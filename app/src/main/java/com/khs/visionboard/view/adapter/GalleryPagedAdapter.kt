package com.khs.visionboard.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.khs.visionboard.R
import com.khs.visionboard.databinding.BoardGalleryItemBinding
import com.khs.visionboard.model.gallery.PhotoItem
import com.khs.visionboard.model.gallery.PhotoItem.Companion.diffCallback
import com.khs.visionboard.module.glide.GlideImageLoader
import com.khs.visionboard.module.glide.ProgressAppGlideModule.Companion.requestOptions
import timber.log.Timber

class GalleryPagedAdapter(var mContext: Context) :
    PagedListAdapter<PhotoItem, GalleryPagedAdapter.GalleryViewHolder>(diffCallback) {

    lateinit var mBinding: BoardGalleryItemBinding
    private lateinit var listener: GalleryPagedListener

    interface GalleryPagedListener {
        fun onClickEvent(position: Int, item: PhotoItem?)
        fun onClickLongEvent(
            position: Int,
            item: PhotoItem?
        )
    }

    fun addListener(listener: GalleryPagedListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.board_gallery_item,
            parent,
            false
        )
        return GalleryViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        getItem(position)?.let { item -> holder.bind(item)
            Timber.d(item.imageDataPath)
        }
    }

    inner class GalleryViewHolder(val mBinding: BoardGalleryItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        init {
            mBinding.ivGallery.setOnClickListener {
                listener?.onClickEvent(adapterPosition,getItem(adapterPosition))
            }

            mBinding.ivGallery.setOnLongClickListener {
                listener?.onClickLongEvent(adapterPosition,getItem(adapterPosition))
                return@setOnLongClickListener true
            }
        }

        fun bind(item: PhotoItem) {
            GlideImageLoader(mBinding.ivGallery,mBinding.pgbLoading)
                .load(item.imageDataPath,requestOptions(mContext))
        }
    }
}