import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Container, Table, Spinner, Alert, Row, Col, Card } from 'react-bootstrap';
import axios from 'axios';
import { formatLKR } from '../utils/currency';

const OrderDetail = () => {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDetail = async () => {
      try {
        const res = await axios.get(`/api/online/orders/${id}`);
        if (res?.data?.success) {
          setOrder(res.data.data);
        } else {
          setError(res?.data?.message || 'Failed to load order');
        }
      } catch (e) {
        setError(e.response?.data?.message || 'Failed to load order');
      } finally {
        setLoading(false);
      }
    };
    fetchDetail();
  }, [id]);

  if (loading) {
    return (
      <Container className="py-4 text-center">
        <Spinner animation="border" />
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="py-4">
        <Alert variant="danger">{error}</Alert>
      </Container>
    );
  }

  if (!order) return null;

  return (
    <Container className="py-4">
      <h3>Order #{order.orderId}</h3>
      <Row className="mt-3">
        <Col md={6} className="mb-3">
          <Card>
            <Card.Body>
              <Card.Title>Order Info</Card.Title>
              <p><strong>Bill #:</strong> {order.billNumber || '-'}</p>
              <p><strong>Status:</strong> {order.orderStatus}</p>
              <p><strong>Placed At:</strong> {order.placedAt?.replace('T', ' ').substring(0, 19)}</p>
              <p><strong>Shipping:</strong> {order.shippingMethod || '-'}</p>
              <p><strong>Tracking:</strong> {order.trackingNumber || '-'}</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={6} className="mb-3">
          <Card>
            <Card.Body>
              <Card.Title>Shipping Address</Card.Title>
              <p>{order.addressLine1}</p>
              {order.addressLine2 && <p>{order.addressLine2}</p>}
              <p>{order.city} {order.postalCode}</p>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Card className="mt-3">
        <Card.Body>
          <Card.Title>Items</Card.Title>
          <Table striped bordered hover responsive className="mt-2">
            <thead>
              <tr>
                <th>Product</th>
                <th>Qty</th>
                <th>Unit Price</th>
                <th>Line Total</th>
              </tr>
            </thead>
            <tbody>
              {order.items?.map((it, idx) => (
                <tr key={idx}>
                  <td>{it.productName}</td>
                  <td>{it.quantity}</td>
                  <td>{formatLKR(Number(it.unitPrice))}</td>
                  <td>{formatLKR(Number(it.lineTotal))}</td>
                </tr>
              ))}
            </tbody>
          </Table>
          <div className="text-end">
            <div><strong>Subtotal:</strong> {formatLKR(Number(order.totalAmount))}</div>
            <div><strong>Tax:</strong> {formatLKR(Number(order.taxAmount || 0))}</div>
            <div><strong>Total:</strong> {formatLKR(Number(order.netAmount))}</div>
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default OrderDetail;
