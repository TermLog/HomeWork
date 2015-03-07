package com.alexzandr.myapplication;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.*;
import java.util.HashMap;

/**
 * Created by AlexZandR on 21.02.2015.
 */
public class QueryToServer {
    private String mParam1;
    private String mParam2;
    private String mConnectionUrl;
    private String mServerIp;
    private Connection mCN = null;
    private Statement mST = null;
    private ResultSet mRS = null;

    private final static String JTDS_CLASS_NAME = "net.sourceforge.jtds.jdbc.Driver";
    private final static String MAP_KEY_COLUMN = "mapKey";
    private final static String MAP_VALUE_COLUMN = "mapValue";
    private final static int PORT = 1433;
    private final static int CONNECTION_TIMEOUT_MS = 5 * 1000;

    public QueryToServer(String serverIP, String param1, String param2){
        mServerIp = serverIP;
        mConnectionUrl = "jdbc:jtds:sqlserver://" + mServerIp + ":1433/PRD1";
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
        if (!isReachableServer()) {
            return false;
        }
        try{
            Class.forName(JTDS_CLASS_NAME);
            mCN = DriverManager.getConnection(mConnectionUrl, mParam1, mParam2);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            LoginActivity.errorType = LoginActivity.ERROR_WRONG_LOGIN_PASSWORD;
        } finally {
            if (mRS != null) try { mRS.close(); } catch(Exception e) {e.printStackTrace(); }
            if (mST != null) try { mST.close(); } catch(Exception e) {e.printStackTrace(); }
            if (mCN != null) try { mCN.close(); } catch(Exception e) {e.printStackTrace(); }
        }
        return false;
    }

    private boolean isReachableServer(){
        Socket socket = new Socket();
        SocketAddress address = new InetSocketAddress(mServerIp, PORT);
        try{
            socket.connect(address, CONNECTION_TIMEOUT_MS);
            if (socket.isConnected())
                return true;
        }catch (Exception e){
            e.printStackTrace();
            LoginActivity.errorType = LoginActivity.ERROR_WRONG_SERVER;
        }finally {
            if (socket != null) try{ socket.close(); }catch (Exception e){ e.printStackTrace(); }
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
                resultMap.put(mRS.getString(MAP_KEY_COLUMN), mRS.getInt(MAP_VALUE_COLUMN));
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
