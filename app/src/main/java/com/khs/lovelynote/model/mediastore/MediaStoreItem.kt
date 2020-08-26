package com.khs.lovelynote.model.mediastore

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import java.util.*

open class MediaStoreItem(
    open val id: Long,
    open val dateTaken: Date?,
    open val displayName: String?,
    open var contentUri: String?,
    open val type: MediaStoreFileType
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readLong(),
        source.readSerializable() as Date?,
        source.readString(),
        source.readString(),
        MediaStoreFileType.values()[source.readInt()]
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeSerializable(dateTaken)
        writeString(displayName)
        writeString(contentUri)
        writeInt(type.ordinal)
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<MediaStoreItem>() {
            override fun areItemsTheSame(
                oldItem: MediaStoreItem,
                newItem: MediaStoreItem
            ): Boolean =
                oldItem.contentUri == newItem.contentUri

            override fun areContentsTheSame(
                oldItem: MediaStoreItem,
                newItem: MediaStoreItem
            ): Boolean =
                oldItem.contentUri == newItem.contentUri
        }

        @JvmField
        val CREATOR: Parcelable.Creator<MediaStoreItem> =
            object : Parcelable.Creator<MediaStoreItem> {
                override fun createFromParcel(source: Parcel): MediaStoreItem =
                    MediaStoreItem(source)

                override fun newArray(size: Int): Array<MediaStoreItem?> = arrayOfNulls(size)
            }
    }
}