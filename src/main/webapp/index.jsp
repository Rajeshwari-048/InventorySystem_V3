<%
if (session.getAttribute("admin") != null) response.sendRedirect("dashboard");
else if (session.getAttribute("user") != null) response.sendRedirect("home");
else response.sendRedirect("login");
%>
