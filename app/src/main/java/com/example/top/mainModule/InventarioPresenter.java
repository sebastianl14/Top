package com.example.top.mainModule;

import com.example.top.common.pojo.Product;
import com.example.top.mainModule.events.InventarioEvent;

public interface InventarioPresenter {

    void onCreate();
    void onPause();
    void onResume();
    void onDestroy();

    void remove(Product product);

    void onEvenListener(InventarioEvent inventarioEvent);
}
