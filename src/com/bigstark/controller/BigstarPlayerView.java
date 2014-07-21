package com.bigstark.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

public class BigstarPlayerView extends FrameLayout {
	
	private float VIDEO_RATIO = 0;
	
	private int SCREEN_WIDTH;
	private int SCREEN_HEIGHT;
	private int VIDEO_HEIGHT_PORTRAIT;
	
	private Activity activity;
	
	private int toFullScreenIcon = R.drawable.icon_player_full_screen;
	private int toBaseScreenIcon = R.drawable.icon_player_basic_screen;
	private int playIcon = R.drawable.ic_play_circle;
	private int pauseIcon = R.drawable.ic_pause_circle;
	private int seekBarProgressDrawable = R.drawable.seekbar_progress;
	private int seekBarThumb = R.drawable.seekbar_thumb;
	private int currentPositionColor = 0xFFFFFFFF;
	private int durationColor = 0xFFFFFFFF;
	
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
	
	private boolean isFullScreen = false;
	private Uri videoUri;
	
	private OnFullScreenListener fullScreenListener;
	
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
		
		final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BigstarPlayerView, defStyle, 0);
		
		toFullScreenIcon = ta.getResourceId(R.styleable.BigstarPlayerView_toFullScreenIcon, toFullScreenIcon);
		toBaseScreenIcon = ta.getResourceId(R.styleable.BigstarPlayerView_toBaseScreenIcon, toBaseScreenIcon);
		playIcon = ta.getResourceId(R.styleable.BigstarPlayerView_playIcon, playIcon);
		pauseIcon = ta.getResourceId(R.styleable.BigstarPlayerView_pauseIcon, pauseIcon);
		seekBarProgressDrawable = ta.getResourceId(R.styleable.BigstarPlayerView_seekBarProgressDrawable, seekBarProgressDrawable);
		seekBarThumb = ta.getResourceId(R.styleable.BigstarPlayerView_seekBarThumb, seekBarThumb);
		currentPositionColor = ta.getColor(R.styleable.BigstarPlayerView_currentPositionColor, currentPositionColor);
		durationColor = ta.getColor(R.styleable.BigstarPlayerView_durationColor, durationColor);
		
		ta.recycle();

		init();
		initVideo();
		initHandlers();
	}
	
	public void initialize(Activity activity) {
		this.activity = activity;
		
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		SCREEN_WIDTH = metrics.widthPixels;
		SCREEN_HEIGHT = metrics.heightPixels;
	}

	private void init() {
		layoutVideoController = findViewById(R.id.layout_controller);
		ivPlayPause = (ImageView) findViewById(R.id.iv_play_pause);
		tvCurrentPosition = (TextView) findViewById(R.id.tv_current_position);
		tvDuration = (TextView) findViewById(R.id.tv_duration);
		ivFullscreen = (ImageView) findViewById(R.id.iv_fullscreen);
		seekBar = (SeekBar) findViewById(R.id.seek_bar_video);
		
		ivPlayPause.setImageResource(playIcon);
		setCurrentPositionColor(currentPositionColor);
		setDurationColor(durationColor);
		ivFullscreen.setImageResource(toFullScreenIcon);
		setSeekBarProgressDrawable(seekBarProgressDrawable);
		setSeekBarThumb(seekBarThumb);
		
		ivPlayPause.setOnClickListener(mPlayPauseClickListener);
		ivFullscreen.setOnClickListener(mFullscreenClickListener);
		seekBar.setOnSeekBarChangeListener(mSeekChangeLinstener);
	}
	
	private void initHandlers() {
		mediaControllerHandler = new MediaControllerHandler(getContext(), this);
		controllerVisibleHandler = new ControllerVisibleHandler(getContext(), layoutVideoController);
	}
	
	private void initVideo() {
		layoutVideo = (RelativeLayout) findViewById(R.id.layout_video);
		videoView = (VideoView) findViewById(R.id.vv_video);
		
		layoutVideo.setOnClickListener(mLayoutClickListener);
		videoView.setOnPreparedListener(mPreparedListener);
		videoView.setOnCompletionListener(mCompletionListener);
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
				
				if(videoUri != null) {
					setVideoURI(videoUri);
				}
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

				if(videoUri != null) {
					setVideoURI(videoUri);
				}
			}
			
			if(videoView.isPlaying()) {
				mediaControllerHandler.pause();
				ivPlayPause.setImageResource(playIcon);
				showController(false);
			} else {
				mediaControllerHandler.start();
				ivPlayPause.setImageResource(pauseIcon);
				showController();
			}
		}
	};
	
	private View.OnClickListener mFullscreenClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(videoView == null) {
				initVideo();

				if(videoUri != null) {
					setVideoURI(videoUri);
				}
			}
			
			isFullScreen = !isFullScreen;
			doLayout(isFullScreen);
			if(fullScreenListener != null) {
				fullScreenListener.onFullScreen(isFullScreen);
			}
		}
		
		private void doLayout(final boolean isFullScreen) {
			boolean isPlaying = videoView.isPlaying();
			mediaControllerHandler.pause();
			activity.setRequestedOrientation(isFullScreen ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
			
			if(isFullScreen) {
				activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				params.width = WindowManager.LayoutParams.MATCH_PARENT;
				params.height= SCREEN_WIDTH;
				ivFullscreen.setImageResource(toBaseScreenIcon);
			} else {
				activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				params.width = SCREEN_WIDTH;
				params.height = VIDEO_HEIGHT_PORTRAIT;
				ivFullscreen.setImageResource(toFullScreenIcon);
			}
			layoutVideo.setLayoutParams(params);
			if(isPlaying) {
				mediaControllerHandler.start();
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
			VIDEO_RATIO = ((float) ((float) mp.getVideoHeight()) / ((float) mp.getVideoWidth()));
			VIDEO_HEIGHT_PORTRAIT = (int) (((float) SCREEN_WIDTH) * VIDEO_RATIO);
			
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
	
	/**
	 * @return it returns video whole times. it's not millisecond
	 */
	public int getDuration() {
		return videoView.getDuration();
	}

	/**
	 * 
	 * @return it returns video current time. it's not millisecond
	 */
	public int getCurrentPosition() {
		return videoView.getCurrentPosition();
	}
	
	/**
	 * @return it returns video current buffer percentage.
	 */
	public int getBufferPercentage() {
		if(videoView == null) {
			return 0;
		}
		
		return videoView.getBufferPercentage();
	}
	
	/**
	 * Set video uri.
	 * @param uri : Video uri can be file, streaming etc...
	 */
	public void setVideoURI(Uri uri) {
		this.videoUri = uri;
		
		if(videoView == null) {
			initVideo();
		}
		
		videoView.setVideoURI(uri);
	}
	
	/**
	 * @return it returns video ratio. ratio means height / width.
	 */
	public float getVideoRatio() {
		return VIDEO_RATIO;
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
	
	public void setToFullScreenIcon(int resId) {
		toFullScreenIcon = resId;
	}
	
	public void setToBaseScreenIcon(int resId) {
		toBaseScreenIcon = resId;
	}
	
	public void setPlayIcon(int resId) {
		playIcon = resId;
	}
	
	public void setPauseIcon(int resId) {
		pauseIcon = resId;
	}
	
	public void setSeekBarProgressDrawable(int resId) {
		seekBarProgressDrawable = resId;
		seekBar.setProgressDrawable(getResources().getDrawable(resId));
	}
	
	public void setSeekBarThumb(int resId) {
		seekBarThumb = resId;
		seekBar.setThumb(getResources().getDrawable(resId));
	}
	
	public void setCurrentPositionColorResource(int colorResId) {
		setCurrentPositionColor(getResources().getColor(colorResId));
	}
	
	public void setCurrentPositionColor(int color) {
		currentPositionColor = color;
		tvCurrentPosition.setTextColor(color);
	}
	
	public void setDurationColorResource(int colorResId) {
		setDurationColor(getResources().getColor(colorResId));
	}
	
	public void setDurationColor(int color) {
		durationColor = color;
		tvDuration.setTextColor(color);
	}
	
	public void setOnFullScreenListener(OnFullScreenListener listener) {
		fullScreenListener = listener;
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

	public interface OnFullScreenListener {
		public void onFullScreen(boolean isFullScreen);
	}
}
