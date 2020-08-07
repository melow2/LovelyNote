package com.khs.visionboard.view.activity

import android.os.Bundle
import com.khs.visionboard.R
import com.khs.visionboard.databinding.ActivityBoardDetailBinding
import com.khs.visionboard.model.Board
import com.khs.visionboard.model.Constants.TAG_DETAIL_FRAGMENT
import com.khs.visionboard.model.Constants.TAG_PARCELABLE_BOARD
import com.khs.visionboard.view.fragment.BoardDetailFragment
import com.khs.visionboard.view.fragment.BoardListFragment

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
        if(savedInstanceState == null){
            val ft = fm.beginTransaction()
            boardDetailFragment = BoardDetailFragment.newInstance(board)
            ft.add(mBinding?.fragmentDetailContainer?.id!!,boardDetailFragment,TAG_DETAIL_FRAGMENT).commit()
        }else{
            boardDetailFragment = fm.findFragmentByTag(TAG_DETAIL_FRAGMENT) as BoardDetailFragment
        }
    }
}