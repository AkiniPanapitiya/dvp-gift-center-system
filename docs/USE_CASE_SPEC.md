# Use Case Specification (Admin, Cashier, Customer)

Date: 2025-10-09  
Version: 1.0  
Scope: Core operational features for Admin, Cashier (POS), and Registered Customer roles. Guest-only browsing use cases are excluded unless they overlap with registered customer flows.

---
## 1. Actors
- **Admin**: Manages catalog, categories, users, orders, system configuration.
- **Cashier**: Operates POS, processes in‑store sales, reprints receipts, reviews POS transactions.
- **Customer**: Registered end-user; browses products, manages cart, places and tracks orders.
- **Payment Gateway (External System)**: Handles payment authorization (placeholder / future integration).
- **Inventory System (Internal Service / DB)**: Maintains stock counts (JPA entities & transactional logic).

---
## 2. Use Case Diagram (PlantUML)
```plantuml
@startuml
left to right direction
actor Admin
actor Cashier
actor Customer
rectangle "Gift Center System" {
  (Manage Categories) as UC_ManageCategories
  (Manage Products) as UC_ManageProducts
  (Manage Users) as UC_ManageUsers
  (Configure System Settings) as UC_Config
  (View All Orders) as UC_ViewAllOrders
  (Update Order Status) as UC_UpdateOrderStatus
  (Process POS Sale) as UC_ProcessPOSSale
  (Search Product (Barcode/SKU)) as UC_SearchBarcode
  (List POS Transactions) as UC_ListPOSTx
  (Reprint POS Receipt) as UC_ReprintReceipt
  (Register Account) as UC_Register
  (Authenticate User) as UC_Login
  (Browse & Search Products) as UC_Browse
  (Manage Cart) as UC_Cart
  (Checkout Order) as UC_Checkout
  (View My Orders) as UC_ViewMyOrders
  (View Order Detail) as UC_ViewOrderDetail
}
Admin --> UC_ManageCategories
Admin --> UC_ManageProducts
Admin --> UC_ManageUsers
Admin --> UC_Config
Admin --> UC_ViewAllOrders
Admin --> UC_UpdateOrderStatus

Cashier --> UC_ProcessPOSSale
Cashier --> UC_SearchBarcode
Cashier --> UC_ListPOSTx
Cashier --> UC_ReprintReceipt

Customer --> UC_Register
Customer --> UC_Login
Customer --> UC_Browse
Customer --> UC_Cart
Customer --> UC_Checkout
Customer --> UC_ViewMyOrders
Customer --> UC_ViewOrderDetail

UC_ProcessPOSSale --> UC_SearchBarcode : <<include>>
UC_Checkout --> UC_Login : <<include>>
UC_Checkout --> UC_Cart : <<include>>
@enduml
```

---
## 3. Use Case Specifications

Each use case below follows the template:  
Name | Actors | Preconditions | Main Flow | Postconditions | Expectations / Notes

### 3.1 Manage Categories
- **Actors**: Admin
- **Preconditions**: Admin authenticated; category table accessible.
- **Main Flow**:
  1. Admin views category list.
  2. Admin creates, updates, or deactivates a category (soft delete if supported) ensuring uniqueness of name.
  3. System validates input and persists changes.
- **Postconditions**: Category set reflects performed changes; timestamps updated.
- **Expectations**: Changes propagate to product listings; inactive categories hidden from customer browse.

### 3.2 Manage Products
- **Actors**: Admin
- **Preconditions**: Admin authenticated; at least one active category exists.
- **Main Flow**:
  1. Admin opens product catalog maintenance interface.
  2. Admin creates or edits product (name, category, price, tax applicability, active flag, stock, barcode/SKU).
  3. System validates uniqueness of barcode/SKU and persists.
  4. Admin may deactivate product instead of deletion.
- **Postconditions**: Product catalog updated; search index (if any) reflects change.
- **Expectations**: Inactive products excluded from POS and browse queries; stock initialized correctly.

### 3.3 Manage Users
- **Actors**: Admin
- **Preconditions**: Admin authenticated.
- **Main Flow**:
  1. Admin lists users by role.
  2. Admin creates cashier or toggles user activation.
  3. System hashes password (if manual set) and stores record.
