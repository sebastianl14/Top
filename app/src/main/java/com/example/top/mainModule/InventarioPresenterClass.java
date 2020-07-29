package com.example.top.mainModule;

import com.example.top.common.pojo.Product;
import com.example.top.mainModule.events.InventarioEvent;
import com.example.top.mainModule.model.InventarioInteractor;
import com.example.top.mainModule.model.InventarioInteractorClass;
import com.example.top.mainModule.view.InventarioView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class InventarioPresenterClass implements InventarioPresenter {

    private InventarioView inventarioView;
    private InventarioInteractor inventarioInteractor;

    public InventarioPresenterClass(InventarioView inventarioView) {
        this.inventarioView = inventarioView;
        this.inventarioInteractor = new InventarioInteractorClass();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        inventarioInteractor.unsubscribeToProducts();
    }

    @Override
    public void onResume() {
        inventarioInteractor.subscribeToProducts();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        inventarioView = null;
    }

    @Override
    public void remove(Product product) {
        if (setProgress()){
            inventarioInteractor.removeProduct(product);
        }
    }

    private boolean setProgress() {
        if (inventarioView != null){
            inventarioView.showProgress();
            return true;
        }
        return false;
    }

    @Subscribe
    @Override
    public void onEvenListener(InventarioEvent inventarioEvent) {
        if (inventarioView != null){
            inventarioView.hideProgress();

            switch (inventarioEvent.getTypeEvent()){
                case InventarioEvent.SUCCESS_ADD:
                    android.util.Log.d("InventarioPresenter", "Adicion nuevo Producto");
                    inventarioView.add(inventarioEvent.getProduct());
                    break;
                case InventarioEvent.SUCCESS_UPDATE:
                    inventarioView.update(inventarioEvent.getProduct());
                    break;
                case InventarioEvent.SUCCESS_REMOVE:
                    inventarioView.remove(inventarioEvent.getProduct());
                    break;
                case InventarioEvent.ERROR_SERVER:
                    inventarioView.onShowError(inventarioEvent.getResMsg());
                    break;
                case InventarioEvent.ERROR_TO_REMOVE:
                    inventarioView.removeFail();
                    break;
            }
        }
    }
}
