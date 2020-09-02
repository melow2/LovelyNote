package com.khs.lovelynote.view.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.khs.lovelynote.R
import com.khs.lovelynote.databinding.ActivityAddedViewPagerBinding
import com.khs.lovelynote.extension.Constants
import com.khs.lovelynote.extension.viewFile
import com.khs.lovelynote.model.mediastore.*
import com.khs.lovelynote.view.adapter.MediaStoreItemAdapter
import com.khs.lovelynote.view.dialog.AudioPlayDialogFragment

class MediaStoreViewPagerActivity : BaseActivity<ActivityAddedViewPagerBinding>(),MediaStoreItemAdapter.MediaStoreItemEvent  {

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
        viewPagerAdapter = MediaStoreItemAdapter(this, mList).apply {
            addListener(this@MediaStoreViewPagerActivity)
        }
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

    override fun onPlayAudio(item: MediaStoreAudio) {
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag(Constants.TAG_AUDIO_DIALOG_FRAGMENT)
        prev?.let {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val audioPlayDialog = AudioPlayDialogFragment.newInstance(item)
        audioPlayDialog.show(ft, Constants.TAG_AUDIO_DIALOG_FRAGMENT)
    }

    override fun onPlayVideo(item:MediaStoreVideo) {
        val mIntent = ExoPlayerActivity.getStartIntent(this, item)
        startActivity(mIntent)
    }

    override fun onOpenFile(item: MediaStoreFile) {
        viewFile(Uri.parse(item.contentUri),item.displayName)
    }
}