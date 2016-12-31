package com.example.woods.amin.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.woods.amin.Controller.ImagesController;
import com.example.woods.amin.Controller.ProductsController;
import com.example.woods.amin.Fragment.AddProductPicturesFragment;
import com.example.woods.amin.Fragment.AddProductViewFragment;
import com.example.woods.amin.Interface.AddProductDataPassInterface;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddProductActivity extends AppCompatActivity implements AddProductDataPassInterface, View.OnClickListener, DialogInterface.OnDismissListener, Runnable {
    private Long editIntent = -1L;
    private String emailIntent = "";
    private SweetAlertDialog loadingDialog = null;
    private Handler handler = null;
    private Runnable runnable = null;
    private Bundle viewData = null;
    private Bundle imagesData = null;
    private View dialogView = null;
    private Boolean menuVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        config.changeDirection(getWindow());

        this.editIntent = getIntent().getLongExtra("edit", -1L);
        this.emailIntent = getIntent().getStringExtra("email");

        if (this.editIntent != -1L) {
            setTitle(R.string.title_activity_edit_product);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.addProductActivity_toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.addProductActivity_container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.addProductActivity_tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mViewPager);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addProductActivity_fab_insert);
        if (fab != null) {
            fab.setOnClickListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_add_product, menu);

        return this.menuVisible;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addProductMenu_item_delete:
                List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
                Fragment fragment1 = fragmentList.get(0);
                Fragment fragment2 = fragmentList.get(1);

                if (fragment1 instanceof AddProductPicturesFragment) {
                    ((AddProductPicturesFragment) fragment1).deletePictures();
                } else if (fragment2 instanceof AddProductPicturesFragment) {
                    ((AddProductPicturesFragment) fragment2).deletePictures();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        this.loadingDialog = new SweetAlertDialog(AddProductActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        this.loadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        this.loadingDialog.setTitleText(getResources().getStringArray(R.array.global_message)[0]);
        this.loadingDialog.setCancelable(false);
        this.loadingDialog.setOnDismissListener(onCloseLoadingDialog(v));
        this.loadingDialog.show();

        this.handler = new Handler();
        this.runnable = onRunnableListener();
        this.handler.postDelayed(runnable, 3000);

        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        Fragment fragment1 = fragmentList.get(0);
        Fragment fragment2 = fragmentList.get(1);

        if (fragment1 instanceof AddProductViewFragment && fragment2 instanceof AddProductPicturesFragment) {
            ((AddProductViewFragment) fragment1).onClickFab();
            ((AddProductPicturesFragment) fragment2).onClickFab();
        } else if (fragment1 instanceof AddProductPicturesFragment && fragment2 instanceof AddProductViewFragment) {
            ((AddProductPicturesFragment) fragment1).onClickFab();
            ((AddProductViewFragment) fragment2).onClickFab();
        }
    }

    public Runnable onRunnableListener() {
        return this;
    }

    @Override
    public void run() {
        if (this.loadingDialog.isShowing()) {
            this.loadingDialog.dismiss();
        }
    }

    public DialogInterface.OnDismissListener onCloseLoadingDialog(final View view) {
        this.dialogView = view;
        return this;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        this.handler.removeCallbacks(this.runnable);

        ArrayList<String> errors = this.viewData.getStringArrayList("errors");
        if (errors != null && errors.size() > 0) {
            String message = "";

            for (int i = 0; i < errors.size(); i++) {
                message += "* " + errors.get(i) + "\r\n";
            }

            new SweetAlertDialog(AddProductActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getResources().getStringArray(R.array.addProductActivity_message)[2])
                    .setContentText(message)
                    .setConfirmText(getResources().getStringArray(R.array.addProductActivity_message)[3])
                    .show();
        } else {
            String message;
            ProductsController productsController = new ProductsController(AddProductActivity.this);
            ImagesController imagesController = new ImagesController(AddProductActivity.this);

            if (this.editIntent != -1L) {
                message = getResources().getStringArray(R.array.addProductActivity_message)[1];
                productsController.editProduct(this.editIntent, this.viewData);
                imagesController.editProductImage(this.editIntent, this.viewData.getString("image"), this.imagesData);
            } else {
                message = getResources().getStringArray(R.array.addProductActivity_message)[0];
                productsController.addNewProduct(this.viewData);
                imagesController.addNewImages(productsController.getId(), this.viewData.getString("image"), this.imagesData);
            }

            Intent HomeActivityIntent = new Intent(AddProductActivity.this, HomeActivity.class);
            HomeActivityIntent.putExtra("message", message);
            setResult(Activity.RESULT_OK, HomeActivityIntent);
            finish();
        }
    }

    @Override
    public void onDataPassView(Bundle data) {
        if (data.getBoolean("error_exist", false)) {
            Intent HomeActivityIntent = new Intent(AddProductActivity.this, HomeActivity.class);
            HomeActivityIntent.putExtra("message", getResources().getStringArray(R.array.addProductActivity_message)[4]);
            setResult(Activity.RESULT_OK, HomeActivityIntent);
            finish();
        }
        this.viewData = data;
    }

    @Override
    public void onDataPassPictures(Bundle data) {
        this.imagesData = data;
    }

    @Override
    public void onSetOptionsMenuVisible(Boolean visible) {
        this.menuVisible = visible;
        this.invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return AddProductViewFragment.newInstance(editIntent);
                case 1:
                    return AddProductPicturesFragment.newInstance(editIntent);
            }

            return AddProductViewFragment.newInstance(editIntent);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.addProduct_section_view);
                case 1:
                    return getResources().getString(R.string.addProduct_section_pictures);
            }

            return null;
        }
    }
}
