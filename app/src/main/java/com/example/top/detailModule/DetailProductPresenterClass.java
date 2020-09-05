package com.example.top.detailModule;

import com.example.top.common.pojo.Product;
import com.example.top.detailModule.events.DetailProductEvent;
import com.example.top.detailModule.model.DetailProductInteractor;
import com.example.top.detailModule.model.DetailProductInteractorClass;
import com.example.top.detailModule.view.DetailtProductView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class DetailProductPresenterClass implements DetailProductPresenter {

    private DetailProductInteractor interactor;
    private DetailtProductView view;

    public DetailProductPresenterClass(DetailtProductView view) {
        this.view = view;
        this.interactor = new DetailProductInteractorClass();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
        if (view != null){
            view.hideFab();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (view != null){
            view.showFab();
            view = null;
        }
    }

    @Override
    public void updateProduct(Product product) {
        if (setProgress()){
            interactor.updateProduct(product);
        }
    }

    private boolean setProgress() {
        if (view != null){
            view.showProgress();
            view.disableUIElements();
            return true;
        }
        return false;
    }

    @Subscribe
    @Override
    public void onEventListener(DetailProductEvent event) {

        if (view != null){
            view.hideProgress();
            view.enableUIElements();

            switch (event.getTypeEvent()){
                case DetailProductEvent.UPDATE_SUCCESS:
                    view.updateSuccess();
                    break;
                case DetailProductEvent.ERROR_SERVER:
                    view.updateError();
                    break;
            }
        }
    }
}
