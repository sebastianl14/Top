package com.example.top.common;

public interface BasicErrorEventCallback {

    void onSuccess();
    void onError(int typeEvent, int resMsg);
}
