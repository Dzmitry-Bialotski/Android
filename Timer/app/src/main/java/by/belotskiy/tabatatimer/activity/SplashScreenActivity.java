package by.belotskiy.tabatatimer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import java.util.Locale;

import by.belotskiy.tabatatimer.activity.MainActivity;

public class SplashScreenActivity extends AppCompatActivity {
    public static String language;
    public static float font_size;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean theme_style = preferences.getBoolean("app_theme", false);
        if (theme_style) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        language = preferences.getString("language", "en");
        Configuration config = getBaseContext().getResources().getConfiguration();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        font_size = Float.parseFloat(preferences.getString("font", "10"));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}