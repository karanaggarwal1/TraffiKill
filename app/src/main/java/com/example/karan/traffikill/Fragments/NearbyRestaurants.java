package com.example.karan.traffikill.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.karan.traffikill.Adapters.NearbyPlaceAdapter;
import com.example.karan.traffikill.R;
import com.example.karan.traffikill.models.ResultData;

import java.util.ArrayList;

/**
 * Created by Karan on 28-07-2017.
 */

public class NearbyRestaurants extends Fragment {
    ArrayList<ResultData> restaurantList = new ArrayList<>();
    NearbyPlaceAdapter nearbyPlaceAdapter;
    RecyclerView restaurants;
    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview;
        rootview = inflater.inflate(R.layout.fragment_nearby_restaurants, container, false);
        restaurants = (RecyclerView) rootview.findViewById(R.id.rv_restaurants);
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("dataList") != null) {
            this.restaurantList = savedInstanceState.getParcelableArrayList("dataList");
        }
        restaurants.setHasFixedSize(true);
        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        restaurants.setLayoutManager(gaggeredGridLayoutManager);
        nearbyPlaceAdapter = new NearbyPlaceAdapter(this.context, restaurantList);
        nearbyPlaceAdapter.updateData(this.restaurantList);
        restaurants.setAdapter(nearbyPlaceAdapter);
        return rootview;
    }

    public void updateList(ArrayList<ResultData> nearbyPlaces) {
        if (nearbyPlaceAdapter != null) {
            this.restaurantList = nearbyPlaces;
        }
    }
}
