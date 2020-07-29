package com.example.top.mainModule.model.dataAccess;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.top.R;
import com.example.top.common.BasicErrorEventCallback;
import com.example.top.common.model.dataAccess.FirebaseRealtimeDatabaseAPI;
import com.example.top.common.pojo.Product;
import com.example.top.mainModule.events.InventarioEvent;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class RealtimeDatabase {

    private FirebaseRealtimeDatabaseAPI firebaseRealtimeDatabaseAPI;
    private ChildEventListener productsChildEventListener;

    public RealtimeDatabase() {
        firebaseRealtimeDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
    }

    public void suscribeToProducts(ProductsEvenListener listener){
        if (productsChildEventListener == null){
            productsChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    listener.onChildAdded(getProduct(dataSnapshot));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    listener.onChildUpdated(getProduct(dataSnapshot));
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    listener.onChildRemoved(getProduct(dataSnapshot));
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    switch (databaseError.getCode()){
                        case DatabaseError.PERMISSION_DENIED:
                            listener.onError(R.string.inventario_error_permission_denied);
                            break;
                        default:
                            listener.onError(R.string.inventario_error_server);
                            break;
                    }
                }
            };
        }

        firebaseRealtimeDatabaseAPI.getProductsReference().addChildEventListener(productsChildEventListener);
    }

    private Product getProduct(DataSnapshot dataSnapshot) {
        Product product = dataSnapshot.getValue(Product.class);
        if (product != null){
            product.setId(dataSnapshot.getKey());
        }
        return product;
    }

    public void unsuscribeToProducts(){
        if (productsChildEventListener != null){
            firebaseRealtimeDatabaseAPI.getProductsReference().removeEventListener(productsChildEventListener);
        }
    }

    public void removeProduct(Product product, BasicErrorEventCallback callback){
        firebaseRealtimeDatabaseAPI.getProductsReference().child(product.getId())
                .removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null){
                            callback.onSuccess();
                        } else {
                            switch (databaseError.getCode()){
                                case DatabaseError.PERMISSION_DENIED:
                                    callback.onError(InventarioEvent.ERROR_TO_REMOVE, R.string.inventario_error_remove);
                                    break;
                                default:
                                    callback.onError(InventarioEvent.ERROR_SERVER, R.string.inventario_error_server);
                                    break;
                            }
                        }
                    }
                });
    }
}
