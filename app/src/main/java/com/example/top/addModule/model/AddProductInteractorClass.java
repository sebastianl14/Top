package com.example.top.addModule.model;

import com.example.top.addModule.events.AddProductEvent;
import com.example.top.addModule.model.dataAccess.RealtimeDatabase;
import com.example.top.common.BasicErrorEventCallback;
import com.example.top.common.pojo.Product;

import org.greenrobot.eventbus.EventBus;

public class AddProductInteractorClass implements  AddProductInteractor {

    private RealtimeDatabase realtimeDatabase;

    public AddProductInteractorClass() {
        realtimeDatabase = new RealtimeDatabase();
    }

    @Override
    public void addProduct(Product product) {
        realtimeDatabase.addProduct(product, new BasicErrorEventCallback() {
            @Override
            public void onSuccess() {
                post(AddProductEvent.SUCCESS_ADD);
            }

            @Override
            public void onError(int typeEvent, int resMsg) {
                post(typeEvent, resMsg);
            }
        });
    }

    private void post(int typeEvent) {
        post(typeEvent, 0);
    }

    private void post(int typeEvent, int resMsg) {
        AddProductEvent event = new AddProductEvent();
        event.setTypeEvent(typeEvent);
        event.setResMsg(resMsg);

        EventBus.getDefault().post(event);
    }
}
