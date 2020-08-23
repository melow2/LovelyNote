package com.khs.lovelynote.model.mediastore

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class SelectedItem(
    val position: Int?,
    val contentUri: Uri?,
    val type: MediaStoreFileType,
    val item: MediaStoreItem?
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readValue(Int::class.java.classLoader) as Int?,
        source.readParcelable<Uri>(Uri::class.java.classLoader),
        MediaStoreFileType.values()[source.readInt()],
        source.readParcelable<MediaStoreItem>(MediaStoreItem::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(position)
        writeParcelable(contentUri, 0)
        writeInt(type.ordinal)
        writeParcelable(item, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SelectedItem> = object : Parcelable.Creator<SelectedItem> {
            override fun createFromParcel(source: Parcel): SelectedItem = SelectedItem(source)
            override fun newArray(size: Int): Array<SelectedItem?> = arrayOfNulls(size)
        }
    }
}