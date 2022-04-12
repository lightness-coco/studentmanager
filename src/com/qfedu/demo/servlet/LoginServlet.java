package com.qfedu.demo.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qfedu.demo.model.RespBean;
import com.qfedu.demo.service.UserService;
import com.qfedu.demo.utils.CommonsUtils;
import com.qfedu.demo.utils.VerificationCode;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 处理登录页面和登录请求的接口
 */
@WebServlet(urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("vcode".equals(action)) {
            //说明是来获取验证码的
            //http://localhost:8080/s/login?action=vcode
            VerificationCode verificationCode = new VerificationCode();
            //获取验证码图片，注意顺序，要先获取图片，后获取文本
            BufferedImage image = verificationCode.getImage();
            //获取验证码上面的文本
            String text = verificationCode.getText();
            //将验证码的文本存入 session 中，将来方便比对
            HttpSession session = req.getSession();
            session.setAttribute("vcode", text);
            //把验证码写出到浏览器上
            VerificationCode.output(image,resp.getOutputStream());
        } else {
            //http://localhost:8080/s/login
            //服务端跳转
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        }
    }

    /**
     * 在这里处理登录请求
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        Integer result = userService.login(username, password,req);
        RespBean respBean = null;
        if (result == CommonsUtils.ACCOUNT_DISABLED) {
            respBean = RespBean.error("账户被禁用，登录失败");
        } else if (result == CommonsUtils.USERNAME_NOTFOUND_OR_BAD_PASSWORD) {
            respBean = RespBean.error("用户名或者密码输入错误，登录失败");
        } else if (result == CommonsUtils.LOGIN_SUCCESS) {
            respBean = RespBean.ok("登录成功");
        } else if (result == CommonsUtils.BAD_VCODE) {
            respBean = RespBean.error("验证码错误，登录失败");
        } else {
            respBean = RespBean.error("登录失败");
        }
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        ObjectMapper om = new ObjectMapper();
        out.write(om.writeValueAsString(respBean));
    }
}
