package com.khs.lovelynote.model


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
    var isHold: Boolean? = null
) : BaseObservable() {

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
            notifyPropertyChanged(BR._thumbnail)
        }

    companion object {
        val itemCallback: DiffUtil.ItemCallback<LovelyNote> =
            object : DiffUtil.ItemCallback<LovelyNote>() {
                override fun areItemsTheSame(oldItem: LovelyNote, newItem: LovelyNote): Boolean {
                    return oldItem.Id == newItem.Id
                }

                override fun areContentsTheSame(oldItem: LovelyNote, newItem: LovelyNote): Boolean {
                    return oldItem.content.equals(newItem.content)
                }
            }
    }

}