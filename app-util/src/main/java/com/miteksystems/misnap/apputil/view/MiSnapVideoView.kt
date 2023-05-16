package com.miteksystems.misnap.apputil.view

import android.app.AlertDialog
import android.content.Context
import android.media.*
import android.media.MediaPlayer.*
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.Pair
import android.view.*
import android.widget.MediaController
import android.widget.MediaController.MediaPlayerControl
import com.miteksystems.misnap.apputil.MiSnapVideoSource
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * Displays a video file.  The VideoView class
 * can load images from various sources (such as resources or content
 * providers), takes care of computing its measurement from the video so that
 * it can be used in any layout manager, and provides various display options
 * such as scaling and tinting.
 *
 *
 *
 * By default, VideoView requests audio focus with [AudioManager.AUDIOFOCUS_GAIN]. Use
 * [.setAudioFocusRequest] to change this behavior.
 *
 *
 * The default [AudioAttributes] used during playback have a usage of
 * [AudioAttributes.USAGE_MEDIA] and a content type of
 * [AudioAttributes.CONTENT_TYPE_MOVIE], use [.setAudioAttributes] to
 * modify them.
 */
class MiSnapVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) :
    SurfaceView(context, attrs, defStyleAttr, defStyleRes), MediaPlayerControl {
    private val mPendingSubtitleTracks = Vector<Pair<InputStream, MediaFormat>>()

    // settable by the client
    private var mUri: Uri? = null
    private var mHeaders: Map<String, String>? = null
    private var mVideoSource: MiSnapVideoSource? = null

    // mCurrentState is a VideoView object's current state.
    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the VideoView object's current state,
    // calling pause() intends to bring the object to a target state
    // of MediaPlayerState.PAUSED.
    private var mCurrentState = MediaPlayerState.IDLE
    private var mTargetState = MediaPlayerState.IDLE

    // All the stuff we need for playing and showing a video
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var mAudioSession = 0
    private var mVideoWidth = 0
    private var mVideoHeight = 0
    private var mSizeChangedListener =
        OnVideoSizeChangedListener { mediaPlayer, _, _ ->
            mVideoWidth = mediaPlayer.videoWidth
            mVideoHeight = mediaPlayer.videoHeight
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                holder.setFixedSize(mVideoWidth, mVideoHeight)
                requestLayout()
            }
        }
    private var mSurfaceWidth = 0
    private var mSurfaceHeight = 0
    private var mMediaController: MediaController? = null
    private var mOnCompletionListener: OnCompletionListener? = null
    private var mOnPreparedListener: OnPreparedListener? = null
    private var mCurrentBufferPercentage = 0
    private var mOnErrorListener: OnErrorListener? = null
    private var mOnInfoListener: OnInfoListener? = null
    private var mSeekWhenPrepared = 0 // recording the seek position while preparing
    private var mCanPause = false
    private var mCanSeekBack = false
    private var mCanSeekForward = false
    private var mPreparedListener = OnPreparedListener { mediaPlayer ->
        mCurrentState = MediaPlayerState.PREPARED
        // Get the capabilities of the player for this stream
        mCanSeekForward = true
        mCanSeekBack = mCanSeekForward
        mCanPause = mCanSeekBack
        mOnPreparedListener?.onPrepared(mMediaPlayer)
        mMediaController?.isEnabled = true
        mVideoWidth = mediaPlayer.videoWidth
        mVideoHeight = mediaPlayer.videoHeight
        val seekToPosition =
            mSeekWhenPrepared // mSeekWhenPrepared may be changed after seekTo() call
        if (seekToPosition != 0) {
            seekTo(seekToPosition)
        }
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            //Log.i("@@@@", "video size: " + mVideoWidth +"/"+ mVideoHeight);
            holder.setFixedSize(mVideoWidth, mVideoHeight)
            if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                // We didn't actually change the size (it was already at the size
                // we need), so we won't get a "surface changed" callback, so
                // start the video here instead of in the callback.
                if (mTargetState == MediaPlayerState.PLAYING) {
                    start()
                    showMediaControls()
                } else if (!isPlaying &&
                    (seekToPosition != 0 || currentPosition > 0)
                ) {
                    // Show the media controls when we're paused into a video and make 'em stick.
                    showMediaControls(0)
                }
            }
        } else {
            // We don't know the video size yet, but should start anyway.
            // The video size might be reported to us later.
            if (mTargetState == MediaPlayerState.PLAYING) {
                start()
            }
        }
    }
    private val mAudioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mAudioFocusType = AudioManager.AUDIOFOCUS_GAIN // legacy focus gain
    private var mAudioAttributes: AudioAttributes
    private val mCompletionListener = OnCompletionListener {
        mCurrentState = MediaPlayerState.PLAYBACK_COMPLETED
        mTargetState = MediaPlayerState.PLAYBACK_COMPLETED
        mMediaController?.hide()
        mOnCompletionListener?.onCompletion(mMediaPlayer)
    }
    private val mInfoListener =
        OnInfoListener { mp, arg1, arg2 ->
            mOnInfoListener?.onInfo(mp, arg1, arg2)
            true
        }
    private val mErrorListener =
        OnErrorListener { _, framework_err, impl_err ->
            Log.d(TAG, "Error: $framework_err,$impl_err")
            mCurrentState = MediaPlayerState.ERROR
            mTargetState = MediaPlayerState.ERROR
            mMediaController?.hide()

            /* If an error handler has been supplied, use it and finish. */
            mOnErrorListener?.let {
                it.onError(mMediaPlayer, framework_err, impl_err)
                return@OnErrorListener true
            }

            /* Otherwise, pop up an error dialog so the user knows that
             * something bad has happened. Only try and pop up the dialog
             * if we're attached to a window. When we're going away and no
             * longer have a window, don't bother showing the user an error.
             */
            windowToken?.let {
                val message: String =
                    if (framework_err == MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
                        "Error Progressive Playback"
                    } else {
                        "Unknown Error"
                    }
                AlertDialog.Builder(getContext())
                    .setMessage(message)
                    .setPositiveButton(
                        "Exit"
                    ) { _, _ ->
                        /* If we get here, there is no onError listener, so
                         * at least inform them that the video is over.
                         */
                        mOnCompletionListener?.onCompletion(mMediaPlayer)

                    }
                    .setCancelable(false)
                    .show()
            }
            true
        }

    private val mBufferingUpdateListener =
        OnBufferingUpdateListener { _, percent -> mCurrentBufferPercentage = percent }

    private var mSHCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        override fun surfaceChanged(
            holder: SurfaceHolder, format: Int,
            width: Int, height: Int,
        ) {
            mSurfaceWidth = width
            mSurfaceHeight = height
            val isValidState = mTargetState == MediaPlayerState.PLAYING
            val hasValidSize = mVideoWidth == width && mVideoHeight == height
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared)
                }
                start()
            }
        }

        override fun surfaceCreated(holder: SurfaceHolder) {
            mSurfaceHolder = holder
            openVideo()
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            // after we return from this we can't use the surface any more
            mSurfaceHolder = null
            mMediaController?.hide()
            release(true)
            mMediaController?.setOnClickListener(null)
            mMediaController = null

        }
    }

    init {
        mAudioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE).build()
        holder.addCallback(mSHCallback)
        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
        mCurrentState = MediaPlayerState.IDLE
        mTargetState = MediaPlayerState.IDLE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //Log.i("@@@@", "onMeasure(" + MeasureSpec.toString(widthMeasureSpec) + ", "
        //        + MeasureSpec.toString(heightMeasureSpec) + ")");
        var width = getDefaultSize(mVideoWidth, widthMeasureSpec)
        var height = getDefaultSize(mVideoHeight, heightMeasureSpec)
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
            val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
            val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
            val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                // the size is fixed
                width = widthSpecSize
                height = heightSpecSize
                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize
                height = width * mVideoHeight / mVideoWidth
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize
                width = height * mVideoWidth / mVideoHeight
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize
                }
            } else {
                // neither the width nor the height are fixed, try to use actual video size
                width = mVideoWidth
                height = mVideoHeight
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize
                    width = height * mVideoWidth / mVideoHeight
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize
                    height = width * mVideoHeight / mVideoWidth
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }
        setMeasuredDimension(width, height)
    }

    override fun getAccessibilityClassName(): CharSequence {
        return MiSnapVideoView::class.java.name
    }

    fun resolveAdjustedSize(desiredSize: Int, measureSpec: Int): Int {
        return getDefaultSize(desiredSize, measureSpec)
    }

    fun setVideoData(data: ByteArray?) {
        mSeekWhenPrepared = 0
        mVideoSource = MiSnapVideoSource(data!!)
        openVideo()
        requestLayout()
        invalidate()
    }

    /**
     * Sets video path.
     *
     * @param path the path of the video.
     */
    fun setVideoPath(path: String?) {
        setVideoURI(Uri.parse(path))
    }

    /**
     * Sets video URI.
     *
     * @param uri the URI of the video.
     */
    private fun setVideoURI(uri: Uri?) {
        setVideoURI(uri, null)
    }

    /**
     * Sets video URI using specific headers.
     *
     * @param uri     the URI of the video.
     * @param headers the headers for the URI request.
     * Note that the cross domain redirection is allowed by default, but that can be
     * changed with key/value pairs through the headers parameter with
     * "android-allow-cross-domain-redirect" as the key and "0" or "1" as the value
     * to disallow or allow cross domain redirection.
     */
    private fun setVideoURI(uri: Uri?, headers: Map<String, String>?) {
        mUri = uri
        mHeaders = headers
        mSeekWhenPrepared = 0
        openVideo()
        requestLayout()
        invalidate()
    }

    /**
     * Sets which type of audio focus will be requested during the playback, or configures playback
     * to not request audio focus. Valid values for focus requests are
     * [AudioManager.AUDIOFOCUS_GAIN], [AudioManager.AUDIOFOCUS_GAIN_TRANSIENT],
     * [AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK], and
     * [AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE]. Or use
     * [AudioManager.AUDIOFOCUS_NONE] to express that audio focus should not be
     * requested when playback starts. You can for instance use this when playing a silent animation
     * through this class, and you don't want to affect other audio applications playing in the
     * background.
     * @param focusGain the type of audio focus gain that will be requested, or
     * [AudioManager.AUDIOFOCUS_NONE] to disable the use audio focus during playback.
     */
    fun setAudioFocusRequest(focusGain: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            require(focusGain == AudioManager.AUDIOFOCUS_NONE) { "Illegal audio focus type $focusGain" }
        }
        require(!(focusGain != AudioManager.AUDIOFOCUS_GAIN && focusGain != AudioManager.AUDIOFOCUS_GAIN_TRANSIENT && focusGain != AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK && focusGain != AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)) { "Illegal audio focus type $focusGain" }
        mAudioFocusType = focusGain
    }


    /**
     * Sets the [AudioAttributes] to be used during the playback of the video.
     * @param attributes non-null `AudioAttributes`.
     */
    fun setAudioAttributes(attributes: AudioAttributes) {
        mAudioAttributes = attributes
    }

    fun stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
            mCurrentState = MediaPlayerState.IDLE
            mTargetState = MediaPlayerState.IDLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AudioFocusRequest.Builder(mAudioFocusType).build()?.let {
                    mAudioManager.abandonAudioFocusRequest(it)
                }
            }
        }
    }

    private fun openVideo() {
        if (mUri == null && mVideoSource == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return
        }
        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false)
