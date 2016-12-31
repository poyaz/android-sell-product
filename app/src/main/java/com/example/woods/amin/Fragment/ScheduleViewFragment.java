package com.example.woods.amin.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.woods.amin.Adapter.ListViewScheduleAdapter;
import com.example.woods.amin.Controller.SchedulesController;
import com.example.woods.amin.Database.Schedules;
import com.example.woods.amin.Interface.ScheduleDataPassInterface;
import com.example.woods.amin.R;

import java.util.List;


public class ScheduleViewFragment extends Fragment {
    private Activity activity = null;
    private View inflaterView = null;
    private ListViewScheduleAdapter scheduleAdapter = null;
    private ScheduleDataPassInterface dataPass = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScheduleViewFragment() {
    }

    public static ScheduleViewFragment newInstance() {
        ScheduleViewFragment fragment = new ScheduleViewFragment();
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
        this.inflaterView = inflater.inflate(R.layout.fragment_schedule_view, container, false);
        this.createListViewSchedule();

        return this.inflaterView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            this.activity.findViewById(R.id.scheduleActivity_fab_insert).setVisibility(View.GONE);
        }
        if (this.dataPass != null && this.scheduleAdapter != null && !this.scheduleAdapter.isEmptySelected()) {
            this.dataPass.onSetOptionsMenuVisible(true);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
        this.dataPass = (ScheduleDataPassInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
        this.dataPass = null;
    }

    public void setNewData(Bundle data) {
        this.scheduleAdapter.insert(data, 0);

        TextView noData = (TextView) this.inflaterView.findViewById(R.id.scheduleViewFragment_tv_noData);
        if (noData != null) {
            noData.setVisibility(View.GONE);
        }
    }

    private void createListViewSchedule() {
        SchedulesController schedulesController = new SchedulesController(this.activity);
        final List<Schedules> schedules = schedulesController.getSchedulesList();

        if (schedules == null || schedules.size() == 0) {
            TextView noData = (TextView) this.inflaterView.findViewById(R.id.scheduleViewFragment_tv_noData);
            if (noData != null) {
                noData.setVisibility(View.VISIBLE);
                noData.setText(getResources().getStringArray(R.array.global_message)[1]);
            }
        }

        ListView scheduleListView = (ListView) this.inflaterView.findViewById(R.id.scheduleViewFragment_lv_schedule);
        if (scheduleListView != null) {
            this.scheduleAdapter = new ListViewScheduleAdapter(this.activity, schedules);
            scheduleListView.setAdapter(this.scheduleAdapter);
        }
    }

    public void deleteSelected() {
        this.scheduleAdapter.deleteSelected();
    }

    public void removeAllSelected() {
        this.scheduleAdapter.removeAllSelected();
    }

    public void delete(List<Long> items) {
        if (items.size() != 0) {
            SchedulesController schedulesController = new SchedulesController(this.activity);
            schedulesController.deleteSchedule(items);

            if (this.scheduleAdapter.getCount() == 0) {
                TextView noData = (TextView) this.inflaterView.findViewById(R.id.scheduleViewFragment_tv_noData);
                if (noData != null) {
                    noData.setVisibility(View.VISIBLE);
                    noData.setText(getResources().getStringArray(R.array.global_message)[1]);
                }
            }
        }
    }
}
