package com.alexzandr.myapplication;

import android.app.Application;
import android.content.Context;

/**
 * Created by AlexZandR on 07.04.2015.
 */
public class GlobalAccess extends Application {
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
