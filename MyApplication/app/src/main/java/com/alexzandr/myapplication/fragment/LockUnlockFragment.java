package com.alexzandr.myapplication.fragment;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.alexzandr.myapplication.service.SqlQueryIntentService;
import com.alexzandr.myapplication.sql.DataBaseTask;
import com.alexzandr.myapplication.adapter.LockUnlockAdapter;
import com.alexzandr.myapplication.R;
import com.alexzandr.myapplication.application.Singleton;
import com.alexzandr.myapplication.handler.AdapterItemHandler;
import com.alexzandr.myapplication.handler.LevelHandler;
import com.alexzandr.myapplication.handler.SectionHandler;
import com.alexzandr.myapplication.handler.ZoneHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class LockUnlockFragment extends WarehouseFragment {

    private int mLevelsCount;
    private int mZonesCount;
    private boolean mStartOrientationIsPort;
    private static int sNumberOfChanges;
    private LockUnlockAdapter mAdapterSection;
    private LockUnlockAdapter mAdapterLevel;
    private ArrayList<AdapterItemHandler> mSections;
    private ArrayList<AdapterItemHandler> mLevels;
    private GridView mGridViewLevel;
    private GridView mGridViewSection;

    public SectionHandler mItemHandler;
    public boolean mIsZoneChanged;
    public int mZoneLevelIndex;

    private final static String KEY_COUNT_OF_ZONES = "zoneCount";
    private final static String KEY_COUNT_OF_LEVELS = "levelCount";
    private final static int ID_FOR_NOTIFICATION = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isPortOrientation()) {
            mStartOrientationIsPort = true;
        } else {
            mStartOrientationIsPort = false;
        }

        mReceiverSuccess = new LockBroadcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lock_unlock, container, false);
        mGridViewLevel = (GridView) view.findViewById(R.id.lockUnlock_test_firstRow);
        mGridViewSection = (GridView) view.findViewById(R.id.lockUnlock_test_sections);

