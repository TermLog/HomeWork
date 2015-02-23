package com.alexzandr.myapplication;

import java.sql.*;
import java.util.HashMap;

/**
 * Created by AlexZandR on 21.02.2015.
 */
public class QueryToServer {
    private final static String PARAM1 = "sa";
    private final static String PARAM2 = "sql";
    private final static String CONNECTION_URL = "jdbc:jtds:sqlserver://192.168.1.104:1433/PRD1";
    private final static String JTDS_CLASS_NAME = "net.sourceforge.jtds.jdbc.Driver";
    private Connection cn = null;
    private Statement st = null;
    private ResultSet rs = null;

    public HashMap<String, Integer> getAllData(){
        String queryText = "exec prd1.dbo.proc_Alex_GetAllData";
        HashMap<String, Integer> resultMap = new HashMap<>();
        try{
            Class.forName(JTDS_CLASS_NAME);
            cn = DriverManager.getConnection(CONNECTION_URL, PARAM1, PARAM2);
            st = cn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            rs = st.executeQuery(queryText);
            while(rs.next()){
                resultMap.put(rs.getString("mapKey"), rs.getInt("mapValue"));
            }
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        } finally {
            if (rs != null) try { rs.close(); } catch(Exception e) {e.printStackTrace(); }
            if (st != null) try { st.close(); } catch(Exception e) {e.printStackTrace(); }
            if (cn != null) try { cn.close(); } catch(Exception e) {e.printStackTrace(); }
        }
    }
    public HashMap<String, Integer> changeSection(int zone, int level){
        String queryText = "exec prd1.dbo.proc_Alex_ChangeSection @zone = '" + zone + "', @level = '" + level + "'";
        HashMap<String, Integer> resultMap = new HashMap<>();
        try{
            Class.forName(JTDS_CLASS_NAME);
            cn = DriverManager.getConnection(CONNECTION_URL, PARAM1, PARAM2);
            st = cn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            rs = st.executeQuery(queryText);
            while(rs.next()){
                resultMap.put(rs.getString("mapKey"), rs.getInt("mapValue"));
            }
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        } finally {
            if (rs != null) try { rs.close(); } catch(Exception e) {e.printStackTrace(); }
            if (st != null) try { st.close(); } catch(Exception e) {e.printStackTrace(); }
            if (cn != null) try { cn.close(); } catch(Exception e) {e.printStackTrace(); }
        }
    }
    public HashMap<String, Integer> changeZoneLevel(int type, int value){
        String queryText = "exec prd1.dbo.proc_Alex_ChangeZoneLevel @type = '" + type + "', @value = '" + value + "'";
        HashMap<String, Integer> resultMap = new HashMap<>();
        try{
            Class.forName(JTDS_CLASS_NAME);
            cn = DriverManager.getConnection(CONNECTION_URL, PARAM1, PARAM2);
            st = cn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            rs = st.executeQuery(queryText);
            while(rs.next()){
                resultMap.put(rs.getString("mapKey"), rs.getInt("mapValue"));
            }
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        } finally {
            if (rs != null) try { rs.close(); } catch(Exception e) {e.printStackTrace(); }
            if (st != null) try { st.close(); } catch(Exception e) {e.printStackTrace(); }
            if (cn != null) try { cn.close(); } catch(Exception e) {e.printStackTrace(); }
        }
    }
}
