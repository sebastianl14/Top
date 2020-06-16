package com.example.top.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FcmMessagingService extends Service {
    public FcmMessagingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
