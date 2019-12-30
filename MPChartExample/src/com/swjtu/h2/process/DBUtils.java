package com.swjtu.h2.process;

import android.util.Log;

import com.swjtu.h2.custom.Machine_h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class DBUtils {
    private static final String TAG = "DBUtils";
    private static Connection getConnection(String dbName) {
        Connection conn = null;
        try {
            Log.d(TAG, " test1");
            Class.forName("com.mysql.jdbc.Driver"); //加载驱动
            String ip = "192.168.31.75";
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + ip + ":3306/" + dbName,
                    "root", "123456");
        } catch (SQLException ex) {
            Log.d(TAG, " test2");
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Log.d(TAG, " test3");
            ex.printStackTrace();
        }
        return conn;
    }
    public static HashMap<String, String> getUserInfoByName(String name,String pwd) {
        HashMap<String, String> map = new HashMap<>();
        Connection conn = getConnection("h2_swjtu");
        if(conn==null){
            Log.d(TAG, " not connet");
        }
        try {
            Statement st = conn.createStatement();
            String sql = "select * from swjtuss where name = '" + name + "' and password = '"+ pwd +"'";
            ResultSet res = st.executeQuery(sql);
            if (res == null) {
                return null;
            } else {
                int cnt = res.getMetaData().getColumnCount();
                res.next();
                for (int i = 1; i <= cnt; ++i) {
                    String field = res.getMetaData().getColumnName(i);
                    map.put(field, res.getString(field));
                }
                conn.close();
                st.close();
                res.close();
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据操作异常");
            return null;
        }
    }

    public static HashMap<String, String> getUserInfoByName(String name,long resfrom,long resto) {
        HashMap<String, String> map = new HashMap<>();
        Connection conn = getConnection("h2_swjtu");
        if(conn==null){
            Log.d(TAG, " not connet");
        }
        try {
            Statement st = conn.createStatement();
            String sql = "SELECT date,heart FROM h2_swjtu.swjtu_date where name= '" + name + "' and date between '" + resfrom + "' and '" + resto + "' order by date asc";//"select * from swjtuss where name = '" + name + "'";
            ResultSet res = st.executeQuery(sql);
            if (res == null) {
                return null;
            } else {
                while(res.next()) {
                    map.put(res.getString("date"), res.getString("heart"));
                }
                conn.close();
                st.close();
                res.close();
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据操作异常");
            return null;
        }
    }

    public static ArrayList<Machine_h2> getUserInfoByName(int unm) {
        HashMap<String, String> map = new HashMap<>();
        ArrayList<Machine_h2> values = new ArrayList<Machine_h2>();
        Connection conn = getConnection("h2_swjtu");
        if(conn==null){
            Log.d(TAG, " not connet");
        }
        try {
            Statement st = conn.createStatement();
            String sql = "select * from swjtu";
            ResultSet res = st.executeQuery(sql);
            if (res == null) {
                return null;
            } else {
                while(res.next()) {
                    String field1 = res.getMetaData().getColumnName(2);
                    String field2 = res.getMetaData().getColumnName(3);
                    String field3 = res.getMetaData().getColumnName(4);
                    values.add(new Machine_h2(res.getString(field1),res.getString(field2),res.getString(field3)));
                }
                conn.close();
                st.close();
                res.close();
                return values;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据操作异常");
            return null;
        }
    }

    public static boolean insertUserByName(String name,String psd) {
        Connection conn = getConnection("h2_swjtu");
        String sql = "insert into swjtuss (name,password) values('" + name + "','" + psd + "')";
        try {
            Statement st = conn.createStatement();
            st.execute(sql);
            conn.close();
            st.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据插入失败");
            return false;
        }
    }

    public static boolean deletetUserByName(String name) {
        Connection conn = getConnection("h2_swjtu");
        String sql = "delete from swjtuss where name = '" + name + "'";
        try {
            Statement st = conn.createStatement();
            st.execute(sql);
            conn.close();
            st.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据删除失败");
            return false;
        }
    }

    public static boolean updateUserByName(String name,String psd) {
        Connection conn = getConnection("h2_swjtu");
        String sql = "update swjtuss set password = '" + psd + "' where name = '" + name + "'";
        try {
            Statement st = conn.createStatement();
            st.execute(sql);
            conn.close();
            st.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据更新失败");
            return false;
        }
    }

    public static ArrayList<Machine_h2> getInfoByName() {
        HashMap<String, String> map = new HashMap<>();
        ArrayList<Machine_h2> values = new ArrayList<Machine_h2>();
        Connection conn = getConnection("h2_swjtu");
        if(conn==null){
            Log.d(TAG, " not connet");
        }
        try {
            Statement st = conn.createStatement();
            String sql = "select * from swjtus";
            ResultSet res = st.executeQuery(sql);
            if (res == null) {
                return null;
            } else {
                while(res.next()) {
                    String field1 = res.getMetaData().getColumnName(1);
                    String field2 = res.getMetaData().getColumnName(2);
                    values.add(new Machine_h2(res.getString(field1),res.getString(field2),""));
                }
                conn.close();
                st.close();
                res.close();
                return values;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据操作异常");
            return null;
        }
    }

    public static HashMap<String, String> getInfoByName_res(int num) {
        HashMap<String, String> map = new HashMap<>();
        Connection conn = getConnection("h2_swjtu");
        if(conn==null){
            Log.d(TAG, " not connet");
        }
        try {
            Statement st = conn.createStatement();
            String sql = "select * from swjtus where id = '" + num + "'";
            ResultSet res = st.executeQuery(sql);
            if (res == null) {
                return null;
            } else {
                int cnt = res.getMetaData().getColumnCount();
                res.next();
                for (int i = 1; i <= cnt; ++i) {
                    String field = res.getMetaData().getColumnName(i);
                    map.put(field, res.getString(field));
                }
                conn.close();
                st.close();
                res.close();
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据操作异常");
            return null;
        }
    }
}
