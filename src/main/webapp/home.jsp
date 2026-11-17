<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, com.inventory.Util" %>
<%
    List<Map<String,Object>> products = (List<Map<String,Object>>) request.getAttribute("products");
    if(products == null) products = new ArrayList<>();
    boolean isAdmin = session.getAttribute("admin") != null;
    String who = "User";
    if (session.getAttribute("user") != null) who = session.getAttribute("user").toString();
    else if (isAdmin) who = session.getAttribute("admin").toString();
%>
<html>
<head>
<title>Home - Products</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
body{font-family:Arial,sans-serif;background:#f4f6f8;margin:0;color:#222}
.top{background:#2e7d32;color:#fff;padding:14px 24px;display:flex;justify-content:space-between;align-items:center;position:sticky;top:0;z-index:10}
.top a{color:#fff;text-decoration:none;margin-left:10px}
.grid{max-width:1100px;margin:20px auto;padding:0 16px;display:grid;grid-template-columns:repeat(auto-fill,minmax(220px,1fr));gap:18px}
.pc{background:#fff;border-radius:10px;box-shadow:0 2px 8px rgba(0,0,0,0.1);padding:14px;text-align:center;transition:0.2s}
.pc:hover{box-shadow:0 4px 16px rgba(0,0,0,0.2);transform:translateY(-2px)}
.pc img{width:100%;height:160px;object-fit:cover;border-radius:8px;border:1px solid #eee}
.noimg{height:160px;background:#eee;border-radius:8px;display:flex;align-items:center;justify-content:center;color:#888;font-size:14px}
.pc h4{margin:12px 0 6px 0;font-size:16px}
.price{font-weight:bold;color:#111;font-size:16px;margin:5px 0}
.ok{color:#2e7d32;font-size:13px;font-weight:bold}
.out{color:#d32f2f;font-size:13px;font-weight:bold}
.buy-btn{display:inline-block;margin-top:12px;background:#111;color:#fff !important;padding:9px 18px;text-decoration:none;border-radius:6px;font-weight:bold;font-size:14px}
.buy-btn:hover{background:#333}
.msg{padding:10px;margin:10px;border-radius:5px;text-align:center}
.err{background:#ffebee;color:#c62828}
.succ{background:#e8f5e9;color:#2e7d32}
</style>
</head>
<body>

<div class="top">
    <h3 style="margin:0">Inventory Store</h3>
    <div>
        Hi, <%= Util.esc(who) %> |
        <% if (isAdmin) { %>
            <a href="<%= request.getContextPath() %>/orders">Orders</a> |
            <a href="<%= request.getContextPath() %>/dashboard">Dashboard</a> |
        <% } else { %>
            <a href="<%= request.getContextPath() %>/myorders">My Orders</a> |
        <% } %>
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </div>
</div>

<% if (request.getAttribute("success") != null) { %>
    <div class="msg succ"><%= Util.esc(request.getAttribute("success").toString()) %></div>
<% } %>
<% if (request.getAttribute("error") != null) { %>
    <div class="msg err"><%= Util.esc(request.getAttribute("error").toString()) %></div>
<% } %>

<div class="grid">
<% for (Map<String,Object> p : products) {
    int stock = 0;
    try { stock = (Integer) p.get("stock"); } catch(Exception e){ stock = 0; }
    String name = (String) p.get("name");
    Object priceObj = p.get("price");
    double price = 0;
    if(priceObj instanceof Number) price = ((Number)priceObj).doubleValue();
    String img = (String) p.get("image");
    Object idObj = p.get("id");
%>
<div class="pc">
    <% if (img != null && !img.isEmpty()) { %>
        <img src="<%= request.getContextPath() %>/img?f=<%= Util.esc(img) %>" alt="<%= Util.esc(name) %>">
    <% } else { %>
        <div class="noimg">No Image</div>
    <% } %>

    <h4><%= Util.esc(name) %></h4>
    <div class="price">₹<%= String.format("%.2f", price) %></div>
    <div class="<%= stock > 0 ? "ok" : "out" %>"><%= stock > 0 ? stock + " in stock" : "Out of stock" %></div>

    <% if(stock > 0) { %>
        <a class="buy-btn" href="<%= request.getContextPath() %>/PurchaseServlet?productId=<%= idObj %>">Buy Now</a>
    <% } else { %>
        <span style="display:inline-block;margin-top:12px;background:#ccc;color:#666;padding:9px 18px;border-radius:6px;font-size:14px">Out of Stock</span>
    <% } %>
</div>
<% } %>
</div>

<% if (products.isEmpty()) { %>
    <div style="text-align:center;margin-top:50px;color:#666">
        <h3>No products available yet.</h3>
        <p>Please ask admin to add products.</p>
    </div>
<% } %>

</body>
</html>