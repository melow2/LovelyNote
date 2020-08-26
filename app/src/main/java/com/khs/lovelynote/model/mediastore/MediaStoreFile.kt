package com.khs.lovelynote.model.mediastore

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import java.util.*

data class MediaStoreFile(
    override var id: Long,
    override var dateTaken: Date?,
    override var displayName: String?,
    override var contentUri: String?,
    override var type: MediaStoreFileType
) : MediaStoreItem(id, dateTaken, displayName, contentUri, type), Parcelable {
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
        val diffCallback = object : DiffUtil.ItemCallback<MediaStoreFile>() {
            override fun areItemsTheSame(
                oldItem: MediaStoreFile,
                newItem: MediaStoreFile
            ): Boolean =
                oldItem.contentUri == newItem.contentUri

            override fun areContentsTheSame(
                oldItem: MediaStoreFile,
                newItem: MediaStoreFile
            ): Boolean =
                oldItem.contentUri == newItem.contentUri
        }

        @JvmField
        val CREATOR: Parcelable.Creator<MediaStoreFile> =
            object : Parcelable.Creator<MediaStoreFile> {
                override fun createFromParcel(source: Parcel): MediaStoreFile =
                    MediaStoreFile(source)

                override fun newArray(size: Int): Array<MediaStoreFile?> = arrayOfNulls(size)
            }
    }
}