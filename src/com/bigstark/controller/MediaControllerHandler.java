package com.bigstark.controller;

import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

class MediaControllerHandler extends Handler {
	
	private VideoView videoView;
	
	private TextView tvCurrentPosition;
	private SeekBar seekBar;
	private ImageView ivPlayPause;
	
	boolean isPlaying = false;
	private int currentPosition = 0;
	
	public MediaControllerHandler(BigstarPlayerView videoPlayerView) {
		this.videoView = videoPlayerView.getVideoView();
		
	}
	
	public void setCurrentPosition(TextView tvCurrentPosition) {
		this.tvCurrentPosition = tvCurrentPosition;
	}
	
	public void setSeekBar(SeekBar seekBar) {
		this.seekBar = seekBar;
	}
	
	public void setPlayPause(ImageView ivPlayPause) {
		this.ivPlayPause = ivPlayPause;
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		
		if(videoView == null) {
			return;
		}

		if (!isPlaying) {
			return;
		}

		currentPosition = videoView.getCurrentPosition() / 1000;

		int minute = currentPosition / 60;
		int second = currentPosition - (minute * 60);

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%d", minute) + ":");
		sb.append(String.format("%02d", second));
		tvCurrentPosition.setText("" + sb.toString());
		seekBar.setProgress(currentPosition);

		sendEmptyMessageDelayed(0, 1000);
	}

	public void start() {
		if (videoView == null) {
			return;
		}

		isPlaying = true;
		videoView.start();
		ivPlayPause.setImageResource(R.drawable.ic_pause_circle);
		sendEmptyMessage(0);
	}

	public void pause() {
		if (videoView == null) {
			return;
		}

		isPlaying = false;
		videoView.pause();
		ivPlayPause.setImageResource(R.drawable.ic_play_circle);
	}

	/**
	 * @param second
	 *  not millisecond
	 */
	public void seekTo(int second) {
		if (videoView == null) {
			return;
		}
		videoView.seekTo(second * 1000);
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

}
