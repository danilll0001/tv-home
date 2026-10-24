package com.mihir.tvhome;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

    private TextView tvGreeting, tvTime, tvDate, tvWeather;
    private RelativeLayout mainLayout;
    private LinearLayout recentAppsLayout, musicPlayerLayout;
    private Handler timeHandler = new Handler(Looper.getMainLooper());
    private SharedPreferences prefs;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("TVHomePrefs", MODE_PRIVATE);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        mainLayout = findViewById(R.id.mainLayout);
        tvGreeting = findViewById(R.id.tvGreeting);
        tvTime = findViewById(R.id.tvTime);
        tvDate = findViewById(R.id.tvDate);
        tvWeather = findViewById(R.id.tvWeather);
        recentAppsLayout = findViewById(R.id.recentAppsLayout);
        musicPlayerLayout = findViewById(R.id.musicPlayerLayout);

        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnApps = findViewById(R.id.btnApps);
        Button btnDevices = findViewById(R.id.btnDevices);

        Button app1 = findViewById(R.id.app1);
        Button app2 = findViewById(R.id.app2);
        Button app3 = findViewById(R.id.app3);
        Button app4 = findViewById(R.id.app4);
        Button app5 = findViewById(R.id.app5);

        // Focus Zoom & Sound Effekte
        setupFocusAndSound(btnSettings);
        setupFocusAndSound(btnApps);
        setupFocusAndSound(btnDevices);
        setupFocusAndSound(app1);
        setupFocusAndSound(app2);
        setupFocusAndSound(app3);
        setupFocusAndSound(app4);
        setupFocusAndSound(app5);

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Apps starten
        app1.setOnClickListener(v -> launchApp("com.google.android.youtube.tv"));
        app2.setOnClickListener(v -> launchApp("com.netflix.ninja"));
        app3.setOnClickListener(v -> launchApp("com.amazon.amazonvideo.livingroom"));
        app4.setOnClickListener(v -> launchApp("com.spotify.tv.web"));
        app5.setOnClickListener(v -> launchApp("de.zdf.o2o"));

        startClock();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyPreferences();
    }

    private void applyPreferences() {
        boolean showWeather = prefs.getBoolean("show_weather", true);
        boolean showRecent = prefs.getBoolean("show_recent", true);
        boolean daytimeBg = prefs.getBoolean("daytime_bg", true);
        boolean showMusic = prefs.getBoolean("show_music", true);

        tvWeather.setVisibility(showWeather ? View.VISIBLE : View.GONE);
        recentAppsLayout.setVisibility(showRecent ? View.VISIBLE : View.GONE);
        musicPlayerLayout.setVisibility(showMusic ? View.VISIBLE : View.GONE);

        if (daytimeBg) {
            int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
            if (hour >= 6 && hour < 12) {
                tvGreeting.setText("Guten Morgen");
                mainLayout.setBackgroundColor(0xFF141E30);
            } else if (hour >= 12 && hour < 18) {
                tvGreeting.setText("Guten Tag");
                mainLayout.setBackgroundColor(0xFF0F2027);
            } else {
                tvGreeting.setText("Guten Abend");
                mainLayout.setBackgroundColor(0xFF0B0B0B);
            }
        }
    }

    private void setupFocusAndSound(View view) {
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.animate().scaleX(1.12f).scaleY(1.12f).setDuration(180)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).start();
                if (prefs.getBoolean("sound_effects", true) && audioManager != null) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(180)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).start();
            }
        });
    }

    private void launchApp(String packageName) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            startActivity(launchIntent);
        }
    }

    private void startClock() {
        timeHandler.post(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.GERMANY);
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d. MMMM", Locale.GERMANY);
                Date now = new Date();
                tvTime.setText(timeFormat.format(now));
                tvDate.setText(dateFormat.format(now));
                timeHandler.postDelayed(this, 10000);
            }
        });
    }
}