//        LockTask task = new LockTask();
//        task.execute(DataBaseTask.GET_ALL_DATA);

        if (isPortOrientation()) {
            mActivity.setTitle(R.string.title_activity_lock_unlock);
        }

        return view;
    }

    @Override
    public void onStart() {
        System.out.println("START LockUnlockFragment");
        super.onStart();

    }

    @Override
    public void onResume() {
        System.out.println("RESUME LockUnlockFragment");
        super.onResume();
        onAdapterChanged();
        refresh();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (isPortOrientation() == mStartOrientationIsPort) {
            System.out.println("NUMBER OF CHANGES IS " + sNumberOfChanges);
            showNotification(sNumberOfChanges);
            sNumberOfChanges = 0;
        }
    }

    void createTable(HashMap<String, Integer> map){

        mZonesCount = map.get(KEY_COUNT_OF_ZONES);
        mLevelsCount = map.get(KEY_COUNT_OF_LEVELS);
        mSections = createSections(map, mSections);

        fillFirstRow(mLevelsCount);

        if (mAdapterSection == null) {
            mAdapterSection = new LockUnlockAdapter(mActivity, mSections);
            mGridViewSection.setAdapter(mAdapterSection);
            mGridViewSection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AdapterItemHandler handler = mAdapterSection.getHandler(position);
                    if (handler.isAvailability()) {
                        view.startAnimation(Singleton.getAnimation());
                        switch (handler.getType()) {
                            case AdapterItemHandler.ZONE_BUTTON:
                                zoneClick((ZoneHandler)handler, position);
                                break;
                            case AdapterItemHandler.SECTION_BUTTON:
                                sectionClick((SectionHandler)handler);
                                break;
                        }
                    }
                }
            });
        }
        mGridViewSection.setNumColumns(mLevelsCount + 1);
        mAdapterSection.notifyDataSetChanged();
    }

    private ArrayList<AdapterItemHandler> createSections(HashMap<String, Integer> map, ArrayList<AdapterItemHandler> handlers) {
        if (handlers == null) {
            handlers = new ArrayList<>();
        } else {
            handlers.clear();
        }
        for (int zone = 1; zone <= mZonesCount; zone++) {
            handlers.add(new ZoneHandler(zone));
            for (int level = 1; level <= mLevelsCount; level++) {
                SectionHandler handler = new SectionHandler(zone, level);
                try {
                    handler.setBlockedType(map.get(handler.getStringForKey()));
                } catch (NullPointerException nullException){
                    handler.setAvailability(false);
                }
                handlers.add(handler);
            }
        }
        return handlers;
    }

    private void fillFirstRow(int levelsCount) {
        if (mLevels == null){
            mLevels = new ArrayList<>();
        }

        mLevels.clear();
        mLevels.add(new LevelHandler(LevelHandler.REFRESH_BUTTON));
        for (int level = 1; level <= levelsCount; level++){
            mLevels.add(new LevelHandler(level));
        }

        if (mAdapterLevel == null){
            mAdapterLevel = new LockUnlockAdapter(mActivity, mLevels);
            mGridViewLevel.setAdapter(mAdapterLevel);
            mGridViewLevel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LevelHandler handler = (LevelHandler) mAdapterLevel.getHandler(position);
                    if (handler.isAvailability()) {
                        view.startAnimation(Singleton.getAnimation());
                        if (handler.getLevel() == LevelHandler.REFRESH_BUTTON) {
                            refresh();
                        } else {
                            levelClick(handler.getLevel());
                        }
                    }
                }
            });
        }

        mAdapterLevel.notifyDataSetChanged();
        mGridViewLevel.setNumColumns(mLevelsCount + 1);
    }

    public void refresh(){
//        LockTask task = new LockTask();
//        task.execute(DataBaseTask.GET_ALL_DATA);
        System.out.println("REFRESH_REFRESH_REFRESH_REFRESH");
//        SqlQueryIntentService.executeQuery(
//                getActivity(),
//                SqlQueryIntentService.GET_ALL_DATA
//        );
        Intent intent = new Intent(getActivity(), SqlQueryIntentService.class);
        intent.putExtra(SqlQueryIntentService.EXTRA_QUERY_TYPE, SqlQueryIntentService.GET_ALL_DATA);
        getActivity().startService(intent);
        mProgressDialog.show();
    }

    private void sectionClick(SectionHandler handler){
        mItemHandler = handler;
//        LockTask task = new LockTask(handler);
//        task.procedureParamZone = handler.getZone();
//        task.procedureParamLevel = handler.getLevel();
//        task.execute(DataBaseTask.SECTION_CHANGE);

        SqlQueryIntentService.executeQuery(
                mActivity,
                SqlQueryIntentService.SECTION_CHANGE,
                handler.getZone(),
                handler.getLevel()
        );
        mProgressDialog.show();
    }

    private void sectionChange(SectionHandler handler, HashMap<String, Integer> map){
        String key = handler.getStringForKey();
        handler.setBlockedType(map.get(key));
        mAdapterSection.notifyDataSetChanged();
        sNumberOfChanges++;
    }

    private void zoneClick(ZoneHandler handler, int position){
        mIsZoneChanged = true;
        mZoneLevelIndex = position;
//        LockTask task = new LockTask(handler, position);
//        task.procedureParamType = handler.getType();
//        task.procedureParamValue = handler.getZone();
//        task.execute(DataBaseTask.ZONE_LEVEL_CHANGE);

        SqlQueryIntentService.executeQuery(
                mActivity,
                SqlQueryIntentService.ZONE_LEVEL_CHANGE,
                handler.getType(),
                handler.getZone()
        );
        mProgressDialog.show();
    }

    private void zoneChange(int itemIndex, HashMap<String, Integer> map){
        for (int level = 1; level <= mLevelsCount; level++) {
            SectionHandler handler = (SectionHandler) mSections.get(itemIndex + level);
            String key = handler.getStringForKey();
            try {
                handler.setBlockedType(map.get(key));
                handler.setAvailability(true);
            } catch (NullPointerException e){
                handler.setAvailability(false);
            }
        }
        mAdapterSection.notifyDataSetChanged();
        sNumberOfChanges++;
    }

    private void levelClick(int level){
        mIsZoneChanged = false;
        mZoneLevelIndex = level;
//        LockTask task = new LockTask(level);
//        task.procedureParamType = AdapterItemHandler.LEVEL_BUTTON;
//        task.procedureParamValue = level;
//        task.execute(DataBaseTask.ZONE_LEVEL_CHANGE);

        SqlQueryIntentService.executeQuery(
                mActivity,
                SqlQueryIntentService.ZONE_LEVEL_CHANGE,
                AdapterItemHandler.LEVEL_BUTTON,
                level
        );
        mProgressDialog.show();
    }

    private void levelChange(int level, HashMap<String, Integer> map){
        for (int zone = 0; zone < mZonesCount; zone++) {
            SectionHandler handler = (SectionHandler) mSections.get(level + zone * (mLevelsCount + 1));
            String key = handler.getStringForKey();
            try {
                handler.setBlockedType(map.get(key));
                handler.setAvailability(true);
            } catch (NullPointerException e){
                handler.setAvailability(false);
            }
        }
        mAdapterSection.notifyDataSetChanged();
        sNumberOfChanges++;
    }

    @Override
    public void onAdapterChanged() {
        if (mAdapterLevel != null) {
            mAdapterLevel.notifyDataSetChanged();
        }
        if (mAdapterSection != null) {
            mAdapterSection.notifyDataSetChanged();
        }
    }

    private void showNotification(int number) {
        String messageText;
        if (number > 0) {
            messageText = getString(R.string.notification_text_prefix) + number;
        } else {
            messageText = getString(R.string.notification_no_changes);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mActivity)
                .setSmallIcon(R.drawable.warehouse)
                .setContentTitle(getString(R.string.title_notification))
                .setContentText(messageText)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(ID_FOR_NOTIFICATION /* ID of notification */, notificationBuilder.build());
    }

    class LockBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                System.out.println("CATCH SUCCESS");
                mProgressDialog.dismiss();

                int queryType = intent.getIntExtra(SqlQueryIntentService.EXTRA_QUERY_TYPE,
                        SqlQueryIntentService.GET_ALL_DATA);

                HashMap<String, Integer> resultMap =
                        (HashMap<String, Integer>) intent.getSerializableExtra(SqlQueryIntentService.EXTRA_RESULT_MAP);

                if (mItemHandler != null)
                    System.out.println("LEVEL IS - " + mItemHandler.getLevel());

                switch (queryType) {
                    case SqlQueryIntentService.GET_ALL_DATA:
                        createTable(resultMap);
                        break;
                    case SqlQueryIntentService.SECTION_CHANGE:
                        sectionChange(mItemHandler, resultMap);
                        break;
                    case SqlQueryIntentService.ZONE_LEVEL_CHANGE:
                        if (mIsZoneChanged) {
                            zoneChange(mZoneLevelIndex, resultMap);
                        } else {
                            levelChange(mZoneLevelIndex, resultMap);
                        }
                    default:
                        break;
                }
            } catch (Exception e) {
                System.out.println("ERROR_ERROR_ERROR_ERROR_ERROR_ERROR_ERROR");
                e.printStackTrace();
            }
        }
    }

    private class LockTask extends WarehouseTask {

        final static int NO_INDEX = 0;
        final static int CREATE_TABLE = 0;
        final static int SECTION_CHANGE = 1;
        final static int LEVEL_CHANGE = 2;
        final static int ZONE_CHANGE = 3;
        AdapterItemHandler itemHandler;
        int itemIndex;
        int queryType;

        LockTask(){
            this(null, NO_INDEX, CREATE_TABLE);
        }

        LockTask(int levelNumber) {
            this(null, levelNumber, LEVEL_CHANGE);
        }

        LockTask(AdapterItemHandler itemHandler){
            this(itemHandler, NO_INDEX, SECTION_CHANGE);
        }

        LockTask(AdapterItemHandler itemHandler, int index){
            this(itemHandler, index, ZONE_CHANGE);
        }

        LockTask(AdapterItemHandler itemHandler, int index, int type){
            this.itemHandler = itemHandler;
            this.itemIndex = index;
            this.queryType = type;
        }

        @Override
        void processResult(HashMap<String, Integer> result){
            switch (queryType){
                case CREATE_TABLE:
                    createTable(result);
                    break;
                case SECTION_CHANGE:
                    sectionChange((SectionHandler)itemHandler, result);
                    break;
                case LEVEL_CHANGE:
                    levelChange(itemIndex, result);
                    break;
                case ZONE_CHANGE:
                    zoneChange(itemIndex, result);
                    break;
                default: break;
            }
        }
    }
}
