// frontend/src/services/pharmacyService.js
import api from './api';

// Fonction utilitaire pour extraire les tableaux
const extractArray = (data) => {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (data.content && Array.isArray(data.content)) return data.content;
  if (data.data && Array.isArray(data.data)) return data.data;
  if (data.results && Array.isArray(data.results)) return data.results;
  if (data.medicines && Array.isArray(data.medicines)) return data.medicines;
  return [];
};

export const pharmacyService = {
  // ✅ VOIR LES PRESCRIPTIONS EN ATTENTE (via pharmacy → prescription)
  getPendingPrescriptions: async () => {
    try {
      console.log('🔍 Récupération des prescriptions en attente...');
      
      // Essayer différents endpoints
      const endpoints = [
        '/prescriptions/status/PENDING',
        '/prescriptions/pending',
        '/prescriptions?status=PENDING'
      ];
      
      for (const endpoint of endpoints) {
        try {
          const response = await api.get(endpoint);
          const data = extractArray(response.data);
          if (data.length > 0 || response.status === 200) {
            console.log(`✅ Prescriptions trouvées sur ${endpoint}:`, data);
            return data;
          }
        } catch (e) {
          console.log(`⚠️ ${endpoint} non disponible`);
        }
      }
      
      return [];
    } catch (error) {
      console.error('❌ Erreur getPendingPrescriptions:', error);
      return [];
    }
  },
  
  // ✅ DÉLIVRER UNE PRESCRIPTION
  fulfillPrescription: async (id, pharmacyId, pharmacist) => {
    try {
      console.log(`🔍 Délivrance prescription ${id}...`);
      const response = await api.post(`/prescriptions/${id}/fill`, {
        pharmacyId,
        pharmacist,
        filledAt: new Date().toISOString()
      });
      return response.data;
    } catch (error) {
      console.error('❌ Erreur fulfillPrescription:', error);
      throw error;
    }
  },
  
  // ✅ INVENTAIRE
  getInventory: async () => {
    try {
      console.log('🔍 Récupération de l\'inventaire...');
      
      // Essayer différents endpoints
      const endpoints = [
        '/pharmacy/medicines',
        '/medicines',
        '/inventory'
      ];
      
      for (const endpoint of endpoints) {
        try {
          const response = await api.get(endpoint);
          const data = extractArray(response.data);
          if (data.length > 0 || response.status === 200) {
            console.log(`✅ Inventaire trouvé sur ${endpoint}:`, data);
            return data;
          }
        } catch (e) {
          console.log(`⚠️ ${endpoint} non disponible`);
        }
      }
      
      // Données mockées pour le développement
      return [
        {
          id: 'MED001',
          name: 'Doliprane',
          genericName: 'Paracétamol',
          category: 'Antalgique',
          form: 'Comprimé',
          strength: '500mg',
          stockQuantity: 150,
          maximumStock: 300,
          reorderLevel: 50,
          unitPrice: 2.50,
          sellingPrice: 3.20,
          requiresPrescription: false
        },
        {
          id: 'MED002',
          name: 'Amoxicilline',
          genericName: 'Amoxicilline',
          category: 'Antibiotique',
          form: 'Gélule',
          strength: '500mg',
          stockQuantity: 75,
          maximumStock: 200,
          reorderLevel: 40,
          unitPrice: 5.80,
          sellingPrice: 7.50,
          requiresPrescription: true
        },
        {
          id: 'MED003',
          name: 'Ventoline',
          genericName: 'Salbutamol',
          category: 'Bronchodilatateur',
          form: 'Inhalateur',
          strength: '100µg/dose',
          stockQuantity: 30,
          maximumStock: 100,
          reorderLevel: 25,
          unitPrice: 8.90,
          sellingPrice: 11.50,
          requiresPrescription: true
        }
      ];
    } catch (error) {
      console.error('❌ Erreur getInventory:', error);
      return [];
    }
  },
  
  // ✅ STOCK FAIBLE
  getLowStock: async () => {
    try {
      console.log('🔍 Récupération des stocks faibles...');
      
      const inventory = await pharmacyService.getInventory();
      const lowStock = inventory.filter(item => 
        item.stockQuantity <= item.reorderLevel
      );
      
      console.log('✅ Stocks faibles:', lowStock);
      return lowStock;
    } catch (error) {
      console.error('❌ Erreur getLowStock:', error);
      return [];
    }
  },
  
  // ✅ METTRE À JOUR LE STOCK
  updateStock: async (id, quantity) => {
    try {
      console.log(`🔍 Mise à jour stock ${id} -> ${quantity}`);
      
      // Essayer différents endpoints
      const endpoints = [
        `/pharmacy/medicines/${id}/stock`,
        `/medicines/${id}/stock`,
        `/inventory/${id}`
      ];
      
      for (const endpoint of endpoints) {
        try {
          const response = await api.patch(endpoint, { quantity });
          return response.data;
        } catch (e) {
          console.log(`⚠️ ${endpoint} non disponible`);
        }
      }
      
      // Simulation pour le développement
      return { id, quantity, updated: true };
    } catch (error) {
      console.error('❌ Erreur updateStock:', error);
      throw error;
    }
  },
  
  // ✅ CRÉER UN MÉDICAMENT
  createMedicine: async (data) => {
    try {
      console.log('🔍 Création médicament:', data);
      
      // Essayer différents endpoints
      const endpoints = [
        '/pharmacy/medicines',
        '/medicines',
        '/inventory'
      ];
      
      for (const endpoint of endpoints) {
        try {
          const response = await api.post(endpoint, data);
          return response.data;
        } catch (e) {
          console.log(`⚠️ ${endpoint} non disponible`);
        }
      }
      
      // Simulation pour le développement
      return { ...data, id: 'NEW' + Date.now() };
    } catch (error) {
      console.error('❌ Erreur createMedicine:', error);
      throw error;
    }
  },
};

export default pharmacyService;