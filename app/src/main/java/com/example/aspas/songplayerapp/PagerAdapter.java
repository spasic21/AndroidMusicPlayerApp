package com.example.aspas.songplayerapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by aspas on 11/27/2017.
 */

/**
 * Custom page adaptor that is used for the tab layout.
 *
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    int tabCount;

    public PagerAdapter(FragmentManager fm, int numberOfTabs){
        super(fm);
        this.tabCount = numberOfTabs;
    }

    /**
     * Retrieves the tab item
     *
     * @param position - position of the tab fragment.
     * @return - New fragment.
     */
    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                AllMusicFragment allMusicFragment = new AllMusicFragment();
                return allMusicFragment;
            case 1:
                PlaylistFragment playlistFragment = new PlaylistFragment();
                return playlistFragment;
            case 2:
                BonusFragment bonusFragment = new BonusFragment();
                return bonusFragment;
            case 3:
                PlaylistMusicListFragment playlistMusicListFragment = new PlaylistMusicListFragment();
                return playlistMusicListFragment;
            default:
                return null;
        }
    }

    /**
     * Retrieves the total tab count.
     *
     * @return - tab count.
     */
    @Override
    public int getCount(){
        return tabCount;
    }
}
