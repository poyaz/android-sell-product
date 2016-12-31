package com.example.woods.amin.Interface;

import android.preference.Preference;

import java.util.Map;

public interface SettingsDataPassInterface {
    void onDataPassBind(Preference preference);
    Map<String, String> onDataPassGet();
}
