package com.example.karan.traffikill.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
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
    public static final String TAG = "NearbyRestaurants";
    ArrayList<ResultData> restaurantList = new ArrayList<>();
    NearbyPlaceAdapter nearbyPlaceAdapter;
    RecyclerView restaurants;
    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("dataList") != null) {
            Log.d(TAG, "onCreateView: if clause entered");
            this.restaurantList = savedInstanceState.getParcelableArrayList("dataList");
        }
        if (getArguments() != null && getArguments().getParcelableArrayList("dataList!") != null) {
            Log.d(TAG, "onCreateView: if clause entered getArguments()");
            this.restaurantList = savedInstanceState.getParcelableArrayList("dataList");
        }
    }

    public ArrayList<ResultData> getData() {
        return this.restaurantList;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview;
        Log.d(TAG, "onCreateView: ");
        rootview = inflater.inflate(R.layout.fragment_nearby_restaurants, container, false);
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("dataList") != null) {
            Log.d(TAG, "onCreateView: if clause entered");
            this.restaurantList = savedInstanceState.getParcelableArrayList("dataList");
        }
        if (getArguments() != null && getArguments().getParcelableArrayList("dataList!") != null) {
            Log.d(TAG, "onCreateView: if clause entered getArguments()");
            this.restaurantList = savedInstanceState.getParcelableArrayList("dataList");
        }
        return rootview;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        restaurants = (RecyclerView) view.findViewById(R.id.rv_restaurants);
        restaurants.setHasFixedSize(true);
        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        restaurants.setLayoutManager(gaggeredGridLayoutManager);
        nearbyPlaceAdapter = new NearbyPlaceAdapter(this.context, restaurantList);
        nearbyPlaceAdapter.updateData(this.restaurantList);
        restaurants.setAdapter(nearbyPlaceAdapter);
    }

    public void updateList(ArrayList<ResultData> nearbyPlaces) {
        if (nearbyPlaceAdapter != null) {
            this.restaurantList = nearbyPlaces;
            nearbyPlaceAdapter.updateData(nearbyPlaces);
            nearbyPlaceAdapter.notifyDataSetChanged();
        }
        Log.d(TAG, "updateList: " + this.restaurantList.size());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: RestaurantList " + this.restaurantList.isEmpty());
        Log.d(TAG, "onPause: Adapter " + (this.nearbyPlaceAdapter == null));
        Log.d(TAG, "onPause: RestaurantList " + (this.restaurantList == null));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("dataList", this.restaurantList);
        super.onSaveInstanceState(outState);
    }
}
