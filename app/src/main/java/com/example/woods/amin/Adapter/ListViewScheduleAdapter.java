package com.example.woods.amin.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.woods.amin.Activity.ViewScheduleActivity;
import com.example.woods.amin.Database.Schedules;
import com.example.woods.amin.Interface.ScheduleDataPassInterface;
import com.example.woods.amin.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ListViewScheduleAdapter extends BaseAdapter implements View.OnClickListener, View.OnLongClickListener, SweetAlertDialog.OnSweetClickListener {
    private Activity activity;
    private List<Schedules> schedules;
    private ScheduleDataPassInterface scheduleDataPassInterface;
    private static LayoutInflater inflater = null;
    private List<Long> selected = null;
    private List<Integer> position = null;

    public ListViewScheduleAdapter(Activity activity, List<Schedules> schedules) {
        this.activity = activity;
        this.scheduleDataPassInterface = (ScheduleDataPassInterface) this.activity;
        this.schedules = schedules;
        this.selected = new ArrayList<>();
        this.position = new ArrayList<>();
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.schedules.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return this.schedules.get(position).getId();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;

        if (convertView == null) {
            rowView = inflater.inflate(android.R.layout.simple_list_item_1, null);

            holder = new ViewHolder();
            holder.nameView = (TextView) rowView.findViewById(android.R.id.text1);
            holder.nameView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            holder.nameView.setTextDirection(View.TEXT_DIRECTION_ANY_RTL);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.position = position;
        holder.sid = this.getItemId(position);
        holder.nameView.setText(this.schedules.get(position).getTitle());

        if (convertView != null) {
            if (this.selected.indexOf(this.getItemId(position)) != -1) {
                convertView.setBackgroundColor(this.activity.getResources().getColor(R.color.error_stroke_color));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        rowView.setOnClickListener(this);
        rowView.setOnLongClickListener(this);

        return rowView;
    }

    @Override
    public void onClick(View v) {
        if (this.selected.size() != 0) {
            this.updateSelect(v);
        } else {
            ViewHolder holder = (ViewHolder) v.getTag();
            Intent intent = new Intent(this.activity, ViewScheduleActivity.class);
            intent.putExtra("sid", holder.sid);
            this.activity.startActivity(intent);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        this.scheduleDataPassInterface.onSetOptionsMenuVisible(true);
        this.updateSelect(v);

        return false;
    }

    @Override
    public void onClick(SweetAlertDialog sweetAlertDialog) {
        if (this.selected.size() == 0)
            return;

        sweetAlertDialog.setTitleText(this.activity.getResources().getStringArray(R.array.global_message)[10])
                .setContentText(this.activity.getResources().getStringArray(R.array.global_message)[11])
                .setConfirmText(this.activity.getResources().getStringArray(R.array.global_message)[3])
                .setConfirmClickListener(null)
                .showCancelButton(false)
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

        List<Schedules> removeSchedules = new ArrayList<>();
        for (int i = 0; i < this.schedules.size(); i++) {
            if (this.selected.indexOf(this.schedules.get(i).getId()) != -1) {
                removeSchedules.add(this.schedules.get(i));
            }
        }

        this.schedules.removeAll(removeSchedules);

        this.scheduleDataPassInterface.onSetOptionsMenuVisible(false);
        this.scheduleDataPassInterface.onDataPassDelete(this.selected);
        this.selected.clear();
        this.position.clear();
        this.notifyDataSetChanged();
    }

    private void updateSelect(View v) {
        ViewHolder holder = (ViewHolder) v.getTag();
        Long sid = holder.sid;
        Integer position = holder.position;
        if (this.selected.indexOf(sid) == -1) {
            this.selected.add(sid);
            this.position.add(position);
        } else {
            this.selected.remove(sid);
            this.position.remove((int) position);

            if (this.selected.size() == 0) {
                this.scheduleDataPassInterface.onSetOptionsMenuVisible(false);
            }
        }
        this.notifyDataSetChanged();
    }

    public Boolean isEmptySelected() {
        return this.selected.size() == 0;
    }

    public void removeAllSelected() {
        this.selected.clear();
        this.position.clear();
        this.notifyDataSetChanged();
        this.scheduleDataPassInterface.onSetOptionsMenuVisible(false);
    }

    public void deleteSelected() {
        new SweetAlertDialog(this.activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(this.activity.getResources().getStringArray(R.array.global_message)[8])
                .setContentText(this.activity.getResources().getStringArray(R.array.global_message)[9])
                .setConfirmText(this.activity.getResources().getStringArray(R.array.global_message)[6])
                .setCancelText(this.activity.getResources().getStringArray(R.array.global_message)[3])
                .setConfirmClickListener(this)
                .show();
    }

    public void insert(Bundle data, int position) {
        if (position > this.schedules.size())
            return;

        Log.v("add", data.getLong("sid", -1L) + "");
        Schedules newSchedule = new Schedules();
        newSchedule.setId(data.getLong("sid", -1L));
        newSchedule.setTitle(data.getString("name"));
        newSchedule.setDescription(data.getString("description"));
        newSchedule.setUnix_time(data.getString("date"));
        newSchedule.setEnable(data.getBoolean("enable"));
        newSchedule.setUser_id(data.getLong("uid"));

        this.schedules.add(position, newSchedule);
        this.notifyDataSetChanged();
    }

    private static class ViewHolder {
        public Integer position;
        public Long sid;
        public TextView nameView;
    }
}
