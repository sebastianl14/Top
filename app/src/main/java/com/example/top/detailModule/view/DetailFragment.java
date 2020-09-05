package com.example.top.detailModule.view;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.top.R;
import com.example.top.common.pojo.Product;
import com.example.top.common.utils.CommonUtils;
import com.example.top.detailModule.DetailProductPresenter;
import com.example.top.detailModule.DetailProductPresenterClass;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements DetailtProductView {


    @BindView(R.id.etName)
    TextInputEditText etName;
    @BindView(R.id.etQuantity)
    EditText etQuantity;
    @BindView(R.id.etPhotoUrl)
    EditText etPhotoUrl;
    @BindView(R.id.imgPhoto)
    AppCompatImageView imgPhoto;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.contentMain)
    ConstraintLayout contentMain;

    Unbinder unbinder;
    private Product product;
    private DetailProductPresenter presenter;

    public DetailFragment() {
        // Required empty public constructor
        presenter = new DetailProductPresenterClass(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            configProduct(getArguments());
            configValues();
            configEditText();
        }
        presenter.onCreate();
        return view;
    }

    private void configProduct(Bundle args) {
        product = new Product();
        product.setId(args.getString(Product.ID));
        product.setName(args.getString(Product.NAME));
        product.setPhotoUrl(args.getString(Product.PHOTO_URL));
        product.setQuantity(args.getInt(Product.QUANTITY));
    }

    private void configValues() {
        etName.setText(product.getName());
        etQuantity.setText(String.valueOf(product.getQuantity()));
        etPhotoUrl.setText(product.getPhotoUrl());

        configPhoto(product.getPhotoUrl());
    }

    private void configPhoto(String photoUrl) {

        if (getActivity() != null) {
            RequestOptions options = new RequestOptions()
                    //.diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop();

            Glide.with(getActivity())
                    .load(photoUrl)
                    .apply(options)
                    .into(imgPhoto);
        }
    }

    private void configEditText() {
        etPhotoUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String photoUrl = etPhotoUrl.getText().toString().trim();
                if (photoUrl.isEmpty()) {
                    imgPhoto.setImageDrawable(null);
                } else {
                    configPhoto(photoUrl);
                }
            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        presenter.onDestroy();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void enableUIElements() {
        etName.setEnabled(true);
        etQuantity.setEnabled(true);
        etPhotoUrl.setEnabled(true);
        btnSave.setEnabled(true);
    }

    @Override
    public void disableUIElements() {
        etName.setEnabled(false);
        etQuantity.setEnabled(false);
        etPhotoUrl.setEnabled(false);
        btnSave.setEnabled(false);
    }

    @Override
    public void showFab() {
        if (getActivity() != null) {
            getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideFab() {
        if (getActivity() != null) {
            getActivity().findViewById(R.id.fab).setVisibility(View.GONE);
        }
    }

    @Override
    public void updateSuccess() {
        Snackbar.make(contentMain, R.string.detailProduct_update_successfully, Snackbar.LENGTH_LONG)
                .setAction(R.string.detailProduct_snackbar_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                }).show();
    }

    @Override
    public void updateError() {
        Snackbar.make(contentMain, R.string.detailProduct_update_error, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.btnSave)
    public void onViewClicked() {

        if (CommonUtils.validateProduct(getActivity(), etName, etQuantity, etPhotoUrl)) {
            product.setName(etName.getText().toString());
            product.setQuantity(Integer.valueOf(etQuantity.getText().toString().trim()));
            product.setPhotoUrl(etPhotoUrl.getText().toString());

            presenter.updateProduct(product);
        }
    }
}
