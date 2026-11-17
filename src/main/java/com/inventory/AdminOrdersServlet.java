package com.inventory;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/orders")
public class AdminOrdersServlet extends HttpServlet {

    private boolean isAdmin(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s != null && s.getAttribute("admin") != null;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (!isAdmin(req)) { res.sendRedirect(req.getContextPath() + "/login"); return; }
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        out.println("<html><head><title>Admin Orders</title><style>"
            + "body{font-family:Arial;background:#f4f6f8;margin:0}"
            + ".bar{background:#2e7d32;color:#fff;padding:18px 30px;font-size:22px;font-weight:bold}"
            + ".bar a{color:#fff;font-size:15px;margin-left:20px;text-decoration:none}"
            + ".box{background:#fff;margin:20px 30px;padding:15px 20px;border-radius:8px}"
            + "table{border-collapse:collapse;width:100%}th,td{border:1px solid #ddd;padding:8px;text-align:center}"
            + "th{background:#2e7d32;color:#fff}"
            + "button{border:0;color:#fff;padding:6px 12px;border-radius:5px;cursor:pointer}"
            + ".ok{background:#2e7d32}.no{background:#c62828}"
            + "</style></head><body>");
        out.println("<div class='bar'>Admin Orders <a href='home'>Home</a><a href='dashboard'>Dashboard</a><a href='logout'>Logout</a></div>");

        try (Connection con = DBConnection.getConnection()) {
            out.println("<div class='box'><h3>Stock available</h3><table><tr><th>ID</th><th>Product</th><th>Stock</th><th>Price</th></tr>");
            ResultSet p = con.createStatement().executeQuery("SELECT id, name, stock, price FROM products");
            while (p.next()) {
                out.println("<tr><td>" + p.getInt(1) + "</td><td>" + Util.esc(p.getString(2)) + "</td><td>"
                    + p.getInt(3) + "</td><td>" + p.getDouble(4) + "</td></tr>");
            }
            out.println("</table></div>");

            out.println("<div class='box'><h3>Approved purchases by user</h3><table><tr><th>User</th><th>Products purchased</th><th>Total amount</th></tr>");
            ResultSet g = con.createStatement().executeQuery(
                "SELECT username, COUNT(*), SUM(price) FROM orders WHERE status='APPROVED' GROUP BY username");
            while (g.next()) {
                out.println("<tr><td>" + Util.esc(g.getString(1)) + "</td><td>" + g.getInt(2) + "</td><td>Rs. "
                    + String.format("%.2f", g.getDouble(3)) + "</td></tr>");
            }
            out.println("</table></div>");

            out.println("<div class='box'><h3>All requests</h3><table><tr><th>Order</th><th>User</th><th>Product</th><th>Price</th><th>Time</th><th>Status</th><th>Action</th></tr>");
            ResultSet o = con.createStatement().executeQuery(
                "SELECT id, username, product_name, price, created_at, status FROM orders ORDER BY id DESC");
            while (o.next()) {
                int oid = o.getInt(1);
                String st = o.getString(6);
                out.println("<tr><td>" + oid + "</td><td>" + Util.esc(o.getString(2)) + "</td><td>"
                    + Util.esc(o.getString(3)) + "</td><td>" + o.getDouble(4) + "</td><td>" + o.getTimestamp(5)
                    + "</td><td>" + Util.esc(st) + "</td><td>");
                if ("PENDING".equals(st)) {
                    out.println("<form method='post' action='orders' style='display:inline'>"
                        + "<input type='hidden' name='id' value='" + oid + "'>"
                        + "<button class='ok' name='action' value='approve'>Approve</button> "
                        + "<button class='no' name='action' value='reject'>Reject</button></form>");
                } else {
                    out.println("-");
                }
                out.println("</td></tr>");
            }
            out.println("</table></div>");
        } catch (Exception e) {
            out.println("<div class='box' style='color:red'>Error: " + Util.esc(e.getMessage()) + "</div>");
        }
        out.println("</body></html>");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (!isAdmin(req)) { res.sendRedirect(req.getContextPath() + "/login"); return; }
        String action = req.getParameter("action");
        try (Connection con = DBConnection.getConnection()) {
            int id = Integer.parseInt(req.getParameter("id"));
            con.setAutoCommit(false);
            PreparedStatement sel = con.prepareStatement("SELECT product_id FROM orders WHERE id=? AND status='PENDING'");
            sel.setInt(1, id);
            ResultSet rs = sel.executeQuery();
            if (rs.next()) {
                String newStatus = "REJECTED";
                if ("approve".equals(action)) {
                    PreparedStatement up = con.prepareStatement(
                        "UPDATE products SET stock = stock - 1 WHERE id=? AND stock > 0");
                    up.setInt(1, rs.getInt(1));
                    if (up.executeUpdate() == 1) newStatus = "APPROVED";
                }
                PreparedStatement st = con.prepareStatement("UPDATE orders SET status=? WHERE id=?");
                st.setString(1, newStatus);
                st.setInt(2, id);
                st.executeUpdate();
            }
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        res.sendRedirect(req.getContextPath() + "/orders");
    }
}