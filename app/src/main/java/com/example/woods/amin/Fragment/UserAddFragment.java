package com.example.woods.amin.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.woods.amin.Interface.UserDataPassInterface;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserAddFragment extends Fragment implements View.OnClickListener, Dialog.OnClickListener, DialogInterface.OnCancelListener {
    private Activity activity = null;
    private UserDataPassInterface dataPass = null;
    private ArrayList<String> errors = new ArrayList<>();
    private View inflaterView = null;

    private List<String> mobile = null;
    private List<String> phone = null;
    private List<String> address = null;
    private List<AlertDialog> selectDialog = null;
    private List<String> nameDialog = null;
    private EditText selectChoiceItem = null;


    public UserAddFragment() {
    }

    public static UserAddFragment newInstance() {
        UserAddFragment fragment = new UserAddFragment();
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
        this.inflaterView = inflater.inflate(R.layout.fragment_user_add, container, false);
        this.inflaterView.findViewById(R.id.addUserView_bt_select).setOnClickListener(this);

        return this.inflaterView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            this.activity.findViewById(R.id.userActivity_fab_insert).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
        this.dataPass = (UserDataPassInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
        this.dataPass = null;
    }

    @Override
    public void onClick(View v) {
        this.intentSelectContact();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == config.REQUEST_PERMISSION_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.intentSelectContact();
            } else {
                new SweetAlertDialog(this.activity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(this.activity.getResources().getStringArray(R.array.permission_message)[0])
                        .setContentText(this.activity.getResources().getStringArray(R.array.permission_message)[2])
                        .show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == config.ANDROID_INTENT_SELECT_CONTACT) {
            Uri contactData = data.getData();
            Cursor contactCursor = this.activity.getContentResolver().query(contactData, null, null, null, null);
            if (contactCursor != null && contactCursor.moveToFirst()) {
                this.reset();
                long id = contactCursor.getLong(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                this.phone = new ArrayList<>();
                this.mobile = new ArrayList<>();
                this.address = new ArrayList<>();
                this.selectDialog = new ArrayList<>();
                this.nameDialog = new ArrayList<>();

                Cursor numberCursor = this.activity.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                        null,
                        ContactsContract.Data.CONTACT_ID + "=?",
                        new String[]{String.valueOf(id)},
                        null);

                if (numberCursor != null) {
                    numberCursor.moveToFirst();
                    while (numberCursor.moveToNext()) {
                        String number = numberCursor.getString(numberCursor.getColumnIndex(ContactsContract.Data.DATA1));
                        if (number != null && number.length() > 2) {
                            number = number.replace("+98", "0");
                            if (number.matches(config.REGEX_PHONE_NUMBER)) {
                                if (number.substring(0, 2).equals("09")) {
                                    this.mobile.add(number);
                                } else {
                                    this.phone.add(number);
                                }
                            }
                        }
                    }
                    numberCursor.close();
                }

                Cursor addressCursor = this.activity.getContentResolver().query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                        null,
                        ContactsContract.Data.CONTACT_ID + "=?",
                        new String[]{String.valueOf(id)},
                        null);

                if (addressCursor != null) {
                    while (addressCursor.moveToNext()) {
                        String address = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
                        if (address != null)
                            this.address.add(address);
                    }
                    addressCursor.close();
                }

                ((EditText) this.inflaterView.findViewById(R.id.addUserView_et_name)).setText(name);
                if (this.mobile.size() == 1) {
                    ((EditText) this.inflaterView.findViewById(R.id.addUserView_et_mobile)).setText(this.mobile.get(0));
                } else if (this.mobile.size() > 1) {
                    TextView title = new TextView(this.activity);
                    title.setText(this.activity.getResources().getStringArray(R.array.select_contact_message)[0]);
                    title.setBackgroundColor(Color.DKGRAY);
                    title.setPadding(40, 40, 40, 40);
                    title.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                    title.setTextColor(Color.WHITE);
                    title.setTextSize(20);

                    AlertDialog dialog = new AlertDialog.Builder(this.activity)
                            .setCustomTitle(title)
                            .setSingleChoiceItems(this.mobile.toArray(new CharSequence[this.mobile.size()]), -1, this)
                            .setPositiveButton(this.activity.getResources().getStringArray(R.array.global_message)[7], this)
                            .setNegativeButton(this.activity.getResources().getStringArray(R.array.global_message)[3], this)
                            .create();
                    dialog.setOnCancelListener(this);
                    dialog.setCanceledOnTouchOutside(true);

                    this.selectDialog.add(dialog);
                    this.nameDialog.add("mobile");
                }
                if (this.phone.size() == 1) {
                    ((EditText) this.inflaterView.findViewById(R.id.addUserView_et_phone)).setText(this.phone.get(0));
                } else if (this.phone.size() > 1) {
                    TextView title = new TextView(this.activity);
                    title.setText(this.activity.getResources().getStringArray(R.array.select_contact_message)[1]);
                    title.setBackgroundColor(Color.DKGRAY);
                    title.setPadding(40, 40, 40, 40);
                    title.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                    title.setTextColor(Color.WHITE);
                    title.setTextSize(20);

                    AlertDialog dialog = new AlertDialog.Builder(this.activity)
                            .setCustomTitle(title)
                            .setSingleChoiceItems(this.phone.toArray(new CharSequence[this.phone.size()]), -1, this)
                            .setPositiveButton(this.activity.getResources().getStringArray(R.array.global_message)[7], this)
                            .setNegativeButton(this.activity.getResources().getStringArray(R.array.global_message)[3], this)
                            .create();
                    dialog.setOnCancelListener(this);
                    dialog.setCanceledOnTouchOutside(true);

                    this.selectDialog.add(dialog);
                    this.nameDialog.add("phone");
                }
                if (this.address.size() == 1) {
                    ((EditText) this.inflaterView.findViewById(R.id.addUserView_et_address)).setText(this.address.get(0));
                } else if (this.address.size() > 1) {
                    TextView title = new TextView(this.activity);
                    title.setText(this.activity.getResources().getStringArray(R.array.select_contact_message)[2]);
                    title.setBackgroundColor(Color.DKGRAY);
                    title.setPadding(40, 40, 40, 40);
                    title.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                    title.setTextColor(Color.WHITE);
                    title.setTextSize(20);

                    AlertDialog dialog = new AlertDialog.Builder(this.activity)
                            .setCustomTitle(title)
                            .setSingleChoiceItems(this.address.toArray(new CharSequence[this.address.size()]), -1, this)
                            .setPositiveButton(this.activity.getResources().getStringArray(R.array.global_message)[7], this)
                            .setNegativeButton(this.activity.getResources().getStringArray(R.array.global_message)[3], this)
                            .create();
                    dialog.setOnCancelListener(this);
                    dialog.setCanceledOnTouchOutside(true);

                    this.selectDialog.add(dialog);
                    this.nameDialog.add("address");
                }

                if (this.selectDialog.size() > 0) {
                    this.selectDialog.remove(0).show();
                }
                contactCursor.close();
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                this.nameDialog.remove(0);
                if (this.selectDialog.size() > 0) {
                    this.selectDialog.remove(0).show();
                }
                break;
            case Dialog.BUTTON_NEGATIVE:
                this.nameDialog.remove(0);
                if (this.selectChoiceItem != null) {
                    this.selectChoiceItem.setText("");
                }
                if (this.selectDialog.size() > 0) {
                    this.selectDialog.remove(0).show();
                }
                break;
            default:
                if (this.nameDialog.size() > 0) {
                    switch (this.nameDialog.get(0)) {
                        case "mobile":
                            this.selectChoiceItem = (EditText) this.inflaterView.findViewById(R.id.addUserView_et_mobile);
                            this.selectChoiceItem.setText(this.mobile.get(which));
                            break;
                        case "phone":
                            this.selectChoiceItem = (EditText) this.inflaterView.findViewById(R.id.addUserView_et_phone);
                            this.selectChoiceItem.setText(this.phone.get(which));
                            break;
                        case "address":
                            this.selectChoiceItem = (EditText) this.inflaterView.findViewById(R.id.addUserView_et_address);
                            this.selectChoiceItem.setText(this.address.get(which));
                            break;
                    }
                }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        this.nameDialog.remove(0);
        if (this.selectChoiceItem != null) {
            this.selectChoiceItem.setText("");
        }
        if (this.selectDialog.size() > 0) {
            this.selectDialog.remove(0).show();
        }
    }

    private void intentSelectContact() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, config.REQUEST_PERMISSION_READ_CONTACTS);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            startActivityForResult(intent, config.ANDROID_INTENT_SELECT_CONTACT);
        }
    }

    public void onClickFab() {
        this.errors.clear();

        String user_name = ((EditText) this.inflaterView.findViewById(R.id.addUserView_et_name)).getText().toString();
        String user_mobile = ((EditText) this.inflaterView.findViewById(R.id.addUserView_et_mobile)).getText().toString();
        String user_phone = ((EditText) this.inflaterView.findViewById(R.id.addUserView_et_phone)).getText().toString();
        String user_address = ((EditText) this.inflaterView.findViewById(R.id.addUserView_et_address)).getText().toString();

        Bundle args = new Bundle();

        if (this.validateInput(user_name, user_mobile, user_phone, user_address)) {
            args.putString("name", user_name);
            args.putString("mobile", user_mobile);
            args.putString("phone", user_phone);
            args.putString("address", user_address);
        }
        args.putStringArrayList("errors", this.errors);

        this.dataPass.onDataPassAdd(args);
    }

    public void reset() {
        this.errors.clear();

        ((EditText) this.inflaterView.findViewById(R.id.addUserView_et_name)).setText("");
        ((EditText) this.inflaterView.findViewById(R.id.addUserView_et_mobile)).setText("");
        ((EditText) this.inflaterView.findViewById(R.id.addUserView_et_phone)).setText("");
        ((EditText) this.inflaterView.findViewById(R.id.addUserView_et_address)).setText("");
    }

    private Boolean validateInput(String user_name, String user_mobile, String user_phone, String user_address) {
        if (user_name.isEmpty()) {
            this.errors.add(this.inflaterView.getResources().getStringArray(R.array.userAddFragment_errors)[0]);
        }
        if (user_mobile.isEmpty() && user_phone.isEmpty()) {
            this.errors.add(this.inflaterView.getResources().getStringArray(R.array.userAddFragment_errors)[1]);
        }
        if (user_address.isEmpty()) {
            this.errors.add(this.inflaterView.getResources().getStringArray(R.array.userAddFragment_errors)[2]);
        }

        return this.errors.size() == 0;
    }
}
