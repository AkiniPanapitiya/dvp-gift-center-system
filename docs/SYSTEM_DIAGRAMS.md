# System Diagrams (PlantUML)

Date: 2025-10-09  
Version: 1.0

This file provides full-system diagrams as PlantUML code blocks you can render directly.

---
## 1) Entire System Use Case Diagram (PlantUML)
```plantuml
@startuml
left to right direction

actor Admin
actor Cashier
actor Customer

rectangle "Gift Center System" {
  (Manage Categories) as UC_Admin_ManageCategories
  (Manage Products) as UC_Admin_ManageProducts
  (Manage Users) as UC_Admin_ManageUsers
  (Configure System Settings) as UC_Admin_Config
  (View All Orders) as UC_Admin_ViewOrders
  (Update Order Status) as UC_Admin_UpdateOrderStatus
  (View Transactions) as UC_Admin_ViewTransactions

  (Process POS Sale) as UC_Cashier_ProcessSale
  (Search Product (Barcode/SKU)) as UC_Cashier_Search
  (List POS Transactions) as UC_Cashier_ListTx
  (Reprint POS Receipt) as UC_Cashier_Reprint

  (Register Account) as UC_Cust_Register
  (Authenticate User) as UC_Cust_Login
  (Browse & Filter Products) as UC_Cust_Browse
  (Search Products) as UC_Cust_Search
  (View Product Detail) as UC_Cust_ViewDetail
  (Manage Cart) as UC_Cust_Cart
  (Checkout Order) as UC_Cust_Checkout
  (View My Orders) as UC_Cust_ViewMyOrders
  (View Order Detail) as UC_Cust_ViewOrderDetail
}

Admin --> UC_Admin_ManageCategories
Admin --> UC_Admin_ManageProducts
Admin --> UC_Admin_ManageUsers
Admin --> UC_Admin_Config
Admin --> UC_Admin_ViewOrders
Admin --> UC_Admin_UpdateOrderStatus
Admin --> UC_Admin_ViewTransactions

Cashier --> UC_Cashier_ProcessSale
Cashier --> UC_Cashier_Search
Cashier --> UC_Cashier_ListTx
Cashier --> UC_Cashier_Reprint

Customer --> UC_Cust_Register
Customer --> UC_Cust_Login
Customer --> UC_Cust_Browse
Customer --> UC_Cust_Search
Customer --> UC_Cust_ViewDetail
Customer --> UC_Cust_Cart
Customer --> UC_Cust_Checkout
Customer --> UC_Cust_ViewMyOrders
Customer --> UC_Cust_ViewOrderDetail

UC_Cashier_ProcessSale --> UC_Cashier_Search : <<include>>
UC_Cust_Checkout --> UC_Cust_Login : <<include>>
UC_Cust_Cart --> UC_Cust_ViewDetail : <<extend>>
@enduml
```

Notes:
- Scope includes implemented features only (no external payment gateway, no COD).
- Admin transactions view is read-only in current build; exports/aggregates are future.

---
## 2) Entire System Class Diagram (PlantUML)

Derived from `database/dvp_gift_center_schema.sql` (tables, PKs/FKs, relationships).

