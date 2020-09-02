package com.khs.lovelynote.view.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.khs.lovelynote.R
import com.khs.lovelynote.databinding.ActivityMainBinding
import com.khs.lovelynote.extension.Constants.TAG_ADD_FRAGMENT
import com.khs.lovelynote.extension.Constants.TAG_DETAIL_FRAGMENT
import com.khs.lovelynote.extension.Constants.TAG_LIST_FRAGMENT
import com.khs.lovelynote.view.fragment.BoardAddFragment
import com.khs.lovelynote.view.fragment.BoardDetailFragment
import com.khs.lovelynote.view.fragment.BoardListFragment


/*
* todo 권한 splash activity로 옮겨야 함.
* */
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var mBoardListFragment: BoardListFragment
    private lateinit var mBoardAddFragment: BoardAddFragment
    private var mCurrentFabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
    private var mHandler: Handler = Handler(Looper.getMainLooper())

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_main, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mBoardListFragment.listAdapter?.filter(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView(R.layout.activity_main)

        setToolBar(
            mBinding?.toolbar as Toolbar,
            false,
            getString(applicationInfo.labelRes),
            findViewById(R.id.tv_title)
        )

        TedPermission.with(this)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    init(savedInstanceState)
                    setNavigation()
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
            ft.apply {
                add(mBinding?.fragmentMainContainer?.id!!, mBoardListFragment, TAG_LIST_FRAGMENT)
                commit()
            }
        } else {
            mBoardListFragment = fm.findFragmentByTag(TAG_LIST_FRAGMENT) as BoardListFragment
        }

        mBinding?.btnAdd?.apply {
            setOnClickListener {
                val fm = supportFragmentManager
                val ft = fm.beginTransaction()
                val currentFragment = fm.findFragmentById(R.id.fragment_main_container)
                mBinding?.apply {
                    toggleFab()
                    when (mCurrentFabAlignmentMode) {
                        BottomAppBar.FAB_ALIGNMENT_MODE_CENTER -> {
                            mBoardAddFragment = BoardAddFragment.newInstance("param1", "param1")
                            ft.apply {
                                replace(
                                    mBinding?.fragmentMainContainer?.id!!,
                                    mBoardAddFragment,
                                    TAG_ADD_FRAGMENT
                                )
                                addToBackStack(TAG_ADD_FRAGMENT)
                                setReorderingAllowed(true) // 트랜지션 최적화
                                commitAllowingStateLoss()
                            }
                            // bottomAppBar.navigationIcon = null
                            // bottomAppBar.replaceMenu(bottom_app_bar_add)
                            mHandler.post {
                                changeToolBar("Write", R.drawable.ic_baseline_arrow_back_24)
                                changeFabImage(getDrawable(R.drawable.ic_baseline_create_24))
                            }
                        }
                        BottomAppBar.FAB_ALIGNMENT_MODE_END -> {
                            when (currentFragment) {
                                is BoardAddFragment ->{
                                    mBoardAddFragment.save(System.currentTimeMillis().toString())
                                    fm.popBackStack(
                                        TAG_ADD_FRAGMENT,
                                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                                    )
                                    // bottomAppBar.navigationIcon = getDrawable(ic_baseline_menu)
                                    // bottomAppBar.replaceMenu(bottom_app_bar_main)
                                    mHandler.post {
                                        changeToolBar(
                                            getString(applicationInfo.labelRes), R.drawable.ic_baseline_menu
                                        )
                                        changeFabImage(getDrawable(R.drawable.tag_plus))
                                    }
                                }
                                is BoardDetailFragment ->{
                                    currentFragment.update()
                                    fm.popBackStack(
                                        TAG_DETAIL_FRAGMENT,
                                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                                    )
                                    // bottomAppBar.navigationIcon = getDrawable(ic_baseline_menu)
                                    // bottomAppBar.replaceMenu(bottom_app_bar_main)
                                    mHandler.post {
                                        changeToolBar(getString(applicationInfo.labelRes), R.drawable.ic_baseline_menu)
                                        changeFabImage(getDrawable(R.drawable.tag_plus))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setNavigation() {
        mToolbar.setNavigationOnClickListener {
            val fragment: Fragment? =
                supportFragmentManager.findFragmentById(R.id.fragment_main_container)
            if (fragment is BoardListFragment) {
                mBinding?.drawerLayout?.openDrawer(GravityCompat.START)
            } else {
                onBackPressed()
            }
        }

        mBinding?.navigationView?.setNavigationItemSelectedListener { menuItem ->
            // menuItem.isChecked = true
            when (menuItem.itemId) {
                R.id.navigation_item_version -> {
                    showToast(
                        context = MainActivity@ this,
                        msg = "Version: " + packageManager.getPackageInfo(
                            packageName,
                            0
                        ).versionName + "v"
                    )
                }

            }
            mBinding?.drawerLayout?.closeDrawers()
            true
        }
    }

    fun toggleFab() {
        mBinding?.bottomAppBar?.apply {
            mCurrentFabAlignmentMode = fabAlignmentMode
            fabAlignmentMode = mCurrentFabAlignmentMode.xor(1)
        }
    }

    fun changeFabImage(image: Drawable?) {
        mBinding?.btnAdd?.setImageDrawable(image)
    }
}


