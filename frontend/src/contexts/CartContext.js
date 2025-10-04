import React, { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';

const CartContext = createContext();

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};

export const CartProvider = ({ children }) => {
  const [cartItems, setCartItems] = useState([]);

  // Load cart from backend on mount (if authenticated)
  useEffect(() => {
    const loadCart = async () => {
      try {
        const res = await axios.get('/api/online/cart');
        if (res?.data?.success) {
          setCartItems(res.data.data || []);
        }
      } catch (e) {
        // ignore if unauthenticated or 403; frontend local cart will be empty
      }
    };
    loadCart();
  }, []);

  const addToCart = async (product, quantity = 1) => {
    try {
      await axios.post('/api/online/cart/add', {
        productId: product.productId,
        quantity
      });
      // refresh from server
      const res = await axios.get('/api/online/cart');
      if (res?.data?.success) setCartItems(res.data.data || []);
    } catch (e) {
      // Fallback to local cart if server call fails
      setCartItems(prevItems => {
        const existingItem = prevItems.find(item => item.productId === product.productId);
        if (existingItem) {
          return prevItems.map(item =>
            item.productId === product.productId
              ? { ...item, quantity: (item.quantity || 0) + quantity }
              : item
          );
        } else {
          return [...prevItems, { ...product, quantity }];
        }
      });
      throw e;
    }
  };

  const removeFromCart = async (productId) => {
    try {
      await axios.delete(`/api/online/cart/remove/${productId}`);
      const res = await axios.get('/api/online/cart');
      if (res?.data?.success) setCartItems(res.data.data || []);
    } catch (e) {
      setCartItems(prevItems => prevItems.filter(item => item.productId !== productId));
    }
  };

  const updateQuantity = async (productId, quantity) => {
    try {
      if (quantity <= 0) {
        await removeFromCart(productId);
        return;
      }
      await axios.put('/api/online/cart/update', { productId, quantity });
      const res = await axios.get('/api/online/cart');
      if (res?.data?.success) setCartItems(res.data.data || []);
    } catch (e) {
      if (quantity <= 0) {
        setCartItems(prevItems => prevItems.filter(item => item.productId !== productId));
      } else {
        setCartItems(prevItems =>
          prevItems.map(item =>
            item.productId === productId
              ? { ...item, quantity }
              : item
          )
        );
      }
    }
  };

  const clearCart = async () => {
    try {
      await axios.delete('/api/online/cart/clear');
      setCartItems([]);
    } catch (e) {
      setCartItems([]);
    }
  };

  const getCartTotal = () => {
    return cartItems.reduce((total, item) => 
      total + ((item.onlinePrice || 0) * (item.quantity || 0)), 0
    );
  };

  const getCartItemCount = () => {
    return cartItems.reduce((total, item) => total + (item.quantity || 0), 0);
  };

  const value = {
    cartItems,
    addToCart,
    removeFromCart,
    updateQuantity,
    clearCart,
    getCartTotal,
    getCartItemCount
  };

  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  );
};