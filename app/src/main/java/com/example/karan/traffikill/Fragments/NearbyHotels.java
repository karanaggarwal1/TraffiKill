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

import com.example.karan.traffikill.Adapters.NearbyHotelsAdapter;
import com.example.karan.traffikill.R;
import com.example.karan.traffikill.models.ResultData;

import java.util.ArrayList;

public class NearbyHotels extends Fragment {
    ArrayList<ResultData> hotelList = new ArrayList<>();
    NearbyHotelsAdapter nearbyPlaceAdapter;
    RecyclerView hotels;
    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<ResultData> getData() {
        return this.hotelList;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview;
        rootview = inflater.inflate(R.layout.fragment_nearby_hotels, container, false);
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("dataList") != null) {
            this.hotelList = savedInstanceState.getParcelableArrayList("dataList");
        }
        if (getArguments() != null && getArguments().getParcelableArrayList("dataList!") != null) {
            assert savedInstanceState != null;
            this.hotelList = savedInstanceState.getParcelableArrayList("dataList");
        }
        return rootview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("dataList", this.hotelList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("dataList") != null) {
            this.hotelList = savedInstanceState.getParcelableArrayList("dataList");
        }
        if (getArguments() != null && getArguments().getParcelableArrayList("dataList!") != null) {
            assert savedInstanceState != null;
            this.hotelList = savedInstanceState.getParcelableArrayList("dataList");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hotels = (RecyclerView) view.findViewById(R.id.rv_hotels);
        hotels.setHasFixedSize(true);
        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        hotels.setLayoutManager(gaggeredGridLayoutManager);
        nearbyPlaceAdapter = new NearbyHotelsAdapter(this.context, hotelList);
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
