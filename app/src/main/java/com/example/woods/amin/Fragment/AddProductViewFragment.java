package com.example.woods.amin.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.woods.amin.Controller.ProductsController;
import com.example.woods.amin.Database.Products;
import com.example.woods.amin.Interface.AddProductDataPassInterface;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

@SuppressLint("ValidFragment")
public class AddProductViewFragment extends Fragment implements View.OnClickListener {
    private View inflaterView = null;
    private Activity activity = null;
    private AddProductDataPassInterface dataPass = null;
    private String product_image = "";
    private Long edit = -1L;
    private ArrayList<String> errors = new ArrayList<>();
    private boolean activate = false;

    public AddProductViewFragment() {
    }

    public static AddProductViewFragment newInstance(Long edit) {
        AddProductViewFragment fragment = new AddProductViewFragment();
        Bundle args = new Bundle();
        args.putLong("edit", edit);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.edit = getArguments().getLong("edit", -1L);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflaterView = inflater.inflate(R.layout.fragment_add_product_view, container, false);

        this.inflaterView.findViewById(R.id.addProductView_iv_image).setOnClickListener(this);

        if (this.edit != -1L) {
            ProductsController productsController = new ProductsController(this.activity);
            Products product = productsController.getProductInfoById(this.edit);
            if (product == null) {
                Bundle data = new Bundle();
                data.putBoolean("error_exist", true);
                this.dataPass.onDataPassView(data);

                return this.inflaterView;
            }

            this.product_image = product.getProductImages().get(0).getUri();
            Drawable drawable = Drawable.createFromPath(this.product_image);
            ((ImageView) this.inflaterView.findViewById(R.id.addProductView_iv_image)).setImageDrawable(drawable);
            ((EditText) this.inflaterView.findViewById(R.id.addProductView_et_title)).setText(product.getTitle());
            ((EditText) this.inflaterView.findViewById(R.id.addProductView_et_info)).setText(product.getInfo());
            ((EditText) this.inflaterView.findViewById(R.id.addProductView_et_price)).setText(product.getPrice());
            ((EditText) this.inflaterView.findViewById(R.id.addProductView_et_count)).setText(String.valueOf(product.getCount()));
            if (!product.getOff().isEmpty()) {
                String[] off = product.getOff().split("\\|");
                ((EditText) this.inflaterView.findViewById(R.id.addProductView_et_off)).setText(off[0]);
                switch (off[1]) {
                    case "%":
                        ((Spinner) this.inflaterView.findViewById(R.id.addProductView_spinner_off)).setSelection(1);
                        break;
                    case "R":
                        ((Spinner) this.inflaterView.findViewById(R.id.addProductView_spinner_off)).setSelection(2);
                        break;
                }
            }
        }

        return this.inflaterView;
    }

    @Override
    public void onClick(View v) {
        this.intentSelectImage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == config.REQUEST_PERMISSION_EXTERNAL_READ) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.intentSelectImage();
            } else if (this.activate) {
                new SweetAlertDialog(this.activity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(this.activity.getResources().getStringArray(R.array.permission_message)[0])
                        .setContentText(this.activity.getResources().getStringArray(R.array.permission_message)[1])
                        .show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == config.ANDROID_INTENT_SELECT_IMAGE) {
            if (data.getClipData() != null) {
                new SweetAlertDialog(this.activity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getResources().getStringArray(R.array.addProductActivity_message)[2])
                        .setContentText(this.inflaterView.getResources().getStringArray(R.array.addProductView_errors)[4])
                        .show();
            } else {
                product_image = config.getRealPath(this.inflaterView.getContext(), data.getData());
                if (product_image != null && !product_image.isEmpty()) {
                    Drawable drawable = Drawable.createFromPath(product_image);
                    ((ImageView) this.inflaterView.findViewById(R.id.addProductView_iv_image)).setImageDrawable(drawable);
                }
            }
        }
    }

    private void intentSelectImage() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, config.REQUEST_PERMISSION_EXTERNAL_READ);
        } else {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.addCategory(Intent.CATEGORY_OPENABLE);
            getIntent.setType("image/*");

            this.activity.startActivityForResult(getIntent, config.ANDROID_INTENT_SELECT_IMAGE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.activate = isVisibleToUser;
        if (this.dataPass != null) {
            this.dataPass.onSetOptionsMenuVisible(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
        this.dataPass = (AddProductDataPassInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.dataPass = null;
    }

    public void onClickFab() {
        this.errors.clear();

        String product_title = ((EditText) this.inflaterView.findViewById(R.id.addProductView_et_title)).getText().toString();
        String product_info = ((EditText) this.inflaterView.findViewById(R.id.addProductView_et_info)).getText().toString();
        String product_price = ((EditText) this.inflaterView.findViewById(R.id.addProductView_et_price)).getText().toString();
        String product_count = ((EditText) this.inflaterView.findViewById(R.id.addProductView_et_count)).getText().toString();
        String product_off = ((EditText) this.inflaterView.findViewById(R.id.addProductView_et_off)).getText().toString();
        String product_spinner = ((Spinner) this.inflaterView.findViewById(R.id.addProductView_spinner_off)).getSelectedItem().toString();

        if (product_spinner.equals(this.inflaterView.getResources().getStringArray(R.array.addProductView_spinner_item)[1])) {
            if (!product_off.isEmpty() && product_off.matches(config.REGEX_INT_VALID) && Integer.parseInt(product_off) <= 100) {
                product_off = Integer.parseInt(product_off) + "|%";
            } else {
                product_off = "";
            }
        } else if (product_spinner.equals(this.inflaterView.getResources().getStringArray(R.array.addProductView_spinner_item)[2])) {
            if (!product_off.isEmpty() && product_off.matches(config.REGEX_INT_VALID) && Integer.parseInt(product_off) <= Integer.parseInt(product_price)) {
                product_off = Integer.parseInt(product_off) + "|R";
            } else {
                product_off = "";
            }
        } else {
            product_off = "";
        }

        Bundle args = new Bundle();

        if (this.validateInput(product_title, product_info, product_price)) {
            args.putString("image", this.product_image);
            args.putString("title", product_title);
            args.putString("info", product_info);
            args.putString("price", product_price);
            args.putInt("count", !product_count.isEmpty() ? Integer.parseInt(product_count) : 0);
            args.putString("off", product_off);
        }
        args.putStringArrayList("errors", this.errors);

        this.dataPass.onDataPassView(args);
    }

    private Boolean validateInput(String product_title, String product_info, String product_price) {
        if (this.product_image.isEmpty()) {
            this.errors.add(this.inflaterView.getResources().getStringArray(R.array.addProductView_errors)[0]);
        }
        if (product_title.isEmpty()) {
            this.errors.add(this.inflaterView.getResources().getStringArray(R.array.addProductView_errors)[1]);
        }
        if (product_info.isEmpty()) {
            this.errors.add(this.inflaterView.getResources().getStringArray(R.array.addProductView_errors)[2]);
        }
        if (product_price.isEmpty()) {
            this.errors.add(this.inflaterView.getResources().getStringArray(R.array.addProductView_errors)[3]);
        }

        return this.errors.size() == 0;
    }
}
