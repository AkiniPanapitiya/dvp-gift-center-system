import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Table, Form, Button, InputGroup, Badge, Alert } from 'react-bootstrap';
import { FaSearch, FaPrint } from 'react-icons/fa';

const CashierTransactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [bill, setBill] = useState('');
  const [start, setStart] = useState(''); // yyyy-MM-dd
  const [end, setEnd] = useState('');     // yyyy-MM-dd
  const [loading, setLoading] = useState(false);
  const [alert, setAlert] = useState({ show: false, message: '', type: 'success' });
  // navigation handled by global navbar

  const showAlert = (message, type) => {
    setAlert({ show: true, message, type });
    setTimeout(() => setAlert({ show: false, message: '', type: 'success' }), 3000);
  };

  const loadTransactions = async (params = {}) => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      const query = new URLSearchParams();
      if (params.bill) query.set('bill', params.bill);
      if (params.start && params.end) {
        // Build ISO datetime strings expected by backend
        query.set('start', `${params.start}T00:00:00`);
        query.set('end', `${params.end}T23:59:59`);
      }
      const qs = query.toString();
      const url = `/api/cashier/transactions${qs ? `?${qs}` : ''}`;
      const res = await fetch(url, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      if (!res.ok) throw new Error('Failed to load transactions');
      const data = await res.json();
      setTransactions(data.data || []);
    } catch (e) {
      showAlert(e.message, 'danger');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTransactions();
  }, []);

  const handleSearch = () => {
    loadTransactions({ bill, start, end });
  };

  const clearFilters = () => {
    setBill('');
    setStart('');
    setEnd('');
    loadTransactions();
  };

  const printReceipt = async (tx) => {
    try {
      const token = localStorage.getItem('token');
      const res = await fetch(`/api/cashier/transactions/${tx.transactionId}/receipt`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('Failed to load receipt');
      const { data } = await res.json();
      const printWindow = window.open('', '_blank');
      const html = generateReceiptHtml(data);
      printWindow.document.write(html);
      printWindow.document.close();
      printWindow.print();
    } catch (e) {
      showAlert(e.message, 'danger');
    }
  };

  const generateReceiptHtml = (transaction) => `
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

  return (
    <div className="pos-system">

      {alert.show && (
        <Alert variant={alert.type} className="m-3" dismissible onClose={() => setAlert({ show: false, message: '', type: 'success' })}>
          {alert.message}
        </Alert>
      )}

      <Container fluid className="py-3">
        <Card className="mb-3">
          <Card.Header>
            <Row className="align-items-end">
              <Col md={4} className="mb-2">
                <Form.Label>Bill Number</Form.Label>
                <InputGroup>
                  <Form.Control placeholder="Search by bill no" value={bill} onChange={e => setBill(e.target.value)} />
                  <Button variant="outline-secondary" onClick={handleSearch}><FaSearch /></Button>
                </InputGroup>
              </Col>
              <Col md={3} className="mb-2">
                <Form.Label>Start Date</Form.Label>
                <Form.Control type="date" value={start} onChange={e => setStart(e.target.value)} />
              </Col>
              <Col md={3} className="mb-2">
                <Form.Label>End Date</Form.Label>
                <Form.Control type="date" value={end} onChange={e => setEnd(e.target.value)} />
              </Col>
              <Col md={2} className="mb-2 d-grid">
                <Button variant="primary" onClick={handleSearch}>Filter</Button>
                <Button variant="outline-secondary" className="mt-2" onClick={clearFilters}>Clear</Button>
              </Col>
            </Row>
          </Card.Header>
          <Card.Body style={{ maxHeight: '60vh', overflowY: 'auto' }}>
            {loading ? (
              <div className="text-center p-4">
                <div className="spinner-border" role="status"><span className="visually-hidden">Loading...</span></div>
              </div>
            ) : transactions.length === 0 ? (
              <p className="text-center text-muted m-0">No transactions found</p>
            ) : (
              <Table hover size="sm" responsive>
                <thead>
                  <tr>
                    <th>Bill No</th>
                    <th>Date</th>
                    <th>Customer</th>
                    <th>Cashier</th>
                    <th className="text-end">Net Amount (LKR)</th>
                    <th>Status</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  {transactions.map(tx => (
                    <tr key={tx.transactionId}>
                      <td>{tx.billNumber}</td>
                      <td>{new Date(tx.transactionDate).toLocaleString()}</td>
                      <td>{tx.customerName}</td>
                      <td>{tx.cashierName}</td>
                      <td className="text-end">{Number(tx.netAmount).toFixed(2)}</td>
                      <td>
                        <Badge bg={tx.status === 'completed' ? 'success' : tx.status === 'pending' ? 'warning' : 'secondary'}>
                          {tx.status}
                        </Badge>
                      </td>
                      <td className="text-end">
                        <Button size="sm" variant="outline-primary" onClick={() => printReceipt(tx)}>
                          <FaPrint className="me-1" /> Receipt
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            )}
          </Card.Body>
        </Card>
      </Container>
    </div>
  );
};

export default CashierTransactions;
