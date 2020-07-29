package com.example.top.addModule;

import com.example.top.addModule.events.AddProductEvent;
import com.example.top.addModule.model.AddProductInteractor;
import com.example.top.addModule.model.AddProductInteractorClass;
import com.example.top.addModule.view.AddProductView;
import com.example.top.common.pojo.Product;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AddProductPresenterClass implements AddProductPresenter {

    private AddProductView view;
    private AddProductInteractor interactor;

    public AddProductPresenterClass(AddProductView view) {
        this.view = view;
        this.interactor = new AddProductInteractorClass();
    }

    @Override
    public void onShow() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        view = null;
    }

    @Override
    public void addProduct(Product product) {
        if (setProgress()){
            interactor.addProduct(product);
        }
    }

    private boolean setProgress() {
        if (view != null){
            view.disableUIElements();
            view.showProgress();
            return true;
        }
        return false;
    }

    @Subscribe
    @Override
    public void onEventListener(AddProductEvent event) {
        if (view != null){
            view.hideProgress();
            view.enableUIElements();

            switch (event.getTypeEvent()){
                case AddProductEvent.SUCCESS_ADD:
                    view.productAdded();
                    break;
                case AddProductEvent.ERROR_MAX_VALUE:
                    view.maxValueError(event.getResMsg());
                    break;
                case AddProductEvent.ERROR_SERVER:
                    view.showError(event.getResMsg());
                    break;
            }
        }
    }
}
