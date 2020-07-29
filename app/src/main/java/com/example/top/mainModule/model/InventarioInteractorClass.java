package com.example.top.mainModule.model;

import com.example.top.common.BasicErrorEventCallback;
import com.example.top.common.pojo.Product;
import com.example.top.mainModule.events.InventarioEvent;
import com.example.top.mainModule.model.dataAccess.ProductsEvenListener;
import com.example.top.mainModule.model.dataAccess.RealtimeDatabase;

import org.greenrobot.eventbus.EventBus;

public class InventarioInteractorClass implements InventarioInteractor {

    private RealtimeDatabase realtimeDatabase;

    public InventarioInteractorClass() {
        realtimeDatabase = new RealtimeDatabase();
    }

    @Override
    public void subscribeToProducts() {
        realtimeDatabase.suscribeToProducts(new ProductsEvenListener() {
            @Override
            public void onChildAdded(Product product) {
                post(product, InventarioEvent.SUCCESS_ADD);
            }

            @Override
            public void onChildUpdated(Product product) {
                post(product, InventarioEvent.SUCCESS_UPDATE);
            }

            @Override
            public void onChildRemoved(Product product) {
                post(product, InventarioEvent.SUCCESS_REMOVE);
            }

            @Override
            public void onError(int resMsg) {
                post(InventarioEvent.ERROR_SERVER, resMsg);
            }
        });

    }

    @Override
    public void unsubscribeToProducts() {
        realtimeDatabase.unsuscribeToProducts();
    }

    @Override
    public void removeProduct(Product product) {
        realtimeDatabase.removeProduct(product, new BasicErrorEventCallback() {
            @Override
            public void onSuccess() {
                post(InventarioEvent.SUCCESS_REMOVE);
            }
            @Override
            public void onError(int typeEvent, int resMsg) {
                post(typeEvent, resMsg);
            }
        });
    }

    private void post(int typeEvent) {
        post(null, typeEvent, 0);
    }

    private void post(int typeEvent, int resMsg) {
        post(null, typeEvent, resMsg);
    }

    private void post(Product product, int typeEvent) {
        post(product, typeEvent, 0);
    }

    private void post(Product product, int typeEvent, int resMsg) {
        InventarioEvent inventarioEvent = new InventarioEvent();
        inventarioEvent.setProduct(product);
        inventarioEvent.setTypeEvent(typeEvent);
        inventarioEvent.setResMsg(resMsg);

        EventBus.getDefault().post(inventarioEvent);
    }
}
