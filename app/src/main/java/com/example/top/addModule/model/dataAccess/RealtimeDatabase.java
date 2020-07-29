package com.example.top.addModule.model.dataAccess;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.top.R;
import com.example.top.addModule.events.AddProductEvent;
import com.example.top.common.BasicErrorEventCallback;
import com.example.top.common.model.dataAccess.FirebaseRealtimeDatabaseAPI;
import com.example.top.common.pojo.Product;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class RealtimeDatabase {

    private FirebaseRealtimeDatabaseAPI firebaseRealtimeDatabaseAPI;

    public RealtimeDatabase() {
        firebaseRealtimeDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
    }

    public void addProduct(Product product, BasicErrorEventCallback callback){
        firebaseRealtimeDatabaseAPI.getProductsReference().push().setValue(product, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null){
                    callback.onSuccess();
                } else {
                     switch (databaseError.getCode()){
                         case DatabaseError.PERMISSION_DENIED:
                             callback.onError(AddProductEvent.ERROR_MAX_VALUE,
                                     R.string.addProduct_message_validate_max_quantity);
                             break;
                         default:
                             callback.onError(AddProductEvent.ERROR_SERVER,
                                     R.string.addProduct_message_added_error);
                             break;
                     }
                }
            }
        });

    }
}
