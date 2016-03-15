package com.bigstark.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bigstark.video.BigstarVideoView;
import com.bigstark.video.ErrorReason;
import com.bigstark.video.OnPlayStateChangedListener;
import com.bigstark.video.OnPlaybackEventListener;

/**
 * Created by bigstark on 16. 1. 7..
 */
public class BigstarPlayerView extends RelativeLayout {
    private static final String TAG = BigstarPlayerView.class.getSimpleName();

    private static final String FORMAT_TIME = "%d:%02d";

    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;
    private final float SCREEN_RATIO;


    private BigstarVideoView videoView;
    private float ratioVideo;
    private float ratioScreenToVideo;
    private int layoutVideoWidth;
    private int layoutVideoHeight;


    private View layoutVideo;
    private View layoutController;


    private ImageButton btnPlayPause;
    private ImageButton btnFullscreen;
    private TextView tvCurrentPosition;
    private TextView tvDuration;
    private SeekBar seekBar;

    private int icPlay = R.drawable.ic_play;
    private int icPause = R.drawable.ic_pause;
    private int icFullscreenSwitch = R.drawable.ic_fullscreen_switch;
    private int icFullscreenCancel = R.drawable.ic_fullscreen_cancel;
    private int seekbarProgress = R.drawable.seekbar_progress;
    private int seekbarThumb = R.drawable.seekbar_thumb;


    private boolean isFull = false;


    public BigstarPlayerView(Context context) {
        this(context, null);
    }

    public BigstarPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigstarPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        SCREEN_WIDTH = metrics.widthPixels;
        SCREEN_HEIGHT = metrics.heightPixels;
        SCREEN_RATIO = (float) SCREEN_WIDTH / SCREEN_HEIGHT;

