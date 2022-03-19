package com.example.spandtime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView;
    int bannerAdsClickCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Extract Click Count
        SharedPreferences countPref = getSharedPreferences("ClickCounter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = countPref.edit();

        bannerAdsClickCounter = countPref.getInt("ClickCount", 0);

        //if user has not clicked ads 4 times in 8 hours then only show banner ad
        if (bannerAdsClickCounter < 4)
            initAds();

        //Reset the shared preference data after 8 hours (480 Minutes)
        if (countPref.getLong("ExpiredDate", -1) > System.currentTimeMillis()) {
            Log.d("MyAds", String.valueOf(countPref.getLong("ExpiredDate", -1)));
        } else {
            editor.clear();
            editor.apply();
        }
    }

    /**
     * Method to Init Ads
     */
    private void initAds() {
        //load the ad
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

                Toast.makeText(MainActivity.this, "Loading Ads", Toast.LENGTH_SHORT).show();
                loadBannerAd();
            }
        });
    }

    /*
    Method to load the banner ads and handle the clicks
     */
    private void loadBannerAd() {

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //click listeners are implemented here
        //BannerAdsListener.listen(mAdView, MainActivity.this);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                bannerAdsClickCounter++;

                SharedPreferences countPref = getSharedPreferences("ClickCounter", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = countPref.edit();

                editor.putInt("ClickCount", bannerAdsClickCounter);
                editor.putLong("ExpiredDate", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(480));
                editor.apply();

                Toast.makeText(MainActivity.this, "Ads Clicked " + bannerAdsClickCounter + " Times", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
