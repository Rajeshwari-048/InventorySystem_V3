<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, java.sql.*, com.inventory.DBConnection, com.inventory.Util" %>
<%
    // Session check
    if(session.getAttribute("user") == null){
        response.sendRedirect("login");
        return;
    }

    List<Map<String,Object>> products = (List<Map<String,Object>>) request.getAttribute("products");
    List<Map<String,Object>> orders = new ArrayList<>();
    int pendingCount = 0;

    // If directly accessed, load from DB
    if(products == null){
        products = new ArrayList<>();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            con = DBConnection.getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM products");
            while(rs.next()){
                Map<String,Object> m = new HashMap<>();
                m.put("id", rs.getInt("id"));
                m.put("name", rs.getString("name"));
                m.put("category", rs.getString("category"));
                m.put("stock", rs.getInt("stock"));
                m.put("price", rs.getDouble("price"));
                m.put("purchased", rs.getInt("purchased"));
                m.put("spent", rs.getDouble("spent"));
                products.add(m);
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try{ if(rs!=null) rs.close(); }catch(Exception e){}
            try{ if(st!=null) st.close(); }catch(Exception e){}
            try{ if(con!=null) con.close(); }catch(Exception e){}
        }
    }

    // Load recent orders (last 10) and pending count
    try (Connection con2 = DBConnection.getConnection();
         Statement st2 = con2.createStatement()) {
        ResultSet r2 = st2.executeQuery(
            "SELECT id, username, product_name, price, created_at, status FROM orders ORDER BY id DESC LIMIT 10");
        while(r2.next()){
            Map<String,Object> o = new HashMap<>();
            o.put("id", r2.getInt("id"));
            o.put("username", r2.getString("username"));
            o.put("product", r2.getString("product_name"));
            o.put("price", r2.getDouble("price"));
            o.put("time", r2.getTimestamp("created_at"));
            o.put("status", r2.getString("status"));
            orders.add(o);
        }
        ResultSet r3 = st2.executeQuery("SELECT COUNT(*) FROM orders WHERE status='PENDING'");
        if(r3.next()) pendingCount = r3.getInt(1);
    } catch(Exception e){
        e.printStackTrace();
    }

    int totalStock = 0, totalPurchased = 0;
    double stockValue = 0, totalSpent = 0;
    for(Map<String,Object> p : products){
        totalStock += (Integer) p.get("stock");
        totalPurchased += (Integer) p.get("purchased");
        stockValue += (Integer) p.get("stock") * (Double) p.get("price");
        totalSpent += (Double) p.get("spent");
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Dashboard - Inventory System</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<style>
body{background:#f5f6fa;}
.card{border-radius:15px; box-shadow:0 4px 12px rgba(0,0,0,0.08); border:none;}
</style>
</head>
<body>
<nav class="navbar navbar-dark bg-dark px-4">
  <span class="navbar-brand">📦 Inventory System</span>
  <div>
    <span class="text-white me-3">Welcome, <%= session.getAttribute("user") %></span>
    <a href="orders" class="btn btn-sm btn-success me-2">Orders
      <% if(pendingCount > 0){ %><span class="badge bg-warning text-dark"><%= pendingCount %></span><% } %>
    </a>
    <a href="logout" class="btn btn-sm btn-danger">Logout</a>
  </div>
</nav>

<div class="container mt-4">
  <h3 class="mb-4">Dashboard</h3>

  <div class="row g-3 mb-4">
    <div class="col-md-3">
      <div class="card p-3 bg-primary text-white">
        <h6>Total Stock</h6>
        <h2><%= totalStock %></h2>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card p-3 bg-success text-white">
        <h6>Total Purchased</h6>
        <h2><%= totalPurchased %></h2>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card p-3 bg-warning text-dark">
        <h6>Stock Value</h6>
        <h2>₹ <%= String.format("%.0f", stockValue) %></h2>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card p-3 bg-info text-white">
        <h6>Total Spent</h6>
        <h2>₹ <%= String.format("%.0f", totalSpent) %></h2>
      </div>
    </div>
  </div>

  <div class="card p-3 mb-4">
    <h5>Products (<%= products.size() %>)</h5>
    <table class="table table-hover mt-3">
      <thead><tr><th>ID</th><th>Name</th><th>Category</th><th>Stock</th><th>Price</th><th>Purchased</th><th>Spent</th></tr></thead>
      <tbody>
      <% for(Map<String,Object> p : products){ %>
        <tr>
          <td><%= p.get("id") %></td>
          <td><%= Util.esc(p.get("name")) %></td>
          <td><%= Util.esc(p.get("category")) %></td>
          <td><%= p.get("stock") %></td>
          <td>₹ <%= p.get("price") %></td>
          <td><%= p.get("purchased") %></td>
          <td>₹ <%= p.get("spent") %></td>
        </tr>
      <% }
         if(products.isEmpty()){
      %>
        <tr><td colspan="7" class="text-center text-muted">No products found. Add products in DB.</td></tr>
      <% } %>
      </tbody>
    </table>
  </div>

  <div class="card p-3 mb-5">
    <div class="d-flex justify-content-between align-items-center">
      <h5 class="mb-0">Recent Orders (<%= pendingCount %> pending)</h5>
      <a href="orders" class="btn btn-sm btn-success">Approve / Reject</a>
    </div>
    <table class="table table-hover mt-3">
      <thead><tr><th>Order</th><th>User</th><th>Product</th><th>Price</th><th>Time</th><th>Status</th></tr></thead>
      <tbody>
      <% for(Map<String,Object> o : orders){
           String stt = String.valueOf(o.get("status"));
           String badge = "APPROVED".equals(stt) ? "bg-success" : ("REJECTED".equals(stt) ? "bg-danger" : "bg-warning text-dark");
      %>
        <tr>
          <td><%= o.get("id") %></td>
          <td><%= Util.esc(o.get("username")) %></td>
          <td><%= Util.esc(o.get("product")) %></td>
          <td>₹ <%= o.get("price") %></td>
          <td><%= o.get("time") %></td>
          <td><span class="badge <%= badge %>"><%= Util.esc(stt) %></span></td>
        </tr>
      <% }
         if(orders.isEmpty()){
      %>
        <tr><td colspan="6" class="text-center text-muted">No orders yet.</td></tr>
      <% } %>
      </tbody>
    </table>
  </div>
</div>
</body>
</html>