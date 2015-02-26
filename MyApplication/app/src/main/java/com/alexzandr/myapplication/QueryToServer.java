package com.alexzandr.myapplication;

import java.net.Socket;
import java.sql.*;
import java.util.HashMap;

/**
 * Created by AlexZandR on 21.02.2015.
 */
public class QueryToServer {
    private String mParam1;
    private String mParam2;
    private final static int HOME_SERVER = R.string.serverName_home;
    private final static int WORK_SERVER = R.string.serverName_work;
//    private final static int OTHER_SERVER = R.string.serverName_other;
    private final static String HOME_URL = "192.168.1.104";
    private final static String WORK_URL = "10.100.6.15";
//    private final static String OTHER_URL = "jdbc:jtds:sqlserver://192.168.1.104:1433/PRD1";
    private final static String JTDS_CLASS_NAME = "net.sourceforge.jtds.jdbc.Driver";
    private String mConnectionUrl;
    private Connection mCN = null;
    private Statement mST = null;
    private ResultSet mRS = null;

    public QueryToServer(int serverId, String param1, String param2){
        switch (serverId){
            case HOME_SERVER:
                mConnectionUrl = "jdbc:jtds:sqlserver://" + HOME_URL + ":1433/PRD1";
                break;
            case WORK_SERVER:
                mConnectionUrl = "jdbc:jtds:sqlserver://" + WORK_URL + ":1433/PRD1";
                break;
            default: break;
        }
        mParam1 = param1;
        mParam2 = param2;
    }

    public HashMap<String, Integer> getAllData(){
        String queryText = "exec prd1.dbo.proc_Alex_GetAllData";
        return getResult(queryText);
    }

    public HashMap<String, Integer> changeSection(int zone, int level){
        String queryText = "exec prd1.dbo.proc_Alex_ChangeSection @zone = '" + zone + "', @level = '" + level + "'";
        return getResult(queryText);
    }

    public HashMap<String, Integer> changeZoneLevel(int type, int value){
        String queryText = "exec prd1.dbo.proc_Alex_ChangeZoneLevel @type = '" + type + "', @value = '" + value + "'";
        return getResult(queryText);
    }

    public HashMap<String, Integer> updateDoc(String docList){
        String queryText = "exec prd1.dbo.proc_Alex_UpdateDocument @rec = '" + docList + "'";
        return getResult(queryText);
    }

    public HashMap<String, Integer> deleteDoc(String docList){
        String queryText = "exec prd1.dbo.proc_Alex_DeleteQuote @rec = '" + docList + "'";
        return getResult(queryText);
    }

    public boolean checkConnection(){
        try{
            Class.forName(JTDS_CLASS_NAME);
            mCN = DriverManager.getConnection(mConnectionUrl, mParam1, mParam2);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (mRS != null) try { mRS.close(); } catch(Exception e) {e.printStackTrace(); }
            if (mST != null) try { mST.close(); } catch(Exception e) {e.printStackTrace(); }
            if (mCN != null) try { mCN.close(); } catch(Exception e) {e.printStackTrace(); }
        }
        return false;
    }

    private  HashMap<String, Integer> getResult(String text){
        HashMap<String, Integer> resultMap = new HashMap<>();
        try{
            Class.forName(JTDS_CLASS_NAME);
            mCN = DriverManager.getConnection(mConnectionUrl, mParam1, mParam2);
            mST = mCN.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            mRS = mST.executeQuery(text);
            while(mRS.next()){
                resultMap.put(mRS.getString("mapKey"), mRS.getInt("mapValue"));
            }
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        } finally {
            if (mRS != null) try { mRS.close(); } catch(Exception e) {e.printStackTrace(); }
            if (mST != null) try { mST.close(); } catch(Exception e) {e.printStackTrace(); }
            if (mCN != null) try { mCN.close(); } catch(Exception e) {e.printStackTrace(); }
        }
    }
}
