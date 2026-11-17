package com.inventory;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

/** Handles: add product (with image), add stock (purchase), delete product. */
@WebServlet("/product")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024)
public class ProductServlet extends HttpServlet {
    static final Path UPLOAD_DIR = Paths.get(System.getProperty("user.home"), "inventory_uploads");

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = req.getParameter("action");
        String msg;
        try {
            if ("add".equals(action)) msg = addProduct(req);
            else if ("stock".equals(action)) msg = addStock(req);
            else if ("delete".equals(action)) msg = deleteProduct(req);
            else msg = "Unknown action";
        } catch (NumberFormatException e) {
            msg = "Error: please enter valid numbers.";
        } catch (Exception e) {
            msg = "Error: " + e.getMessage();
        }
        req.getSession().setAttribute("msg", msg);
        res.sendRedirect(req.getContextPath() + "/dashboard");
    }

    private String addProduct(HttpServletRequest req) throws Exception {
        String name = req.getParameter("name").trim();
        double price = Double.parseDouble(req.getParameter("price").trim());
        int qty = Integer.parseInt(req.getParameter("quantity").trim());
        double cost = Double.parseDouble(req.getParameter("unit_cost").trim());
        if (name.isEmpty() || price < 0 || qty < 0 || cost < 0) return "Error: invalid values.";
        String image = saveImage(req.getPart("image"));
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO products(name,price,image,stock) VALUES(?,?,?,0)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name); ps.setDouble(2, price); ps.setString(3, image);
                ps.executeUpdate();
                try (ResultSet k = ps.getGeneratedKeys()) {
                    k.next();
                    if (qty > 0) purchase(c, k.getInt(1), qty, cost);
                }
                c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        }
        return "Product added: " + name;
    }

    private String addStock(HttpServletRequest req) throws Exception {
        int id = Integer.parseInt(req.getParameter("id"));
        int qty = Integer.parseInt(req.getParameter("quantity").trim());
        double cost = Double.parseDouble(req.getParameter("unit_cost").trim());
        if (qty <= 0 || cost < 0) return "Error: quantity must be more than 0.";
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try { purchase(c, id, qty, cost); c.commit(); }
            catch (SQLException e) { c.rollback(); throw e; }
        }
        return "Stock added: +" + qty;
    }

    private void purchase(Connection c, int productId, int qty, double cost) throws SQLException {
        try (PreparedStatement a = c.prepareStatement("INSERT INTO purchases(product_id,quantity,unit_cost) VALUES(?,?,?)");
             PreparedStatement b = c.prepareStatement("UPDATE products SET stock = stock + ? WHERE id=?")) {
            a.setInt(1, productId); a.setInt(2, qty); a.setDouble(3, cost); a.executeUpdate();
            b.setInt(1, qty); b.setInt(2, productId); b.executeUpdate();
        }
    }

    private String deleteProduct(HttpServletRequest req) throws Exception {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM products WHERE id=?")) {
            ps.setInt(1, Integer.parseInt(req.getParameter("id")));
            ps.executeUpdate();
        }
        return "Product deleted.";
    }

    /** Saves an uploaded image (jpg/png/gif/webp only) and returns the stored file name, or null. */
    private String saveImage(Part part) throws IOException {
        if (part == null || part.getSize() == 0) return null;
        String orig = part.getSubmittedFileName() == null ? "" : part.getSubmittedFileName().toLowerCase();
        String ext = orig.contains(".") ? orig.substring(orig.lastIndexOf('.') + 1) : "";
        if (!ext.matches("jpg|jpeg|png|gif|webp")) throw new IOException("Only jpg, png, gif or webp images are allowed.");
        Files.createDirectories(UPLOAD_DIR);
        String file = UUID.randomUUID() + "." + ext;
        try (InputStream in = part.getInputStream()) { Files.copy(in, UPLOAD_DIR.resolve(file)); }
        return file;
    }
}
