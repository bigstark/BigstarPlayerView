#BigstarPlayerView

It helps you play video more comfortable, pretty and useful.


##Include your project
add build.gradle
```
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```
```
dependencies {
        compile 'com.github.bigstark:BigstarPlayerView:1.2'
}
```



###Usage
You cau use it on xml or java.


###XML
```xml
<com.bigstark.controller.BigstarPlayerView
        android:id="@+id/bpv_video"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
```

###Custom Setting
- Play/Pause Icons
- seekBarProgressDrawable
- seekBarThumb
- toBaseScreenIcon
- toFullScreenIcon

```xml
bigstark:pauseIcon="@drawable/icon_pause"
bigstark:playIcon="@drawable/icon_play"
bigstark:seekBarProgressDrawable="@drawable/po_seekbar"
bigstark:seekBarThumb="@drawable/icon_seekbar_thumb"
bigstark:toBaseScreenIcon="@drawable/icon_player_basic_screen"
bigstark:toFullScreenIcon="@drawable/icon_player_full_screen"
```

also please add `xmlns:bigstark="http://schemas.android.com/apk/res-auto"`

#Must
For FullScreen, you must add `android:configChanges="keyboard|keyboardHidden|orientation|screenSize"` on Activity in AndroidManifest

###Java
After `BigstarPlayerView bpvMain = (BigstarPlayerView) findViewById(R.id.bpv_video)`

You must intialize like this.

``bpvMain.initialize(activity)``

you must set `bpvMain.setVideoURI(uri)` before `bpvMain.start()`

if you want to seek to specific position, you can use `bpvMain.seekTo(second)`
also you can stop video as `bpvMain.pause()`

if you want to fix video height, you can use `bpvMain.setVideoHeight(height)`

if you want to kwow video ratio, you can use `bpvMain.getVideoRatio()`


License
-------

    Copyright 2015 BigStarK

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
