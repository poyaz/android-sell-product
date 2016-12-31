package com.example.woods.amin.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.woods.amin.Controller.SchedulesController;
import com.example.woods.amin.Database.Schedules;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

public class NotificationActivity extends AppCompatActivity {
    private Schedules schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        config.changeDirection(getWindow());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Long schedules_id = getIntent().getLongExtra("sid", -1L);
        if (schedules_id == -1L) {
            finish();
        }

        SchedulesController schedulesController = new SchedulesController(this);
        this.schedule = schedulesController.getScheduleInfoById(schedules_id);
        setTitle(this.schedule.getTitle());

        drawSchedule();
    }

    private void drawSchedule() {

        TextView user = (TextView) findViewById(R.id.contentNotification_tv_user_schedule);
        TextView title = (TextView) findViewById(R.id.contentNotification_tv_name_schedule);
        TextView description = (TextView) findViewById(R.id.contentNotification_tv_description_schedule);

        if (user != null && title != null && description != null) {
            user.setText(this.schedule.getUserSchedules().getName());
            title.setText(this.schedule.getTitle());
            description.setText(this.schedule.getDescription());
        }
    }
}
