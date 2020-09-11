package com.khs.lovelynote.model


import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.recyclerview.widget.DiffUtil
import com.khs.lovelynote.BR
import com.khs.lovelynote.extension.toSimpleString
import com.khs.lovelynote.model.mediastore.MediaStoreItem
import java.text.SimpleDateFormat
import java.util.*

data class LovelyNote(
    var Id: Long? = null,
    var content: String? = null,
    var thumbnail: String? = null,
    var mediaItems: List<MediaStoreItem>? = null,
    var createTimeStamp: Date? = null,
    var updateTimeStamp: Date? = null,
    var isHold: Boolean? = null,
    var isLock: Boolean? = null
) : BaseObservable(), Parcelable {
    var _content: String?
        @Bindable get() = content
        set(value) {
            content = value
            notifyPropertyChanged(BR._content)
        }

    var _mediaItems: List<MediaStoreItem>?
        @Bindable get() = mediaItems
        set(value) {
            mediaItems = value
            notifyPropertyChanged(BR._mediaItems)
        }

    var _createTimeStamp: String?
        get() = createTimeStamp?.toSimpleString()
        set(value) {
            createTimeStamp = SimpleDateFormat().parse(value)
            // notifyPropertyChanged(BR._updateTimeStamp)
        }

    var _updateTimeStamp: String?
        get() = updateTimeStamp?.toSimpleString()
        set(value) {
            updateTimeStamp = SimpleDateFormat().parse(value)
            // notifyPropertyChanged(BR._updateTimeStamp)
        }

    var _thumbnail: String?
        @Bindable get() = thumbnail
        set(value) {
            thumbnail = value
            // notifyPropertyChanged(BR._thumbnail)
        }

    constructor(source: Parcel) : this(
        source.readValue(Long::class.java.classLoader) as Long?,
        source.readString(),
        source.readString(),
        source.createTypedArrayList(MediaStoreItem.CREATOR),
        source.readSerializable() as Date?,
        source.readSerializable() as Date?,
        source.readValue(Boolean::class.java.classLoader) as Boolean?,
        source.readValue(Boolean::class.java.classLoader) as Boolean?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(Id)
        writeString(content)
        writeString(thumbnail)
        writeTypedList(mediaItems)
        writeSerializable(createTimeStamp)
        writeSerializable(updateTimeStamp)
        writeValue(isHold)
        writeValue(isLock)
    }

    companion object {
        val itemCallback: DiffUtil.ItemCallback<LovelyNote> =
            object : DiffUtil.ItemCallback<LovelyNote>() {
                override fun areItemsTheSame(oldItem: LovelyNote, newItem: LovelyNote): Boolean {
                    return oldItem.Id == newItem.Id
                }

                override fun areContentsTheSame(oldItem: LovelyNote, newItem: LovelyNote): Boolean {
                    return oldItem.updateTimeStamp == newItem.updateTimeStamp
                            && oldItem._mediaItems == newItem._mediaItems
                            && oldItem._content == newItem._content
                }
            }

        @JvmField
        val CREATOR: Parcelable.Creator<LovelyNote> = object : Parcelable.Creator<LovelyNote> {
            override fun createFromParcel(source: Parcel): LovelyNote = LovelyNote(source)
            override fun newArray(size: Int): Array<LovelyNote?> = arrayOfNulls(size)
        }
    }
}