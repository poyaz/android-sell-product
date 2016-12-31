package com.example.woods.amin.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.woods.amin.Controller.OrderProductsController;
import com.example.woods.amin.Controller.ProductsController;
import com.example.woods.amin.Database.OrderProducts;
import com.example.woods.amin.Database.Products;
import com.example.woods.amin.Interface.OrderDataPassInterface;
import com.example.woods.amin.Listener.BasketShoppingListener;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.io.File;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ViewProductActivity extends AppCompatActivity implements View.OnClickListener, OrderDataPassInterface, DialogInterface.OnClickListener, SweetAlertDialog.OnSweetClickListener, DialogInterface.OnDismissListener {
    private BasketShoppingListener basketShoppingListener = null;
    private Long basketId = -1L;
    private Long userId = -1L;
    private Long productId = -1L;
    private View basketView = null;
    private Products product = null;
    private Boolean setCount = false;
    private int count = 0;
    private Boolean isFabOpen = false;
    private FloatingActionButton fabOpen = null;
    private FloatingActionButton fabInsert = null;
    private FloatingActionButton fabImage = null;
    private AlertDialog basketCard = null;
    private OrderProductsController ordersController = null;
    private Boolean delete = false;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        config.changeDirection(getWindow());

        Toolbar toolbar = (Toolbar) findViewById(R.id.userActivity_toolbar);
        setSupportActionBar(toolbar);

        this.productId = getIntent().getLongExtra("pid", -1L);
        if (this.productId == -1L) {
            finish();
        }
        this.basketId = getIntent().getLongExtra("oid", -1L);

        ProductsController productsController = new ProductsController(this);
        this.product = productsController.getProductInfoById(this.productId);
        setTitle(this.product.getTitle());

        this.fabOpen = (FloatingActionButton) findViewById(R.id.viewProductActivity_fab_open);
        this.fabImage = (FloatingActionButton) findViewById(R.id.viewProductActivity_fab_image);
        this.fabInsert = (FloatingActionButton) findViewById(R.id.viewProductActivity_fab_insert);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        this.fabImage.setBackgroundTintList(getResources().getColorStateList(R.color.colorFirstFab));
        this.fabInsert.setBackgroundTintList(getResources().getColorStateList(R.color.colorSecondFab));

        this.fabOpen.setOnClickListener(this);
        this.fabInsert.setOnClickListener(this);
        this.fabImage.setOnClickListener(this);

        this.orderProductsInfo();
        this.drawProducts();
    }

    private void orderProductsInfo() {
        if (this.basketId == -1L || this.productId == -1L)
            return;

        this.ordersController = new OrderProductsController(this);
        List<OrderProducts> orderProducts = ordersController.getOrderProductsListByBasketInfo(this.basketId, this.productId);

        if (orderProducts.size() > 0) {
            this.setCount = true;
            this.count = orderProducts.get(0).getCount();
            this.fabInsert.setBackgroundTintList(getResources().getColorStateList(R.color.material_deep_teal_50));
        }
    }

    private void drawProducts() {
        TextView titleView = (TextView) findViewById(R.id.contentViewProduct_tv_title);
        TextView offView = (TextView) findViewById(R.id.contentViewProduct_tv_off);
        TextView priceView = (TextView) findViewById(R.id.contentViewProduct_tv_price);
        TextView priceOffView = (TextView) findViewById(R.id.contentViewProduct_tv_price_off);
        TextView countView = (TextView) findViewById(R.id.contentViewProduct_tv_count);
        ImageView imageView = (ImageView) findViewById(R.id.contentViewProduct_iv_pic);

        if (titleView == null || offView == null || priceView == null || priceOffView == null || countView == null || imageView == null)
            return;

        titleView.setText(this.product.getTitle());
        priceView.setText(this.product.getPrice());
        offView.setText(this.product.getOff().replace("|", "").replace("R", " " + getResources().getString(R.string.listViewProduct_tv_price_type)));
        countView.setText(String.valueOf(this.product.getCount()));

        File file = new File(this.product.getProductImages().get(0).getUri());
        if (file.exists()) {
            imageView.setImageDrawable(Drawable.createFromPath(this.product.getProductImages().get(0).getUri()));
        }

        if (!this.product.getOff().isEmpty()) {
            String[] off = this.product.getOff().split("\\|");
            Integer priceOff = Integer.valueOf(this.product.getPrice());
            switch (off[1]) {
                case "%":
                    priceOff -= (priceOff * Integer.valueOf(off[0])) / 100;
                    break;
                case "R":
                    priceOff -= Integer.valueOf(off[0]);
                    break;
            }
            priceView.setPaintFlags(priceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            priceOffView.setText(String.valueOf(priceOff));
        }
    }

    private void showSelectDialog(Boolean delete) {
        this.basketView = View.inflate(this, R.layout.product_select, null);

        TextView title = new TextView(this);
        title.setText(getResources().getStringArray(R.array.basket_message)[5]);
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(40, 40, 40, 40);
        title.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        if (!delete) {
            this.basketView.findViewById(R.id.productSelect_ll_delete).setVisibility(View.GONE);
        } else {
            this.basketView.findViewById(R.id.productSelect_bt_delete).setOnClickListener(this);
        }

        ((EditText) this.basketView.findViewById(R.id.productSelect_et_count)).setText(String.valueOf(this.count));

        this.basketCard = new AlertDialog.Builder(this)
                .setCustomTitle(title)
                .setView(basketView)
                .setPositiveButton(getResources().getStringArray(R.array.global_message)[4], this)
                .setNegativeButton(getResources().getStringArray(R.array.global_message)[3], this)
                .show();
    }

    @Override
    public void onBackPressed() {
        Intent HomeActivityIntent = new Intent(ViewProductActivity.this, HomeActivity.class);
        HomeActivityIntent.putExtra("oid", this.basketId);
        HomeActivityIntent.putExtra("pid", this.productId);
        HomeActivityIntent.putExtra("count", this.count);
        if (this.delete) {
            HomeActivityIntent.putExtra("delete", true);
        }
        setResult(Activity.RESULT_OK, HomeActivityIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("test", "vp oid: " + this.basketId);
        Log.v("test", "vp uid: " + this.userId);

        this.basketShoppingListener = new BasketShoppingListener(this, this.basketId, this.userId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.productSelect_bt_delete:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(this.getResources().getStringArray(R.array.basket_message)[9])
                        .setContentText(this.getResources().getStringArray(R.array.basket_message)[10])
                        .setConfirmText(this.getResources().getStringArray(R.array.global_message)[6])
                        .setCancelText(this.getResources().getStringArray(R.array.global_message)[3])
                        .setConfirmClickListener(this)
                        .show();
                break;
            case R.id.viewProductActivity_fab_open:
                this.fabClickListener();
                break;
            case R.id.viewProductActivity_fab_insert:
                if (this.basketId == -1L) {
                    this.basketShoppingListener.createBasket();
                } else {
                    showSelectDialog(this.setCount);
                }
                break;
            case R.id.viewProductActivity_fab_image:
                Intent intent = new Intent(this, ViewImagesProductActivity.class);
                intent.putExtra("pid", this.productId);
                startActivity(intent);
                this.fabClickListener();
                break;
        }
    }

    private void fabClickListener() {
        if (this.isFabOpen) {
            this.fabOpen.startAnimation(rotate_backward);
            this.fabImage.startAnimation(fab_close);
            this.fabInsert.startAnimation(fab_close);
            this.fabImage.setClickable(false);
            this.fabInsert.setClickable(false);
            this.isFabOpen = false;
        } else {
            this.fabOpen.startAnimation(rotate_forward);
            this.fabImage.startAnimation(fab_open);
            this.fabInsert.startAnimation(fab_open);
            this.fabImage.setClickable(true);
            this.fabInsert.setClickable(true);
            this.isFabOpen = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == config.REQUEST_INTENT_ORDER_PRODUCTS_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK && data.getStringExtra("key") != null && data.getLongExtra("value", -1L) != -1L) {
                switch (data.getStringExtra("key")) {
                    case "uid":
                        this.userId = data.getExtras().getLong("value");
                        break;
                    case "oid":
                        this.basketId = data.getExtras().getLong("value");
                        this.basketShoppingListener.successSelectOrder();
                        break;
                }
            }
        }
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
        this.orderProductsInfo();
        showSelectDialog(false);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            case DialogInterface.BUTTON_POSITIVE:
                String countString = ((EditText) basketView.findViewById(R.id.productSelect_et_count)).getText().toString();
                int count = Integer.parseInt(!countString.isEmpty() ? countString : "0");
                if (count == 0) {
                    new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(this.getResources().getStringArray(R.array.global_message)[5])
                            .setContentText(this.getResources().getStringArray(R.array.basket_message)[6])
                            .setConfirmText(this.getResources().getStringArray(R.array.global_message)[3])
                            .show();
                    return;
                } else if (count > this.product.getCount()) {
                    new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(this.getResources().getStringArray(R.array.global_message)[5])
                            .setContentText(this.getResources().getStringArray(R.array.basket_message)[7])
                            .setConfirmText(this.getResources().getStringArray(R.array.global_message)[3])
                            .show();
                    return;
                }
                this.count = count;
                this.fabInsert.setBackgroundTintList(getResources().getColorStateList(R.color.material_deep_teal_50));

                OrderProductsController orderProductsController = new OrderProductsController(ViewProductActivity.this);
                if (this.setCount) {
                    orderProductsController.updateCountOrderProduct(this.basketId, this.productId, count);
                } else {
                    this.delete = false;
                    this.setCount = true;

                    Bundle data = new Bundle();
                    data.putString("price", this.product.getPrice());
                    data.putInt("count", count);
                    data.putString("off", this.product.getOff());
                    data.putLong("pid", this.productId);
                    data.putLong("oid", this.basketId);

                    orderProductsController.addNewOrderProduct(data);
                }
                break;
        }
    }

    @Override
    public void onClick(SweetAlertDialog sweetAlertDialog) {
        sweetAlertDialog.dismiss();
        SweetAlertDialog deleteBasketProduct = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        deleteBasketProduct.setTitleText("").setContentText(this.getResources().getStringArray(R.array.basket_message)[11])
                .setConfirmText(this.getResources().getStringArray(R.array.global_message)[3])
                .show();

        deleteBasketProduct.setOnDismissListener(this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        this.basketCard.dismiss();

        if (this.ordersController != null) {
            this.fabInsert.setBackgroundTintList(getResources().getColorStateList(R.color.colorSecondFab));
            this.ordersController.deleteOrderProductsByBasketInfo(this.basketId, this.productId);

            this.setCount = false;
            this.count = 0;
            this.delete = true;
        }
    }
}
