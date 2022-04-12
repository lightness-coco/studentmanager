package com.qfedu.demo.servlet.grade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qfedu.demo.model.GradeDTO;
import com.qfedu.demo.model.RespBean;
import com.qfedu.demo.service.GradeService;
import com.qfedu.demo.utils.CommonsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//http://localhost:8080/s/grade?action=page
@WebServlet(urlPatterns = "/grade")
public class GradeServlet extends HttpServlet {
    GradeService gradeService = new GradeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("page".equals(action)) {
            req.getRequestDispatcher("/WEB-INF/jsp/grade/gradeList.jsp").forward(req, resp);
        } else if ("data".equals(action)) {
            //获取 json
            //查询所有的年级
            List<GradeDTO> list = gradeService.getAllGrades();
            resp.setContentType("application/json;charset=utf-8");
            PrintWriter out = resp.getWriter();
            out.write(new ObjectMapper().writeValueAsString(list));
        } else if ("data_from_clazz".equals(action)) {
            //获取 json
            //查询所有的年级
            List<GradeDTO> list = gradeService.getAllGrades();
            GradeDTO gradeDTO = new GradeDTO();
            gradeDTO.setGid(-1);
            gradeDTO.setGradeName("全部");
            list.add(0, gradeDTO);
            resp.setContentType("application/json;charset=utf-8");
            PrintWriter out = resp.getWriter();
            out.write(new ObjectMapper().writeValueAsString(list));
        }
    }

    /**
     * 添加年级
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取年级名字
        String gradeName = req.getParameter("gradeName");
        //获取课程 id
        String[] cids = req.getParameterValues("cid");
        List<Integer> collect = Arrays.stream(cids).map(i -> Integer.parseInt(i)).collect(Collectors.toList());
        Integer result = gradeService.addGrade(gradeName, collect);
        RespBean respBean = null;
        if (result == CommonsUtils.REPEATABLE_VALUE) {
            respBean = RespBean.error("年级名重复，添加失败");
        } else if (result == CommonsUtils.INSERT_SUCCESS) {
            respBean = RespBean.ok("添加成功");
        } else {
            respBean = RespBean.error("添加失败");
        }
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.write(new ObjectMapper().writeValueAsString(respBean));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String gid = req.getParameter("gid");
        Integer result = gradeService.deleteGradeByGid(Integer.parseInt(gid));
        RespBean respBean = null;
        if (result == CommonsUtils.DELETE_SUCCESS) {
            respBean = RespBean.ok("删除成功");
        }else{
            respBean = RespBean.error("删除失败");
        }
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.write(new ObjectMapper().writeValueAsString(respBean));
    }
}
