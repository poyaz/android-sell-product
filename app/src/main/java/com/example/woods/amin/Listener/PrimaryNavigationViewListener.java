package com.example.woods.amin.Listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.woods.amin.Activity.AddProductActivity;
import com.example.woods.amin.Activity.OrderProductsActivity;
import com.example.woods.amin.Activity.ScheduleActivity;
import com.example.woods.amin.Activity.SettingsActivity;
import com.example.woods.amin.Activity.UserActivity;
import com.example.woods.amin.Interface.OrderDataPassInterface;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

public class PrimaryNavigationViewListener implements NavigationView.OnNavigationItemSelectedListener {
    private Integer current = null;
    private Context context = null;
    private OrderDataPassInterface orderDataPassInterface = null;
    private Activity activity = null;
    private DrawerLayout drawerLayout = null;
    private View headerView = null;
    private Long basketId = -1L;
    private String emailIntent = "";

    public PrimaryNavigationViewListener(Context context, Toolbar toolbar, Intent intent, Integer current) {
        this.context = context;
        this.activity = (Activity) context;
        this.orderDataPassInterface = (OrderDataPassInterface) context;
        this.current = current;

        if (intent.getExtras() != null) {
            emailIntent = intent.getExtras().getString("email");
        } else {
            emailIntent = "";
        }

       this.createDrawerLayout(toolbar);

        ((TextView) headerView.findViewById(R.id.primaryNavHeader_tv_email)).setText(emailIntent);
    }

    private void createDrawerLayout(Toolbar toolbar) {
        drawerLayout = (DrawerLayout) this.activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this.activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.primaryNavigation_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
    }

    public boolean onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);

            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == this.current)
            return true;

        Intent intent;
        switch (id) {
            case R.id.primaryDrawer_item_viewProduct:
                break;
            case R.id.primaryDrawer_item_addProduct:
                intent = new Intent(this.context, AddProductActivity.class);
                intent.putExtra("email", emailIntent);
                this.activity.startActivityForResult(intent, config.REQUEST_INTENT_ADD_PRODUCT_ACTIVITY);
                break;
            case R.id.primaryDrawer_item_awaitingPayment:
                intent = new Intent(this.context, OrderProductsActivity.class);
                intent.putExtra("email", emailIntent);
                intent.putExtra("orderStatusWhere", config.ORDERS_STATUS_ACCEPT);
                this.activity.startActivity(intent);
                break;
            case R.id.primaryDrawer_item_viewPayment:
                intent = new Intent(this.context, OrderProductsActivity.class);
                intent.putExtra("email", emailIntent);
                this.activity.startActivityForResult(intent, config.REQUEST_INTENT_ORDER_PRODUCTS_ACTIVITY);
                break;
            case R.id.primaryDrawer_item_viewCustomer:
                intent = new Intent(this.context, UserActivity.class);
                intent.putExtra("email", emailIntent);
                this.activity.startActivity(intent);
                break;
            case R.id.primaryDrawer_item_scheduleCustomer:
                intent = new Intent(this.context, ScheduleActivity.class);
                intent.putExtra("email", emailIntent);
                this.activity.startActivity(intent);
                break;
            case R.id.primaryDrawer_item_settings:
                TwoAuthenticateListener twoAuthenticateListener = new TwoAuthenticateListener(this.activity, SettingsActivity.class.getSimpleName(), 0L);
                twoAuthenticateListener.startAuthenticate();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
