package com.qfedu.demo.utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtils {
    private static DataSource ds = null;
    private static final Properties PROPERTIES = new Properties();
    private static final ThreadLocal<Connection> THREAD_LOCAL = new ThreadLocal<>();

    static {
        try {
            PROPERTIES.load(DBUtils.class.getResourceAsStream("/jdbc.properties"));
            ds = DruidDataSourceFactory.createDataSource(PROPERTIES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getCon() {
        Connection connection = THREAD_LOCAL.get();
        if (connection == null) {
            try {
                //说明当前线程是第一次来获取 con 的，此时应该从 ds 中拿一个出来
                Connection con = ds.getConnection();
                THREAD_LOCAL.set(con);
                return con;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return connection;
    }

    public static void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(PreparedStatement con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(ResultSet con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static DataSource getDs() {
        return ds;
    }

}
