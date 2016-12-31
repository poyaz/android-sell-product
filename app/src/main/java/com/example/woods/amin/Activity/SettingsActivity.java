package com.example.woods.amin.Activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import com.example.woods.amin.Controller.SettingsController;
import com.example.woods.amin.Database.Settings;
import com.example.woods.amin.Fragment.SettingsMailFragment;
import com.example.woods.amin.Interface.SettingsDataPassInterface;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatPreferenceActivity implements Preference.OnPreferenceChangeListener, SettingsDataPassInterface {
    private Map<String, String> settings = new HashMap<>();
    private SettingsController settingsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config.changeDirection(getWindow());

        setupActionBar();

        settingsController = new SettingsController(this);

        for (Settings item : settingsController.getSettingsList()) {
            this.settings.put(item.getKey(), item.getValue());
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.settings_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return SettingsMailFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    public void onDataPassBind(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    public Map<String, String> onDataPassGet() {
        return this.settings;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        Log.v("value", value.toString());
        String stringValue = value.toString();
        if (stringValue.isEmpty())
            return false;

        switch (preference.getKey()) {
            case "settingsMail_ep_mail":
                preference.setSummary(stringValue);
                if (!stringValue.equalsIgnoreCase(this.settings.get("username"))) {
                    this.settingsController.updateValueOfKey("username", stringValue);
                }
                break;
            case "settingsMail_ep_password1":
                this.settingsController.updateValueOfKey("password1", stringValue);
                break;
            case "settingsMail_ep_password2":
                this.settingsController.updateValueOfKey("password2", stringValue);
                break;
        }

        return true;
    }
}
