package com.example.aspas.songplayerapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by aspas on 11/27/2017.
 */

/**
 * Bonus fragment that I couldn't get to due to time.
 *
 */
public class BonusFragment extends Fragment {

    public BonusFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.not_implemented_tab, container, false);

        return view;
    }
}
