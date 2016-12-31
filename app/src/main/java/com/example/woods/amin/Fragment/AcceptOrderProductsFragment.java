package com.example.woods.amin.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.woods.amin.Adapter.ListViewOrderProductAdapter;
import com.example.woods.amin.Database.OrderProducts;
import com.example.woods.amin.Interface.AcceptOrderDataPassInterface;
import com.example.woods.amin.R;

import java.util.List;

public class AcceptOrderProductsFragment extends Fragment {

    private Activity activity = null;
    private View inflaterView = null;
    private List<OrderProducts> orderProducts = null;
    private ListViewOrderProductAdapter listViewOrderProductAdapter = null;

    public AcceptOrderProductsFragment() {

    }

    public static AcceptOrderProductsFragment newInstance() {
        AcceptOrderProductsFragment fragment = new AcceptOrderProductsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflaterView = inflater.inflate(R.layout.fragment_accept_order_products, container, false);

        return this.inflaterView;
    }

    public void drawOrderProducts() {
        if (this.orderProducts == null)
            return;

        this.listViewOrderProductAdapter = new ListViewOrderProductAdapter(this.activity, this.orderProducts);

        ListView productsList = (ListView) this.inflaterView.findViewById(R.id.acceptOrderProductFragment_lv_products);
        productsList.setAdapter(this.listViewOrderProductAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.acceptOrderMenu_item_delete:
                break;
            case R.id.acceptOrderMenu_item_cancel:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            this.activity.findViewById(R.id.acceptOrderActivity_fab_accept).setVisibility(View.GONE);
            this.orderProducts = ((AcceptOrderDataPassInterface) this.activity).onGetOrderProducts();
            this.drawOrderProducts();
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
}
