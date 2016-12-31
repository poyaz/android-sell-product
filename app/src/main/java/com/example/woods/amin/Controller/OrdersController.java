package com.example.woods.amin.Controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.example.woods.amin.Database.DaoMaster;
import com.example.woods.amin.Database.DaoSession;
import com.example.woods.amin.Database.Orders;
import com.example.woods.amin.Database.OrdersDao;
import com.example.woods.amin.config;

import java.util.Date;
import java.util.List;

public class OrdersController {
    private OrdersDao ordersDao;
    private Orders orders;
    private Context context;
    private Long id = -1L;

    public OrdersController(Context context) {
        this.context = context;
        this.ordersDao = setupDb();
    }

    public void addNewOrder(Bundle data, int status) {
        if (data.getLong("uid", -1L) == -1L)
            return;

        Date date = new Date();

        Orders orders = new Orders(
                null,
                "0",
                "0",
                "0",
                status,
                date,
                data.getLong("uid")
        );
        this.id = this.ordersDao.insert(orders);
    }

    public Long getId() {
        return this.id;
    }

    public List<Orders> getOrdersList() {
        return this.ordersDao.queryBuilder()
                .orderDesc(OrdersDao.Properties.Create)
                .build().list();
    }

    public List<Orders> getOrdersListByStatus(int status) {
        return this.ordersDao.queryBuilder()
                .where(OrdersDao.Properties.Status.eq(status))
                .orderDesc(OrdersDao.Properties.Create)
                .build().list();
    }

    public Orders getOrdersById(Long id) {
        return this.ordersDao.queryBuilder()
                .where(OrdersDao.Properties.Id.eq(id))
                .build().list().get(0);
    }

    public void updateOrdersById(Long id, String firstPrice, String lastPrice, String off, int status) {
        Orders orders = this.getOrdersById(id);
        orders.setFirst_price(firstPrice);
        orders.setLast_price(lastPrice);
        orders.setOff(off);
        orders.setStatus(status);
        orders.update();
        orders.refresh();
    }

    private OrdersDao setupDb() {
        DaoMaster.DevOpenHelper masterHelper = new DaoMaster.DevOpenHelper(this.context, config.DB_NAME, null);
        SQLiteDatabase db = masterHelper.getWritableDatabase();
        DaoMaster master = new DaoMaster(db);
        DaoSession masterSession=master.newSession();

        return masterSession.getOrdersDao();
    }
}
