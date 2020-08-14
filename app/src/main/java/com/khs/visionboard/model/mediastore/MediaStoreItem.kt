package com.khs.visionboard.model.mediastore

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import java.util.*

open class MediaStoreItem(
    open val id: Long,
    open val dateTaken: Date,
    open val displayName: String?,
    open val contentUri: Uri?,
    open val type: MediaStoreFileType
){
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<MediaStoreItem>() {
            override fun areItemsTheSame(oldItem: MediaStoreItem, newItem: MediaStoreItem): Boolean =
                oldItem.contentUri == newItem.contentUri

            override fun areContentsTheSame(oldItem: MediaStoreItem, newItem: MediaStoreItem): Boolean =
                oldItem.contentUri == newItem.contentUri
        }
    }
}