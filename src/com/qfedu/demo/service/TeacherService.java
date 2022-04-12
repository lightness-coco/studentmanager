package com.qfedu.demo.service;

import com.qfedu.demo.dao.TeacherDao;
import com.qfedu.demo.model.RespPageBean;
import com.qfedu.demo.model.Teacher;
import com.qfedu.demo.model.TeacherDTO;
import com.qfedu.demo.utils.CommonsUtils;
import com.qfedu.demo.utils.DBUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TeacherService {
    TeacherDao teacherDao = new TeacherDao();
    public RespPageBean getTeacherByPage(String page, String rows, String sort, String order) {
        Long total = null;
        List<TeacherDTO> list = null;
        try {
            //获取总记录数
            total = teacherDao.getTotal();
            int offset = Integer.parseInt(page);
            int size = Integer.parseInt(rows);
            list = teacherDao.getTeacherByPage((offset - 1) * size, size, sort, order);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new RespPageBean(total,list);
    }

    public Integer addTeacher(String number, String name, String gender, String phone, String qq, String[] courses) {
        Connection con = DBUtils.getCon();
        try {
            Teacher t = teacherDao.getTeacherByNumber(number);
            if (t != null) {
                //工号重复，添加失败
                return CommonsUtils.REPEATABLE_VALUE;
            }
            con.setAutoCommit(false);
            Teacher teacher = new Teacher();
            teacher.setGender(gender);
            teacher.setName(name);
            teacher.setNumber(number);
            teacher.setPhone(phone);
            teacher.setQq(qq);
            //这个添加要主键回填，添加完成后，teacher 的 tid 属性就有值了
            Integer r1 = teacherDao.addTeacher(teacher);
            Integer r2 = teacherDao.addTeacherCourse(teacher.getTid(), courses);
            con.commit();
            return (r1 == 1 && r2 == courses.length) ? CommonsUtils.INSERT_SUCCESS : CommonsUtils.OTHER_EXCEPTION;
        } catch (Exception e) {
//            e.printStackTrace();
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

    /**
     * 1. 修改教师基本信息
     * 2. 删除教师的所有课程
     * 3. 添加教师的课程
     * @param teacherDTO
     * @return
     */
    public Integer updateTeacher(TeacherDTO teacherDTO) {
        Connection con = DBUtils.getCon();
        try {
            con.setAutoCommit(false);
            //通过工号修改教师基本信息
            Integer r1 = teacherDao.updateTeacherByNumber(teacherDTO);
            Integer r2 = teacherDao.deleteTeacherCourserByTid(teacherDTO.getTid());
            String[] arr = new String[teacherDTO.getCourse().size()];
            for (int i = 0; i < teacherDTO.getCourse().size(); i++) {
                arr[i] = teacherDTO.getCourse().get(i);
            }
            Integer r3 = teacherDao.addTeacherCourse(teacherDTO.getTid(), arr);
            con.commit();
            return (r1 == 1 && r3 == teacherDTO.getCourse().size()) ? CommonsUtils.UPDATE_SUCCESS : CommonsUtils.OTHER_EXCEPTION;
        } catch (Exception e) {
//            e.printStackTrace();
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
