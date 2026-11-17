
package com.inventory;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Only Admin can access this page
        if (session == null ||
            session.getAttribute("admin") == null) {

            response.sendRedirect(
                request.getContextPath() + "/login");
            return;
        }

        List<Map<String, Object>> products = new ArrayList<>();

        String sql = "SELECT * FROM products";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();

                m.put("id", rs.getInt("id"));
                m.put("name", rs.getString("name"));
                m.put("category", rs.getString("category"));
                m.put("stock", rs.getInt("stock"));
                m.put("price", rs.getDouble("price"));
                m.put("purchased", rs.getInt("purchased"));
                m.put("spent", rs.getDouble("spent"));

                products.add(m);
            }

        } catch (Exception e) {
            throw new ServletException(
                "Unable to load admin dashboard", e);
        }

        request.setAttribute("products", products);

        request.getRequestDispatcher("dashboard.jsp")
               .forward(request, response);
    }
}
