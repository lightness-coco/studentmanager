package com.qfedu.demo.service;

import com.qfedu.demo.dao.CourseDao;
import com.qfedu.demo.model.Course;
import com.qfedu.demo.utils.CommonsUtils;

import java.sql.SQLException;
import java.util.List;

public class CourseService {
    CourseDao courseDao = new CourseDao();

    public List<Course> getAllCourses() {
        try {
            return courseDao.getAllCourses();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer addCourse(String courseName) {

        if (courseName == null || "".equals(courseName)) {
            //参数有问题
            return CommonsUtils.INVALIDATE_PARAM;
        }

        try {
            //根据课程名查询课程
            Course c = courseDao.getCourseByCourseName(courseName);
            if (c != null) {
                //课程名重复，添加失败
                return CommonsUtils.REPEATABLE_VALUE;
            }
            Integer r = courseDao.addCourse(courseName);
            return r == 1 ? CommonsUtils.INSERT_SUCCESS : CommonsUtils.OTHER_EXCEPTION;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return CommonsUtils.OTHER_EXCEPTION;
    }

    public Integer deleteCourseByCid(String cid) {
        Integer r = null;
        try {
            r = courseDao.deleteCourseByCid(Integer.parseInt(cid));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r == 1 ? CommonsUtils.DELETE_SUCCESS : CommonsUtils.OTHER_EXCEPTION;
    }

    public List<Course> getCoursesByGid(String gid) {
        try {
            return courseDao.getCoursesByGid(Integer.parseInt(gid));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
