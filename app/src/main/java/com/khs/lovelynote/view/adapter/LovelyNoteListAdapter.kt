package com.khs.lovelynote.view.adapter

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.khs.lovelynote.R
import com.khs.lovelynote.databinding.BoardItemBinding
import com.khs.lovelynote.extension.Constants
import com.khs.lovelynote.extension.getMediaMetaData
import com.khs.lovelynote.extension.parseTime
import com.khs.lovelynote.model.LovelyNote
import com.khs.lovelynote.model.mediastore.*
import com.khs.lovelynote.module.glide.GlideImageLoader
import com.khs.lovelynote.module.glide.ProgressAppGlideModule
import com.khs.lovelynote.view.dialog.ImageViewDialogFragment
import timber.log.Timber

class LovelyNoteListAdapter(
    val mContext: Context
) : ListAdapter<LovelyNote, LovelyNoteListAdapter.NoteViewHolder>(LovelyNote.itemCallback) {

    private lateinit var mBinding: BoardItemBinding
    private var listener: LovelyNoteEventListener? = null
    private val handler = Handler(Looper.getMainLooper())
    private var unFilteredList = listOf<LovelyNote>()

    interface LovelyNoteEventListener {
        fun onClick(position: LovelyNote, ivThumbnailImage: ImageView)
        fun onClick(currentList: List<MediaStoreItem>?)
    }

    fun addEventListener(listener: LovelyNoteEventListener) {
        this.listener = listener
    }

    override fun getItemId(position: Int): Long {
        return unFilteredList[position].Id!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.board_iist_item, parent, false
        )
        return NoteViewHolder(mBinding)
    }


    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val board = getItem(position)
        holder.bind(board)              // bind item
    }

    inner class NoteViewHolder(private val mBinding: BoardItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        var viewBackGround: ConstraintLayout = mBinding.viewBackground
        var viewForeGround: ConstraintLayout = mBinding.viewForeground

        init {
            mBinding.apply {
                rootThumbnailLyt.setOnClickListener {
                    listener?.onClick(getItem(adapterPosition).mediaItems)
                }
                rootContentLyt.setOnClickListener {
                    listener?.onClick(getItem(adapterPosition),ivThumbnailImage)
                }
            }
        }

        fun bind(note: LovelyNote) {
            Timber.d("bind, note: ${note.thumbnail}")
            mBinding.apply {
                this.note = note
                if (note.mediaItems?.size == 0) {
                    tvItemCount.visibility = View.INVISIBLE
                    ivThumbnailImage.visibility = View.VISIBLE
                    Glide.with(mContext).load(R.drawable.ic_no_photos).into(ivThumbnailImage)
                } else {
                    when (note.mediaItems?.get(0)?.type) {
                        MediaStoreFileType.IMAGE -> {
                            ivThumbnailImage.visibility = View.VISIBLE
                            tvItemCount.text = note.mediaItems?.size.toString()
                            GlideImageLoader(
                                ivThumbnailImage,
                                null
                            ).load(
                                note._thumbnail,
                                ProgressAppGlideModule.requestOptions(mContext)
                            )

                        }
                        MediaStoreFileType.AUDIO -> {
                            rootLytThumbnailAudio.visibility = View.VISIBLE
                            val contentUri = Uri.parse(note.mediaItems?.get(0)?.contentUri)
                            val itemName = note.mediaItems?.get(0)!!.displayName
                            val duration = mContext.getMediaMetaData(
                                contentUri,
                                MediaMetadataRetriever.METADATA_KEY_DURATION
                            )
                            tvAudioDuration.text = duration?.toLong()?.parseTime()
                            tvItemCount.text = note.mediaItems?.size.toString()
                        }

                        MediaStoreFileType.VIDEO -> {
                            val contentUri = Uri.parse(note.mediaItems?.get(0)?.contentUri)
                            rootLytThumbnailVideo.visibility = View.VISIBLE
                            GlideImageLoader(
                                ivThumbnailVideo,
                                null
                            ).load(
                                note._thumbnail,
                                ProgressAppGlideModule.requestOptions(mContext)
                            )
                            val duration = mContext.getMediaMetaData(
                                contentUri,
                                MediaMetadataRetriever.METADATA_KEY_DURATION
                            )
                            tvVideoDuration.text = duration?.toLong()?.parseTime()
                            tvItemCount.text = note.mediaItems?.size.toString()
                            //  Glide.with(mContext).load(R.drawable.ic_no_photos).into(ivThumbnail)

                        }
                        MediaStoreFileType.FILE -> {
                            rootLytThumbnailFile.visibility = View.VISIBLE
                            tvFileName.text = note.mediaItems?.get(0)?.displayName
                            tvItemCount.text = note.mediaItems?.size.toString()
                            //  Glide.with(mContext).load(R.drawable.ic_no_photos).into(ivThumbnail)
                        }
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).Id?.toInt()!!
    }

    fun modifyList(list: List<LovelyNote>) {
        unFilteredList = list
        submitList(list)
    }

    fun filter(query: CharSequence?) {
        val list = mutableListOf<LovelyNote>()

        // perform the data filtering
        if (!query.isNullOrEmpty()) {
            for (note in unFilteredList) {
                val content = note.content?.toLowerCase()
                content?.let {
                    if (it.contains(query.toString().toLowerCase())) {
                        list.add(note)
                    }
                }
            }
        } else {
            list.addAll(unFilteredList)
        }
        submitList(list)
    }

}
