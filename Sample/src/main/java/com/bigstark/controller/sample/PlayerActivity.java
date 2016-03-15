package com.bigstark.controller.sample;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.bigstark.controller.BigstarPlayerView;


public class PlayerActivity extends Activity {
    private static final String SAMPLE_URL = "http://api.wecandeo.com/video/default/BOKNS9AQWrFSTupSbNE3M60gpF3OhwV3K8yrlKIEcwYPMWo7Gbip7cgieie";

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
