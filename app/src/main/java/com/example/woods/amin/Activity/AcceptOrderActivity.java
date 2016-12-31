package com.example.woods.amin.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import com.example.woods.amin.Controller.OrderProductsController;
import com.example.woods.amin.Database.OrderProducts;
import com.example.woods.amin.Fragment.AcceptOrderProductsFragment;
import com.example.woods.amin.Fragment.AcceptOrderVerifyFragment;
import com.example.woods.amin.Interface.AcceptOrderDataPassInterface;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.util.List;

public class AcceptOrderActivity extends AppCompatActivity implements View.OnClickListener, AcceptOrderDataPassInterface {

    private boolean menuVisible = false;
    private List<OrderProducts> orderProducts;
    private int count = 0;
    private int totalPrice = 0;
    private int orderStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_order);

        if (getIntent().getLongExtra("oid", -1L) == -1L) {
            finish();
        }

        config.changeDirection(getWindow());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mViewPager);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.acceptOrderActivity_fab_accept);
        if (fab != null) {
            fab.setOnClickListener(this);
        }

        this.fetchOrderProducts();
    }

    public void fetchOrderProducts() {
        OrderProductsController orderProductsController = new OrderProductsController(this);
        this.orderProducts = orderProductsController.getOrderProductsListByOrderId(getIntent().getLongExtra("oid", -1L));

        for (OrderProducts orderProduct : this.orderProducts) {
            Integer priceOff = Integer.valueOf(orderProduct.getPrice());
            if (!orderProduct.getOff().isEmpty()) {
                String[] off = orderProduct.getOff().split("\\|");
                switch (off[1]) {
                    case "%":
                        priceOff -= (priceOff * Integer.valueOf(off[0])) / 100;
                        break;
                    case "R":
                        priceOff -= Integer.valueOf(off[0]);
                        break;
                }
            }
            this.count += orderProduct.getCount();
            this.totalPrice += priceOff * orderProduct.getCount();
        }
    }

    @Override
    public void onClick(View v) {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        Fragment fragment1 = fragmentList.get(0);
        Fragment fragment2 = fragmentList.get(1);

        if (fragment1 instanceof AcceptOrderVerifyFragment) {
            ((AcceptOrderVerifyFragment) fragment1).onClickFab();
        } else if (fragment2 instanceof AcceptOrderVerifyFragment) {
            ((AcceptOrderVerifyFragment) fragment2).onClickFab();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        if (getIntent().getExtras().getString("dest", "").equals(OrderProductsActivity.class.getSimpleName())) {
            intent = new Intent(this, OrderProductsActivity.class);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }
        if (this.orderStatus != 0) {
            intent.putExtra("close-basket", true);
            intent.putExtra("oid", getIntent().getExtras().getLong("oid"));
            intent.putExtra("status", this.orderStatus);
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_accept_order, menu);

        return this.menuVisible;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSetOrderStatus(int status) {
        this.orderStatus = status;
    }

    @Override
    public void onSetOptionsMenuVisible(Boolean visible) {
        this.menuVisible = visible;
        this.invalidateOptionsMenu();
    }

    @Override
    public List<OrderProducts> onGetOrderProducts() {
        return this.orderProducts;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return AcceptOrderVerifyFragment.newInstance(count, totalPrice);
                case 1:
                    return AcceptOrderProductsFragment.newInstance();
            }

            return AcceptOrderVerifyFragment.newInstance(count, totalPrice);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.acceptOrder_section_verify);
                case 1:
                    return getResources().getString(R.string.acceptOrder_section_product);
            }
            return null;
        }
    }
}
