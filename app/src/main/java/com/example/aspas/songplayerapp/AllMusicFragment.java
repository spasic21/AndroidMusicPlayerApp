package com.example.aspas.songplayerapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by aspas on 11/27/2017.
 */

/**
 * A tab fragment that displays all of the songs that are loaded from internal memory.
 */
public class AllMusicFragment extends Fragment {

    FragmentCommunicator fragmentCommunicator;
    ListView listView;
    ArrayList<File> songList;
    ArrayList<String> formattedSongList;
    ListAdapter listAdapter;

    public AllMusicFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_music_layout, container, false);

        fragmentCommunicator = (FragmentCommunicator) getActivity();
        listView = (ListView) view.findViewById(R.id.listView);

        songList = fragmentCommunicator.loadAllSongs();
        formattedSongList = new ArrayList<>();
        for(File f : songList){
            formattedSongList.add(f.getName());
        }

        listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, formattedSongList);

        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                fragmentCommunicator.setSongList(songList);
                fragmentCommunicator.playSong(i);
            }
        });

        return view;
    }
}
