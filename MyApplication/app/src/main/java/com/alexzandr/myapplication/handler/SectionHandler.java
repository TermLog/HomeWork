package com.alexzandr.myapplication.handler;

import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.Singleton;

/**
 * Created by anekrasov on 20.04.15.
 */
public class SectionHandler implements AdapterItemHandler {
    private boolean mAvailability;
    private int mZone;
    private int mLevel;
    private int mBlockedType;
    private static final String PREFIX_FOR_TEXT = "P";
    private static final String SEPARATOR_FOR_TEXT = ", ";
    private static final String PREFIX_FOR_KEY = "P";
    private static final String SEPARATOR_FOR_KEY = "_";

    public SectionHandler(int zone, int level){
        this.mZone = zone;
        this.mLevel = level;
        this.mAvailability = true;
        this.mBlockedType = UNBLOCKED;
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
        if (isAvailability()) {
            switch (getBlockedType()) {
                case UNBLOCKED:
                    return Singleton.getColor(R.color.lockUnlock_button_unblocked);
                case BLOCKED:
                    return Singleton.getColor(R.color.lockUnlock_button_blocked);
                case BOTH:
                    return Singleton.getColor(R.color.lockUnlock_button_both);
            }
        } else {
            return Singleton.getColor(R.color.background_grey);
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
