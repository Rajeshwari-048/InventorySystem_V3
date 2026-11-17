SETUP
1. MySQL: run schema.sql (creates inventory_db and tables).
2. Edit user/password in src/main/java/com/inventory/DBConnection.java if needed.
3. Import into Eclipse, run on Tomcat 9 (Java 17 or 21 recommended).
4. (Already have data? run add_users_table.sql instead of schema.sql)
5. Open http://localhost:8080/InventorySystem/  -> login: admin / admin123 (change it!)
Uploaded images are saved in <your home folder>/inventory_uploads.

LOGIN
- Login page has a role dropdown: User or Admin.
- Users: click 'Create account' to register, then login -> read-only product Home page.
- Admin: full dashboard (add products, add stock, delete).
