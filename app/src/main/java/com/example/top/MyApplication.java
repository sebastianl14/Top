package com.example.top;

import android.app.Application;

import com.dbflow5.config.DatabaseConfig;
import com.dbflow5.config.FlowConfig;
import com.dbflow5.config.FlowManager;
import com.dbflow5.database.AndroidSQLiteOpenHelper;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //FlowManager.init(this);

        FlowManager.init(new FlowConfig.Builder(this)
                .database(DatabaseConfig.builder(TopDB.class, AndroidSQLiteOpenHelper.createHelperCreator(this))
                        .databaseName("TopDatabase")
                .build())
                        .build());

    }
}
