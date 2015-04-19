package com.alexzandr.myapplication;

/**
 * Created by AlexZandR on 19.04.2015.
 */
public class AdapterItemHandler {
    private int mType;
    private int mZone;
    private int mLevel;
    private int mBlockedType;
    private boolean mAvailability;
    public final static int ZONE_BUTTON = 1;
    public final static int SECTION_BUTTON = 2;
    private final static int UNBLOCKED = 1;
    private final static int BLOCKED = 2;
    private final static int BOTH = 3;

    public AdapterItemHandler(int zone) {
        this(ZONE_BUTTON, zone, 0, 0, true);
    }

    public AdapterItemHandler(int zone, int level) {
        this(SECTION_BUTTON, zone, level, 0, true);
    }

    public AdapterItemHandler(int type, int zone, int level, int blocked, boolean availability) {
        setType(type);
        setZone(zone);
        setLevel(level);
        setBlockedType(blocked);
        setAvailability(availability);
    }

    void setType(int type) {
        this.mType = type;
    }

    void setZone(int zone) {
        this.mZone = zone;
    }

    void setLevel(int level) {
        this.mLevel = level;
    }

    public void setAvailability(boolean availability) {
        this.mAvailability = availability;
    }

    public void setBlockedType(int block) {
        this.mBlockedType = block;
    }

    public int getType(){
        return this.mType;
    }

    public int getZone() {
        return this.mZone;
    }

    public int getLevel() {
        return this.mLevel;
    }

    public int getBlockedType() {
        return this.mBlockedType;
    }

    public boolean getAvailability() {
        return this.mAvailability;
    }

    public String getStringForKey(){
        return "P" + getZone() + "_" + getLevel();
    }

    public String getTextForButton() {
        if (getType() == SECTION_BUTTON) {
            String text = "00" + getZone();
            text = "P" + text.substring(text.length() - 2) + ", " + getLevel();
            return text;
        }
        return "Zone " + getZone();
    }

    public int getBackgroundColor() {
        if (getAvailability()) {
            if (getType() == SECTION_BUTTON) {
                switch (getBlockedType()) {
                    case UNBLOCKED:
                        return Singleton.getContext().getResources().getColor(R.color.lockUnlock_button_unblocked);
                    case BLOCKED:
                        return Singleton.getContext().getResources().getColor(R.color.lockUnlock_button_blocked);
                    case BOTH:
                        return Singleton.getContext().getResources().getColor(R.color.lockUnlock_button_both);
                }
            }
            return Singleton.getContext().getResources().getColor(R.color.lockUnlock_button_zoneAndLevel);
        } else {
            return Singleton.getContext().getResources().getColor(R.color.background_grey);
        }
    }

    public int getTextColor() {
        if (getAvailability()) {
            if (getType() == ZONE_BUTTON) {
                return Singleton.getContext().getResources().getColor(R.color.text_white);
            } else {
                return Singleton.getContext().getResources().getColor(R.color.text_black);
            }
        } else {
            return Singleton.getContext().getResources().getColor(R.color.text_grey);
        }
    }


}
