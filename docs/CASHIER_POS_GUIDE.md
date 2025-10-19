# Cashier POS System - DVP Gift Center

## Overview

The cashier POS system allows cashiers to process in-store sales, manage product selection, handle transactions, and generate receipts for customers.

## Features

- **Product Selection**: Browse and search for products available for in-store sales
- **Barcode Scanning**: Quick product addition via barcode scanning
- **Cart Management**: Add, remove, and modify quantities of products in the cart
- **Transaction Processing**: Complete sales with various payment methods
- **Receipt Generation**: Print receipts for completed transactions
- **Real-time Stock Management**: Automatic stock updates after each sale

## Access

### User Roles
- **Cashier**: Can access the POS system to process sales
- **Admin**: Can access both POS system and admin functions

### Login Redirection
- Cashiers are automatically redirected to `/cashier/pos` after login
- Admins are redirected to `/admin/dashboard` but can also access cashier functions

## How to Use

### 1. Product Selection

#### Browsing Products
- All products available for in-store sale are displayed in a grid layout
- Each product card shows:
  - Product image (if available)
  - Product name and code
  - Unit price in LKR
  - Available stock with color-coded badges:
    - **Green**: Good stock (>10 items)
    - **Orange**: Low stock (1-10 items)
    - **Red**: Out of stock (0 items)

#### Searching Products
- Use the search bar to find products by name, code, or partial matches
- Press Enter or click the search button to execute the search

#### Barcode Scanning
- Enter a barcode number in the search field
- Press Enter or click the barcode button
- The product will be automatically added to the cart if found

### 2. Cart Management

#### Adding Products
- Click on any product card to add it to the cart
- If the product is already in the cart, the quantity will increase by 1
- Products with insufficient stock cannot be added beyond available quantity

#### Modifying Cart Items
- **Change Quantity**: Use the number input field for each item
- **Remove Items**: Click the "×" button next to any item
- The system prevents quantities exceeding available stock

### 3. Processing Transactions

#### Pre-Transaction Setup
- Select payment method:
  - Cash
  - Credit Card
  - Debit Card
- Default customer is set to "Walk-in Customer" for POS sales

#### Transaction Calculation
- **Subtotal**: Sum of all item totals
- **Tax**: 5% of subtotal (configurable)
- **Total**: Subtotal + Tax

#### Completing the Sale
1. Ensure cart has items
2. Select payment method
3. Click "Complete Sale" button
4. Transaction is processed and stock is automatically updated
5. Receipt can be printed immediately

### 4. Receipt Management

#### Auto-Generated Receipts
Each completed transaction generates a receipt containing:
- Store information (DVP Gift Center)
- Transaction details (bill number, date, cashier)
- Customer information
- Itemized list with quantities and prices
- Tax breakdown
- Total amount and payment method
- Thank you message

#### Printing Receipts
- Receipt printing opens in a new window
- Standard browser print functionality
- Formatted for thermal/receipt printers

## Backend API Endpoints

### Product Management
- `GET /api/cashier/products` - Get all in-store products
- `GET /api/cashier/products/search?query={query}` - Search products
- `GET /api/cashier/products/barcode/{barcode}` - Get product by barcode

### Transaction Processing
- `POST /api/cashier/transactions` - Process a new transaction
- `GET /api/cashier/transactions/{id}/receipt` - Get transaction receipt

## Technical Implementation

### Frontend Components
- `CashierDashboard.js` - Main POS interface
- `pos.css` - Specialized styling for POS system

### Backend Components
- `CashierController.java` - REST API endpoints
- `CashierService.java` - Business logic
- `CashierProductDto.java` - Product data transfer
- `PosTransactionRequest.java` - Transaction request model
- `PosTransactionResponse.java` - Transaction response model

### Key Features

#### Stock Management
- Real-time stock validation during cart operations
- Automatic stock deduction after successful transactions
- Stock movement logging for audit trails

#### Transaction Security
- JWT token authentication required
- Role-based access control (cashier/admin only)
- Transaction validation and error handling

#### Data Integrity
- Transactional operations ensure data consistency
- Payment records linked to transactions
- Complete audit trail for all sales

## Error Handling

### Common Scenarios
- **Insufficient Stock**: Prevents adding more items than available
- **Product Not Found**: Clear error message for invalid barcodes
- **Network Issues**: Graceful error handling with user feedback
- **Transaction Failures**: Rollback mechanism to maintain data integrity

## Configuration

### Tax Rate
- Default: 5% (configurable in application.properties)
- Property: `app.tax.rate=0.05`

### Default Customer
- Walk-in customers use a default customer record
- Can be modified for specific customer sales if needed

## Installation & Setup

1. Ensure backend is running with cashier endpoints
2. Frontend should have React Icons installed (`react-icons`)
3. Update API base URL in environment variables
4. Cashier role must be properly configured in the user database

## Troubleshooting

### Common Issues
1. **Products not loading**: Check API connectivity and authentication
2. **Barcode not working**: Ensure barcode exists in product database
3. **Transaction failing**: Check stock availability and payment method
4. **Receipt not printing**: Check browser popup blockers and printer settings

### Support
For technical issues, check:
- Browser console for JavaScript errors
- Network tab for API call failures
- Backend logs for server-side issues