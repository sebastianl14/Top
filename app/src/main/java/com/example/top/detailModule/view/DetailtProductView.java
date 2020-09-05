package com.example.top.detailModule.view;

public interface DetailtProductView {

    void showProgress();
    void hideProgress();
    void enableUIElements();
    void disableUIElements();

    void showFab();
    void hideFab();

    void updateSuccess();
    void updateError();
}
