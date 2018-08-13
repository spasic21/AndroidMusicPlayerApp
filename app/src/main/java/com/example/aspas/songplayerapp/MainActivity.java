package com.example.aspas.songplayerapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;

public class MainActivity extends AppCompatActivity implements FragmentCommunicator{

    RelativeLayout currentSongLayout;
    PlaylistMusicListFragment playlistMusicListFragment = new PlaylistMusicListFragment();
    Fragment musicCoverFragment = new MusicCoverFragment();
    PagerAdapter adapter;
    MediaPlayer mediaPlayer = new MediaPlayer();
    ArrayList<File> songList;
    HashMap<String, ArrayList<File>> playlistMap;
    SeekBar seekBar;
    TabLayout tabLayout;
    TextView currentTime, totalTime, currentSongName, currentSinger;
    Handler handler = new Handler();
    ImageView playButton, previousButton, nextButton;
    int currentSongIndex = 0;
    String playlistTitle;
    AudioManager audioManager;
    boolean repeatSong = false, shuffleSongs = false;

    /**
     * Loads the contents of the MainActivity view and implements functionality for the various
     * components.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songList = loadAllSongs();
        playlistMap = new HashMap<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        currentTime = (TextView) findViewById(R.id.currentTime);
        totalTime = (TextView) findViewById(R.id.totalTime);
        currentSongName = (TextView) findViewById(R.id.currentSongName);
        currentSongLayout = (RelativeLayout) findViewById(R.id.currentSongLayout);
        playButton = (ImageView) findViewById(R.id.playButton);
        previousButton = (ImageView) findViewById(R.id.previousButton);
        nextButton = (ImageView) findViewById(R.id.nextButton);

        currentSongName.setSelected(true);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("All Music Tab"));
        tabLayout.addTab(tabLayout.newTab().setText("Playlists Tab"));
        tabLayout.addTab(tabLayout.newTab().setText("Bonus Tab"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if(focusChange == AUDIOFOCUS_LOSS_TRANSIENT){
                    mediaPlayer.pause();
                    playButton.setImageResource(R.drawable.ic_play);
                }else if(focusChange == AUDIOFOCUS_GAIN){
                    mediaPlayer.start();
                    playButton.setImageResource(R.drawable.ic_pause);
                }else if(focusChange == AUDIOFOCUS_LOSS){
                    mediaPlayer.pause();
                    playButton.setImageResource(R.drawable.ic_play);
                }
            }
        };

        int audioResult = audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if(audioResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            mediaPlayer.start();
        }else{
            mediaPlayer.pause();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        currentSongLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.animator_slide_up, R.animator.animator_slide_down, R.animator.animator_slide_up, R.animator.animator_slide_down);

                fragmentTransaction.replace(R.id.activity_main, musicCoverFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {
                if(mediaPlayer != null && input){
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playButtonAction();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextButtonAction();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousButtonAction();
            }
        });
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    /**
     * Updates the seekbar progress every second.
     */
    private Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            if(mediaPlayer != null){
                currentTime.setText(milliSecondsToTime(mediaPlayer.getCurrentPosition()));
                seekBar.setProgress(mediaPlayer.getCurrentPosition()/1000);
            }

