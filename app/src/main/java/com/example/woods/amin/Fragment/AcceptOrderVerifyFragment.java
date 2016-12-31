package com.example.woods.amin.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.woods.amin.Controller.OrdersController;
import com.example.woods.amin.Database.Orders;
import com.example.woods.amin.Interface.AcceptOrderDataPassInterface;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AcceptOrderVerifyFragment extends Fragment implements AdapterView.OnItemSelectedListener, TextWatcher, Dialog.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private Activity activity = null;
    private View inflaterView = null;
    private int count = 0;
    private int firstPrice = 0;
    private int lastPrice = 0;
    private int selected = 0;
    private String priceOff = "";
    private String off = "";
    private int orderStatus = 0;

    public AcceptOrderVerifyFragment() {
    }

    public static AcceptOrderVerifyFragment newInstance(int count, int price) {
        AcceptOrderVerifyFragment fragment = new AcceptOrderVerifyFragment();
        Bundle args = new Bundle();
        args.putInt("count", count);
        args.putInt("price", price);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.count = getArguments().getInt("count");
            this.firstPrice = getArguments().getInt("price");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflaterView = inflater.inflate(R.layout.fragment_accept_order_verify, container, false);
        this.drawOrderInfo();

        return this.inflaterView;
    }

    public void drawOrderInfo() {
        OrdersController ordersController = new OrdersController(this.activity);
        Orders order = ordersController.getOrdersById(this.activity.getIntent().getLongExtra("oid", -1L));

        this.orderStatus = order.getStatus();
        this.lastPrice = Integer.valueOf(order.getLast_price());
        this.off = order.getOff();

        ((TextView) this.inflaterView.findViewById(R.id.contentAcceptOrder_tv_username)).setText(order.getOrderUsers().getName());
        ((TextView) this.inflaterView.findViewById(R.id.contentAcceptOrder_tv_first_price)).setText(String.valueOf(this.firstPrice));
        ((EditText) this.inflaterView.findViewById(R.id.contentAcceptOrder_et_off)).addTextChangedListener(this);
        ((Spinner) this.inflaterView.findViewById(R.id.contentAcceptOrder_spinner_off)).setOnItemSelectedListener(this);
        if (order.getLast_price().isEmpty()) {
            ((TextView) this.inflaterView.findViewById(R.id.contentAcceptOrder_tv_last_price)).setText(String.valueOf(this.firstPrice));
        } else {
            ((TextView) this.inflaterView.findViewById(R.id.contentAcceptOrder_tv_last_price)).setText(order.getLast_price());
        }

        this.changeStats(order.getStatus());
    }

    private void changeStats(int status) {
        ((TextView) this.inflaterView.findViewById(R.id.contentAcceptOrder_tv_stats)).setText(this.activity.getResources().getStringArray(R.array.order_status)[status]);
        if (status != 0) {
            ((TextView) this.inflaterView.findViewById(R.id.contentAcceptOrder_tv_off_show)).setText(this.off.replace("|", "").replace("R", " " + getResources().getString(R.string.listViewProduct_tv_price_type)));
            this.inflaterView.findViewById(R.id.contentAcceptOrder_rl_off_show).setVisibility(View.VISIBLE);
            this.inflaterView.findViewById(R.id.contentAcceptOrder_ll_off).setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.selected = position;
        changeLastPrice();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.priceOff = s.toString();
        changeLastPrice();
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void changeLastPrice() {
        Integer orderPrice = this.firstPrice;

        if (!this.priceOff.isEmpty() && this.priceOff.matches(config.REGEX_INT_VALID) && this.firstPrice != 0) {
            Integer off = Integer.parseInt(this.priceOff);
            switch (this.selected) {
                case 1:
                    if (off <= 100) {
                        this.off = this.priceOff + "|%";
                        orderPrice = this.firstPrice - (this.firstPrice * off) / 100;
                    }
                    break;
                case 2:
                    if (off <= this.firstPrice) {
                        this.off = this.priceOff + "|R";
                        orderPrice = this.firstPrice - off;
                    }
                    break;
            }
        }

        this.lastPrice = orderPrice;
        ((TextView) this.inflaterView.findViewById(R.id.contentAcceptOrder_tv_last_price)).setText(String.valueOf(orderPrice));
    }

    public void onClickFab() {
        if (this.orderStatus == config.ORDERS_STATUS_SEND) {
            SweetAlertDialog sendDialog = new SweetAlertDialog(this.activity, SweetAlertDialog.NORMAL_TYPE);
            sendDialog.setCanceledOnTouchOutside(true);
            sendDialog.setTitleText("")
                    .setContentText(this.activity.getResources().getString(R.string.orderAccept_dialog_send))
                    .setConfirmText(this.activity.getResources().getStringArray(R.array.global_message)[3])
                    .setCancelText(null)
                    .show();

            return;
        } else if (this.count == 0) {
            SweetAlertDialog sendDialog = new SweetAlertDialog(this.activity, SweetAlertDialog.WARNING_TYPE);
            sendDialog.setCanceledOnTouchOutside(true);
            sendDialog.setTitleText("")
                    .setContentText(this.activity.getResources().getString(R.string.orderAccept_dialog_empty))
                    .setConfirmText(this.activity.getResources().getStringArray(R.array.global_message)[3])
                    .setCancelText(null)
                    .show();

            return;
        }

        View acceptView = View.inflate(this.activity, R.layout.accept_order_view, null);
        acceptView.setBackgroundColor(this.activity.getResources().getColor((android.R.color.white)));
        ((RadioGroup) acceptView.findViewById(R.id.acceptOrderView_rg_status)).setOnCheckedChangeListener(this);

        if (this.orderStatus == config.ORDERS_STATUS_ACCEPT) {
            acceptView.findViewById(R.id.acceptOrderView_rb_accept).setVisibility(View.GONE);
        } else if (this.orderStatus == config.ORDERS_STATUS_PAY) {
            acceptView.findViewById(R.id.acceptOrderView_rb_accept).setVisibility(View.GONE);
            acceptView.findViewById(R.id.acceptOrderView_rb_pay).setVisibility(View.GONE);
        }

        TextView title = new TextView(this.activity);
        title.setText(this.activity.getResources().getStringArray(R.array.basket_message)[5]);
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(40, 40, 40, 40);
        title.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        AlertDialog acceptDialog = new AlertDialog.Builder(this.activity)
                .setCustomTitle(title)
                .setView(acceptView)
                .setPositiveButton(this.activity.getResources().getStringArray(R.array.global_message)[7], this)
                .setNegativeButton(this.activity.getResources().getStringArray(R.array.global_message)[3], this)
                .show();

        acceptDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            this.activity.findViewById(R.id.acceptOrderActivity_fab_accept).setVisibility(View.VISIBLE);
        }
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
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            OrdersController ordersController = new OrdersController(this.activity);
            this.changeStats(this.orderStatus);
            ordersController.updateOrdersById(this.activity.getIntent().getLongExtra("oid", -1L), String.valueOf(this.firstPrice), String.valueOf(this.lastPrice), this.off, this.orderStatus);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.acceptOrderView_rb_accept:
                this.orderStatus = config.ORDERS_STATUS_ACCEPT;
                break;
            case R.id.acceptOrderView_rb_pay:
                this.orderStatus = config.ORDERS_STATUS_PAY;
                break;
            case R.id.acceptOrderView_rb_send:
                this.orderStatus = config.ORDERS_STATUS_SEND;
                break;
            default:
                return;
        }
        ((AcceptOrderDataPassInterface) this.activity).onSetOrderStatus(this.orderStatus);
    }
}
