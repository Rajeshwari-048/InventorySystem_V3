<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head><title>Success</title>
<style>
body{font-family:Arial;background:#e8f5e9;display:flex;justify-content:center;align-items:center;height:100vh;margin:0}
.box{background:white;padding:40px;border-radius:15px;text-align:center;box-shadow:0 5px 15px rgba(0,0,0,0.2)}
</style>
</head>
<body>
<div class="box">
<h2 style="color:green;">✅ Order Successful!</h2>
<p>Product: <b>${pname}</b></p>
<p>Quantity: <b>${qty}</b></p>
<p>Your order has been placed!</p>
<br><br>
<a href="UserHomeServlet" style="background:#007bff;color:white;padding:12px 25px;text-decoration:none;border-radius:8px;">Continue Shopping</a>
</div>
</body>
</html>