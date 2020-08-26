package com.khs.lovelynote.model.mediastore

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.khs.lovelynote.extension.toSimpleString
import java.text.SimpleDateFormat
import java.util.*

data class MediaStoreImage(
    override var id: Long,
    override var dateTaken: Date?,
    override var displayName: String?,
    override var contentUri: String?,
    override var type: MediaStoreFileType
) : MediaStoreItem(id, dateTaken, displayName, contentUri, type), Parcelable {
    var _dateTaken: String?
        get() = dateTaken?.toSimpleString()
        set(value) {
            dateTaken = SimpleDateFormat().parse(value)
        }

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
        val diffCallback = object : DiffUtil.ItemCallback<MediaStoreImage>() {
            override fun areItemsTheSame(
                oldItem: MediaStoreImage,
                newItem: MediaStoreImage
            ): Boolean =
                oldItem.contentUri == newItem.contentUri

            override fun areContentsTheSame(
                oldItem: MediaStoreImage,
                newItem: MediaStoreImage
            ): Boolean =
                oldItem.contentUri == newItem.contentUri
        }

        @JvmField
        val CREATOR: Parcelable.Creator<MediaStoreImage> =
            object : Parcelable.Creator<MediaStoreImage> {
                override fun createFromParcel(source: Parcel): MediaStoreImage =
                    MediaStoreImage(source)

                override fun newArray(size: Int): Array<MediaStoreImage?> = arrayOfNulls(size)
            }
    }
}

