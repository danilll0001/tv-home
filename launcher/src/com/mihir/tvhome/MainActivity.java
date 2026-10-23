package com.mihir.tvhome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private SharedPreferences prefs;
    private SoundPool soundPool;
    private int clickSoundId;
    private boolean soundEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("TVHomeSettings", Context.MODE_PRIVATE);
        soundEnabled = prefs.getBoolean("sound_effects", true);

        // Apple TV Click-Sound
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        setupTimeAndGreeting();
        setupButtons();
    }

    private void setupTimeAndGreeting() {
        TextView txtTime = findViewById(R.id.txtTime);
        TextView txtDate = findViewById(R.id.txtDate);
        TextView txtGreeting = findViewById(R.id.txtGreeting);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.GERMANY);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d. MMMM", Locale.GERMANY);

        Date now = new Date();
        if (txtTime != null) txtTime.setText(timeFormat.format(now));
        if (txtDate != null) txtDate.setText(dateFormat.format(now));

        int hour = Integer.parseInt(new SimpleDateFormat("HH", Locale.GERMANY).format(now));
        if (txtGreeting != null) {
            if (hour >= 5 && hour < 12) {
                txtGreeting.setText(R.string.good_morning);
            } else if (hour >= 12 && hour < 18) {
                txtGreeting.setText(R.string.good_afternoon);
            } else {
                txtGreeting.setText(R.string.good_evening);
            }
        }
    }

    private void setupButtons() {
        Button btnSettings = findViewById(R.id.btnSettings);
        if (btnSettings != null) {
            applyAppleFocusEffect(btnSettings);
            btnSettings.setOnClickListener(v -> {
                playClickSound();
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            });
        }

        Button btnApps = findViewById(R.id.btnApps);
        if (btnApps != null) {
            applyAppleFocusEffect(btnApps);
            btnApps.setOnClickListener(v -> {
                playClickSound();
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
                startActivity(intent);
            });
        }

        Button btnHdmi1 = findViewById(R.id.btnHdmi1);
        if (btnHdmi1 != null) {
            applyAppleFocusEffect(btnHdmi1);
            btnHdmi1.setOnClickListener(v -> playClickSound());
        }
    }

    private void applyAppleFocusEffect(View view) {
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.animate().scaleX(1.08f).scaleY(1.08f).setDuration(150).start();
                playClickSound();
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
            }
        });
    }

    private void playClickSound() {
        if (soundEnabled && soundPool != null && clickSoundId != 0) {
            soundPool.play(clickSoundId, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }
}
