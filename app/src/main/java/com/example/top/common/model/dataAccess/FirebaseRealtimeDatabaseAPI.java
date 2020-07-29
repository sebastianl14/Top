package com.example.top.common.model.dataAccess;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseRealtimeDatabaseAPI {

    private static final String PATH_PRODUCTS = "products";

    private DatabaseReference databaseReference;

    private static FirebaseRealtimeDatabaseAPI INSTANCE = null;

    private FirebaseRealtimeDatabaseAPI() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseRealtimeDatabaseAPI getInstance(){
        if (INSTANCE == null){
            INSTANCE = new FirebaseRealtimeDatabaseAPI();
        }
        return INSTANCE;
    }

    //Referencias
    public DatabaseReference getDatabaseReference(){
        return databaseReference;
    }

    public DatabaseReference getProductsReference(){
        return getDatabaseReference().child(PATH_PRODUCTS);
    }


}
