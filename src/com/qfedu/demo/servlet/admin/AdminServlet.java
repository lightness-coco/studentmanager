package com.qfedu.demo.servlet.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

@WebServlet(urlPatterns = "/index")
public class AdminServlet extends HttpServlet {
    private static final Properties SYSTEM_INFO = new Properties();
    /**
     * 登录成功后，跳转到这个页面上
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //将配置文件的内容读取到 SYSTEM_INFO 中
        SYSTEM_INFO.load(AdminServlet.class.getResourceAsStream("/system_info.properties"));
        //将 SYSTEM_INFO 存入当前请求中
        req.setAttribute("si",SYSTEM_INFO);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/admin.jsp").forward(req, resp);
    }
}
