package com.example.woods.amin.Listener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.woods.amin.Activity.AcceptOrderActivity;
import com.example.woods.amin.Activity.OrderProductsActivity;
import com.example.woods.amin.Adapter.ListViewOrderProductAdapter;
import com.example.woods.amin.Controller.OrderProductsController;
import com.example.woods.amin.Controller.OrdersController;
import com.example.woods.amin.Database.OrderProducts;
import com.example.woods.amin.Interface.OrderDataPassInterface;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BasketShoppingListener implements SweetAlertDialog.OnSweetClickListener, DialogInterface.OnDismissListener, DialogInterface.OnClickListener {
    private Activity activity = null;
    private OrderDataPassInterface orderDataPassInterface = null;
    private Long basketId = -1L;
    private Long userId = -1L;
    private SweetAlertDialog cardDialog = null;
    private AlertDialog userSelectDialog = null;
    private ListViewOrderProductAdapter orderProductsAdapter = null;
    private AlertDialog basketDialog = null;

    public BasketShoppingListener(Context context, Long basketId, Long userId) {
        this.activity = (Activity) context;
        this.orderDataPassInterface = (OrderDataPassInterface) context;
        this.basketId = basketId;
        this.userId = userId;

        if (basketId == -1L && userId != -1L)
            this.successSelectUser();
    }

    public void setBasketId(Long basketId) {
        this.basketId = basketId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    private void createUserCard() {
        Bundle data = new Bundle();
        data.putLong("uid", this.userId);

        OrdersController ordersController = new OrdersController(this.activity);
        ordersController.addNewOrder(data, config.ORDERS_STATUS_PRE_ORDER);
        this.basketId = ordersController.getId();
        this.orderDataPassInterface.onSetOrderId(this.basketId);
    }

    public void createBasket() {
        if (this.basketId != -1L || this.userId != -1L)
            return;

        this.cardDialog = new SweetAlertDialog(this.activity, SweetAlertDialog.WARNING_TYPE);
        this.cardDialog.setCanceledOnTouchOutside(true);
        this.cardDialog.setTitleText(this.activity.getResources().getStringArray(R.array.basket_message)[0])
                .setConfirmText(this.activity.getResources().getStringArray(R.array.basket_message)[1])
                .setCancelText(null)
                .setConfirmClickListener(this)
                .show();
    }

    public void onClick() {
        this.createBasket();

        if (this.basketId != -1L) {
            View basketView = View.inflate(this.activity, R.layout.basket_view, null);
            basketView.setBackgroundColor(this.activity.getResources().getColor((android.R.color.white)));

            TextView title = new TextView(this.activity);
            title.setText(this.activity.getResources().getStringArray(R.array.basket_message)[5]);
            title.setBackgroundColor(Color.DKGRAY);
            title.setPadding(40, 40, 40, 40);
            title.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            title.setTextColor(Color.WHITE);
            title.setTextSize(20);

            this.basketDialog = new AlertDialog.Builder(this.activity)
                    .setCustomTitle(title)
                    .setView(basketView)
                    .setPositiveButton(this.activity.getResources().getStringArray(R.array.basket_message)[12], this)
                    .show();

            OrderProductsController orderProductsController = new OrderProductsController(this.activity);
            List<OrderProducts> orderProducts = orderProductsController.getOrderProductsListByOrderId(this.basketId);

            if (orderProducts == null || orderProducts.size() == 0) {
                TextView noData = (TextView) basketView.findViewById(R.id.basketView_tv_noData);
                if (noData != null) {
                    noData.setVisibility(View.VISIBLE);
                    noData.setText(this.activity.getResources().getStringArray(R.array.global_message)[1]);
                }
            }

            ListView userListView = (ListView) basketView.findViewById(R.id.basketView_lv_user);
            this.orderProductsAdapter = new ListViewOrderProductAdapter(this.activity, orderProducts);
            userListView.setAdapter(orderProductsAdapter);
        }
    }

    public void onDelete(Long pid) {
        if (this.orderProductsAdapter != null) {
            this.orderProductsAdapter.delete(pid);
            if (this.orderProductsAdapter.getCount() == 0) {
                this.basketDialog.dismiss();
            }
        }
    }

    public void onUpdate(Long pid, int count) {
        Log.v("count1", count + "");
        if (this.orderProductsAdapter != null && this.orderProductsAdapter.getCount() > 0) {
            Log.v("count2", count + "");
            this.orderProductsAdapter.update(pid, count);
        }
    }

    @Override
    public void onClick(SweetAlertDialog sweetAlertDialog) {
        this.cardDialog.dismiss();

        Intent intent = new Intent(this.activity, OrderProductsActivity.class);
        intent.putExtra("email", this.activity.getIntent().getStringExtra("email"));
        intent.putExtra("dest", this.activity.getClass().getSimpleName());
        intent.putExtra("tab", 2);
        intent.putExtra("orderStatusWhere", config.ORDERS_STATUS_PRE_ORDER);
        this.activity.startActivityForResult(intent, config.REQUEST_INTENT_ORDER_PRODUCTS_ACTIVITY);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        switch (dialog.getClass().getSimpleName()) {
            case "AlertDialog":
                this.onDismissAlertDialog();
                break;
            case "SweetAlertDialog":
                this.onDismissSweetAlertDialog();
                break;
        }
    }

    private void onDismissSweetAlertDialog() {
        this.orderDataPassInterface.onDismissSuccessDialog();
    }

    private void onDismissAlertDialog() {
        if (this.userId != null && this.userId != -1L) {
            this.successSelectUser();
        } else {
            this.errorSelectUser();
        }
    }

    public void successSelectUser() {
        this.createUserCard();

        SweetAlertDialog createBasketSuccessDialog = new SweetAlertDialog(this.activity, SweetAlertDialog.SUCCESS_TYPE);
        createBasketSuccessDialog.setTitleText("").setContentText(this.activity.getResources().getStringArray(R.array.basket_message)[4])
                .setConfirmText(this.activity.getResources().getStringArray(R.array.global_message)[3])
                .show();

        createBasketSuccessDialog.setOnDismissListener(this);
    }

    public void errorSelectUser() {
        new SweetAlertDialog(this.activity, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(this.activity.getResources().getStringArray(R.array.basket_message)[2])
                .setContentText(this.activity.getResources().getStringArray(R.array.basket_message)[3])
                .setConfirmText(this.activity.getResources().getStringArray(R.array.global_message)[3])
                .show();
    }

    public void successSelectOrder() {
        SweetAlertDialog selectBasketSuccessDialog = new SweetAlertDialog(this.activity, SweetAlertDialog.SUCCESS_TYPE);
        selectBasketSuccessDialog.setTitleText("").setContentText(this.activity.getResources().getStringArray(R.array.basket_message)[8])
                .setConfirmText(this.activity.getResources().getStringArray(R.array.global_message)[3])
                .show();

        selectBasketSuccessDialog.setOnDismissListener(this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            case DialogInterface.BUTTON_POSITIVE:
                OrderProductsController orderProductsController = new OrderProductsController(this.activity);
                Long count = orderProductsController.getCountOfOrderProductsListByOrderId(this.basketId);

                TwoAuthenticateListener twoAuthenticateListener = new TwoAuthenticateListener(this.activity, AcceptOrderActivity.class.getSimpleName(), count);
                twoAuthenticateListener.setBasketId(this.basketId);
                twoAuthenticateListener.startAuthenticate();
                break;
        }
    }
}
