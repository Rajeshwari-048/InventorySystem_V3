
package com.inventory;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("login.jsp")
               .forward(request, response);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        boolean valid = false;
        boolean isAdmin = role != null &&
                          role.toLowerCase().contains("admin");

        try (Connection con = DBConnection.getConnection()) {

            String sql = isAdmin
                    ? "SELECT * FROM admin WHERE username=? AND password=?"
                    : "SELECT * FROM users WHERE username=? AND password=?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, username);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    valid = rs.next();
                }
            }

            if (valid) {

                HttpSession session = request.getSession();
                session.setAttribute("user", username);

                if (isAdmin) {
                    session.setAttribute("admin", username);
                    session.setAttribute("role", "ADMIN");

                    response.sendRedirect(
                        request.getContextPath() + "/dashboard");

                } else {
                    session.removeAttribute("admin");
                    session.setAttribute("role", "USER");

                    response.sendRedirect(
                        request.getContextPath() + "/home");
                }

            } else {
                request.setAttribute(
                    "error", "Invalid username or password");

                request.getRequestDispatcher("login.jsp")
                       .forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute(
                "error", "DB Error: " + e.getMessage());

            request.getRequestDispatcher("login.jsp")
                   .forward(request, response);
        }
    }
}
