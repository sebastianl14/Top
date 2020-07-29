package com.example.top.common.utils;

import android.content.Context;
import android.widget.EditText;

import com.example.top.R;
import com.google.firebase.auth.FacebookAuthProvider;

public class CommonUtils {

    public static boolean validateProduct(Context context, EditText etName, EditText etQuantity,
                                          EditText etPhotoUrl){
        boolean esValido = true;

        if (etQuantity.getText().toString().isEmpty()){
            etQuantity.setError(context.getString(R.string.common_validate_field_required));
            etQuantity.requestFocus();
            esValido = false;
        } else if (Integer.valueOf(etQuantity.getText().toString()) <= 0){
            etQuantity.setError(context.getString(R.string.common_validate_min_quantity));
            etQuantity.requestFocus();
            esValido = false;
        }

        if (etPhotoUrl.getText().toString().isEmpty()){
            etPhotoUrl.setError(context.getString(R.string.common_validate_field_required));
            etPhotoUrl.requestFocus();
            esValido = false;
        }

        if (etName.getText().toString().isEmpty()){
            etName.setError(context.getString(R.string.common_validate_field_required));
            etName.requestFocus();
            esValido = false;
        }
        return esValido;
    }
}
