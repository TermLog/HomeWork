package com.alexzandr.myapplication.handler;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.Singleton;

/**
 * Created by anekrasov on 20.04.15.
 */
public class LevelHandler implements AdapterItemHandler {
    private boolean mAvailability;
    private int mLevel;
    private static final String PREFIX_FOR_TEXT = "LvL ";
    public static final int REFRESH_BUTTON = 0;

    public LevelHandler(int level){
        this.mLevel = level;
        this.mAvailability = true;
    }

    @Override
    public void setAvailability(boolean isAvailability) {
        this.mAvailability = isAvailability;
    }

    @Override
    public int getType() {
        return LEVEL_BUTTON;
    }

    @Override
    public int getTextColor() {
        return isAvailability() ? Singleton.getColor(R.color.text_white) : Singleton.getColor(R.color.text_grey);
    }

    @Override
    public int getBackgroundColor() {
        return isAvailability() ? Singleton.getColor(R.color.background_blue) : Singleton.getColor(R.color.background_grey);
    }

    @Override
    public boolean isAvailability() {
        return mAvailability;
    }

    @Override
    public String getTextForButton() {
        return mLevel != REFRESH_BUTTON ? PREFIX_FOR_TEXT + mLevel : Singleton.getContext().getResources().getString(R.string.lock_buttonRefresh);
    }

    public int getLevel(){
        return mLevel;
    }
}
