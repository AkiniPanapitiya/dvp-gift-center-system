import React, { useEffect, useState } from 'react';
import { Table, Container, Spinner, Alert } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import axios from 'axios';
import { formatLKR } from '../utils/currency';

const Orders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const res = await axios.get('/api/online/orders');
        if (res?.data?.success) {
          setOrders(res.data.data || []);
        } else {
          setError(res?.data?.message || 'Failed to load orders');
        }
      } catch (e) {
        setError(e.response?.data?.message || 'Failed to load orders');
      } finally {
        setLoading(false);
      }
    };
    fetchOrders();
  }, []);

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

  return (
    <Container className="py-4">
      <h3>Your Orders</h3>
      {orders.length === 0 ? (
        <p>No orders yet.</p>
      ) : (
        <Table striped bordered hover responsive className="mt-3">
          <thead>
            <tr>
              <th>Order #</th>
              <th>Bill #</th>
              <th>Status</th>
              <th>Placed At</th>
              <th>Total</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {orders.map(o => (
              <tr key={o.orderId}>
                <td>{o.orderId}</td>
                <td>{o.billNumber || '-'}</td>
                <td>{o.orderStatus}</td>
                <td>{o.placedAt?.replace('T', ' ').substring(0, 19)}</td>
                <td>{o.netAmount != null ? formatLKR(Number(o.netAmount)) : '-'}</td>
                <td>
                  <Link to={`/orders/${o.orderId}`}>View</Link>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}
    </Container>
  );
};

export default Orders;
