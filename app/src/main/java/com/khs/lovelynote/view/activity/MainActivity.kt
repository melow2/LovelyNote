package com.khs.lovelynote.view.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.khs.lovelynote.R
import com.khs.lovelynote.R.drawable.*
import com.khs.lovelynote.R.menu.bottom_app_bar_add
import com.khs.lovelynote.R.menu.bottom_app_bar_main
import com.khs.lovelynote.databinding.ActivityMainBinding
import com.khs.lovelynote.extension.Constants.TAG_ADD_FRAGMENT
import com.khs.lovelynote.extension.Constants.TAG_LIST_FRAGMENT
import com.khs.lovelynote.view.fragment.BoardAddFragment
import com.khs.lovelynote.view.fragment.BoardListFragment


/*
* todo 권한 splash activity로 옮겨야 함.
* */
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var mBoardListFragment: BoardListFragment
    private lateinit var mBoardAddFragment: BoardAddFragment
    private var mCurrentFabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
    private var mHandler: Handler = Handler(Looper.getMainLooper())

    private val addVisibilityChanged: FloatingActionButton.OnVisibilityChangedListener =
        object : FloatingActionButton.OnVisibilityChangedListener() {
            override fun onShown(fab: FloatingActionButton?) {
                super.onShown(fab)
            }

            override fun onHidden(fab: FloatingActionButton?) {
                super.onHidden(fab)
                val fm = supportFragmentManager
                val ft = fm.beginTransaction()
                mBinding?.apply {
                    bottomAppBar.toggleFab()
                    when (mCurrentFabAlignmentMode) {
                        BottomAppBar.FAB_ALIGNMENT_MODE_CENTER -> {
                            mBoardAddFragment = BoardAddFragment.newInstance("param1", "param1")
                            ft.setCustomAnimations(
                                R.anim.enter_from_right,
                                R.anim.exit_to_left,
                                R.anim.enter_from_left,
                                R.anim.exit_to_right
                            )
                            ft.replace(
                                mBinding?.fragmentMainContainer?.id!!,
                                mBoardAddFragment,
                                TAG_ADD_FRAGMENT
                            )
                            ft.addToBackStack(TAG_ADD_FRAGMENT)
                            ft.commitAllowingStateLoss()

                            bottomAppBar.navigationIcon = null
                            bottomAppBar.replaceMenu(bottom_app_bar_add)
                            fab?.setImageDrawable(getDrawable(tag_plus))
                            fab?.show()

                        }
                        BottomAppBar.FAB_ALIGNMENT_MODE_END -> {
                            mBoardAddFragment.save(System.currentTimeMillis().toString())
                            fm.popBackStack(
                                TAG_ADD_FRAGMENT,
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                            )

                            bottomAppBar.navigationIcon = getDrawable(ic_baseline_menu)
                            bottomAppBar.replaceMenu(bottom_app_bar_main)
                            fab?.setImageDrawable(getDrawable(ic_baseline_create_24))
                            fab?.show()
                        }
                    }
                }
            }
        }


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
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.CAMERA
            )
            .check()
    }

    private fun init(savedInstanceState: Bundle?) {
        val fm = supportFragmentManager
        if (savedInstanceState == null) {
            val ft = fm.beginTransaction()
            mBoardListFragment = BoardListFragment.newInstance("param1", "param2")
            ft.add(mBinding?.fragmentMainContainer?.id!!, mBoardListFragment, TAG_LIST_FRAGMENT)
                .commit()
        } else {
            mBoardListFragment = fm.findFragmentByTag(TAG_LIST_FRAGMENT) as BoardListFragment
        }

        mBinding?.apply {
            btnAdd.setOnClickListener {
                mBinding?.btnAdd?.hide(addVisibilityChanged)
            }
        }

    }

    private fun BottomAppBar.toggleFab() {
        mCurrentFabAlignmentMode = fabAlignmentMode
        fabAlignmentMode = mCurrentFabAlignmentMode.xor(1)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mBinding?.btnAdd?.hide(addVisibilityChanged)
    }
}


