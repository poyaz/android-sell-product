package com.example.woods.amin.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.example.woods.amin.Controller.SchedulesController;
import com.example.woods.amin.Database.Schedules;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ViewScheduleActivity extends AppCompatActivity implements View.OnClickListener {
    private long scheduleId = -1L;
    private Long uid = -1L;
    private String user = "";
    private Schedules schedule = null;
    private ArrayList<String> errors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);

        config.changeDirection(getWindow());
        config.removeFocus(getWindow());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.scheduleId = getIntent().getLongExtra("sid", -1L);
        if (this.scheduleId == -1L) {
            finish();
        }

        SchedulesController schedulesController = new SchedulesController(this);
        this.schedule = schedulesController.getScheduleInfoById(this.scheduleId);

        this.uid = this.schedule.getUser_id();
        this.user = this.schedule.getUserSchedules().getName();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.viewScheduleActivity_fab_update);
        if (fab != null) {
            fab.setOnClickListener(this);
        }

        this.drawSchedules();
    }

    private void drawSchedules() {
        Button selectButton = (Button) findViewById(R.id.contentViewSchedule_bt_select);
        TextView nameView = (TextView) findViewById(R.id.contentViewSchedule_tv_name_schedule);
        ToggleButton statusButton = (ToggleButton) findViewById(R.id.contentViewSchedule_tb_status);
        EditText descriptionView = (EditText) findViewById(R.id.contentViewSchedule_et_description);
        DatePicker datePicker = (DatePicker) findViewById(R.id.contentViewSchedule_dp_date);
        TimePicker timePicker = (TimePicker) findViewById(R.id.contentViewSchedule_tp_time);

        if (selectButton != null && nameView != null && statusButton != null && descriptionView != null && datePicker != null && timePicker != null) {
            Date date = new Date(Long.valueOf(this.schedule.getUnix_time()));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            selectButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_dialog_save, 0);
            selectButton.setOnClickListener(this);
            nameView.setText(this.schedule.getTitle());
            statusButton.setChecked(this.schedule.getEnable());
            descriptionView.setText(this.schedule.getDescription());
            datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            timePicker.setIs24HourView(true);
            timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contentViewSchedule_bt_select:
                this.setUser();
                break;
            case R.id.viewScheduleActivity_fab_update:
                this.updateSchedule(v);
                break;
        }
    }

    private void setUser() {
        if (this.uid == -1L) {
            this.startSelectUserIntent();
            return;
        }

        SweetAlertDialog selectUserDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(this.getResources().getStringArray(R.array.user_message)[4])
                .setContentText(this.getResources().getStringArray(R.array.user_message)[5] + " " + this.user)
                .setConfirmText(this.getResources().getStringArray(R.array.global_message)[7])
                .setConfirmClickListener(this.onConfirmClick())
                .setCancelText(this.getResources().getStringArray(R.array.global_message)[6])
                .setCancelClickListener(this.onCancelClick());

        selectUserDialog.setCanceledOnTouchOutside(true);
        selectUserDialog.show();
    }

    private void startSelectUserIntent() {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("email", getIntent().getStringExtra("email"));
        intent.putExtra("tab", 1);
        intent.putExtra("dest", ViewScheduleActivity.class.getSimpleName());
        startActivityForResult(intent, config.REQUEST_INTENT_USER_ACTIVITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == config.REQUEST_INTENT_USER_ACTIVITY) {
            if (data.getLongExtra("uid", -1L) != -1L && !data.getExtras().getString("user", "").equals("")) {
                Button selectButton = (Button) findViewById(R.id.addScheduleView_bt_select);
                if (selectButton != null)
                    selectButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_dialog_save, 0);
                this.uid = data.getExtras().getLong("uid");
                this.user = data.getStringExtra("user");
            }
        }
    }

    private SweetAlertDialog.OnSweetClickListener onConfirmClick() {
        return new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                startSelectUserIntent();
            }
        };
    }

    private SweetAlertDialog.OnSweetClickListener onCancelClick() {
        return new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                Button selectButton = (Button) findViewById(R.id.contentViewSchedule_bt_select);
                if (selectButton != null)
                    selectButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                uid = -1L;
                user = "";
            }
        };
    }

    private void updateSchedule(View v) {
        ToggleButton statusButton = (ToggleButton) findViewById(R.id.contentViewSchedule_tb_status);
        EditText descriptionView = (EditText) findViewById(R.id.contentViewSchedule_et_description);
        DatePicker datePicker = (DatePicker) findViewById(R.id.contentViewSchedule_dp_date);
        TimePicker timePicker = (TimePicker) findViewById(R.id.contentViewSchedule_tp_time);

        if (statusButton != null && descriptionView != null && datePicker != null && timePicker != null) {
            String schedule_description = descriptionView.getText().toString();
            int schedule_day = datePicker.getDayOfMonth();
            int schedule_month = datePicker.getMonth() + 1;
            int schedule_year = datePicker.getYear();
            int schedule_hour = timePicker.getCurrentHour();
            int schedule_minute = timePicker.getCurrentMinute();

            Date date = new Date(schedule_year - 1900, schedule_month - 1, schedule_day, schedule_hour, schedule_minute);

            Bundle args = new Bundle();

            if (this.validateInput(schedule_description, schedule_year, schedule_month, schedule_day, date)) {
                args.putString("description", schedule_description);
                args.putString("date", String.valueOf(date.getTime()));
                args.putBoolean("enable", statusButton.isChecked());
                args.putLong("uid", this.uid);
            }

            if (this.errors.size() > 0) {
                String message = "";

                for (int i = 0; i < errors.size(); i++) {
                    message += "* " + errors.get(i) + "\r\n";
                }

                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getResources().getStringArray(R.array.schedule_message)[2])
                        .setContentText(message)
                        .setConfirmText(getResources().getStringArray(R.array.schedule_message)[3])
                        .show();

                this.errors.clear();
            } else {
                SchedulesController schedulesController = new SchedulesController(this);
                schedulesController.updateSchedulesById(this.scheduleId, args);

                Snackbar.make(v, getResources().getStringArray(R.array.schedule_message)[1], Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    private Boolean validateInput(String schedule_description, int schedule_year, int schedule_month, int schedule_day, Date date) {
        Calendar calendar = Calendar.getInstance();
        if (this.uid == -1L) {
            this.errors.add(getResources().getStringArray(R.array.scheduleAddFragment_errors)[0]);
        }
        if (schedule_description.isEmpty()) {
            this.errors.add(getResources().getStringArray(R.array.scheduleAddFragment_errors)[2]);
        }
        if (schedule_year < calendar.get(Calendar.YEAR) || schedule_month < (calendar.get(Calendar.MONTH) + 1) || schedule_day < calendar.get(Calendar.DAY_OF_MONTH)) {
            this.errors.add(getResources().getStringArray(R.array.scheduleAddFragment_errors)[3]);
        }
        if (date.before(calendar.getTime())) {
            this.errors.add(getResources().getStringArray(R.array.scheduleAddFragment_errors)[4]);
        }

        return this.errors.size() == 0;
    }
}
