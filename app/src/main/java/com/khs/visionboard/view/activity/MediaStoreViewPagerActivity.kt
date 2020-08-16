package com.khs.visionboard.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.khs.visionboard.R
import com.khs.visionboard.databinding.ActivityAddedViewPagerBinding
import com.khs.visionboard.model.mediastore.SelectedItem
import com.khs.visionboard.model.mediastore.SelectedMediaStoreItem
import com.khs.visionboard.view.adapter.MediaStoreItemAdapter

class MediaStoreViewPagerActivity : BaseActivity<ActivityAddedViewPagerBinding>() {

    private var mList:ArrayList<SelectedItem> = arrayListOf()
    private var mPosition: Int = 0
    private var viewPagerAdapter:MediaStoreItemAdapter?=null

    companion object{
        const val MEDIA_ITEM_LIST = "MEDIA_ITEM_LIST"
        const val MEDIA_ITEM_POSTION = "MEDIA_ITEM_POSTION"
        fun getStartIntent(
            context: Context?, list: ArrayList<SelectedItem>,
            position: Int?): Intent? {
            val intent = Intent(context, MediaStoreViewPagerActivity::class.java)
            intent.putParcelableArrayListExtra(MEDIA_ITEM_LIST,list)
            intent.putExtra(MEDIA_ITEM_POSTION,position)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView(R.layout.activity_added_view_pager)
        if(intent.hasExtra(MEDIA_ITEM_POSTION)) mPosition = intent.getIntExtra(MEDIA_ITEM_POSTION,0)
        if(intent.hasExtra(MEDIA_ITEM_LIST)) mList = intent.getParcelableArrayListExtra<SelectedItem>(MEDIA_ITEM_LIST) as ArrayList<SelectedItem>
        viewPagerAdapter = MediaStoreItemAdapter(this, mList)
        mBinding?.run {
            mediaViwpager.adapter = viewPagerAdapter
            mediaViwpager.currentItem = mPosition
            mediaViwpager.registerOnPageChangeCallback(ViewPagerChangeCallback())
        }
    }

    inner class ViewPagerChangeCallback:ViewPager2.OnPageChangeCallback(){
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
        }
    }

}