package com.mihir.tvhome;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.Switch;

public class SettingsActivity extends Activity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("TVHomePrefs", MODE_PRIVATE);

        Switch swSound = findViewById(R.id.swSound);
        Switch swWeather = findViewById(R.id.swWeather);
        Switch swDayTimeBg = findViewById(R.id.swDayTimeBg);
        Switch swRecentApps = findViewById(R.id.swRecentApps);
        Switch swHighContrast = findViewById(R.id.swHighContrast);
        Switch swMusicPlayer = findViewById(R.id.swMusicPlayer);

        Button btnSystemSettings = findViewById(R.id.btnSystemSettings);
        Button btnBack = findViewById(R.id.btnBack);

        // Werte laden
        swSound.setChecked(prefs.getBoolean("sound_effects", true));
        swWeather.setChecked(prefs.getBoolean("show_weather", true));
        swDayTimeBg.setChecked(prefs.getBoolean("daytime_bg", true));
        swRecentApps.setChecked(prefs.getBoolean("show_recent", true));
        swHighContrast.setChecked(prefs.getBoolean("high_contrast", false));
        swMusicPlayer.setChecked(prefs.getBoolean("show_music", true));

        // Listeners zum Speichern
        swSound.setOnCheckedChangeListener((b, val) -> prefs.edit().putBoolean("sound_effects", val).apply());
        swWeather.setOnCheckedChangeListener((b, val) -> prefs.edit().putBoolean("show_weather", val).apply());
        swDayTimeBg.setOnCheckedChangeListener((b, val) -> prefs.edit().putBoolean("daytime_bg", val).apply());
        swRecentApps.setOnCheckedChangeListener((b, val) -> prefs.edit().putBoolean("show_recent", val).apply());
        swHighContrast.setOnCheckedChangeListener((b, val) -> prefs.edit().putBoolean("high_contrast", val).apply());
        swMusicPlayer.setOnCheckedChangeListener((b, val) -> prefs.edit().putBoolean("show_music", val).apply());

        setupFocus(swSound);
        setupFocus(swWeather);
        setupFocus(swDayTimeBg);
        setupFocus(swRecentApps);
        setupFocus(swHighContrast);
        setupFocus(swMusicPlayer);
        setupFocus(btnSystemSettings);
        setupFocus(btnBack);

        btnSystemSettings.setOnClickListener(v -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupFocus(android.view.View view) {
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(150)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).start();
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150)
                        .setInterpolator(new AccelerateDecelerateInterpolator()).start();
            }
        });
    }
}
