package com.bigstark.controller.sample;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bigstark.controller.player.BigstarVideoView;

/**
 * Created by bigstark on 15. 12. 21..
 */
public class VideoActivity extends Activity {
  private static final String SAMPLE_URL = "http://api.wecandeo.com/video/default/BOKNS9AQWrFSTupSbNE3M60gpF3OhwV3yyzlOMrMC7eistTjDgbxwEIMOwkBTzaHcoOViiNisKuiiEwsGmlVaxo7l8JteyHOb4J7WZHhNSvMRcoie";

  private ViewGroup container;
  private BigstarVideoView videoView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_video);
    container = (ViewGroup) findViewById(R.id.layout_container);

    videoView = (BigstarVideoView) findViewById(R.id.bigstar_video_view);
    videoView.setVideoURI(Uri.parse(SAMPLE_URL), true);
    videoView.setRetainPlayerInstance(true);
    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
      @Override
      public void onPrepared(MediaPlayer mp) {
        Log.v("TAG", "onPrepared");
      }
    });

    findViewById(R.id.btn_appear).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (videoView.getParent() != null) {
          return;
        }

        Log.v("TAG", "appear");
        container.addView(videoView, 0);
      }
    });

    findViewById(R.id.btn_disappear).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (videoView.getParent() == null) {
          return;
        }

        Log.v("TAG", "disappear");
        container.removeView(videoView);
      }
    });
  }
}
