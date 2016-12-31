package com.example.woods.amin.Interface;

import com.example.woods.amin.Database.OrderProducts;

import java.util.List;

public interface AcceptOrderDataPassInterface {
    void onSetOrderStatus(int status);
    void onSetOptionsMenuVisible(Boolean visible);
    List<OrderProducts> onGetOrderProducts();
}
