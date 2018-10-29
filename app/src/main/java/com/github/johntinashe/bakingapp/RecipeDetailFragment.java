package com.github.johntinashe.bakingapp;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.johntinashe.bakingapp.model.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.Objects;

/**
 * A fragment representing a single Recipe detail screen.
 * This fragment is either contained in a {@link RecipeListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeDetailFragment extends Fragment {

    private static final String TAG = "SESSION" ;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private static final String KEY_VISIBILITY = "exoPlayerVisibility";
    private static final String MEDIA_POSITION = "MEDIA_POSITION";
    private static final String PLAY_WHEN_READY="playWhenReady";
    private long positionPlayer;
    private boolean playWhenReady;

    private Step step;
    private ImageView noVideo;
    private TextView mStepDescription;
    private boolean  mTwoPane;

    public RecipeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        if (getArguments().containsKey("step")) {

            Activity activity = this.getActivity();
            if (activity != null) {
                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    step = bundle.getParcelable("step");
                    if (step != null) {
                        getActivity().setTitle(step.getShortDescription());
                    }
                }
            }


        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail, container, false);

        if (savedInstanceState != null) {
            playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY);
            positionPlayer = savedInstanceState.getLong(MEDIA_POSITION);
        }


        mPlayerView = rootView.findViewById(R.id.playerView);
        mStepDescription = rootView.findViewById(R.id.step_description);
        mStepDescription.setText(step.getDescription());
        noVideo = rootView.findViewById(R.id.no_video);
        setNoVideo(step);
        mTwoPane = rootView.findViewById(R.id.two_pane) != null ;
        return rootView;
    }

    private void setNoVideo(Step step) {
        if(step != null) {
            assert step.getVideoURL() != null;
            if(step.getVideoURL().equals("")) {
                mPlayerView.setVisibility(View.INVISIBLE);
                noVideo.setVisibility(View.VISIBLE);
            } else {
                initializePlayer(Uri.parse(step.getVideoURL()));
            }
        }
    }

    private void initializeMedia() {
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(Objects.requireNonNull(getActivity()), TAG);
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSessionCompat.setMediaButtonReceiver(null);
        PlaybackStateCompat.Builder playbackBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaSessionCompat.setPlaybackState(playbackBuilder.build());
        mediaSessionCompat.setCallback(new SessionCallBacks());
        mediaSessionCompat.setActive(true);
    }

   private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
            String userAgent = Util.getUserAgent(getActivity(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    Objects.requireNonNull(getActivity()), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_VISIBILITY, mPlayerView.getVisibility());
        outState.putLong(MEDIA_POSITION, positionPlayer);
        outState.putBoolean(PLAY_WHEN_READY, playWhenReady);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE && !mTwoPane ) {
            hideSystemUI();
            mStepDescription.setVisibility(View.INVISIBLE);
            mPlayerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            mPlayerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mExoPlayer != null)
        releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mExoPlayer != null) {
            positionPlayer = mExoPlayer.getCurrentPosition();
            playWhenReady = mExoPlayer.getPlayWhenReady();
            releasePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mExoPlayer != null) {
            //resuming properly
            mExoPlayer.setPlayWhenReady(playWhenReady);
            mExoPlayer.seekTo(positionPlayer);
        } else {
            initializeMedia();
            if(step.getVideoURL() != null) initializePlayer(Uri.parse(step.getVideoURL()));
            mExoPlayer.setPlayWhenReady(playWhenReady);
            mExoPlayer.seekTo(positionPlayer);
        }
    }

    private class SessionCallBacks extends MediaSessionCompat.Callback {

        @Override
        public void onPlay() {
            super.onPlay();
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            super.onPause();
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            mExoPlayer.seekTo(0);
        }
    }

    private void hideSystemUI() {

        View decorView = Objects.requireNonNull(getActivity()).getWindow().getDecorView();
        Objects.requireNonNull(getActivity()).getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar != null) actionBar.hide();
    }

}
