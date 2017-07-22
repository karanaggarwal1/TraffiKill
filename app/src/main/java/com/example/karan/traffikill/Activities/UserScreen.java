package com.example.karan.traffikill.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karan.traffikill.R;
import com.example.karan.traffikill.Services.WeatherAPI;
import com.example.karan.traffikill.models.CurrentData;
import com.example.karan.traffikill.models.KeyListDaily;
import com.example.karan.traffikill.models.KeyListHourly;
import com.example.karan.traffikill.models.UserDetails;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //REQUEST CODES and STRING KEYS
    public static final int NEW_VERSION = 123;
    public static final int NEW_USER = 234;
    public static final int PERM_REQ_CODE = 345;
    public static final int REQUEST_CHECK_SETTINGS = 456;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String LAST_APP_VERSION = "1";
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 300000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 150000;
    private static final String TAG = "LocationUpdates";

    //Firebase Authentication
    protected static FirebaseAuth userAuthentication;
    protected static FirebaseUser currentUser;

    //checking AppStart mode
    private static AppStart appStart = null;
    public WeatherAPI weatherAPI;
    protected UserDetails currentUserDetails;
    SharedPreferences checkFirstTimeStart;
    //UI elements.
    private ImageView profileImage;
    private TextView displayName;
    private TextView displayMail;
    private boolean doubleBackToExitPressedOnce = false;
    //status check if location updates are turned on or not
    private boolean mRequestingLocationUpdates = true;
    //Time when the location was updated the last time.
    private String mLastUpdateTime;
    //Location API Providers and Clients
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
        setContentView(R.layout.user_screen);

        checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        checkPermission(this, Manifest.permission.INTERNET);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView InavigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = InavigationView.getHeaderView(0);
        profileImage = (ImageView) (hView.findViewById(R.id.iv_profile_pic));
        displayName = (TextView) (hView.findViewById(R.id.tvName));
        displayMail = (TextView) (hView.findViewById(R.id.email));

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
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        checkFirstTimeStart = getSharedPreferences("TRAFFIKILL", MODE_PRIVATE);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        switch (checkAppStart(this, checkFirstTimeStart)) {
            case NORMAL:
                break;
            case FIRST_TIME_VERSION:
                //show what's new in future versions
//                Intent firstTimeInVersion = new Intent(this, FirstVersionLaunch.class);
//                startActivityForResult(firstTimeInVersion, NEW_VERSION);
                break;
            case FIRST_TIME:
                //the User has launched your app for the first time
                //irrespective of the version code
//                Intent firstIntent = new Intent(this, FirstLaunch.class);
//                startActivityForResult(firstIntent, NEW_USER);
                break;
            default:
                break;
        }
        weatherAPI = new WeatherAPI();
        /*start code for established user
        get user data
        start using firebase database for storing data
        three possible cases, launched app but user is signed out,
        never made an account but launched the app before
        the user is signed in*/
        userAuthentication = FirebaseAuth.getInstance();
        if (currentUser == null) {
            //redirect user to login screen
            //show login Activity
            Intent loginScreen = new Intent(this, LoginActivity.class);
            startActivity(loginScreen);
            finish();
        }
        /*setup User UI details
        Get useful data to set up user profile*/
        if (currentUser != null && currentUser.getDisplayName() != null)
            currentUserDetails = new UserDetails(currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getPhotoUrl());
        if (currentUserDetails != null && currentUserDetails.getName() != null) {
            displayName.setText(currentUserDetails.getName());
        } else {
            displayName.setText("");
        }
        if (currentUserDetails != null && currentUserDetails.getImageURL() != null) {
            Picasso.with(getApplicationContext())
                    .load(currentUserDetails.getImageURL())
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                    .resize(200, 200)
                    .centerCrop()
                    .into(profileImage);
        }
        if (currentUser != null && currentUserDetails.getEmail() != null) {
            displayMail.setText(currentUserDetails.getEmail());
        } else {
            //in case the user has signed up with Phone Number instead of Email Address
            displayMail.setText("");
        }
        //initialise data arrayLists

        if (savedInstanceState != null) {
            this.mCurrentData = savedInstanceState.getParcelableArrayList("CurrentData");
            this.mHourlyData = savedInstanceState.getParcelableArrayList("HourlyData");
            this.mDailyData = savedInstanceState.getParcelableArrayList("DailyData");
        } else {
            this.mDailyData = new ArrayList<>();
            this.mHourlyData = new ArrayList<>();
            this.mCurrentData = new ArrayList<>();
        }
        checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
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
                //Get request to Dark Sky API will be made here, every time the location is changed
                weatherAPI.getWeatherClient().getWeatherInfo(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()).
                        enqueue(new Callback<WeatherInfo>() {
                    @Override
                    public void onResponse(Call<WeatherInfo> call, Response<WeatherInfo> response) {
                        for (CurrentData currentData : response.body().getCurrently()) {
                            UserScreen.this.mCurrentData.add(currentData);
                            Log.d(TAG, "onResponse: " + currentData.getSummary());
                        }
                        for (KeyListHourly hourlyData : response.body().getHourly()) {
                            UserScreen.this.mHourlyData.add(hourlyData);
                            Log.d(TAG, "onResponse: " + hourlyData.getSummary());
                        }
                        for (KeyListDaily dailyData : response.body().getDaily()) {
                            UserScreen.this.mDailyData.add(dailyData);
                            Log.d(TAG, "onResponse: " + dailyData.getSummary());
                        }
                    }

                    //
                    @Override
                    public void onFailure(Call<WeatherInfo> call, Throwable t) {

                    }
                });
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                Log.d(TAG, "onLocationResult: " + mLastUpdateTime);
            }
        };
    }

    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