- **Postconditions**: User accounts updated with correct roles / status.
- **Expectations**: Disabled users cannot authenticate; audit logging recommended (future).

### 3.4 Configure System Settings
- **Actors**: Admin
- **Preconditions**: Admin authenticated; settings table or properties file accessible.
- **Main Flow**:
  1. Admin opens settings (tax rate, currency, invoice numbering prefix, etc.).
  2. Admin updates value(s) and submits.
  3. System validates range (e.g., tax rate 0–100%) and persists.
- **Postconditions**: New settings active for subsequent operations.
- **Expectations**: Long‑lived values cached; changes audit logged (future enhancement).

### 3.5 View All Orders
- **Actors**: Admin
- **Preconditions**: Admin authenticated; orders exist.
- **Main Flow**:
  1. Admin requests paginated list of all orders.
  2. System returns orders with filters (status/date/user).
- **Postconditions**: None (read-only).
- **Expectations**: Performance maintained via pagination & indexes.

### 3.6 Update Order Status
- **Actors**: Admin
- **Preconditions**: Admin authenticated; target order exists and is mutable.
- **Main Flow**:
  1. Admin selects order.
  2. Admin sets new status (e.g., PROCESSING → SHIPPED / COMPLETED / CANCELED).
  3. System validates allowed transition and persists.
- **Postconditions**: Order status changed; related timestamps updated.
- **Expectations**: Customers notified (email/notification future); inventory adjustments on cancellation if unpaid.

### 3.7 Process POS Sale
- **Actors**: Cashier
- **Preconditions**: Cashier authenticated; at least one active product has stock; POS UI loaded.
- **Main Flow**:
  1. Cashier searches/scans product(s) (barcode or browse list of active products).
  2. Cashier sets quantities; system validates stock.
  3. Cashier optionally associates registered customer or leaves Walk-in (null customerId).
  4. Cashier submits sale; system calculates line totals, tax, grand total.
  5. System decrements inventory atomically, creates transaction & line items, generates bill number, payment reference.
  6. System returns receipt payload.
- **Postconditions**: Transaction persisted; inventory reduced; receipt available for print.
- **Expectations**: Concurrency safe (optimistic/pessimistic locking as needed); failure rolls back stock changes.

### 3.8 Search Product (Barcode/SKU)
- **Actors**: Cashier
- **Preconditions**: Cashier authenticated; product exists and active.
- **Main Flow**:
  1. Cashier enters/scans barcode.
  2. System returns matching product details.
- **Postconditions**: None.
- **Expectations**: O(1) or indexed lookup; instant feedback.

### 3.9 List POS Transactions
- **Actors**: Cashier
- **Preconditions**: Cashier authenticated; POS transactions exist.
- **Main Flow**:
  1. Cashier opens transaction history.
  2. (Optional) Cashier filters by bill number substring and/or date range.
  3. System returns limited (e.g., 100 most recent) summaries.
- **Postconditions**: None.
- **Expectations**: Pagination or limit enforced; secure (only POS transactions for source=POS).

### 3.10 Reprint POS Receipt
- **Actors**: Cashier
- **Preconditions**: Cashier authenticated; transaction bill number valid.
- **Main Flow**:
  1. Cashier selects a transaction summary.
  2. System retrieves full receipt detail lines.
  3. Cashier prints or exports.
- **Postconditions**: None (read-only).
- **Expectations**: Idempotent; access controlled.

### 3.11 Register Account
- **Actors**: Customer
- **Preconditions**: Registration form accessible; email/username not in use.
- **Main Flow**:
  1. User submits registration data.
  2. System validates uniqueness & password policy; hashes password.
  3. System stores customer record and returns success.
- **Postconditions**: New customer account active (or pending verification if added later).
- **Expectations**: Password never logged; potential email verification future.

### 3.12 Authenticate User
- **Actors**: Customer, Admin, Cashier
- **Preconditions**: User exists and active; credentials available.
- **Main Flow**:
  1. User submits credentials.
  2. System validates and issues JWT (role claims inside) on success.
- **Postconditions**: Session established (JWT in client storage).
- **Expectations**: Lockout / rate limit after repeated failures (future enhancement).

