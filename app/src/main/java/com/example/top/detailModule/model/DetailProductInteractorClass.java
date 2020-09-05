package com.example.top.detailModule.model;

import com.example.top.common.BasicEventCallback;
import com.example.top.common.pojo.Product;
import com.example.top.detailModule.events.DetailProductEvent;
import com.example.top.detailModule.model.dataAccess.RealtimeDatabase;

import org.greenrobot.eventbus.EventBus;

public class DetailProductInteractorClass implements DetailProductInteractor {

    private RealtimeDatabase realtimeDatabase;

    public DetailProductInteractorClass() {
        realtimeDatabase = new RealtimeDatabase();
    }

    @Override
    public void updateProduct(Product product) {
        realtimeDatabase.updateProduct(product, new BasicEventCallback() {
            @Override
            public void onSuccess() {
                post(DetailProductEvent.UPDATE_SUCCESS);
            }

            @Override
            public void onError() {
                post(DetailProductEvent.ERROR_SERVER);
            }
        });
    }

    private void post(int typeEvent) {
        DetailProductEvent event = new DetailProductEvent();
        event.setTypeEvent(typeEvent);

        EventBus.getDefault().post(event);
    }
}
