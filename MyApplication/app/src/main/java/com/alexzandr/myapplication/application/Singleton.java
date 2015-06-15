package com.alexzandr.myapplication.application;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.alexzandr.myapplication.sql.QueryToServer;
import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.fragment.WarehouseFragment;

/**
 * Created by anekrasov on 15.04.15.
 */
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    private static Context sContext;
    private static WarehouseFragment mSavedFragment;
    private static String sPreferencesName;
    private static QueryToServer sQueryToServer;
    private static Animation sScaleAnimationForButton;
    private static boolean sTabletModeDetermined = false;
    private static boolean sTabletMode = false;
    private static final int SEEK_BAR_MIN_VALUE = 18;
    private static final float PERCENT_OF_DISPLAY_HEIGHT = 0.3f;
    private static final int DEFAULT_SECTION_HEIGHT_DP = getContext().getResources().getInteger(R.integer.default_height_section);
    private static final int DEFAULT_HEADLINE_HEIGHT_DP = getContext().getResources().getInteger(R.integer.default_height_headline);

    private Singleton(){
        sContext =  WarehouseApplication.getApp();
        sScaleAnimationForButton = AnimationUtils.loadAnimation(sContext, R.anim.button_scale_animation);
    }

    public static Animation getAnimation () {
        return sScaleAnimationForButton;
    }

    public static int getColor(int color){
        return getContext().getResources().getColor(color);
    }

    public static int getDefaultHeadlineHeightDp(){
        return DEFAULT_HEADLINE_HEIGHT_DP;
    }

    public static int getDefaultSectionHeightDp(){
        return DEFAULT_SECTION_HEIGHT_DP;
    }

    private static DisplayMetrics getDisplayMetrics() {
        return getContext().getResources().getDisplayMetrics();
    }

    public static Context getContext(){
        return sContext;
    }

    public static String getPreferencesName(){
        return sPreferencesName;
    }

    public static WarehouseFragment getSavedFragment(){
        return mSavedFragment;
    }

    public static int getSeekBarMax(){
        float dpHeight = getDisplayMetrics().heightPixels / getDisplayMetrics().density;
        return (int) (PERCENT_OF_DISPLAY_HEIGHT * dpHeight);
    }

    public static int getSeekBarMin(){
        return SEEK_BAR_MIN_VALUE;
    }

    public static QueryToServer getQueryToServer(){
        return sQueryToServer;
    }

    public static void setPreferencesName(String name){
        sPreferencesName = getContext().getResources().getString(R.string.preference_prefix_name) + name;
    }

    public static void setQuery(QueryToServer query){
        sQueryToServer = query;
    }

    public static void saveFragment(WarehouseFragment fragment){
        mSavedFragment = fragment;
    }

    public static void clearSavedFragment() {
        mSavedFragment = null;
    }

    public static boolean isTablet() {
        if (!sTabletModeDetermined) {
            if (sContext.getResources().getConfiguration().smallestScreenWidthDp>= 600) {
                sTabletMode = true;
            }
            sTabletModeDetermined = true;
        }
        return sTabletMode;
    }

    public static boolean isPortOrientation() {
        return getDisplayMetrics().widthPixels < getDisplayMetrics().heightPixels;
    }
}
