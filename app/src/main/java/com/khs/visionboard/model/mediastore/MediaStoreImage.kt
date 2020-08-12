package com.khs.visionboard.model.mediastore

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import java.util.*

data class MediaStoreImage(
    override val id:Long,
    override val dateTaken: Date,
    override val displayName: String,
    override var contentUri: Uri,
    override val type: MediaStoreFileType
):MediaStoreItem(id,dateTaken,displayName,contentUri,type){
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<MediaStoreImage>() {
            override fun areItemsTheSame(oldItem: MediaStoreImage, newItem: MediaStoreImage): Boolean =
                oldItem.contentUri == newItem.contentUri

            override fun areContentsTheSame(oldItem: MediaStoreImage, newItem: MediaStoreImage): Boolean =
                oldItem.contentUri == newItem.contentUri
        }
    }
}

