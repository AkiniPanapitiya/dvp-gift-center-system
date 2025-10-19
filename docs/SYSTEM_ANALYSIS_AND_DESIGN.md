# System Analysis & Design Documentation

## 1. Overview
This document captures the functional analysis of the DVP Gift Center system covering Customer web storefront, Cashier POS, and Admin back‑office capabilities. It includes: actors, use case diagram, comprehensive use case list & scenarios, activity diagrams, and a domain class diagram.

## 2. Actors
Primary Actors:
- **Guest** – Unauthenticated visitor who can browse and search products, then register/login.
- **Customer** – Authenticated user purchasing via the online storefront.
- **Cashier** – In‑store staff operating the POS to process walk‑in or registered customer sales.
- **Admin** – Manages products, categories, inventory, availability, transactions, and orders.
- **Payment Processor** (external conceptual) – Represents payment capture (currently internal simulation).
- **Email Service** (conceptual) – Future integration for order confirmation notifications.

Secondary/Internal Actors:
- **Authentication Service** – Issues and validates JWT tokens.
- **Inventory System** – Internal responsibility for up-to-date stock levels and movements.

## 3. High-Level Use Case Diagram (PlantUML)
```plantuml
@startuml
left to right direction
skinparam actorStyle awesome

actor Guest
actor Customer
actor Cashier
actor Admin
actor "Payment Processor" as PaymentExt
actor "Email Service" as EmailExt

rectangle "DVP Gift Center System" {
  (Register Account) as UC_Register
  (Login) as UC_Login
  (Logout) as UC_Logout
  (Browse Products) as UC_Browse
  (Search Products) as UC_Search
  (View Product Detail) as UC_ViewProduct
  (Add to Cart) as UC_AddCart
  (Update Cart Item) as UC_UpdateCart
  (Remove Cart Item) as UC_RemoveCart
  (Checkout Online Order) as UC_Checkout
  (View My Orders) as UC_ViewOrders
  (View Order Detail) as UC_OrderDetail

  (POS View Products) as UC_PosViewProducts
  (POS Scan/Search Product) as UC_PosScan
  (POS Manage Cart) as UC_PosCart
  (Process POS Sale) as UC_PosSale
  (Print POS Receipt) as UC_PrintReceipt
  (View POS Bills) as UC_ViewBills

  (Manage Products) as UC_AdminProducts
  (Soft Delete Product) as UC_SoftDelete
  (Restore Product) as UC_Restore
  (Manage Categories) as UC_AdminCategories
  (Manage Inventory) as UC_AdminInventory
  (Adjust Stock) as UC_AdjustStock
  (Manage Online Availability) as UC_OnlineAvailability
  (View Transactions) as UC_ViewTransactions
  (View Online Orders) as UC_ViewOnlineOrders

  (Authenticate & Authorize) as UC_Auth
  (Compute Tax) as UC_Tax
  (Generate Bill Number) as UC_BillNo
  (Record Payment) as UC_RecordPayment
}

Guest --> UC_Register
Guest --> UC_Login
Guest --> UC_Browse
Guest --> UC_Search
Guest --> UC_ViewProduct

Customer --> UC_Login
Customer --> UC_Logout
Customer --> UC_Browse
Customer --> UC_Search
Customer --> UC_ViewProduct
Customer --> UC_AddCart
Customer --> UC_UpdateCart
Customer --> UC_RemoveCart
Customer --> UC_Checkout
Customer --> UC_ViewOrders
Customer --> UC_OrderDetail

Cashier --> UC_Login
Cashier --> UC_Logout
Cashier --> UC_PosViewProducts
Cashier --> UC_PosScan
Cashier --> UC_PosCart
Cashier --> UC_PosSale
Cashier --> UC_PrintReceipt
Cashier --> UC_ViewBills

Admin --> UC_Login
Admin --> UC_Logout
Admin --> UC_AdminProducts
Admin --> UC_SoftDelete
Admin --> UC_Restore
Admin --> UC_AdminCategories
Admin --> UC_AdminInventory
Admin --> UC_AdjustStock
Admin --> UC_OnlineAvailability
Admin --> UC_ViewTransactions
Admin --> UC_ViewOnlineOrders

UC_Checkout --> UC_RecordPayment
UC_PosSale --> UC_RecordPayment
UC_PosSale --> UC_BillNo
UC_Checkout --> UC_Tax
UC_PosSale --> UC_Tax

PaymentExt --> UC_RecordPayment
UC_Checkout --> EmailExt : <<notify confirmation>>

note right of UC_SoftDelete
  Sets isActive=false
  (extends Manage Products)
end note

note right of UC_Restore
  Restores soft-deleted product
end note
@enduml
```

