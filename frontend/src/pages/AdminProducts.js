import React, { useState, useEffect, useCallback } from 'react';
import { 
  Row, Col, Card, Table, Button, Modal, Form, 
  Alert, Badge, Spinner, InputGroup 
} from 'react-bootstrap';
import { useAuth } from '../contexts/AuthContext';
import AdminLayout from '../components/AdminLayout';
import axios from 'axios';
import { formatLKR } from '../utils/currency';

const AdminProducts = () => {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  // Online availability filter: '', 'online', 'offline'
  // '' means show all; used to filter table rows by product.isAvailableOnline
  const [onlineFilter, setOnlineFilter] = useState('');
  const [showArchived, setShowArchived] = useState(false);

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    category: '',
    stockQuantity: '',
    imageUrl: '',
    productCode: '',
    barcode: '',
    costPrice: '',
    minStockLevel: '',
    maxStockLevel: '',
    isAvailableOnline: false,
    onlinePrice: '',
    onlineDescription: '',
    promotionalDetails: ''
  });

  const { getAuthToken, user, isAdmin } = useAuth();

  const fetchProducts = useCallback(async () => {
    try {
      setLoading(true);
      const token = getAuthToken();
      console.log('Current token:', token);
      console.log('Current user:', user);
      console.log('Is admin:', isAdmin);
      
      if (!token) {
        setError('No authentication token found. Please log in.');
        setLoading(false);
        return;
      }
      // If onlineFilter is set to 'online' or 'offline', pass it to server for filtering
      const params = {};
      if (onlineFilter) {
        params.availability = onlineFilter; // server expects 'online' | 'offline'
      }
      params.includeArchived = showArchived; // Include archived products if toggle is on

      const response = await axios.get('/api/admin/products', {
        headers: {
          'Authorization': `Bearer ${token}`
        },
        params
      });
      
      // Extract the data array from the ApiResponse wrapper
      if (response.data.success && response.data.data) {
        setProducts(response.data.data);
      } else {
        setProducts([]);
        setError(response.data.message || 'Failed to load products');
      }
    } catch (error) {
      setError('Failed to load products');
      console.error('Error fetching products:', error);
      console.log('Response status:', error.response?.status);
      console.log('Response data:', error.response?.data);
    } finally {
      setLoading(false);
    }
  // dependency array updated to include showArchived
  }, [getAuthToken, user, isAdmin, onlineFilter, showArchived]);

  const fetchCategories = useCallback(async () => {
    try {
      const token = getAuthToken();
      
      if (!token) {
        return;
      }
      
      const response = await axios.get('/api/admin/categories/active', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      
      // Extract the data array from the ApiResponse wrapper
      if (response.data.success && response.data.data) {
        setCategories(response.data.data);
      } else {
        setCategories([]);
      }
    } catch (error) {
      console.error('Error fetching categories:', error);
      // Don't set error here as it's not critical for product page
    }
  }, [getAuthToken]);

  useEffect(() => {
    fetchProducts();
    fetchCategories();
  }, [fetchProducts, fetchCategories]);

  const handleShowModal = (product = null) => {
    if (product) {
      setEditingProduct(product);
      setFormData({
        name: product.name,
        description: product.description || '',
        price: product.price.toString(),
        category: product.category,
        stockQuantity: product.stockQuantity.toString(),
        imageUrl: product.imageUrl || '',
        productCode: product.productCode || '',
        barcode: product.barcode || '',
        costPrice: product.costPrice ? product.costPrice.toString() : '',
        minStockLevel: product.minStockLevel ? product.minStockLevel.toString() : '',
        maxStockLevel: product.maxStockLevel ? product.maxStockLevel.toString() : '',
        isAvailableOnline: product.isAvailableOnline || false,
        onlinePrice: product.onlinePrice ? product.onlinePrice.toString() : '',
        onlineDescription: product.onlineDescription || '',
        promotionalDetails: product.promotionalDetails || ''
      });
    } else {
      setEditingProduct(null);
      setFormData({
        name: '',
        description: '',
        price: '',
        category: '',
        stockQuantity: '',
        imageUrl: '',
        productCode: '',
        barcode: '',
        costPrice: '',
        minStockLevel: '',
        maxStockLevel: '',
        isAvailableOnline: false,
        onlinePrice: '',
        onlineDescription: '',
        promotionalDetails: ''
      });
    }
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingProduct(null);
    setError('');
  };

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const token = getAuthToken();
      // Build payload with proper types; avoid sending empty strings for numeric fields
      const toNumberOrNull = (v, parser) => (v === '' || v === undefined || v === null ? null : parser(v));
      const productData = {
        name: formData.name,
        description: formData.description || null,
        price: parseFloat(formData.price), // required
        category: formData.category || null, // name; resolved server-side
        stockQuantity: parseInt(formData.stockQuantity, 10), // required
        imageUrl: formData.imageUrl || null,
        productCode: formData.productCode || null,
        barcode: formData.barcode || null,
        costPrice: toNumberOrNull(formData.costPrice, parseFloat),
        minStockLevel: toNumberOrNull(formData.minStockLevel, (x) => parseInt(x, 10)),
        maxStockLevel: toNumberOrNull(formData.maxStockLevel, (x) => parseInt(x, 10)),
        isAvailableOnline: !!formData.isAvailableOnline,
        onlinePrice: formData.isAvailableOnline ? toNumberOrNull(formData.onlinePrice, parseFloat) : null,
        onlineDescription: formData.onlineDescription || null,
        promotionalDetails: formData.promotionalDetails || null,
      };

      // Basic client-side validation for required numeric fields
      if (Number.isNaN(productData.price)) {
        setError('Price is required and must be a valid number.');
        return;
      }
      if (Number.isNaN(productData.stockQuantity)) {
        setError('Stock quantity is required and must be a valid integer.');
        return;
      }

      if (editingProduct) {
        await axios.put(
          `/api/admin/products/${editingProduct.id}`,
          productData,
          {
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          }
        );
      } else {
        await axios.post(
          '/api/admin/products',
          productData,
          {
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          }
        );
      }

      handleCloseModal();
      fetchProducts();
    } catch (error) {
      const serverMessage = error.response?.data?.message || error.response?.data?.error;
      setError(serverMessage ? `Failed to save product: ${serverMessage}` : 'Failed to save product');
      console.error('Error saving product:', error);
    }
  };

  const handleDelete = async (productId) => {
    if (window.confirm('Are you sure you want to delete this product?')) {
      try {
        const token = getAuthToken();
        await axios.delete(`/api/admin/products/${productId}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        fetchProducts();
      } catch (error) {
        setError('Failed to delete product');
        console.error('Error deleting product:', error);
      }
    }
  };

  const handleRestore = async (productId) => {
    try {
      const token = getAuthToken();
      await axios.put(`/api/admin/products/${productId}/restore`, {}, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      fetchProducts();
    } catch (error) {
      setError('Failed to restore product');
      console.error('Error restoring product:', error);
    }
  };

  const formatPrice = (price) => formatLKR(price);

  // Apply client-side filtering for search and category only (online availability is filtered server-side)
  const filteredProducts = Array.isArray(products) ? products.filter(product => {
    const matchesSearch = product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         product.description?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = !categoryFilter || product.category === categoryFilter;
    return matchesSearch && matchesCategory;
  }) : [];

  if (loading) {
    return (
      <AdminLayout>
        <div className="py-5 text-center">
          <Spinner animation="border" />
          <p className="mt-3">Loading products...</p>
        </div>
      </AdminLayout>
    );
  }

  return (
    <AdminLayout>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Product Management</h2>
        <Button variant="primary" onClick={() => handleShowModal()}>
          Add New Product
        </Button>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}

      <Row className="mb-4">
        <Col md={6}>
          <InputGroup>
            <Form.Control
              type="text"
              placeholder="Search products..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </InputGroup>
        </Col>
        <Col md={3}>
          <Form.Select
            value={categoryFilter}
            onChange={(e) => setCategoryFilter(e.target.value)}
          >
            <option value="">All Categories</option>
            {categories.map(category => (
              <option key={category.categoryId} value={category.categoryName}>
                {category.categoryName}
              </option>
            ))}
          </Form.Select>
        </Col>
        <Col md={3}>
          {/* Online availability filter dropdown (now server-side for large datasets) */}
          <Form.Select
            value={onlineFilter}
            onChange={(e) => setOnlineFilter(e.target.value)}
          >
            <option value="">All Availability</option>
            <option value="online">Online Only</option>
            <option value="offline">Offline Only</option>
          </Form.Select>
        </Col>
        <Col md={3} className="d-flex align-items-center mt-3 mt-md-0">
          <Form.Check
            type="checkbox"
            id="showArchived"
            label="Show archived"
            checked={showArchived}
            onChange={(e) => setShowArchived(e.target.checked)}
          />
        </Col>
      </Row>

      <Card>
        <Card.Body>
          <Table responsive hover>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Category</th>
                <th>Price</th>
                <th>Stock</th>
                <th>Status</th>
                {/* New column to display whether product is available online */}
                <th>Online</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredProducts.map((product) => (
                <tr key={product.id}>
                  <td>{product.id}</td>
                  <td>
                    <div className="d-flex align-items-center">
                      <img 
                        src={product.imageUrl || '/placeholder-product.jpg'}
                        alt={product.name}
                        style={{ width: '40px', height: '40px', objectFit: 'cover' }}
                        className="rounded me-2"
                      />
                      <div>
                        <div>{product.name}</div>
                        <small className="text-muted">
                          {product.description?.substring(0, 50)}...
                        </small>
                      </div>
                    </div>
                  </td>
                  <td>
                    <Badge bg="secondary">{product.category}</Badge>
                  </td>
                  <td>{formatPrice(product.price)}</td>
                  <td>
                    <Badge 
                      bg={product.stockQuantity > 10 ? 'success' : 
                          product.stockQuantity > 0 ? 'warning' : 'danger'}
                    >
                      {product.stockQuantity}
                    </Badge>
                  </td>
                  <td>
                    <Badge bg={product.isActive ? (product.stockQuantity > 0 ? 'success' : 'warning') : 'secondary'}>
                      {product.isActive ? (product.stockQuantity > 0 ? 'Active' : 'Active (No Stock)') : 'Archived'}
                    </Badge>
                  </td>
                  {/* Online availability badge with optional online price hint */}
                  <td>
                    {product.isAvailableOnline ? (
                      <div className="d-flex align-items-center gap-2">
                        <Badge bg="success">Online</Badge>
                        {(product.onlinePrice !== null && product.onlinePrice !== undefined) && (
                          <small className="text-muted">{formatPrice(product.onlinePrice)}</small>
                        )}
                      </div>
                    ) : (
                      <Badge bg="secondary">Offline</Badge>
                    )}
                  </td>
                  <td>
                    <Button
                      variant="outline-primary"
                      size="sm"
                      className="me-2"
                      onClick={() => handleShowModal(product)}
                    >
                      Edit
                    </Button>
                    {product.isActive ? (
                      <Button
                        variant="outline-danger"
                        size="sm"
                        onClick={() => handleDelete(product.id)}
                      >
                        Delete
                      </Button>
                    ) : (
                      <Button
                        variant="outline-success"
                        size="sm"
                        onClick={() => handleRestore(product.id)}
                      >
                        Restore
                      </Button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>

          {filteredProducts.length === 0 && (
            <div className="text-center py-4">
              <p className="text-muted">No products found</p>
            </div>
          )}
        </Card.Body>
      </Card>

      <Modal show={showModal} onHide={handleCloseModal} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>
            {editingProduct ? 'Edit Product' : 'Add New Product'}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}
          
          <Form onSubmit={handleSubmit} id="product-form">
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Product Name *</Form.Label>
                  <Form.Control
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleInputChange}
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Category *</Form.Label>
                  <Form.Select
                    name="category"
                    value={formData.category}
                    onChange={handleInputChange}
                    required
                  >
                    <option value="">Select Category</option>
                    {categories.map(category => (
                      <option key={category.categoryId} value={category.categoryName}>
                        {category.categoryName}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
            </Row>

            <Form.Group className="mb-3">
              <Form.Label>Description</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                name="description"
                value={formData.description}
                onChange={handleInputChange}
              />
            </Form.Group>

            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Price *</Form.Label>
                  <InputGroup>
                    <InputGroup.Text>Rs</InputGroup.Text>
                    <Form.Control
                      type="number"
                      step="0.01"
                      name="price"
                      value={formData.price}
                      onChange={handleInputChange}
                      required
                    />
                  </InputGroup>
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Cost Price</Form.Label>
                  <InputGroup>
                    <InputGroup.Text>Rs</InputGroup.Text>
                    <Form.Control
                      type="number"
                      step="0.01"
                      name="costPrice"
                      value={formData.costPrice}
                      onChange={handleInputChange}
                      placeholder="Purchase/wholesale price"
                    />
                  </InputGroup>
                </Form.Group>
              </Col>
            </Row>

            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Product Code</Form.Label>
                  <Form.Control
                    type="text"
                    name="productCode"
                    value={formData.productCode}
                    onChange={handleInputChange}
                    placeholder="SKU or product code"
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Barcode</Form.Label>
                  <Form.Control
                    type="text"
                    name="barcode"
                    value={formData.barcode}
                    onChange={handleInputChange}
                    placeholder="UPC/EAN barcode"
                  />
                </Form.Group>
              </Col>
            </Row>

            <Row>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Stock Quantity *</Form.Label>
                  <Form.Control
                    type="number"
                    name="stockQuantity"
                    value={formData.stockQuantity}
                    onChange={handleInputChange}
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Min Stock Level</Form.Label>
                  <Form.Control
                    type="number"
                    name="minStockLevel"
                    value={formData.minStockLevel}
                    onChange={handleInputChange}
                    placeholder="Reorder point"
                  />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Max Stock Level</Form.Label>
                  <Form.Control
                    type="number"
                    name="maxStockLevel"
                    value={formData.maxStockLevel}
                    onChange={handleInputChange}
                    placeholder="Maximum stock"
                  />
                </Form.Group>
              </Col>
            </Row>

            <hr />
            <h5>Online Availability</h5>
            
            <Form.Group className="mb-3">
              <Form.Check
                type="checkbox"
                id="isAvailableOnline"
                name="isAvailableOnline"
                label="Available for online purchase"
                checked={formData.isAvailableOnline}
                onChange={(e) => setFormData({...formData, isAvailableOnline: e.target.checked})}
              />
            </Form.Group>

            {formData.isAvailableOnline && (
              <>
                <Form.Group className="mb-3">
                  <Form.Label>Online Price</Form.Label>
                  <InputGroup>
                    <InputGroup.Text>Rs</InputGroup.Text>
                    <Form.Control
                      type="number"
                      step="0.01"
                      name="onlinePrice"
                      value={formData.onlinePrice}
                      onChange={handleInputChange}
                      placeholder="Leave empty to use regular price"
                    />
                  </InputGroup>
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Online Description</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={3}
                    name="onlineDescription"
                    value={formData.onlineDescription}
                    onChange={handleInputChange}
                    placeholder="Special description for online store"
                  />
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Promotional Details</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={2}
                    name="promotionalDetails"
                    value={formData.promotionalDetails}
                    onChange={handleInputChange}
                    placeholder="Special offers, bundles, or promotional information"
                  />
                </Form.Group>
              </>
            )}

            <Form.Group className="mb-3">
              <Form.Label>Image URL</Form.Label>
              <Form.Control
                type="url"
                name="imageUrl"
                value={formData.imageUrl}
                onChange={handleInputChange}
                placeholder="https://example.com/image.jpg"
              />
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCloseModal}>
            Cancel
          </Button>
          <Button variant="primary" type="submit" form="product-form">
            {editingProduct ? 'Update Product' : 'Add Product'}
          </Button>
        </Modal.Footer>
      </Modal>
    </AdminLayout>
  );
};

export default AdminProducts;