package com.khs.visionboard.model.mediastore

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.khs.visionboard.R
import com.khs.visionboard.module.glide.GlideImageLoader
import com.khs.visionboard.module.glide.ProgressAppGlideModule
import java.util.*

class MediaStoreVideo(
    override val id: Long,
    override val dateTaken: Date,
    override val displayName: String?,
    override val contentUri: Uri?,
    override val type: MediaStoreFileType
) : MediaStoreItem(id, dateTaken, displayName, contentUri, type), Parcelable {
    constructor(source: Parcel) : this(
        source.readLong(),
        source.readSerializable() as Date,
        source.readString(),
        source.readParcelable<Uri>(Uri::class.java.classLoader),
        MediaStoreFileType.values()[source.readInt()]
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeSerializable(dateTaken)
        writeString(displayName)
        writeParcelable(contentUri, 0)
        writeInt(type.ordinal)
    }

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

        @JvmField
        val CREATOR: Parcelable.Creator<MediaStoreVideo> =
            object : Parcelable.Creator<MediaStoreVideo> {
                override fun createFromParcel(source: Parcel): MediaStoreVideo =
                    MediaStoreVideo(source)

                override fun newArray(size: Int): Array<MediaStoreVideo?> = arrayOfNulls(size)
            }
    }
}