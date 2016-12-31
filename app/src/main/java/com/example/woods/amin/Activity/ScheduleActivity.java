package com.example.woods.amin.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.example.woods.amin.Controller.SchedulesController;
import com.example.woods.amin.Fragment.ScheduleAddFragment;
import com.example.woods.amin.Fragment.ScheduleViewFragment;
import com.example.woods.amin.Fragment.UserViewFragment;
import com.example.woods.amin.Interface.ScheduleDataPassInterface;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ScheduleActivity extends AppCompatActivity implements View.OnClickListener, ScheduleDataPassInterface, Runnable, DialogInterface.OnDismissListener {
    private String emailIntent = null;
    private boolean menuVisible = false;
    private Bundle addData = null;
    private View dialogView = null;
    private SweetAlertDialog loadingDialog = null;
    private Handler handler = null;
    private Runnable runnable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        config.changeDirection(getWindow());
        config.removeFocus(getWindow());

        this.emailIntent = getIntent().getStringExtra("email");

        Toolbar toolbar = (Toolbar) findViewById(R.id.scheduleActivity_toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.scheduleActivity_container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.scheduleActivity_tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mViewPager);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.scheduleActivity_fab_insert);
        if (fab != null) {
            fab.setOnClickListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_schedule, menu);

        return this.menuVisible;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        Fragment fragment1 = fragmentList.get(0);
        Fragment fragment2 = fragmentList.get(1);
        ScheduleViewFragment scheduleViewFragment = null;

        if (fragment1 instanceof ScheduleViewFragment) {
            scheduleViewFragment = (ScheduleViewFragment) fragment1;
        } else if (fragment2 instanceof ScheduleViewFragment) {
            scheduleViewFragment = (ScheduleViewFragment) fragment2;
        }

        if (scheduleViewFragment != null)
            switch (item.getItemId()) {
                case R.id.scheduleMenu_item_delete:
                    scheduleViewFragment.deleteSelected();
                    break;
                case R.id.scheduleMenu_item_cancel:
                    scheduleViewFragment.removeAllSelected();
                    break;
            }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        this.loadingDialog = new SweetAlertDialog(ScheduleActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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

        if (fragment1 instanceof ScheduleAddFragment) {
            ((ScheduleAddFragment) fragment1).onClickFab();
        } else if (fragment2 instanceof ScheduleAddFragment) {
            ((ScheduleAddFragment) fragment2).onClickFab();
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

            new SweetAlertDialog(ScheduleActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getResources().getStringArray(R.array.schedule_message)[2])
                    .setContentText(message)
                    .setConfirmText(getResources().getStringArray(R.array.schedule_message)[3])
                    .show();
        } else {
            Snackbar.make(this.dialogView, getResources().getStringArray(R.array.schedule_message)[0], Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();

            SchedulesController schedulesController = new SchedulesController(this);
            schedulesController.addNewSchedule(this.addData);
            this.addData.putLong("sid", schedulesController.getId());

            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            Fragment fragment1 = fragmentList.get(0);
            Fragment fragment2 = fragmentList.get(1);

            if (fragment1 instanceof ScheduleAddFragment && fragment2 instanceof ScheduleViewFragment) {
                ((ScheduleViewFragment) fragment2).setNewData(this.addData);
                ((ScheduleAddFragment) fragment1).reset();
            } else if (fragment1 instanceof ScheduleViewFragment && fragment2 instanceof ScheduleAddFragment) {
                ((ScheduleViewFragment) fragment1).setNewData(this.addData);
                ((ScheduleAddFragment) fragment2).reset();
            }
            this.addData = null;
        }
    }

    @Override
    public void onDataPassAdd(Bundle data) {
        this.addData = data;
    }

    @Override
    public void onSetOptionsMenuVisible(Boolean visible) {
        this.menuVisible = visible;
        this.invalidateOptionsMenu();
    }

    @Override
    public void onDataPassDelete(List<Long> items) {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        Fragment fragment1 = fragmentList.get(0);
        Fragment fragment2 = fragmentList.get(1);

        if (fragment1 instanceof ScheduleViewFragment) {
            ((ScheduleViewFragment) fragment1).delete(items);
        } else if (fragment2 instanceof ScheduleViewFragment) {
            ((ScheduleViewFragment) fragment2).delete(items);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ScheduleViewFragment.newInstance();
                case 1:
                    return ScheduleAddFragment.newInstance();
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
                    return getResources().getString(R.string.schedule_section_view);
                case 1:
                    return getResources().getString(R.string.schedule_section_add);
            }

            return null;
        }
    }
}
