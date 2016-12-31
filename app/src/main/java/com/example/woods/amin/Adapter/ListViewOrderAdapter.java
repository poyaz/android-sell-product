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

import com.example.woods.amin.Activity.AcceptOrderActivity;
import com.example.woods.amin.Database.Orders;
import com.example.woods.amin.Listener.TwoAuthenticateListener;

import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class ListViewOrderAdapter extends BaseAdapter implements View.OnClickListener {
    private Activity activity;
    private List<Orders> orders;
    private String dest;
    private static LayoutInflater inflater = null;

    public ListViewOrderAdapter(Activity activity, List<Orders> orders) {
        this._ListViewOrderAdapter(activity, orders, null);
    }

    public ListViewOrderAdapter(Activity activity, List<Orders> orders, String dest) {
        this._ListViewOrderAdapter(activity, orders, dest);
    }

    private void _ListViewOrderAdapter(Activity activity, List<Orders> orders, String dest) {
        this.activity = activity;
        this.orders = orders;
        this.dest = dest;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.orders.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return this.orders.get(position).getId();
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

        holder.oid = this.getItemId(position);
        holder.nameView.setText(this.orders.get(position).getOrderUsers().getName() + "\r\n\r\n" + this.orders.get(position).getCreate());

        rowView.setOnClickListener(this);

        return rowView;
    }

    @Override
    public void onClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag();

        if (this.dest != null && !this.dest.isEmpty()) {
            if (this.dest.equalsIgnoreCase(AcceptOrderActivity.class.getSimpleName())) {
                Bundle data = new Bundle();
                data.putLong("oid", holder.oid);

                TwoAuthenticateListener twoAuthenticateListener = new TwoAuthenticateListener(this.activity, AcceptOrderActivity.class.getSimpleName(), 0L);
                twoAuthenticateListener.setData(data);
                twoAuthenticateListener.startAuthenticate();
            } else {
                this.onClickWithFinish(holder);
            }
        }
    }

    private void onClickWithFinish(ViewHolder holder) {
        Intent intent = new Intent();
        intent.setClassName(this.activity, this.dest);
        intent.putExtra("key", "oid");
        intent.putExtra("value", holder.oid);
        this.activity.setResult(Activity.RESULT_OK, intent);
        this.activity.finish();
    }

    private static class ViewHolder {
        public Long oid;
        public TextView nameView;
    }
}
