package com.khs.visionboard.view.activity


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import com.khs.visionboard.R
import com.khs.visionboard.databinding.ActivityExoPlayerBinding
import com.khs.visionboard.model.mediastore.MediaStoreVideo
import kotlinx.android.synthetic.main.activity_exo_player.*


class ExoPlayerActivity : BaseActivity<ActivityExoPlayerBinding>(), Player.EventListener {

    private var video: MediaStoreVideo? = null
    private var player: SimpleExoPlayer? = null

    companion object {
        /** ExoPlayer Config */
        const val MIN_BUFFER_DURATION = 3000            // Minimum Video you want to buffer while Playing
        const val MAX_BUFFER_DURATION = 3000            // Max Video you want to buffer during PlayBack
        const val MIN_PLAYBACK_START_BUFFER = 1500      // Min Video you want to buffer before start Playing it
        const val MIN_PLAYBACK_RESUME_BUFFER = 3000     // Min video You want to buffer when user resumes video

        fun getStartIntent(context: Context?, video: MediaStoreVideo?): Intent? {
            val intent = Intent(context, ExoPlayerActivity::class.java)
            intent.putExtra("video", video)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true); // using vector icon
        supportActionBar?.hide();
        bindView(R.layout.activity_exo_player)
        if (intent.hasExtra("video")) {
            video = intent.getParcelableExtra("video")
        }
        setUp();
    }

    private fun setUp() {
        initializePlayer()
        if (video?.contentUri == null) {
            return
        }
        buildMediaSource(Uri.parse(video?.contentUri.toString()))
        mBinding?.btnExit?.setOnClickListener { finish() }
    }

    private fun initializePlayer() {
        if (player == null) {
            // 1. Create a default TrackSelector
            val loadControl: LoadControl = DefaultLoadControl(
                DefaultAllocator(true, 16),
                MIN_BUFFER_DURATION,
                MAX_BUFFER_DURATION,
                MIN_PLAYBACK_START_BUFFER,
                MIN_PLAYBACK_RESUME_BUFFER, -1, true
            )
            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
            val videoTrackSelectionFactory: TrackSelection.Factory =
                AdaptiveTrackSelection.Factory(bandwidthMeter)
            val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
            // 2. Create the player
            player = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(this), trackSelector,
                loadControl
            )
            videoFullScreenPlayer.player = player
        }
    }

    private fun buildMediaSource(mUri: Uri) {
        // Measures bandwidth during playback. Can be null if not required.
        val bandwidthMeter = DefaultBandwidthMeter()
        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter
        )
        // This is the MediaSource representing the media to be played.
        val videoSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mUri)
        // Prepare the player with the source.
        player?.prepare(videoSource)
        player?.playWhenReady = true
        player?.addListener(this)
    }

    private fun releasePlayer() {
        player?.let{
            player!!.release()
            player = null
        }
    }

    private fun pausePlayer() {
        player?.let{
            player!!.playWhenReady = false
            player!!.playbackState
        }
    }

    private fun resumePlayer() {
        player?.let{
            player!!.playWhenReady = true
            player!!.playbackState
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer();
    }

    override fun onRestart() {
        super.onRestart()
        resumePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }


    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_BUFFERING -> spinnerVideoDetails.visibility = View.VISIBLE
            Player.STATE_ENDED -> {
            }
            Player.STATE_IDLE -> {
            }
            Player.STATE_READY -> spinnerVideoDetails.visibility = View.GONE
            else -> {
            }
        }
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {}
    override fun onTracksChanged(
        trackGroups: TrackGroupArray?,
        trackSelections: TrackSelectionArray?
    ) {
    }

    override fun onLoadingChanged(isLoading: Boolean) {}
    override fun onRepeatModeChanged(repeatMode: Int) {}
    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
    override fun onPlayerError(error: ExoPlaybackException?) {}
    override fun onPositionDiscontinuity(reason: Int) {}
    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}
    override fun onSeekProcessed() {}
}