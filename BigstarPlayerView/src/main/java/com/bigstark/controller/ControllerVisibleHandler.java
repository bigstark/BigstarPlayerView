package com.bigstark.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AnimationUtils;

class ControllerVisibleHandler extends Handler {
  private static final int SHOW_DURATION = 4000;

  private Context context;
  private View layoutVideoController;
  private int currentMsg = 0;

  public ControllerVisibleHandler(View layoutVideoController) {
    this.layoutVideoController = layoutVideoController;
    this.context = layoutVideoController.getContext();
  }

  @Override
  public void handleMessage(Message msg) {
    super.handleMessage(msg);
    if (!isVisibleController()) {
      return;
    }

    if (currentMsg != msg.what) {
      return;
    }

    hideController();
  }

  public void showController(boolean isHideLater) {
    showController(true, isHideLater);
  }

  public void showController(boolean hasAnimation, boolean isHideLater) {
    try {
      removeMessages(currentMsg);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (hasAnimation && !isVisibleController()) {
      layoutVideoController.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
    }
    layoutVideoController.setVisibility(View.VISIBLE);

    if (!isHideLater) {
      return;
    }

    currentMsg = (int) System.currentTimeMillis();
    Message msg = obtainMessage();
    msg.what = currentMsg;
    sendMessageDelayed(msg, SHOW_DURATION);
  }

  public void hideController() {
    layoutVideoController.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
    layoutVideoController.setVisibility(View.GONE);
  }

  public boolean isVisibleController() {
    return layoutVideoController != null && layoutVideoController.getVisibility() == View.VISIBLE;
  }
}
