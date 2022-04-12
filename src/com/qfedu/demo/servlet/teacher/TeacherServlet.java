package com.qfedu.demo.servlet.teacher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qfedu.demo.model.RespBean;
import com.qfedu.demo.model.RespPageBean;
import com.qfedu.demo.model.TeacherDTO;
import com.qfedu.demo.service.TeacherService;
import com.qfedu.demo.utils.CommonsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/teacher")
public class TeacherServlet extends HttpServlet {
    TeacherService teacherService = new TeacherService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("page".equals(action)) {
            req.getRequestDispatcher("/WEB-INF/jsp/teacher/teacherList.jsp").forward(req, resp);
        } else if ("data".equals(action)) {
            String page = req.getParameter("page");
            String rows = req.getParameter("rows");
            String sort = req.getParameter("sort");
            String order = req.getParameter("order");
            RespPageBean respPageBean = teacherService.getTeacherByPage(page, rows, sort, order);
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write(new ObjectMapper().writeValueAsString(respPageBean));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String number = req.getParameter("number");
        String name = req.getParameter("name");
        String gender = req.getParameter("sex");
        String phone = req.getParameter("phone");
        String qq = req.getParameter("qq");
        String[] courses = req.getParameterValues("course[]");
        Integer result = teacherService.addTeacher(number, name, gender, phone, qq, courses);
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        RespBean respBean = null;
        if (result == CommonsUtils.INSERT_SUCCESS) {
            respBean = RespBean.ok("添加成功");
        } else if (result == CommonsUtils.REPEATABLE_VALUE) {
            respBean = RespBean.error("工号重复，添加失败");
        } else {
            respBean = RespBean.error("添加失败");
        }
        out.write(new ObjectMapper().writeValueAsString(respBean));
    }

    /**
     * 在 post 请求中，请求体里边的参数会被自动转为 key-value 的形式，然后用户直接调用 getParameter 方法读取即可
     * 但是，在 put 请求中，请求体里边的参数不会被自动转化，所以我们有两种解决方案：
     * 1. 手动转化
     * 2. 用 json
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper om = new ObjectMapper();
        //{
        //  "number": "0001",
        //  "name": "张三222",
        //  "sex": "男",
        //  "phone": "13111111111",
        //  "qq": "123",
        //  "course": [
        //    "3_14_20",
        //    "2_8_18",
        //    "3_2_18"
        //  ]
        //}
        //将前端提交上来的 JSON 字符串转为一个 teacherdto 对象
        TeacherDTO teacherDTO = om.readValue(req.getInputStream(), TeacherDTO.class);
        Integer r = teacherService.updateTeacher(teacherDTO);
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        RespBean respBean = null;
        if (r == CommonsUtils.UPDATE_SUCCESS) {
            respBean = RespBean.ok("更新成功");
        }else{
            respBean = RespBean.error("更新失败");
        }
        out.write(om.writeValueAsString(respBean));
    }
}
