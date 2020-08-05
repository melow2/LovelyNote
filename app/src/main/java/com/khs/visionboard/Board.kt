package com.khs.visionboard

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR


data class Board(
    private var _name: String?,
    private var _description: String?,
    private var _imageUrl: Int
) : BaseObservable(), Parcelable {
    var name: String?
        @Bindable get() = _name
        set(value) {
            _name = value
            notifyPropertyChanged(BR.name)
        }

    var description: String?
        @Bindable get() = _description
        set(value) {
            _description = value
            notifyPropertyChanged(BR.description)
        }

    var imageUrl: Int
        @Bindable get() = _imageUrl
        set(value) {
            _imageUrl = value
            notifyPropertyChanged(BR.imageUrl)
        }

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(_name)
        writeString(_description)
        writeInt(_imageUrl)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Board> = object : Parcelable.Creator<Board> {
            override fun createFromParcel(source: Parcel): Board = Board(source)
            override fun newArray(size: Int): Array<Board?> = arrayOfNulls(size)
        }
    }
}