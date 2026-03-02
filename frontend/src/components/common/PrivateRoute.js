// frontend/src/components/common/PrivateRoute.js
import React, { useEffect } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import LoadingSpinner from './LoadingSpinner';

const PrivateRoute = ({ children, allowedRoles = [] }) => {
  const { isAuthenticated, loading, role, user } = useAuth();

  console.log('🔍 [PrivateRoute] Render - isAuthenticated:', isAuthenticated, 'role:', role, 'loading:', loading);
  console.log('   user:', user?.email);
  console.log('   allowedRoles:', allowedRoles);

  useEffect(() => {
    console.log('🔍 [PrivateRoute] useEffect - isAuthenticated:', isAuthenticated, 'user:', user?.email);
  }, [isAuthenticated, user]);

  if (loading) {
    console.log('🔍 [PrivateRoute] Chargement en cours...');
    return <LoadingSpinner />;
  }

  if (!isAuthenticated) {
    console.log('🔍 [PrivateRoute] Non authentifié, redirection vers login');
    return <Navigate to="/login" />;
  }

  if (allowedRoles.length > 0 && !allowedRoles.includes(role)) {
    console.log('🔍 [PrivateRoute] Rôle non autorisé:', role);
    let redirectPath = '/dashboard';
    switch (role) {
      case 'PATIENT': redirectPath = '/patient/dashboard'; break;
      case 'DOCTOR': redirectPath = '/doctor/dashboard'; break;
      case 'PHARMACIST': redirectPath = '/pharmacy/dashboard'; break;
      case 'LAB_TECHNICIAN': redirectPath = '/lab/dashboard'; break;
      default: redirectPath = '/dashboard';
    }
    console.log('   Redirection vers:', redirectPath);
    return <Navigate to={redirectPath} />;
  }

  console.log('🔍 [PrivateRoute] Authentification OK, affichage du composant enfant');
  return children;
};

export default PrivateRoute;