package com.example.woods.amin.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.woods.amin.Controller.UsersController;
import com.example.woods.amin.Fragment.UserAddFragment;
import com.example.woods.amin.Fragment.UserViewFragment;
import com.example.woods.amin.Interface.UserDataPassInterface;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserActivity extends AppCompatActivity implements UserDataPassInterface, View.OnClickListener, DialogInterface.OnDismissListener, Runnable {
    private String emailIntent = "";
    private Bundle addData = null;
    private SweetAlertDialog loadingDialog = null;
    private Handler handler = null;
    private Runnable runnable = null;
    private View dialogView = null;
    private String dest = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        config.changeDirection(getWindow());

        this.emailIntent = getIntent().getStringExtra("email");
        this.dest = getIntent().getExtras().getString("dest", "");
        Integer tab = getIntent().getIntExtra("tab", 2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.userActivity_toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.userActivity_container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.userActivity_tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mViewPager);
            if (tab == 1) {
                tabLayout.setVisibility(View.GONE);
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.userActivity_fab_insert);
        if (fab != null) {
            fab.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        this.loadingDialog = new SweetAlertDialog(UserActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        this.loadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        this.loadingDialog.setTitleText(getResources().getStringArray(R.array.global_message)[0]);
        this.loadingDialog.setCancelable(false);
        this.loadingDialog.setOnDismissListener(onCloseLoadingDialog(v));
        this.loadingDialog.show();

        this.handler = new Handler();
        this.runnable = onRunnableListener();
        this.handler.postDelayed(runnable, 2000);

        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        Fragment fragment1 = fragmentList.get(0);
        Fragment fragment2 = fragmentList.get(1);

        if (fragment1 instanceof UserAddFragment) {
            ((UserAddFragment) fragment1).onClickFab();
        } else if (fragment2 instanceof UserAddFragment) {
            ((UserAddFragment) fragment2).onClickFab();
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
        handler.removeCallbacks(runnable);

        ArrayList<String> errors = this.addData.getStringArrayList("errors");
        if (errors != null && errors.size() > 0) {
            String message = "";

            for (int i = 0; i < errors.size(); i++) {
                message += "* " + errors.get(i) + "\r\n";
            }

            new SweetAlertDialog(UserActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getResources().getStringArray(R.array.user_message)[2])
                    .setContentText(message)
                    .setConfirmText(getResources().getStringArray(R.array.user_message)[3])
                    .show();
        } else {
            Snackbar.make(this.dialogView, getResources().getStringArray(R.array.user_message)[0], Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();

            UsersController usersController = new UsersController(this);
            usersController.addNewUser(this.addData);
            this.addData.putLong("uid", usersController.getId());

            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            Fragment fragment1 = fragmentList.get(0);
            Fragment fragment2 = fragmentList.get(1);

            if (fragment1 instanceof UserAddFragment && fragment2 instanceof UserViewFragment) {
                ((UserViewFragment) fragment2).setNewData(this.addData);
                ((UserAddFragment) fragment1).reset();
            } else if (fragment1 instanceof UserViewFragment && fragment2 instanceof UserAddFragment) {
                ((UserViewFragment) fragment1).setNewData(this.addData);
                ((UserAddFragment) fragment2).reset();
            }
            this.addData = null;
        }
    }

    @Override
    public void onDataPassAdd(Bundle data) {
        this.addData = data;
    }

    @Override
    public void onSetUserId(Long id) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return UserViewFragment.newInstance(dest.equals("") ? this.getClass().getSimpleName() : dest);
                case 1:
                    return UserAddFragment.newInstance();
            }

            return UserViewFragment.newInstance(this.getClass().getSimpleName());
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.user_section_view);
                case 1:
                    return getResources().getString(R.string.user_section_add);
            }

            return null;
        }
    }
}
