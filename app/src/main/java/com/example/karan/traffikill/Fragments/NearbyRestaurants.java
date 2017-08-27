package com.example.karan.traffikill.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.karan.traffikill.Adapters.NearbyPlaceAdapter;
import com.example.karan.traffikill.R;
import com.example.karan.traffikill.models.ResultData;

import java.util.ArrayList;

public class NearbyRestaurants extends Fragment {
    static RecyclerView restaurants;
    static ProgressBar progressBar;
    ArrayList<ResultData> restaurantList = new ArrayList<>();
    NearbyPlaceAdapter nearbyPlaceAdapter;
    Context context;

    public static void activate_list() {
        restaurants.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("dataList") != null) {
            this.restaurantList = savedInstanceState.getParcelableArrayList("dataList");
        }
        if (getArguments() != null && getArguments().getParcelableArrayList("dataList!") != null) {
            assert savedInstanceState != null;
            this.restaurantList = savedInstanceState.getParcelableArrayList("dataList");
        }
    }

    public ArrayList<ResultData> getData() {
        return this.restaurantList;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview;
        rootview = inflater.inflate(R.layout.fragment_nearby_restaurants, container, false);
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("dataList") != null) {
            this.restaurantList = savedInstanceState.getParcelableArrayList("dataList");
        }
        if (getArguments() != null && getArguments().getParcelableArrayList("dataList!") != null) {
            assert savedInstanceState != null;
            this.restaurantList = savedInstanceState.getParcelableArrayList("dataList");
        }
        return rootview;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        restaurants = (RecyclerView) view.findViewById(R.id.rv_restaurants);
        progressBar = (ProgressBar) view.findViewById(R.id.list_loading);
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
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("dataList", this.restaurantList);
        super.onSaveInstanceState(outState);
    }
}
