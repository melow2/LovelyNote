package com.khs.visionboard.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.khs.visionboard.R
import com.khs.visionboard.databinding.BoardItemBinding
import com.khs.visionboard.model.Board

class BoardListAdapter(
    val mContext: Context
) : ListAdapter<Board, BoardListAdapter.BoardViewHolder>(Board.itemCallback) {

    private lateinit var mBinding: BoardItemBinding
    private var listener: BoardListEvent? = null

    interface BoardListEvent {
        fun onClick(position: Int)
        fun onDelete(position: Int)
    }

    fun addEventListener(listener: BoardListEvent) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.board_iist_item, parent, false
        )
        return BoardViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val board = getItem(position)
        holder.bind(board)              // bind item
    }

    inner class BoardViewHolder(private val mBinding: BoardItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        init {
            mBinding.rootCvBoard.setOnClickListener {
                listener?.let {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener?.onClick(adapterPosition)
                    }
                }
            }
            mBinding.btnDelete.setOnClickListener {
                listener?.let {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener?.onDelete(adapterPosition)
                    }
                }
            }
        }

        fun bind(board: Board) {
            mBinding.board = board
        }
    }

}
