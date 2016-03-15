package com.bigstark.controller.sample;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bigstark.controller.BigstarPlayerView;
import com.bigstark.video.ErrorReason;
import com.bigstark.video.OnPlayStateChangedListener;
import com.bigstark.video.OnPlaybackEventListener;


public class PlayerActivity extends Activity {
    private static final String SAMPLE_URL = "https://walterebert.com/playground/video/hls/sintel-trailer.m3u8";

    private final float VIDEO_RATIO = (float) 9 / (float) 16;
    private final String KEY_CURRENT_POSITION = "CurrentPosition";

    private boolean isFullScreen = false;
    private boolean isRestore = false;

    private BigstarPlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initVideo();

        if (savedInstanceState != null) {
            int currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION);
            playerView.pause();
            playerView.seekTo(currentPosition);
            isRestore = true;
        }
    }


    private void initVideo() {
        playerView = (BigstarPlayerView) findViewById(R.id.bigstar_player_view);
        playerView.setOnPlayStateChangedListener(new OnPlayStateChangedListener() {
            @Override
            public void onPrepared() {
                Log.v("TAG", "onPrepared");
            }

            @Override
            public void onError(ErrorReason errorReason) {
                Log.v("TAG", "onError");
            }

            @Override
            public void onCompletion() {
                Log.v("TAG", "onCompletion");
            }

            @Override
            public void onReleased() {
                Log.v("TAG", "onReleased");
            }
        });
        playerView.setOnPlaybackEventListener(new OnPlaybackEventListener() {
            @Override
            public void onPlaying() {
                Log.v("TAG", "onPlaying");
            }

            @Override
            public void onPaused() {
                Log.v("TAG", "onPaused");
            }

            @Override
            public void onStopped() {
                Log.v("TAG", "onStopped");
            }

            @Override
            public void onPositionChanged(int i) {
                Log.v("TAG", "onPosition Changed to " + i);
            }

            @Override
            public void onBufferingUpdate(int i) {
            }

            @Override
            public void onSeekComplete() {
                Log.v("TAG", "onSeekComplete");
            }
        });

//    DisplayMetrics metrics = new DisplayMetrics();
//    getWindowManager().getDefaultDisplay().getMetrics(metrics);
//    playerView.setVideoHeight((int) (metrics.widthPixels * VIDEO_RATIO));
//    playerView.setOnFullScreenListener(new BigstarPlayerView.OnFullScreenListener() {
//
//      @Override
//      public void onFullScreen(boolean isFullScreen) {
//        PlayerActivity.this.isFullScreen = isFullScreen;
//      }
//    });
//    playerView.setOnPrepareCompleteListener(new BigstarPlayerView.OnPrepareCompleteListener() {
//
//      @Override
//      public void onPrepareComplete() {
//        if (isRestore) {
//          return;
//        }
//        playerView.start();
//      }
//    });

        Uri vidUri = Uri.parse(SAMPLE_URL);
        playerView.setVideoURI(vidUri, true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_CURRENT_POSITION, playerView.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }

}
