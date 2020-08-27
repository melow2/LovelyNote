package com.khs.lovelynote.view.adapter

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.khs.lovelynote.R
import com.khs.lovelynote.databinding.BoardItemBinding
import com.khs.lovelynote.extension.Constants.TAG_RECORD
import com.khs.lovelynote.extension.getMediaMetaData
import com.khs.lovelynote.extension.parseTime
import com.khs.lovelynote.model.LovelyNote
import com.khs.lovelynote.model.mediastore.MediaStoreAudio
import com.khs.lovelynote.model.mediastore.MediaStoreFile
import com.khs.lovelynote.model.mediastore.MediaStoreFileType
import com.khs.lovelynote.model.mediastore.MediaStoreVideo
import com.khs.lovelynote.module.glide.GlideImageLoader
import com.khs.lovelynote.module.glide.ProgressAppGlideModule
import timber.log.Timber

class LovelyNoteListAdapter(
    val mContext: Context
) : ListAdapter<LovelyNote, LovelyNoteListAdapter.NoteViewHolder>(LovelyNote.itemCallback) {

    private lateinit var mBinding: BoardItemBinding
    private var listener: LovelyNoteEventListener? = null
    private val handler = Handler(Looper.getMainLooper())

    interface LovelyNoteEventListener {
        fun onClick(position: Int)
        fun onDelete(position: Int)
        fun onPlayAudio(item: MediaStoreAudio)
        fun onPlayVideo(item:MediaStoreVideo)
        fun onOpenFile(item:MediaStoreFile)
    }

    fun addEventListener(listener: LovelyNoteEventListener) {
        this.listener = listener
    }

    override fun getItemId(position: Int): Long {
        return currentList[position].Id!!
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
                btnPlayAudio.setOnClickListener {
                    val audioItem = getItem(adapterPosition).mediaItems?.get(0).run {
                        val contentUri = Uri.parse(this?.contentUri)
                        MediaStoreAudio(
                            this?.id!!, // id never be null
                            this.dateTaken,
                            this.displayName ?:TAG_RECORD,
                            this.contentUri,
                            this.type,
                            mContext.getMediaMetaData(contentUri, MediaMetadataRetriever.METADATA_KEY_ALBUM)?: TAG_RECORD,
                            mContext.getMediaMetaData(contentUri, MediaMetadataRetriever.METADATA_KEY_TITLE)?: TAG_RECORD,
                            mContext.getMediaMetaData(contentUri, MediaMetadataRetriever.METADATA_KEY_DURATION)
                        )
                    }
                    listener?.onPlayAudio(audioItem)
                }
                btnPlayVideo.setOnClickListener {
                    val videoItem = getItem(adapterPosition).mediaItems?.get(0).run {
                        val contentUri = Uri.parse(this?.contentUri)
                        MediaStoreVideo(
                            this?.id!!, // id never be null
                            this.dateTaken,
                            this.displayName,
                            this.contentUri,
                            this.type,
                            mContext.getMediaMetaData(contentUri, MediaMetadataRetriever.METADATA_KEY_DURATION)
                        )
                    }
                    listener?.onPlayVideo(videoItem)
                }
                btnOpenFile.setOnClickListener {
                    val fileItem = getItem(adapterPosition).mediaItems?.get(0).run {
                        MediaStoreFile(
                            this?.id!!, // id never be null
                            this.dateTaken,
                            this.displayName,
                            this.contentUri,
                            this.type
                        )
                    }
                    listener?.onOpenFile(fileItem)
                }
            }
        }

        fun bind(note:LovelyNote) {
            Timber.d("bind, note: ${note.thumbnail}")
            mBinding.apply {
                this.note = note
                if(note.mediaItems?.size==0){
                    tvItemCount.visibility = View.INVISIBLE
                    ivThumbnailImage.visibility = View.VISIBLE
                    Glide.with(mContext).load(R.drawable.ic_no_photos).into(ivThumbnailImage)
                }else{
                    when(note.mediaItems?.get(0)?.type){
                        MediaStoreFileType.IMAGE ->{
                            handler.post {
                                ivThumbnailImage.visibility = View.VISIBLE
                                tvItemCount.text = note.mediaItems?.size.toString()
                                GlideImageLoader(
                                    ivThumbnailImage,
                                    null
                                ).load(note.thumbnail,
                                    ProgressAppGlideModule.requestOptions(mContext)
                                )
                            }
                        }
                        MediaStoreFileType.AUDIO->{
                            handler.post{
                                rootLytThumbnailAudio.visibility = View.VISIBLE
                                val contentUri = Uri.parse(note.mediaItems?.get(0)?.contentUri)
                                val duration = mContext.getMediaMetaData(contentUri, MediaMetadataRetriever.METADATA_KEY_DURATION)
                                tvAudioDuration.text = duration?.toLong()?.parseTime()
                                tvItemCount.text = note.mediaItems?.size.toString()
                                //  Glide.with(mContext).load(R.drawable.ic_no_photos).into(ivThumbnail)
                            }
                        }

                        MediaStoreFileType.VIDEO->{
                            handler.post{
                                rootLytThumbnailVideo.visibility = View.VISIBLE
                                GlideImageLoader(
                                    ivThumbnailVideo,
                                    null
                                ).load(note.thumbnail,
                                    ProgressAppGlideModule.requestOptions(mContext)
                                )
                                val contentUri = Uri.parse(note.mediaItems?.get(0)?.contentUri)
                                val duration = mContext.getMediaMetaData(contentUri, MediaMetadataRetriever.METADATA_KEY_DURATION)
                                tvVideoDuration.text = duration?.toLong()?.parseTime()
                                tvItemCount.text = note.mediaItems?.size.toString()
                                //  Glide.with(mContext).load(R.drawable.ic_no_photos).into(ivThumbnail)
                            }
                        }
                        MediaStoreFileType.FILE->{
                            handler.post{
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
    }

}
