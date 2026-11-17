package com.inventory;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

/** Public: login/register. Users: home, images, logout, purchase, my orders, bill. Admin: everything. */
@WebFilter("/*")
public class AuthFilter implements Filter {
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) rq;
        HttpServletResponse res = (HttpServletResponse) rs;
        String ctx = req.getContextPath();
        String path = req.getRequestURI().substring(ctx.length());
        HttpSession s = req.getSession(false);
        boolean admin = s != null && s.getAttribute("admin") != null;
        boolean user = s != null && s.getAttribute("user") != null;

        boolean isPublic = path.equals("/login") || path.equals("/login.jsp")
                || path.equals("/register") || path.equals("/register.jsp");
        boolean userOk = path.equals("/home") || path.equals("/home.jsp") || path.equals("/img")
                || path.equals("/logout") || path.equals("/") || path.equals("/index.jsp")
                || path.equals("/PurchaseServlet") || path.equals("/orderSuccess.jsp")
                || path.equals("/myorders") || path.equals("/bill");

        if (admin || isPublic || (user && userOk)) chain.doFilter(rq, rs);
        else if (user) res.sendRedirect(ctx + "/home");
        else res.sendRedirect(ctx + "/login");
    }
}