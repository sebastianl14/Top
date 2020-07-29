package com.example.top.mainModule.model;

import com.example.top.common.pojo.Product;

public interface InventarioInteractor {

    void subscribeToProducts();
    void unsubscribeToProducts();

    void removeProduct(Product product);
}
