package com.example.aspas.songplayerapp;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by aspas on 11/29/2017.
 */

public class PlaylistMusicListFragment extends Fragment {

    private static FragmentCommunicator fragmentCommunicator;
    private static ArrayList<File> playlistSongList;
    private static ListView listView;
    private static ListAdapter listAdapter;
    HashMap<String, ArrayList<File>> playlistMap;
    ArrayList<String> formattedSongList = new ArrayList<>();
    FloatingActionButton addPlaylistMusicButton;
    AddPlaylistMusicFragment addPlaylistMusicFragment = new AddPlaylistMusicFragment();

    public PlaylistMusicListFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    /**
     * Creates the view to be used for the user to see.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return - view for the user to see.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlist_music_list_layout, container, false);

        fragmentCommunicator = (FragmentCommunicator) getActivity();
        playlistSongList = new ArrayList();
        playlistMap = new HashMap<>();
        listView = (ListView) view.findViewById(R.id.playlistListView);
        addPlaylistMusicButton = (FloatingActionButton) view.findViewById(R.id.addPlaylistMusicButton);
        listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, formattedSongList);

        listView.setAdapter(listAdapter);

        loadPlaylists();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                fragmentCommunicator.setSongList(playlistMap.get(fragmentCommunicator.getPlaylistTitle()));
                fragmentCommunicator.setCurrentSongIndex(playlistSongList.get(i));
                fragmentCommunicator.playSong(fragmentCommunicator.getCurrentSongIndex());
            }
        });

        addPlaylistMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_left, R.anim.slide_right, R.anim.slide_left, R.anim.slide_right);

                fragmentTransaction.replace(R.id.playlistMusicListLayout, addPlaylistMusicFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    /**
     * Updates the data of the current playlist that the user is in.
     *
     * @param songName - song file to be added to the playlist.
     */
    public void updatePlaylist(File songName){
        fragmentCommunicator.retrievePlaylistHashMapFromFile();
        playlistMap = fragmentCommunicator.getPlaylistHashMap();

        playlistSongList.add(songName);
        playlistMap.put(fragmentCommunicator.getPlaylistTitle(), playlistSongList);
        fragmentCommunicator.savePlaylistHashMap(playlistMap);
        formattedSongList.clear();

        for(File f : playlistSongList){
            formattedSongList.add(f.getName());
        }

        ((BaseAdapter)listAdapter).notifyDataSetChanged();
        listView.invalidateViews();
    }

    /**
     * Loads the playlist information from a file to be displayed for the user.
     *
     */
    public void loadPlaylists(){
        fragmentCommunicator.retrievePlaylistHashMapFromFile();
        playlistMap = fragmentCommunicator.getPlaylistHashMap();
        ArrayList<File> files = playlistMap.get(fragmentCommunicator.getPlaylistTitle());

        if(files != null){
            for(File f : files){
                updatePlaylist(f);
            }
        }
    }
}