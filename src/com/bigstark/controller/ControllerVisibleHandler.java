package com.bigstark.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AnimationUtils;

class ControllerVisibleHandler extends Handler {
	private Context context;
	private View layoutVideoController;
	private int currentMsg = 0;
	
	public ControllerVisibleHandler(Context context, View layoutVideoController) {
		this.context = context;
		this.layoutVideoController = layoutVideoController;
	}
	
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		if(!isVisibleController()) {
			return;
		}
		
		if(currentMsg != msg.what) {
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
		if(hasAnimation && !isVisibleController()) {
			layoutVideoController.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
		} else {
		}
		layoutVideoController.setVisibility(View.VISIBLE);
		
		if(!isHideLater) {
			return;
		}
		
		currentMsg = (int) System.currentTimeMillis();
		Message msg = obtainMessage();
		msg.what = currentMsg;
		sendMessageDelayed(msg, 4000);
	}
	
	public void hideController() {
		layoutVideoController.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
		layoutVideoController.setVisibility(View.GONE);
	}
	
	private boolean isVisibleController() {
		if(layoutVideoController == null) {
			return false;
		}
		
		return layoutVideoController.getVisibility() == View.VISIBLE ? true : false;
	}
}
