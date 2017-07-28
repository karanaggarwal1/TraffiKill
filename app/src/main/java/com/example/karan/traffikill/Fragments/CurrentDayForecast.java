package com.example.karan.traffikill.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.karan.traffikill.R;

/**
 * Created by Karan on 28-07-2017.
 */

public class CurrentDayForecast extends Fragment{
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview;
        rootview = inflater.inflate(R.layout.fragment_current_forecast,container,false);

        return rootview;
    }
}