        initResources(context, attrs, defStyleAttr);
        initViews(context);
    }


    // init image resources.
    private void initResources(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) {
            return;
        }

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BigstarPlayerView, defStyleAttr, 0);

        icFullscreenSwitch = ta.getResourceId(R.styleable.BigstarPlayerView_toFullScreenIcon, icFullscreenSwitch);
        icFullscreenCancel = ta.getResourceId(R.styleable.BigstarPlayerView_toBaseScreenIcon, icFullscreenCancel);
        icPlay = ta.getResourceId(R.styleable.BigstarPlayerView_playIcon, icPlay);
        icPause = ta.getResourceId(R.styleable.BigstarPlayerView_pauseIcon, icPause);
        seekbarProgress = ta.getResourceId(R.styleable.BigstarPlayerView_seekBarProgressDrawable, seekbarProgress);
        seekbarThumb = ta.getResourceId(R.styleable.BigstarPlayerView_seekBarThumb, seekbarThumb);

        ta.recycle();
    }


    // init views, listener and set listener on view.
    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.bigstar_player_view, this);

        videoView = (BigstarVideoView) findViewById(R.id.bvv_video);

        layoutVideo = findViewById(R.id.layout_video);
        layoutController = findViewById(R.id.layout_controller);

        btnPlayPause = (ImageButton) findViewById(R.id.btn_play_pause);
        btnFullscreen = (ImageButton) findViewById(R.id.btn_fullscreen);
        tvCurrentPosition = (TextView) findViewById(R.id.tv_current_position);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        seekBar = (SeekBar) findViewById(R.id.sb_video);

        videoView.setOnPlaybackEventListener(new PlaybackEventListenerImpl());
        videoView.setOnPlayStateChangedListener(new PlayStateChangedListenerImpl());

        ComponentClickListenerImpl componentClickListener = new ComponentClickListenerImpl();
        layoutVideo.setOnClickListener(componentClickListener);
        btnPlayPause.setOnClickListener(componentClickListener);
        btnFullscreen.setOnClickListener(componentClickListener);

        seekBar.setOnSeekBarChangeListener(new SeekBarChangedListenerImpl());
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.v(TAG, "layoutVideoWidth : " + layoutVideo.getWidth());
        Log.v(TAG, "layoutVideoHeight : " + layoutVideo.getHeight());
    }

    /**
     * Set video uri.
     *
     * @param uri
     */
    public void setVideoURI(Uri uri) {
        setVideoURI(uri, false);
    }


    /**
     * Set video uri.
     *
     * @param uri
     * @param startImmediately
     */
    public void setVideoURI(Uri uri, boolean startImmediately) {
        videoView.setVideoURI(uri, startImmediately);
    }


    /**
     * Start video.
     */
    public void start() {
        videoView.start();
    }


    /**
     * Pause video.
     */
    public void pause() {
        videoView.pause();
    }


    /**
     * Stop video.
     */
    public void stop() {
        videoView.stop();
    }


       /**
     * Seek video position
     *
     * @param msec
     */
    public void seekTo(int msec) {
        videoView.seekTo(msec);
    }

    /**
     * Set Fullscreen
     *
     * @param isFull
     */
    public void setFullscreen(boolean isFull) {

        if (!(getContext() instanceof Activity)) {
            Log.d(TAG, "Set Fullscreen function need a activity context. If context is not activity, this function is not working.");
            return;
        }

        Activity activity = (Activity) getContext();
        if (activity.isFinishing()) {
            Log.e(TAG, "Activity is finishing. It wasn't work.");
            return;
        }

        this.isFull = isFull;
        btnFullscreen.setImageResource(isFull ? icFullscreenCancel : icFullscreenSwitch);

        activity.setRequestedOrientation(isFull ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutVideo.getLayoutParams();
        if (params == null) {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutVideo.setLayoutParams(params);
        }

        params.height = isFull ? ViewGroup.LayoutParams.MATCH_PARENT : Math.round(ratioScreenToVideo * SCREEN_HEIGHT);
        layoutVideo.requestLayout();

        scaleVideo();
    }


    private void scaleVideo() {
    }


    /**
     * @return current state is full screen.
     */
    public boolean isFullscreen() {
        return isFull;
    }


    /**
     * @return VideoView is playing or not.
     */
    public boolean isPlaying() {
        return videoView.isPlaying();
    }


    /**
     * @return Duration of video.
     */
    public int getDuration() {
        return videoView.getDuration();
    }


    /**
     * @return Current Position of video.
     */
    public int getCurrentPosition() {
        return videoView.getCurrentPosition();
    }

    /**
     * @return Video is released or not.
     */
    public boolean isReleased() {
        return videoView.isReleased();
    }


    /**
     * @return Video's width
     */
    public int getVideoWidth() {
        return videoView.getVideoWidth();
    }


    /**
     * @return Video's height
     */
    public int getVideoHeight() {
        return videoView.getVideoHeight();
    }


    private OnPlayStateChangedListener onPlayStateChangedListener;
    private OnPlaybackEventListener onPlaybackEventListener;


    public void setOnPlayStateChangedListener(OnPlayStateChangedListener listener) {
        this.onPlayStateChangedListener = listener;
    }


    public void setOnPlaybackEventListener(OnPlaybackEventListener listener) {
        this.onPlaybackEventListener = listener;
    }


    private class PlayStateChangedListenerImpl implements OnPlayStateChangedListener {

        @Override
        public void onPrepared() {
            int videoWidth = videoView.getVideoWidth();
            int videoHeight = videoView.getVideoHeight();

            Log.v(TAG, String.format("video width : %d, video height : %d", videoWidth, videoHeight));

            ratioVideo = (float) videoWidth / videoHeight;
            ratioScreenToVideo = SCREEN_RATIO / ratioVideo;

            int duration = videoView.getDuration() / 1000;

            int minute = duration / 60;
            int second = duration - (minute * 60);

            tvDuration.setText(String.format(FORMAT_TIME, minute, second));
            seekBar.setMax(duration);

            setFullscreen(false);

            // TODO show controller

            if (onPlayStateChangedListener != null) {
                onPlayStateChangedListener.onPrepared();
            }
        }

        @Override
        public void onError(ErrorReason errorReason) {
            if (onPlayStateChangedListener != null) {
                onPlayStateChangedListener.onError(errorReason);
            }
        }

        @Override
        public void onCompletion() {
            pause();

            if (onPlayStateChangedListener != null) {
                onPlayStateChangedListener.onCompletion();
            }
        }

        @Override
        public void onReleased() {
            if (onPlayStateChangedListener != null) {
                onPlayStateChangedListener.onReleased();
            }
        }
    }


    private class PlaybackEventListenerImpl implements OnPlaybackEventListener {
        @Override
        public void onPlaying() {
            btnPlayPause.setImageResource(icPause);

            if (onPlaybackEventListener != null) {
                onPlaybackEventListener.onPlaying();
            }
        }

        @Override
        public void onPaused() {
            btnPlayPause.setImageResource(icPlay);

            if (onPlaybackEventListener != null) {
                onPlaybackEventListener.onPaused();
            }
        }

        @Override
        public void onStopped() {
            btnPlayPause.setImageResource(icPlay);

            if (onPlaybackEventListener != null) {
                onPlaybackEventListener.onStopped();
            }
        }

        @Override
        public void onPositionChanged(int position) {
            int currentPosition = position / 1000;

            int minute = currentPosition / 60;
            int second = currentPosition - (minute * 60);

            tvCurrentPosition.setText(String.format(FORMAT_TIME, minute, second));
            seekBar.setProgress(currentPosition);

            if (onPlaybackEventListener != null) {
                onPlaybackEventListener.onPositionChanged(position);
            }
        }

        @Override
        public void onBufferingUpdate(int percent) {
            if (onPlaybackEventListener != null) {
                onPlaybackEventListener.onBufferingUpdate(percent);
            }
        }

        @Override
        public void onSeekComplete() {
            if (onPlaybackEventListener != null) {
                onPlaybackEventListener.onSeekComplete();
            }
        }
    }


    private class ComponentClickListenerImpl implements OnClickListener {
        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.btn_play_pause) {
                if (isPlaying()) {
                    pause();
                } else {
                    start();
                }
            } else if (v.getId() == R.id.layout_video) {
                // TODO show or hide controller
            } else if (v.getId() == R.id.btn_fullscreen) {
                setFullscreen(!isFull);
            }
        }
    }


    private class SeekBarChangedListenerImpl implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }

            seekTo(progress * 1000);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO show controller
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO show controller
        }
    }
}
