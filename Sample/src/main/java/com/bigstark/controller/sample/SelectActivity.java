package com.bigstark.controller.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by bigstark on 15. 12. 21..
 */
public class SelectActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_select);

    findViewById(R.id.btn_player).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(SelectActivity.this, PlayerActivity.class));
      }
    });

    findViewById(R.id.btn_video).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(SelectActivity.this, VideoActivity.class));
      }
    });
  }
}
