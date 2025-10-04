import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Button, Card } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import axios from 'axios';

const Home = () => {
  const [featuredProducts, setFeaturedProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchFeaturedProducts = async () => {
      try {
        const response = await axios.get('/api/online/products');
        if (response.data.success) {
          // Get first 6 products as featured
          setFeaturedProducts(response.data.data.slice(0, 6));
        }
      } catch (error) {
        console.error('Error fetching featured products:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchFeaturedProducts();
  }, []);

  return (
    <div>
      {/* Hero Section */}
      <section className="hero-section">
        <Container>
          <Row>
            <Col>
              <h1 className="display-4 mb-4">Welcome to DVP Gift Center</h1>
              <p className="lead mb-4">
                Discover unique gifts for every occasion. From electronics to fashion, 
                we have everything you need to make your loved ones smile.
              </p>
              <Link to="/products">
                <Button variant="light" size="lg">
                  Shop Now
                </Button>
              </Link>
            </Col>
          </Row>
        </Container>
      </section>

      {/* Featured Products */}
      <Container className="my-5">
        <Row>
          <Col>
            <h2 className="text-center mb-5">Featured Products</h2>
          </Col>
        </Row>
        
        {loading ? (
          <Row>
            <Col className="text-center">
              <div className="spinner-border" role="status">
                <span className="visually-hidden">Loading...</span>
              </div>
            </Col>
          </Row>
        ) : (
          <Row>
            {featuredProducts.map(product => (
              <Col md={4} className="mb-4" key={product.productId}>
                <Card className="product-card h-100">
                  <Card.Img 
                    variant="top" 
                    src={product.imageUrl || '/api/placeholder/300/200'} 
                    className="product-image"
                    alt={product.productName}
                  />
                  <Card.Body className="d-flex flex-column">
                    <Card.Title>{product.productName}</Card.Title>
                    <Card.Text className="flex-grow-1">
                      {product.onlineDescription || product.description}
                    </Card.Text>
                    <div className="mt-auto">
                      <div className="price-tag mb-2">
                        ${product.onlinePrice}
                      </div>
                      <Link to={`/products/${product.productId}`}>
                        <Button variant="primary" className="w-100">
                          View Details
                        </Button>
                      </Link>
                    </div>
                  </Card.Body>
                </Card>
              </Col>
            ))}
          </Row>
        )}
      </Container>

      {/* Features Section */}
      <Container className="my-5">
        <Row>
          <Col>
            <h2 className="text-center mb-5">Why Choose DVP Gift Center?</h2>
          </Col>
        </Row>
        <Row>
          <Col md={4} className="text-center mb-4">
            <div className="mb-3">
              <i className="fas fa-shipping-fast fa-3x text-primary"></i>
            </div>
            <h4>Fast Shipping</h4>
            <p>Quick and reliable delivery to your doorstep.</p>
          </Col>
          <Col md={4} className="text-center mb-4">
            <div className="mb-3">
              <i className="fas fa-shield-alt fa-3x text-success"></i>
            </div>
            <h4>Secure Shopping</h4>
            <p>Your data and transactions are always protected.</p>
          </Col>
          <Col md={4} className="text-center mb-4">
            <div className="mb-3">
              <i className="fas fa-headset fa-3x text-info"></i>
            </div>
            <h4>24/7 Support</h4>
            <p>Our customer service team is here to help anytime.</p>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default Home;