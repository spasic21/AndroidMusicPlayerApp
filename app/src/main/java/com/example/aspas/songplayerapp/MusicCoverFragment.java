package com.example.aspas.songplayerapp;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by aspas on 12/10/2017.
 */

public class MusicCoverFragment extends Fragment{

    ImageView repeatButton, shuffleButton, previousButton, playButton, nextButton;
    FragmentCommunicator fragmentCommunicator;
    TextView currentSongName, currentTime, totalTime;
    SeekBar seekBar;
    Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        fragmentCommunicator = (FragmentCommunicator) getActivity();
    }

    /**
     * Creates the view for the user to see.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.music_cover_layout, container, false);

        currentSongName = (TextView) view.findViewById(R.id.currentSongName);
        currentTime = (TextView) view.findViewById(R.id.currentTime);
        totalTime = (TextView) view.findViewById(R.id.totalTime);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        repeatButton = (ImageView) view.findViewById(R.id.repeatButton);
        shuffleButton = (ImageView) view.findViewById(R.id.shuffleButton);
        previousButton = (ImageView) view.findViewById(R.id.previousButton);
        playButton = (ImageView) view.findViewById(R.id.playButton);
        nextButton = (ImageView) view.findViewById(R.id.nextButton);

        setupButtons();

        currentSongName.setText(fragmentCommunicator.getCurrentSongName());
        totalTime.setText(fragmentCommunicator.getSongLengthText());
        seekBar.setMax(fragmentCommunicator.getSongLength() / 1000);
        seekBar.setProgress(fragmentCommunicator.getCurrentSongPosition());

        repeatButton.setOnClickListener(new ImageListener());
        shuffleButton.setOnClickListener(new ImageListener());
        previousButton.setOnClickListener(new ImageListener());
        playButton.setOnClickListener(new ImageListener());
        nextButton.setOnClickListener(new ImageListener());

        if(fragmentCommunicator.isMusicPlaying()){
            playButton.setImageResource(R.drawable.ic_pause);
        }else{
            playButton.setImageResource(R.drawable.ic_play);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {
                if(input){
                    fragmentCommunicator.setSeekbarProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        getActivity().runOnUiThread(updateTimeTask);

        return view;
    }

    /**
     * A thread the would update the seekbar and text views every second.
     *
     */
    private Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            currentSongName.setText(fragmentCommunicator.getCurrentSongName());
            currentTime.setText(fragmentCommunicator.getCurrentSongTime());
            totalTime.setText(fragmentCommunicator.getSongLengthText());
            seekBar.setMax(fragmentCommunicator.getSongLength() / 1000);
            seekBar.setProgress(fragmentCommunicator.getCurrentSongPosition());
            handler.postDelayed(this, 1000);
        }
    };

    /**
     * An inner class that is used to act as an onClickListener for the images in the view.
     *
     */
    private class ImageListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            if(view == shuffleButton){
                if(!fragmentCommunicator.shuffleSongs()){
                    fragmentCommunicator.setShuffle(true);
                    shuffleButton.setColorFilter(Color.BLACK);
                }else{
                    fragmentCommunicator.setShuffle(false);
                    shuffleButton.setColorFilter(Color.LTGRAY);
                }
            }else if(view == repeatButton){
                if (!fragmentCommunicator.repeatSong()){
                    fragmentCommunicator.setRepeat(true);
                    repeatButton.setImageResource(R.drawable.ic_repeat_one_black_24dp);
                }else{
                    fragmentCommunicator.setRepeat(false);
                    repeatButton.setImageResource(R.drawable.ic_repeat_black_24dp);
                }
            }else if(view == previousButton){
                fragmentCommunicator.previousButtonAction();
                currentSongName.setText(fragmentCommunicator.getCurrentSongName());
                totalTime.setText(fragmentCommunicator.getSongLengthText());
            }else if(view == playButton){
                if(fragmentCommunicator.isMusicPlaying()){
                    playButton.setImageResource(R.drawable.ic_play);
                }else{
                    playButton.setImageResource(R.drawable.ic_pause);
                }
                fragmentCommunicator.playButtonAction();
            }else{
                fragmentCommunicator.nextButtonAction();
                currentSongName.setText(fragmentCommunicator.getCurrentSongName());
                totalTime.setText(fragmentCommunicator.getSongLengthText());
            }
        }
    }

    /**
     * Sets up the image buttons to the correct state.
     *
     */
    public void setupButtons(){
        if(fragmentCommunicator.repeatSong()){
            repeatButton.setImageResource(R.drawable.ic_repeat_one_black_24dp);
        }else{
            repeatButton.setImageResource(R.drawable.ic_repeat_black_24dp);
        }

        if(fragmentCommunicator.shuffleSongs()){
            shuffleButton.setColorFilter(Color.BLACK);
        }else{
            shuffleButton.setColorFilter(Color.LTGRAY);
        }
    }
}
