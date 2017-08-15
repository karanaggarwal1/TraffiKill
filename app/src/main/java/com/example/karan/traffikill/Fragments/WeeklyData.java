package com.example.karan.traffikill.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.karan.traffikill.Adapters.WeatherItemsAdapter;
import com.example.karan.traffikill.R;
import com.example.karan.traffikill.models.CurrentData;
import com.gigamole.navigationtabstrip.NavigationTabStrip;

import java.util.ArrayList;

public class WeeklyData extends Fragment {

    View rootview;
    ArrayList<CurrentData> hourlyList = new ArrayList<>();
    ArrayList<CurrentData> currentList = new ArrayList<>();
    WeatherItemsAdapter weatherItemsAdapter;
    RecyclerView weatherList;
    Context context;

    public void setContext(Context context) {
        this.context = context;
        weatherItemsAdapter = new WeatherItemsAdapter(context);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_weekly_data, container, false);
        final NavigationTabStrip navigationTabStrip = (NavigationTabStrip) rootview.findViewById(R.id.navTabStrip);
        weatherList = (RecyclerView) rootview.findViewById(R.id.weatherCards);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        weatherList.setLayoutManager(linearLayoutManager);
        weatherItemsAdapter = new WeatherItemsAdapter(this.context);
        weatherItemsAdapter.setView(rootview.findViewById(R.id.weatherPreview));
        weatherItemsAdapter.setType("currently");
        weatherItemsAdapter.setView(rootview.findViewById(R.id.weatherPreview));
        weatherItemsAdapter.updateData(this.currentList, "currently");
        weatherList.setAdapter(weatherItemsAdapter);
        navigationTabStrip.setTitles("TODAY", "THIS WEEK");
        navigationTabStrip.setTabIndex(0, true);
        navigationTabStrip.setTitleSize(35);
        navigationTabStrip.setStripColor(Color.RED);
        navigationTabStrip.setStripWeight(6);
        navigationTabStrip.setStripFactor(2);
        navigationTabStrip.setStripType(NavigationTabStrip.StripType.LINE);
        navigationTabStrip.setStripGravity(NavigationTabStrip.StripGravity.BOTTOM);
        navigationTabStrip.setTypeface("fonts/Roboto-Thin.ttf");
        navigationTabStrip.setCornersRadius(3);
        navigationTabStrip.setAnimationDuration(300);
        navigationTabStrip.setInactiveColor(Color.GRAY);
        navigationTabStrip.setActiveColor(Color.BLACK);
        navigationTabStrip.setOnTabStripSelectedIndexListener(new NavigationTabStrip.OnTabStripSelectedIndexListener() {
            @Override
            public void onStartTabSelected(String title, int index) {
                if (title.equals("TODAY")) {
                    weatherItemsAdapter.setType("currently");
                    ArrayList<CurrentData> currentList = WeeklyData.this.currentList;
                    weatherItemsAdapter.updateData(currentList, "currently");
                }
            }

            @Override
            public void onEndTabSelected(String title, int index) {
                if (title.equals("THIS WEEK")) {
                    weatherItemsAdapter.setType("hourly");
                    ArrayList<CurrentData> currentList = WeeklyData.this.hourlyList;
                    weatherItemsAdapter.updateData(currentList, "hourly");
                }
            }
        });
        return rootview;
    }

    public void updateList(ArrayList<CurrentData> mCurrentData, String type) {
        if (weatherItemsAdapter != null) {
            if (type.equals("currently")) {
                this.currentList = mCurrentData;
            } else {
                this.hourlyList = mCurrentData;
            }
        }
    }
}
