package com.bigstark.controller.player;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
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

  private MediaPlayer player;
  private MediaPlayerListenerContainer listenerContainer;
  private Uri uri;
  private Surface surface;
  private boolean startImmediately = false;
  private boolean isRetainPlayerInstance = false;

  private int width;
  private int height;

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

  private SurfaceTextureListener listener;

  private void initSurface() {
    // set surface texture listener.
    super.setSurfaceTextureListener(new SurfaceTextureListener() {
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
        // TODO seekbar update in here

        if (listener != null) {
          listener.onSurfaceTextureUpdated(surface);
        }
      }
    });
  }

  @Override
  public void setSurfaceTextureListener(SurfaceTextureListener listener) {
    this.listener = listener;
  }

  private void initPlayer() {
    // initialize media player.
    player = new MediaPlayer();
    listenerContainer = new MediaPlayerListenerContainer();

    player.setOnPreparedListener(listenerContainer);
    player.setOnBufferingUpdateListener(listenerContainer);
    player.setOnErrorListener(listenerContainer);
    player.setOnSeekCompleteListener(listenerContainer);
    player.setOnInfoListener(listenerContainer);
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
   *
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
   *
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
  }

  /**
   * Pause video.
   *
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
  }

  /**
   * Stop video.
   *
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
  }

  /**
   * Release media player.
   *
   * It releases all listener, so After releases, re initialize.
   */
  public void release() {
    // already released
    if (player == null) {
      return;
    }

    Log.v(TAG, "Player is released");

    player.release();
    listenerContainer.release();

    player = null;
    uri = null;
    startImmediately = false;
  }

  public boolean isReleased() {
    return player == null;
  }

  /**
   * @return video is playing or not.
   */
  public boolean isPlaying() {
    return player != null && uri != null && player.isPlaying();
  }

  public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
    if (listenerContainer != null) {
      listenerContainer.setOnPreparedListener(listener);
    }
  }

  public void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener listener) {
    if (listenerContainer != null) {
      listenerContainer.setOnBufferingUpdateListener(listener);
    }
  }

  public void setOnErrorListener(MediaPlayer.OnErrorListener listener) {
    if (listenerContainer != null) {
      listenerContainer.setOnErrorListener(listener);
    }
  }

  public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener listener) {
    if (listenerContainer != null) {
      listenerContainer.setOnSeekCompleteListener(listener);
    }
  }

  public void setOnInfoListener(MediaPlayer.OnInfoListener listener) {
    if (listenerContainer != null) {
      listenerContainer.setOnInfoListener(listener);
    }
  }

  private class MediaPlayerListenerContainer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener,
      MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener {

    private MediaPlayer.OnPreparedListener preparedListener;
    private MediaPlayer.OnBufferingUpdateListener bufferingUpdateListener;
    private MediaPlayer.OnErrorListener errorListener;
    private MediaPlayer.OnSeekCompleteListener seekCompleteListener;
    private MediaPlayer.OnInfoListener infoListener;


    public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
      this.preparedListener = listener;
    }

    public void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener listener) {
      this.bufferingUpdateListener = listener;
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener listener) {
      this.errorListener = listener;
    }

    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener listener) {
      this.seekCompleteListener = listener;
    }

    public void setOnInfoListener(MediaPlayer.OnInfoListener listener) {
      this.infoListener = listener;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
      if (startImmediately) {
        start();
      }

      width = mp.getVideoWidth();
      height = mp.getVideoHeight();

      Log.v(TAG, String.format("Video width : %d, height : %d", width, height));

      if (preparedListener != null) {
        preparedListener.onPrepared(mp);
      }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {


      if (bufferingUpdateListener != null) {
        bufferingUpdateListener.onBufferingUpdate(mp, percent);
      }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {


      if (errorListener != null) {
        return errorListener.onError(mp, what, extra);
      }
      return false;
    }


    @Override
    public void onSeekComplete(MediaPlayer mp) {


      if (seekCompleteListener != null) {
        seekCompleteListener.onSeekComplete(mp);
      }
    }


    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
      if (infoListener != null) {
        return infoListener.onInfo(mp, what, extra);
      }
      return false;
    }

    public void release() {
      preparedListener = null;
      bufferingUpdateListener = null;
      errorListener = null;
      seekCompleteListener = null;
      infoListener = null;
    }
  }

}
