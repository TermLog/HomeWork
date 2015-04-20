package com.alexzandr.myapplication;

/**
 * Created by anekrasov on 20.04.15.
 */
public interface AdapterItemHandler {
    int ZONE_BUTTON = 1;
    int LEVEL_BUTTON = 2;
    int SECTION_BUTTON = 3;
    int UNBLOCKED = 1;
    int BLOCKED = 2;
    int BOTH = 3;

    void setAvailability(boolean isAvailability);

    int getType();
    int getTextColor();
    int getBackgroundColor();
    boolean isAvailability();
    String getTextForButton();

}
