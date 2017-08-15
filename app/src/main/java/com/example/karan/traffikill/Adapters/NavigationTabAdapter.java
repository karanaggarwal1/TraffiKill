package com.example.karan.traffikill.Adapters;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.karan.traffikill.Fragments.NearbyHotels;
import com.example.karan.traffikill.Fragments.NearbyRestaurants;

import java.util.ArrayList;
import java.util.List;

public class NavigationTabAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragments = new ArrayList<>();
    FragmentManager fragmentManager;

    @Override
    public Parcelable saveState() {
        Bundle retval = new Bundle();
        for (Fragment fragment : mFragments) {
            if (fragment instanceof NearbyHotels) {
                retval.putParcelableArrayList("dataListH", ((NearbyHotels) fragment).getData());
            }
            if (fragment instanceof NearbyRestaurants) {
                retval.putParcelableArrayList("dataListR", ((NearbyRestaurants) fragment).getData());
            }
        }
        return retval;
    }

    public NavigationTabAdapter(FragmentManager manager) {
        super(manager);
        this.fragmentManager = manager;
    }

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

}