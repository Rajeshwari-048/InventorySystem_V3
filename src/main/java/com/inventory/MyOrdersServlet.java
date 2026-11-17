package com.inventory;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(urlPatterns = {"/myorders", "/bill"})
public class MyOrdersServlet extends HttpServlet {

    private static final String CSS =
        "body{font-family:Arial;background:#f4f6f8;margin:0}"
      + ".bar{background:#2e7d32;color:#fff;padding:18px 30px;font-size:22px;font-weight:bold}"
      + ".bar a{color:#fff;font-size:15px;margin-left:20px;text-decoration:none}"
      + ".box{background:#fff;margin:20px 30px;padding:15px 20px;border-radius:8px}"
      + ".cards{display:flex;gap:15px;margin:20px 30px}"
      + ".card{flex:1;background:#fff;padding:15px;border-radius:8px;text-align:center;color:#555}"
      + ".card b{font-size:28px;display:block;color:#222}"
      + "table{border-collapse:collapse;width:100%}th,td{border:1px solid #ddd;padding:8px;text-align:center}"
      + "th{background:#2e7d32;color:#fff}"
      + ".b{padding:4px 10px;border-radius:12px;color:#fff;font-size:13px}"
      + ".p{background:#f9a825}.a{background:#1976d2}.r{background:#c62828}.d{background:#2e7d32}"
      + "button,.btn{border:0;color:#fff;background:#2e7d32;padding:8px 14px;border-radius:5px;"
      + "cursor:pointer;text-decoration:none;font-size:14px}"
      + ".total{font-size:20px;text-align:right;margin-top:15px;font-weight:bold}"
      + "@media print{.noprint{display:none}}";

    private String who(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) return null;
        Object u = s.getAttribute("user") != null ? s.getAttribute("user") : s.getAttribute("admin");
        return u == null ? null : u.toString();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String user = who(req);
        if (user == null) { res.sendRedirect(req.getContextPath() + "/login"); return; }
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        boolean bill = "/bill".equals(req.getServletPath());

        out.println("<html><head><title>" + (bill ? "Bill" : "My Orders") + "</title><style>" + CSS + "</style></head><body>");
        out.println("<div class='bar noprint'>" + (bill ? "Bill" : "My Orders")
            + " <a href='home'>Home</a><a href='myorders'>My Orders</a><a href='logout'>Logout</a></div>");
        try (Connection con = DBConnection.getConnection()) {
            if (bill) showBill(con, out, user); else showOrders(con, out, user);
        } catch (Exception e) {
            out.println("<div class='box' style='color:red'>Error: " + Util.esc(e.getMessage()) + "</div>");
        }
        out.println("</body></html>");
    }

    private void showOrders(Connection con, PrintWriter out, String user) throws SQLException {
        int pending = 0, waiting = 0, rejected = 0, purchased = 0;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT status, confirmed, COUNT(*) FROM orders WHERE username=? GROUP BY status, confirmed")) {
            ps.setString(1, user);
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                String st = r.getString(1); int c = r.getInt(2); int n = r.getInt(3);
                if ("PENDING".equals(st)) pending += n;
                else if ("REJECTED".equals(st)) rejected += n;
                else if ("APPROVED".equals(st)) { if (c == 1) purchased += n; else waiting += n; }
            }
        }
        out.println("<div class='cards'>"
            + "<div class='card'><b>" + pending + "</b>Pending</div>"
            + "<div class='card'><b>" + waiting + "</b>Approved - confirm now</div>"
            + "<div class='card'><b>" + rejected + "</b>Rejected</div>"
            + "<div class='card'><b>" + purchased + "</b>Purchased</div></div>");

        out.println("<div class='box'><div style='text-align:right'><a class='btn' href='bill'>Generate Bill</a></div>"
            + "<h3>My requests</h3><table><tr><th>Order</th><th>Product</th><th>Price</th><th>Time</th>"
            + "<th>Status</th><th>Action</th></tr>");
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id, product_name, price, created_at, status, confirmed FROM orders WHERE username=? ORDER BY id DESC")) {
            ps.setString(1, user);
            ResultSet r = ps.executeQuery();
            boolean any = false;
            while (r.next()) {
                any = true;
                int id = r.getInt(1);
                String st = r.getString(5);
                int conf = r.getInt(6);
                String badge, action;
                if ("PENDING".equals(st)) { badge = "<span class='b p'>Pending</span>"; action = "Waiting for admin"; }
                else if ("REJECTED".equals(st)) { badge = "<span class='b r'>Rejected</span>"; action = "-"; }
                else if (conf == 1) { badge = "<span class='b d'>Purchased</span>"; action = "Done"; }
                else {
                    badge = "<span class='b a'>Approved</span>";
                    action = "<form method='post' action='myorders' style='display:inline'>"
                           + "<input type='hidden' name='id' value='" + id + "'>"
                           + "<button name='action' value='confirm'>Confirm Purchase</button></form>";
                }
                out.println("<tr><td>" + id + "</td><td>" + Util.esc(r.getString(2)) + "</td><td>Rs. "
                    + String.format("%.2f", r.getDouble(3)) + "</td><td>" + r.getTimestamp(4)
                    + "</td><td>" + badge + "</td><td>" + action + "</td></tr>");
            }
            if (!any) out.println("<tr><td colspan='6'>No requests yet. Go to Home and click Buy Now.</td></tr>");
        }
        out.println("</table></div>");
    }

    private void showBill(Connection con, PrintWriter out, String user) throws SQLException {
        out.println("<div class='box'><h2>Inventory Store - Bill</h2>"
            + "<p>Customer: <b>" + Util.esc(user) + "</b><br>Date: " + new java.util.Date() + "</p>"
            + "<table><tr><th>Product</th><th>Price</th><th>Qty</th><th>Amount</th></tr>");
        double total = 0;
        boolean any = false;
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT product_name, price, COUNT(*), SUM(price) FROM orders "
              + "WHERE username=? AND status='APPROVED' AND confirmed=1 GROUP BY product_name, price")) {
            ps.setString(1, user);
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                any = true;
                total += r.getDouble(4);
                out.println("<tr><td>" + Util.esc(r.getString(1)) + "</td><td>Rs. " + String.format("%.2f", r.getDouble(2))
                    + "</td><td>" + r.getInt(3) + "</td><td>Rs. " + String.format("%.2f", r.getDouble(4)) + "</td></tr>");
            }
        }
        if (!any) out.println("<tr><td colspan='4'>No confirmed purchases yet.</td></tr>");
        out.println("</table><div class='total'>Total Bill: Rs. " + String.format("%.2f", total) + "</div>"
            + "<p class='noprint' style='text-align:right'><button onclick='window.print()'>Print Bill</button></p></div>");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String user = who(req);
        if (user == null) { res.sendRedirect(req.getContextPath() + "/login"); return; }
        if ("confirm".equals(req.getParameter("action"))) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                    "UPDATE orders SET confirmed=1 WHERE id=? AND username=? AND status='APPROVED' AND confirmed=0")) {
                ps.setInt(1, Integer.parseInt(req.getParameter("id")));
                ps.setString(2, user);
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        res.sendRedirect(req.getContextPath() + "/myorders");
    }
}