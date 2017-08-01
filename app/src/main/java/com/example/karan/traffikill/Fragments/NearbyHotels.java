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

public class NearbyHotels extends Fragment {
    public static final String TAG = "NearbyHotels";
    ArrayList<ResultData> hotelList = new ArrayList<>();
    NearbyPlaceAdapter nearbyPlaceAdapter;
    RecyclerView hotels;
    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview;
        rootview = inflater.inflate(R.layout.fragment_nearby_hotels, container, false);
        return rootview;
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
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("dataList", this.hotelList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hotels = (RecyclerView) view.findViewById(R.id.rv_hotels);
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("dataList") != null) {
            this.hotelList = savedInstanceState.getParcelableArrayList("dataList");
        }
        hotels.setHasFixedSize(true);
        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        hotels.setLayoutManager(gaggeredGridLayoutManager);
        nearbyPlaceAdapter = new NearbyPlaceAdapter(this.context, hotelList);
        nearbyPlaceAdapter.updateData(this.hotelList);
        hotels.setAdapter(nearbyPlaceAdapter);
    }

    public void updateList(ArrayList<ResultData> nearbyPlaces) {
        if (nearbyPlaceAdapter != null) {
            this.hotelList = nearbyPlaces;
            nearbyPlaceAdapter.updateData(nearbyPlaces);
            nearbyPlaceAdapter.notifyDataSetChanged();
        }
    }
}
