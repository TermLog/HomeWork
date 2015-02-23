package com.alexzandr.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.HashMap;


public class LockUnlockActivity extends ActionBarActivity {

    public TableLayout table;
    public TableRow firstRow;
    public Button refreshButton;
    private static int refreshCount;

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
            int zoneCount = mapForTable.get("zoneCount");
            int levelCount = mapForTable.get("levelCount");

            TableRow.LayoutParams rowLayoutParam = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            rowLayoutParam.setMargins(2, 2, 2, 2);


            for (level = 1; level <= levelCount; level++){
                ZoneLevelButton buttonLevel = new ZoneLevelButton(this, ZoneLevelButton.TYPE_LEVEL, level);
                buttonLevel.setLayoutParams(rowLayoutParam);
                buttonLevel.setText("LvL " + level);
                buttonLevel.setBackgroundResource(R.color.lockUnlock_button_zoneAndLevel);
                buttonLevel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                firstRow.addView(buttonLevel);
            }

            for (zone = 1; zone <= zoneCount; zone++) {

                String zoneName = "00" + zone;
                zoneName = "P" + zoneName.substring(zoneName.length() - 2, zoneName.length());

                ZoneLevelButton buttonZone = new ZoneLevelButton(this, ZoneLevelButton.TYPE_ZONE, zone);
                buttonZone.setLayoutParams(rowLayoutParam);
                buttonZone.setText("Zone " + zoneName);
                buttonZone.setBackgroundResource(R.color.lockUnlock_button_zoneAndLevel);
                buttonZone.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

                TableRow row = new TableRow(this);
                row.setLayoutParams(rowLayoutParam);
                row.addView(buttonZone);

                for (level = 1; level <= levelCount; level++) {
                    BlockButton button = new BlockButton(this, zone, level, mapForTable.get("P" + zone + "_" + level));
                    button.setLayoutParams(rowLayoutParam);
                    button.setText(zoneName + ", " + level);
                    row.addView(button);
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
        refreshButton.setText("Ref_" + (++refreshCount));

    }
}
