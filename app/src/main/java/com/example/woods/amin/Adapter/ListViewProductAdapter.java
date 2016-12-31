package com.example.woods.amin.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.woods.amin.Activity.AddProductActivity;
import com.example.woods.amin.Activity.ViewProductActivity;
import com.example.woods.amin.Database.Products;
import com.example.woods.amin.Interface.OrderDataPassInterface;
import com.example.woods.amin.Listener.TwoAuthenticateListener;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.io.File;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * TODO: document your custom view class.
 */
public class ListViewProductAdapter extends BaseAdapter implements View.OnClickListener, View.OnLongClickListener, SweetAlertDialog.OnSweetClickListener {
    private Activity activity;
    private OrderDataPassInterface orderDataPassInterface;
    private List<Products> produces;
    private static LayoutInflater inflater = null;
    private SweetAlertDialog editDialog = null;
    private Long pid = -1L;

    public ListViewProductAdapter(Activity activity, List<Products> produces) {
        this.activity = activity;
        this.orderDataPassInterface = (OrderDataPassInterface) activity;
        this.produces = produces;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return this.produces.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return this.produces.get(position).getId();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.listview_product, null);

            holder = new ViewHolder();
            holder.imageView = (ImageView) rowView.findViewById(R.id.listViewProduct_iv_pic);
            holder.titleView = (TextView) rowView.findViewById(R.id.listViewProduct_tv_title);
            holder.priceView = (TextView) rowView.findViewById(R.id.listViewProduct_tv_price);
            holder.offView = (TextView) rowView.findViewById(R.id.listViewProduct_tv_off);
            holder.priceOffView = (TextView) rowView.findViewById(R.id.listViewProduct_tv_price_off);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.pid = this.getItemId(position);
        holder.titleView.setText(this.produces.get(position).getTitle());
        holder.priceView.setText(this.produces.get(position).getPrice());
        holder.offView.setText(this.produces.get(position).getOff().replace("|", "").replace("R", " " + this.activity.getResources().getString(R.string.listViewProduct_tv_price_type)));
        if (!this.produces.get(position).getOff().isEmpty()) {
            String[] off = this.produces.get(position).getOff().split("\\|");
            Integer priceOff = Integer.valueOf(this.produces.get(position).getPrice());
            switch (off[1]) {
                case "%":
                    priceOff -= (priceOff * Integer.valueOf(off[0])) / 100;
                    break;
                case "R":
                    priceOff -= Integer.valueOf(off[0]);
                    break;
            }
            holder.priceView.setPaintFlags(holder.priceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.priceOffView.setText(String.valueOf(priceOff + " "));
        }

        File file = new File(this.produces.get(position).getProductImages().get(0).getUri());
        if (file.exists()) {
            holder.imageView.setImageDrawable(Drawable.createFromPath(this.produces.get(position).getProductImages().get(0).getUri()));
        }

        rowView.setOnClickListener(this);
        rowView.setOnLongClickListener(this);

        return rowView;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this.activity, ViewProductActivity.class);
        intent.putExtra("email", this.activity.getIntent().getExtras().getString("email"));
        intent.putExtra("oid", this.orderDataPassInterface.onGetOrderId());
        intent.putExtra("pid", ((ViewHolder) v.getTag()).pid);
        this.activity.startActivityForResult(intent, config.REQUEST_INTENT_VIEW_PRODUCT_ACTIVITY);
    }

    @Override
    public boolean onLongClick(View v) {
        this.pid = ((ViewHolder) v.getTag()).pid;

        this.editDialog = new SweetAlertDialog(this.activity, SweetAlertDialog.NORMAL_TYPE);
        editDialog.setCanceledOnTouchOutside(true);
        editDialog.setTitleText(this.activity.getResources().getStringArray(R.array.edit_message)[0])
                .setContentText(this.activity.getResources().getStringArray(R.array.edit_message)[1])
                .setConfirmText(this.activity.getResources().getStringArray(R.array.edit_message)[2])
                .setCancelText(null)
                .setConfirmClickListener(this)
                .show();

        return false;
    }

    @Override
    public void onClick(SweetAlertDialog sweetAlertDialog) {
        this.editDialog.dismiss();

        Bundle data = new Bundle();
        data.putString("email", this.activity.getIntent().getStringExtra("email"));
        data.putLong("edit", this.pid);

        TwoAuthenticateListener twoAuthenticateListener = new TwoAuthenticateListener(this.activity, AddProductActivity.class.getSimpleName(), 0L);
        twoAuthenticateListener.setData(data);
        twoAuthenticateListener.startAuthenticate();
    }

    private static class ViewHolder {
        public Long pid;
        public ImageView imageView;
        public TextView titleView;
        public TextView priceView;
        public TextView offView;
        public TextView priceOffView;
    }
}
