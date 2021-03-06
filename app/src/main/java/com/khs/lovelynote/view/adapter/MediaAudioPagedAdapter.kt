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
import com.khs.lovelynote.databinding.BoardItemMediaAudioBinding
import com.khs.lovelynote.extension.complexOffAnimation
import com.khs.lovelynote.extension.complexOnAnimation
import com.khs.lovelynote.extension.startDrawableAnimation
import com.khs.lovelynote.model.mediastore.MediaStoreAudio
import com.khs.lovelynote.model.mediastore.MediaStoreAudio.Companion.diffCallback
import com.khs.lovelynote.model.mediastore.MediaStoreFileType
import timber.log.Timber

class MediaAudioPagedAdapter(var mContext: Context) :
    PagedListAdapter<MediaStoreAudio, MediaAudioPagedAdapter.GalleryViewHolder>(diffCallback) {

    lateinit var mBinding: BoardItemMediaAudioBinding
    private var mListener: MediaPagedAudioListener? = null
    private var mSelectedItems: SparseBooleanArray = SparseBooleanArray(0)
    private val mImageList = arrayListOf<MediaStoreAudio>()

    interface MediaPagedAudioListener {
        fun onMediaAudioClickEvent(
            binding: BoardItemMediaAudioBinding,
            adapterPosition: Int,
            item: MediaStoreAudio,
            type: MediaStoreFileType,
            checked: Boolean
        )
        fun onMediaAudioPlayClientEvent(
            item:MediaStoreAudio?
        )
    }

    fun addListener(listener: MediaPagedAudioListener) {
        this.mListener = listener
    }

    fun initSelectedItems() {
        mSelectedItems.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.board_item_media_audio,
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

    fun removeSelectedItem(position:Int){
        mSelectedItems.remove(position,true)
    }

    override fun getItemCount(): Int {
        return mImageList.size
    }

    override fun submitList(pagedList: PagedList<MediaStoreAudio>?) {
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

    inner class GalleryViewHolder(val mBinding: BoardItemMediaAudioBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        init {
            mBinding.run {
                ivAudio.setOnClickListener {
                    when (mSelectedItems.get(adapterPosition, false)) {
                        false -> {
                            mSelectedItems.put(adapterPosition, true)
                            rootAudioLyt.complexOffAnimation()
                            ivSelected.startDrawableAnimation()
                        }
                        else -> {
                            mSelectedItems.put(adapterPosition, false)
                            rootAudioLyt.complexOnAnimation()
                            ivSelected.visibility = View.GONE
                        }
                    }
                    val mediaFile = getItem(adapterPosition)
                    val checked = mSelectedItems.get(adapterPosition, false)
                    mediaFile?.let {
                        mListener?.onMediaAudioClickEvent(
                            mBinding,
                            adapterPosition,
                            it,
                            it.type,
                            checked
                        )
                    }
                }

                btnAudioPlay.setOnClickListener {
                    val mediaFile = getItem(adapterPosition)
                    mListener?.onMediaAudioPlayClientEvent(mediaFile)
                }
            }
        }

        fun bind(item: MediaStoreAudio) {
            Timber.d(item.toString())
            mBinding.audio = item
        }
    }
}