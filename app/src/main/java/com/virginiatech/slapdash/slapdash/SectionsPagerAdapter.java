package com.virginiatech.slapdash.slapdash;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.virginiatech.slapdash.slapdash.EventDisplayFragment.EventDisplayFragment;
import com.virginiatech.slapdash.slapdash.FriendList_Fragment.FriendListFragment;
import com.virginiatech.slapdash.slapdash.Map_Fragment.MapFragment;

/**
 * Created by nima on 10/21/16.
 *
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public static final int MAP_FRAGMENT = 0;
    public static final int EVENT_CREATION_FRAGMENT = 1;
    public static final int FRIENDS_LIST_FRAGMENT = 2;

    private SparseArray<Fragment> mFragmentArray = new SparseArray<>();

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                  Public methods                                         //
    /////////////////////////////////////////////////////////////////////////////////////////////
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    //-------------------------------------------------------------------------------------------

    public Fragment getFragment(int key) {
        return mFragmentArray.get(key);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                       Overrides                                         //
    /////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Fragment getItem(int position) {
        Fragment tempFragment = mFragmentArray.get(position);
        if (tempFragment != null) return tempFragment;

        switch (position) {
            case MAP_FRAGMENT:
                Fragment mapFragment = MapFragment.newInstance();
                mFragmentArray.put(position, mapFragment);
                return mapFragment;
            case EVENT_CREATION_FRAGMENT:
                Fragment eventCreationFragment = EventDisplayFragment.newInstance();
                mFragmentArray.put(position, eventCreationFragment);
                return eventCreationFragment;
            case FRIENDS_LIST_FRAGMENT:
                Fragment friendsListFragment = FriendListFragment.newInstance();
                mFragmentArray.put(position, friendsListFragment);
                return friendsListFragment;
            default:
                return null;
        }
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mFragmentArray.remove(position);
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public int getCount() {
        return 3;
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mFragmentArray.put(position, fragment);
        return fragment;
    }
}
