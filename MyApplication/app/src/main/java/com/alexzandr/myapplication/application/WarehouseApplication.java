package com.alexzandr.myapplication.application;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by AlexZandR on 07.04.2015.
 */
public class WarehouseApplication extends Application {
    public static Context sContext;

    @Override
    public void onCreate(){
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getApp(){
        return sContext;
    }
}
