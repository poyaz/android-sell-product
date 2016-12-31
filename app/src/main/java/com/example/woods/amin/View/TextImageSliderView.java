package com.example.woods.amin.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.example.woods.amin.R;

public class TextImageSliderView extends BaseSliderView {
    public TextImageSliderView(Context context) {
        super(context);
    }

    @Override
    public View getView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.text_image_slider_view, null);
        ImageView target = (ImageView) view.findViewById(R.id.textImageSliderView_image_primary);
        TextView description = (TextView) view.findViewById(R.id.textImageSliderView_tv_description);
        description.setText(getDescription());
        bindEventAndShow(view, target);

        return view;
    }
}
