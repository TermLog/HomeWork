package com.alexzandr.myapplication;

import android.os.AsyncTask;

import java.util.HashMap;

/**
 * Created by AlexZandR on 23.02.2015.
 */
public class DataBaseTask extends AsyncTask<Integer, Void, HashMap<String, Integer>> {
    final static int ALL_TABLE = 1;
    final static int ZONE_LEVEL = 2;
    final static int BLOCK_BUTTON = 3;
    final static int CHECK = 4;
    final static int UPDATE_DOC = 5;
    final static int DELETE_DOC = 6;
    public int procedureParamType;
    public int procedureParamValue;
    public int procedureParamZone;
    public int procedureParamLevel;
    public String procedureParamDocList;

    @Override
    protected HashMap<String, Integer> doInBackground(Integer... params) {
        int intParams = params[0];
        QueryToServer serverData = LoginActivity.db;
        HashMap<String, Integer> mapResult = null;
        switch (intParams){
            case ALL_TABLE:
                mapResult = serverData.getAllData();
                break;
            case ZONE_LEVEL:
                mapResult = serverData.changeZoneLevel(procedureParamType, procedureParamValue);
                break;
            case BLOCK_BUTTON:
                mapResult = serverData.changeSection(procedureParamZone, procedureParamLevel);
                break;
            case UPDATE_DOC:
                mapResult = serverData.updateDoc(procedureParamDocList);
                break;
            case DELETE_DOC:
                mapResult = serverData.deleteDoc(procedureParamDocList);
                break;
            case CHECK:
                if (serverData.checkConnection()) {
                    mapResult = new HashMap<>();
                }
                break;
            default: break;
        }
        return mapResult;
    }
}
