package com.bigstark.controller;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

public class BigstarPlayerView extends FrameLayout {
	
	private int SCREEN_WIDTH = 0;
	private int SCREEN_HEIGHT = 0;
	private int VIDEO_HEIGHT_PORTRAIT = 0;
	
	private RelativeLayout layoutVideo;
	private VideoView videoView;

	private View layoutVideoController;
	private ImageView ivPlayPause;
	private TextView tvCurrentPosition;
	private TextView tvDuration;
	private ImageView ivFullscreen;
	private SeekBar seekBar;

	private MediaControllerHandler mediaControllerHandler;
	private ControllerVisibleHandler controllerVisibleHandler;

	public BigstarPlayerView(Context context) {
		this(context, null);
	}

	public BigstarPlayerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BigstarPlayerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.bigstar_player_view, this);
		init();
	}

	private void init() {
		layoutVideo = (RelativeLayout) findViewById(R.id.layout_video);
		videoView = (VideoView) findViewById(R.id.vv_video);
		
		layoutVideoController = findViewById(R.id.layout_controller);
		ivPlayPause = (ImageView) findViewById(R.id.iv_play_pause);
		tvCurrentPosition = (TextView) findViewById(R.id.tv_current_position);
		tvDuration = (TextView) findViewById(R.id.tv_duration);
		ivFullscreen = (ImageView) findViewById(R.id.iv_fullscreen);
		seekBar = (SeekBar) findViewById(R.id.seek_bar_video);
	}
	
	public VideoView getVideoView() {
		return videoView;
	}

	static class SavedState extends BaseSavedState {
		int stateToSave;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			this.stateToSave = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(this.stateToSave);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

}
