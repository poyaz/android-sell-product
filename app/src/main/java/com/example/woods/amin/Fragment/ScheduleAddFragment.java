package com.example.woods.amin.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.woods.amin.Activity.ScheduleActivity;
import com.example.woods.amin.Activity.UserActivity;
import com.example.woods.amin.Interface.ScheduleDataPassInterface;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ScheduleAddFragment extends Fragment implements View.OnClickListener {
    private Activity activity = null;
    private View inflaterView = null;
    private Long uid = -1L;
    private String user = "";
    private ScheduleDataPassInterface dataPass = null;
    private ArrayList<String> errors = new ArrayList<>();

    public ScheduleAddFragment() {
    }

    public static ScheduleAddFragment newInstance() {
        ScheduleAddFragment fragment = new ScheduleAddFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflaterView = inflater.inflate(R.layout.fragment_schedule_add, container, false);

        this.inflaterView.findViewById(R.id.addScheduleView_bt_select).setOnClickListener(this);
        ((TimePicker) this.inflaterView.findViewById(R.id.addScheduleView_tp_time)).setIs24HourView(true);

        return this.inflaterView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            this.activity.findViewById(R.id.scheduleActivity_fab_insert).setVisibility(View.VISIBLE);
        }
        if (this.dataPass != null) {
            this.dataPass.onSetOptionsMenuVisible(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
        this.dataPass = (ScheduleDataPassInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
        this.dataPass = null;
    }

    @Override
    public void onClick(View v) {
        if (this.uid == -1L) {
            this.startSelectUserIntent();
            return;
        }

        SweetAlertDialog selectUserDialog = new SweetAlertDialog(this.activity, SweetAlertDialog.NORMAL_TYPE)
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
        Intent intent = new Intent(this.activity, UserActivity.class);
        intent.putExtra("email", this.activity.getIntent().getStringExtra("email"));
        intent.putExtra("tab", 1);
        intent.putExtra("dest", ScheduleActivity.class.getSimpleName());
        this.activity.startActivityForResult(intent, config.REQUEST_INTENT_USER_ACTIVITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == config.REQUEST_INTENT_USER_ACTIVITY) {
            if (data.getLongExtra("uid", -1L) != -1L && !data.getExtras().getString("user", "").equals("")) {
                ((Button) this.inflaterView.findViewById(R.id.addScheduleView_bt_select)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_dialog_save, 0);
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
                ((Button) inflaterView.findViewById(R.id.addScheduleView_bt_select)).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                uid = -1L;
                user = "";
            }
        };
    }

    public void onClickFab() {
        this.errors.clear();

        String schedule_name = ((EditText) this.inflaterView.findViewById(R.id.addScheduleView_et_name)).getText().toString();
        String schedule_description = ((EditText) this.inflaterView.findViewById(R.id.addScheduleView_et_description)).getText().toString();
        DatePicker datePicker = (DatePicker) this.inflaterView.findViewById(R.id.addScheduleView_dp_date);
        int schedule_day = datePicker.getDayOfMonth();
        int schedule_month = datePicker.getMonth() + 1;
        int schedule_year = datePicker.getYear();
        TimePicker timePicker = (TimePicker) this.inflaterView.findViewById(R.id.addScheduleView_tp_time);
        int schedule_hour = timePicker.getCurrentHour();
        int schedule_minute = timePicker.getCurrentMinute();

        Date date = new Date(schedule_year - 1900, schedule_month - 1, schedule_day, schedule_hour, schedule_minute);

        Bundle args = new Bundle();

        if (this.validateInput(schedule_name, schedule_description, schedule_year, schedule_month, schedule_day, date)) {
            args.putString("name", schedule_name);
            args.putString("description", schedule_description);
            args.putString("date", String.valueOf(date.getTime()));
            args.putBoolean("enable", true);
            args.putLong("uid", this.uid);
        }
        args.putStringArrayList("errors", this.errors);

        this.dataPass.onDataPassAdd(args);
    }

    public void reset() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        this.errors.clear();

        this.uid = -1L;
        this.user = "";
        ((Button) inflaterView.findViewById(R.id.addScheduleView_bt_select)).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        ((EditText) this.inflaterView.findViewById(R.id.addScheduleView_et_name)).setText("");
        ((EditText) this.inflaterView.findViewById(R.id.addScheduleView_et_description)).setText("");
        ((DatePicker) this.inflaterView.findViewById(R.id.addScheduleView_dp_date)).updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        TimePicker timePicker = (TimePicker) this.inflaterView.findViewById(R.id.addScheduleView_tp_time);
        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }

    private Boolean validateInput(String schedule_name, String schedule_description, int schedule_year, int schedule_month, int schedule_day, Date date) {
        Calendar calendar = Calendar.getInstance();
        if (this.uid == -1L) {
            this.errors.add(this.inflaterView.getResources().getStringArray(R.array.scheduleAddFragment_errors)[0]);
        }
        if (schedule_name.isEmpty()) {
            this.errors.add(this.inflaterView.getResources().getStringArray(R.array.scheduleAddFragment_errors)[1]);
        }
        if (schedule_description.isEmpty()) {
            this.errors.add(this.inflaterView.getResources().getStringArray(R.array.scheduleAddFragment_errors)[2]);
        }
        if (schedule_year < calendar.get(Calendar.YEAR) || schedule_month < (calendar.get(Calendar.MONTH) + 1) || schedule_day < calendar.get(Calendar.DAY_OF_MONTH)) {
            this.errors.add(this.inflaterView.getResources().getStringArray(R.array.scheduleAddFragment_errors)[3]);
        }
        if (date.before(calendar.getTime())) {
            this.errors.add(this.inflaterView.getResources().getStringArray(R.array.scheduleAddFragment_errors)[4]);
        }

        return this.errors.size() == 0;
    }
}
