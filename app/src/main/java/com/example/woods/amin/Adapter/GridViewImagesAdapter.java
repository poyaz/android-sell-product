package com.example.woods.amin.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.woods.amin.Interface.AddProductDataPassInterface;
import com.example.woods.amin.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GridViewImagesAdapter extends BaseAdapter implements View.OnClickListener {
    private Activity activity = null;
    private List<String> images = null;
    private List<String> selected = null;
    private static LayoutInflater inflater = null;

    public GridViewImagesAdapter(Activity activity, List<String> images) {
        this.activity = activity;
        this.images = images;
        this.selected = new ArrayList<>();
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.images.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.gridview_images_product, null);

            holder = new ViewHolder();
            holder.uri = this.images.get(position);
            holder.mainView = (LinearLayout) rowView.findViewById(R.id.gridViewImagesProduct_main);
            holder.imageView = (ImageView) rowView.findViewById(R.id.gridViewImagesProduct_iv_pic);
            holder.checkBox = (CheckBox) rowView.findViewById(R.id.gridViewImagesProduct_cb_select);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Drawable drawable = Drawable.createFromPath(this.images.get(position));
        holder.imageView.setImageDrawable(drawable);
        holder.mainView.setOnClickListener(this);

        return rowView;
    }

    public void changed() {
        this.notifyDataSetChanged();
    }

    public void deleteSelected() {
        if (this.selected.size() != 0) {
            for (int i = 0; i < this.selected.size(); i++) {
                String uri = this.selected.get(i);
                this.images.remove(uri);
            }
            this.selected.clear();
            this.notifyDataSetChanged();
            ((AddProductDataPassInterface) this.activity).onSetOptionsMenuVisible(false);
        }
    }

    public List<String> getImages() {
        return this.images;
    }

    public Boolean isEmptySelected() {
        return this.selected.size() == 0;
    }

    @Override
    public void onClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag();
        if (holder.checkBox.isChecked()) {
            holder.checkBox.setChecked(false);
            holder.checkBox.setVisibility(View.GONE);
            this.selected.remove(holder.uri);

            if (this.selected.size() == 0) {
                ((AddProductDataPassInterface) this.activity).onSetOptionsMenuVisible(false);
            }
        } else {
            holder.checkBox.setChecked(true);
            holder.checkBox.setVisibility(View.VISIBLE);
            this.selected.add(holder.uri);

            ((AddProductDataPassInterface) this.activity).onSetOptionsMenuVisible(true);
        }
    }

    private static class ViewHolder {
        public String uri;
        public LinearLayout mainView;
        public ImageView imageView;
        public CheckBox checkBox;
    }
}
