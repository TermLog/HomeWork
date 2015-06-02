package com.alexzandr.myapplication.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.alexzandr.myapplication.DataBaseTask;
import com.alexzandr.myapplication.fragment.dialog.ErrorShowDialog;
import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.view.SectionButton;
import com.alexzandr.myapplication.view.ZoneLevelButton;

import java.util.HashMap;


public class LockUnlockActivity extends ActionBarActivity implements OnClickListener, ErrorShowDialog.OnShowErrors {

    private TableLayout mTable;
    private TableRow mFirstRow;
    private Button mRefreshButton;
    private ErrorShowDialog mErrorDialog;
//    private final Animation mScaleAnimationForButton = Singleton.getAnimation();
    private HashMap<Integer, HashMap<Integer, SectionButton>> mTableMap;

    private static int sRefreshCount;

    private final static String KEY_COUNT_OF_ZONES = "zoneCount";
    private final static String KEY_COUNT_OF_LEVELS = "levelCount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_unlock);

        mTable = (TableLayout) findViewById(R.id.lockUnlock_table);
        mFirstRow = (TableRow) findViewById(R.id.lockUnlock_firstRow);
        mRefreshButton = (Button) findViewById(R.id.lockUnlock_buttonRefresh);

        mTableMap = new HashMap<>();
        mErrorDialog = new ErrorShowDialog();
        mErrorDialog.setCancelable(false);
    }

    @Override
    protected void onStart(){
        super.onStart();
        if (mTable.getChildCount() <= 1) {
            createTable();
        }
    }

    @Override
    public void onClick(View view) {
        if (view instanceof SectionButton){
            sectionButtonClick((SectionButton) view);
        } else if (view instanceof ZoneLevelButton){
            zoneLevelClick((ZoneLevelButton) view);
        }
    }

    void createTable(){
        HashMap<String, Integer> resultMap = getQueryResult();

        if (resultMap != null) {
            int zonesCount = resultMap.get(KEY_COUNT_OF_ZONES);
            int levelsCount = resultMap.get(KEY_COUNT_OF_LEVELS);

            fillFirstRow(levelsCount);
            addNewRow(zonesCount, levelsCount, resultMap);
        }

    }

    private void fillFirstRow(int levelsCount) {
        for (int level = 1; level <= levelsCount; level++){
            mFirstRow.addView(new ZoneLevelButton(this, ZoneLevelButton.TYPE_LEVEL, level));
        }
    }

    private void addNewRow(int zonesCount, int levelsCount, HashMap<String, Integer> map) {
        for (int zone = 1; zone <= zonesCount; zone++) {
            TableRow newRow = new TableRow(this);
            mTableMap.put(zone, fillRow(zone, levelsCount, newRow, map));
            mTable.addView(newRow);
        }
    }

    private HashMap<Integer, SectionButton> fillRow(int zoneNumber, int levelsCount, TableRow row, HashMap<String, Integer> map){
        HashMap<Integer, SectionButton> resultHashMap = new HashMap<>();
        row.addView(new ZoneLevelButton(this, ZoneLevelButton.TYPE_ZONE, zoneNumber));

        for (int level = 1; level <= levelsCount; level++){
            int buttonBlockedType = map.get("P" + zoneNumber + "_" + level);
            SectionButton sectionButton = new SectionButton(this, zoneNumber, level, buttonBlockedType);
            row.addView(sectionButton);
            resultHashMap.put(level, sectionButton);
        }

        return resultHashMap;
    }

    public void onRefreshClick(View view){
        HashMap<String, Integer> resultMap = getQueryResult();
//        view.startAnimation(mScaleAnimationForButton);

        if (resultMap != null) {
            for (HashMap<Integer, SectionButton> buttonHashMap : mTableMap.values()) {
                for (SectionButton sectionButton : buttonHashMap.values()) {
                    String key = sectionButton.getStringForKey();
                    sectionButton.setBlockedType(resultMap.get(key));
                }
            }
        }
        mRefreshButton.setText("Ref_" + (++sRefreshCount));
    }

    private void sectionButtonClick(SectionButton button){
        String key = button.getStringForKey();
//        button.startAnimation(mScaleAnimationForButton);
        button.setBlockedType(getQueryResult(button).get(key));
    }

    private void zoneLevelClick(ZoneLevelButton button){
        HashMap<String, Integer> resultMap = getQueryResult(button);
//        button.startAnimation(mScaleAnimationForButton);

        if (button.getType() == ZoneLevelButton.TYPE_ZONE && resultMap != null){

            HashMap<Integer, SectionButton> buttonHashMap = mTableMap.get(button.getValue());
            for (SectionButton sectionButton : buttonHashMap.values()) {
                String key = sectionButton.getStringForKey();
                sectionButton.setBlockedType(resultMap.get(key));
            }

        }else if (resultMap != null) {
            for (HashMap<Integer, SectionButton> buttonHashMap : mTableMap.values()) {
                SectionButton sectionButton = buttonHashMap.get(button.getValue());
                String key = sectionButton.getStringForKey();
                sectionButton.setBlockedType(resultMap.get(key));
            }
        }
    }

    private HashMap<String, Integer> getQueryResult (){
        DataBaseTask task = new DataBaseTask();
        return executeQuery(task, DataBaseTask.GET_ALL_DATA);
    }


    private HashMap<String, Integer> getQueryResult (SectionButton btnSection){
        DataBaseTask task = new DataBaseTask();
        task.procedureParamZone = btnSection.getZone();
        task.procedureParamLevel = btnSection.getLevel();

        return executeQuery(task, DataBaseTask.SECTION_CHANGE);
    }

    private HashMap<String, Integer> getQueryResult (ZoneLevelButton btnZoneLevel){
        DataBaseTask task = new DataBaseTask();
        task.procedureParamType = btnZoneLevel.getType();
        task.procedureParamValue = btnZoneLevel.getValue();

        return executeQuery(task, DataBaseTask.ZONE_LEVEL_CHANGE);
    }

    private HashMap<String, Integer> executeQuery(DataBaseTask task, int queryType){
        try {
            task.execute(queryType);
            return task.get();
        } catch (Exception e) {
            showError(e.getMessage());
        }
        return null;
    }

    @Override
    public void showError(String errorText) {
        Bundle errorMassage = new Bundle();
        errorMassage.putString(ErrorShowDialog.KEY_FOR_ERROR, errorText);
        mErrorDialog.setArguments(errorMassage);
        mErrorDialog.show(getFragmentManager(), "ErrorDialog");
    }
}
