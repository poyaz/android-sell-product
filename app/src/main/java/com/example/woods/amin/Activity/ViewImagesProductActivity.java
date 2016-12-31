package com.example.woods.amin.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.woods.amin.Controller.ImagesController;
import com.example.woods.amin.Database.Images;
import com.example.woods.amin.R;
import com.example.woods.amin.View.TextImageSliderView;
import com.example.woods.amin.config;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ViewImagesProductActivity extends AppCompatActivity implements SweetAlertDialog.OnSweetClickListener {
    private SliderLayout sliderShow = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images_product);

        config.changeDirection(getWindow());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Long productId = getIntent().getLongExtra("pid", -1L);
        if (productId == -1L)
            finish();

        ImagesController imagesController = new ImagesController(this);
        List<Images> images = imagesController.getProductImage(productId);

        if (images.size() == 0) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("")
                    .setContentText(this.getResources().getString(R.string.viewProduct_image_not_fount))
                    .setConfirmText(this.getResources().getStringArray(R.array.global_message)[3])
                    .setConfirmClickListener(this)
                    .show();
            return;
        }

        this.sliderShow = (SliderLayout) findViewById(R.id.contentViewImagesProduct_slider_images);
        if (this.sliderShow != null) {
            for (Images image : images) {
                File file = new File(image.getUri());
                if (file.exists()) {
                    String[] name = image.getUri().split("/");
                    TextImageSliderView textSliderView = new TextImageSliderView(this);
                    textSliderView
                            .description(name[name.length - 1])
                            .image(file);

                    this.sliderShow.addSlider(textSliderView);
                }
            }

            this.sliderShow.setPresetTransformer(SliderLayout.Transformer.Accordion);
            this.sliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            this.sliderShow.setCustomIndicator((PagerIndicator) findViewById(R.id.contentViewImagesProduct_slider_indicator));
            this.sliderShow.setDuration(6000);
        }
    }

    @Override
    protected void onStop() {
        if (this.sliderShow != null) {
            this.sliderShow.stopAutoCycle();
        }
        super.onStop();
    }

    @Override
    public void onClick(SweetAlertDialog sweetAlertDialog) {
        finish();
    }
}
