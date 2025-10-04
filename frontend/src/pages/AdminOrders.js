import React, { useEffect, useState, useCallback } from 'react';
import axios from 'axios';
import { Table, Card, Row, Col, Form, Button, Badge, Modal, Spinner, Alert, InputGroup } from 'react-bootstrap';
import AdminLayout from '../components/AdminLayout';

const STATUS_COLORS = {
  PLACED: 'secondary',
  PROCESSING: 'primary',
  SHIPPED: 'info',
  DELIVERED: 'success',
  CANCELLED: 'danger'
};

const AdminOrders = () => {
  const [orders, setOrders] = useState([]);
  const [statusFilter, setStatusFilter] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [detail, setDetail] = useState(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [showDetail, setShowDetail] = useState(false);
  const [updateOrder, setUpdateOrder] = useState(null);
  const [updating, setUpdating] = useState(false);
  const [updateError, setUpdateError] = useState(null);

  const fetchOrders = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params = {};
      if (statusFilter) params.status = statusFilter;
      const res = await axios.get('/api/admin/orders', { params });
      setOrders(res.data.data || []);
    } catch (e) {
      setError(e.response?.data?.message || 'Failed to load orders');
    } finally {
      setLoading(false);
    }
  }, [statusFilter]);

  const openDetail = async (orderId) => {
    setShowDetail(true);
    setDetail(null);
    setDetailLoading(true);
    try {
      const res = await axios.get(`/api/admin/orders/${orderId}`);
      setDetail(res.data.data);
    } catch (e) {
      setDetail(null);
    } finally {
      setDetailLoading(false);
    }
  };

  const openUpdate = (order) => {
    setUpdateOrder({ id: order.orderId, status: order.orderStatus || '', trackingNumber: '' });
    setUpdateError(null);
  };

  const submitUpdate = async () => {
    if (!updateOrder?.status) {
      setUpdateError('Status is required');
      return;
    }
    setUpdating(true);
    setUpdateError(null);
    try {
      await axios.put(`/api/admin/orders/${updateOrder.id}/status`, {
        status: updateOrder.status,
        trackingNumber: updateOrder.trackingNumber || undefined
      });
      setUpdateOrder(null);
      await fetchOrders();
    } catch (e) {
      setUpdateError(e.response?.data?.message || 'Failed to update status');
    } finally {
      setUpdating(false);
    }
  };

  useEffect(() => { fetchOrders(); }, [fetchOrders]);

  return (
    <AdminLayout>
      <Card className="mb-3">
        <Card.Header>
          <Row className="align-items-center">
            <Col><h5 className="mb-0">Orders</h5></Col>
            <Col md="auto">
              <InputGroup>
                <Form.Select
                  value={statusFilter}
                  onChange={(e) => setStatusFilter(e.target.value)}
                >
                  <option value="">All Statuses</option>
                  <option value="PLACED">PLACED</option>
                  <option value="PROCESSING">PROCESSING</option>
                  <option value="SHIPPED">SHIPPED</option>
                  <option value="DELIVERED">DELIVERED</option>
                  <option value="CANCELLED">CANCELLED</option>
                </Form.Select>
                <Button variant="secondary" onClick={fetchOrders}>Apply</Button>
              </InputGroup>
            </Col>
          </Row>
        </Card.Header>
        <Card.Body className="p-0">
          {error && <Alert variant="danger" className="m-3">{error}</Alert>}
          {loading ? (
            <div className="text-center p-4"><Spinner animation="border" /></div>
          ) : (
            <div className="table-responsive">
              <Table hover className="mb-0">
                <thead>
                  <tr>
                    <th>Order ID</th>
                    <th>Bill #</th>
                    <th>Customer</th>
                    <th>Placed At</th>
                    <th>Status</th>
                    <th className="text-end">Net</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map(o => (
                    <tr key={o.orderId}>
                      <td>{o.orderId}</td>
                      <td>{o.billNumber || '-'}</td>
                      <td>
                        <div>{o.customerUsername}</div>
                        <small className="text-muted">{o.customerEmail}</small>
                      </td>
                      <td>{o.placedAt ? new Date(o.placedAt).toLocaleString() : '-'}</td>
                      <td>
                        <Badge bg={STATUS_COLORS[o.orderStatus] || 'secondary'}>{o.orderStatus || 'N/A'}</Badge>
                      </td>
                      <td className="text-end">{o.netAmount != null ? Number(o.netAmount).toFixed(2) : '-'}</td>
                      <td>
                        <Button size="sm" variant="outline-primary" className="me-2" onClick={() => openDetail(o.orderId)}>View</Button>
                        <Button size="sm" variant="outline-secondary" onClick={() => openUpdate(o)}>Update</Button>
                      </td>
                    </tr>
                  ))}
                  {orders.length === 0 && (
                    <tr><td colSpan={7} className="text-center p-4">No orders</td></tr>
                  )}
                </tbody>
              </Table>
            </div>
          )}
        </Card.Body>
      </Card>

      {/* Detail Modal */}
      <Modal show={showDetail} onHide={() => setShowDetail(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Order Detail</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {detailLoading ? (
            <div className="text-center p-3"><Spinner animation="border" /></div>
          ) : detail ? (
            <>
              <Row className="mb-3">
                <Col>
                  <div><strong>Order:</strong> {detail.orderId}</div>
                  <div><strong>Bill #:</strong> {detail.billNumber || '-'}</div>
                  <div><strong>Status:</strong> {detail.orderStatus}</div>
                  <div><strong>Placed:</strong> {detail.placedAt ? new Date(detail.placedAt).toLocaleString() : '-'}</div>
                </Col>
                <Col>
                  <div><strong>Customer:</strong> {detail.customerFullName} ({detail.customerUsername})</div>
                  <div><strong>Email:</strong> {detail.customerEmail}</div>
                  <div><strong>Shipping:</strong> {detail.shippingMethod || '-'}</div>
                  <div><strong>Tracking:</strong> {detail.trackingNumber || '-'}</div>
                </Col>
              </Row>
              <h6>Items</h6>
              <Table size="sm" bordered hover>
                <thead>
                  <tr>
                    <th>Product</th>
                    <th className="text-end">Qty</th>
                    <th className="text-end">Unit</th>
                    <th className="text-end">Total</th>
                  </tr>
                </thead>
                <tbody>
                  {(detail.items || []).map((it, idx) => (
                    <tr key={idx}>
                      <td>{it.productName}</td>
                      <td className="text-end">{it.quantity}</td>
                      <td className="text-end">{Number(it.unitPrice).toFixed(2)}</td>
                      <td className="text-end">{Number(it.lineTotal).toFixed(2)}</td>
                    </tr>
                  ))}
                </tbody>
              </Table>
              <Row>
                <Col md={{ span: 4, offset: 8 }}>
                  <Table size="sm" className="mb-0">
                    <tbody>
                      <tr><td>Total</td><td className="text-end">{Number(detail.totalAmount || 0).toFixed(2)}</td></tr>
                      <tr><td>Tax</td><td className="text-end">{Number(detail.taxAmount || 0).toFixed(2)}</td></tr>
                      <tr><td>Discount</td><td className="text-end">{Number(detail.discountAmount || 0).toFixed(2)}</td></tr>
                      <tr><td><strong>Net</strong></td><td className="text-end"><strong>{Number(detail.netAmount || 0).toFixed(2)}</strong></td></tr>
                    </tbody>
                  </Table>
                </Col>
              </Row>
              <h6 className="mt-3">Payments</h6>
              <Table size="sm" bordered hover>
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Method</th>
                    <th>Reference</th>
                    <th className="text-end">Amount</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {(detail.payments || []).map((p, idx) => (
                    <tr key={idx}>
                      <td>{p.paymentDate ? new Date(p.paymentDate).toLocaleString() : '-'}</td>
                      <td>{p.method}</td>
                      <td>{p.referenceNumber}</td>
                      <td className="text-end">{Number(p.amountPaid || 0).toFixed(2)}</td>
                      <td>{p.status}</td>
                    </tr>
                  ))}
                  {(!detail.payments || detail.payments.length === 0) && (
                    <tr><td colSpan={5} className="text-center">No payments</td></tr>
                  )}
                </tbody>
              </Table>
            </>
          ) : (
            <Alert variant="warning">Failed to load order detail</Alert>
          )}
        </Modal.Body>
      </Modal>

      {/* Update Status Modal */}
      <Modal show={!!updateOrder} onHide={() => setUpdateOrder(null)}>
        <Modal.Header closeButton>
          <Modal.Title>Update Order Status</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {updateError && <Alert variant="danger">{updateError}</Alert>}
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Status</Form.Label>
              <Form.Select
                value={updateOrder?.status || ''}
                onChange={(e) => setUpdateOrder(prev => ({ ...prev, status: e.target.value }))}
                required
              >
                <option value="">Select status</option>
                <option value="PLACED">PLACED</option>
                <option value="PROCESSING">PROCESSING</option>
                <option value="SHIPPED">SHIPPED</option>
                <option value="DELIVERED">DELIVERED</option>
                <option value="CANCELLED">CANCELLED</option>
              </Form.Select>
            </Form.Group>
            <Form.Group>
              <Form.Label>Tracking Number (optional)</Form.Label>
              <Form.Control
                type="text"
                value={updateOrder?.trackingNumber || ''}
                onChange={(e) => setUpdateOrder(prev => ({ ...prev, trackingNumber: e.target.value }))}
              />
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setUpdateOrder(null)}>Cancel</Button>
          <Button variant="primary" onClick={submitUpdate} disabled={updating}>
            {updating ? 'Updating...' : 'Update'}
          </Button>
        </Modal.Footer>
      </Modal>
    </AdminLayout>
  );
};

export default AdminOrders;
