package com.example.woods.amin.Controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.woods.amin.Database.DaoMaster;
import com.example.woods.amin.Database.DaoSession;
import com.example.woods.amin.Database.OrderProducts;
import com.example.woods.amin.Database.OrderProductsDao;
import com.example.woods.amin.config;

import java.util.List;

public class OrderProductsController {
    private OrderProductsDao orderProductsDao;
    private OrderProducts orderProducts;
    private Context context;

    public OrderProductsController(Context context) {
        this.context = context;
        this.orderProductsDao = setupDb();
    }

    public void addNewOrderProduct(Bundle data) {
        OrderProducts orderProducts = new OrderProducts(
                null,
                data.getString("price"),
                data.getInt("count"),
                data.getString("off"),
                data.getLong("pid"),
                data.getLong("oid")
        );

        this.orderProductsDao.insert(orderProducts);
    }

    public void updateCountOrderProduct(Long oid, Long pid, int count) {
        List<OrderProducts> orderProductsList = this.getOrderProductsListByBasketInfo(oid, pid);
        if (orderProductsList.size() > 0) {
            OrderProducts orderProducts = orderProductsList.get(0);
            orderProducts.setCount(count);
            orderProducts.update();
            orderProducts.refresh();
        }
    }

    public List<OrderProducts> getOrderProductsListByOrderId(Long oid) {
        return this.orderProductsDao.queryBuilder()
                .where(OrderProductsDao.Properties.Order_id.eq(oid))
                .build().list();
    }

    public Long getCountOfOrderProductsListByOrderId(Long oid) {
        return this.orderProductsDao.queryBuilder()
                .where(OrderProductsDao.Properties.Order_id.eq(oid))
                .buildCount().count();
    }

    public List<OrderProducts> getOrderProductsListByBasketInfo(Long oid, Long pid) {
        return this.orderProductsDao.queryBuilder()
                .where(OrderProductsDao.Properties.Order_id.eq(oid), OrderProductsDao.Properties.Product_id.eq(pid))
                .build().list();
    }

    public void deleteOrderProductsByBasketInfo(Long oid, Long pid) {
        this.orderProductsDao.queryBuilder()
                .where(OrderProductsDao.Properties.Order_id.eq(oid), OrderProductsDao.Properties.Product_id.eq(pid))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

    private OrderProductsDao setupDb() {
        DaoMaster.DevOpenHelper masterHelper = new DaoMaster.DevOpenHelper(this.context, config.DB_NAME, null);
        SQLiteDatabase db = masterHelper.getWritableDatabase();
        DaoMaster master = new DaoMaster(db);
        DaoSession masterSession = master.newSession();

        return masterSession.getOrderProductsDao();
    }
}
