package com.example.karan.traffikill.Activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.karan.traffikill.R;
import com.example.karan.traffikill.Services.NearbyPlacesAPI;
import com.example.karan.traffikill.models.NearbyPlaces;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RestaurantActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Intent incomingIntent;
    Location receivedLocation;
    NearbyPlacesAPI nearbyPlacesAPI;
    String photoReference;
    private String TAG = "RestaurantActivity";
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        incomingIntent = getIntent();
        receivedLocation = new Location("Hello");
        if (incomingIntent.getType() != null && incomingIntent.getType().equals("restaurant")) {
            receivedLocation.setLatitude(incomingIntent.getDoubleExtra("latitude", 0));
            receivedLocation.setLongitude(incomingIntent.getDoubleExtra("longitude", 0));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView InavigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = InavigationView.getHeaderView(0);
        profileImage = (ImageView) (hView.findViewById(R.id.iv_profile_pic));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LatLng latLng = new LatLng(incomingIntent.getDoubleExtra("latitude", 0), incomingIntent.getDoubleExtra("longitude", 0));
        String location = latLng.latitude + "," + latLng.longitude;
        nearbyPlacesAPI = new NearbyPlacesAPI();
        nearbyPlacesAPI.getNearbyPlacesClient().
                getNearbyPlaces(
                        location,
                        "restaurant").enqueue(new Callback<NearbyPlaces>() {
            @Override
            public void onResponse(Call<NearbyPlaces> call, Response<NearbyPlaces> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.body().getResults().get(0).getGeometry().getLocationData().getLatitude());
                    photoReference = response.body().getResults().get(1).getPhotosData().get(0).getPhotoReference();
                    Log.d(TAG, "onResponse: "+photoReference);
                    String poster="https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&key=AIzaSyCcWcxyuUpiemoDQzHGzJx-yd5jW0Pwt14" +
                            "&photoreference="+photoReference;
                    Picasso.with(RestaurantActivity.this)
                            .load(poster)
                            .fit()
                            .placeholder(R.drawable.ic_placeholder)
                            .error(R.drawable.ic_error)
                            .into(profileImage);
                }
            }

            @Override
            public void onFailure(Call<NearbyPlaces> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getCause());
                Log.d(TAG, "onFailure: " + t.getStackTrace());
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_screen_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_restaurants) {
            return false;
        } else if (id == R.id.nav_hotels) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
