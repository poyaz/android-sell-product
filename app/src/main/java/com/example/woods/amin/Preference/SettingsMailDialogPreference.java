package com.example.woods.amin.Preference;

import android.content.Context;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.woods.amin.R;
import com.example.woods.amin.config;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.woods.amin.R.id.settingsMailDialog_et_mail;
import static com.example.woods.amin.R.id.settingsMailDialog_et_password1;
import static com.example.woods.amin.R.id.settingsMailDialog_et_password2;

public class SettingsMailDialogPreference extends EditTextPreference implements Preference.OnPreferenceClickListener {
    private TextView title;
    private EditText mailEdit;
    private EditText password1Edit;
    private EditText password2Edit;

    public SettingsMailDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(false);

        setDialogTitle(null);
        setDialogLayoutResource(R.layout.settings_mail_dialog);
        setOnPreferenceClickListener(this);

        setPositiveButtonText(getContext().getResources().getStringArray(R.array.global_message)[4]);
        setNegativeButtonText(getContext().getResources().getStringArray(R.array.global_message)[3]);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        this.title = (TextView) view.findViewById(R.id.settingsMailDialog_tv_title);
        this.mailEdit = (EditText) view.findViewById(settingsMailDialog_et_mail);
        this.password1Edit = (EditText) view.findViewById(settingsMailDialog_et_password1);
        this.password2Edit = (EditText) view.findViewById(settingsMailDialog_et_password2);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            switch (getKey()) {
                case "settingsMail_ep_mail":
                    if (this.mailEdit.getText().toString().matches(config.REGEX_EMAIL_ADDRESS)) {
                        getOnPreferenceChangeListener().onPreferenceChange(this, this.mailEdit.getText().toString());
                    } else {
                        new SweetAlertDialog(this.getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(this.getContext().getResources().getStringArray(R.array.global_message)[5])
                                .setContentText(this.getContext().getResources().getStringArray(R.array.authenticate_message)[10])
                                .setConfirmText(this.getContext().getResources().getStringArray(R.array.global_message)[3])
                                .show();
                    }
                    break;
                case "settingsMail_ep_password1":
                    if (this.password1Edit.getText().length() > config.PASSWORD_LENGTH) {
                        getOnPreferenceChangeListener().onPreferenceChange(this, this.password1Edit.getText().toString());
                    } else {
                        new SweetAlertDialog(this.getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(this.getContext().getResources().getStringArray(R.array.global_message)[5])
                                .setContentText(this.getContext().getResources().getStringArray(R.array.authenticate_message)[9])
                                .setConfirmText(this.getContext().getResources().getStringArray(R.array.global_message)[3])
                                .show();
                    }
                    break;
                case "settingsMail_ep_password2":
                    if (this.password2Edit.length() > config.PASSWORD_LENGTH) {
                        getOnPreferenceChangeListener().onPreferenceChange(this, this.password2Edit.getText().toString());
                    } else {
                        new SweetAlertDialog(this.getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(this.getContext().getResources().getStringArray(R.array.global_message)[5])
                                .setContentText(this.getContext().getResources().getStringArray(R.array.authenticate_message)[9])
                                .setConfirmText(this.getContext().getResources().getStringArray(R.array.global_message)[3])
                                .show();
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        this.mailEdit.setVisibility(View.GONE);
        this.password1Edit.setVisibility(View.GONE);
        this.password2Edit.setVisibility(View.GONE);

        switch (getKey()) {
            case "settingsMail_ep_mail":
                this.title.setText(getContext().getResources().getString(R.string.settingsSecure_title_mail));
                this.mailEdit.setVisibility(View.VISIBLE);
                break;
            case "settingsMail_ep_password1":
                this.title.setText(getContext().getResources().getString(R.string.settingsSecure_title_password1));
                this.password1Edit.setVisibility(View.VISIBLE);
                break;
            case "settingsMail_ep_password2":
                this.title.setText(getContext().getResources().getString(R.string.settingsSecure_title_password2));
                this.password2Edit.setVisibility(View.VISIBLE);
                break;
        }
        return false;
    }
}
