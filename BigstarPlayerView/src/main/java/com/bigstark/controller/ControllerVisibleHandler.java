package com.bigstark.controller;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

class ControllerVisibleHandler extends Handler {
    private static final int SHOW_DURATION = 4000;

    private int currentMsg = 0;
    private View layoutController;

    public ControllerVisibleHandler(View layoutController) {
        this.layoutController = layoutController;
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
            Animation fadeIn = AnimationUtils.loadAnimation(layoutController.getContext(), android.R.anim.fade_in);
            layoutController.startAnimation(fadeIn);
        }
        layoutController.setVisibility(View.VISIBLE);

        if (!isHideLater) {
            return;
        }

        Message msg = obtainMessage();
        msg.what = ++currentMsg;
        sendMessageDelayed(msg, SHOW_DURATION);
    }

    public void hideController() {
        Animation fadeOut = AnimationUtils.loadAnimation(layoutController.getContext(), android.R.anim.fade_out);
        layoutController.startAnimation(fadeOut);
        layoutController.setVisibility(View.GONE);
    }

    public boolean isVisibleController() {
        return layoutController != null && layoutController.getVisibility() == View.VISIBLE;
    }
}
