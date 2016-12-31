package com.example.woods.amin.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.woods.amin.Activity.OrderProductsActivity;
import com.example.woods.amin.Activity.ScheduleActivity;
import com.example.woods.amin.Activity.UserActivity;
import com.example.woods.amin.Activity.ViewScheduleActivity;
import com.example.woods.amin.Database.Users;
import com.example.woods.amin.R;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * TODO: document your custom view class.
 */
public class ListViewUserAdapter extends BaseAdapter implements View.OnClickListener {
    private Activity activity;
    private List<Users> users;
    private String dest;
    private static LayoutInflater inflater = null;

    public ListViewUserAdapter(Activity activity, List<Users> users) {
        this._ListViewUserAdapter(activity, users, null);
    }

    public ListViewUserAdapter(Activity activity, List<Users> users, String dest) {
        this._ListViewUserAdapter(activity, users, dest);
    }

    private void _ListViewUserAdapter(Activity activity, List<Users> users, String dest) {
        this.activity = activity;
        this.users = users;
        this.dest = dest;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.users.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return this.users.get(position).getId();
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

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.uid = this.getItemId(position);
        holder.number = this.users.get(position).getMobile() + "\r\n" + this.users.get(position).getPhone();
        holder.address = this.users.get(position).getAddress();
        holder.nameView.setText(this.users.get(position).getName());

        rowView.setOnClickListener(this);

        return rowView;
    }

    @Override
    public void onClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag();

        if (this.dest.equals(ScheduleActivity.class.getSimpleName())) {
            this.onClickScheduleActivity(holder);
        } else if (this.dest.equals(ViewScheduleActivity.class.getSimpleName())) {
            this.onClickViewScheduleActivity(holder);
        } else if (this.activity.getClass().getSimpleName().equals(OrderProductsActivity.class.getSimpleName())) {
            this.onClickOrderProductsActivity(holder);
        } else if (this.activity.getClass().getSimpleName().equals(UserActivity.class.getSimpleName())) {
            this.onClickUserActivity(holder);
        }
    }

    private void onClickScheduleActivity(ViewHolder holder) {
        Intent intent = new Intent(this.activity, ScheduleActivity.class);
        intent.putExtra("uid", holder.uid);
        intent.putExtra("user", holder.nameView.getText().toString());
        this.activity.setResult(Activity.RESULT_OK, intent);
        this.activity.finish();
    }

    private void onClickViewScheduleActivity(ViewHolder holder) {
        Intent intent = new Intent(this.activity, ViewScheduleActivity.class);
        intent.putExtra("uid", holder.uid);
        intent.putExtra("user", holder.nameView.getText().toString());
        this.activity.setResult(Activity.RESULT_OK, intent);
        this.activity.finish();
    }

    private void onClickOrderProductsActivity(ViewHolder holder) {
        Intent intent = new Intent(this.activity, OrderProductsActivity.class);
        intent.putExtra("key", "uid");
        intent.putExtra("value", holder.uid);
        this.activity.setResult(Activity.RESULT_OK, intent);
        this.activity.finish();
    }

    private void onClickUserActivity(ViewHolder holder) {
        String contentText = "" +
                this.activity.getResources().getString(R.string.dialogViewUser_tv_number) + "\r\n" +
                holder.number + "\r\n" +
                this.activity.getResources().getString(R.string.dialogViewUser_tv_address) + "\r\n" +
                holder.address;

        new SweetAlertDialog(this.activity, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(holder.nameView.getText().toString())
                .setContentText(contentText)
                .show();
    }

    public void insert(Bundle data, int position) {
        if (position > this.users.size())
            return;

        Users newUser = new Users();
        newUser.setId(data.getLong("id", -1L));
        newUser.setName(data.getString("name", null));
        newUser.setMobile(data.getString("mobile", null));
        newUser.setPhone(data.getString("phone", null));
        newUser.setAddress(data.getString("address", null));

        this.users.add(position, newUser);
        this.notifyDataSetChanged();
    }

    private static class ViewHolder {
        public Long uid;
        public String number;
        public String address;
        public TextView nameView;
    }
}
