package com.qfedu.demo.dao;

import com.qfedu.demo.model.Course;
import com.qfedu.demo.model.Grade;
import com.qfedu.demo.model.GradeDTO;
import com.qfedu.demo.utils.DBUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDao {
    QueryRunner queryRunner = new QueryRunner(DBUtils.getDs());
    public List<GradeDTO> getAllGrades() throws SQLException {
        return queryRunner.query("SELECT g.*,c.* FROM grade g LEFT JOIN grade_course gc ON g.`gid`=gc.`gid` LEFT JOIN course c ON gc.`cid`=c.`cid` ORDER BY g.`gid`", new ResultSetHandler<List<GradeDTO>>() {
            @Override
            public List<GradeDTO> handle(ResultSet rs) throws SQLException {
                List<GradeDTO> list = new ArrayList<>();
                //上一次遍历的年级 id
                int lastGid = -1;
                GradeDTO gradeDTO = null;
                while (rs.next()) {
                    int gid = rs.getInt("gid");
                    if (gid == lastGid) {
                        //说明这一行的年级已经读过了，这个地方只要处理课程id和课程名称
                        Course c = new Course();
                        c.setCid(rs.getInt("cid"));
                        c.setCourseName(rs.getString("courseName"));
                        gradeDTO.getCourses().add(c);
                    }else{
                        //这一行的年级还没读过，这里要处理 四列
                        //说明这是一个新的年级了
                        gradeDTO = new GradeDTO();
                        gradeDTO.setGid(gid);
                        gradeDTO.setGradeName(rs.getString("gradeName"));
                        List<Course> courses = new ArrayList<>();
                        Course c = new Course();
                        c.setCid(rs.getInt("cid"));
                        c.setCourseName(rs.getString("courseName"));
                        courses.add(c);
                        gradeDTO.setCourses(courses);
                        list.add(gradeDTO);
                    }
                    lastGid = gid;
                }
                return list;
            }
        });
    }

    public Grade getGradeByGradeName(String gradeName) throws SQLException {
        return queryRunner.query("select * from grade where gradeName=?",new BeanHandler<>(Grade.class),gradeName);
    }

    /**
     * 注意，为了事务，这个地方需要确保这个查询方法使用和 service 中相同的 connection
     * @param grade
     * @return
     */
    public Integer addGrade(Grade grade) throws SQLException {
        Connection con = DBUtils.getCon();
        PreparedStatement ps = con.prepareStatement("insert into grade(gradeName) values(?)", Statement.RETURN_GENERATED_KEYS);
        ps.setString(1,grade.getGradeName());
        //执行插入操作
        int i = ps.executeUpdate();
        //获取刚刚插入记录的 id
        //这里获取的 rs 只有一行一列
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int anInt = rs.getInt(1);
            grade.setGid(anInt);
        }
        DBUtils.close(rs);
        DBUtils.close(ps);
        return i;
    }

    /**
     * insert into grade(gid,cid) values(?,?),(?,?)\
     *
     * gid 9
     * cids 1 2 3 4
     *
     * 9 1 9 2 9 3 9 4
     * @param gid
     * @param cids
     * @return
     */
    public Integer addGradeCourse(Integer gid, List<Integer> cids) throws SQLException {
        StringBuffer sql = new StringBuffer("insert into grade_course(gid,cid) values");
        //参数的长度是 cids 长度的 2 倍
        Object[] params = new Object[cids.size() * 2];
        for (int i = 0; i < cids.size(); i++) {
            if (i == cids.size() - 1) {
                //最后一次遍历
                sql.append("(?,?)");
            }else {
                sql.append("(?,?),");
            }
            params[2 * i] = gid;
            params[2 * i + 1] = cids.get(i);
        }
        return queryRunner.update(DBUtils.getCon(),sql.toString(),params);
    }

    public Integer deleteGradeByGid(int gid) throws SQLException {
        return queryRunner.update(DBUtils.getCon(),"delete from grade where gid=?", gid);
    }

    public Integer deleteGradeCourseByGid(int gid) throws SQLException {
        return queryRunner.update(DBUtils.getCon(),"delete from grade_course where gid=?", gid);
    }
}
