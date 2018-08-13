package com.example.aspas.songplayerapp;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aspas on 11/27/2017.
 */

public class PlaylistFragment extends Fragment {

    GridView gridView;
    GridViewAdapter gridAdapter;
    FragmentCommunicator fragmentCommunicator;
    Fragment playlistMusicListFragment = new PlaylistMusicListFragment();
    FloatingActionButton addPlaylistButton;
    List<ImageItem> playlistCovers;
    HashMap<String, ArrayList<File>> playlistMap;
    TypedArray imgs;
    Bitmap bitmap;

    public PlaylistFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.playlist_layout, container, false);

        fragmentCommunicator = (FragmentCommunicator) getActivity();
        playlistCovers = new ArrayList<>();
        playlistMap = new HashMap<>();
        imgs = getResources().obtainTypedArray(R.array.image_ids);
        bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(0, -1));


        gridView = (GridView) view.findViewById(R.id.gridView);
        addPlaylistButton = (FloatingActionButton) view.findViewById(R.id.addPlaylistButton);

        gridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, playlistCovers);
        gridView.setAdapter(gridAdapter);

        loadPlaylists();

        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fragmentCommunicator.setPlaylistTitle(playlistCovers.get(i).getTitle());

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);

                fragmentTransaction.replace(R.id.playlist_layout, playlistMusicListFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                fragmentCommunicator.setPlaylistTitle(playlistCovers.get(i).getTitle());
                PopupMenu popupMenu = new PopupMenu(getActivity(), gridView);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity()).setTitle("Delete Playlist");
                        alertBuilder.setMessage("Are you sure you want to delete playlist?");

                        alertBuilder.setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int num) {
                                deletePlaylist(fragmentCommunicator.getPlaylistTitle(), i);
                            }
                        });

                        alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        Dialog dialog = alertBuilder.create();
                        dialog.show();

                        return true;
                    }
                });

                popupMenu.show();

                return true;
            }
        });

        addPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = (LayoutInflater.from(getActivity())).inflate(R.layout.user_input, null);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity()).setTitle("Create Playlist");
                alertBuilder.setView(dialogView);
                final EditText userInput = (EditText) dialogView.findViewById(R.id.userInput);

                alertBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), userInput.getText() + " Playlist Created!", Toast.LENGTH_LONG).show();
                        playlistMap.put(userInput.getText().toString(), new ArrayList<File>());
                        fragmentCommunicator.savePlaylistHashMap(playlistMap);
                        addToPlaylistList(userInput.getText().toString());
                    }
                });

                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                Dialog dialog = alertBuilder.create();
                dialog.show();
            }
        });

        return view;
    }

    /**
     * Adds a new playlist cover in the grid view.
     *
     * @param title - Title of the newly created playlist.
     */
    public void addToPlaylistList(String title){
        playlistCovers.add(playlistCovers.size(), new ImageItem(bitmap, title));
        gridAdapter.notifyDataSetChanged();
        gridView.invalidateViews();
    }

    /**
     * Loads the playlist cover information from a file to be placed in a grid view.
     */
    public void loadPlaylists(){
        fragmentCommunicator.retrievePlaylistHashMapFromFile();
        playlistMap = fragmentCommunicator.getPlaylistHashMap();

        for(Map.Entry<String, ArrayList<File>> entry : playlistMap.entrySet()){
            playlistCovers.add(playlistCovers.size(), new ImageItem(bitmap, entry.getKey()));
        }
    }

    /**
     * Deletes a playlist from the grid view.
     *
     * @param title - Title of the playlist.
     * @param index - Index of the playlist cover to be deleted.
     */
    public void deletePlaylist(String title, int index){
        playlistMap.remove(title);
        playlistCovers.remove(index);
        fragmentCommunicator.savePlaylistHashMap(playlistMap);
        gridAdapter.notifyDataSetChanged();
        gridView.invalidateViews();
    }
}