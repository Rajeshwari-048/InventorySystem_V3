# Inventory and Stock Management System

## Project Overview

The **Inventory and Stock Management System** is a Java-based web application developed to manage products, inventory details, stock quantities, and product information efficiently.

The application provides two types of users: Admin and User. The Admin has permission to manage products, update stock quantities, and delete products. Users can register, log in, and view available products.

The system uses Java Servlets, JDBC, MySQL, HTML, CSS, and JavaScript to provide an organized inventory management solution.

## Objectives

- To manage product information efficiently.
- To maintain accurate stock quantities.
- To simplify inventory management.
- To provide separate access for Admin and Users.
- To reduce manual inventory management work.
- To store product details securely in a MySQL database.

## Technologies Used

| Component | Technology |
|---|---|
| Frontend | HTML, CSS, JavaScript |
| Backend | Java |
| Web Technology | Java Servlets, JSP |
| Database | MySQL |
| Database Connectivity | JDBC |
| Server | Apache Tomcat 9 |
| IDE | Eclipse / Visual Studio Code |
| JDK | Java 17 or Java 21 |

## Main Modules

### 1. Admin Module

- Admin login.
- Access admin dashboard.
- Add new products.
- View available products.
- Update stock quantities.
- Delete products.
- Upload product images.
- Manage inventory information.

### 2. User Module

- User registration.
- User login.
- View available products.
- View product prices.
- View product stock availability.
- Access a read-only product dashboard.

### 3. Product Management Module

- Store product details.
- Manage product names.
- Maintain product prices.
- Store product images.
- Track available stock quantities.

### 4. Database Management Module

- Store admin information.
- Store user information.
- Store product details.
- Maintain purchase records.
- Retrieve inventory information using JDBC.

## System Workflow

1. The user opens the application.
2. The login page displays Admin and User role options.
3. Admin logs in using admin credentials.
4. Admin manages products and stock.
5. New products can be added with images and prices.
6. Users create an account and log in.
7. Users can view available products and their stock details.
8. Product information is stored in the MySQL database.

## Database Structure

Database Name: `inventory_db`

The project includes the following database tables:

| Table Name | Purpose |
|---|---|
| admins | Stores administrator login information |
| users | Stores registered user information |
| products | Stores product name, price, image, and stock |
| purchases | Stores product purchase details and quantities |

## Project Structure

```text
InventorySystem/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── inventory/
│       │           ├── DBConnection.java
│       │           ├── LoginServlet.java
│       │           ├── DashboardServlet.java
│       │           └── ProductServlet.java
│       │
│       └── webapp/
│           ├── WEB-INF/
│           ├── css/
│           ├── js/
│           ├── images/
│           └── JSP / HTML pages
│
├── schema.sql
├── add_users_table.sql
├── README.md
└── .project
```

*Note: This is a representative structure. Refer to the actual source folders for the complete list of files.*

## Software Requirements

- Windows 10 / Windows 11
- Java JDK 17 or above
- MySQL Server 8.0
- Apache Tomcat 9
- Eclipse IDE or Visual Studio Code
- MySQL JDBC Driver
- Web browser

## Installation and Setup

### Step 1: Clone the Repository

Open the terminal and execute:

```bash
git clone https://github.com/Rajeshwari-048/InventorySystem_V3.git
```

### Step 2: Create the Database

1. Open MySQL Workbench or MySQL Command Line.
2. Navigate to the project folder.
3. Execute the `schema.sql` file.

```sql
SOURCE path/to/schema.sql;
```

This creates the `inventory_db` database and its tables.

**Important:** The `schema.sql` script drops and recreates existing tables. If you already have data that you want to preserve, do not run it again. Instead, execute:

```sql
SOURCE path/to/add_users_table.sql;
```

### Step 3: Configure Database Connection

Open:

`src/main/java/com/inventory/DBConnection.java`

Update the MySQL username and password according to your system.

Example:

```java
String url = "jdbc:mysql://localhost:3306/inventory_db";
String username = "root";
String password = "your_mysql_password";
```

Make sure the MySQL JDBC driver is available in the project.

### Step 4: Configure Apache Tomcat

1. Open Eclipse or Visual Studio Code.
2. Import or open the InventorySystem project.
3. Configure Apache Tomcat 9.
4. Select Java JDK 17 or above.
5. Add the project to the Tomcat server.
6. Start the server.

### Step 5: Run the Application

Open your browser and visit:

```text
http://localhost:8080/InventorySystem/
```

## Default Admin Login

| Field | Details |
|---|---|
| Username | admin |
| Password | admin123 |
| Role | Admin |

**Note:** The default administrator account is created automatically on the first application run. Change the default password before production use.

## Security Features

- Separate Admin and User login.
- Password hashing and salt.
- Role-based access.
- Session-based user authentication.
- Database connectivity using JDBC.

## Advantages

- Easy product management.
- Efficient stock tracking.
- Reduces manual work.
- Separate Admin and User access.
- Centralized database management.
- Easy access to product information.

## Future Enhancements

- Generate inventory reports.
- Add low-stock notifications.
- Add product search and filtering.
- Implement barcode scanning.
- Add sales and purchase reports.
- Improve dashboard analytics.
- Add automated stock alerts.

## Conclusion

The Inventory and Stock Management System provides a simple and effective solution for managing inventory and product information. It helps administrators manage products, update stock quantities, and maintain product records while allowing users to view available products.

This project demonstrates the practical implementation of Java Web Development using Servlets, JSP, JDBC, MySQL, and Apache Tomcat.

## Author

**Rajeshwari P B**

Master of Computer Applications (MCA)

GitHub: [Rajeshwari-048](https://github.com/Rajeshwari-048)

## GitHub Repository

[Inventory and Stock Management System – GitHub](https://github.com/Rajeshwari-048/InventorySystem_V3)
