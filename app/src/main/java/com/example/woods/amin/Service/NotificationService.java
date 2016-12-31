package com.example.woods.amin.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.woods.amin.Activity.NotificationActivity;
import com.example.woods.amin.Controller.SchedulesController;
import com.example.woods.amin.Database.Schedules;
import com.example.woods.amin.R;

import java.util.List;

public class NotificationService extends Service {
    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Service (" + this.getPackageName() + ")", "notification service is start");

        SchedulesController schedulesController = new SchedulesController(this);
        List<Schedules> schedules = schedulesController.getSchedulesListOfAlarm();
        if (schedules.size() != 0) {
            schedulesController.updateSchedules(schedules);

            for (Schedules schedule : schedules) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                Intent notificationIntent = new Intent(this, NotificationActivity.class);
                notificationIntent.putExtra("sid", schedule.getId());
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), notificationIntent, 0);

                Notification notification = new Notification.Builder(this)
                        .setContentTitle(schedule.getTitle() + "(" + schedule.getUserSchedules().getName() + ")")
                        .setContentText(schedule.getDescription())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true).build();

                notification.flags |= Notification.FLAG_AUTO_CANCEL;

                notificationManager.notify(schedule.getId().intValue(), notification);
            }
        }

        this.onDestroy();
        
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Service (" + this.getPackageName() + ")", "notification service is destroy");
    }
}
