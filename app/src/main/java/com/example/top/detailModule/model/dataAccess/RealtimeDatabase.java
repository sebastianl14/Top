package com.example.top.detailModule.model.dataAccess;

import androidx.annotation.NonNull;

import com.example.top.common.BasicEventCallback;
import com.example.top.common.model.dataAccess.FirebaseRealtimeDatabaseAPI;
import com.example.top.common.pojo.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class RealtimeDatabase {

    private FirebaseRealtimeDatabaseAPI firebaseRealtimeDatabaseAPI;

    public RealtimeDatabase() {
        firebaseRealtimeDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
    }

    public void updateProduct(Product product, BasicEventCallback callback){
        firebaseRealtimeDatabaseAPI.getProductsReference().child(product.getId()).setValue(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onError();
                    }
                });
    }
}