```plantuml
@startuml
hide circle
skinparam classAttributeIconSize 0

' ==========================
' Users & Access Control
' ==========================
class User {
  +userId: Long
  +username: String
  +passwordHash: String
  +email: String
  +fullName: String
  +phone: String?
  +role: String  ' admin | cashier | customer
  +address: Text?
  +createdAt: Timestamp
  +isActive: boolean
}

class SessionToken {
  +tokenId: Long
  +userId: Long
  +tokenHash: String
  +createdAt: Timestamp
  +expiresAt: DateTime
  +isActive: boolean
}

class AuditLog {
  +logId: Long
  +userId: Long?
  +actionType: String
  +tableAffected: String
  +recordId: String
  +oldValues: Text?
  +newValues: Text?
  +timestamp: Timestamp
  +ipAddress: String?
}

class CustomerAddress {
  +addressId: Long
  +customerId: Long
  +addressLine1: String
  +addressLine2: String?
  +city: String
  +postalCode: String
}

class CartItem {
  +cartItemId: Long
  +customerId: Long
  +productId: Long
  +quantity: int
  +addedAt: Timestamp
  +updatedAt: Timestamp
}

' ==========================
' Products & Inventory
' ==========================
class Category {
  +categoryId: Long
  +categoryName: String
  +description: Text?
  +isActive: boolean
  +createdAt: Timestamp
}

class Product {
  +productId: Long
  +categoryId: Long?
  +productName: String
  +productCode: String?
  +barcode: String?
  +description: Text?
  +unitPrice: Decimal(10,2)
  +costPrice: Decimal(10,2)?
  +imageUrl: String?
  +isActive: boolean
}

class OnlineProduct {
  +onlineProductId: Long
  +productId: Long (unique)
  +onlinePrice: Decimal(10,2)
  +isAvailableOnline: boolean
  +onlineDescription: Text?
  +promotionalDetails: Text?
  +createdAt: Timestamp
}

class Inventory {
  +inventoryId: Long
  +productId: Long (unique)
  +currentStock: int
  +minStockLevel: int
  +maxStockLevel: int?
  +lastUpdated: Timestamp
}

' ==========================
' Sales & Transactions
' ==========================
class Transaction {
  +transactionId: Long
  +customerId: Long?
  +userId: Long  ' staff/cashier
  +billNumber: String (unique)
  +transactionDate: DateTime
  +totalAmount: Decimal(10,2)
  +taxAmount: Decimal(10,2)
  +discountAmount: Decimal(10,2)
  +netAmount: Decimal(10,2)
  +transactionType: String  ' sale | return
  +status: String  ' completed | pending | cancelled
  +receiptPrinted: boolean
  +emailSent: boolean
  +source: String  ' pos_sale | online_sale
}

class TransactionItem {
  +itemId: Long
  +transactionId: Long
  +productId: Long
  +quantity: int
  +unitPrice: Decimal(10,2)
  +lineTotal: Decimal(10,2)
  +discountAmount: Decimal(10,2)
  +taxAmount: Decimal(10,2)
  +returnQuantity: int
}

class OnlineOrder {
  +orderId: Long
  +customerId: Long
  +transactionId: Long (unique)
  +shippingAddressId: Long?
  +orderStatus: String  ' pending | processing | shipped | delivered | cancelled
  +shippingMethod: String?
  +trackingNumber: String?
  +placedAt: Timestamp
}

class Payment {
  +paymentId: Long
  +transactionId: Long
  +paymentDate: DateTime
  +paymentMethod: String  ' cash | credit_card
  +amountPaid: Decimal(10,2)
  +referenceNumber: String?
  +status: String  ' success | failed | pending
}

class Return {
  +returnId: Long
  +originalTransactionId: Long
  +returnTransactionId: Long?
  +productId: Long
  +returnQuantity: int
  +returnReason: Text?
  +returnDate: DateTime
  +returnAmount: Decimal(10,2)
  +status: String  ' refunded | exchanged | pending
}

class StockMovement {
  +movementId: Long
  +productId: Long
  +transactionId: Long?
  +movementType: String  ' sale | return | adjustment_in | adjustment_out
  +quantityChange: int
  +previousStock: int
  +newStock: int
  +movementDate: Timestamp
  +notes: Text?
}

class EmailLog {
  +emailId: Long
  +transactionId: Long?
  +recipientEmail: String
  +sentDate: DateTime
  +status: String  ' sent | failed
  +errorMessage: Text?
}

' ==========================
' Relationships (from FKs)
' ==========================
User "1" -- "*" CustomerAddress : has
User "1" -- "*" SessionToken : has
User "0..1" -- "*" AuditLog : actor (optional)
User "1" -- "*" CartItem : keeps

Category "1" -- "0..*" Product : categorizes
Product "1" -- "0..1" OnlineProduct : online info
Product "1" -- "0..1" Inventory : stock
Product "1" -- "*" CartItem : referenced

Transaction "1" -- "*" TransactionItem : contains
Transaction "1" -- "0..*" Payment : paid by
Transaction "0..1" -- "*" EmailLog : notifications
Transaction "0..1" -- "*" StockMovement : movements

User "0..1" -- "*" Transaction : customer (nullable)
User "1" -- "*" Transaction : staff

OnlineOrder "1" -- "1" Transaction : links
OnlineOrder "1" -- "1" User : customer
OnlineOrder "0..1" -- "1" CustomerAddress : ships to

Return "*" -- "1" Transaction : original
Return "*" -- "0..1" Transaction : returnTx
Return "*" -- "1" Product : item

TransactionItem "*" -- "1" Product : item

@enduml
```

Assumptions:
- Names and types mirror the SQL schema; optional fields indicated with `?`.
- `OnlineOrder` is a 1:1 extension of `Transaction` for online sales (by `transaction_id`).
- `Transaction.customerId` is nullable for POS walk-in; `Transaction.userId` is staff/cashier.

---
## Render Instructions

You can render these diagrams with PlantUML (locally or via an online renderer):

```powershell
# On Windows (if Java & PlantUML jar available):
java -jar plantuml.jar .\docs\SYSTEM_DIAGRAMS.md
```

Or paste each code block into any PlantUML editor/renderer.
