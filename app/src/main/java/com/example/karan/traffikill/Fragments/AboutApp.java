package com.example.karan.traffikill.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.karan.traffikill.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * Created by Karan on 28-07-2017.
 */

public class AboutApp extends Fragment {
    Context context;

    public AboutApp(Context context) {
        this.context = context;
    }


    public AboutApp() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String description;
        description = "TraffiKill is developed by an independent developer team with limited resources. Help us to grow by donating." +
                " We are powered by ads so please help us continue the good work. This application provides you with an accurate measure" +
                " of weather it is going to rain or not, for the selected time frame and according to that information you can plan your excursion" +
                " accordingly and avoid getting stuck in traffic and wasting otherwise useful time.";
        View aboutPage = null;
        if (this.context != null) {
            aboutPage = new AboutPage(this.context)
                    .isRTL(false)
                    .setDescription(description)
                    .addItem(getVersionInformation())
                    .addItem(getDarkSkyElement())
                    .addItem(getGooglePlacesElement())
                    .addItem(getFutureVersions())
                    .addGroup("Connect with us")
                    .addEmail("traffikill.developer@gmail.com")
                    .addFacebook("")
                    .addPlayStore("com.example.karan.traffikill")
                    .addGitHub("karanaggarwal1/TraffiKill")
                    .create();
        }
        return aboutPage;
    }

    private Element getFutureVersions() {
        Element futureVersions = new Element();
        futureVersions.setTitle("Coming up in future versions:\n\nPush Notifications for Rain Alerts\nReal " +
                "time weather data for a pre-planned trip");
        return futureVersions;
    }

    private Element getGooglePlacesElement() {
        Element googlePlacesElement = new Element();
        googlePlacesElement.setTitle("Powered by Google");
        googlePlacesElement.setIconDrawable(R.drawable.sponsor_powered_by_google);
        return googlePlacesElement;
    }

    private Element getVersionInformation() {
        Element versionElement = new Element();
        versionElement.setTitle("Version 1.0.1");
        return versionElement;
    }

    private Element getDarkSkyElement() {
        Element sponsorsElement = new Element();
        sponsorsElement.setTitle("Powered by Dark Sky");
        sponsorsElement.setIconDrawable(R.drawable.sponsor_dark_sky);
        sponsorsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openBrowser = new Intent(Intent.ACTION_VIEW);
                openBrowser.setData(Uri.parse("https://darksky.net/poweredby/"));
                Intent browserChooserIntent = Intent.createChooser(openBrowser, "Choose browser of your choice");
                startActivity(browserChooserIntent);
            }
        });
        return sponsorsElement;
    }
}
