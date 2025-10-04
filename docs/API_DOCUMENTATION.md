# API Documentation - DVP Gift Center

## Overview
Endpoints for the DVP Gift Center application. All routes are served under the servlet context path /api.

Base URL
- http://localhost:8080/api

Authentication
- Most endpoints require JWT. Send Authorization: Bearer <token>

Response wrapper (typical)
- success: boolean
- data: payload
- message: optional message
- timestamp: ISO8601

Errors
- Standard HTTP status codes with an error message. 401 for unauthenticated, 403 for unauthorized.

---

## Public Endpoints

Products
- GET /online/products
  - List products available online. Supports paging/filtering as implemented.
- GET /online/products/{id}
  - Product detail.
- GET /online/products/search?query=<q>&category=<name>
  - Search and filter.

Categories
- GET /online/categories
  - List public categories.

Notes
- Currency: All prices are in LKR.

---

## Customer Endpoints (JWT required)

Checkout
- POST /online/checkout
  - Server recomputes all monetary values from authoritative prices and applies tax using app.tax.rate (e.g., 0.05 for 5%).
  - Persists totals to transactions: total_amount, tax_amount, net_amount.
  - Returns order info and computed totals.
  - Request body (example):
    {
      "items": [{ "productId": 1, "quantity": 2, "price": 50.00 }],
      "shippingAddress": "string",
      "paymentMethod": "CREDIT_CARD",
      "totalAmount": 108.50
    }

Order history
- GET /online/orders/my
  - Current user's orders summary.
- GET /online/orders/{id}
  - Current user's order detail (line items, totals, status).

---

## Admin Endpoints (ROLE_ADMIN)

Products
- GET /admin/products?availability=all|online|offline&includeArchived=true|false
  - Admin listing; can include archived (is_active=false) and filter by online availability.
- POST /admin/products
  - Create product.
- PUT /admin/products/{id}
  - Update product.
- PUT /admin/products/{id}/restore
  - Restore a previously archived (soft-deleted) product.
- DELETE /admin/products/{id}?hard=false
  - Soft delete by default (archives product). Use hard=true for permanent delete.
  - If referenced by transactions/order items, hard delete returns 409 Conflict.

Orders
- GET /admin/orders
  - List all orders with filters (status/date/user as implemented).
- PUT /admin/orders/{id}/status
  - Update order status.

Dashboard
- GET /admin/dashboard/stats
  - Summary stats for the admin dashboard.

---

## Common Patterns

Pagination (when available)
- Use page, size, sort, direction query params
- Example: GET /online/products?page=0&size=20&sort=name&direction=asc

Validation highlights
- Product: name required; price positive; category valid
- Checkout: at least 1 item; shipping address required; totals are recomputed on server

Security
- Public: /online/products/**, /online/categories/**, /auth/**, Swagger if enabled
- Customer: /online/cart/**, /online/checkout/**, /online/orders/**
- Admin: /admin/**

Currency & Tax
- Currency is LKR sitewide.
- Tax rate configured by app.tax.rate (e.g., 0.05 = 5%).