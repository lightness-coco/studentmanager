package com.qfedu.demo.dao;

import com.qfedu.demo.model.Course;
import com.qfedu.demo.utils.DBUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

public class CourseDao {
    QueryRunner queryRunner = new QueryRunner(DBUtils.getDs());

    public List<Course> getAllCourses() throws SQLException {
        return queryRunner.query("select * from course", new BeanListHandler<>(Course.class));
    }

    public Course getCourseByCourseName(String courseName) throws SQLException {
        return queryRunner.query("select * from course where courseName=?", new BeanHandler<>(Course.class), courseName);
    }

    public Integer addCourse(String courseName) throws SQLException {
        return queryRunner.update("insert into course(courseName) values(?)", courseName);
    }

    public Integer deleteCourseByCid(int cid) throws SQLException {
        return queryRunner.update("delete from course where cid=?", cid);
    }

    public List<Course> getCoursesByGid(int gid) throws SQLException {
        return queryRunner.query("SELECT c.* FROM course c,grade_course gc WHERE c.`cid`=gc.`cid` AND gc.gid=?", new BeanListHandler<>(Course.class), gid);
    }
}
