package com.khs.visionboard.view.adapter

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.khs.visionboard.R
import com.khs.visionboard.databinding.BoardMediaItemBinding
import com.khs.visionboard.extension.complexOffAnimation
import com.khs.visionboard.extension.complexOnAnimation
import com.khs.visionboard.extension.startDrawableAnimation
import com.khs.visionboard.model.mediastore.MediaStoreFileType
import com.khs.visionboard.model.mediastore.MediaStoreImage
import com.khs.visionboard.model.mediastore.MediaStoreImage.Companion.diffCallback
import com.khs.visionboard.module.glide.GlideImageLoader
import com.khs.visionboard.module.glide.ProgressAppGlideModule.Companion.requestOptions
import timber.log.Timber

class MediaImagePagedAdapter(var mContext: Context) :
    PagedListAdapter<MediaStoreImage, MediaImagePagedAdapter.GalleryViewHolder>(diffCallback) {

    lateinit var mBinding: BoardMediaItemBinding
    private var mListener: MediaPagedListener? = null
    private var mSelectedItems: SparseBooleanArray = SparseBooleanArray(0)
    private val mImageList = arrayListOf<MediaStoreImage>()

    interface MediaPagedListener {
        fun onMediaItemClickEvent(
            binding: BoardMediaItemBinding,
            adapterPosition: Int,
            item: Any?,
            type: MediaStoreFileType?,
            checked: Boolean
        )

        fun onMediaItemLongClickEvent(
            binding: BoardMediaItemBinding,
            adapterPosition: Int,
            item: Any?,
            type: MediaStoreFileType?
        )
    }

    fun addListener(listener: MediaPagedListener) {
        this.mListener = listener
    }

    fun initSelectedItems() {
        mSelectedItems.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.board_media_item,
            parent,
            false
        )
        return GalleryViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return mImageList.size
    }

    override fun submitList(pagedList: PagedList<MediaStoreImage>?) {
        pagedList?.addWeakCallback(listOf(), object : PagedList.Callback() {
            override fun onChanged(position: Int, count: Int) {
                mImageList.clear()
                mImageList.addAll(pagedList)
            }

            override fun onInserted(position: Int, count: Int) {
                mImageList.clear()
                mImageList.addAll(pagedList)
            }

            override fun onRemoved(position: Int, count: Int) {
                mImageList.clear()
                mImageList.addAll(pagedList)
            }
        })
        super.submitList(pagedList)
    }

    inner class GalleryViewHolder(val mBinding: BoardMediaItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        init {
            mBinding.run {
                ivGallery.setOnClickListener {
                    when (mSelectedItems.get(adapterPosition, false)) {
                        false -> {
                            mSelectedItems.put(adapterPosition, true)
                            ivGallery.complexOffAnimation()
                            ivSelected.startDrawableAnimation()
                        }
                        else -> {
                            mSelectedItems.put(adapterPosition, false)
                            ivGallery.complexOnAnimation()
                            ivSelected.visibility = View.GONE
                        }
                    }
                    val mediaFile: MediaStoreImage? = getItem(adapterPosition)
                    val checked = mSelectedItems.get(adapterPosition, false)
                    mListener?.onMediaItemClickEvent(
                        mBinding,
                        adapterPosition,
                        mediaFile,
                        mediaFile?.type,
                        checked
                    )
                }

                ivGallery.setOnLongClickListener {
                    val mediaFile: MediaStoreImage? = getItem(adapterPosition)
                    mListener?.onMediaItemLongClickEvent(
                        mBinding,
                        adapterPosition,
                        mediaFile,
                        mediaFile?.type
                    )
                    return@setOnLongClickListener true
                }
            }
        }

        fun bind(item: MediaStoreImage) {
            GlideImageLoader(
                mBinding.ivGallery,
                mBinding.pgbLoading
            ).load((item.contentUri).toString(), requestOptions(mContext))
        }
    }
}