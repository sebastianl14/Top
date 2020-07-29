package com.example.top.mainModule.view;

import com.example.top.common.pojo.Product;

public interface InventarioView {

    void showProgress();
    void hideProgress();

    void add(Product product);
    void update(Product product);
    void remove(Product product);

    void removeFail();
    void onShowError(int resMsg);

}