            handler.postDelayed(this, 1000);
        }
    };

    //////////////////////////// The methods below are from the FragmentCommunicator interface ///////////////////////////////////////////

    @Override
    public String milliSecondsToTime(long milliseconds){
        String finalTime = "";
        String secondsString = "";

        int hours = (int)(milliseconds / (1000 * 60 * 60));
        int minutes = (int)(milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int)((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if(hours > 0){
            finalTime = hours + ":";
        }

        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;
        }

        finalTime = finalTime + minutes + ":" + secondsString;

        return finalTime;
    }

    @Override
    public ArrayList<File> loadAllSongs(){
        final int PERMISSION_REQUEST_CODE = 1;
        ArrayList<File> newSongList = new ArrayList<>();
        File filePath;
        File[] dirSongList;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                Toast.makeText(this, "Permission Denied to access internal files!", Toast.LENGTH_LONG).show();
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }else{
                filePath = new File(Environment.getExternalStorageDirectory() + "/Download");
                dirSongList = filePath.listFiles();

                for(File f : dirSongList){
                    if(f.getName().contains(".mp3")){
                        newSongList.add(f);
                    }
                }
            }
        }

        return newSongList;
    }

    @Override
    public void setCurrentSongIndex(File songFile){
        this.currentSongIndex = songList.indexOf(songFile);
    }

    @Override
    public int getCurrentSongIndex(){
        return currentSongIndex;
    }

    @Override
    public void setSongList(ArrayList<File> songList){
        this.songList = songList;
    }

    @Override
    public ArrayList<File> getSongList(){
        return songList;
    }

    @Override
    public void playSong(int songIndex){
        try{
//            sendNotification(songIndex);
            mediaPlayer.stop();
            mediaPlayer.reset();

            currentSongIndex = songIndex;
            File filePath = songList.get(currentSongIndex);

            FileInputStream fileInputStream = new FileInputStream(filePath);

            mediaPlayer.setDataSource(fileInputStream.getFD());
            mediaPlayer.prepare();

            long totalDuration = mediaPlayer.getDuration();
            long currentPosition = mediaPlayer.getCurrentPosition();

            seekBar.setProgress(0);
            currentTime.setText(milliSecondsToTime(currentPosition));
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            totalTime.setText(milliSecondsToTime(totalDuration));
            currentSongName.setText(songList.get(songIndex).getName());

            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    seekBar.setProgress(0);

                    if(!mediaPlayer.isPlaying()){
                        if(repeatSong){
                            playSong(currentSongIndex);
                        }else if(shuffleSongs){
                            Random rand = new Random();
                            currentSongIndex = rand.nextInt((songList.size() - 1) + 1);
                            playSong(currentSongIndex);
                        }else if(repeatSong && shuffleSongs){
                            Random rand = new Random();
                            currentSongIndex = rand.nextInt((songList.size() - 1) + 1);
                            playSong(currentSongIndex);
                        }else{
                            if(currentSongIndex < (songList.size() - 1)){
                                playSong(currentSongIndex + 1);
                            }else{
                                playSong(0);
                            }
                        }
                    }
                }
            });

            currentSongLayout.setVisibility(View.VISIBLE);
            playButton.setImageResource(R.drawable.ic_pause);
            MainActivity.this.runOnUiThread(updateTimeTask);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void updatePlaylist(File songName){
      playlistMusicListFragment.updatePlaylist(songName);
    }

    @Override
    public void savePlaylistHashMap(HashMap<String, ArrayList<File>> map){
        try{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(this, "Permission Denied to access internal files!", Toast.LENGTH_LONG).show();
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

                    requestPermissions(permissions, 1);
                }else{
                    File playlistFile = new File(Environment.getExternalStorageDirectory(), "playlist_file.txt");

                    if(!playlistFile.exists()){
                        playlistFile.createNewFile();
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(playlistFile);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

                    objectOutputStream.writeObject(map);

                    fileOutputStream.close();
                    objectOutputStream.close();
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void retrievePlaylistHashMapFromFile(){
        try{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(this, "Permission Denied to access internal files!", Toast.LENGTH_LONG).show();
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

                    requestPermissions(permissions, 1);
                }else{
                    File playlistFile = new File(Environment.getExternalStorageDirectory(), "playlist_file.txt");
                    FileInputStream inputStream = new FileInputStream(playlistFile);
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                    playlistMap = (HashMap<String, ArrayList<File>>) objectInputStream.readObject();

                    inputStream.close();
                    objectInputStream.close();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    @Override
    public HashMap<String, ArrayList<File>> getPlaylistHashMap(){
        return playlistMap;
    }

    @Override
    public void setPlaylistTitle(String playlistTitle){
        this.playlistTitle = playlistTitle;
    }

    @Override
    public String getPlaylistTitle(){
        return playlistTitle;
    }

    public void sendNotification(int songIndex){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        String channelName = "SpasChannel";
        final String CHANNEL_ID = "my_channel_01";
        int buildVersion = Build.VERSION.SDK_INT;


        if(buildVersion >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            notificationChannel.setDescription("This is a notification!");
            notificationChannel.setLightColor(Color.CYAN);
            notificationManager.createNotificationChannel(notificationChannel);


            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(getPackageName())
                    .setContentText("This is testing notification!")
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.megaman);

            notificationManager.notify(101, notificationBuilder.build());
        }else{
            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(songList.get(songIndex).getName())
                    .setSmallIcon(R.drawable.megaman)
                    .setOngoing(true);

            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            notification.setContentIntent(pendingIntent);
            notificationManager.notify(0, notification.build());
        }
    }

    @Override
    public void setRepeat(boolean repeat){
        repeatSong = repeat;
    }

    @Override
    public boolean repeatSong(){
        return repeatSong;
    }

    @Override
    public void setShuffle(boolean shuffle){
        shuffleSongs = shuffle;
    }

    @Override
    public boolean shuffleSongs(){
        return shuffleSongs;
    }

    @Override
    public CharSequence getCurrentSongName(){
        return currentSongName.getText();
    }

    @Override
    public int getCurrentSongPosition(){
        return mediaPlayer.getCurrentPosition() / 1000;
    }

    @Override
    public String getCurrentSongTime(){
        return milliSecondsToTime(mediaPlayer.getCurrentPosition());
    }

    @Override
    public int getSongLength(){
        return mediaPlayer.getDuration();
    }

    @Override
    public String getSongLengthText(){
        return milliSecondsToTime(mediaPlayer.getDuration());
    }

    @Override
    public void playButtonAction(){
        if(mediaPlayer.isPlaying()){
            playButton.setImageResource(R.drawable.ic_play);
            mediaPlayer.pause();
        }else{
            playButton.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }
    }

    @Override
    public void previousButtonAction(){
        if(repeatSong){
            playSong(currentSongIndex);
        }else{
            if(currentSongIndex > 0){
                playSong(currentSongIndex - 1);
            }else{
                playSong(songList.size() - 1);
            }
        }
    }

    @Override
    public void nextButtonAction(){
        if(repeatSong){
            playSong(currentSongIndex);
        }else if(shuffleSongs){
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songList.size() - 1) + 1);
            playSong(currentSongIndex);
        }else if(repeatSong && shuffleSongs){
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songList.size() - 1) + 1);
            playSong(currentSongIndex);
        }else{
            if(currentSongIndex < (songList.size() - 1)){
                playSong(currentSongIndex + 1);
            }else{
                playSong(0);
            }
        }
    }

    @Override
    public boolean isMusicPlaying(){
        return mediaPlayer.isPlaying();
    }

    @Override
    public void setSeekbarProgress(int progress){
        mediaPlayer.seekTo(progress * 1000);
    }

    @Override
    public int getSeekbarProgress(){
        return seekBar.getProgress();
    }
}