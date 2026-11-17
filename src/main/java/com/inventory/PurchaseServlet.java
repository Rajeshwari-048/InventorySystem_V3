package com.inventory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/PurchaseServlet")
public class PurchaseServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");

        int id;
        try {
            id = Integer.parseInt(req.getParameter("productId"));
        } catch (Exception e) {
            res.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        // Find who is logged in (user or admin)
        HttpSession s = req.getSession(false);
        Object u = null;
        if (s != null) {
            u = s.getAttribute("user") != null ? s.getAttribute("user") : s.getAttribute("admin");
        }
        String username = (u == null) ? "unknown" : String.valueOf(u);

        String head = "<html><body style='font-family:Arial;text-align:center;margin-top:100px'>";
        String back = "<br><a href='home' style='background:green;color:white;padding:12px 25px;"
                    + "text-decoration:none;border-radius:6px'>Continue Shopping</a></body></html>";
        String body;

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement sel = con.prepareStatement(
                "SELECT name, price, stock FROM products WHERE id = ?");
            sel.setInt(1, id);
            ResultSet rs = sel.executeQuery();

            if (!rs.next()) {
                body = "<h1 style='color:red'>Product not found</h1>";
            } else if (rs.getInt("stock") <= 0) {
                body = "<h1 style='color:red'>Out of stock</h1><h2>"
                     + Util.esc(rs.getString("name")) + "</h2>";
            } else {
                PreparedStatement ins = con.prepareStatement(
                    "INSERT INTO orders (username, product_id, product_name, price, status) "
                  + "VALUES (?, ?, ?, ?, 'PENDING')");
                ins.setString(1, username);
                ins.setInt(2, id);
                ins.setString(3, rs.getString("name"));
                ins.setDouble(4, rs.getDouble("price"));
                ins.executeUpdate();

                body = "<h1 style='color:green'>Request Sent!</h1>"
                     + "<h2>" + Util.esc(rs.getString("name")) + "</h2>"
                     + "<h3>Price: Rs. " + String.format("%.2f", rs.getDouble("price")) + "</h3>"
                     + "<h3>Waiting for admin approval</h3>";
            }
        } catch (Exception e) {
            body = "<h1 style='color:red'>Error</h1><p>" + Util.esc(e.getMessage()) + "</p>";
        }

        res.getWriter().println(head + body + back);
    }
}