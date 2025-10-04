import React from 'react';
import { Navbar as BSNavbar, Nav, Container, Badge } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { FiShoppingCart, FiUser, FiLogOut, FiSettings } from 'react-icons/fi';
import { useAuth } from '../contexts/AuthContext';
import { useCart } from '../contexts/CartContext';

const Navbar = () => {
  const { user, logout, isAuthenticated, isAdmin } = useAuth();
  const { getCartItemCount } = useCart();

  return (
    <BSNavbar bg="dark" variant="dark" expand="lg" className="mb-3">
      <Container>
        <LinkContainer to="/">
          <BSNavbar.Brand>DVP Gift Center</BSNavbar.Brand>
        </LinkContainer>
        
        <BSNavbar.Toggle aria-controls="basic-navbar-nav" />
        
        <BSNavbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <LinkContainer to="/">
              <Nav.Link>Home</Nav.Link>
            </LinkContainer>
            <LinkContainer to="/products">
              <Nav.Link>Products</Nav.Link>
            </LinkContainer>
          </Nav>
          
          <Nav>
            {isAuthenticated ? (
              <>
                {isAdmin && (
                  <LinkContainer to="/admin">
                    <Nav.Link>Admin Dashboard</Nav.Link>
                  </LinkContainer>
                )}
                
                {!isAdmin && (
                  <>
                    <LinkContainer to="/orders">
                      <Nav.Link>My Orders</Nav.Link>
                    </LinkContainer>
                    <LinkContainer to="/cart">
                      <Nav.Link>
                        <FiShoppingCart className="me-1" />
                        Cart
                        {getCartItemCount() > 0 && (
                          <Badge bg="danger" className="ms-1">
                            {getCartItemCount()}
                          </Badge>
                        )}
                      </Nav.Link>
                    </LinkContainer>
                  </>
                )}
                
                {isAdmin && (
                  <LinkContainer to="/admin">
                    <Nav.Link>
                      <FiSettings className="me-1" />
                      Admin Panel
                    </Nav.Link>
                  </LinkContainer>
                )}
                
                <Nav.Link>
                  <FiUser className="me-1" />
                  {user.username}
                </Nav.Link>
                
                <Nav.Link onClick={logout}>
                  <FiLogOut className="me-1" />
                  Logout
                </Nav.Link>
              </>
            ) : (
              <>
                <LinkContainer to="/login">
                  <Nav.Link>Login</Nav.Link>
                </LinkContainer>
                <LinkContainer to="/register">
                  <Nav.Link>Register</Nav.Link>
                </LinkContainer>
              </>
            )}
          </Nav>
        </BSNavbar.Collapse>
      </Container>
    </BSNavbar>
  );
};

export default Navbar;