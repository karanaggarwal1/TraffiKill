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
import android.widget.Toast;

import com.example.karan.traffikill.Adapters.NavigationTabAdapter;
import com.example.karan.traffikill.Fragments.AboutApp;
import com.example.karan.traffikill.Fragments.NearbyHotels;
import com.example.karan.traffikill.Fragments.NearbyRestaurants;
import com.example.karan.traffikill.Fragments.UserProfile;
import com.example.karan.traffikill.Fragments.WeeklyData;
import com.example.karan.traffikill.R;
import com.example.karan.traffikill.Services.NearbyPlacesAPI;
import com.example.karan.traffikill.Services.WeatherAPI;
import com.example.karan.traffikill.models.CurrentData;
import com.example.karan.traffikill.models.KeyListDaily;
import com.example.karan.traffikill.models.NearbyPlaces;
import com.example.karan.traffikill.models.ResultData;
import com.example.karan.traffikill.models.WeatherInfo;
import com.facebook.login.LoginManager;
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
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 900000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 600000;
    private static final String TAG = "LocationUpdates";
    public static Location mCurrentLocation;
    public static ArrayList<CurrentData> mCurrentData;
    public static ArrayList<CurrentData> mHourlyData;
    public static ArrayList<KeyListDaily> mDailyData;
    public static ArrayList<ResultData> nearbyRestaurantList;
    public static ArrayList<ResultData> nearbyHotelList;
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
    Bundle weeklyForecastArguments = new Bundle(), restaurantArguments = new Bundle(), hotelArguments = new Bundle();
    WeeklyData weeklyData;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean mRequestingLocationUpdates = true;
    private String mLastUpdateTime;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SettingsClient settingsClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationCallback locationCallback;
