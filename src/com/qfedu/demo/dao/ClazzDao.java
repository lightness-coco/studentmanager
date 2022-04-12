package com.qfedu.demo.dao;

import com.qfedu.demo.model.Clazz;
import com.qfedu.demo.model.ClazzDTO;
import com.qfedu.demo.model.Grade;
import com.qfedu.demo.utils.DBUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClazzDao {
    QueryRunner queryRunner = new QueryRunner(DBUtils.getDs());

    public Long getTotal(String gid) throws SQLException {
        if (gid != null && !"".equals(gid)) {
            //说明用户传递了 gid 过来
            return queryRunner.query("select count(*) from clazz where gid=?", new ScalarHandler<>(), gid);
        }else{
            //没有 gid
            return queryRunner.query("select count(*) from clazz",new ScalarHandler<>());
        }
    }

    /**
     * SELECT c.*,g.`gradeName` FROM clazz c LEFT JOIN grade g ON c.`gid`=g.`gid` WHERE g.`gid`=3 ORDER BY c.`cid` ASC LIMIT 0,10
     * @param gid
     * @param start
     * @param size
     * @param sort
     * @param order
     * @return
     */
    public List<ClazzDTO> getClazzByPage(String gid, int start, int size, String sort, String order) throws SQLException {
        StringBuffer sql = new StringBuffer("SELECT c.*,g.`gradeName` FROM clazz c LEFT JOIN grade g ON c.`gid`=g.`gid` ");
        Object[] params = null;
        if (gid != null && !"".equals(gid)) {
            sql.append("WHERE g.`gid`=? ORDER BY "+sort+" "+order+" LIMIT ?,?");
            params = new Object[3];
            params[0] = gid;
            params[1] = start;
            params[2] = size;
        }else{
            sql.append("ORDER BY "+sort+" "+order+" LIMIT ?,?");
            params = new Object[2];
            params[0] = start;
            params[1] = size;
        }
        return queryRunner.query(sql.toString(), new ResultSetHandler<List<ClazzDTO>>() {
            @Override
            public List<ClazzDTO> handle(ResultSet rs) throws SQLException {
                List<ClazzDTO> list = new ArrayList<>();
                while (rs.next()) {
                    int cid = rs.getInt("cid");
                    String clazzName = rs.getString("clazzName");
                    int gid1 = rs.getInt("gid");
                    String gradeName = rs.getString("gradeName");
                    ClazzDTO clazzDTO = new ClazzDTO();
                    clazzDTO.setCid(cid);
                    clazzDTO.setClazzName(clazzName);
                    Grade g = new Grade();
                    g.setGid(gid1);
                    g.setGradeName(gradeName);
                    clazzDTO.setGrade(g);
                    list.add(clazzDTO);
                }
                return list;
            }
        }, params);
    }

    public Clazz getClazzByClazzName(String clazzName) throws SQLException {
        return queryRunner.query("select * from clazz where clazzName=?", new BeanHandler<>(Clazz.class), clazzName);
    }

    public Integer addClazz(String clazzName, int gid) throws SQLException {
        return queryRunner.update("insert into clazz set clazzName=?,gid=?", clazzName, gid);
    }
}
