package com.khs.lovelynote.view.activity

import android.os.Bundle
import com.khs.lovelynote.R
import com.khs.lovelynote.databinding.ActivityBoardDetailBinding
import com.khs.lovelynote.extension.Constants.TAG_DETAIL_FRAGMENT
import com.khs.lovelynote.extension.Constants.TAG_PARCELABLE_BOARD
import com.khs.lovelynote.model.Board
import com.khs.lovelynote.view.fragment.BoardDetailFragment

class BoardDetailActivity : BaseActivity<ActivityBoardDetailBinding>() {

    private lateinit var boardDetailFragment: BoardDetailFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView(R.layout.activity_board_detail)
        init(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?) {
        val fm = supportFragmentManager
        val board = intent.getParcelableExtra<Board>(TAG_PARCELABLE_BOARD)
        if (savedInstanceState == null) {
            val ft = fm.beginTransaction()
            boardDetailFragment = BoardDetailFragment.newInstance(board)
            ft.add(
                mBinding?.fragmentDetailContainer?.id!!,
                boardDetailFragment,
                TAG_DETAIL_FRAGMENT
            ).commit()
        } else {
            boardDetailFragment = fm.findFragmentByTag(TAG_DETAIL_FRAGMENT) as BoardDetailFragment
        }
    }
}