//    private String x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        checkPermission(this, Manifest.permission.INTERNET);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //noinspection ResourceAsColor
            this.getWindow().setStatusBarColor(R.color.transparent);
        }
        userAuthentication = FirebaseAuth.getInstance();
        currentUser = userAuthentication.getCurrentUser();
        if (currentUser == null) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Intent loginScreen = new Intent(this, LoginActivity.class);
            startActivity(loginScreen);
            finish();
        }
        initUI();

        UserActivity.mDailyData = new ArrayList<>();
        UserActivity.mHourlyData = new ArrayList<>();
        UserActivity.mCurrentData = new ArrayList<>();
        UserActivity.nearbyRestaurantList = new ArrayList<>();
        UserActivity.nearbyHotelList = new ArrayList<>();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        settingsClient = LocationServices.getSettingsClient(this);
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    private void getNearbyPlaces(String type) {
        final String x = type;
        NearbyPlacesAPI nearbyPlacesAPI = new NearbyPlacesAPI();
        String location = mCurrentLocation.getLatitude() + "," +
                mCurrentLocation.getLongitude();
        nearbyPlacesAPI.getNearbyPlacesClient().getNearbyPlaces(
                location,
                type,
                50000)
                .enqueue(new Callback<NearbyPlaces>() {
                    @Override
                    public void onResponse(Call<NearbyPlaces> call, Response<NearbyPlaces> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "onResponse: " + response.errorBody());
                            Log.d(TAG, "onResponse: " + response.body().getResults().get(0).getRating());
                            Log.d(TAG, "onResponse: " + response.message());
                            Log.d(TAG, "onResponse: " + response.code());
                            Log.d(TAG, "onResponse: " + response.raw());

                            if (x.equals("restaurant")) {
                                UserActivity.nearbyRestaurantList.addAll(response.body().getResults());
                                UserActivity.this.nearbyRestaurants.updateList(UserActivity.nearbyRestaurantList);
                            } else if (x.equals("lodging")) {
                                UserActivity.nearbyHotelList.addAll(response.body().getResults());
                                UserActivity.this.nearbyHotels.updateList(UserActivity.nearbyHotelList);
                            } else {
                                UserActivity.nearbyRestaurantList.addAll(response.body().getResults());
                                UserActivity.this.nearbyRestaurants.updateList(UserActivity.nearbyRestaurantList);
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<NearbyPlaces> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getCause() + "\n" + t.getMessage());
                        t.printStackTrace();
                    }
                });
        if (x.equals("restaurant")) {
            getNearbyPlaces("cafe");
        }
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
                                UserActivity.mCurrentData.add(response.body().getCurrently());
                                Log.d(TAG, "onResponse: " + response.body().getCurrently().getSummary());
                                UserActivity.mCurrentData.addAll(response.body().getHourly().getData());
                                weeklyData.updateList(UserActivity.mCurrentData, "currently");
                            }

                            @Override
                            public void onFailure(Call<WeatherInfo> call, Throwable t) {
                                Log.d(TAG, "onFailure: " + call.isCanceled());
                                Log.d(TAG, "onFailure: " + t.getCause());
                            }
                        });
                weatherAPI.getWeatherClient().getWeatherInfo(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), "hourly").
                        enqueue(new Callback<WeatherInfo>() {
                            @Override
                            public void onResponse(Call<WeatherInfo> call, Response<WeatherInfo> response) {
                                if (response.isSuccessful()) {
                                    UserActivity.this.mHourlyData.addAll(response.body().getHourly().data);
                                    weeklyData.updateList(UserActivity.this.mCurrentData, "hourly");
                                }
                            }

                            @Override
                            public void onFailure(Call<WeatherInfo> call, Throwable t) {

                            }
                        });
                getNearbyPlaces("restaurant");
                getNearbyPlaces("lodging");
                Log.d(TAG, "onLocationResult: ");
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
                                ActivityCompat.checkSelfPermission(UserActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                        PackageManager.PERMISSION_GRANTED) {
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
                                    Log.d(TAG, "onFailure: " + sie.getCause());
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
        UserActivity.mCurrentData = new ArrayList<>();
        UserActivity.mDailyData = new ArrayList<>();
        UserActivity.mHourlyData = new ArrayList<>();
        UserActivity.nearbyHotelList = new ArrayList<>();
        UserActivity.nearbyRestaurantList = new ArrayList<>();
        stopLocationUpdates();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
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

    @Override
    protected void onDestroy() {
        //cache clearing
        UserActivity.mCurrentData = new ArrayList<>();
        UserActivity.mDailyData = new ArrayList<>();
        UserActivity.mHourlyData = new ArrayList<>();
        UserActivity.nearbyHotelList = new ArrayList<>();
        UserActivity.nearbyRestaurantList = new ArrayList<>();
        super.onDestroy();
    }

    private void initUI() {
        viewPager = (ViewPager) findViewById(R.id.navFragContainer);
        navigationTabAdapter = new NavigationTabAdapter(this.getSupportFragmentManager());
        aboutApp = new AboutApp();
        weeklyData = new WeeklyData();
        weeklyForecastArguments.putParcelableArrayList("dataListHourly", UserActivity.mHourlyData);
        weeklyForecastArguments.putParcelableArrayList("dataListCurrently", UserActivity.mCurrentData);
        weeklyData.setContext(this);
        weeklyData.setArguments(weeklyForecastArguments);
        nearbyHotels = new NearbyHotels();
        nearbyHotels.setContext(this);
        hotelArguments.putParcelableArrayList("dataList", UserActivity.nearbyHotelList);
        nearbyHotels.setArguments(hotelArguments);
        nearbyRestaurants = new NearbyRestaurants();
        nearbyRestaurants.setContext(this);
        restaurantArguments.putParcelableArrayList("dataList", UserActivity.nearbyRestaurantList);
        nearbyRestaurants.setArguments(restaurantArguments);
        userProfile = new UserProfile();
        Bundle userDetails = new Bundle();
        userAuthentication = FirebaseAuth.getInstance();
        if (currentUser == null) {
            Intent loginScreen = new Intent(this, LoginActivity.class);
            loginScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginScreen);
            finish();
        }
        Log.d(TAG, "initUI: " + currentUser.getProviders().get(currentUser.getProviders().size() - 1));
        if ((currentUser.getProviders().get(currentUser.getProviders().size() - 1)).equals("facebook.com")) {
            userDetails.putString("provider", "facebook");
        } else if ((currentUser.getProviders().get(currentUser.getProviders().size() - 1)).equals("google.com")) {
            userDetails.putString("provider", "google");
        } else {
            userDetails.putString("provider", "email");
        }
        userProfile.setArguments(userDetails);
        userProfile.setContext(this);
        navigationTabAdapter.addFragment(weeklyData);
        navigationTabAdapter.addFragment(nearbyRestaurants);
        navigationTabAdapter.addFragment(userProfile);
        navigationTabAdapter.addFragment(nearbyHotels);
        navigationTabAdapter.addFragment(aboutApp);
        viewPager.setAdapter(navigationTabAdapter);
        viewPager.setOffscreenPageLimit(5);
        final String[] colors = getResources().getStringArray(R.array.vertical_ntb);
        navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_second),
                        Color.parseColor(colors[0]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_second))
                        .title("Forecast")
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
                        getResources().getDrawable(R.drawable.ic_fifth),
                        Color.parseColor(colors[3]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_fifth))
                        .title("Profile")
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