## 4. Use Case Catalogue (Summary)
| Use Case | Description | Actor(s) |
|----------|-------------|----------|
| Register Account | Create new customer profile | Guest |
| Login | Authenticate and obtain JWT | Guest, Customer, Cashier, Admin |
| Logout | End session client-side | All authenticated |
| Browse Products | Paginated viewing of active products | Guest, Customer |
| Search Products | Query products by text/barcode | Guest, Customer |
| View Product Detail | Detailed product info | Guest, Customer |
| Add to Cart | Add item to cart | Customer |
| Update Cart Item | Change quantity in cart | Customer |
| Remove Cart Item | Remove cart item | Customer |
| Checkout Online Order | Place online order & payment | Customer |
| View My Orders | List customer orders | Customer |
| View Order Detail | Detailed order display | Customer |
| POS View Products | Product list for POS | Cashier |
| POS Scan/Search Product | Quick lookup via barcode/text | Cashier |
| POS Manage Cart | Add/update/remove POS cart lines | Cashier |
| Process POS Sale | Complete in-store sale | Cashier |
| Print POS Receipt | Generate printable receipt | Cashier |
| View POS Bills | List previous POS transactions | Cashier |
| Manage Products | CRUD + pricing | Admin |
| Soft Delete Product | Mark inactive (logical delete) | Admin |
| Restore Product | Reactivate product | Admin |
| Manage Categories | CRUD categories | Admin |
| Manage Inventory | View/update stock | Admin |
| Adjust Stock | Manual stock adjustment + audit | Admin |
| Manage Online Availability | Toggle online listing | Admin |
| View Transactions | List transactions (POS/online) | Admin |
| View Online Orders | View customers' online orders | Admin |
| Authenticate & Authorize | Role-based access control | System |
| Compute Tax | Apply configured tax rate | System |
| Generate Bill Number | Create unique bill identifier | System |
| Record Payment | Store payment data | System |

## 5. Detailed Use Case Scenarios

### 5.1 Register Account
**Actor:** Guest  
**Preconditions:** Not authenticated.  
**Main Flow:** 1) Open form 2) Enter details 3) Validate uniqueness 4) Hash & store user (ROLE_CUSTOMER) 5) Success response.  
**Alternates:** Username/email exists → error.  
**Postconditions:** Customer account created.

### 5.2 Login
**Actors:** Guest/Customer/Cashier/Admin  
**Main:** Submit credentials → validate → issue JWT.  
**Alt:** Invalid credentials → unauthorized.

### 5.3 Browse Products
Retrieve active products list (paginated). Empty list if none.

### 5.4 Search Products
Search by name/code/barcode; return matching active products.

### 5.5 View Product Detail
Return details for selected product (if active).

### 5.6 Add to Cart
Check product active and optionally stock → create/update cart item.

### 5.7 Update Cart Item
Validate quantity >0 and ≤ available stock → persist.

### 5.8 Remove Cart Item
Delete the cart item; recalc summary.

### 5.9 Checkout Online Order
**Preconditions:** Authenticated; cart not empty.  
**Main:** Validate stock → compute totals (subtotal+tax) → persist Transaction (+items, source=online_sale) → record Payment → decrement inventory + StockMovements → confirmation → (optional email).  
**Alt:** Stock fail; Payment failure.  
**Post:** Order placed, inventory updated.

### 5.10 View My Orders
List customer's transactions with source=online_sale.

### 5.11 View Order Detail
Return items, payment, amounts.

### 5.12 POS View Products
List active products with current stock for POS.

### 5.13 POS Scan/Search Product
Barcode or text search to retrieve one or multiple products.

### 5.14 POS Manage Cart
Add/remove/update line items; enforce stock.

### 5.15 Process POS Sale
**Preconditions:** POS cart not empty.  
**Main:** Optional customer selection (nullable) → validate stock → compute totals & tax → generate bill # → persist transaction & items (source=pos_sale) → update inventory & StockMovements → record payment → return receipt DTO.  
**Alt:** Stock failure; payment error.  
**Post:** Inventory updated; sale recorded.

### 5.16 Print POS Receipt
Fetch receipt DTO and render/print.

### 5.17 View POS Bills
Filter last POS transactions by bill or date range (top N default).

### 5.18 Manage Products
CRUD operations with validation and online availability toggles.

### 5.19 Soft Delete Product
Set isActive=false (retains historical references). Restore possible.

### 5.20 Restore Product
Set isActive=true; product becomes available again.

### 5.21 Manage Categories
CRUD for categories used in product classification.

### 5.22 Manage Inventory / Adjust Stock
Manual adjustments create StockMovement with before/after counts.

### 5.23 Manage Online Availability
Enable/disable product for online storefront without disabling product entirely.

### 5.24 View Transactions
Admin lists transactions; may filter by source (POS/online).

### 5.25 View Online Orders
Admin reviews customer orders for fulfillment/resolution.

### 5.26 Record Payment (Internal)
Persist payment details and link to transaction.

### 5.27 Compute Tax (Internal)
Apply configured rate (e.g., `app.tax.rate`) during sale computations.

### 5.28 Generate Bill Number (Internal)
Format: `DVPYYYYMMDD####` (daily sequence).

## 6. Activity Diagrams

