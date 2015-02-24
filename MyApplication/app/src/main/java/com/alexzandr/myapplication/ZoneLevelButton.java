package com.alexzandr.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import java.util.HashMap;

/**
 * Created by AlexZandR on 22.02.2015.
 */
public class ZoneLevelButton extends Button {
    public final static int TYPE_ZONE = 1;
    public final static int TYPE_LEVEL = 2;
    private int mType;
    private int mValue;

    public ZoneLevelButton(Context context){
        super(context);
    }
    public ZoneLevelButton(Context context, AttributeSet attSet){
        super(context, attSet);
    }
    public ZoneLevelButton(Context context, AttributeSet attSet, int defStyleAttr){
        super(context, attSet, defStyleAttr);
    }

    ZoneLevelButton(Context context, int type, int value){
        super(context);
        setType(type);
        setValue(value);
        addListener();
        setTextColor(getResources().getColor(R.color.text_white));
    }

    public void setType(int type){
        mType = type;
    }
    public void setValue(int value){
        mValue = value;
    }
    public int getType(){
        return mType;
    }
    public int getValue(){
        return mValue;
    }
    public void addListener(){
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoneLevelClick();
            }
        });
    }

    private void zoneLevelClick(){
        DataBaseTask dbt = new DataBaseTask();
        HashMap<String, Integer> map = null;
        dbt.procedureParamType = getType();
        dbt.procedureParamValue = getValue();
        try {
            dbt.execute(DataBaseTask.ZONE_LEVEL);
            map = dbt.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(getType() == TYPE_ZONE && map != null){

            changeButtonInRow((TableRow) getParent(), map);

        }else if(map != null) {
             TableLayout parentTable = (TableLayout) getParent().getParent();
            try {
                int rowCount = parentTable.getChildCount();
                for (int rowNumber = 0; rowNumber < rowCount; rowNumber++) {
                    if (parentTable.getChildAt(rowNumber) instanceof TableRow) {
                        changeButtonInRow((TableRow) parentTable.getChildAt(rowNumber), map);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void changeButtonInRow(TableRow parentRow, HashMap<String, Integer> map){
        int buttonCount = parentRow.getChildCount();
        for (int i = 0; i < buttonCount; i++){
            if (parentRow.getChildAt(i) instanceof BlockButton){
                BlockButton button = (BlockButton)parentRow.getChildAt(i);
                try {
                    button.setBlocked(map.get("P" + button.getZone() + "_" + button.getLevel()));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
