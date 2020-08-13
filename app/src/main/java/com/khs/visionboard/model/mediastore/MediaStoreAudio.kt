package com.khs.visionboard.model.mediastore

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import com.khs.visionboard.extension.formateMilliSeccond
import java.util.*

data class MediaStoreAudio(
    override val id: Long,
    override val dateTaken: Date,
    override val displayName: String,
    override val contentUri: Uri,
    override val type: MediaStoreFileType,
    val album: String,
    val title: String,
    var _duration: String?
) : MediaStoreItem(id, dateTaken, displayName, contentUri, type) {

    var duration: String?
        get() = _duration?.toLong()?.formateMilliSeccond()
        set(value) {
            _duration = value
        }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<MediaStoreAudio>() {
            override fun areItemsTheSame(
                oldItem: MediaStoreAudio,
                newItem: MediaStoreAudio
            ): Boolean =
                oldItem.contentUri == newItem.contentUri

            override fun areContentsTheSame(
                oldItem: MediaStoreAudio,
                newItem: MediaStoreAudio
            ): Boolean =
                oldItem.contentUri == newItem.contentUri
        }
    }
}