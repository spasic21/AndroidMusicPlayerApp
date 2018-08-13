package com.example.aspas.songplayerapp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by aspas on 11/28/2017.
 */

public interface FragmentCommunicator {

    /**
     * Sets the song index to be played.
     *
     * @param songFile - the song file to be played.
     */
    public void setCurrentSongIndex(File songFile);

    /**
     * Retrieves the current song index to be played.
     *
     * @return - current song index to be played.
     */
    public int getCurrentSongIndex();

    /**
     * Sets the song list to track through.
     *
     * @param songList - the song list to be tracked through.
     */
    public void setSongList(ArrayList<File> songList);

    /**
     * Retrieves the current song list to be used.
     *
     * @return - the current song list.
     */
    public ArrayList<File> getSongList();

    /**
     * Plays the mp3 file given by the song index.
     *
     * @param songIndex - the song index to be played.
     */
    public void playSong(int songIndex);

    /**
     * Updates a playlist with the given song file.
     *
     * @param songName - the song file to put inside a playlist.
     */
    public void updatePlaylist(File songName);

    /**
     * Saves the hashmap that is responsible for keeping track of the playlist information.
     *
     * @param map - The hashmap that contains the playlist information.
     */
    public void savePlaylistHashMap(HashMap<String, ArrayList<File>> map);

    /**
     * Sets the MainActivity hashmap variable that contains the playlist information.
     *
     */
    public void retrievePlaylistHashMapFromFile();

    /**
     * Retrieves the hashmap variable from the MainActivity.
     *
     * @return
     */
    public HashMap<String, ArrayList<File>> getPlaylistHashMap();

    /**
     * Sets the current playlist title to a string variable to be used to access information from
     * the playlist.
     *
     * @param playlistTitle - string variable to be used to access the playlist information.
     */
    public void setPlaylistTitle(String playlistTitle);

    /**
     * Retrieves the current playlist title to be used to access information from the playlist.
     *
     * @return - current playlist title.
     */
    public String getPlaylistTitle();

    /**
     * Loads all of the songs in the "All Music Tab" from internal memory.
     *
     * @return
     */
    public ArrayList<File> loadAllSongs();

    /**
     * Sets the repeat boolean variable to be true or false.
     *
     * @param repeat - boolean variable to repeat a song or not.
     */
    public void setRepeat(boolean repeat);

    /**
     * Retrieves the repeat boolean variable.
     *
     * @return - boolean variable to repeat a song or not.
     */
    public boolean repeatSong();

    /**
     * Sets the shuffle boolean variable to shuffle through the song list.
     *
     * @param shuffle - boolean variable to shuffle through the song list.
     */
    public void setShuffle(boolean shuffle);

    /**
     * Retrieves the shuffle boolean variable.
     *
     * @return - boolean variable to shuffle through the song list.
     */
    public boolean shuffleSongs();

    /**
     * Converts the song duration to a string for a text view to use.
     *
     * @param milliseconds - time in milliseconds to be converted to a string.
     * @return
     */
    public String milliSecondsToTime(long milliseconds);

    /**
     * Retrieves the current song name to be used for a text view.
     *
     * @return - current song name.
     */
    public CharSequence getCurrentSongName();

    /**
     * Retrieves the mediaplayer position of the song.
     *
     * @return - mediaplayer position of the song.
     */
    public int getCurrentSongPosition();

    /**
     * Converts the mediaplayer position into a string to be used in a text view.
     *
     * @return - mediaplayer position of the song in string form.
     */
    public String getCurrentSongTime();

    /**
     * Retrieves the mediaplayer song length to be used to be converted into a string for a text
     * view.
     *
     * @return - mediaplayer song length.
     */
    public int getSongLength();

    /**
     * Converts the mediaplayer song length into a string to be used for a text view.
     *
     * @return - converted mediaplayer song length in string
     */
    public String getSongLengthText();

    /**
     * Plays the current song.
     *
     */
    public void playButtonAction();

    /**
     * Tracks to the previous song in the song list.
     *
     */
    public void previousButtonAction();

    /**
     * Tracks to the next song in the song list.
     *
     */
    public void nextButtonAction();

    /**
     * Checks if the mediaplayer is playing the current song.
     *
     * @return boolean variable if the mediaplayer is playing or not.
     */
    public boolean isMusicPlaying();

    /**
     * Sets the seekbar progress to be used.
     *
     * @param progress - seekbar progress.
     */
    public void setSeekbarProgress(int progress);

    /**
     * Retrieves the seekbar progress to be used.
     *
     * @return - seekbar progress.
     */
    public int getSeekbarProgress();
}