//            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
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
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
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
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            Toast.makeText(this, "Enable Permissions for Location Services", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                        Log.i(TAG, "All location settings are satisfied.");
                        //noinspection MissingPermission
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
//                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
//                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(UserScreen.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
//                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
//                                Log.e(TAG, errorMessage);
                                Toast.makeText(UserScreen.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove location updates to save battery.
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
        savedInstanceState.putParcelable("CurrentData", (Parcelable) this.mCurrentData);
        savedInstanceState.putParcelable("DailyData", (Parcelable) this.mDailyData);
        savedInstanceState.putParcelable("HourlyData", (Parcelable) this.mHourlyData);
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

    private void checkForLocationServices(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled;

        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gps_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        }
                    });
            dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                }
            });
            dialog.show();
        }
    }

    public AppStart checkAppStart(Context context, SharedPreferences sharedPreferences) {
        PackageInfo pInfo;
        try {
            pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            int lastVersionCode = sharedPreferences.getInt(
                    LAST_APP_VERSION, -1);
            int currentVersionCode = pInfo.versionCode;
            appStart = checkAppStart(currentVersionCode, lastVersionCode);
            sharedPreferences.edit()
                    .putInt(LAST_APP_VERSION, currentVersionCode).apply();
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("TraffiKill",
                    "Unable to determine current app version from package manager. Defensively assuming normal app start.");
        }
        return appStart;
    }

    public AppStart checkAppStart(int currentVersionCode, int lastVersionCode) {
        if (lastVersionCode == -1) {
            return AppStart.FIRST_TIME;
        } else if (lastVersionCode < currentVersionCode) {
            return AppStart.FIRST_TIME_VERSION;
        } else if (lastVersionCode > currentVersionCode) {
            Log.w("TraffiKill", "Current version code (" + currentVersionCode
                    + ") is less then the one recognized on last startup ("
                    + lastVersionCode
                    + "). Defensively assuming normal app start.");
            return AppStart.NORMAL;
        } else {
            return AppStart.NORMAL;
        }
    }

    public void checkPermission(Context context, String perm) {
        //TODO: Implement a permission driven interface in other activities as well
        if (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{perm}, PERM_REQ_CODE);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, perm)) {
            Toast.makeText(context, "Give the permission please.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!drawer.isDrawerOpen(GravityCompat.START)) {
            super.onBackPressed();
        }
        if (doubleBackToExitPressedOnce) {
            android.os.Process.killProcess(android.os.Process.myPid());
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

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public enum AppStart {
        FIRST_TIME, FIRST_TIME_VERSION, NORMAL
    }

}
