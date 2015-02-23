package com.alexzandr.myapplication;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by AlexZandR on 22.02.2015.
 */
public class BlockButton extends Button {
    private int mZone;
    private int mLevel;
    private int mBlocked;
    final static int UNBLOCKED = 1;
    final static int BLOCKED = 2;
    final static int BOTH = 3;

    public BlockButton(Context context, int zone, int level, int blocked) {
        super(context);
        setZone(zone);
        setLevel(level);
        setBlocked(blocked);
        addListener();
    }

    public void setZone(int zone) {
        this.mZone = zone;
    }
    public void setLevel(int level) {
        this.mLevel = level;
    }
    public void setBlocked(int block) {
        this.mBlocked = block;
        setAndroidSettings();
    }

    public int getZone() {
        return this.mZone;
    }
    public int getLevel() {
        return this.mLevel;
    }
    public int getBlocked() {
        return this.mBlocked;
    }

    private void setAndroidSettings(){
        switch (mBlocked){
            case UNBLOCKED:
                setBackgroundResource(R.color.lockUnlock_button_unblocked);
                break;
            case BLOCKED:
                setBackgroundResource(R.color.lockUnlock_button_blocked);
                break;
            case BOTH:
                setBackgroundResource(R.color.lockUnlock_button_both);
                break;
            default: break;
        }
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
    }

    public void addListener(){
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blockButtonClick();
            }
        });
    }

    private void blockButtonClick(){
        DataBaseTask dbt = new DataBaseTask();
        dbt.procedureParamZone = getZone();
        dbt.procedureParamLevel = getLevel();
        try {
            dbt.execute(DataBaseTask.BLOCK_BUTTON);
            setBlocked(dbt.get().get("P" + getZone() + "_" + getLevel()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}