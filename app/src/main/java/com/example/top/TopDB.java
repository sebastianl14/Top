package com.example.top;

import com.dbflow5.annotation.Database;
import com.dbflow5.config.DBFlowDatabase;

@Database(version = TopDB.VERSION)
public abstract class TopDB extends DBFlowDatabase {

    public static final String NAME = "TopDatabase";
    public static final int VERSION = 1;



}
