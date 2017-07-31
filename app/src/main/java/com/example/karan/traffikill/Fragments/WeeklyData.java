package com.example.karan.traffikill.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.karan.traffikill.Adapters.HourlyDataAdapter;
import com.example.karan.traffikill.R;
import com.example.karan.traffikill.models.DataItemsHourly;

import java.util.ArrayList;

/**
 * Created by Karan on 28-07-2017.
 */

public class WeeklyData extends Fragment {
    ArrayList<DataItemsHourly> hourlyArrayList;
    RecyclerView recyclerView;
    HourlyDataAdapter slAdapter;
    View rootview;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_weekly_data, container, false);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.weeklyData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        hourlyArrayList = getArguments().getParcelableArrayList("dataList");
        if (hourlyArrayList != null) {
            slAdapter = new HourlyDataAdapter(hourlyArrayList, getContext());
            recyclerView.setAdapter(slAdapter);
        }
        return rootview;
    }

    public void updateList(ArrayList<DataItemsHourly> updatedList) {
        ArrayList<DataItemsHourly> hourlyArrayList = updatedList;
        slAdapter = new HourlyDataAdapter(hourlyArrayList, getContext());
        recyclerView = (RecyclerView) rootview.findViewById(R.id.weeklyData);
        recyclerView.setAdapter(slAdapter);
        slAdapter.notifyDataSetChanged();
    }
}
