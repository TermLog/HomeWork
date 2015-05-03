package com.alexzandr.myapplication;

import android.content.Context;
import android.util.TypedValue;
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
    private static final int DEFAULT_SECTION_HEIGHT_DP = 80;
    private static final int DEFAULT_HEADLINE_HEIGHT_DP = 80;

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

    public static int getColor(int color){
        return getContext().getResources().getColor(color);
    }

    public static int getDefaultSectionHeightDp(){
        return DEFAULT_SECTION_HEIGHT_DP;
    }

    public static int getDefaultSectionHeightPx(){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_SECTION_HEIGHT_DP,
                getContext().getResources().getDisplayMetrics());
    }

    public static int getDefaultHeadlineHeightDp(){
        return DEFAULT_HEADLINE_HEIGHT_DP;
    }

    public static int getDefaultHeadlineHeightPx(){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_HEADLINE_HEIGHT_DP,
                getContext().getResources().getDisplayMetrics());
    }

}
