import React, { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';
import jwtDecode from 'jwt-decode';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      try {
        const decodedToken = jwtDecode(token);
        
        // Check if token is expired
        if (decodedToken.exp * 1000 < Date.now()) {
          logout();
        } else {
          setUser({
            username: decodedToken.sub,
            role: decodedToken.role
          });
          // Set default Authorization header
          axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        }
      } catch (error) {
        console.error('Invalid token:', error);
        logout();
      }
    }
    setLoading(false);
  }, [token]);

  const login = async (username, password) => {
    try {
      const response = await axios.post('/api/auth/login', {
        username,
        password
      });

      // Extract data from ApiResponse wrapper
      const { data: responseData } = response.data;
      const { token: newToken } = responseData;

      localStorage.setItem('token', newToken);
      setToken(newToken);
      
      const decodedToken = jwtDecode(newToken);
      setUser({
        username: decodedToken.sub,
        role: decodedToken.role,
        fullName: responseData.fullName,
        email: responseData.email
      });

      // Set default Authorization header
      axios.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;

      return { success: true, user: { username: decodedToken.sub, role: decodedToken.role, fullName: responseData.fullName, email: responseData.email } };
    } catch (error) {
      console.error('Login error:', error);
      return { 
        success: false, 
        message: error.response?.data?.message || 'Login failed' 
      };
    }
  };

  const register = async (userData) => {
    try {
      const response = await axios.post('/api/auth/register', userData);
      return { success: true, message: response.data.message };
    } catch (error) {
      console.error('Registration error:', error);
      return { 
        success: false, 
        message: error.response?.data?.message || 'Registration failed' 
      };
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setUser(null);
    delete axios.defaults.headers.common['Authorization'];
  };

  const getAuthToken = () => {
    return token;
  };

  const value = {
    user,
    token,
    login,
    register,
    logout,
    getAuthToken,
    loading,
    isAuthenticated: !!user,
    isAdmin: user?.role === 'admin',
    isCashier: user?.role === 'cashier'
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};