package com.alexzandr.myapplication;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by anekrasov on 15.04.15.
 */
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    private static Context sContext;
    private static String sPreferencesName;
    private static QueryToServer sQueryToServer;
    private static Animation sScaleAnimationForButton;
    private static final int SEEK_BAR_MIN_VALUE = 18;
    private static final float PERCENT_OF_DISPLAY_HEIGHT = 0.3f;
    private static final int DEFAULT_SECTION_HEIGHT_DP = getContext().getResources().getInteger(R.integer.default_height_section);
    private static final int DEFAULT_HEADLINE_HEIGHT_DP = getContext().getResources().getInteger(R.integer.default_height_headline);

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

    public static int getDefaultHeadlineHeightDp(){
        return DEFAULT_HEADLINE_HEIGHT_DP;
    }

    public static int getSeekBarMin(){
        return SEEK_BAR_MIN_VALUE;
    }

    public static int getSeekBarMax(){
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        return (int) (PERCENT_OF_DISPLAY_HEIGHT * dpHeight);
    }

    public static void setPreferencesName(String name){
        sPreferencesName = getContext().getResources().getString(R.string.preference_prefix_name) + name;
    }

    public static String getPreferencesName(){
        return sPreferencesName;
    }

}
