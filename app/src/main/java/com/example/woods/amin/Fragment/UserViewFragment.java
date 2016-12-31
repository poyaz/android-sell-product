package com.example.woods.amin.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.woods.amin.Activity.UserActivity;
import com.example.woods.amin.Controller.UsersController;
import com.example.woods.amin.Database.Users;
import com.example.woods.amin.Adapter.ListViewUserAdapter;
import com.example.woods.amin.R;

import java.util.List;

public class UserViewFragment extends Fragment {
    private Activity activity = null;
    private View inflaterView = null;
    private ListViewUserAdapter userAdapter = null;
    private String parent = null;

    public UserViewFragment() {
    }

    public static UserViewFragment newInstance(String parent) {
        UserViewFragment fragment = new UserViewFragment();
        Bundle args = new Bundle();
        args.putString("parent", parent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.parent = getArguments().getString("parent");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflaterView = inflater.inflate(R.layout.fragment_user_view, container, false);
        this.createListViewUser();

        return inflaterView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getArguments().getString("parent", "").equalsIgnoreCase(UserActivity.class.getSimpleName()) && isVisibleToUser) {
            this.activity.findViewById(R.id.userActivity_fab_insert).setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
//        if (getArguments().getString("parent", "").equalsIgnoreCase(UserActivity.class.getSimpleName())) {
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    public void setNewData(Bundle data) {
        this.userAdapter.insert(data, 0);

        TextView noData = (TextView) this.inflaterView.findViewById(R.id.userViewFragment_tv_noData);
        if (noData != null) {
            noData.setVisibility(View.GONE);
        }
    }

    private void createListViewUser() {
        UsersController usersController = new UsersController(this.activity);
        List<Users> users = usersController.getUserList();

        if (users == null || users.size() == 0) {
            TextView noData = (TextView) this.inflaterView.findViewById(R.id.userViewFragment_tv_noData);
            if (noData != null) {
                noData.setVisibility(View.VISIBLE);
                noData.setText(getResources().getStringArray(R.array.global_message)[1]);
            }
        }

        ListView userListView = (ListView) this.inflaterView.findViewById(R.id.userViewFragment_lv_user);
        if (userListView != null) {
            this.userAdapter = new ListViewUserAdapter(this.activity, users, this.parent);
            userListView.setAdapter(this.userAdapter);
        }
    }
}
