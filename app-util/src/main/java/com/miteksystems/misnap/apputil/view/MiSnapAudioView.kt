package com.miteksystems.misnap.apputil.view

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnBufferingUpdateListener
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnErrorListener
import android.media.MediaPlayer.OnPreparedListener
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.MediaController
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import com.miteksystems.misnap.apputil.MiSnapVideoSource
import com.miteksystems.misnap.apputil.R
import java.io.IOException

class MiSnapAudioView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes),
    MediaController.MediaPlayerControl {
    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private var mediaPlayer: MediaPlayer? = null
    private var source: MiSnapVideoSource? = null

    private var sessionId: Int = 0

    private var currentState = MediaPlayerState.IDLE
    private var targetState = MediaPlayerState.IDLE

    private var seekTo: Int = 0
    private var currentBufferPercentage: Int = 0

    private var canPause: Boolean = false
    private var canSeekForward: Boolean = false
    private var canSeekBackward: Boolean = false

    private var mediaController: MediaController? = null

    private var imageViewId = View.generateViewId()

    private val audioManager = context.getSystemService(AudioManager::class.java)
    private val audioFocusType = AudioManager.AUDIOFOCUS_GAIN

    private val isInPlaybackState: Boolean
        get() = null != mediaPlayer
                && currentState != MediaPlayerState.ERROR
                && currentState != MediaPlayerState.IDLE
                && currentState != MediaPlayerState.PREPARING

    private val onPreparedListener = OnPreparedListener {
        currentState = MediaPlayerState.PREPARED

        canSeekForward = true
        canSeekBackward = true
        canPause = true

        mediaController?.isEnabled = true

        if (seekTo != 0) {
            seekTo(seekTo)
        }

        if (targetState == MediaPlayerState.PlAYING) {
            start()
        }
    }

    private val onCompletionListener = OnCompletionListener {
        currentState = MediaPlayerState.PLAYBACK_COMPLETED
        targetState = MediaPlayerState.PLAYBACK_COMPLETED

        showMediaControls()
    }

    private val onErrorListener = OnErrorListener { _, _, _ ->
        currentState = MediaPlayerState.ERROR
        targetState = MediaPlayerState.ERROR

        true
    }

    private val onBufferingUpdateListener = OnBufferingUpdateListener { _, percent ->
        currentBufferPercentage = percent
    }

    init {
        AppCompatImageView(context).apply {
            id = imageViewId

            setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.misnap_audio_view_background
                )
            )
        }.also { imageView ->
            addView(
                imageView, LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT,
                    Gravity.CENTER
                )
            )
        }
    }

    fun setAudioData(data: ByteArray) {
        seekTo = 0
        source = MiSnapVideoSource(data)

        release(false)

        try {
            mediaPlayer = MediaPlayer()

            mediaPlayer?.let { player ->
                if (sessionId != 0) {
                    player.audioSessionId = sessionId
                } else {
                    sessionId = player.audioSessionId
                }

                player.setOnPreparedListener(onPreparedListener)
                player.setOnCompletionListener(onCompletionListener)
                player.setOnErrorListener(onErrorListener)
                player.setOnBufferingUpdateListener(onBufferingUpdateListener)

                source?.let {
                    player.setDataSource(it)
                }

                player.setAudioAttributes(audioAttributes)
                player.setScreenOnWhilePlaying(true)
                player.prepareAsync()
            }
        } catch (exception: IOException) {
            currentState = MediaPlayerState.ERROR
            targetState = MediaPlayerState.ERROR
        } catch (exception: IllegalArgumentException) {
            currentState = MediaPlayerState.ERROR
            targetState = MediaPlayerState.ERROR
        }
    }

    fun setMediaController(controller: MediaController) {
        mediaController = controller
        attachMediaController()
    }

    fun showMediaControls() {
        hideMediaControls()

        if (isAttachedToWindow) {
            kotlin.runCatching { mediaController?.show(0) }
        }
    }

    fun hideMediaControls() {
        mediaController?.hide()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        pause()
        hideMediaControls()
        mediaController = null
    }

    override fun start() {
        if (isInPlaybackState) {
            mediaPlayer?.start()
            showMediaControls()

            currentState = MediaPlayerState.PlAYING
        }

        targetState = MediaPlayerState.PlAYING
    }

    override fun pause() {
        if (isInPlaybackState && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()

            currentState = MediaPlayerState.PAUSED
        }

        targetState = MediaPlayerState.PAUSED
    }

    override fun getDuration(): Int =
        if (isInPlaybackState) mediaPlayer?.duration ?: -1
        else -1

    override fun getCurrentPosition(): Int =
        if (isInPlaybackState) mediaPlayer?.currentPosition ?: 0
        else 0

    override fun seekTo(pos: Int) {
        if (!SEEKBAR_ENABLED) {
            return
        }
        seekTo = if (isInPlaybackState) {
            mediaPlayer?.seekTo(pos)
            0
        } else {
            pos
        }
    }

    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    override fun getBufferPercentage(): Int =
        if (null != mediaPlayer) currentBufferPercentage
        else 0

    override fun canPause(): Boolean = canPause

    override fun canSeekBackward(): Boolean = SEEKBAR_ENABLED

    override fun canSeekForward(): Boolean = SEEKBAR_ENABLED

    override fun getAudioSessionId(): Int =
        if (sessionId == 0) {
            MediaPlayer().also {
                sessionId = it.audioSessionId
                it.release()
            }
            sessionId
        } else {
            sessionId
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && isInPlaybackState) {
            showMediaControls()
        }

        performClick()
        return super.onTouchEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        showMediaControls()

        if (isInPlaybackState && mediaController != null) {
            when (keyCode) {
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                    if (mediaPlayer?.isPlaying == true) {
                        pause()
                    } else {
                        start()
                    }

                    return true
                }
                KeyEvent.KEYCODE_MEDIA_PLAY -> {
                    if (mediaPlayer?.isPlaying != true) {
                        start()
                    }

                    return true
                }
                KeyEvent.KEYCODE_MEDIA_STOP,
                KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    if (mediaPlayer?.isPlaying == true) {
                        pause()
                    }

                    return true
                }
                else -> {
                    // Do Nothing
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun attachMediaController() {
        mediaController?.let {
            it.setMediaPlayer(this)

            val anchorView =
                if (this.parent is View) this.parent as View
                else this
            it.setAnchorView(anchorView)

            it.isEnabled = isInPlaybackState
        }
    }

    private fun release(clearTargetState: Boolean) {
        mediaPlayer?.let { player ->
            player.reset()
            player.release()

            mediaPlayer = null

            currentState = MediaPlayerState.IDLE

            if (clearTargetState) {
                targetState = MediaPlayerState.IDLE
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AudioFocusRequest.Builder(audioFocusType).build()?.let {
                    audioManager.abandonAudioFocusRequest(it)
                }
            }
        }
    }

    private companion object {
        //FIXME: MediaController sometimes throws an exception when using the seekbar.
        private const val SEEKBAR_ENABLED = false

        private enum class MediaPlayerState {
            ERROR,
            IDLE,
            PREPARING,
            PREPARED,
            PlAYING,
            PAUSED,
            PLAYBACK_COMPLETED
        }
    }
}
