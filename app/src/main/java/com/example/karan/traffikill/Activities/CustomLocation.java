package com.example.karan.traffikill.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.karan.traffikill.Adapters.WeatherItemsAdapter;
import com.example.karan.traffikill.R;
import com.example.karan.traffikill.Services.WeatherAPI;
import com.example.karan.traffikill.models.CurrentData;
import com.example.karan.traffikill.models.WeatherInfo;
import com.gigamole.navigationtabstrip.NavigationTabStrip;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomLocation extends AppCompatActivity {
    RecyclerView weatherList;
    WeatherItemsAdapter weatherItemsAdapter;
    ArrayList<CurrentData> currentList;
    ArrayList<CurrentData> hourlyList;
    WeatherAPI weatherAPI;
    ProgressBar progressBar;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_weekly_data);
        progressBar = (ProgressBar) findViewById(R.id.list_loading);
        relativeLayout = (RelativeLayout) findViewById(R.id.content_holder);
        progressBar.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.GONE);
        currentList = new ArrayList<>();
        hourlyList = new ArrayList<>();
        weatherAPI = new WeatherAPI();
        weatherList = (RecyclerView) findViewById(R.id.weatherCards);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        weatherList.setLayoutManager(linearLayoutManager);
        weatherItemsAdapter = new WeatherItemsAdapter(this);
        weatherItemsAdapter.setView(findViewById(R.id.weatherPreview));
        weatherItemsAdapter.setType("currently");
        weatherItemsAdapter.setView(findViewById(R.id.weatherPreview));
        weatherItemsAdapter.updateData(this.currentList, "currently");
        weatherList.setAdapter(weatherItemsAdapter);
        weatherAPI.getWeatherClient().getWeatherInfo(getIntent().getDoubleExtra("latitude", 0.0),
                getIntent().getDoubleExtra("longitude", 0)).enqueue(new Callback<WeatherInfo>() {
            @Override
            public void onResponse(Call<WeatherInfo> call, Response<WeatherInfo> response) {
                if (response.isSuccessful()) {
                    currentList.add(response.body().getCurrently());
                    currentList.addAll(response.body().getHourly().getData());
                    progressBar.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                    weatherList.setAdapter(null);
                    weatherItemsAdapter.updateData(currentList, "currently");
                    weatherList.setAdapter(weatherItemsAdapter);
                }
            }

            @Override
            public void onFailure(Call<WeatherInfo> call, Throwable t) {
                Toast.makeText(CustomLocation.this, t.getCause().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        weatherAPI.getWeatherClient().getWeatherInfo(getIntent().getDoubleExtra("latitude", 0.0),
                getIntent().getDoubleExtra("longitude", 0), "hourly").enqueue(new Callback<WeatherInfo>() {
            @Override
            public void onResponse(Call<WeatherInfo> call, Response<WeatherInfo> response) {
                if (response.isSuccessful()) {
                    hourlyList.addAll(response.body().getHourly().getData());
                    progressBar.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                    weatherList.setAdapter(null);
                    weatherItemsAdapter.updateData(hourlyList, "hourly");
                    weatherList.setAdapter(weatherItemsAdapter);
                }
            }

            @Override
            public void onFailure(Call<WeatherInfo> call, Throwable t) {
                Toast.makeText(CustomLocation.this, t.getCause().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        final NavigationTabStrip navigationTabStrip = (NavigationTabStrip) findViewById(R.id.navTabStrip);
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
                    ArrayList<CurrentData> currentList = CustomLocation.this.currentList;
                    weatherItemsAdapter.updateData(currentList, "currently");
                }
            }

            @Override
            public void onEndTabSelected(String title, int index) {
                if (title.equals("THIS WEEK")) {
                    weatherItemsAdapter.setType("hourly");
                    ArrayList<CurrentData> currentList = CustomLocation.this.hourlyList;
                    weatherItemsAdapter.updateData(currentList, "hourly");
                }
            }
        });
    }
}
