package com.example.woods.amin.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.woods.amin.Controller.ImagesController;
import com.example.woods.amin.Database.Images;
import com.example.woods.amin.Interface.AddProductDataPassInterface;
import com.example.woods.amin.Adapter.GridViewImagesAdapter;
import com.example.woods.amin.R;
import com.example.woods.amin.config;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddProductPicturesFragment extends Fragment implements View.OnClickListener {
    private View inflaterView = null;
    private AddProductDataPassInterface dataPass = null;
    private Long edit = -1L;
    private Activity activity = null;
    private Boolean activate = false;
    private List<String> images = null;
    private GridViewImagesAdapter adapter = null;
    private GridView gridView = null;

    public AddProductPicturesFragment() {
    }

    public static AddProductPicturesFragment newInstance(Long edit) {
        AddProductPicturesFragment fragment = new AddProductPicturesFragment();
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
        // Inflate the layout for this fragment
        this.inflaterView = inflater.inflate(R.layout.fragment_add_product_pictures, container, false);
        this.images = new ArrayList<>();

        if (this.edit != -1L) {
            ImagesController imagesController = new ImagesController(this.activity);
            List<Images> imagesProduct = imagesController.getProductImage(this.edit);
            if (imagesProduct != null) {
                for (int i = 0; i < imagesProduct.size(); i++) {
                    this.images.add(imagesProduct.get(i).getUri());
                }
            }
        }

        this.inflaterView.findViewById(R.id.addProductPictures_bt_add).setOnClickListener(this);
        this.gridView = (GridView) this.inflaterView.findViewById(R.id.addProductPictures_gv_list);
        this.adapter = new GridViewImagesAdapter(this.activity, this.images);
        this.gridView.setAdapter(this.adapter);

        return this.inflaterView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == config.REQUEST_PERMISSION_EXTERNAL_READ) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.intentSelectImages();
            } else if (this.activate) {
                new SweetAlertDialog(this.activity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(this.activity.getResources().getStringArray(R.array.permission_message)[0])
                        .setContentText(this.activity.getResources().getStringArray(R.array.permission_message)[1])
                        .show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        this.intentSelectImages();
    }

    private void intentSelectImages() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, config.REQUEST_PERMISSION_EXTERNAL_READ);
        } else {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.addCategory(Intent.CATEGORY_OPENABLE);
            getIntent.setType("image/*");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }

            this.activity.startActivityForResult(getIntent, config.ANDROID_INTENT_SELECT_IMAGES);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == config.ANDROID_INTENT_SELECT_IMAGES) {
            if (data.getClipData() != null) {
                ClipData images = data.getClipData();
                for (int i = 0; i < images.getItemCount(); i++) {
                    ClipData.Item item = images.getItemAt(i);
                    this.insertDrawer(item.getUri());
                }
            } else {
                this.insertDrawer(data.getData());
            }
        }
    }

    public void deletePictures() {
        this.adapter.deleteSelected();
        this.images = this.adapter.getImages();
        this.gridView.setAdapter(this.adapter);
    }

    private void insertDrawer(Uri uri) {
        String product_image = config.getRealPath(this.inflaterView.getContext(), uri);
        if (this.adapter != null && product_image != null && !product_image.isEmpty()) {
            if (this.images.indexOf(product_image) != 0) {
                this.images.add(product_image);
                this.adapter.changed();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.activate = isVisibleToUser;
        if (this.dataPass != null && this.adapter != null && !this.adapter.isEmptySelected()) {
            this.dataPass.onSetOptionsMenuVisible(true);
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
        Bundle args = new Bundle();
        String[] images = new String[this.images.size()];

        for (int i = 0; i < this.images.size(); i++) {
            images[i] = this.images.get(i);
        }
        args.putStringArray("images", images);

        dataPass.onDataPassPictures(args);
    }
}
