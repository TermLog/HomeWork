package com.alexzandr.myapplication.handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.application.Singleton;

/**
 * Created by anekrasov on 20.04.15.
 */
public class SectionHandler implements AdapterItemHandler {
    private boolean mAvailability;
    private int mZone;
    private int mLevel;
    private int mBlockedType;
    private SharedPreferences mPreferences;
    private static final String PREFIX_FOR_TEXT = "P";
    private static final String SEPARATOR_FOR_TEXT = ", ";
    private static final String PREFIX_FOR_KEY = "P";
    private static final String SEPARATOR_FOR_KEY = "_";

    public SectionHandler(int zone, int level){
        this.mZone = zone;
        this.mLevel = level;
        this.mAvailability = true;
        this.mBlockedType = UNBLOCKED;
        mPreferences = Singleton.getContext().getSharedPreferences(Singleton.getPreferencesName(), Context.MODE_PRIVATE);
    }

    @Override
    public void setAvailability(boolean isAvailability) {
        this.mAvailability = isAvailability;
    }

    @Override
    public int getType() {
        return SECTION_BUTTON;
    }

    @Override
    public int getTextColor() {
        return isAvailability() ? R.color.text_black : R.color.text_grey;
    }

    @Override
    public int getBackgroundColor() {
        String key, defaultValue, value;

        if (isAvailability()) {
            switch (getBlockedType()) {
                case UNBLOCKED:
                    key = Singleton.getContext().getResources().getString(R.string.preference_key_color_unblocked);
                    defaultValue = Singleton.getContext().getResources().getString(R.string.default_color_unblocked);
                    value = mPreferences.getString(key, defaultValue);
                    return Color.parseColor(value);
                case BLOCKED:
                    key = Singleton.getContext().getResources().getString(R.string.preference_key_color_blocked);
                    defaultValue = Singleton.getContext().getResources().getString(R.string.default_color_blocked);
                    value = mPreferences.getString(key, defaultValue);
                    return Color.parseColor(value);
                case BOTH:
                    key = Singleton.getContext().getResources().getString(R.string.preference_key_color_both);
                    defaultValue = Singleton.getContext().getResources().getString(R.string.default_color_both);
                    value = mPreferences.getString(key, defaultValue);
                    return Color.parseColor(value);
            }
        } else {
            key = Singleton.getContext().getResources().getString(R.string.preference_key_color_notExists);
            defaultValue = Singleton.getContext().getResources().getString(R.string.default_color_notExists);
            value = mPreferences.getString(key, defaultValue);
            return Color.parseColor(value);
        }
        return 0;
    }

    @Override
    public boolean isAvailability() {
        return mAvailability;
    }

    @Override
    public String getTextForView() {
        String text = "00" + getZone();
        text = PREFIX_FOR_TEXT + text.substring(text.length() - 2) + SEPARATOR_FOR_TEXT + getLevel();
        return text;
    }

    public String getStringForKey(){
        return PREFIX_FOR_KEY + getZone() + SEPARATOR_FOR_KEY + getLevel();
    }

    public int getLevel(){
        return mLevel;
    }

    public int getZone(){
        return mZone;
    }

    public void setBlockedType(int type){
        this.mBlockedType = type;
    }

    public int getBlockedType() {
        return mBlockedType;
    }
}
