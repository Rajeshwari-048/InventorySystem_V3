package com.inventory;
import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, res);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String u = req.getParameter("username") == null ? "" : req.getParameter("username").trim();
        String p = req.getParameter("password") == null ? "" : req.getParameter("password");
        String err = null;
        if (!u.matches("[A-Za-z0-9_]{3,30}")) err = "Username: 3-30 letters, numbers or _ only.";
        else if (p.length() < 6) err = "Password must be at least 6 characters.";
        else if (!p.equals(req.getParameter("confirm"))) err = "Passwords do not match.";
        if (err == null) {
            String salt = Util.newSalt();
            try (Connection c = DBConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement("INSERT INTO users(username,password_hash,salt) VALUES(?,?,?)")) {
                ps.setString(1, u); ps.setString(2, Util.hash(p, salt)); ps.setString(3, salt);
                ps.executeUpdate();
                req.setAttribute("success", "Account created. Please login.");
                req.getRequestDispatcher("/login.jsp").forward(req, res);
                return;
            } catch (SQLIntegrityConstraintViolationException e) {
                err = "That username is already taken.";
            } catch (SQLException e) {
                err = "Database error: " + e.getMessage();
            }
        }
        req.setAttribute("error", err);
        req.getRequestDispatcher("/register.jsp").forward(req, res);
    }
}
