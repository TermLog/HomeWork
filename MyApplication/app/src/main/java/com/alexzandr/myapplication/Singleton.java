package com.alexzandr.myapplication;

import android.content.Context;

/**
 * Created by anekrasov on 15.04.15.
 */
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    private static Context sContext;
    private static QueryToServer sQueryToServer;

    private Singleton(){
        sContext =  GlobalAccess.getApp();
    }

    public static void setQuery(QueryToServer query){
        sQueryToServer = query;
    }

    public static Context getContext(){
       return sContext;
    }

    public static QueryToServer getQueryToServer(){
        return sQueryToServer;
    }

}
