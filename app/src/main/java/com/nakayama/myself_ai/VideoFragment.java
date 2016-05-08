package com.nakayama.myself_ai;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

/**
 * Created by nakayama on 2016/02/09.
 */
public class VideoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstance){
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);
        VideoView videoView = (VideoView)rootView.findViewById(R.id.video_view);

        String url = getArguments().getString("result");
        //String url = "https://video.twimg.com/ext_tw_video/697041220883783681/pr/vid/720x720/ZYHTwMEtjO1LhDEE.mp4";

        videoView.setVideoURI(Uri.parse(url));
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                getActivity().finish();
            }
        });
        videoView.start();

        return rootView;

    }
}