//        //TODO: what even is this API
//        if (mAudioFocusType != AudioManager.AUDIOFOCUS_NONE) {
//           // TODO: this should have a focus listener
//            mAudioManager.requestAudioFocus(null, mAudioAttributes, mAudioFocusType, 0 /*flags*/);
//        }
        try {
            mMediaPlayer = MediaPlayer()
            // TODO: create SubtitleController in MediaPlayer, but we need
            //  a context for the subtitle renderers
            val context = context
            if (mAudioSession != 0) {
                mMediaPlayer!!.audioSessionId = mAudioSession
            } else {
                mAudioSession = mMediaPlayer!!.audioSessionId
            }
            mMediaPlayer!!.setOnPreparedListener(mPreparedListener)
            mMediaPlayer!!.setOnVideoSizeChangedListener(mSizeChangedListener)
            mMediaPlayer!!.setOnCompletionListener(mCompletionListener)
            mMediaPlayer!!.setOnErrorListener(mErrorListener)
            mMediaPlayer!!.setOnInfoListener(mInfoListener)
            mMediaPlayer!!.setOnBufferingUpdateListener(mBufferingUpdateListener)
            mCurrentBufferPercentage = 0

            mUri?.let { mMediaPlayer!!.setDataSource(context, it, mHeaders) }
                ?: mMediaPlayer!!.setDataSource(mVideoSource)

            mMediaPlayer!!.setDisplay(mSurfaceHolder)
            mMediaPlayer!!.setAudioAttributes(mAudioAttributes)
            mMediaPlayer!!.setScreenOnWhilePlaying(true)
            mMediaPlayer!!.prepareAsync()
            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = MediaPlayerState.PREPARING
            attachMediaController()
        } catch (exception: IOException) {
            Log.w(
                TAG,
                "Unable to open content: $mUri", exception
            )
            mCurrentState = MediaPlayerState.ERROR
            mTargetState = MediaPlayerState.ERROR
            mErrorListener.onError(mMediaPlayer, MEDIA_ERROR_UNKNOWN, 0)
            return
        } catch (ex: IllegalArgumentException) {
            Log.w(
                TAG,
                "Unable to open content: $mUri", ex
            )
            mCurrentState = MediaPlayerState.ERROR
            mTargetState = MediaPlayerState.ERROR
            mErrorListener.onError(mMediaPlayer, MEDIA_ERROR_UNKNOWN, 0)
            return
        } finally {
            mPendingSubtitleTracks.clear()
        }
    }

    fun setMediaController(controller: MediaController?) {
        mMediaController?.hide()
        mMediaController = controller
        attachMediaController()
    }

    private fun attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController!!.setMediaPlayer(this)
            val anchorView = if (this.parent is View) this.parent as View else this
            mMediaController!!.setAnchorView(anchorView)
            mMediaController!!.isEnabled = isInPlaybackState
        }
    }

    /**
     * Register a callback to be invoked when the media file
     * is loaded and ready to go.
     *
     * @param listener The callback that will be run
     */
    fun setOnPreparedListener(listener: OnPreparedListener?) {
        mOnPreparedListener = listener
    }

    /**
     * Register a callback to be invoked when the end of a media file
     * has been reached during playback.
     *
     * @param listener The callback that will be run
     */
    fun setOnCompletionListener(listener: OnCompletionListener?) {
        mOnCompletionListener = listener
    }

    /**
     * Register a callback to be invoked when an error occurs
     * during playback or setup.  If no listener is specified,
     * or if the listener returned false, VideoView will inform
     * the user of any errors.
     *
     * @param listener The callback that will be run
     */
    fun setOnErrorListener(listener: OnErrorListener?) {
        mOnErrorListener = listener
    }

    /**
     * Register a callback to be invoked when an informational event
     * occurs during playback or setup.
     *
     * @param l The callback that will be run
     */
    fun setOnInfoListener(listener: OnInfoListener?) {
        mOnInfoListener = listener
    }

    /*
     * release the media player in any state
     */
    private fun release(clearTargetState: Boolean) {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.reset()
            mMediaPlayer!!.release()
            mMediaPlayer = null
            mPendingSubtitleTracks.clear()
            mCurrentState = MediaPlayerState.IDLE
            if (clearTargetState) {
                mTargetState = MediaPlayerState.IDLE
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AudioFocusRequest.Builder(mAudioFocusType).build()?.let {
                    mAudioManager.abandonAudioFocusRequest(it)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && isInPlaybackState && mMediaController != null) {
            toggleMediaControlsVisibility()
        }

        performClick()
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTrackballEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && isInPlaybackState && mMediaController != null) {
            toggleMediaControlsVisibility()
        }
        return super.onTrackballEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val isKeyCodeSupported =
            keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_VOLUME_MUTE && keyCode != KeyEvent.KEYCODE_MENU && keyCode != KeyEvent.KEYCODE_CALL && keyCode != KeyEvent.KEYCODE_ENDCALL

        if (isInPlaybackState && isKeyCodeSupported && mMediaController != null) {
            when (keyCode) {
                KeyEvent.KEYCODE_HEADSETHOOK,
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                -> {
                    if (mMediaPlayer!!.isPlaying) {
                        pause()
                        showMediaControls()
                    } else {
                        start()
                        mMediaController!!.hide()
                    }
                    return true
                }

                KeyEvent.KEYCODE_MEDIA_PLAY -> {
                    if (!mMediaPlayer!!.isPlaying) {
                        start()
                        mMediaController!!.hide()
                    }
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_STOP,
                KeyEvent.KEYCODE_MEDIA_PAUSE,
                -> {
                    if (mMediaPlayer!!.isPlaying) {
                        pause()
                        showMediaControls()
                    }
                    return true
                }
                else -> {
                    toggleMediaControlsVisibility()
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    // 3000 is the default timeout in MediaController.sDefaultTimeout
    private fun showMediaControls(timeout: Int = 3000) {
        kotlin.runCatching {
            mMediaController?.show(timeout)
        }
    }

    private fun toggleMediaControlsVisibility() {
        if (mMediaController!!.isShowing) {
            mMediaController!!.hide()
        } else {
            showMediaControls()
        }
    }

    override fun start() {
        if (isInPlaybackState) {
            mMediaPlayer!!.start()
            showMediaControls()
            mCurrentState = MediaPlayerState.PLAYING
        }
        mTargetState = MediaPlayerState.PLAYING
    }

    override fun pause() {
        if (isInPlaybackState) {
            if (mMediaPlayer!!.isPlaying) {
                mMediaPlayer!!.pause()
                mCurrentState = MediaPlayerState.PAUSED
            }
        }
        mTargetState = MediaPlayerState.PAUSED
    }

    fun suspend() {
        release(false)
    }

    fun resume() {
        openVideo()
    }

    override fun getDuration(): Int {
        return if (isInPlaybackState) {
            mMediaPlayer!!.duration
        } else -1
    }

    override fun getCurrentPosition(): Int {
        return if (isInPlaybackState) {
            mMediaPlayer!!.currentPosition
        } else 0
    }

    override fun seekTo(msec: Int) {
        if (!SEEKBAR_ENABLED) {
            return
        }

        mSeekWhenPrepared = if (isInPlaybackState) {
            mMediaPlayer!!.seekTo(msec)
            0
        } else {
            msec
        }
    }

    override fun isPlaying(): Boolean {
        return isInPlaybackState && mMediaPlayer!!.isPlaying
    }

    override fun getBufferPercentage(): Int {
        return if (mMediaPlayer != null) {
            mCurrentBufferPercentage
        } else 0
    }

    private val isInPlaybackState: Boolean
        get() = mMediaPlayer != null && mCurrentState != MediaPlayerState.ERROR && mCurrentState != MediaPlayerState.IDLE && mCurrentState != MediaPlayerState.PREPARING

    override fun canPause(): Boolean {
        return mCanPause
    }

    override fun canSeekBackward(): Boolean {
        return SEEKBAR_ENABLED
    }

    override fun canSeekForward(): Boolean {
        return SEEKBAR_ENABLED
    }

    override fun getAudioSessionId(): Int {
        if (mAudioSession == 0) {
            val foo = MediaPlayer()
            mAudioSession = foo.audioSessionId
            foo.release()
        }
        return mAudioSession
    }

    private companion object {
        private const val TAG = "MiSnapVideoView"

        //FIXME: MediaController sometimes throws an exception when using the seekbar.
        private const val SEEKBAR_ENABLED = false

        // all possible internal states
        private enum class MediaPlayerState {
            ERROR,
            IDLE,
            PREPARING,
            PREPARED,
            PLAYING,
            PAUSED,
            PLAYBACK_COMPLETED
        }
    }
}