### 3.13 Browse & Search Products
- **Actors**: Customer
- **Preconditions**: Products exist and active.
- **Main Flow**:
  1. Customer navigates catalog, optionally searches or filters by category.
  2. System returns paginated product list.
- **Postconditions**: None.
- **Expectations**: Only active products; performance optimized by indexes & caching.

### 3.14 Manage Cart
- **Actors**: Customer
- **Preconditions**: Customer authenticated (or future guest cart); products active.
- **Main Flow**:
  1. Customer adds/removes/updates product quantities in cart.
  2. System validates stock availability snapshot.
- **Postconditions**: Cart state stored (in client/localStorage or server side session).
- **Expectations**: Graceful handling if stock changes before checkout.

### 3.15 Checkout Order
- **Actors**: Customer
- **Preconditions**: Customer authenticated; cart not empty; products still active and in stock.
- **Main Flow**:
  1. Customer reviews cart and initiates checkout.
  2. System recalculates totals & tax; (optional) calls payment gateway.
  3. On success, order and order lines persisted; stock decremented.
  4. System returns order confirmation.
- **Postconditions**: Order persisted with initial status (e.g., PENDING/PAYMENT_CONFIRMED).
- **Expectations**: Atomic transaction; rollback if payment/stock validation fails.

### 3.16 View My Orders
- **Actors**: Customer
- **Preconditions**: Customer authenticated; has orders.
- **Main Flow**:
  1. Customer requests list of own orders.
  2. System returns paginated, recent-first list.
- **Postconditions**: None.
- **Expectations**: Only user-owned orders; secure access control.

### 3.17 View Order Detail
- **Actors**: Customer
- **Preconditions**: Customer authenticated; order belongs to customer.
- **Main Flow**:
  1. Customer selects an order.
  2. System returns line items, totals, status timeline.
- **Postconditions**: None.
- **Expectations**: Sensitive internal fields excluded.

---
## 4. Traceability Matrix (High-Level)
| Use Case | Key Entities | Related Tables |
|----------|--------------|----------------|
| Manage Categories | Category | category |
| Manage Products | Product, Category, Inventory | product, category |
| Manage Users | User, Role | user, role, user_role |
| Configure System Settings | Setting (config) | settings / properties |
| View All Orders | Order, User | orders, order_items |
| Update Order Status | Order | orders |
| Process POS Sale | Transaction, Product, Inventory | transactions, transaction_lines, product |
| Search Product (Barcode/SKU) | Product | product |
| List POS Transactions | Transaction | transactions |
| Reprint POS Receipt | Transaction, TransactionLine | transactions, transaction_lines |
| Register Account | User | user |
| Authenticate User | User, Role | user, role |
| Browse & Search Products | Product, Category | product, category |
| Manage Cart | Cart (in-memory/client), Product | (n/a server) |
| Checkout Order | Order, Product, Inventory | orders, order_items, product |
| View My Orders | Order | orders |
| View Order Detail | Order, OrderItem | orders, order_items |

---
## 5. Conversion to .docx or .doc
A direct modern format is .docx. Use Pandoc locally (recommended). From repository root run:

```powershell
# Ensure pandoc is installed (winget or choco examples):
winget install --id=Pandoc.Pandoc -e --source=winget
# Convert to DOCX
pandoc .\docs\USE_CASE_SPEC.md -o .\docs\USE_CASE_SPEC.docx --metadata title="Use Case Specification"
# (Optional) Also generate PDF if LaTeX installed
pandoc .\docs\USE_CASE_SPEC.md -o .\docs\USE_CASE_SPEC.pdf
```

Legacy .doc format is not recommended; if absolutely required, open the generated .docx in Word and "Save As" .doc.

---
## 6. Future Enhancements
- Add sequence diagrams for POS sale & checkout payment.
- Integrate audit logging use case (Admin security oversight).
- Add guest browsing + wishlist use cases.
- Internationalization and multi-currency pricing rules.

---
## 7. Approval
| Role | Name | Signature | Date |
|------|------|-----------|------|
| Product Owner | TBD |  |  |
| Tech Lead | TBD |  |  |
| QA Lead | TBD |  |  |

---
End of Document.
