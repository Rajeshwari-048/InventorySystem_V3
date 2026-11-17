package com.inventory;
import java.io.IOException;
import java.nio.file.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Serves uploaded images (admin-only, thanks to AuthFilter). */
@WebServlet("/img")
public class ImageServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String f = req.getParameter("f");
        if (f == null) { res.sendError(404); return; }
        Path p = ProductServlet.UPLOAD_DIR.resolve(Paths.get(f).getFileName().toString());
        if (!Files.exists(p)) { res.sendError(404); return; }
        String type = Files.probeContentType(p);
        res.setContentType(type != null ? type : "application/octet-stream");
        Files.copy(p, res.getOutputStream());
    }
}
