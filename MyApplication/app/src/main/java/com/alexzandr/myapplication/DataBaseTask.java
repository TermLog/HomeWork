package com.alexzandr.myapplication;

import android.os.AsyncTask;

import java.util.HashMap;

/**
 * Created by AlexZandR on 23.02.2015.
 */
class DataBaseTask extends AsyncTask<Integer, Void, HashMap<String, Integer>> {
    final static int GET_ALL_DATA = 1;
    final static int ZONE_LEVEL_CHANGE = 2;
    final static int SECTION_CHANGE = 3;
    final static int CHECK_CONNECTION = 4;
    final static int UPDATE_IN_DOC = 5;
    final static int DELETE_IN_DOC = 6;
    public int procedureParamType;
    public int procedureParamValue;
    public int procedureParamZone;
    public int procedureParamLevel;
    public String procedureParamDocList;
    public CheckConnectionException exception = null;

    @Override
    protected HashMap<String, Integer> doInBackground(Integer... params) {
        int intParams = params[0];
        QueryToServer serverData = Singleton.getQueryToServer();
        HashMap<String, Integer> mapResult = null;
        try {
            switch (intParams){
                case GET_ALL_DATA:
                    mapResult = serverData.getAllData();
                    break;
                case ZONE_LEVEL_CHANGE:
                    mapResult = serverData.changeZoneLevel(procedureParamType, procedureParamValue);
                    break;
                case SECTION_CHANGE:
                    mapResult = serverData.changeSection(procedureParamZone, procedureParamLevel);
                    break;
                case UPDATE_IN_DOC:
                    mapResult = serverData.updateDoc(procedureParamDocList);
                    break;
                case DELETE_IN_DOC:
                    mapResult = serverData.deleteDoc(procedureParamDocList);
                    break;
                case CHECK_CONNECTION:
                    mapResult = serverData.checkConnection();
                    break;
                default: break;
            }
        } catch (CheckConnectionException e) {
            exception = e;
        }
        return mapResult;
    }
}
