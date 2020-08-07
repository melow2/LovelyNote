package com.khs.visionboard.view.activity

import android.os.Bundle
import com.khs.visionboard.R
import com.khs.visionboard.databinding.ActivityMainBinding
import com.khs.visionboard.model.Constants.TAG_LIST_FRAGMENT
import com.khs.visionboard.view.fragment.BoardListFragment
import timber.log.Timber

class MainActivity : BaseActivity<ActivityMainBinding>() {

    lateinit var boardListFragment: BoardListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView(R.layout.activity_main)
        init(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?) {
        val fm = supportFragmentManager
        if (savedInstanceState == null) {
            val ft = fm.beginTransaction()
            boardListFragment = BoardListFragment.newInstance("param1", "param2")
            ft.add(mBinding?.fragmentMainContainer?.id!!, boardListFragment, TAG_LIST_FRAGMENT).commit()
        } else {
            boardListFragment = fm.findFragmentByTag(TAG_LIST_FRAGMENT) as BoardListFragment
        }
    }
}