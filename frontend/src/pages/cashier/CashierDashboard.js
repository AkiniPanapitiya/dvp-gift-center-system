import React, { useState, useEffect, useCallback } from 'react';
import { Container, Row, Col, Card, Button, Form, InputGroup, Table, Badge, Alert } from 'react-bootstrap';
import { FaSearch, FaBarcode, FaShoppingCart, FaReceipt } from 'react-icons/fa';
import '../../styles/pos.css';

const CashierDashboard = () => {
  const [products, setProducts] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [cart, setCart] = useState([]);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [loading, setLoading] = useState(false);
  const [alert, setAlert] = useState({ show: false, message: '', type: 'success' });
  const [paymentMethod, setPaymentMethod] = useState('CASH');

  const loadProducts = useCallback(async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      const response = await fetch(`/api/cashier/products`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (response.ok) {
        const data = await response.json();
        setProducts(data.data || []);
      } else {
        showAlert('Failed to load products', 'danger');
      }
    } catch (error) {
      showAlert('Error loading products: ' + error.message, 'danger');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadProducts();
  }, [loadProducts]);

  const searchProducts = async (query) => {
    if (!query.trim()) {
      loadProducts();
      return;
    }

    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      const response = await fetch(
        `/api/cashier/products/search?query=${encodeURIComponent(query)}`,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );

      if (response.ok) {
        const data = await response.json();
        setProducts(data.data || []);
      } else {
        showAlert('Search failed', 'danger');
      }
    } catch (error) {
      showAlert('Search error: ' + error.message, 'danger');
    } finally {
      setLoading(false);
    }
  };

  const searchByBarcode = async (barcode) => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `/api/cashier/products/barcode/${encodeURIComponent(barcode)}`,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );

      if (response.ok) {
        const data = await response.json();
        if (data.data) {
          addToCart(data.data);
        } else {
          showAlert('Product not found', 'warning');
        }
      } else {
        showAlert('Product not found', 'warning');
      }
    } catch (error) {
      showAlert('Barcode scan error: ' + error.message, 'danger');
    }
  };

  const addToCart = (product) => {
    if (product.availableStock <= 0) {
      showAlert('Product is out of stock', 'warning');
      return;
    }

    const existingItem = cart.find(item => item.productId === product.productId);
    
    if (existingItem) {
      if (existingItem.quantity >= product.availableStock) {
        showAlert('Cannot add more items. Insufficient stock.', 'warning');
        return;
      }
      
      setCart(cart.map(item =>
        item.productId === product.productId
          ? { ...item, quantity: item.quantity + 1 }
          : item
      ));
    } else {
      setCart([...cart, {
        productId: product.productId,
        productName: product.productName,
        productCode: product.productCode,
        unitPrice: product.unitPrice,
        quantity: 1,
        availableStock: product.availableStock
      }]);
    }
    
    showAlert(`Added ${product.productName} to cart`, 'success');
  };

  const updateCartQuantity = (productId, newQuantity) => {
    if (newQuantity <= 0) {
      removeFromCart(productId);
      return;
    }

    const product = cart.find(item => item.productId === productId);
    if (newQuantity > product.availableStock) {
      showAlert('Cannot exceed available stock', 'warning');
      return;
    }

    setCart(cart.map(item =>
      item.productId === productId
        ? { ...item, quantity: newQuantity }
        : item
    ));
  };

  const removeFromCart = (productId) => {
    setCart(cart.filter(item => item.productId !== productId));
  };

  const calculateTotal = () => {
    const subtotal = cart.reduce((total, item) => total + (item.unitPrice * item.quantity), 0);
    const tax = subtotal * 0.05; // 5% tax
    return {
      subtotal: subtotal.toFixed(2),
      tax: tax.toFixed(2),
      total: (subtotal + tax).toFixed(2)
    };
  };

  const processTransaction = async () => {
    if (cart.length === 0) {
      showAlert('Cart is empty', 'warning');
      return;
    }

    // Customer is optional (walk-in). If none selected, we'll omit customerId.

    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      const totals = calculateTotal();
      
      const transactionRequest = {
        items: cart.map(item => ({
          productId: item.productId,
          quantity: item.quantity,
          unitPrice: item.unitPrice,
          discountAmount: 0
        })),
        discountAmount: 0,
        taxAmount: parseFloat(totals.tax),
        paymentMethod: paymentMethod
      };

      // Include customerId only if a specific customer is selected
      if (selectedCustomer?.userId) {
        transactionRequest.customerId = selectedCustomer.userId;
      }

      const response = await fetch(`/api/cashier/transactions`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(transactionRequest)
      });

      if (response.ok) {
        const data = await response.json();
        showAlert('Transaction completed successfully!', 'success');
        
        // Clear cart and refresh products
        setCart([]);
        setSelectedCustomer(null);
        loadProducts();
        
        // Show receipt option
        if (window.confirm('Transaction completed! Would you like to print the receipt?')) {
          printReceipt(data.data);
        }
      } else {
        const errorData = await response.json();
        showAlert('Transaction failed: ' + (errorData.message || 'Unknown error'), 'danger');
      }
    } catch (error) {
      showAlert('Transaction error: ' + error.message, 'danger');
    } finally {
      setLoading(false);
    }
  };

  const printReceipt = (transactionData) => {
    // Create a new window for receipt printing
    const printWindow = window.open('', '_blank');
    const receiptHtml = generateReceiptHtml(transactionData);
    
    printWindow.document.write(receiptHtml);
    printWindow.document.close();
    printWindow.print();
  };

  const generateReceiptHtml = (transaction) => {
    return `
      <html>
        <head>
          <title>Receipt - ${transaction.billNumber}</title>
          <style>
            body { font-family: monospace; margin: 20px; }
            .header { text-align: center; border-bottom: 1px solid #000; padding-bottom: 10px; }
            .item { display: flex; justify-content: space-between; margin: 5px 0; }
            .total { border-top: 1px solid #000; padding-top: 10px; font-weight: bold; }
          </style>
        </head>
        <body>
          <div class="header">
            <h2>DVP GIFT CENTER</h2>
            <p>Receipt: ${transaction.billNumber}</p>
            <p>Date: ${new Date(transaction.transactionDate).toLocaleString()}</p>
            <p>Cashier: ${transaction.cashierName}</p>
            <p>Customer: ${transaction.customerName}</p>
          </div>
          <div class="items">
            ${transaction.items.map(item => `
              <div class="item">
                <span>${item.productName} x${item.quantity}</span>
                <span>LKR ${(item.unitPrice * item.quantity).toFixed(2)}</span>
              </div>
            `).join('')}
          </div>
          <div class="total">
            <div class="item">
              <span>Subtotal:</span>
              <span>LKR ${transaction.totalAmount.toFixed(2)}</span>
            </div>
            <div class="item">
              <span>Tax:</span>
              <span>LKR ${transaction.taxAmount.toFixed(2)}</span>
            </div>
            <div class="item">
              <span>Total:</span>
              <span>LKR ${transaction.netAmount.toFixed(2)}</span>
            </div>
            <div class="item">
              <span>Payment Method:</span>
              <span>${transaction.paymentMethod}</span>
            </div>
          </div>
          <div style="text-align: center; margin-top: 20px;">
            <p>Thank you for your purchase!</p>
          </div>
        </body>
      </html>
    `;
  };

  const showAlert = (message, type) => {
    setAlert({ show: true, message, type });
    setTimeout(() => setAlert({ show: false, message: '', type: 'success' }), 3000);
  };

  const totals = calculateTotal();

  return (
    <div className="pos-system">

      {/* Alert */}
      {alert.show && (
        <Alert variant={alert.type} className="m-3" dismissible onClose={() => setAlert({ show: false, message: '', type: 'success' })}>
          {alert.message}
        </Alert>
      )}

      <Container fluid className="py-3">
        <Row>
          {/* Product Selection */}
          <Col lg={8}>
            <Card>
              <Card.Header>
                <Row className="align-items-center">
                  <Col>
                    <h5 className="mb-0">
                      <FaSearch className="me-2" />
                      Product Selection
                    </h5>
                  </Col>
                  <Col xs="auto">
                    <InputGroup>
                      <Form.Control
                        placeholder="Search or scan barcode..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        onKeyPress={(e) => {
                          if (e.key === 'Enter') {
                            e.preventDefault();
                            if (searchQuery.match(/^\d+$/)) {
                              // If query is all digits, treat as barcode
                              searchByBarcode(searchQuery);
                              setSearchQuery('');
                            } else {
                              searchProducts(searchQuery);
                            }
                          }
                        }}
                      />
                      <Button
                        variant="outline-secondary"
                        onClick={() => {
                          if (searchQuery.match(/^\d+$/)) {
                            searchByBarcode(searchQuery);
                            setSearchQuery('');
                          } else {
                            searchProducts(searchQuery);
                          }
                        }}
                      >
                        <FaBarcode />
                      </Button>
                    </InputGroup>
                  </Col>
                </Row>
              </Card.Header>
              <Card.Body>
                <div className="product-grid" style={{ maxHeight: '500px', overflowY: 'auto' }}>
                  <Row>
                    {loading ? (
                      <Col className="text-center p-4">
                        <div className="spinner-border" role="status">
                          <span className="visually-hidden">Loading...</span>
                        </div>
                      </Col>
                    ) : products.length === 0 ? (
                      <Col className="text-center p-4">
                        <p>No products found</p>
                      </Col>
                    ) : (
                      products.map(product => (
                        <Col sm={6} md={4} lg={3} key={product.productId} className="mb-3">
                          <Card 
                            className={`product-card ${product.availableStock <= 0 ? 'out-of-stock' : ''}`}
                            style={{ cursor: 'pointer' }}
                            onClick={() => addToCart(product)}
                          >
                            {product.imageUrl && (
                              <Card.Img 
                                variant="top" 
                                src={product.imageUrl} 
                                style={{ height: '150px', objectFit: 'cover' }}
                              />
                            )}
                            <Card.Body className="p-2">
                              <Card.Title className="h6 text-truncate">
                                {product.productName}
                              </Card.Title>
                              <Card.Text className="small">
                                <div>Code: {product.productCode}</div>
                                <div>Price: LKR {product.unitPrice}</div>
                                <div>
                                  Stock: 
                                  <Badge bg={product.availableStock <= 0 ? 'danger' : product.availableStock <= 10 ? 'warning' : 'success'} className="ms-1">
                                    {product.availableStock}
                                  </Badge>
                                </div>
                              </Card.Text>
                            </Card.Body>
                          </Card>
                        </Col>
                      ))
                    )}
                  </Row>
                </div>
              </Card.Body>
            </Card>
          </Col>

          {/* Cart and Checkout */}
          <Col lg={4}>
            <Card className="mb-3">
              <Card.Header>
                <h5 className="mb-0">
                  <FaShoppingCart className="me-2" />
                  Cart ({cart.length} items)
                </h5>
              </Card.Header>
              <Card.Body style={{ maxHeight: '300px', overflowY: 'auto' }}>
                {cart.length === 0 ? (
                  <p className="text-muted text-center">Cart is empty</p>
                ) : (
                  <Table size="sm">
                    <thead>
                      <tr>
                        <th>Product</th>
                        <th>Qty</th>
                        <th>Total</th>
                        <th></th>
                      </tr>
                    </thead>
                    <tbody>
                      {cart.map(item => (
                        <tr key={item.productId}>
                          <td>
                            <small className="text-truncate d-block" style={{ maxWidth: '100px' }}>
                              {item.productName}
                            </small>
                            <small className="text-muted">LKR {item.unitPrice}</small>
                          </td>
                          <td>
                            <Form.Control
                              type="number"
                              size="sm"
                              min="1"
                              max={item.availableStock}
                              value={item.quantity}
                              onChange={(e) => updateCartQuantity(item.productId, parseInt(e.target.value))}
                              style={{ width: '60px' }}
                            />
                          </td>
                          <td>
                            LKR {(item.unitPrice * item.quantity).toFixed(2)}
                          </td>
                          <td>
                            <Button
                              variant="outline-danger"
                              size="sm"
                              onClick={() => removeFromCart(item.productId)}
                            >
                              ×
                            </Button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </Table>
                )}
              </Card.Body>
            </Card>

            {/* Totals and Checkout */}
            <Card>
              <Card.Header>
                <h5 className="mb-0">Checkout</h5>
              </Card.Header>
              <Card.Body>
                <Table size="sm" borderless>
                  <tbody>
                    <tr>
                      <td>Subtotal:</td>
                      <td className="text-end">LKR {totals.subtotal}</td>
                    </tr>
                    <tr>
                      <td>Tax (5%):</td>
                      <td className="text-end">LKR {totals.tax}</td>
                    </tr>
                    <tr className="fw-bold border-top">
                      <td>Total:</td>
                      <td className="text-end">LKR {totals.total}</td>
                    </tr>
                  </tbody>
                </Table>

                <Form.Group className="mb-3">
                  <Form.Label>Payment Method</Form.Label>
                  <Form.Select
                    value={paymentMethod}
                    onChange={(e) => setPaymentMethod(e.target.value)}
                  >
                    <option value="CASH">Cash</option>
                    <option value="CREDIT_CARD">Credit Card</option>
                    <option value="DEBIT_CARD">Debit Card</option>
                  </Form.Select>
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Customer</Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Walk-in Customer"
                    value={selectedCustomer ? selectedCustomer.fullName : 'Walk-in Customer'}
                    readOnly
                  />
                  <Form.Text className="text-muted">
                    Leave as Walk-in Customer if not registered
                  </Form.Text>
                </Form.Group>

                <div className="d-grid">
                  <Button
                    variant="success"
                    size="lg"
                    onClick={processTransaction}
                    disabled={loading || cart.length === 0}
                  >
                    {loading ? (
                      <>
                        <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                        Processing...
                      </>
                    ) : (
                      <>
                        <FaReceipt className="me-2" />
                        Complete Sale
                      </>
                    )}
                  </Button>
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default CashierDashboard;