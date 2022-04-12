package com.qfedu.demo.dao;

import com.qfedu.demo.model.User;
import com.qfedu.demo.utils.DBUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.SQLException;

public class UserDao {
    QueryRunner queryRunner = new QueryRunner(DBUtils.getDs());

    public User getUserByUsername(String username) throws SQLException {
        return queryRunner.query("select * from user where username=?", new BeanHandler<>(User.class), username);
    }
}
