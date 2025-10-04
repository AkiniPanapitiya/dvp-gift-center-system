import React, { useState } from 'react';
import { Container, Row, Col, Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../contexts/CartContext';
import { useAuth } from '../contexts/AuthContext';
import axios from 'axios';
import { formatLKR } from '../utils/currency';

const Checkout = () => {
  const [formData, setFormData] = useState({
    shippingAddress: '',
    paymentMethod: 'CREDIT_CARD',
    cardNumber: '',
    expiryDate: '',
    cvv: '',
    cardName: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [orderSuccess, setOrderSuccess] = useState(false);

  const { cartItems, getCartTotal, clearCart } = useCart();
  const { getAuthToken } = useAuth();
  const navigate = useNavigate();

  const formatPrice = (price) => formatLKR(price);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const validateForm = () => {
    if (!formData.shippingAddress.trim()) {
      setError('Shipping address is required');
      return false;
    }
    if (formData.paymentMethod === 'CREDIT_CARD') {
      if (!formData.cardNumber.trim() || !formData.expiryDate.trim() || !formData.cvv.trim() || !formData.cardName.trim()) {
        setError('All credit card fields are required');
        return false;
      }
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setLoading(true);
    setError('');

    try {
      const orderData = {
        items: cartItems.map(item => ({
          productId: item.productId,
          quantity: item.quantity
        })),
        shippingAddress: {
          addressLine1: formData.shippingAddress,
          city: "Default City", // You might want to add these fields to the form
          postalCode: "00000"
        },
        paymentMethod: formData.paymentMethod
      };

      const token = getAuthToken();
      const response = await axios.post(
        '/api/online/checkout',
        orderData,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );

      if (response.status === 200) {
        setOrderSuccess(true);
        clearCart();
        setTimeout(() => {
          navigate('/');
        }, 3000);
      }
    } catch (error) {
      setError('Failed to process order. Please try again.');
      console.error('Checkout error:', error);
    } finally {
      setLoading(false);
    }
  };

  if (cartItems.length === 0 && !orderSuccess) {
    return (
      <Container className="py-5">
        <Alert variant="warning">
          Your cart is empty. Please add some items before checkout.
        </Alert>
        <Button onClick={() => navigate('/products')}>Continue Shopping</Button>
      </Container>
    );
  }

  if (orderSuccess) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8} className="text-center">
            <Card className="border-success">
              <Card.Body className="py-5">
                <div className="text-success mb-4">
                  <i className="fas fa-check-circle fa-5x"></i>
                </div>
                <h2 className="text-success mb-4">Order Placed Successfully!</h2>
                <p className="mb-4">
                  Thank you for your order. You will receive an email confirmation shortly.
                </p>
                <p className="text-muted">
                  Redirecting to home page in a few seconds...
                </p>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    );
  }

  const subtotal = getCartTotal();
  const tax = subtotal * 0.05;
  const total = subtotal + tax;

  return (
    <Container className="py-5">
      <h2 className="mb-4">Checkout</h2>
      
      <Row>
        <Col lg={8}>
          <Card>
            <Card.Header>
              <h5 className="mb-0">Shipping & Payment Information</h5>
            </Card.Header>
            <Card.Body>
              {error && <Alert variant="danger">{error}</Alert>}
              
              <Form onSubmit={handleSubmit}>
                <h6 className="mb-3">Shipping Address</h6>
                <Form.Group className="mb-4">
                  <Form.Label>Full Address *</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={3}
                    name="shippingAddress"
                    value={formData.shippingAddress}
                    onChange={handleChange}
                    placeholder="Enter your complete shipping address"
                    required
                  />
                </Form.Group>

                <h6 className="mb-3">Payment Method</h6>
                <Form.Group className="mb-3">
                  <Form.Check
                    type="radio"
                    id="credit-card"
                    name="paymentMethod"
                    value="CREDIT_CARD"
                    label="Credit Card"
                    checked={formData.paymentMethod === 'CREDIT_CARD'}
                    onChange={handleChange}
                  />
                  <Form.Check
                    type="radio"
                    id="cash-on-delivery"
                    name="paymentMethod"
                    value="CASH_ON_DELIVERY"
                    label="Cash on Delivery"
                    checked={formData.paymentMethod === 'CASH_ON_DELIVERY'}
                    onChange={handleChange}
                  />
                </Form.Group>

                {formData.paymentMethod === 'CREDIT_CARD' && (
                  <>
                    <h6 className="mb-3">Credit Card Information</h6>
                    <Row>
                      <Col md={6}>
                        <Form.Group className="mb-3">
                          <Form.Label>Card Number *</Form.Label>
                          <Form.Control
                            type="text"
                            name="cardNumber"
                            value={formData.cardNumber}
                            onChange={handleChange}
                            placeholder="1234 5678 9012 3456"
                            required={formData.paymentMethod === 'CREDIT_CARD'}
                          />
                        </Form.Group>
                      </Col>
                      <Col md={3}>
                        <Form.Group className="mb-3">
                          <Form.Label>Expiry Date *</Form.Label>
                          <Form.Control
                            type="text"
                            name="expiryDate"
                            value={formData.expiryDate}
                            onChange={handleChange}
                            placeholder="MM/YY"
                            required={formData.paymentMethod === 'CREDIT_CARD'}
                          />
                        </Form.Group>
                      </Col>
                      <Col md={3}>
                        <Form.Group className="mb-3">
                          <Form.Label>CVV *</Form.Label>
                          <Form.Control
                            type="text"
                            name="cvv"
                            value={formData.cvv}
                            onChange={handleChange}
                            placeholder="123"
                            required={formData.paymentMethod === 'CREDIT_CARD'}
                          />
                        </Form.Group>
                      </Col>
                    </Row>
                    <Form.Group className="mb-3">
                      <Form.Label>Cardholder Name *</Form.Label>
                      <Form.Control
                        type="text"
                        name="cardName"
                        value={formData.cardName}
                        onChange={handleChange}
                        placeholder="John Doe"
                        required={formData.paymentMethod === 'CREDIT_CARD'}
                      />
                    </Form.Group>
                  </>
                )}

                <div className="d-grid mt-4">
                  <Button
                    type="submit"
                    variant="success"
                    size="lg"
                    disabled={loading}
                  >
                    {loading ? (
                      <>
                        <Spinner size="sm" className="me-2" />
                        Processing Order...
                      </>
                    ) : (
                      `Place Order - ${formatPrice(total)}`
                    )}
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>
        </Col>

        <Col lg={4}>
          <Card>
            <Card.Header>
              <h5 className="mb-0">Order Summary</h5>
            </Card.Header>
            <Card.Body>
              {cartItems.map((item) => (
                <div key={item.productId} className="d-flex justify-content-between mb-2">
                  <span>{item.productName} x {item.quantity}</span>
                  <span>{formatPrice(item.onlinePrice * item.quantity)}</span>
                </div>
              ))}
              
              <hr />
              
              <div className="d-flex justify-content-between mb-2">
                <span>Subtotal:</span>
                <span>{formatPrice(subtotal)}</span>
              </div>
              
              <div className="d-flex justify-content-between mb-2">
                <span>Shipping:</span>
                <span className="text-success">Free</span>
              </div>
              
              <div className="d-flex justify-content-between mb-2">
                <span>Tax (5%):</span>
                <span>{formatPrice(tax)}</span>
              </div>
              
              <hr />
              
              <div className="d-flex justify-content-between">
                <strong>Total:</strong>
                <strong>{formatPrice(total)}</strong>
              </div>
            </Card.Body>
          </Card>

          <Alert variant="info" className="mt-3">
            <small>
              <strong>Secure Payment:</strong> Your payment information is encrypted and secure.
            </small>
          </Alert>
        </Col>
      </Row>
    </Container>
  );
};

export default Checkout;