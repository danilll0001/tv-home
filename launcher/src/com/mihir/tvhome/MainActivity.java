package com.mihir.tvhome;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView tvTime, tvDate, tvGreeting, tvWeather, tvWifi;
    private View recentAppsLayout, weatherWidget, musicPlayerLayout;
    private RelativeLayout mainLayout;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("TVHomePrefs", MODE_PRIVATE);

        mainLayout = findViewById(R.id.mainLayout);
        tvTime = findViewById(R.id.tvTime);
        tvDate = findViewById(R.id.tvDate);
        tvGreeting = findViewById(R.id.tvGreeting);
        tvWeather = findViewById(R.id.tvWeather);
        tvWifi = findViewById(R.id.tvWifi);
        recentAppsLayout = findViewById(R.id.recentAppsLayout);
        weatherWidget = findViewById(R.id.tvWeather);
        musicPlayerLayout = findViewById(R.id.musicPlayerLayout);

        setupTopBar();
        setupAppsRow();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyPreferences();
        updateTimeAndGreeting();
        checkNetworkStatus();
    }

    private void applyPreferences() {
        boolean showWeather = prefs.getBoolean("show_weather", true);
        boolean showRecent = prefs.getBoolean("show_recent", true);
        boolean dayTimeBg = prefs.getBoolean("daytime_bg", true);
        boolean highContrast = prefs.getBoolean("high_contrast", false);
        boolean showMusic = prefs.getBoolean("show_music", true);

        weatherWidget.setVisibility(showWeather ? View.VISIBLE : View.GONE);
        recentAppsLayout.setVisibility(showRecent ? View.VISIBLE : View.GONE);
        musicPlayerLayout.setVisibility(showMusic ? View.VISIBLE : View.GONE);

        if (dayTimeBg) {
            int hour = Integer.parseInt(new SimpleDateFormat("HH", Locale.GERMANY).format(new Date()));
            if (hour >= 6 && hour < 18) {
                mainLayout.setBackgroundColor(Color.parseColor("#141A29")); // Tag-Farbton
            } else {
                mainLayout.setBackgroundColor(Color.parseColor("#0B0B0B")); // Nacht-Farbton
            }
        } else {
            mainLayout.setBackgroundColor(Color.parseColor("#0B0B0B"));
        }

        if (highContrast) {
            tvTime.setTextColor(Color.YELLOW);
            tvGreeting.setTextColor(Color.WHITE);
        } else {
            tvTime.setTextColor(Color.WHITE);
            tvGreeting.setTextColor(Color.parseColor("#A0A0A0"));
        }
    }

    private void setupTopBar() {
        Button btnSettings = findViewById(R.id.btnSettings);
        if (btnSettings != null) {
            setupFocusZoom(btnSettings);
            btnSettings.setOnClickListener(v -> {
                playClickSound();
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            });
        }

        Button btnApps = findViewById(R.id.btnApps);
        if (btnApps != null) {
            setupFocusZoom(btnApps);
            btnApps.setOnClickListener(v -> {
                playClickSound();
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
                startActivity(intent);
            });
        }

        Button btnDevices = findViewById(R.id.btnDevices);
        if (btnDevices != null) {
            setupFocusZoom(btnDevices);
            btnDevices.setOnClickListener(v -> playClickSound());
        }
    }

    private void setupAppsRow() {
        int[] appIds = {R.id.app1, R.id.app2, R.id.app3, R.id.app4, R.id.app5, R.id.btnRecent1, R.id.btnRecent2};
        for (int id : appIds) {
            Button btn = findViewById(id);
            if (btn != null) {
                setupFocusZoom(btn);
                btn.setOnClickListener(v -> playClickSound());
            }
        }
    }

    private void updateTimeAndGreeting() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.GERMANY);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d. MMMM", Locale.GERMANY);
        Date now = new Date();

        if (tvTime != null) tvTime.setText(timeFormat.format(now));
        if (tvDate != null) tvDate.setText(dateFormat.format(now));

        int hour = Integer.parseInt(new SimpleDateFormat("HH", Locale.GERMANY).format(now));
        if (tvGreeting != null) {
            if (hour >= 5 && hour < 12) tvGreeting.setText("Guten Morgen");
            else if (hour >= 12 && hour < 18) tvGreeting.setText("Guten Tag");
            else tvGreeting.setText("Guten Abend");
        }
    }

    private void checkNetworkStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (tvWifi != null) {
            tvWifi.setText(isConnected ? "📡 Online" : "⚠️ Offline");
        }
    }

    private void setupFocusZoom(View view) {
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                playClickSound();
                v.animate().scaleX(1.12f).scaleY(1.12f).setDuration(180)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).start();
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(180)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).start();
            }
        });
    }

    private void playClickSound() {
        boolean soundEnabled = prefs.getBoolean("sound_effects", true);
        if (soundEnabled) {
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
            }
        }
    }
}
