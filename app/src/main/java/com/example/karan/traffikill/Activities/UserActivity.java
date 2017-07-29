package com.example.karan.traffikill.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karan.traffikill.Adapters.NavigationTabAdapter;
import com.example.karan.traffikill.Fragments.AboutApp;
import com.example.karan.traffikill.Fragments.CurrentDayForecast;
import com.example.karan.traffikill.Fragments.NearbyHotels;
import com.example.karan.traffikill.Fragments.NearbyRestaurants;
import com.example.karan.traffikill.Fragments.UserProfile;
import com.example.karan.traffikill.Fragments.WeeklyData;
import com.example.karan.traffikill.R;
import com.example.karan.traffikill.Services.WeatherAPI;
import com.example.karan.traffikill.models.CurrentData;
import com.example.karan.traffikill.models.KeyListDaily;
import com.example.karan.traffikill.models.KeyListHourly;
import com.example.karan.traffikill.models.WeatherInfo;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import devlight.io.library.ntb.NavigationTabBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {
    public static final int PERM_REQ_CODE = 345;
    public static final int REQUEST_CHECK_SETTINGS = 456;
    private static final String LAST_APP_VERSION = "1";
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 900000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 600000;
    private static final String TAG = "LocationUpdates";

    protected static FirebaseAuth userAuthentication;
    protected static FirebaseUser currentUser;

    public WeatherAPI weatherAPI;
    NavigationTabAdapter navigationTabAdapter;
    NavigationTabBar navigationTabBar;
    ViewPager viewPager;
    NearbyHotels nearbyHotels;
    NearbyRestaurants nearbyRestaurants;
    AboutApp aboutApp;
    UserProfile userProfile;
    CurrentDayForecast currentDayForecast;
    WeeklyData weeklyData;

    private ImageView profileImage;
    private TextView displayName;
    private TextView displayMail;
    private boolean doubleBackToExitPressedOnce = false;

    private boolean mRequestingLocationUpdates = true;

    private String mLastUpdateTime;

    private Location mCurrentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SettingsClient settingsClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationCallback locationCallback;
    private ArrayList<CurrentData> mCurrentData;
    private ArrayList<KeyListHourly> mHourlyData;
    private ArrayList<KeyListDaily> mDailyData;

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = userAuthentication.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        checkPermission(this, Manifest.permission.INTERNET);
        initUI();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //noinspection ResourceAsColor
            this.getWindow().setStatusBarColor(R.color.transparent);
        }
        userAuthentication = FirebaseAuth.getInstance();
        if (currentUser == null) {
            Intent loginScreen = new Intent(this, LoginActivity.class);
            startActivity(loginScreen);
            finish();
        }
        if (!currentUser.getProviderId().equals("facebook.com") &&
                !currentUser.getProviderId().equals("google.com") &&
                currentUser.isEmailVerified()) {

        }
        if (savedInstanceState != null) {
            updateValuesFromBundle(savedInstanceState);
        } else {
            this.mDailyData = new ArrayList<>();
            this.mHourlyData = new ArrayList<>();
            this.mCurrentData = new ArrayList<>();
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        settingsClient = LocationServices.getSettingsClient(this);
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                Log.d(TAG, "onLocationResult: " + mCurrentLocation.getLatitude() + "::" + mCurrentLocation.getLongitude());
                weatherAPI = new WeatherAPI();
                weatherAPI.getWeatherClient().getWeatherInfo(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()).
                        enqueue(new Callback<WeatherInfo>() {
                            @Override
                            public void onResponse(Call<WeatherInfo> call, Response<WeatherInfo> response) {
                                Log.d(TAG, "onResponse: " + response.isSuccessful());
                                UserActivity.this.mCurrentData.add(response.body().getCurrently());
                                Log.d(TAG, "onResponse: " + response.body().getCurrently().getSummary());
                                UserActivity.this.mHourlyData.add(response.body().getHourly());
                                Log.d(TAG, "onResponse: " + response.body().getHourly().getSummary());
                                UserActivity.this.mDailyData.add(response.body().getDaily());
                                Log.d(TAG, "onResponse: " + response.body().getDaily().getSummary());
                            }

                            @Override
                            public void onFailure(Call<WeatherInfo> call, Throwable t) {
                                Log.d(TAG, "onFailure: " + call.isCanceled());
                                Log.d(TAG, "onFailure: " + t.getCause());
                            }
                        });
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                Log.d(TAG, "onLocationResult: " + mLastUpdateTime);
            }
        };
    }

    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            return;
        }
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            Toast.makeText(this, "Enable Permissions for Location Services", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocationUpdates() {
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        if (ActivityCompat.checkSelfPermission(UserActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(UserActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                                locationCallback, Looper.myLooper());
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(UserActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {

                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Toast.makeText(UserActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
        }
    }

    public void checkPermission(Context context, String perm) {

        if (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{perm}, PERM_REQ_CODE);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, perm)) {
            Toast.makeText(context, "Give the permission please.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    private void initUI() {
        viewPager = (ViewPager) findViewById(R.id.navFragContainer);
        navigationTabAdapter = new NavigationTabAdapter(getSupportFragmentManager());
        aboutApp = new AboutApp();
        weeklyData = new WeeklyData();
        currentDayForecast = new CurrentDayForecast();
        nearbyHotels = new NearbyHotels();
        nearbyRestaurants = new NearbyRestaurants();
        userProfile = new UserProfile();
        userProfile.setContext(this);
        navigationTabAdapter.addFragment(currentDayForecast);
        navigationTabAdapter.addFragment(weeklyData);
        navigationTabAdapter.addFragment(userProfile);
        navigationTabAdapter.addFragment(nearbyRestaurants);
        navigationTabAdapter.addFragment(nearbyHotels);
        navigationTabAdapter.addFragment(aboutApp);
        viewPager.setAdapter(navigationTabAdapter);
        final String[] colors = getResources().getStringArray(R.array.vertical_ntb);
        navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_first),
                        Color.parseColor(colors[0]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_first))
                        .title("Forecast")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_second),
                        Color.parseColor(colors[0]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_second))
                        .title("This Week")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_fifth),
                        Color.parseColor(colors[3]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_fifth))
                        .title("Profile")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_third),
                        Color.parseColor(colors[1]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_third))
                        .title("Restaurants")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_fourth),
                        Color.parseColor(colors[2]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_fourth))
                        .title("Hotels")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_sixth),
                        Color.parseColor(colors[4]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_sixth))
                        .title("About")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.deselect();
        navigationTabBar.setViewPager(viewPager, 2);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                navigationTabBar.getModels().get(position).hideBadge();
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

        navigationTabBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
                    navigationTabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            model.showBadge();
                        }
                    }, i * 100);
                }
            }
        }, 500);
    }

}
