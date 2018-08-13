package com.example.aspas.songplayerapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
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
 * Created by aspas on 12/01/2017.
 */

public class AddPlaylistMusicFragment extends Fragment {

    FragmentCommunicator fragmentCommunicator;
    ListView listView;
    ArrayList<File> playlistSongList;
    ArrayList<String> formattedSongList;
    ListAdapter listAdapter;
    FloatingActionButton confirmPlaylistMusicButton;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.add_playlist_music_layout, container, false);

        fragmentCommunicator = (FragmentCommunicator) getActivity();
        playlistSongList = fragmentCommunicator.loadAllSongs();
        formattedSongList = new ArrayList<>();
        for(File f : playlistSongList){
            formattedSongList.add(f.getName());
        }

        listView = (ListView) view.findViewById(R.id.addPlaylistMusicListView);
        confirmPlaylistMusicButton = (FloatingActionButton) view.findViewById(R.id.confirmPlaylistMusicButton);

        listAdapter = new ArrayAdapter<>(getActivity(), R.layout.add_playlist_music_list_item_layout, formattedSongList);
        listView.setAdapter(listAdapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(false);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        confirmPlaylistMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = listView.getCheckedItemPositions();


                for(int i = 0; i < checked.size(); i++){
                    int key = checked.keyAt(i);
                    boolean value = checked.get(key);

                    if(value){
                        fragmentCommunicator.updatePlaylist(playlistSongList.get(key));
                        listView.setItemChecked(key, false);
                    }
                }


                getFragmentManager().popBackStack();
            }
        });

        return view;
    }
}