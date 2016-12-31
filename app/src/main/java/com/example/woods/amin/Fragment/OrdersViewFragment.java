package com.example.woods.amin.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.woods.amin.Activity.AcceptOrderActivity;
import com.example.woods.amin.Adapter.ListViewOrderAdapter;
import com.example.woods.amin.Controller.OrdersController;
import com.example.woods.amin.Database.Orders;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.util.List;

public class OrdersViewFragment extends Fragment {
    private Activity activity = null;
    private View inflaterView = null;
    private String parent = null;
    private Integer orderStatusWhere = -1;

    public OrdersViewFragment() {
        // Required empty public constructor
    }

    public static OrdersViewFragment newInstance(String parent, Integer orderStatusWhere) {
        OrdersViewFragment fragment = new OrdersViewFragment();
        Bundle args = new Bundle();
        args.putString("parent", parent);
        args.putInt("orderStatusWhere", orderStatusWhere);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.parent = getArguments().getString("parent");
            this.orderStatusWhere = getArguments().getInt("orderStatusWhere");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflaterView = inflater.inflate(R.layout.fragment_order_view, container, false);
        this.createListViewOrder(this.orderStatusWhere);

        return this.inflaterView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == config.REQUEST_INTENT_ACCEPT_ORDER_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK && data.getLongExtra("oid", -1L) != -1L && data.getExtras().getInt("status", -1) > this.orderStatusWhere) {
                this.createListViewOrder(this.orderStatusWhere);
            }
        }
    }

    public void changeOrdersStatus(Integer select) {
        this.createListViewOrder(select);
    }

    private void createListViewOrder(Integer orderStatusWhere) {
        OrdersController ordersController = new OrdersController(this.activity);
        List<Orders> orders;

        if (orderStatusWhere.equals(config.ORDERS_STATUS_PRE_ORDER)) {
            orders = ordersController.getOrdersListByStatus(config.ORDERS_STATUS_PRE_ORDER);
        } else if (orderStatusWhere.equals(config.ORDERS_STATUS_ACCEPT)) {
            orders = ordersController.getOrdersListByStatus(config.ORDERS_STATUS_ACCEPT);
        } else if (orderStatusWhere.equals(config.ORDERS_STATUS_PAY)) {
            orders = ordersController.getOrdersListByStatus(config.ORDERS_STATUS_PAY);
        } else if (orderStatusWhere.equals(config.ORDERS_STATUS_SEND)) {
            orders = ordersController.getOrdersListByStatus(config.ORDERS_STATUS_SEND);
        } else {
            orders = ordersController.getOrdersList();
        }

        TextView noData = (TextView) this.inflaterView.findViewById(R.id.orderViewFragment_tv_noData);
        if (noData != null) {
            if (orders == null || orders.size() == 0) {
                noData.setVisibility(View.VISIBLE);
                noData.setText(getResources().getStringArray(R.array.global_message)[1]);
            } else {
                noData.setVisibility(View.GONE);
            }
        }

        ListView orderListView = (ListView) this.inflaterView.findViewById(R.id.orderViewFragment_lv_user);
        if (orderListView != null) {
            if (this.parent == null || this.parent.isEmpty()) {
                orderListView.setAdapter(new ListViewOrderAdapter(this.activity, orders, AcceptOrderActivity.class.getSimpleName()));
            } else {
                orderListView.setAdapter(new ListViewOrderAdapter(this.activity, orders, this.parent));
            }
        }
    }
}
