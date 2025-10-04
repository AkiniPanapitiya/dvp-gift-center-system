import React from 'react';
import { Container, Row, Col } from 'react-bootstrap';

const Footer = () => {
  return (
    <footer className="footer">
      <Container>
        <Row>
          <Col md={4}>
            <h5>DVP Gift Center</h5>
            <p>Your one-stop destination for unique gifts and special occasions.</p>
          </Col>
          <Col md={4}>
            <h5>Quick Links</h5>
            <ul className="list-unstyled">
              <li><a href="/" className="text-light">Home</a></li>
              <li><a href="/products" className="text-light">Products</a></li>
              <li><a href="/about" className="text-light">About Us</a></li>
              <li><a href="/contact" className="text-light">Contact</a></li>
            </ul>
          </Col>
          <Col md={4}>
            <h5>Contact Info</h5>
            <p>Email: info@dvpgiftcenter.com</p>
            <p>Phone: +1 (555) 123-4567</p>
            <p>Address: 123 Gift Street, City, State 12345</p>
          </Col>
        </Row>
        <hr className="my-4" />
        <Row>
          <Col className="text-center">
            <p>&copy; 2024 DVP Gift Center. All rights reserved.</p>
          </Col>
        </Row>
      </Container>
    </footer>
  );
};

export default Footer;