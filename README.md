# thrift — The Campus Exchange

A complete, runnable, database-connected desktop application for college students to sell, donate, exchange, and request used academic materials and campus essentials.

Built with **Java Swing**, **AWT**, **JDBC**, and **MySQL 8.0**.

---

## 1. Project Overview & Features

- **Application Name**: `thrift`
- **Visual Identity**: Inspired by the modern college thrift exchange aesthetic with editorial Rose (`#7B1E3A`), Blush (`#F0D9E4`), Raspberry (`#A83258`), Cream (`#FFF5F7`), and Gold accents.
- **Student Authentication**:
  - **Signup**: Student name, college registration number, email, department, year of study, phone, and securely hashed passwords using **PBKDF2-HMAC-SHA256**.
  - **Login**: Login with either **Registration Number** or **Email Address**.
  - **Duplicate prevention**: Real database checks prevent duplicate registration numbers and emails.
- **Marketplace**:
  - Real-time catalog of available academic materials (Books, Electronics, Lab Equipment, Study Kits, Stationery, Clothing, etc.).
  - Search by item name or keywords.
  - Category filtering and listing type filtering (**SELL**, **DONATE**, **EXCHANGE**).
  - Price sorting (Low to High, High to Low, Newest).
  - Item detail modal with seller information.
- **Add Item**:
  - Complete form to publish items for sale, donation, or exchange.
  - Automatically associates listings with the logged-in student.
  - Validates prices for sale listings (must be positive) and auto-handles free items.
- **My Listings**:
  - Displays all items published by the current student.
  - Edit item details.
  - Update availability status (**AVAILABLE**, **RESERVED**, **SOLD**, **DONATED**).
  - Delete own listings safely.
  - Strict security prevents modifying other students' items.
- **Item Requests**:
  - Request available items from peers (prevents self-requests and duplicate active requests).
  - **Sent Requests**: Track status of your inquiries (**PENDING**, **APPROVED**, **REJECTED**, **COMPLETED**).
  - **Incoming Requests**: Accept/Reserve items or Decline requests in database transactions. Mark items as completed when handed over.
- **Profile Management**:
  - View and update student contact details (department, year, phone, name).

---

## 2. Technology Stack

- **Language**: Java 17+ (tested on Java 27)
- **GUI**: Java Swing & AWT (with optional FlatLaf theme for high DPI scaling)
- **Database Connectivity**: JDBC (`mysql-connector-j-8.3.0.jar`)
- **Database Engine**: MySQL Server 8.0
- **Security**: Cryptographic password hashing (PBKDF2-HMAC-SHA256 with 65,536 iterations and random 16-byte salt)
- **Architecture**: Model-View-DAO-Service pattern

---

## 3. Database Configuration

The application connects to MySQL on `localhost:3306` with database `campus_thrift`.

Create a local `db.properties` file in the project root by copying `db.properties.example`, then enter your MySQL credentials. `db.properties` is ignored by Git and must not be committed.

```properties
db.url=jdbc:mysql://localhost:3306/campus_thrift?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=your_mysql_password
db.driver=com.mysql.cj.jdbc.Driver
```

### Automatic Database Setup
On first launch, `DatabaseInitializer` automatically creates:
1. The `campus_thrift` database if it does not already exist.
2. The `users`, `items`, and `item_requests` tables.
3. Pre-seeded demo student accounts and sample college listings.

### Manual Database Setup (Alternative via MySQL Workbench)
If you prefer to run the SQL script manually:
1. Open **MySQL Workbench**.
2. Connect to your local MySQL instance.
3. Open the file `sql/campus_thrift.sql`.
4. Click the lightning bolt icon (Execute).

---

## 4. How to Compile and Run

### Option A: Using Windows Batch Files (Recommended)

1. **Compile**: Double-click or run `compile.bat` in Command Prompt / PowerShell.
   ```cmd
   compile.bat
   ```
2. **Run**: Double-click or run `run.bat`.
   ```cmd
   run.bat
   ```
3. **Run Automated Test Suite**:
   ```cmd
   test.bat
   ```

### Option B: Using Command Line (PowerShell / Command Prompt)

