package com.alexzandr.myapplication.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.alexzandr.myapplication.ButtonHandler;
import com.alexzandr.myapplication.DataBaseTask;
import com.alexzandr.myapplication.LockUnlockAdapter;
import com.alexzandr.myapplication.Singleton;
import com.alexzandr.myapplication.fragment.ErrorShowDialog;
import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.view.SectionButton;
import com.alexzandr.myapplication.view.ZoneLevelButton;

import java.util.ArrayList;
import java.util.HashMap;


public class LockUnlockActivityTest extends ActionBarActivity implements OnClickListener, ErrorShowDialog.OnShowErrors {

    private int mLevelsCount;
    private int mZonesCount;
    private LinearLayout mFirstRow;
    private LockUnlockAdapter mAdapter;
    private ArrayList<ButtonHandler> mArrayForAdapter;
    private GridView mGridView;
    private Button mRefreshButton;
    private ErrorShowDialog mErrorDialog;
    private HashMap<Integer, HashMap<Integer, SectionButton>> mTableMap;

    private static int sRefreshCount;

    private final static String KEY_COUNT_OF_ZONES = "zoneCount";
    private final static String KEY_COUNT_OF_LEVELS = "levelCount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_unlock_test);

        mFirstRow = (LinearLayout) findViewById(R.id.lockUnlock_test_firstRow);
        mRefreshButton = (Button) findViewById(R.id.lockUnlock_test_buttonRefresh);
        mGridView = (GridView) findViewById(R.id.lockUnlock_test_list_of_button);

        mTableMap = new HashMap<>();
        mErrorDialog = new ErrorShowDialog();
        mErrorDialog.setCancelable(false);

        createTable();
    }

    @Override
    public void onClick(View view) {
//        if (view instanceof SectionButton){
//            sectionButtonClick((SectionButton) view);
//        } else if (view instanceof ZoneLevelButton){
            levelClick((ZoneLevelButton) view);
//        }
    }

    void createTable(){
        HashMap<String, Integer> resultMap = getQueryResult();

        if (resultMap != null) {
            mZonesCount = resultMap.get(KEY_COUNT_OF_ZONES);
            mLevelsCount = resultMap.get(KEY_COUNT_OF_LEVELS);
            mArrayForAdapter = createArray(resultMap);

            fillFirstRow(mLevelsCount);

            mAdapter = new LockUnlockAdapter(this, mArrayForAdapter);
            mGridView.setAdapter(mAdapter);
            mGridView.setNumColumns(mLevelsCount + 1);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    System.out.println("Position is " + position);
                    view.startAnimation(Singleton.getAnimation());
                    ButtonHandler handler = mAdapter.getHandler(position);
                    switch (handler.getType()){
                        case ButtonHandler.ZONE_BUTTON:
                            zoneClick(handler, position);
                            break;
                        case ButtonHandler.SECTION_BUTTON:
                            sectionButtonClick(handler);
                            break;
                    }
                }
            });
        }

    }

    private ArrayList<ButtonHandler> createArray(HashMap<String, Integer> map) {
        ArrayList<ButtonHandler> result = new ArrayList<>();
        for (int zone = 1; zone <= mZonesCount; zone++) {
            result.add(new ButtonHandler(zone));
            for (int level = 1; level <= mLevelsCount; level++) {
                ButtonHandler handler = new ButtonHandler(zone, level);
                handler.setBlockedType(map.get(handler.getStringForKey()));
                result.add(handler);
            }
        }
        return result;
    }

    private void fillFirstRow(int levelsCount) {
        for (int level = 1; level <= levelsCount; level++){
            mFirstRow.addView(new ZoneLevelButton(this, ZoneLevelButton.TYPE_LEVEL, level));
        }
    }

    public void onRefreshClick(View view){
        HashMap<String, Integer> resultMap = getQueryResult();

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

    private void sectionButtonClick(ButtonHandler handler){
        HashMap<String, Integer> resultMap = getQueryResult(handler, false);
        if (resultMap != null) {
            String key = handler.getStringForKey();
            handler.setBlockedType(resultMap.get(key));
        }
        mAdapter.notifyDataSetChanged();
    }

    private void levelClick(ZoneLevelButton levelButton){
        HashMap<String, Integer> resultMap = getQueryResult(levelButton);

        if (resultMap != null){
            for (int zone = 0; zone < mZonesCount; zone++) {
                ButtonHandler button = mArrayForAdapter.get(levelButton.getValue() + zone * (mLevelsCount + 1));
                String key = button.getStringForKey();
                button.setBlockedType(resultMap.get(key));
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void zoneClick(ButtonHandler handler, int itemIndex){
        HashMap<String, Integer> resultMap = getQueryResult(handler, true);
        if (resultMap != null) {
            for (int level = 1; level <= mLevelsCount; level++) {
                ButtonHandler button = mArrayForAdapter.get(itemIndex + level);
                String key = button.getStringForKey();
                button.setBlockedType(resultMap.get(key));
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private HashMap<String, Integer> getQueryResult (){
        DataBaseTask task = new DataBaseTask();
        return executeQuery(task, DataBaseTask.GET_ALL_DATA);
    }


    private HashMap<String, Integer> getQueryResult (ButtonHandler btn, boolean isZone){

        if (isZone){
            DataBaseTask task = new DataBaseTask();
            task.procedureParamType = btn.getType();
            task.procedureParamValue = btn.getZone();

            return executeQuery(task, DataBaseTask.ZONE_LEVEL_CHANGE);

        } else {
            DataBaseTask task = new DataBaseTask();
            task.procedureParamZone = btn.getZone();
            task.procedureParamLevel = btn.getLevel();

            return executeQuery(task, DataBaseTask.SECTION_CHANGE);
        }
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
