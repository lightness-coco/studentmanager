package com.qfedu.demo.servlet.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qfedu.demo.model.Course;
import com.qfedu.demo.model.RespBean;
import com.qfedu.demo.service.CourseService;
import com.qfedu.demo.utils.CommonsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = "/course")
public class CourseServlet extends HttpServlet {

    CourseService courseService = new CourseService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("page".equals(action)) {
            //说明是来请求页面的
            req.getRequestDispatcher("/WEB-INF/jsp/course/courseList.jsp").forward(req, resp);
        } else if ("data".equals(action)) {
            //说明是来请求json数据的
            //前端是一个表格，所以这里返回的是 json 数组，数组格式是 [{xx:xx,xx:xx},{},{}]
            List<Course> list = courseService.getAllCourses();
            ObjectMapper om = new ObjectMapper();
            String json = om.writeValueAsString(list);
            resp.setContentType("application/json;charset=utf-8");
            PrintWriter out = resp.getWriter();
            out.write(json);
        } else if ("data_from_teacher".equals(action)) {
            String gid = req.getParameter("gid");
            List<Course> list = courseService.getCoursesByGid(gid);
            ObjectMapper om = new ObjectMapper();
            String json = om.writeValueAsString(list);
            resp.setContentType("application/json;charset=utf-8");
            PrintWriter out = resp.getWriter();
            out.write(json);
        }
    }

    /**
     * 添加课程
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String courseName = req.getParameter("courseName");
        Integer result = courseService.addCourse(courseName);
        resp.setContentType("application/json;charset=utf-8");
        RespBean respBean = null;
        if (result == CommonsUtils.REPEATABLE_VALUE) {
            respBean = RespBean.error("课程名重复，添加失败");
        } else if (result == CommonsUtils.INSERT_SUCCESS) {
            respBean = RespBean.ok("添加成功");
        } else if (result == CommonsUtils.INVALIDATE_PARAM) {
            respBean = RespBean.error("参数非法，添加失败");
        } else {
            respBean = RespBean.error("添加失败");
        }
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(respBean);
        resp.getWriter().write(json);
    }


    /**
     * 删除课程
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取删除的课程id
        String cid = req.getParameter("cid");
        Integer result = courseService.deleteCourseByCid(cid);
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        RespBean respBean = null;
        if (result == CommonsUtils.DELETE_SUCCESS) {
            respBean = RespBean.ok("删除成功");
        }else{
            respBean = RespBean.error("删除失败");
        }
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(respBean);
        out.write(json);
    }
}