### 6.1 Online Checkout
```plantuml
@startuml
start
:Customer initiates checkout;
:Load cart items;
if (Cart empty?) then (yes)
  :Show error; stop
else (no)
  :Validate stock;
  if (Stock OK?) then (yes)
    :Compute totals;
    :Persist Transaction & Items;
    :Record Payment;
    :Adjust inventory & StockMovement;
    :Return confirmation; :Optional email;
  else (no)
    :Show insufficient stock error;
  endif
endif
stop
@enduml
```

### 6.2 POS Transaction
```plantuml
@startuml
start
:Build POS cart;
if (Cart empty?) then (yes)
  :Prompt to add items; stop
else (no)
  :Optional select customer;
  :Validate stock lines;
  if (Valid?) then (yes)
    :Compute total & tax;
    :Generate bill number;
    :Persist Transaction & Items;
    :Update inventory & StockMovements;
    :Record Payment; :Return receipt; :Print receipt;
  else (no)
    :Show stock error;
  endif
endif
stop
@enduml
```

### 6.3 Product Soft Delete / Restore
```plantuml
@startuml
start
:Admin selects product;
if (Action = Soft Delete?) then (Yes)
  :Set isActive=false; :Persist; :Log audit optional;
else (Restore)
  :Set isActive=true; :Persist;
endif
:Refresh list;
stop
@enduml
```

### 6.4 Inventory Adjustment
```plantuml
@startuml
start
:Open inventory record;
:Enter adjustment (+/-);
:Compute new stock;
if (new stock < 0?) then (yes)
  :Reject adjustment; stop
else (no)
  :Persist new stock; :Create StockMovement; :Show success;
endif
stop
@enduml
```

## 7. Domain Class Diagram
```plantuml
@startuml
skinparam classAttributeIconSize 0

class User {
  Long userId
  String username
  String passwordHash
  String email
  String fullName
  String phone
  String role
  Boolean isActive
}

class Product {
  Long productId
  String productName
  String productCode
  String barcode
  String description
  BigDecimal unitPrice
  Boolean isActive
  Boolean onlineAvailable
  String imageUrl
}

class Category {
  Integer categoryId
  String categoryName
  Boolean isActive
}

class Inventory {
  Long inventoryId
  Integer currentStock
  LocalDateTime lastUpdated
}

class StockMovement {
  Long movementId
  String movementType
  Integer quantityChange
  Integer previousStock
  Integer newStock
  LocalDateTime movementDate
  String notes
}

class Transaction {
  Long transactionId
  String billNumber
  LocalDateTime transactionDate
  BigDecimal totalAmount
  BigDecimal taxAmount
  BigDecimal discountAmount
  BigDecimal netAmount
  String transactionType
  String status
  String source
}

class TransactionItem {
  Long itemId
  Integer quantity
  BigDecimal unitPrice
  BigDecimal discountAmount
  BigDecimal lineTotal
  Integer returnQuantity
}

class Payment {
  Long paymentId
  BigDecimal amountPaid
  String paymentMethod
  LocalDateTime paymentDate
  String status
  String referenceNumber
}

class OnlineOrder {
  Long orderId
  LocalDateTime placedAt
  String status
  String fulfillmentStatus
}

class CartItem {
  Long cartItemId
  Integer quantity
}

Category "1" -- "0..*" Product
Product "1" -- "0..1" Inventory
Product "1" -- "0..*" StockMovement
Product "1" -- "0..*" TransactionItem
Inventory "1" -- "0..*" StockMovement
User "1" -- "0..*" Transaction : cashier/user
User "1" -- "0..*" Transaction : customer (optional)
Transaction "1" -- "1..*" TransactionItem
Transaction "1" -- "0..*" Payment
Transaction "1" -- "0..1" OnlineOrder
User "1" -- "0..*" CartItem
Product "1" -- "0..*" CartItem
@enduml
```

## 8. Traceability Matrix (Use Cases → Classes)
| Use Case | Key Classes |
|----------|-------------|
| Checkout Online Order | CartItem, Product, Inventory, Transaction, TransactionItem, Payment, StockMovement, User |
| Process POS Sale | Product, Inventory, Transaction, TransactionItem, Payment, StockMovement, User |
| Manage Products | Product, Category |
| Soft Delete / Restore | Product |
| Adjust Stock | Inventory, StockMovement, Product |
| View POS Bills | Transaction, TransactionItem, Payment |
| View My Orders | Transaction, TransactionItem, Payment |
| Print POS Receipt | Transaction, TransactionItem, Payment |
| Browse/Search Products | Product, Category |

## 9. Non-Functional Considerations
- **Security:** JWT + role-based access (ROLE_ADMIN, ROLE_CASHIER, ROLE_CUSTOMER).
- **Consistency:** Inventory and stock movements updated atomically in transaction boundary.
- **Auditability:** StockMovement retains before/after counts.
- **Extensibility:** Walk-in customers supported with nullable customer in `Transaction`.
- **Performance:** Simple queries; indexes recommended on `bill_number`, `barcode`, `product_code`.

## 10. Future Enhancements
- External payment gateway integration.
- Email/SMS notification service.
- Reporting dashboards (low stock, daily sales KPIs).
- Customer loyalty / discount engine.
- Batch import/export for products and inventory.

---
*End of Document*
