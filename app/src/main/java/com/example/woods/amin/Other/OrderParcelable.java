package com.example.woods.amin.Other;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pooya.Azarpour on 12/28/2016.
 * Email: pooya_azarpour@yahoo.com
 */

public class OrderParcelable implements Parcelable {

    private List<Long> orders;

    public OrderParcelable(List<Long> orders) {
        this.orders = orders;
    }

    public OrderParcelable(Parcel in) {
        this.orders = new ArrayList<>();
        in.readList(this.orders, Long.class.getClassLoader());
    }

    public List<Long> getOrders() {
        return this.orders;
    }

    @Override
    public int describeContents() {
        return this.orders.size();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.orders);
    }

    public static final Creator<OrderParcelable> CREATOR = new Creator<OrderParcelable>() {
        @Override
        public OrderParcelable createFromParcel(Parcel in) {
            return new OrderParcelable(in);
        }

        @Override
        public OrderParcelable[] newArray(int size) {
            return new OrderParcelable[size];
        }
    };
}
