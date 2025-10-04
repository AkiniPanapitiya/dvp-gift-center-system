import React, { useEffect, useState, useCallback } from 'react';
import axios from 'axios';
import { Table, Card, Row, Col, Form, Button, Modal, Spinner, Alert, InputGroup, Badge } from 'react-bootstrap';
import AdminLayout from '../components/AdminLayout';

const AdminTransactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [source, setSource] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [detail, setDetail] = useState(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [showDetail, setShowDetail] = useState(false);

  const fetchTransactions = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params = {};
      if (source) params.source = source;
      const res = await axios.get('/api/admin/transactions', { params });
      setTransactions(res.data.data || []);
    } catch (e) {
      setError(e.response?.data?.message || 'Failed to load transactions');
    } finally {
      setLoading(false);
    }
  }, [source]);

  const openDetail = async (transactionId) => {
    setShowDetail(true);
    setDetail(null);
    setDetailLoading(true);
    try {
      const res = await axios.get(`/api/admin/transactions/${transactionId}`);
      setDetail(res.data.data);
    } catch (e) {
      setDetail(null);
    } finally {
      setDetailLoading(false);
    }
  };

  useEffect(() => { fetchTransactions(); }, [fetchTransactions]);

  return (
    <AdminLayout>
      <Card className="mb-3">
        <Card.Header>
          <Row className="align-items-center">
            <Col><h5 className="mb-0">Transactions</h5></Col>
            <Col md="auto">
              <InputGroup>
                <Form.Select value={source} onChange={(e) => setSource(e.target.value)}>
                  <option value="">All Sources</option>
                  <option value="online_sale">Online Sale</option>
                  <option value="pos">POS</option>
                </Form.Select>
                <Button variant="secondary" onClick={fetchTransactions}>Apply</Button>
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
                    <th>Txn ID</th>
                    <th>Bill #</th>
                    <th>Customer</th>
                    <th>Date</th>
                    <th>Source</th>
                    <th>Status</th>
                    <th className="text-end">Net</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {transactions.map(t => (
                    <tr key={t.transactionId}>
                      <td>{t.transactionId}</td>
                      <td>{t.billNumber}</td>
                      <td>
                        <div>{t.customerUsername}</div>
                        <small className="text-muted">{t.customerEmail}</small>
                      </td>
                      <td>{t.transactionDate ? new Date(t.transactionDate).toLocaleString() : '-'}</td>
                      <td><Badge bg={t.source === 'online_sale' ? 'info' : 'secondary'}>{t.source}</Badge></td>
                      <td>{t.status}</td>
                      <td className="text-end">{t.netAmount != null ? Number(t.netAmount).toFixed(2) : '-'}</td>
                      <td>
                        <Button size="sm" variant="outline-primary" onClick={() => openDetail(t.transactionId)}>View</Button>
                      </td>
                    </tr>
                  ))}
                  {transactions.length === 0 && (
                    <tr><td colSpan={8} className="text-center p-4">No transactions</td></tr>
                  )}
                </tbody>
              </Table>
            </div>
          )}
        </Card.Body>
      </Card>

      <Modal show={showDetail} onHide={() => setShowDetail(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Transaction Detail</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {detailLoading ? (
            <div className="text-center p-3"><Spinner animation="border" /></div>
          ) : detail ? (
            <>
              <Row className="mb-3">
                <Col>
                  <div><strong>Transaction:</strong> {detail.transactionId}</div>
                  <div><strong>Bill #:</strong> {detail.billNumber}</div>
                  <div><strong>Date:</strong> {detail.transactionDate ? new Date(detail.transactionDate).toLocaleString() : '-'}</div>
                </Col>
                <Col>
                  <div><strong>Customer:</strong> {detail.customerUsername}</div>
                  <div><strong>Email:</strong> {detail.customerEmail}</div>
                  <div><strong>Source:</strong> {detail.source}</div>
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
            <Alert variant="warning">Failed to load transaction detail</Alert>
          )}
        </Modal.Body>
      </Modal>
    </AdminLayout>
  );
};

export default AdminTransactions;
