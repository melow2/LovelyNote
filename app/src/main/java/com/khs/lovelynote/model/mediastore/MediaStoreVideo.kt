package com.khs.lovelynote.model.mediastore

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.khs.lovelynote.extension.parseTime
import com.khs.lovelynote.extension.toSimpleString
import java.text.SimpleDateFormat
import java.util.*

data class MediaStoreVideo(
    override var id: Long,
    override var dateTaken: Date?,
    override var displayName: String?,
    override var contentUri: String?,
    override var type: MediaStoreFileType,
    var _duration: String?
) : MediaStoreItem(id, dateTaken, displayName, contentUri, type), Parcelable {

    var duration: String?
        get() = _duration?.toLong()?.parseTime()
        set(value) {
            _duration = value
        }

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
        MediaStoreFileType.values()[source.readInt()],
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeSerializable(dateTaken)
        writeString(displayName)
        writeString(contentUri)
        writeInt(type.ordinal)
        writeString(_duration)
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