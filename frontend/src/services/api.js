// frontend/src/services/api.js
import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Intercepteur pour ajouter le token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    console.log(`🔍 [API] Requête ${config.method.toUpperCase()} ${config.url}`);
    console.log(`   Token présent: ${token ? 'OUI' : 'NON'}`);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log(`   Token ajouté: ${token.substring(0, 15)}...`);
    }
    return config;
  },
  (error) => {
    console.error('🔍 [API] Erreur requête:', error);
    return Promise.reject(error);
  }
);


// frontend/src/services/api.js - Assurez-vous qu'il n'y a PAS de suppression automatique
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.log('🔍 [API] Erreur', error.config?.url, error.response?.status);
    
    // NE PAS SUPPRIMER LE TOKEN POUR LES TESTS
    // if (error.response?.status === 401) {
    //   localStorage.removeItem('token');
    // }
    
    return Promise.reject(error);
  }
);
export default api;