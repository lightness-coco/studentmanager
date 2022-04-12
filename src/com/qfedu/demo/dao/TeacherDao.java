package com.qfedu.demo.dao;

import com.qfedu.demo.model.*;
import com.qfedu.demo.utils.DBUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherDao {
    QueryRunner queryRunner = new QueryRunner(DBUtils.getDs());

    public Long getTotal() throws SQLException {
        return queryRunner.query("select count(*) from teacher", new ScalarHandler<>());
    }

    public List<TeacherDTO> getTeacherByPage(int start, int size, String sort, String order) throws SQLException {
        return queryRunner.query("SELECT t.*,c.*,co.`cid` AS coid,co.`courseName`,g.`gradeName` FROM (SELECT * FROM teacher ORDER BY " + sort + " " + order + " LIMIT ?,?) t LEFT JOIN teacher_course tc ON t.`tid`=tc.`tid` LEFT JOIN grade g ON g.`gid`=tc.`gid` LEFT JOIN clazz c ON c.`cid`=tc.`clazzId` LEFT JOIN course co ON co.cid=tc.`courseId`", new ResultSetHandler<List<TeacherDTO>>() {
            @Override
            public List<TeacherDTO> handle(ResultSet rs) throws SQLException {
                List<TeacherDTO> list = new ArrayList<>();
                int lastTid = -1;
                TeacherDTO teacherDTO = null;
                while (rs.next()) {
                    int tid = rs.getInt("tid");
                    if (tid == lastTid) {
                        //说明这个 teacher 之前已经加载过
                        Map<String, Object> map = new HashMap<>();
                        Grade g = new Grade();
                        int gid = rs.getInt("gid");
                        g.setGid(gid);
                        g.setGradeName(rs.getString("gradeName"));
                        Clazz c = new Clazz();
                        c.setCid(rs.getInt("cid"));
                        c.setClazzName(rs.getString("clazzName"));
                        c.setGid(gid);
                        Course co = new Course();
                        co.setCid(rs.getInt("coid"));
                        co.setCourseName(rs.getString("courseName"));
                        map.put("grade", g);
                        map.put("clazz", c);
                        map.put("course", co);
                        teacherDTO.getCourses().add(map);
                    } else {
                        //第一次加载这个 teacher
                        teacherDTO = new TeacherDTO();
                        teacherDTO.setTid(tid);
                        teacherDTO.setGender(rs.getString("gender"));
                        teacherDTO.setName(rs.getString("name"));
                        teacherDTO.setNumber(rs.getString("number"));
                        teacherDTO.setPhone(rs.getString("phone"));
                        teacherDTO.setQq(rs.getString("qq"));
                        List<Map<String, Object>> courses = new ArrayList<>();
                        String gradeName = rs.getString("gradeName");
                        if (gradeName != null && !"".equals(gradeName)) {
                            //说明这个老师有课程
                            Map<String, Object> map = new HashMap<>();
                            Grade g = new Grade();
                            int gid = rs.getInt("gid");
                            g.setGid(gid);
                            g.setGradeName(gradeName);
                            Clazz c = new Clazz();
                            c.setCid(rs.getInt("cid"));
                            c.setClazzName(rs.getString("clazzName"));
                            c.setGid(gid);
                            Course co = new Course();
                            co.setCid(rs.getInt("coid"));
                            co.setCourseName(rs.getString("courseName"));
                            map.put("grade", g);
                            map.put("clazz", c);
                            map.put("course", co);
                            courses.add(map);
                        }
                        teacherDTO.setCourses(courses);
                        list.add(teacherDTO);
                    }
                    lastTid = tid;
                }
                return list;
            }
        }, start, size);
    }

    public Teacher getTeacherByNumber(String number) throws SQLException {
        return queryRunner.query("select * from teacher where number=?", new BeanHandler<>(Teacher.class), number);
    }

    public Integer addTeacher(Teacher teacher) throws SQLException {
        Connection con = DBUtils.getCon();
        PreparedStatement ps = con.prepareStatement("insert into teacher (number,name,gender,phone,qq) values (?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
        ps.setString(1,teacher.getNumber());
        ps.setString(2,teacher.getName());
        ps.setString(3,teacher.getGender());
        ps.setString(4,teacher.getPhone());
        ps.setString(5, teacher.getQq());
        //执行插入操作
        int i = ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int anInt = rs.getInt(1);
            teacher.setTid(anInt);
        }
        DBUtils.close(rs);
        DBUtils.close(ps);
        return i;
    }

    /**
     * insert into teacher_course(tid,gid,clazzId,courseId) values(?,?,?,?),(),()
     * @param tid
     * @param courses
     * @return
     */
    public Integer addTeacherCourse(Integer tid, String[] courses) throws SQLException {
        StringBuffer sql = new StringBuffer("insert into teacher_course(tid,gid,clazzId,courseId) values");
        Object[] params = new Object[courses.length * 4];
        for (int i = 0; i < courses.length; i++) {
            if (i == courses.length - 1) {
                //最后一次拼接
                sql.append("(?,?,?,?)");
            }else{
                sql.append("(?,?,?,?),");
            }
            String cours = courses[i];// 2_3_4
            String[] s = cours.split("_");//[2,3,4]
            params[i * 4] = tid;
            params[i * 4 + 1] = s[0];
            params[i * 4 + 2] = s[1];
            params[i * 4 + 3] = s[2];
        }
        return queryRunner.update(DBUtils.getCon(), sql.toString(), params);
    }

    public Integer deleteTeacherCourserByTid(Integer tid) throws SQLException {
        return queryRunner.update(DBUtils.getCon(),"delete from teacher_course where tid=?", tid);
    }

    public Integer updateTeacherByNumber(TeacherDTO teacherDTO) throws SQLException {
        return queryRunner.update(DBUtils.getCon(), "update teacher set name=?,phone=?,gender=?,qq=? where number=?", teacherDTO.getName(), teacherDTO.getPhone(), teacherDTO.getGender(), teacherDTO.getQq(), teacherDTO.getNumber());
    }
}
