package com.khs.lovelynote.model.mediastore

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.khs.lovelynote.extension.parseTime
import com.khs.lovelynote.extension.toSimpleString
import java.text.SimpleDateFormat
import java.util.*

data class MediaStoreAudio(
    override var id: Long,
    override var dateTaken: Date?,
    override var displayName: String?,
    override var contentUri: Uri?,
    override var type: MediaStoreFileType,
    var album: String?,
    var title: String?,
    var _duration: String?
) : MediaStoreItem(id, dateTaken, displayName, contentUri, type), Parcelable {

    var duration: String?
        get() = _duration?.toLong()?.parseTime()
        set(value) {
            _duration = value
        }

    var _dateTaken:String?
        get() = dateTaken?.toSimpleString()
        set(value){
            dateTaken = SimpleDateFormat().parse(value)
        }

    constructor(source: Parcel) : this(
        source.readLong(),
        source.readSerializable() as Date,
        source.readString(),
        source.readParcelable<Uri>(Uri::class.java.classLoader),
        MediaStoreFileType.values()[source.readInt()],
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeSerializable(dateTaken)
        writeString(displayName)
        writeParcelable(contentUri, 0)
        writeInt(type.ordinal)
        writeString(album)
        writeString(title)
        writeString(_duration)
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

        @JvmField
        val CREATOR: Parcelable.Creator<MediaStoreAudio> =
            object : Parcelable.Creator<MediaStoreAudio> {
                override fun createFromParcel(source: Parcel): MediaStoreAudio =
                    MediaStoreAudio(source)

                override fun newArray(size: Int): Array<MediaStoreAudio?> = arrayOfNulls(size)
            }
    }
}