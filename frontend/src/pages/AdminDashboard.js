import React from 'react';
import { Row, Col, Card } from 'react-bootstrap';
import AdminLayout from '../components/AdminLayout';
import { formatLKR } from '../utils/currency';

const AdminDashboard = () => {
  return (
    <AdminLayout>
      <div>
        <h2 className="mb-4">Dashboard Overview</h2>
        <Row>
          <Col md={6} lg={3} className="mb-4">
            <Card className="text-center">
              <Card.Body>
                <h3 className="text-primary">156</h3>
                <p className="mb-0">Total Products</p>
              </Card.Body>
            </Card>
          </Col>
          <Col md={6} lg={3} className="mb-4">
            <Card className="text-center">
              <Card.Body>
                <h3 className="text-success">89</h3>
                <p className="mb-0">Orders Today</p>
              </Card.Body>
            </Card>
          </Col>
          <Col md={6} lg={3} className="mb-4">
            <Card className="text-center">
              <Card.Body>
                <h3 className="text-info">1,245</h3>
                <p className="mb-0">Total Users</p>
              </Card.Body>
            </Card>
          </Col>
          <Col md={6} lg={3} className="mb-4">
            <Card className="text-center">
              <Card.Body>
                <h3 className="text-warning">{formatLKR(15432)}</h3>
                <p className="mb-0">Revenue Today</p>
              </Card.Body>
            </Card>
          </Col>
        </Row>

        <Row>
          <Col lg={6} className="mb-4">
            <Card>
              <Card.Header>
                <h5>Recent Orders</h5>
              </Card.Header>
              <Card.Body>
                <div className="d-flex justify-content-between mb-2">
                  <span>Order #1001 - John Doe</span>
                  <span className="text-success">{formatLKR(45.99)}</span>
                </div>
                <div className="d-flex justify-content-between mb-2">
                  <span>Order #1002 - Jane Smith</span>
                  <span className="text-success">{formatLKR(78.50)}</span>
                </div>
                <div className="d-flex justify-content-between mb-2">
                  <span>Order #1003 - Bob Johnson</span>
                  <span className="text-success">{formatLKR(125.25)}</span>
                </div>
                <div className="d-flex justify-content-between">
                  <span>Order #1004 - Alice Brown</span>
                  <span className="text-success">{formatLKR(89.99)}</span>
                </div>
              </Card.Body>
            </Card>
          </Col>

          <Col lg={6} className="mb-4">
            <Card>
              <Card.Header>
                <h5>Low Stock Alert</h5>
              </Card.Header>
              <Card.Body>
                <div className="d-flex justify-content-between mb-2">
                  <span>$50 Gift Card</span>
                  <span className="text-warning">5 left</span>
                </div>
                <div className="d-flex justify-content-between mb-2">
                  <span>Premium Gift Box</span>
                  <span className="text-danger">2 left</span>
                </div>
                <div className="d-flex justify-content-between mb-2">
                  <span>Chocolate Assortment</span>
                  <span className="text-warning">8 left</span>
                </div>
                <div className="d-flex justify-content-between">
                  <span>Wine & Cheese Set</span>
                  <span className="text-warning">6 left</span>
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </div>
    </AdminLayout>
  );
};

export default AdminDashboard;