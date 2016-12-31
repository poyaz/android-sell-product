package com.example.woods.amin.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.woods.amin.Adapter.ListViewProductAdapter;
import com.example.woods.amin.Controller.ProductsController;
import com.example.woods.amin.Database.Products;
import com.example.woods.amin.Interface.OrderDataPassInterface;
import com.example.woods.amin.Listener.BasketShoppingListener;
import com.example.woods.amin.Listener.PrimaryNavigationViewListener;
import com.example.woods.amin.Other.OrderParcelable;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, OrderDataPassInterface {
    private PrimaryNavigationViewListener primaryNavigationView = null;
    private BasketShoppingListener basketShoppingListener = null;
    private Long basketId = -1L;
    private Long userId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        config.changeDirection(getWindow());

        Toolbar toolbar = (Toolbar) findViewById(R.id.homeActivity_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.homeActivity_fab_view);
        if (fab != null) {
            fab.setOnClickListener(this);
        }

        primaryNavigationView = new PrimaryNavigationViewListener(this, toolbar, getIntent(), R.id.primaryDrawer_item_viewProduct);

        this.basketShoppingListener = new BasketShoppingListener(this, this.basketId, this.userId);
    }

    private void createListViewProduct(String where) {
        TextView noData = (TextView) findViewById(R.id.contentHome_tv_noData);

        ProductsController productsController = new ProductsController(this);
        List<Products> produces = productsController.getProductList(where);

        if (noData != null) {
            if (produces == null || produces.size() == 0) {
                noData.setVisibility(View.VISIBLE);
                noData.setText(getResources().getStringArray(R.array.global_message)[1]);
            } else {
                noData.setVisibility(View.GONE);
            }
        }

        ListView  productListView = (ListView) findViewById(R.id.contentHome_lv_product);
        if (productListView != null) {
            productListView.setAdapter(new ListViewProductAdapter(this, produces));
        }
    }

    @Override
    public void onBackPressed() {
        if (primaryNavigationView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.homeMenu_item_top:
                createListViewProduct("top");
                break;
            case R.id.homeMenu_item_exist:
                createListViewProduct("exist");
                break;
            case R.id.homeMenu_item_all:
                createListViewProduct("");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == config.REQUEST_INTENT_ADD_PRODUCT_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                TextView noData = (TextView) findViewById(R.id.contentHome_tv_noData);
                if (noData != null) {
                    noData.setVisibility(View.GONE);
                }

                View contentView = findViewById(R.id.content_home);
                if (contentView != null) {
                    Snackbar.make(contentView, data.getStringExtra("message"), Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();
                }
            }
        } else if (requestCode == config.REQUEST_INTENT_EDIT_PRODUCTS_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                View contentView = findViewById(R.id.content_home);
                if (contentView != null) {
                    Snackbar.make(contentView, data.getStringExtra("message"), Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();
                }
            }
        } else if (requestCode == config.REQUEST_INTENT_VIEW_PRODUCT_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getLongExtra("oid", -1L) != -1L) {
                    this.basketId = data.getExtras().getLong("oid");
                }
                if (data.getLongExtra("pid", -1L) != -1L && data.getBooleanExtra("delete", false)) {
                    this.basketShoppingListener.onDelete(data.getExtras().getLong("pid"));
                } else if (data.getLongExtra("pid", -1L) != -1L && data.getIntExtra("count", 0) != 0) {
                    this.basketShoppingListener.onUpdate(data.getExtras().getLong("pid"), data.getExtras().getInt("count"));
                }
            }
        } else if (requestCode == config.REQUEST_INTENT_ORDER_PRODUCTS_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK && data.getStringExtra("key") != null && data.getExtras().get("value") != null) {
                switch (data.getStringExtra("key")) {
                    case "uid":
                        if (data.getLongExtra("value", -1L) != -1L) {
                            this.userId = data.getExtras().getLong("value");
                            this.basketShoppingListener.setUserId(this.userId);
                            this.basketShoppingListener.successSelectUser();
                        }
                        break;
                    case "oid":
                        if (data.getLongExtra("value", -1L) != -1L) {
                            this.basketId = data.getExtras().getLong("value");
                            this.basketShoppingListener.setBasketId(this.basketId);
                            this.basketShoppingListener.successSelectOrder();
                        }
                        break;
                    case "close-basket":
                        OrderParcelable orderParcelable = data.getParcelableExtra("value");
                        for (Long oid : orderParcelable.getOrders()) {
                            if (this.basketId.equals(oid)) {
                                this.basketId = -1L;
                                this.userId = -1L;
                                break;
                            }
                        }
                        break;
                }
            }
        } else if (requestCode == config.REQUEST_INTENT_ACCEPT_ORDER_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK && data.getBooleanExtra("close-basket", false)) {
                this.basketId = -1L;
                this.userId = -1L;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("test", "h oid: " + this.basketId);
        Log.v("test", "h uid: " + this.userId);

        this.basketShoppingListener.setBasketId(this.basketId);
        this.basketShoppingListener.setUserId(this.userId);
        createListViewProduct("exist");
    }

    @Override
    public void onClick(View v) {
        this.basketShoppingListener.onClick();
    }

    @Override
    public void onSetOrderId(Long id) {
        this.basketId = id;
    }

    @Override
    public Long onGetOrderId() {
        return this.basketId;
    }

    @Override
    public void onDismissSuccessDialog() {

    }
}
