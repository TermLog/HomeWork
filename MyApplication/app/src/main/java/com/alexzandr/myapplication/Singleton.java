package com.alexzandr.myapplication;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by anekrasov on 15.04.15.
 */
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    private static Context sContext;
    private static QueryToServer sQueryToServer;
    private static Animation sScaleAnimationForButton;

    private Singleton(){
        sContext =  GlobalAccess.getApp();
        sScaleAnimationForButton = AnimationUtils.loadAnimation(sContext, R.anim.button_scale_animation);
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

    public static Animation getAnimation () {
        return sScaleAnimationForButton;
    }

}
