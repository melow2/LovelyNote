package com.khs.visionboard.view.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.khs.visionboard.R
import com.khs.visionboard.databinding.ActivityAddBoardBinding
import com.khs.visionboard.extension.Constants.TAG_ADD_FRAGMENT
import com.khs.visionboard.view.fragment.AddBoardFragment

class AddBoardActivity : BaseActivity<ActivityAddBoardBinding>() {

    private lateinit var addBoardFragment: AddBoardFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView(R.layout.activity_add_board)
        init(savedInstanceState)
        setToolbar(mBinding?.toolbar as Toolbar, true, "AddVision", findViewById(R.id.tv_title))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun init(savedInstanceState: Bundle?) {
        val fm = supportFragmentManager
        if (savedInstanceState == null) {
            val ft = fm.beginTransaction()
            addBoardFragment = AddBoardFragment.newInstance("param1", "param2")
            ft.add(mBinding?.fragmentAddContainer?.id!!, addBoardFragment, TAG_ADD_FRAGMENT)
                .commit()
        } else {
            addBoardFragment = fm.findFragmentByTag(TAG_ADD_FRAGMENT) as AddBoardFragment
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}