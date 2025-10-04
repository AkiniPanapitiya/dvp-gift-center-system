import React from 'react';
import { Container, Row, Col, Card, Nav } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { useLocation } from 'react-router-dom';

const AdminLayout = ({ children }) => {
  const location = useLocation();
  
  return (
    <Container fluid className="py-4">
      <Row>
        <Col md={3} lg={2}>
          <Card>
            <Card.Header>
              <h5 className="mb-0">Admin Panel</h5>
            </Card.Header>
            <Card.Body className="p-0">
              <Nav variant="pills" className="flex-column">
                <LinkContainer to="/admin/dashboard">
                  <Nav.Link className={location.pathname === '/admin/dashboard' ? 'active' : ''}>
                    Dashboard Overview
                  </Nav.Link>
                </LinkContainer>
                <LinkContainer to="/admin/products">
                  <Nav.Link className={location.pathname === '/admin/products' ? 'active' : ''}>
                    Manage Products
                  </Nav.Link>
                </LinkContainer>
                <LinkContainer to="/admin/categories">
                  <Nav.Link className={location.pathname === '/admin/categories' ? 'active' : ''}>
                    Manage Categories
                  </Nav.Link>
                </LinkContainer>
                <LinkContainer to="/admin/orders">
                  <Nav.Link className={location.pathname === '/admin/orders' ? 'active' : ''}>
                    Manage Orders
                  </Nav.Link>
                </LinkContainer>
                <LinkContainer to="/admin/transactions">
                  <Nav.Link className={location.pathname === '/admin/transactions' ? 'active' : ''}>
                    View Transactions
                  </Nav.Link>
                </LinkContainer>
              </Nav>
            </Card.Body>
          </Card>
        </Col>

        <Col md={9} lg={10}>
          {children}
        </Col>
      </Row>
    </Container>
  );
};

export default AdminLayout;