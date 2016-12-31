package com.example.woods.amin.Fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.example.woods.amin.Interface.SettingsDataPassInterface;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.util.Map;

public class SettingsMailFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_mail);
        setHasOptionsMenu(true);

        config.changeDirection(getActivity().getWindow());

        Map<String, String> settings = ((SettingsDataPassInterface) getActivity()).onDataPassGet();

        findPreference("settingsMail_ep_mail").setSummary(settings.get("username"));

        ((SettingsDataPassInterface) getActivity()).onDataPassBind(findPreference("settingsMail_ep_mail"));
        ((SettingsDataPassInterface) getActivity()).onDataPassBind(findPreference("settingsMail_ep_password1"));
        ((SettingsDataPassInterface) getActivity()).onDataPassBind(findPreference("settingsMail_ep_password2"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
