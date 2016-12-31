package com.example.woods.amin.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.example.woods.amin.Fragment.OrdersViewFragment;
import com.example.woods.amin.Fragment.UserViewFragment;
import com.example.woods.amin.Other.OrderParcelable;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.util.ArrayList;
import java.util.List;

public class OrderProductsActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String dest;
    private Integer orderStatusWhere;
    private List<Long> closeBaskets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_products);

        config.changeDirection(getWindow());

        this.closeBaskets = new ArrayList<>();
        this.dest = getIntent().getExtras().getString("dest", "");
        this.orderStatusWhere = getIntent().getIntExtra("orderStatusWhere", -1);
        Integer tab = getIntent().getIntExtra("tab", 1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.orderProductActivity_toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.orderProductActivity_container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.orderProductActivity_tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mViewPager);
            if (tab == 1) {
                setTitle(getResources().getString(R.string.orderProduct_section_orderView));
                tabLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_product, menu);

        return this.orderStatusWhere == -1;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Integer select = -1;

        switch (id) {
            case R.id.orderProductMenu_item_preOrder:
                select = config.ORDERS_STATUS_PRE_ORDER;
                break;
            case R.id.orderProductMenu_item_accept:
                select = config.ORDERS_STATUS_ACCEPT;
                break;
            case R.id.orderProductMenu_item_pay:
                select = config.ORDERS_STATUS_PAY;
                break;
            case R.id.orderProductMenu_item_send:
                select = config.ORDERS_STATUS_SEND;
                break;
        }

        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        Fragment fragment1 = fragmentList.get(0);
        Fragment fragment2 = fragmentList.get(1);

        if (fragment1 instanceof OrdersViewFragment) {
            ((OrdersViewFragment) fragment1).changeOrdersStatus(select);
        } else if (fragment2 instanceof OrdersViewFragment) {
            ((OrdersViewFragment) fragment2).changeOrdersStatus(select);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("key", "close-basket");
        intent.putExtra("value", new OrderParcelable(this.closeBaskets));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (orderStatusWhere != -1) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        } else if (requestCode == config.REQUEST_INTENT_ACCEPT_ORDER_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK && data.getBooleanExtra("close-basket", false) && this.closeBaskets.indexOf(data.getExtras().getLong("oid")) == -1) {
                this.closeBaskets.add(data.getExtras().getLong("oid"));
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private Context context = null;

        SectionsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return OrdersViewFragment.newInstance(dest, orderStatusWhere);
                case 1:
                    return UserViewFragment.newInstance(dest);
            }

            return OrdersViewFragment.newInstance(dest, orderStatusWhere);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return this.context.getResources().getString(R.string.orderProduct_section_orderView);
                case 1:
                    return this.context.getResources().getString(R.string.orderProduct_section_userView);
            }
            return null;
        }
    }
}
