
package com.inventory;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/home")
public class UserHomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        // Only logged-in Users can access User Home
        if (session == null ||
            session.getAttribute("user") == null ||
            !"USER".equals(session.getAttribute("role"))) {

            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<Map<String, Object>> products = new ArrayList<>();

        String sql = "SELECT * FROM products ORDER BY id";

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                Map<String, Object> m = new HashMap<>();

                m.put("id", rs.getInt("id"));
                m.put("name", rs.getString("name"));
                m.put("price", rs.getDouble("price"));
                m.put("image", rs.getString("image"));
                m.put("stock", rs.getInt("stock"));

                products.add(m);
            }

        } catch (SQLException e) {
            throw new ServletException(
                "Unable to load products", e);
        }

        req.setAttribute("products", products);

        req.getRequestDispatcher("/home.jsp")
           .forward(req, res);
    }
}
