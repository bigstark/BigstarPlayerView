package com.bigstark.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class BigstarPlayerView extends VideoView {
	
	public BigstarPlayerView(Context context) {
		this(context, null);
	}
	
	public BigstarPlayerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BigstarPlayerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

}
