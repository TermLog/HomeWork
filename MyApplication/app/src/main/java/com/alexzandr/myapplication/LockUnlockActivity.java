package com.alexzandr.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import java.util.HashMap;


public class LockUnlockActivity extends ActionBarActivity implements OnClickListener {

    public TableLayout table;
    public TableRow firstRow;
    public Button refreshButton;

    private static int sRefreshCount;

    private final static String KEY_COUNT_OF_ZONES = "zoneCount";
    private final static String KEY_COUNT_OF_LEVELS = "levelCount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_unlock);
        table = (TableLayout) findViewById(R.id.lockUnlock_table);
        firstRow = (TableRow) findViewById(R.id.lockUnlock_firstRow);
        refreshButton = (Button) findViewById(R.id.lockUnlock_buttonRefresh);
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(table.getChildCount() <= 1) {
            createTable();
        }
    }

    @Override
    public void onClick(View view) {
        if(view instanceof BlockButton){
            blockButtonClick((BlockButton) view);
        } else if (view instanceof ZoneLevelButton){
            zoneLevelClick((ZoneLevelButton) view);
        }
    }

    public void createTable(){
        HashMap<String, Integer> mapForTable = null;
        DataBaseTask dbt = new DataBaseTask();
        try{
            dbt.execute(DataBaseTask.ALL_TABLE);
            mapForTable = dbt.get();
        } catch (Exception e){
            e.printStackTrace();
        }

        if(mapForTable != null) {
            int zone, level;
            int zoneCount = mapForTable.get(KEY_COUNT_OF_ZONES);
            int levelCount = mapForTable.get(KEY_COUNT_OF_LEVELS);

//            TableRow.LayoutParams rowLayoutParam = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
//            rowLayoutParam.setMargins(2, 2, 2, 2);

            for (level = 1; level <= levelCount; level++){
                firstRow.addView(new ZoneLevelButton(this, ZoneLevelButton.TYPE_LEVEL, level));
            }

            for (zone = 1; zone <= zoneCount; zone++) {
                TableRow row = new TableRow(this);
                row.addView(new ZoneLevelButton(this, ZoneLevelButton.TYPE_ZONE, zone));

                for (level = 1; level <= levelCount; level++) {
                    row.addView(new BlockButton(this, zone, level, mapForTable.get("P" + zone + "_" + level)));
                }

                table.addView(row);
            }
        }
    }

    public void onRefreshClick(View view){
        HashMap<String, Integer> refreshMap = null;
        DataBaseTask dbt = new DataBaseTask();
        try{
            dbt.execute(DataBaseTask.ALL_TABLE);
            refreshMap = dbt.get();
        } catch (Exception e){
            e.printStackTrace();
        }
        if (refreshMap != null) {
            int rowCount = table.getChildCount();
            for (int rowNumber = 0; rowNumber < rowCount; rowNumber++) {
                if (table.getChildAt(rowNumber) instanceof TableRow) {
                    TableRow row = (TableRow) table.getChildAt(rowNumber);
                    int buttonCount = row.getChildCount();
                    for (int buttonNumber = 0; buttonNumber < buttonCount; buttonNumber++) {
                        if (row.getChildAt(buttonNumber) instanceof BlockButton) {
                            BlockButton button = (BlockButton) row.getChildAt(buttonNumber);
                            button.setBlocked(refreshMap.get("P" + rowNumber + "_" + buttonNumber));
                        }
                    }

                }
            }
        }
        refreshButton.setText("Ref_" + (++sRefreshCount));
    }

    private void blockButtonClick(BlockButton button){
        DataBaseTask dbt = new DataBaseTask();
        dbt.procedureParamZone = button.getZone();
        dbt.procedureParamLevel = button.getLevel();
        try {
            dbt.execute(DataBaseTask.BLOCK_BUTTON);
            button.setBlocked(dbt.get().get(button.getStringForKey()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void zoneLevelClick(ZoneLevelButton button){
        DataBaseTask dbt = new DataBaseTask();
        HashMap<String, Integer> map = null;
        dbt.procedureParamType = button.getType();
        dbt.procedureParamValue = button.getValue();
        try {
            dbt.execute(DataBaseTask.ZONE_LEVEL);
            map = dbt.get();
        }catch (Exception e){
            e.printStackTrace();
        }

        if(button.getType() == ZoneLevelButton.TYPE_ZONE && map != null){

            changeButtonInRow((TableRow) button.getParent(), map, button);

        }else if(map != null) {
            try {
                int rowCount = table.getChildCount();
                for (int rowNumber = 0; rowNumber < rowCount; rowNumber++) {
                    if (table.getChildAt(rowNumber) instanceof TableRow) {
                        changeButtonInRow((TableRow) table.getChildAt(rowNumber), map, button);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void changeButtonInRow(TableRow parentRow, HashMap<String, Integer> map, ZoneLevelButton zoneLevelButton){
        int buttonCount = parentRow.getChildCount();
        for (int i = 0; i < buttonCount; i++){
            if (parentRow.getChildAt(i) instanceof BlockButton){
                BlockButton button = (BlockButton)parentRow.getChildAt(i);
                if (button.getLevel() == zoneLevelButton.getValue() || zoneLevelButton.getType() == ZoneLevelButton.TYPE_ZONE) {
                    try {
                        button.setBlocked(map.get(button.getStringForKey()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
