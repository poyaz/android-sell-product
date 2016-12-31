package com.example.woods.amin.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.woods.amin.config;

import java.util.Calendar;

public class AutoStartReceiver extends BroadcastReceiver {
    private static final long REPEAT_TIME = 60 * 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("BroadcastReceiver (" + context.getPackageName() + ")", "boot receiver is start.");

        AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationReceiver = new Intent(context, NotificationReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, config.BROADCAST_NOTIFICATION, notificationReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 3);
        service.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), REPEAT_TIME, pending);
    }
}
