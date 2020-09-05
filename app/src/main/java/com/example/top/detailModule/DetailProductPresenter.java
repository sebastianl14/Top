package com.example.top.detailModule;

import com.example.top.common.pojo.Product;
import com.example.top.detailModule.events.DetailProductEvent;

public interface DetailProductPresenter {

    void onCreate();
    void onDestroy();

    void updateProduct(Product product);

    void onEventListener(DetailProductEvent event);
}
