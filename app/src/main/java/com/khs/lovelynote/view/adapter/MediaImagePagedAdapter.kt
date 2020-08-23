package com.khs.lovelynote.view.adapter

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.remove
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.khs.lovelynote.R
import com.khs.lovelynote.databinding.BoardItemMediaImageBinding
import com.khs.lovelynote.model.mediastore.MediaStoreFileType
import com.khs.lovelynote.model.mediastore.MediaStoreImage
import com.khs.lovelynote.model.mediastore.MediaStoreImage.Companion.diffCallback
import com.khs.lovelynote.module.glide.GlideImageLoader
import com.khs.lovelynote.module.glide.ProgressAppGlideModule.Companion.requestOptions

class MediaImagePagedAdapter(var mContext: Context) :
    PagedListAdapter<MediaStoreImage, MediaImagePagedAdapter.GalleryViewHolder>(diffCallback) {

    lateinit var mBinding: BoardItemMediaImageBinding
    private var mListener: MediaPagedImageListener? = null
    private var mSelectedItems: SparseBooleanArray = SparseBooleanArray(0)
    private val mImageList = arrayListOf<MediaStoreImage>()

    interface MediaPagedImageListener {
        fun onMediaImageClickEvent(
            binding: BoardItemMediaImageBinding,
            adapterPosition: Int,
            item: MediaStoreImage,
            type: MediaStoreFileType,
            checked: Boolean
        )
    }

    fun addListener(listener: MediaPagedImageListener) {
        this.mListener = listener
    }

    fun initSelectedItems() {
        mSelectedItems.clear()
    }

    fun removeSelectedItem(position:Int){
        mSelectedItems.remove(position,true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.board_item_media_image,
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

    inner class GalleryViewHolder(val mBinding: BoardItemMediaImageBinding) :
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
                    val mediaFile = getItem(adapterPosition)
                    val checked = mSelectedItems.get(adapterPosition, false)
                    mediaFile?.let {
                        mListener?.onMediaImageClickEvent(mBinding, adapterPosition, it, it.type, checked)
                    }
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