```powershell
# 1. Compile Java sources into bin directory
& "C:\Program Files\Java\jdk-27.0.1\bin\javac.exe" -encoding UTF-8 -d ".\bin" -cp ".\lib\*;." (Get-ChildItem -Path ".\src\main\java" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName)

# 2. Launch application
& "C:\Program Files\Java\jdk-27.0.1\bin\java.exe" -cp ".\bin;.\lib\*" Main

# 3. (Optional) Run automated verification test suite
& "C:\Program Files\Java\jdk-27.0.1\bin\java.exe" -cp ".\bin;.\lib\*" AppTest
```

---

## 5. Demo Student Accounts & Credentials

The database comes pre-populated with demo college student accounts for immediate testing:

| Student Name | Registration No | Email Address | Password | Department |
|---|---|---|---|---|
| **Aadhya Raman** | `2025501042` | `aadhya.raman@campus.edu` | `student123` | ECE |
| **Ishaan Kumar** | `2025502186` | `ishaan.k@campus.edu` | `student123` | CSE |
| **Meera S.** | `2025503077` | `meera.s@campus.edu` | `student123` | Mathematics |
| **Kavin Raj** | `2025500916` | `kavin.r@campus.edu` | `student123` | Mechanical |

*You can also create any brand new account by switching to the **Create Account** tab!*

---

## 6. Pre-Seeded Sample Listings

1. **Engineering Drawing Kit** — ₹300.00 (Study Materials, Sell)
2. **Scientific Calculator (Casio fx-991EX)** — ₹450.00 (Electronics, Sell)
3. **Lab Coat — Clean & Freshly Washed** — FREE (Lab Equipment, Donate)
4. **DSA in Java — Textbook & Problem Sets** — ₹280.00 (Books, Sell)
5. **Java: The Complete Reference (12th Ed)** — EXCHANGE (Books, Exchange)
6. **Record Notebook & Mechanics Study Notes** — FREE (Stationery, Donate)
7. **Warm LED Desk Lamp in Blush Pink** — ₹350.00 (Other, Sell)

---

## 7. Project Structure

```
PROJECT JAVA/
├── lib/
│   ├── mysql-connector-j-8.3.0.jar   (MySQL JDBC Driver)
│   └── flatlaf-3.4.1.jar             (Modern Swing Look & Feel)
├── sql/
│   └── campus_thrift.sql             (Complete MySQL schema & seed script)
├── src/
│   └── main/
│       ├── java/
│       │   ├── Main.java             (Application Main Entry Point)
│       │   ├── AppTest.java          (End-to-End Automated Test Runner)
│       │   ├── database/
│       │   │   ├── DatabaseConfig.java
│       │   │   ├── DatabaseConnection.java
│       │   │   └── DatabaseInitializer.java
│       │   ├── model/
│       │   │   ├── User.java
│       │   │   ├── Item.java
│       │   │   └── ItemRequest.java
│       │   ├── dao/
│       │   │   ├── UserDAO.java
│       │   │   ├── ItemDAO.java
│       │   │   └── ItemRequestDAO.java
│       │   ├── service/
│       │   │   ├── AuthenticationService.java
│       │   │   ├── ItemService.java
│       │   │   └── RequestService.java
│       │   ├── util/
│       │   │   ├── PasswordUtil.java
│       │   │   └── ValidationUtil.java
│       │   └── ui/
│       │       ├── UITheme.java
│       │       ├── AuthFrame.java
│       │       ├── MainFrame.java
│       │       ├── HomePanel.java
│       │       ├── MarketplacePanel.java
│       │       ├── AddItemPanel.java
│       │       ├── MyListingsPanel.java
│       │       ├── MyRequestsPanel.java
│       │       ├── ProfilePanel.java
│       │       └── ItemDetailDialog.java
├── compile.bat                       (One-click Windows build script)
├── run.bat                           (One-click Windows launch script)
├── test.bat                          (One-click automated test script)
├── pom.xml                           (Maven dependency management configuration)
├── db.properties.example             (Safe configuration template)
└── README.md                         (Documentation)
```
