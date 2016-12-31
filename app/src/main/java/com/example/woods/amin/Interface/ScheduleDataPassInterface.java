package com.example.woods.amin.Interface;

import android.os.Bundle;

import java.util.List;

public interface ScheduleDataPassInterface {
    void onDataPassAdd(Bundle data);
    void onSetOptionsMenuVisible(Boolean visible);
    void onDataPassDelete(List<Long> items);
}
