// frontend/src/contexts/AuthContext.js
import React, { createContext, useState, useContext, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import api from '../services/api';

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
  const [loading, setLoading] = useState(true);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const navigate = useNavigate();

  const isAuthenticated = !!user;

  useEffect(() => {
    const loadUser = async () => {
      if (token) {
        try {
          const response = await api.get('/auth/me');
          setUser(response.data);
        } catch (error) {
          localStorage.removeItem('token');
          setToken(null);
        }
      }
      setLoading(false);
    };
    loadUser();
  }, [token]);

  const login = async (email, password) => {
    try {
      const response = await api.post('/auth/login', { email, password });
      const { token: newToken, ...userData } = response.data;
      
      localStorage.setItem('token', newToken);
      setToken(newToken);
      setUser(userData);
      
      toast.success('Connexion réussie !');
      
      // Redirection selon le rôle
      const role = userData.role;
      let redirectPath = '/dashboard';
      switch (role) {
        case 'PATIENT': redirectPath = '/patient/dashboard'; break;
        case 'DOCTOR': redirectPath = '/doctor/dashboard'; break;
        case 'PHARMACIST': redirectPath = '/pharmacy/dashboard'; break;
        case 'LAB_TECHNICIAN': redirectPath = '/lab/dashboard'; break;
        default: redirectPath = '/dashboard';
      }
      
      navigate(redirectPath);
      return { success: true };
    } catch (error) {
      toast.error(error.response?.data?.message || 'Erreur de connexion');
      return { success: false };
    }
  };

  // ✅ AJOUTER LA FONCTION REGISTER
  const register = async (userData) => {
    try {
      const response = await api.post('/auth/register', userData);
      toast.success('Inscription réussie ! Veuillez vous connecter.');
      navigate('/login');
      return { success: true, data: response.data };
    } catch (error) {
      toast.error(error.response?.data?.message || "Erreur lors de l'inscription");
      return { success: false, error: error.response?.data };
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setUser(null);
    toast.success('Déconnexion réussie');
    navigate('/login');
  };

  const value = {
    user,
    loading,
    login,
    register,  // ← BIEN INCLURE register DANS LA VALEUR RETOURNÉE
    logout,
    token,
    isAuthenticated,
    role: user?.role,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};