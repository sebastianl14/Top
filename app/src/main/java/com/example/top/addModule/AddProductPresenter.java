package com.example.top.addModule;

import com.example.top.addModule.events.AddProductEvent;
import com.example.top.common.pojo.Product;

public interface AddProductPresenter {

    void onShow();
    void onDestroy();

    void addProduct(Product product);

    void onEventListener(AddProductEvent event);
}
