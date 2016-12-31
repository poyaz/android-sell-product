package com.example.woods.amin.Listener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.woods.amin.Activity.AcceptOrderActivity;
import com.example.woods.amin.Activity.AddProductActivity;
import com.example.woods.amin.Activity.SettingsActivity;
import com.example.woods.amin.Controller.SettingsController;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TwoAuthenticateListener implements DialogInterface.OnClickListener {
    private Activity activity = null;
    private String dest = null;
    private Long basketId = -1L;
    private Long count = 0L;
    private EditText password2 = null;
    private Bundle data = null;

    public TwoAuthenticateListener(Activity activity, String dest, Long count) {
        this.activity = activity;
        this.dest = dest;
        this.count = count;
    }

    public void setBasketId(Long basketId) {
        this.basketId = basketId;
    }

    public void setData(Bundle data) {
        this.data = data;
    }

    public void startAuthenticate() {
        if (this.dest == null) {
            this.nullDest();
            return;
        } else {
            if (this.dest.equals(AcceptOrderActivity.class.getSimpleName())) {
                if (this.data != null && this.data.getLong("oid", -1L) != -1L) {
                    this.basketId = this.data.getLong("oid");
                } else {
                    if (this.basketId == -1L) {
                        this.noBasket();
                        return;
                    } else if (this.count == 0L) {
                        this.emptyBasket();
                        return;
                    }
                }
            } else if (this.dest.equals(AddProductActivity.class.getSimpleName())) {
                if (this.data.getString("email") == null || this.data.getLong("edit", -1L) == -1L) {
                    this.dataError();
                    return;
                }
            } else if (this.dest.equals(SettingsActivity.class.getSimpleName())) {
                if (this.data != null) {
                    this.dataError();
                    return;
                }
            } else {
                this.nullDest();
                return;
            }
        }

        View authenticateView = View.inflate(this.activity, R.layout.authenticate_view, null);
        authenticateView.setBackgroundColor(this.activity.getResources().getColor((android.R.color.white)));

        TextView title = new TextView(this.activity);
        title.setText(this.activity.getResources().getStringArray(R.array.authenticate_message)[0]);
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(40, 40, 40, 40);
        title.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        ((TextView) authenticateView.findViewById(R.id.authenticateView_tv_password)).setText(this.activity.getResources().getStringArray(R.array.authenticate_message)[1]);
        this.password2 = (EditText) authenticateView.findViewById(R.id.authenticateView_et_password);

        new AlertDialog.Builder(this.activity)
                .setCustomTitle(title)
                .setView(authenticateView)
                .setPositiveButton(this.activity.getResources().getStringArray(R.array.authenticate_message)[2], this)
                .show();
    }

    private void noBasket() {
        new SweetAlertDialog(this.activity, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(this.activity.getResources().getStringArray(R.array.basket_message)[13])
                .setContentText(this.activity.getResources().getStringArray(R.array.basket_message)[14])
                .setConfirmText(this.activity.getResources().getStringArray(R.array.global_message)[3])
                .show();
    }

    private void emptyBasket() {
        new SweetAlertDialog(this.activity, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(this.activity.getResources().getStringArray(R.array.basket_message)[13])
                .setContentText(this.activity.getResources().getStringArray(R.array.basket_message)[0])
                .setConfirmText(this.activity.getResources().getStringArray(R.array.global_message)[3])
                .show();
    }

    private void failedAuthenticate() {
        new SweetAlertDialog(this.activity, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(this.activity.getResources().getStringArray(R.array.authenticate_message)[3])
                .setContentText(this.activity.getResources().getStringArray(R.array.authenticate_message)[4])
                .setConfirmText(this.activity.getResources().getStringArray(R.array.global_message)[3])
                .show();
    }

    private void nullDest() {
        new SweetAlertDialog(this.activity, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(this.activity.getResources().getStringArray(R.array.authenticate_message)[5])
                .setContentText(this.activity.getResources().getStringArray(R.array.authenticate_message)[6])
                .setConfirmText(this.activity.getResources().getStringArray(R.array.global_message)[3])
                .show();
    }

    private void dataError() {
        new SweetAlertDialog(this.activity, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(this.activity.getResources().getStringArray(R.array.authenticate_message)[7])
                .setContentText(this.activity.getResources().getStringArray(R.array.authenticate_message)[8])
                .setConfirmText(this.activity.getResources().getStringArray(R.array.global_message)[3])
                .show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            case DialogInterface.BUTTON_POSITIVE:
                SettingsController settingsController = new SettingsController(this.activity);
                String password2 = this.password2.getText().toString();
                if (!password2.isEmpty() && settingsController.checkTwoAuthenticate(password2)) {
                    this.startActivity();
                } else {
                    this.failedAuthenticate();
                }
                break;
        }
    }

    private void startActivity() {
        if (this.dest.equals(AcceptOrderActivity.class.getSimpleName())) {
            Intent intent = new Intent(this.activity, AcceptOrderActivity.class);
            intent.putExtra("oid", this.basketId);
            if (this.data != null)
                intent.putExtra("dest", this.data.getString("dest", ""));
            this.activity.startActivityForResult(intent, config.REQUEST_INTENT_ACCEPT_ORDER_ACTIVITY);
        } else if (this.dest.equals(AddProductActivity.class.getSimpleName())) {
            Intent intent = new Intent(this.activity, AddProductActivity.class);
            intent.putExtra("email", this.data.getString("email"));
            intent.putExtra("edit", this.data.getLong("edit"));
            this.activity.startActivityForResult(intent, config.REQUEST_INTENT_EDIT_PRODUCTS_ACTIVITY);
        } else if (this.dest.equals(SettingsActivity.class.getSimpleName())) {
            Intent intent = new Intent(this.activity, SettingsActivity.class);
            this.activity.startActivityForResult(intent, config.REQUEST_INTENT_SETTINGS_ACTIVITY);
        }
    }
}
