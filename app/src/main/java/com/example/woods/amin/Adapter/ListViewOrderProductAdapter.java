package com.example.woods.amin.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.woods.amin.Activity.AcceptOrderActivity;
import com.example.woods.amin.Activity.HomeActivity;
import com.example.woods.amin.Activity.ViewProductActivity;
import com.example.woods.amin.Database.OrderProducts;
import com.example.woods.amin.Database.Products;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.io.File;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class ListViewOrderProductAdapter extends BaseAdapter implements View.OnClickListener {
    private Activity activity;
    private List<OrderProducts> orderProducts;
    private LongSparseArray<Integer> pos = null;
    private static LayoutInflater inflater = null;

    public ListViewOrderProductAdapter(Activity activity, List<OrderProducts> orderProducts) {
        this.activity = activity;
        this.orderProducts = orderProducts;
        this.pos = new LongSparseArray<>();
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.orderProducts.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (this.activity.getClass().getSimpleName().equals(AcceptOrderActivity.class.getSimpleName())) {
            return getViewAcceptOrderActivity(position, convertView);
        } else {
            return getViewHomeActivity(position, convertView);
        }
    }

    private View getViewHomeActivity(int position, View convertView) {
        View rowView = convertView;
        DialogViewHolder holder;

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.listview_order_product, null);

            holder = new DialogViewHolder();
            holder.titleView = (TextView) rowView.findViewById(R.id.listViewOrderProduct_tv_title);
            holder.countView = (TextView) rowView.findViewById(R.id.listViewOrderProduct_tv_count);

            rowView.setTag(holder);
        } else {
            holder = (DialogViewHolder) convertView.getTag();
        }

        holder.oid = this.orderProducts.get(position).getOrder_id();
        holder.pid = this.orderProducts.get(position).getProduct_id();
        holder.titleView.setText(this.orderProducts.get(position).getOrderProductProduct().getTitle());
        holder.countView.setText(("." + this.orderProducts.get(position).getCount() + "X"));
        this.pos.put(holder.pid, position);

        rowView.setOnClickListener(this);

        return rowView;
    }

    private View getViewAcceptOrderActivity(int position, View convertView) {
        View rowView = convertView;
        ImageViewHolder holder;

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.listview_accept_order, null);

            holder = new ImageViewHolder();
            holder.imageView = (ImageView) rowView.findViewById(R.id.listViewAcceptOrder_iv_pic);
            holder.titleView = (TextView) rowView.findViewById(R.id.listViewAcceptOrder_tv_title);
            holder.countView = (TextView) rowView.findViewById(R.id.listViewAcceptOrder_tv_count);
            holder.lastPriceView = (TextView) rowView.findViewById(R.id.listViewAcceptOrder_tv_price);

            rowView.setTag(holder);
        } else {
            holder = (ImageViewHolder) convertView.getTag();
        }

        holder.oid = this.orderProducts.get(position).getOrder_id();
        holder.pid = this.orderProducts.get(position).getProduct_id();

        Products product = this.orderProducts.get(position).getOrderProductProduct();
        Integer priceOff = Integer.valueOf(product.getPrice());
        if (!product.getOff().isEmpty()) {
            String[] off = product.getOff().split("\\|");
            switch (off[1]) {
                case "%":
                    priceOff -= (priceOff * Integer.valueOf(off[0])) / 100;
                    break;
                case "R":
                    priceOff -= Integer.valueOf(off[0]);
                    break;
            }
        }
        holder.product = product;
        holder.defaultPrice = priceOff;

        File file = new File(this.orderProducts.get(position).getOrderProductProduct().getProductImages().get(0).getUri());
        if (file.exists()) {
            holder.imageView.setImageDrawable(Drawable.createFromPath(this.orderProducts.get(position).getOrderProductProduct().getProductImages().get(0).getUri()));
        }
        holder.titleView.setText(this.orderProducts.get(position).getOrderProductProduct().getTitle());
        holder.countView.setText((this.orderProducts.get(position).getOrderProductProduct().getCount() + "X"));
        holder.lastPriceView.setText(String.valueOf(priceOff * this.orderProducts.get(position).getCount()));

        return rowView;
    }

    @Override
    public void onClick(View v) {
        DialogViewHolder holder = (DialogViewHolder) v.getTag();

        if (this.activity.getClass().getSimpleName().equals(HomeActivity.class.getSimpleName())) {
            Intent intent = new Intent(this.activity, ViewProductActivity.class);
            intent.putExtra("oid", holder.oid);
            intent.putExtra("pid", holder.pid);
            this.activity.startActivityForResult(intent, config.REQUEST_INTENT_VIEW_PRODUCT_ACTIVITY);
        }
    }

    public void delete(Long pid) {
        if (this.getCount() > 0) {
            int position = this.pos.get(pid);
            this.orderProducts.remove(position);
            this.notifyDataSetChanged();
        }
    }

    public void update(Long pid, int count) {
        Log.v("update", "update");
        if (this.getCount() > 0) {
            this.orderProducts.get(this.pos.get(pid)).setCount(count);
            Log.v("string", this.orderProducts.get(this.pos.get(pid)).getCount() + "");
            this.notifyDataSetChanged();
        }
    }

    private static class DialogViewHolder {
        public Long oid;
        public Long pid;
        public TextView titleView;
        public TextView countView;
    }

    private static class ImageViewHolder {
        public Long oid;
        public Long pid;
        public Products product;
        public Integer defaultPrice;
        public ImageView imageView;
        public TextView titleView;
        public TextView countView;
        public TextView lastPriceView;
    }
}
