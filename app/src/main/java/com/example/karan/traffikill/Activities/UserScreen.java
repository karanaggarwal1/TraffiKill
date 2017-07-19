package com.example.karan.traffikill.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import android.widget.TextView;

import com.example.karan.traffikill.R;
import com.example.karan.traffikill.models.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;


public class UserScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final int NEW_VERSION = 123;
    public static final int NEW_USER = 234;
    private static final String LAST_APP_VERSION = "1";
    protected static FirebaseAuth userAuthentication;
    protected static FirebaseUser currentUser;
    private static AppStart appStart = null;
    protected UserDetails currentUserDetails;
    SharedPreferences checkFirstTimeStart;
    private ImageView profileImage;
    private TextView displayName;
    private TextView displayMail;

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = userAuthentication.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_screen);

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
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        checkFirstTimeStart = getSharedPreferences("TRAFFIKILL", MODE_PRIVATE);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        switch (checkAppStart(this, checkFirstTimeStart)) {
            case NORMAL:
                break;
            case FIRST_TIME_VERSION:
                //show what's new in future versions
                Intent firstTimeInVersion = new Intent(this, FirstVersionLaunch.class);
                startActivityForResult(firstTimeInVersion, NEW_VERSION);
                break;
            case FIRST_TIME:
                //the User has launched your app for the first time
                //irrespective of the version code
                Intent firstIntent = new Intent(this, FirstLaunch.class);
                startActivityForResult(firstIntent, NEW_USER);
                break;
            default:
                break;
        }
        //start code for established user
        //get user data
        //start using firebase database for storing data
        //three possible cases, launched app but user is signed out,
        //never made an account but launched the app before
        //the user is signed in
        userAuthentication = FirebaseAuth.getInstance();
        if (currentUser == null) {
            //redirect user to login screen
            //TODO:user has logged out or never made an account
            //show login Activity
            Intent loginScreen = new Intent(this, LoginActivity.class);
            startActivity(loginScreen);
            finish();
        }
        //setup User UI details
        //Patterns.EMAIL_ADDRESS.matcher(email).matches()
        //to check if email address is valid or not
        //Get useful data to set up user profile
        currentUserDetails = new UserDetails(currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getPhotoUrl());
        if (currentUserDetails.getName() != null) {
            displayName.setText(currentUserDetails.getName());
        }
        if (currentUserDetails.getImageURL() != null) {
            Picasso.with(getApplicationContext())
                    .load(currentUserDetails.getImageURL())
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                    .resize(200, 200)
                    .centerCrop()
                    .into(profileImage);
        }
        if (currentUserDetails.getEmail() != null) {
            displayMail.setText(currentUserDetails.getEmail());
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
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
