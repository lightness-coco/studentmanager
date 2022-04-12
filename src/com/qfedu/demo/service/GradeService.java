package com.qfedu.demo.service;

import com.qfedu.demo.dao.GradeDao;
import com.qfedu.demo.model.Grade;
import com.qfedu.demo.model.GradeDTO;
import com.qfedu.demo.utils.CommonsUtils;
import com.qfedu.demo.utils.DBUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GradeService {
    GradeDao gradeDao = new GradeDao();

    public List<GradeDTO> getAllGrades() {
        try {
            return gradeDao.getAllGrades();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 1. 年级名不能重复
     * 2. 添加分两步：
     * 2.1 添加年级
     * 2.2 添加年级和课程的关联关系
     *
     * @param gradeName
     * @param cids
     * @return
     */
    public Integer addGrade(String gradeName, List<Integer> cids) {
        //当前线程第一次获取到 con，这个 con 是从 ds 中拿到的
        Connection con = DBUtils.getCon();
        //开启事务
        try {
            Grade g = gradeDao.getGradeByGradeName(gradeName);
            if (g != null) {
                //说明年级名称重复了，添加失败
                return CommonsUtils.REPEATABLE_VALUE;
            }
            con.setAutoCommit(false);
            Grade grade = new Grade();
            grade.setGradeName(gradeName);
            //现在 gid 没有值，等插入完成之后，会自动给 gid 赋上值
            Integer r1 = gradeDao.addGrade(grade);
            //添加年级和课程的关联关系
            Integer r2 = gradeDao.addGradeCourse(grade.getGid(), cids);
            //正常实行完毕，就提交
            con.commit();
            return (r1 == 1 && r2 == cids.size()) ? CommonsUtils.INSERT_SUCCESS : CommonsUtils.OTHER_EXCEPTION;
        } catch (SQLException e) {
//            e.printStackTrace();
            try {
                //出问题了就回滚
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            //关闭连接
            DBUtils.close(con);
        }
        return CommonsUtils.OTHER_EXCEPTION;
    }

    /**
     * 需要删除两个地方：1.年级；2.年级和课程的关联关系
     * @param gid
     * @return
     */
    public Integer deleteGradeByGid(int gid) {
        Connection con = DBUtils.getCon();
        try {
            con.setAutoCommit(false);
            //1. 删除年级
            Integer r1 = gradeDao.deleteGradeByGid(gid);
            //2. 删除年级课程关联关系
            Integer r2 = gradeDao.deleteGradeCourseByGid(gid);
            con.commit();
            return r1 == 1 ? CommonsUtils.DELETE_SUCCESS : CommonsUtils.OTHER_EXCEPTION;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            DBUtils.close(con);
        }
        return CommonsUtils.OTHER_EXCEPTION;
    }
}
