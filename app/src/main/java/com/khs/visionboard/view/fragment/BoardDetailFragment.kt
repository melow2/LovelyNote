package com.khs.visionboard.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.khs.visionboard.R
import com.khs.visionboard.databinding.BoardDetailBinding
import com.khs.visionboard.model.Board
import com.khs.visionboard.model.Constants.TAG_PARCELABLE_BOARD
import timber.log.Timber

class BoardDetailFragment : BaseFragment<BoardDetailBinding>() {

    private var board: Board? = null

    companion object {
        @JvmStatic
        fun newInstance(board: Board?) =
            BoardDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(TAG_PARCELABLE_BOARD, board)
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            board = it.getParcelable(TAG_PARCELABLE_BOARD)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindView(inflater, container!!, R.layout.fragment_board_detail)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding?.board = board
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onDestroyView() {
        Timber.d("onDestroyView()")
        super.onDestroyView()
    }

    override fun onDetach() {
        Timber.d("onDetach()")
        super.onDetach()
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}