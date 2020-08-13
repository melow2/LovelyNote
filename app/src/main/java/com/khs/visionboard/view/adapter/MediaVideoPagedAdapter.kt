package com.khs.visionboard.view.adapter

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
import com.khs.visionboard.R
import com.khs.visionboard.databinding.BoardItemMediaVideoBinding
import com.khs.visionboard.extension.complexOffAnimation
import com.khs.visionboard.extension.complexOnAnimation
import com.khs.visionboard.extension.startDrawableAnimation
import com.khs.visionboard.model.mediastore.MediaStoreFileType
import com.khs.visionboard.model.mediastore.MediaStoreVideo
import com.khs.visionboard.model.mediastore.MediaStoreVideo.Companion.diffCallback
import com.khs.visionboard.module.glide.GlideImageLoader
import com.khs.visionboard.module.glide.ProgressAppGlideModule.Companion.requestOptions

class MediaVideoPagedAdapter(var mContext: Context) :
    PagedListAdapter<MediaStoreVideo, MediaVideoPagedAdapter.VideoViewHolder>(diffCallback) {

    lateinit var mBinding: BoardItemMediaVideoBinding
    private var mListener: MediaPagedViedoListener? = null
    private var mSelectedItems: SparseBooleanArray = SparseBooleanArray(0)
    private val mImageList = arrayListOf<MediaStoreVideo>()

    interface MediaPagedViedoListener {
        fun onMediaVideoClickEvent(
            binding: BoardItemMediaVideoBinding,
            adapterPosition: Int,
            item: MediaStoreVideo,
            type: MediaStoreFileType,
            checked: Boolean
        )
    }

    fun addListener(listener: MediaPagedViedoListener) {
        this.mListener = listener
    }

    fun initSelectedItems() {
        mSelectedItems.clear()
    }

    fun removeSelectedItem(position:Int){
        mSelectedItems.remove(position,true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.board_item_media_video,
            parent,
            false
        )
        return VideoViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
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

    override fun submitList(pagedList: PagedList<MediaStoreVideo>?) {
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

    inner class VideoViewHolder(val mBinding: BoardItemMediaVideoBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        init {
            mBinding.run {
                ivVideo.setOnClickListener {
                    when (mSelectedItems.get(adapterPosition, false)) {
                        false -> {
                            mSelectedItems.put(adapterPosition, true)
                            ivVideo.complexOffAnimation()
                            ivSelected.startDrawableAnimation()
                        }
                        else -> {
                            mSelectedItems.put(adapterPosition, false)
                            ivVideo.complexOnAnimation()
                            ivSelected.visibility = View.GONE
                        }
                    }
                    val mediaFile = getItem(adapterPosition)
                    val checked = mSelectedItems.get(adapterPosition, false)
                    mediaFile?.let {
                        mListener?.onMediaVideoClickEvent(
                            mBinding,
                            adapterPosition,
                            it,
                            it.type,
                            checked
                        )
                    }
                }
            }
        }

        fun bind(item: MediaStoreVideo) {
            GlideImageLoader(mBinding.ivVideo, null).load(
                (item.contentUri).toString(),
                requestOptions(mContext)
            )
        }
    }
}