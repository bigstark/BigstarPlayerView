package com.bigstark.controller.player;

import android.media.MediaPlayer;

/**
 * Created by BigStarK on 16. 1. 4..
 */
public abstract class OnPlayStateChangedListener implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {

    public abstract void onReleased();

}
