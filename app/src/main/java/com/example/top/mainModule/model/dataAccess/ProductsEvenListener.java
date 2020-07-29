package com.example.top.mainModule.model.dataAccess;

import com.example.top.common.pojo.Product;

public interface ProductsEvenListener {

    void onChildAdded(Product product);

    void onChildUpdated(Product product);

    void onChildRemoved(Product product);

    void onError(int resMsg);
}
