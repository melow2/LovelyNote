package com.khs.visionboard.model.mediastore

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import java.util.*

class MediaStoreVideo(
    override val id: Long,
    override val dateTaken: Date,
    override val displayName: String,
    override val contentUri: Uri,
    override val type: MediaStoreFileType
) : MediaStoreItem(id, dateTaken, displayName, contentUri, type) {
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<MediaStoreVideo>() {
            override fun areItemsTheSame(
                oldItem: MediaStoreVideo,
                newItem: MediaStoreVideo
            ): Boolean =
                oldItem.contentUri == newItem.contentUri

            override fun areContentsTheSame(
                oldItem: MediaStoreVideo,
                newItem: MediaStoreVideo
            ): Boolean =
                oldItem.contentUri == newItem.contentUri
        }
    }
}