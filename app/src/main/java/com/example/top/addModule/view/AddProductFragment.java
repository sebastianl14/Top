package com.example.top.addModule.view;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.top.R;
import com.example.top.addModule.AddProductPresenter;
import com.example.top.addModule.AddProductPresenterClass;
import com.example.top.common.pojo.Product;
import com.example.top.common.utils.CommonUtils;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddProductFragment extends DialogFragment implements DialogInterface.OnShowListener, AddProductView {


    @BindView(R.id.etPhotoUrl)
    EditText etPhotoUrl;
    @BindView(R.id.imgPhoto)
    AppCompatImageView imgPhoto;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etQuantity)
    EditText etQuantity;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.contentMain)
    ConstraintLayout contentMain;

    Unbinder unbinder;
    private AddProductPresenter presenter;

    public AddProductFragment() {
        // Required empty public constructor
        presenter = new AddProductPresenterClass(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.addProduct_title)
                .setPositiveButton(R.string.addProduct_dialog_ok, null)
                .setNegativeButton(R.string.label_dialog_cancel, null);

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_product, null);
        unbinder = ButterKnife.bind(this, view);
        builder.setView(view);

        configFocus();
        configEditText();

        AlertDialog dialog  = builder.create();
        dialog.setOnShowListener(this);
        return  dialog;
    }

    private void configFocus() {
        etName.requestFocus();
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
                final String photoUrl = etPhotoUrl.getText().toString().trim();
                if (photoUrl.isEmpty()){
                    imgPhoto.setImageDrawable(null);
                } else {
                    if (getActivity() != null){
                        RequestOptions requestOptions = new RequestOptions().centerCrop();

                        Glide.with(getActivity())
                                .load(photoUrl)
                                .apply(requestOptions)
                                .into(imgPhoto);
                    }
                }
            }
        });
    }

    //    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_add_product, container, false);
//    }


    @Override
    public void onShow(DialogInterface dialogInterface) {
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null){
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CommonUtils.validateProduct(getActivity(), etName, etQuantity, etPhotoUrl)) {
                        Product product = new Product();
                        product.setName(etName.getText().toString());
                        product.setQuantity(Integer.valueOf(etQuantity.getText().toString()));
                        product.setPhotoUrl(etPhotoUrl.getText().toString());
                        presenter.addProduct(product);
                    }
                }
            });

            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        presenter.onShow();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        presenter.onDestroy();
    }

    @Override
    public void enableUIElements() {
        etName.setEnabled(true);
        etQuantity.setEnabled(true);
        etPhotoUrl.setEnabled(true);
    }

    @Override
    public void disableUIElements() {
        etName.setEnabled(false);
        etQuantity.setEnabled(false);
        etPhotoUrl.setEnabled(false);
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
    public void productAdded() {
        Toast.makeText(getActivity(), R.string.addProduct_message_added_successfully, Toast.LENGTH_LONG).show();
        dismiss();
    }

    @Override
    public void showError(int resMsg) {
        Snackbar.make(contentMain, resMsg, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.addProduct_snackbar_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }

    @Override
    public void maxValueError(int resMsg) {
        etQuantity.setError(getString(resMsg));
        etQuantity.requestFocus();
    }

}
