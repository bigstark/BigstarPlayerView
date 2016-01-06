package com.bigstark.controller.player;

import android.media.MediaPlayer;

/**
 * Created by BigStarK on 16. 1. 4..
 */
public abstract class OnPlaybackEventListener implements MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnBufferingUpdateListener {

    public abstract void onPlaying();

    public abstract void onStopped();

    public abstract void onPositionChanged(int position);

}
