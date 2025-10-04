import React, { useState, useEffect, useCallback } from 'react';
import { 
  Row, Col, Card, Table, Button, Modal, Form, 
  Alert, Badge, Spinner, InputGroup 
} from 'react-bootstrap';
import { useAuth } from '../contexts/AuthContext';
import AdminLayout from '../components/AdminLayout';
import axios from 'axios';

const AdminCategories = () => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  const [formData, setFormData] = useState({
    categoryName: '',
    description: '',
    isActive: true
  });

  const { getAuthToken, user, isAdmin } = useAuth();

  const fetchCategories = useCallback(async () => {
    try {
      const token = getAuthToken();
      console.log('Current token:', token);
      console.log('Current user:', user);
      console.log('Is admin:', isAdmin);
      
      if (!token) {
        setError('No authentication token found. Please log in.');
        setLoading(false);
        return;
      }
      
      const response = await axios.get('/api/admin/categories', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      
      // Extract the data array from the ApiResponse wrapper
      if (response.data.success && response.data.data) {
        setCategories(response.data.data);
      } else {
        setCategories([]);
        setError(response.data.message || 'Failed to load categories');
      }
    } catch (error) {
      setError('Failed to load categories');
      console.error('Error fetching categories:', error);
      console.log('Response status:', error.response?.status);
      console.log('Response data:', error.response?.data);
    } finally {
      setLoading(false);
    }
  }, [getAuthToken, user, isAdmin]);

  useEffect(() => {
    fetchCategories();
  }, [fetchCategories]);

  const handleShowModal = (category = null) => {
    if (category) {
      setEditingCategory(category);
      setFormData({
        categoryName: category.categoryName,
        description: category.description || '',
        isActive: category.isActive
      });
    } else {
      setEditingCategory(null);
      setFormData({
        categoryName: '',
        description: '',
        isActive: true
      });
    }
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingCategory(null);
    setError('');
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      const token = getAuthToken();
      if (!token) {
        setError('Authentication token not found');
        return;
      }

      let response;
      if (editingCategory) {
        // Update existing category
        response = await axios.put(`/api/admin/categories/${editingCategory.categoryId}`, formData, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });
      } else {
        // Create new category
        response = await axios.post('/api/admin/categories', formData, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });
      }

      console.log('Category operation successful:', response.data);
      handleCloseModal();
      fetchCategories(); // Reload the categories list

    } catch (error) {
      console.error('Error saving category:', error);
      
      if (error.response?.data?.message) {
        setError(error.response.data.message);
      } else {
        setError(`Failed to ${editingCategory ? 'update' : 'create'} category`);
      }
    }
  };

  const handleDelete = async (categoryId) => {
    if (window.confirm('Are you sure you want to deactivate this category?')) {
      try {
        const token = getAuthToken();
        
        await axios.delete(`/api/admin/categories/${categoryId}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });

        console.log('Category deactivated successfully');
        fetchCategories(); // Reload the categories list

      } catch (error) {
        console.error('Error deactivating category:', error);
        setError('Failed to deactivate category');
      }
    }
  };

  // Filter categories based on search term
  const filteredCategories = Array.isArray(categories) ? categories.filter(category => {
    const matchesSearch = category.categoryName.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         (category.description && category.description.toLowerCase().includes(searchTerm.toLowerCase()));
    return matchesSearch;
  }) : [];

  if (loading) {
    return (
      <AdminLayout>
        <div className="py-5 text-center">
          <Spinner animation="border" />
          <p className="mt-3">Loading categories...</p>
        </div>
      </AdminLayout>
    );
  }

  return (
    <AdminLayout>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Category Management</h2>
        <Button variant="primary" onClick={() => handleShowModal()}>
          Add New Category
        </Button>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}

      <Row className="mb-4">
        <Col md={6}>
          <InputGroup>
            <Form.Control
              type="text"
              placeholder="Search categories..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </InputGroup>
        </Col>
      </Row>

      <Card>
        <Card.Body>
          <Table responsive hover>
            <thead>
              <tr>
                <th>ID</th>
                <th>Category Name</th>
                <th>Description</th>
                <th>Status</th>
                <th>Created At</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredCategories.map((category) => (
                <tr key={category.categoryId}>
                  <td>{category.categoryId}</td>
                  <td>{category.categoryName}</td>
                  <td>{category.description || 'No description'}</td>
                  <td>
                    <Badge bg={category.isActive ? 'success' : 'danger'}>
                      {category.isActive ? 'Active' : 'Inactive'}
                    </Badge>
                  </td>
                  <td>{new Date(category.createdAt).toLocaleDateString()}</td>
                  <td>
                    <Button 
                      variant="outline-primary" 
                      size="sm" 
                      className="me-2"
                      onClick={() => handleShowModal(category)}
                    >
                      Edit
                    </Button>
                    <Button 
                      variant="outline-danger" 
                      size="sm"
                      onClick={() => handleDelete(category.categoryId)}
                      disabled={!category.isActive}
                    >
                      Deactivate
                    </Button>
                  </td>
                </tr>
              ))}
              {filteredCategories.length === 0 && (
                <tr>
                  <td colSpan="6" className="text-center">
                    No categories found.
                  </td>
                </tr>
              )}
            </tbody>
          </Table>
        </Card.Body>
      </Card>

      {/* Category Form Modal */}
      <Modal show={showModal} onHide={handleCloseModal}>
        <Modal.Header closeButton>
          <Modal.Title>{editingCategory ? 'Edit Category' : 'Add New Category'}</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSubmit}>
          <Modal.Body>
            {error && <Alert variant="danger">{error}</Alert>}
            
            <Form.Group className="mb-3">
              <Form.Label>Category Name *</Form.Label>
              <Form.Control
                type="text"
                name="categoryName"
                value={formData.categoryName}
                onChange={handleInputChange}
                required
                maxLength={100}
                placeholder="Enter category name"
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Description</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                placeholder="Enter category description"
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Check
                type="checkbox"
                name="isActive"
                checked={formData.isActive}
                onChange={handleInputChange}
                label="Active"
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleCloseModal}>
              Cancel
            </Button>
            <Button variant="primary" type="submit">
              {editingCategory ? 'Update Category' : 'Create Category'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </AdminLayout>
  );
};

export default AdminCategories;