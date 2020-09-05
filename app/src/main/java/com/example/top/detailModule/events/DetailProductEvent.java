package com.example.top.detailModule.events;

public class DetailProductEvent {

    public static final int UPDATE_SUCCESS = 0;
    public static final int ERROR_SERVER = 100;

    private int typeEvent;

    public DetailProductEvent() {

    }

    public int getTypeEvent() {
        return typeEvent;
    }

    public void setTypeEvent(int typeEvent) {
        this.typeEvent = typeEvent;
    }
}
