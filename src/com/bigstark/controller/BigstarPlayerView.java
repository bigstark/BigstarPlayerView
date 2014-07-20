package com.bigstark.controller;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
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
	
	private Uri videoUri;

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
		initVideo();
		initHandlers();
	}

	private void init() {
		layoutVideoController = findViewById(R.id.layout_controller);
		ivPlayPause = (ImageView) findViewById(R.id.iv_play_pause);
		tvCurrentPosition = (TextView) findViewById(R.id.tv_current_position);
		tvDuration = (TextView) findViewById(R.id.tv_duration);
		ivFullscreen = (ImageView) findViewById(R.id.iv_fullscreen);
		seekBar = (SeekBar) findViewById(R.id.seek_bar_video);
		
		layoutVideo.setOnClickListener(mLayoutClickListener);
		ivPlayPause.setOnClickListener(mPlayPauseClickListener);
		seekBar.setOnSeekBarChangeListener(mSeekChangeLinstener);
	}
	
	private void initHandlers() {
		mediaControllerHandler = new MediaControllerHandler(getContext(), this);
		controllerVisibleHandler = new ControllerVisibleHandler(getContext(), layoutVideoController);
	}
	
	private void initVideo() {
		layoutVideo = (RelativeLayout) findViewById(R.id.layout_video);
		videoView = (VideoView) findViewById(R.id.vv_video);
		
		videoView.setOnPreparedListener(mPreparedListener);
		videoView.setOnCompletionListener(mCompletionListener);
	}
	
	protected VideoView getVideoView() {
		return videoView;
	}
	
	protected ImageView getPlayPauseView() {
		return ivPlayPause;
	}
	
	protected TextView getCurrentPositionView() {
		return tvCurrentPosition;
	}
	
	protected TextView getDurationView() {
		return tvDuration;
	}
	
	protected ImageView getFullscreenView() {
		return ivFullscreen;
	}
	
	protected SeekBar getSeekBar() {
		return seekBar;
	}
	
	/**
	 * Start video play
	 */
	public void start() {
		mediaControllerHandler.start();
		showController();
	}

	/**
	 * Video pause
	 */
	public void pause() {
		mediaControllerHandler.pause();
		showController(false);
	}

	/**
	 * Seek Video position
	 * @param second : not millisecond.
	 */
	public void seekTo(int second) {
		mediaControllerHandler.seekTo(second);
		showController();
	}
	/**
	 *  Show controller, and hide after 4 second 
	 */
	public void showController() {
		showController(true);
	}
	
	/**
	 * Show controller
	 * @param isHideLater : if false, don't hide controller
	 */
	public void showController(boolean isHideLater) {
		showController(true, isHideLater);
	}

	/**
	 * Show controller
	 * @param hasAnimation : if true, animate. else, don't animate
	 * @param isHideLater : if false, don't hide controller
	 */
	public void showController(boolean hasAnimation, boolean isHideLater) {
		controllerVisibleHandler.showController(hasAnimation, isHideLater);
	}

	/**
	 * Hide controller
	 */
	public void hideController() {
		controllerVisibleHandler.hideController();
	}
		
	private View.OnClickListener mLayoutClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(videoView == null) {
				initVideo();
			}
			
			if(videoUri != null) {
				setVideoURI(videoUri);
			}
			
			if(controllerVisibleHandler.isVisibleController()) {
				hideController();
			} else {
				showController();
			}
		}
	};
	
	private View.OnClickListener mPlayPauseClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(videoView == null) {
				initVideo();
			}
			
			if(videoUri != null) {
				setVideoURI(videoUri);
			}
			
			if(videoView.isPlaying()) {
				mediaControllerHandler.pause();
				showController(false);
			} else {
				mediaControllerHandler.start();
				showController();
			}
		}
	};
	
	private SeekBar.OnSeekBarChangeListener mSeekChangeLinstener = new SeekBar.OnSeekBarChangeListener() {
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			showController(false, false);
		}
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			showController(false, true);
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if(!fromUser) {
				return;
			}
			
			if(videoView == null) {
				initVideo();
			}
			
			if(videoUri != null) {
				setVideoURI(videoUri);
			}
			
			mediaControllerHandler.seekTo(progress);
		}
	};
	
	private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
		
		@Override
		public void onPrepared(MediaPlayer mp) {
			int duration = mp.getDuration() / 1000;
			
			int minute = duration / 60;
			int second = duration - (minute * 60);
			
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("%d", minute) + ":");
			sb.append(String.format("%02d", second));
			
			tvDuration.setText("" + sb.toString());
			
			seekBar.setMax(duration);
		}
	};
	
	private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
		
		@Override
		public void onCompletion(MediaPlayer mp) {
			
		}
	};
	
	public int getBufferPercentage() {
		if(videoView == null) {
			return 0;
		}
		
		return videoView.getBufferPercentage();
	}
	
	public void setVideoURI(Uri uri) {
		this.videoUri = uri;
		
		if(videoView == null) {
			initVideo();
		}
		
		videoView.setVideoURI(uri);
	}
	
	protected void setFullScreenClickListener(View.OnClickListener listener) {
		ivFullscreen.setOnClickListener(listener);
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
