package com.hit.pretstreet.pretstreet.navigation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.hit.pretstreet.pretstreet.R;

/**
 * Play introduction video which will be there in the resource file
 * Not using now
 * @author SVS
 * */
public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        VideoView videoView =findViewById(R.id.vv_intro);

        //Creating MediaController
        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);

        //specify the location of media file
        /*String path = "android.resource://"+getPackageName() + "/" + R.raw.screen_video;
        Uri uri = Uri.parse(path);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });*/

    }
}
