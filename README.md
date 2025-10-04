# DVP Gift Center - Full Stack E-commerce Application

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/spring%20boot-3.2.0-green.svg)
![React](https://img.shields.io/badge/react-18.2.0-blue.svg)
![MySQL](https://img.shields.io/badge/mysql-8.0-blue.svg)

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [Database Schema](#database-schema)
- [Installation & Setup](#installation--setup)
- [API Documentation](#api-documentation)
- [Frontend Structure](#frontend-structure)
- [Security Implementation](#security-implementation)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)
 - [Migrations & Schema Alignment](#migrations--schema-alignment)
 - [Security Hardening Summary](#security-hardening-summary)
 - [Troubleshooting FK errors](#troubleshooting-fk-errors)

## 🌟 Overview

DVP Gift Center is a comprehensive full-stack e-commerce application designed to serve dual purposes:
1. **Backend for Internal POS Operations** - Supporting point-of-sale transactions
2. **Online E-commerce Platform** - Customer-facing website with admin management

The system provides a complete solution for gift retailers with inventory management, customer orders, and administrative controls.

## ✨ Features

### 🛍️ Customer Features
- **Product Browsing**: Browse products by categories with search and filtering
- **Product Details**: Detailed product information with high-quality images
- **Shopping Cart**: Add, remove, and manage cart items with quantity controls
- **Secure Checkout**: Protected checkout process with multiple payment options
- **User Authentication**: Registration, login, and profile management
- **Responsive Design**: Mobile-friendly interface with Bootstrap styling

### 👨‍💼 Admin Features
- **Dashboard Overview**: Real-time statistics and business metrics
- **Product Management**: Complete CRUD operations for online products
- **Inventory Control**: Stock quantity tracking and low-stock alerts
- **Order Management**: View and manage customer orders
- **User Management**: Admin user controls and permissions
- **Role-based Access**: Secure admin-only sections

### 🔒 Security Features
- **JWT Authentication**: Token-based secure authentication system
- **Role-based Authorization**: Customer and Admin role separation
- **Password Encryption**: BCrypt hashing for secure password storage
- **Protected Routes**: Frontend and backend route protection
- **CORS Configuration**: Secure cross-origin resource sharing

## 🛠️ Technology Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **MySQL 8.0** - Primary database
- **JWT (JSON Web Tokens)** - Authentication tokens
- **Maven** - Dependency management

### Frontend
- **React 18.2.0** - Frontend library
- **React Router 6** - Client-side routing
- **Bootstrap 5** - UI framework
- **React Bootstrap** - Bootstrap components for React
- **Axios** - HTTP client for API calls
- **React Icons** - Icon library
- **Context API** - State management

### Development Tools
- **VS Code** - IDE
- **Postman** - API testing
- **MySQL Workbench** - Database management
- **Git** - Version control

## 🏗️ System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   (React)       │───▶│ (Spring Boot)   │───▶│   (MySQL)       │
│                 │    │                 │    │                 │
│ - Customer UI   │    │ - REST APIs     │    │ - User Data     │
│ - Admin Panel   │    │ - Authentication│    │ - Products      │
│ - Shopping Cart │    │ - Business Logic│    │ - Orders        │
│ - Authentication│    │ - Data Access   │    │ - Transactions  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Request Flow
1. **User Request** → Frontend (React)
2. **API Call** → Backend (Spring Boot)
3. **Authentication** → JWT Validation
4. **Business Logic** → Service Layer
5. **Data Access** → Repository Layer
6. **Database** → MySQL Operations
7. **Response** → JSON Data
8. **UI Update** → React Components

## 🗄️ Database Schema

The application uses a comprehensive database schema with 15+ tables:

### Core Tables
- **users** - User accounts (customers and admins)
- **user_roles** - Role assignments
- **categories** - Product categories
- **products** - Physical inventory products
- **online_products** - E-commerce specific products
- **transactions** - POS transactions
- **online_orders** - E-commerce orders
- **order_items** - Order line items

### Key Relationships
```sql
users (1) ←→ (many) online_orders
online_orders (1) ←→ (many) order_items
categories (1) ←→ (many) products
products (1) ←→ (1) online_products
```

### Sample Data
The database includes comprehensive sample data for:
- Default admin user (admin/admin123)
- Product categories (Gift Cards, Electronics, etc.)
- Sample products with pricing and inventory
- Test transactions and orders

## 🚀 Installation & Setup

### Prerequisites
- **Java 17+** installed
- **Node.js 16+** and npm installed
- **MySQL 8.0+** running
- **Git** for version control

### Backend Setup

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd dvp-gift-center
   ```

2. **Database Configuration**
   ```sql
   CREATE DATABASE dvp_gift_center;
   ```
   
   Import the schema:
   ```bash
   mysql -u root -p dvp_gift_center < database/dvp_gift_center_schema.sql
   ```

3. **Configure Application Properties**
    ```properties
    # backend/src/main/resources/application.properties

    # Database Configuration (auto-create DB if missing)
    spring.datasource.url=jdbc:mysql://localhost:3306/dvp_gift_center?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
    spring.datasource.username=root
    spring.datasource.password=your_password

    # JPA DDL disabled (schema is provided via SQL file)
    spring.jpa.hibernate.ddl-auto=none

    # JWT Configuration
    jwt.secret=your-secret-key
    jwt.expiration=86400000

    # Server Configuration
    server.port=8080
    server.servlet.context-path=/api

    # Business Config
    # Tax rate used during checkout computations (5% example)
    app.tax.rate=0.05
    ```

4. **Run Backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

   Backend will be available at: `http://localhost:8080`

    Note: The backend serves APIs under the servlet context-path `/api`, so your effective base URL is `http://localhost:8080/api`.

### Frontend Setup

1. **Install Dependencies**
   ```bash
   cd frontend
   npm install
   ```

2. **Configure API Base URL**
   ```javascript
   // src/contexts/AuthContext.js
   const API_BASE_URL = 'http://localhost:8080';
   ```

3. **Run Frontend**
   ```bash
   npm start
   ```

   Frontend will be available at: `http://localhost:3000`

### Default Login Credentials
- **Admin User**: `admin` / `admin123`
- **Test Customer**: `john_doe` / `password123`

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication Endpoints

#### Register User
```http
POST /auth/register
Content-Type: application/json

{
    "username": "newuser",
    "password": "password",
    "email": "user@example.com",
    "fullName": "Full Name"
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "admin123"
}

Response:
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "admin",
    "roles": ["ROLE_ADMIN"]
}
```

### Public Product Endpoints

#### Get All Products
```http
GET /online/products
```

#### Get Product by ID
```http
GET /online/products/{id}
```

#### Search Products
```http
GET /online/products/search?query=gift&category=Gift Cards
```

### Protected Customer Endpoints

#### Checkout Order
```http
POST /online/checkout
Authorization: Bearer {token}
Content-Type: application/json

{
    "items": [
        {
            "productId": 1,
            "quantity": 2,
            "price": 50.00
        }
    ],
    "shippingAddress": "123 Main St, City, State",
    "paymentMethod": "CREDIT_CARD",
    "totalAmount": 108.50
}
```

### Admin Endpoints

#### Get All Products (Admin)
```http
GET /admin/products?includeArchived=false&availability=all|online|offline
Authorization: Bearer {admin-token}
```

#### Create Product
```http
POST /admin/products
Authorization: Bearer {admin-token}
Content-Type: application/json

{
    "name": "New Gift Card",
    "description": "Perfect for any occasion",
    "price": 25.00,
    "category": "Gift Cards",
    "stockQuantity": 100,
    "imageUrl": "https://example.com/image.jpg"
}
```

#### Update Product
```http
PUT /admin/products/{id}
Authorization: Bearer {admin-token}
Content-Type: application/json
```

#### Restore Product (from archived)
```http
PUT /admin/products/{id}/restore
Authorization: Bearer {admin-token}
```

#### Delete Product
```http
DELETE /admin/products/{id}?hard=false
Authorization: Bearer {admin-token}
```

Notes:
- Default deletion is soft (the product is archived: is_active=false). It will disappear from online listings but remains in the catalog for recovery and historical references.
- Use `?hard=true` for a hard delete. If the product is referenced by transaction/order items, the API responds with `409 Conflict` to protect referential integrity.

## 🎨 Frontend Structure

```
frontend/src/
├── components/              # Reusable components
│   ├── Navbar.js           # Navigation component
│   ├── Footer.js           # Footer component
│   ├── ProtectedRoute.js   # Route protection
│   └── AdminLayout.js      # Admin panel layout
├── contexts/               # React contexts
│   ├── AuthContext.js      # Authentication state
│   └── CartContext.js      # Shopping cart state
├── pages/                  # Page components
│   ├── Home.js            # Homepage
│   ├── Products.js        # Product listing
│   ├── ProductDetail.js   # Product details
│   ├── Cart.js            # Shopping cart
│   ├── Checkout.js        # Checkout process
│   ├── Login.js           # User login
│   ├── Register.js        # User registration
│   ├── AdminDashboard.js  # Admin overview
│   └── AdminProducts.js   # Product management
├── styles/                 # Custom styles
│   └── custom.css         # Application styling
└── App.js                 # Main application component
```

### Key Frontend Features

#### Authentication Context
```javascript
// Provides authentication state and methods
const AuthContext = {
    currentUser: null,
    login: (credentials) => Promise,
    register: (userData) => Promise,
    logout: () => void,
    getAuthToken: () => string
}
```

#### Cart Context
```javascript
// Manages shopping cart state
const CartContext = {
    cartItems: [],
    addToCart: (product, quantity) => void,
    removeFromCart: (productId) => void,
    updateQuantity: (productId, quantity) => void,
    clearCart: () => void,
    getCartTotal: () => number
}
```

#### Protected Routes
```javascript
// Route protection based on authentication and roles
<ProtectedRoute requiredRole="admin">
    <AdminDashboard />
</ProtectedRoute>
```

## 🔐 Security Implementation

### JWT Authentication Flow

1. **User Login** → Credentials validation
2. **Token Generation** → JWT with user claims
3. **Token Storage** → Local storage (frontend)
4. **Request Authentication** → Bearer token in headers
5. **Token Validation** → Spring Security filter
6. **Access Control** → Role-based authorization

### Security Hardening Summary

The security rules are intentionally strict and mapped to the servlet path (without repeating the `/api` context-path in matchers):

- Public:
    - `/online/products`, `/online/products/**`
    - `/online/categories`, `/online/categories/**`
    - `/auth/**` (login/register/refresh as applicable)
    - `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`
- Customer-only:
    - `/online/cart/**`
    - `/online/checkout`, `/online/checkout/**`
    - `/online/orders/**`
- Admin-only:
    - `/admin/**`
- All others: authenticated

Your application sets `server.servlet.context-path=/api`, so the effective URLs are `/api/...` at runtime. Do not prefix `/api` in the security matchers.

Roles are mapped as `ROLE_<UPPERCASE>`. For an admin row with role `admin`, the authority becomes `ROLE_ADMIN` and satisfies `hasRole("ADMIN")`.

## Schema & Alignment (no migrations)

This project does not use runtime database migrations. Hibernate DDL is disabled (`spring.jpa.hibernate.ddl-auto=none`) and the schema is provided via the SQL file `database/dvp_gift_center_schema.sql`.

Alignment notes:
- The schema assumes identifiers (PK/FK) are aligned in MySQL (e.g., signedness compatibility) to avoid FK errors.
- If you see errors such as `... referenced column ... are incompatible`, ensure your columns match the provided schema (BIGINT columns should be consistently defined).
- You can import the full schema with sample data using the SQL file, or start with an empty database; the configured JDBC URL includes `createDatabaseIfNotExist=true` to create the database automatically.

## Troubleshooting FK errors

Symptoms:

- App fails to start with logs similar to:
    - `Referencing column 'transaction_id' and referenced column 'transaction_id' ... are incompatible.`

Fix:

1. Ensure you are running with the provided Flyway configuration and that `ddl-auto` is `none`.
2. Verify the database user has ALTER privileges.
3. Check that no legacy negative IDs exist in your data (they shouldn’t for identifiers).
4. If you started with an old schema, the V1 migration will align the columns to UNSIGNED and the app will start cleanly.

### Security Configuration

#### Backend Security
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

#### CORS Configuration
```java
@CrossOrigin(origins = "http://localhost:3000")
```

### Password Security
- **BCrypt Hashing** - Industry-standard password encryption
- **Salt Rounds** - Additional security against rainbow table attacks
- **Password Validation** - Minimum length and complexity requirements

### Role-based Access Control
## Currency & Tax

- Currency: All prices and totals are presented in Sri Lankan Rupees (LKR). The frontend uses a shared formatter at `frontend/src/utils/currency.js` which wraps `Intl.NumberFormat('en-LK', { style: 'currency', currency: 'LKR' })`.
- Tax: Checkout totals are computed on the server. The tax rate is configurable via `app.tax.rate` (default 0.05 for 5%). The backend recomputes line totals from authoritative product prices, applies tax, and persists `total_amount`, `tax_amount`, and `net_amount` in the transactions table. Client-sent totals are validated and not trusted.
- **ROLE_CUSTOMER** - Standard user permissions
- **ROLE_ADMIN** - Administrative permissions
- **Method-level Security** - `@PreAuthorize` annotations
- **Route Protection** - Frontend and backend route guards

## 🧪 Testing

### Backend Testing
```bash
cd backend
mvn test
```

### API Testing with Postman
1. Import the provided Postman collection
2. Set environment variables (base_url, token)
3. Test all endpoints with sample data

### Frontend Testing
```bash
cd frontend
npm test
```

### Manual Testing Checklist
- [ ] User registration and login
- [ ] Product browsing and search
- [ ] Shopping cart operations
- [ ] Checkout process
- [ ] Admin product management
- [ ] Role-based access control
- [ ] Responsive design on mobile

## 🚀 Deployment

### Production Build

#### Backend
```bash
cd backend
mvn clean package
java -jar target/dvp-gift-center-backend-1.0.0.jar
```

#### Frontend
```bash
cd frontend
npm run build
# Serve the build folder with nginx or Apache
```

### Environment Variables
```bash
# Backend
SPRING_PROFILES_ACTIVE=production
DATABASE_URL=jdbc:mysql://prod-db:3306/dvp_gift_center
JWT_SECRET=your-production-secret
JWT_EXPIRATION=86400000

# Frontend
REACT_APP_API_URL=https://api.dvpgiftcenter.com
```

### Docker Deployment (Optional)
```dockerfile
# Backend Dockerfile
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Frontend Dockerfile
FROM node:16-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]
```

## 📊 Database Design Details

### Entity Relationships
```
User ||--o{ OnlineOrder
OnlineOrder ||--o{ OrderItem
Product ||--o| OnlineProduct
Category ||--o{ Product
User ||--o{ Transaction
Product ||--o{ TransactionItem
```

### Indexes for Performance
```sql
-- Search optimization
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_online_products_category ON online_products(category);

-- Order queries
CREATE INDEX idx_orders_user ON online_orders(user_id);
CREATE INDEX idx_orders_date ON online_orders(order_date);

-- Authentication
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
```

## 🔧 Configuration Details

### Application Properties
```properties
# Database Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Security Configuration
spring.security.user.password=default_admin_password

# Logging Configuration
logging.level.com.dvpgiftcenter=INFO
logging.file.name=logs/application.log

# Server Configuration
server.error.include-message=always
server.error.include-binding-errors=always
```

### Frontend Environment Configuration
```javascript
// .env files for different environments
REACT_APP_API_URL=http://localhost:8080
REACT_APP_ENVIRONMENT=development
REACT_APP_VERSION=1.0.0
```

## 🐛 Troubleshooting

### Common Issues

#### Database Connection
```
Error: Could not connect to database
Solution: Check MySQL service and credentials in application.properties
```

#### JWT Token Expiration
```
Error: 401 Unauthorized
Solution: Re-login to get a fresh token
```

#### CORS Errors
```
Error: Access blocked by CORS policy
Solution: Verify @CrossOrigin annotation on controllers
```

#### Frontend Build Issues
```
Error: Module not found
Solution: Delete node_modules and run npm install
```

## 🤝 Contributing

### Development Setup
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Standards
- **Java**: Follow Spring Boot conventions
- **JavaScript**: Use ES6+ features
- **SQL**: Use consistent naming conventions
- **Documentation**: Update README for new features

### Commit Message Format
```
type(scope): description

Examples:
feat(auth): add password reset functionality
fix(cart): resolve quantity update issue
docs(api): update endpoint documentation
```

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Development Team** - Initial work and implementation
- **Contributors** - See [CONTRIBUTORS.md](CONTRIBUTORS.md) for the list of contributors

## 🙏 Acknowledgments

- Spring Boot community for excellent documentation
- React team for the powerful frontend framework
- Bootstrap team for responsive design components
- MySQL team for reliable database system
- All open-source contributors who made this project possible

---

## 📞 Support

For support, email support@dvpgiftcenter.com or create an issue in the repository.

**Happy Shopping! 🎁**