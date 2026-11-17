<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.inventory.Util" %>
<html><head><title>Register</title><style>body{font-family:Arial,sans-serif;background:#f4f6f8;margin:0;color:#222}
.lg{max-width:340px;margin:90px auto;background:#fff;padding:24px;border-radius:8px;box-shadow:0 1px 6px #bbb;text-align:center}
.lg input,.lg select{width:92%;padding:8px;margin:5px 0}button{padding:9px 16px;background:#2e7d32;color:#fff;border:0;border-radius:4px;cursor:pointer}
.msg{padding:10px;background:#e8f5e9;border-radius:6px;margin-bottom:10px}.err{background:#ffebee}a{color:#2e7d32}</style></head><body>
<div class="lg"><h2>Create User Account</h2>
<% if (request.getAttribute("error") != null) { %><div class="msg err"><%= Util.esc(request.getAttribute("error")) %></div><% } %>
<form method="post" action="register">
<input name="username" placeholder="Username" required>
<input name="password" type="password" placeholder="Password (min 6)" required>
<input name="confirm" type="password" placeholder="Confirm password" required><br><br>
<button type="submit">Register</button></form>
<p><a href="login">Back to login</a></p></div></body></html>
