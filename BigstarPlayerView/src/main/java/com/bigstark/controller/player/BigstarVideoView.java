package com.bigstark.controller.player;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.io.IOException;

/**
 * Created by bigstark on 15. 12. 21..
 */
public class BigstarVideoView extends TextureView {
    private static final String TAG = BigstarVideoView.class.getSimpleName();

    private static final int STATE_PLAYING = 0;
    private static final int STATE_PAUSE = 1;
    private static final int STATE_STOPPED = 2;
    private static final int STATE_RELEASED = 3;

    private MediaPlayer player;
    private Uri uri;
    private Surface surface;
    private boolean startImmediately = false;
    private boolean isRetainPlayerInstance = false;

    private int width;
    private int height;

    private PositionHandler positionHandler = new PositionHandler();
    private SurfaceTextureListenerImpl surfaceTextureListenerImpl;
    private PlaybackEventListenerImpl playbackEventListener;
    private PlayStateChangedListenerImpl playStateChangedListener;

    public BigstarVideoView(Context context) {
        this(context, null);
    }

    public BigstarVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigstarVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSurface();
    }


    private void initSurface() {
        // set surface texture listener.
        surfaceTextureListenerImpl = new SurfaceTextureListenerImpl();
        super.setSurfaceTextureListener(surfaceTextureListenerImpl);
    }

    private void initPlayer() {
        // initialize media player.
        playStateChangedListener = new PlayStateChangedListenerImpl();
        playbackEventListener = new PlaybackEventListenerImpl();

        player = new MediaPlayer();
        player.setOnPreparedListener(playStateChangedListener);
        player.setOnCompletionListener(playStateChangedListener);
        player.setOnBufferingUpdateListener(playbackEventListener);
        player.setOnSeekCompleteListener(playbackEventListener);
    }

    /**
     * Prepare video from uri.
     *
     * @param uri
     */
    public void setVideoURI(Uri uri) {
        setVideoURI(uri, false);
    }

    /**
     * Prepare video from uri.
     * <p/>
     * When prepare complete, start video immediately or not.
     *
     * @param uri
     * @param startImmediately
     */
    public void setVideoURI(Uri uri, boolean startImmediately) {
        Log.v(TAG, "setVideoURI");
        this.uri = uri;
        this.startImmediately = startImmediately;

        // player is released. reinitialize
        synchronized (BigstarVideoView.class) {
            if (player == null) {
                initPlayer();
            }
        }

        try {
            player.setDataSource(getContext(), uri);
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setRetainPlayerInstance(boolean isRetain) {
        this.isRetainPlayerInstance = isRetain;
    }

    /**
     * Start video.
     * <p/>
     * If uri is null, not working.
     */
    public void start() {
        // player is released.
        if (isReleased()) {
            throw new IllegalStateException("BigstarVideoView is released. Set video uri again.");
        }

        if (uri == null) {
            return;
        }

        player.start();
        positionHandler.sendMessage(positionHandler.obtainMessage(STATE_PLAYING));
        playbackEventListener.onPlaying();
    }

    /**
     * Pause video.
     * <p/>
     * If uri is null, not working.
     */
    public void pause() {
        // player is released.
        if (isReleased()) {
            throw new IllegalStateException("BigstarVideoView is released. Set video uri again.");
        }

        if (uri == null) {
            return;
        }

        player.pause();
        positionHandler.sendMessage(positionHandler.obtainMessage(STATE_PAUSE));
    }

    /**
     * Stop video.
     * <p/>
     * If uri is null, not working.
     */
    public void stop() {
        // player is released.
        if (isReleased()) {
            throw new IllegalStateException("BigstarVideoView is released. Set video uri again.");
        }

        if (uri == null) {
            return;
        }

        player.stop();
        positionHandler.sendMessage(positionHandler.obtainMessage(STATE_STOPPED));
        playbackEventListener.onStopped();
    }

    /**
     * Reset media player.
     */
    public void reset() {
        // player is released.
        if (isReleased()) {
            throw new IllegalStateException("BigstarVideoView is released. Set video uri again.");
        }

        player.reset();
        positionHandler.sendMessage(positionHandler.obtainMessage(STATE_STOPPED));
    }

    /**
     * Release media player.
     * <p/>
     * It releases all listener, so After releases, re initialize.
     */
    public void release() {
        // already released
        if (player == null) {
            return;
        }

        Log.v(TAG, "Player is released");

        positionHandler.sendMessage(positionHandler.obtainMessage(STATE_STOPPED));
        player.release();

        player = null;
        uri = null;
        startImmediately = false;

        if (onPlayStateChangedListener != null) {
            onPlayStateChangedListener.onReleased();
        }
    }

    /**
     * @return player released or not.
     */
    public boolean isReleased() {
        return player == null;
    }

    /**
     * @return video is playing or not.
     */
    public boolean isPlaying() {
        return player != null && uri != null && player.isPlaying();
    }

    /**
     * @return current position of media player
     */
    public int getCurrentPosition() {
        return player == null ? 0 : player.getCurrentPosition();
    }

    /**
     * @return duration of media player
     */
    public int getDuration() {
        return player == null ? 0 : player.getDuration();
    }

    /**
     * seek to position
     *
     * @param msec : micro seconds.
     */
    public void seekTo(int msec) {
        if (player == null) {
            throw new IllegalStateException("BigstarVideoView is released. Set video uri again.");
        }

        player.seekTo(msec);
    }

    private SurfaceTextureListener listener;

    @Override
    public void setSurfaceTextureListener(SurfaceTextureListener listener) {
        this.listener = listener;
    }

    private class SurfaceTextureListenerImpl implements SurfaceTextureListener {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            Log.v(TAG, String.format("Surface now available - width : %d, height : %d", width, height));
            synchronized (BigstarVideoView.class) {
                if (player == null) {
                    initPlayer();

                    if (uri != null) {
                        setVideoURI(uri, startImmediately);
                    }
                }
            }
            surface = new Surface(surfaceTexture);
            player.setSurface(surface);

            if (listener != null) {
                listener.onSurfaceTextureAvailable(surfaceTexture, width, height);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Log.v(TAG, String.format("Surface size changed - width : %d, height : %d", width, height));

            if (listener != null) {
                listener.onSurfaceTextureSizeChanged(surface, width, height);
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Log.v(TAG, "Surface is destroyed");

            if (!isRetainPlayerInstance) {
                release();
            }

            if (listener != null) {
                return listener.onSurfaceTextureDestroyed(surface);
            }

            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            if (listener != null) {
                listener.onSurfaceTextureUpdated(surface);
            }
        }
    }

    private OnPlaybackEventListener onPlaybackEventListener;
    private OnPlayStateChangedListener onPlayStateChangedListener;

    /**
     * It works only video screen is alive state.
     *
     * @param listener
     */
    public void setOnPlaybackEventListener(OnPlaybackEventListener listener) {
        this.onPlaybackEventListener = listener;
    }

    /**
     * When player released, it works.
     *
     * @param listener
     */
    public void setOnPlayStateChangedListener(OnPlayStateChangedListener listener) {
        this.onPlayStateChangedListener = listener;
    }

    private class PlaybackEventListenerImpl extends OnPlaybackEventListener {
        @Override
        public void onPlaying() {
            if (onPlaybackEventListener != null) {
                onPlaybackEventListener.onPlaying();
            }
        }

        @Override
        public void onStopped() {
            if (onPlaybackEventListener != null) {
                onPlaybackEventListener.onStopped();
            }
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (onPlaybackEventListener != null) {
                onPlaybackEventListener.onBufferingUpdate(mp, percent);
            }
        }

        @Override
        public void onPositionChanged(int position) {
            if (onPlaybackEventListener != null) {
                onPlaybackEventListener.onPositionChanged(position);
            }
        }

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            if (onPlaybackEventListener != null) {
                onPlaybackEventListener.onSeekComplete(mp);
            }
        }
    }

    private class PlayStateChangedListenerImpl extends OnPlayStateChangedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            width = mp.getVideoWidth();
            height = mp.getVideoHeight();

            Log.v(TAG, String.format("Video width : %d, height : %d", width, height));

            if (onPlayStateChangedListener != null) {
                onPlayStateChangedListener.onPrepared(mp);
            }

            if (startImmediately) {
                start();
            }
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (onPlayStateChangedListener != null) {
                onPlayStateChangedListener.onCompletion(mp);
            }
        }

        @Override
        public void onReleased() {
            if (onPlayStateChangedListener != null) {
                onPlayStateChangedListener.onReleased();
            }
        }
    }

    private class PositionHandler extends Handler {
        private static final int INTERVAL_TIME = 128;

        private int offset = 0;

        public PositionHandler() {
            super(Looper.myLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case STATE_PLAYING:
                    schedulePlaying();
                    break;
                case STATE_STOPPED:
                case STATE_RELEASED:
                case STATE_PAUSE:
                    removeMessages(STATE_PLAYING);

            }
        }

        private void schedulePlaying() {
            removeMessages(STATE_PLAYING);
            if (player == null || isReleased() || !player.isPlaying()) {
                return;
            }

            Message msg = obtainMessage();
            msg.what = STATE_PLAYING;
            sendMessageDelayed(msg, INTERVAL_TIME);

            int offset = player.getCurrentPosition();
            if (this.offset == offset) {
                return;
            }

            this.offset = offset;
            playbackEventListener.onPositionChanged(offset);
        }
    }

}

