// frontend/src/services/labService.js
import api from './api';

// Fonction utilitaire pour extraire les tableaux
const extractArray = (data) => {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (data.content && Array.isArray(data.content)) return data.content;
  if (data.data && Array.isArray(data.data)) return data.data;
  if (data.results && Array.isArray(data.results)) return data.results;
  return [];
};

export const labService = {
  // VOIR LES TESTS EN ATTENTE (pour le laboratoire)
  getPendingTests: async () => {
    try {
      console.log('🔍 Récupération des tests en attente...');
      // Essayer d'abord /lab/pending (si disponible)
      try {
        const response = await api.get('/lab/pending');
        return extractArray(response.data);
      } catch (e) {
        // Sinon utiliser /lab/results?status=PENDING
        const response = await api.get('/lab/results?status=PENDING');
        return extractArray(response.data);
      }
    } catch (error) {
      console.error('❌ Erreur getPendingTests:', error);
      return [];
    }
  },
  
  // VOIR LES TESTS EN ATTENTE POUR UN MÉDECIN SPÉCIFIQUE
  getPendingForDoctor: async (doctorId) => {
    try {
      console.log(`🔍 Récupération des tests en attente pour médecin ${doctorId}...`);
      const response = await api.get(`/lab/doctor/${doctorId}/pending`);
      return extractArray(response.data);
    } catch (error) {
      console.error('❌ Erreur getPendingForDoctor:', error);
      return [];
    }
  },
  
  // TESTS EN COURS
  getInProgressTests: async () => {
    try {
      console.log('🔍 Récupération des tests en cours...');
      const response = await api.get('/lab/results?status=IN_PROGRESS');
      return extractArray(response.data);
    } catch (error) {
      console.error('❌ Erreur getInProgressTests:', error);
      return [];
    }
  },
  
  // TESTS COMPLÉTÉS
  getCompletedTests: async () => {
    try {
      console.log('🔍 Récupération des tests complétés...');
      const response = await api.get('/lab/results?status=COMPLETED');
      return extractArray(response.data);
    } catch (error) {
      console.error('❌ Erreur getCompletedTests:', error);
      return [];
    }
  },
  
  // TESTS PAR STATUT (générique)
  getTestsByStatus: async (status) => {
    try {
      const response = await api.get(`/lab/results?status=${status}`);
      return extractArray(response.data);
    } catch (error) {
      console.error(`❌ Erreur getTestsByStatus ${status}:`, error);
      return [];
    }
  },
  
  // COMPLÉTER UN TEST
  completeTest: async (id, results) => {
    try {
      console.log(`🔍 Complétion du test ${id}...`, results);
      const response = await api.post(`/lab/results/${id}/complete`, results);
      return response.data;
    } catch (error) {
      console.error('❌ Erreur completeTest:', error);
      throw error;
    }
  },
  
  // RÉSULTAT PAR ID
  getTestById: async (id) => {
    try {
      const response = await api.get(`/lab/results/${id}`);
      return response.data;
    } catch (error) {
      console.error(`❌ Erreur getTestById ${id}:`, error);
      throw error;
    }
  },
  
  // UPLOAD DE FICHIER
  uploadFile: async (id, file) => {
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const response = await api.post(`/lab/results/${id}/upload`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        onUploadProgress: (progressEvent) => {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          console.log(`Upload: ${percentCompleted}%`);
        },
      });
      return response.data;
    } catch (error) {
      console.error('❌ Erreur uploadFile:', error);
      throw error;
    }
  },
  
  // RÉVISER UN RÉSULTAT
  reviewResult: async (id, data) => {
    try {
      console.log(`🔍 Révision du résultat ${id}...`, data);
      const response = await api.patch(`/lab/results/${id}/review`, data);
      return response.data;
    } catch (error) {
      console.error('❌ Erreur reviewResult:', error);
      throw error;
    }
  },
  
  // STATISTIQUES
  getStats: async () => {
    try {
      console.log('🔍 Récupération des statistiques...');
      
      // Compter directement depuis les résultats
      const [pending, inProgress, completed] = await Promise.all([
        labService.getPendingTests(),
        labService.getInProgressTests(),
        labService.getCompletedTests()
      ]);
      
      return {
        pending: pending.length,
        inProgress: inProgress.length,
        completed: completed.length,
        total: pending.length + inProgress.length + completed.length
      };
    } catch (error) {
      console.error('❌ Erreur getStats:', error);
      return { pending: 0, inProgress: 0, completed: 0, total: 0 };
    }
  },
  
  // UPLOADER UN RÉSULTAT (alias pour uploadFile)
  uploadResult: async (id, file) => {
    return labService.uploadFile(id, file);
  },
};

export default labService;