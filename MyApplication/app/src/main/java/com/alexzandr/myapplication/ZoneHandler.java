package com.alexzandr.myapplication;

/**
 * Created by anekrasov on 20.04.15.
 */
public class ZoneHandler implements AdapterItemHandler {
    private boolean mAvailability;
    private int mZone;
    private static final String PREFIX_FOR_TEXT = "Zone ";

    public ZoneHandler(int zone){
        this.mZone = zone;
        this.mAvailability = true;
    }

    @Override
    public void setAvailability(boolean isAvailability) {
        this.mAvailability = isAvailability;
    }

    @Override
    public int getType() {
        return ZONE_BUTTON;
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
        return PREFIX_FOR_TEXT + mZone;
    }

    public int getZone(){
        return mZone;
    }
}
