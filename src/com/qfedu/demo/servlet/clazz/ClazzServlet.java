package com.qfedu.demo.servlet.clazz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qfedu.demo.model.RespBean;
import com.qfedu.demo.model.RespPageBean;
import com.qfedu.demo.service.ClazzService;
import com.qfedu.demo.utils.CommonsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/clazz")
public class ClazzServlet extends HttpServlet {
    ClazzService clazzService = new ClazzService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("page".equals(action)) {
            req.getRequestDispatcher("/WEB-INF/jsp/clazz/clazzList.jsp").forward(req, resp);
        } else if ("data".equals(action)) {
            //获取年级的id
            String gid = req.getParameter("gid");
            //分页页码
            String page = req.getParameter("page");
            //每页查询多少条
            String rows = req.getParameter("rows");
            String sort = req.getParameter("sort");
            String order = req.getParameter("order");
            RespPageBean respPageBean = clazzService.getClazzByPage(gid, page, rows, sort, order);
            resp.setContentType("application/json;charset=utf-8");
            PrintWriter out = resp.getWriter();
            out.write(new ObjectMapper().writeValueAsString(respPageBean));
        } else if ("data_from_teacher".equals(action)) {
            //获取年级的id
            String gid = req.getParameter("gid");
            RespPageBean respPageBean = clazzService.getClazzByPage(gid, null,null, null,null);
            resp.setContentType("application/json;charset=utf-8");
            PrintWriter out = resp.getWriter();
            out.write(new ObjectMapper().writeValueAsString(respPageBean.getRows()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String clazzName = req.getParameter("clazzName");
        String gid = req.getParameter("gid");
        Integer result = clazzService.addClazz(clazzName, Integer.parseInt(gid));
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        RespBean respBean = null;
        if (result == CommonsUtils.INSERT_SUCCESS) {
            respBean = RespBean.ok("添加成功");
        } else if (result == CommonsUtils.REPEATABLE_VALUE) {
            respBean = RespBean.error("班级名重复，添加失败");
        }else {
            respBean = RespBean.error("添加失败");
        }
        out.write(new ObjectMapper().writeValueAsString(respBean));
    }
}
