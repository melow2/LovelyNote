package com.khs.visionboard.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.DiffUtil


data class Board(
    private var _boardId: String?,
    private var _boardTitle: String?,
    private var _boardDescription: String?,
    private var _boardImageUrl: Int
) : BaseObservable(){

    var boardId: String?
        @Bindable get() = _boardId
        set(value) {
            _boardId = value
            notifyPropertyChanged(BR.boardId)
        }

    var boardTitle: String?
        @Bindable get() = _boardTitle
        set(value) {
            _boardTitle = value
            notifyPropertyChanged(BR.boardTitle)
        }

    var boardDescription: String?
        @Bindable get() = _boardDescription
        set(value) {
            _boardDescription = value
            notifyPropertyChanged(BR.boardDescription)
        }

    var boardImageUrl: Int
        @Bindable get() = _boardImageUrl
        set(value) {
            _boardImageUrl = value
            notifyPropertyChanged(BR.boardImageUrl)
        }

    companion object {
        val itemCallback: DiffUtil.ItemCallback<Board> = object : DiffUtil.ItemCallback<Board>() {
            override fun areItemsTheSame(oldItem: Board, newItem: Board): Boolean {
                return oldItem._boardId.equals(newItem._boardId)
            }

            override fun areContentsTheSame(oldItem: Board, newItem: Board): Boolean {
                return oldItem.boardDescription.equals(newItem.boardDescription)
            }
        }
    }

}