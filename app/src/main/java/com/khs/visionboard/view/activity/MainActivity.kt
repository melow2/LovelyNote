package com.khs.visionboard.view.activity

import android.os.Bundle
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.khs.visionboard.R
import com.khs.visionboard.databinding.ActivityMainBinding
import com.khs.visionboard.extension.Constants.TAG_LIST_FRAGMENT
import com.khs.visionboard.view.fragment.BoardListFragment


/*
* todo 권한 splash activity로 옮겨야 함.
* */
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var boardListFragment: BoardListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView(R.layout.activity_main)
        TedPermission.with(this)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    init(savedInstanceState)
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    finish()
                }
            })
            .setDeniedMessage("앱을 실행하려면 권한이 필요합니다.")
            .setPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.RECORD_AUDIO
            )
            .check()
    }

    private fun init(savedInstanceState: Bundle?) {
        val fm = supportFragmentManager
        if (savedInstanceState == null) {
            val ft = fm.beginTransaction()
            boardListFragment = BoardListFragment.newInstance("param1", "param2")
            ft.add(mBinding?.fragmentMainContainer?.id!!, boardListFragment, TAG_LIST_FRAGMENT)
                .commit()
        } else {
            boardListFragment = fm.findFragmentByTag(TAG_LIST_FRAGMENT) as BoardListFragment
        }
    }
}