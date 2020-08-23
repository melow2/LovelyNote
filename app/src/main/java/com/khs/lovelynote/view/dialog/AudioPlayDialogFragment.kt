package com.khs.lovelynote.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.khs.lovelynote.R
import com.khs.lovelynote.databinding.DialogAudioPlayBinding
import com.khs.lovelynote.extension.parseTime
import com.khs.lovelynote.model.mediastore.MediaStoreAudio
import kotlinx.android.synthetic.main.dialog_audio_play.*

class AudioPlayDialogFragment : DialogFragment() {

    private var mAudio: MediaStoreAudio? = null
    private lateinit var mPlayer: MediaPlayer
    private lateinit var mBinding: DialogAudioPlayBinding
    private var oTime: Int = 0
    private var sTime = 0
    private var eTime = 0
    private var fTime = 5000
    private var bTime = 5000
    private val handler = Handler(Looper.getMainLooper())

    private val updateSongTime: Runnable = object : Runnable {
        override fun run() {
            sTime = mPlayer.currentPosition
            mBinding.tvStartTime.text = sTime.toLong().parseTime()
            mBinding.audioSeekBar.progress = sTime
            handler.postDelayed(this, 100)
        }
    }

    companion object {
        private const val ITEM_AUDIO = "ITEM_AUDIO"
        private const val DIALOG_WIDTH = 700
        private const val DIALOG_HEIGHT = 700
        fun newInstance(item: MediaStoreAudio?): AudioPlayDialogFragment {
            return AudioPlayDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ITEM_AUDIO, item)
                }
            }
        }
    }

    interface EventListener {
        fun onPlayClick()
        fun onPauseClick()
        fun onBackwardClick()
        fun onFrontwardClick()
    }

    override fun onStart() {
        super.onStart()
        if (dialog == null || dialog?.window == null) return
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width,height)
        dialog?.window?.setGravity(Gravity.CENTER)
    } // #3


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mAudio = it.getParcelable(ITEM_AUDIO)
        }
        mPlayer = MediaPlayer.create(activity, mAudio?.contentUri)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle);
    } // #1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_audio_play, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 모서리 둥글게.
        mBinding.audio = mAudio
        mBinding.tvStartTime.text = 0L.parseTime()
        return mBinding.root
    } // #2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listener = MediaPlayerListener(mBinding)
        btn_back_ward.setOnClickListener { listener.onBackwardClick() }
        btn_front_ward.setOnClickListener { listener.onFrontwardClick() }
        btn_play.setOnClickListener { listener.onPlayClick() }
        btn_pause.setOnClickListener { listener.onPauseClick() }
        audio_seek_bar.setOnSeekBarChangeListener(listener)
    }

    override fun onDetach() {
        super.onDetach()
        mPlayer.pause()
    }

    override fun onPause() {
        super.onPause()
        mPlayer.pause()
    }

    inner class MediaPlayerListener(private val mBinding: DialogAudioPlayBinding) : EventListener,
        SeekBar.OnSeekBarChangeListener {

        private var isPlaying = false

        override fun onPlayClick() {
            mBinding.run {
                mediaPlayerPlayAction()
                eTime = mPlayer.duration;
                sTime = mPlayer.currentPosition;
                if (oTime == 0) {
                    audioSeekBar.max = eTime;
                    oTime = 1;
                }
                tvStartTime.text = sTime.toLong().parseTime()
                audioSeekBar.progress = sTime;
                handler.postDelayed(updateSongTime, 100);
            }
        }

        override fun onPauseClick() {
            mBinding.run {
                mediaPlayerPauseAction()
            }
        }

        override fun onBackwardClick() {
            if ((sTime - bTime) > 0) {
                sTime -= bTime;
                mPlayer.seekTo(sTime);
            }
        }

        override fun onFrontwardClick() {
            if ((sTime + fTime) <= eTime) {
                sTime += fTime;
                mPlayer.seekTo(sTime);
            }
        }

        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            if(isPlaying) {
                mediaPlayerPauseAction()
            }
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            if(!isPlaying && sTime!=0) {
                seekBar?.progress?.run {
                    mPlayer.seekTo(this)
                    mPlayer.start()
                }
            }
        }

        private fun mediaPlayerPauseAction() {
            if(isPlaying) {
                mPlayer.pause()
                mBinding.btnPause.fadeOutAnimation()
                mBinding.btnPlay.fadeInAnimation()
                isPlaying = false
            }
        }

        private fun mediaPlayerPlayAction(){
            if(!isPlaying) {
                mPlayer.start()
                mBinding.btnPause.fadeInAnimation()
                mBinding.btnPlay.fadeOutAnimation()
                isPlaying = true
            }
        }
    }
}