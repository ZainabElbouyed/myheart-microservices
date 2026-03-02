// frontend/src/services/doctorService.js
import api from './api';

// Fonction utilitaire pour extraire les tableaux
const extractArray = (data) => {
  if (!data) return [];
  
  // Si c'est déjà un tableau
  if (Array.isArray(data)) return data;
  
  // Si c'est un objet avec une propriété 'content' (pagination Spring)
  if (data.content && Array.isArray(data.content)) return data.content;
  
  // Si c'est un objet avec une propriété 'data'
  if (data.data && Array.isArray(data.data)) return data.data;
  
  // Si c'est un objet avec une propriété '_embedded'
  if (data._embedded) {
    const keys = Object.keys(data._embedded);
    if (keys.length > 0 && Array.isArray(data._embedded[keys[0]])) {
      return data._embedded[keys[0]];
    }
  }
  
  console.log('⚠️ Format de données inattendu:', data);
  return [];
};

export const doctorService = {
  // VOIR SES PATIENTS (via doctor-service → patient-service)
  getPatients: async (doctorId) => {
    try {
      console.log(`🔍 Appel API /doctors/${doctorId}/patients...`);
      const response = await api.get(`/doctors/${doctorId}/patients`);
      console.log('📦 Réponse patients:', response.data);
      return extractArray(response.data);
    } catch (error) {
      console.error('❌ Erreur getPatients:', error);
      if (error.response?.status === 404) {
        console.log('⚠️ Route non trouvée, tentative de fallback...');
        // Fallback: récupérer depuis les rendez-vous
        return await doctorService.getPatientsFromAppointments(doctorId);
      }
      return [];
    }
  },

  // Fallback: récupérer les patients depuis les rendez-vous
  getPatientsFromAppointments: async (doctorId) => {
    try {
      const appointments = await doctorService.getAppointments(doctorId);
      const patientsMap = new Map();
      
      appointments.forEach(apt => {
        if (apt.patientId && !patientsMap.has(apt.patientId)) {
          patientsMap.set(apt.patientId, {
            id: apt.patientId,
            firstName: apt.patientName?.split(' ')[0] || 'Patient',
            lastName: apt.patientName?.split(' ')[1] || '',
            email: apt.patientEmail || '',
            lastVisit: apt.startTime
          });
        }
      });
      
      return Array.from(patientsMap.values());
    } catch (error) {
      return [];
    }
  },
  
  // VOIR LE DOSSIER COMPLET D'UN PATIENT
  getFullPatientRecord: async (patientId) => {
    try {
      console.log(`🔍 Récupération dossier complet patient ${patientId}...`);
      
      // Récupérer les informations patient
      const patientResponse = await api.get(`/patients/${patientId}`);
      const patient = patientResponse.data;
      
      // Récupérer les résultats labo
      let labResults = [];
      try {
        const labResponse = await api.get(`/lab/patient/${patientId}`);
        labResults = extractArray(labResponse.data);
      } catch (error) {
        console.log('⚠️ Pas de résultats labo pour ce patient');
      }
      
      // Récupérer les prescriptions
      let prescriptions = [];
      try {
        const prescResponse = await api.get(`/prescriptions/patient/${patientId}`);
        prescriptions = extractArray(prescResponse.data);
      } catch (error) {
        console.log('⚠️ Pas de prescriptions pour ce patient');
      }
      
      return {
        patient,
        labResults,
        prescriptions
      };
    } catch (error) {
      console.error('❌ Erreur getFullPatientRecord:', error);
      throw error;
    }
  },
  
  // Rendez-vous
  getAppointments: async (doctorId) => {
    try {
      const response = await api.get(`/appointments/doctor/${doctorId}`);
      return extractArray(response.data);
    } catch (error) {
      console.error('❌ Erreur getAppointments:', error);
      return [];
    }
  },
  
  getTodayAppointments: async (doctorId) => {
    try {
      console.log(`🔍 Appel API /appointments/doctor/${doctorId}/today...`);
      const response = await api.get(`/appointments/doctor/${doctorId}/today`);
      return extractArray(response.data);
    } catch (error) {
      console.error('❌ Erreur getTodayAppointments:', error);
      
      // Fallback: filtrer les rendez-vous du jour
      try {
        const allAppointments = await doctorService.getAppointments(doctorId);
        const today = new Date().toDateString();
        return allAppointments.filter(apt => 
          new Date(apt.startTime).toDateString() === today
        );
      } catch (fallbackError) {
        return [];
      }
    }
  },
  
  // VOIR LES RÉSULTATS LABO EN ATTENTE
  getPendingLabResults: async (doctorId) => {
    try {
      console.log(`🔍 Appel API /lab/doctor/${doctorId}/pending...`);
      const response = await api.get(`/lab/doctor/${doctorId}/pending`);
      return extractArray(response.data);
    } catch (error) {
      console.error('❌ Erreur getPendingLabResults:', error);
      
      // Fallback: récupérer tous les résultats et filtrer
      try {
        const response = await api.get(`/lab/doctor/${doctorId}`);
        const allResults = extractArray(response.data);
        return allResults.filter(r => 
          r.status === 'PENDING' || r.status === 'IN_PROGRESS'
        );
      } catch (fallbackError) {
        return [];
      }
    }
  },
  
  // Prescriptions
  createPrescription: async (data) => {
    try {
      const response = await api.post('/prescriptions', data);
      return response.data;
    } catch (error) {
      console.error('❌ Erreur createPrescription:', error);
      throw error;
    }
  },
  
  getPrescriptions: async (doctorId) => {
    try {
      const response = await api.get(`/prescriptions/doctor/${doctorId}`);
      return extractArray(response.data);
    } catch (error) {
      console.error('❌ Erreur getPrescriptions:', error);
      return [];
    }
  },
  
  // Réviser un résultat labo
  reviewLabResult: async (id, data) => {
    try {
      const response = await api.patch(`/lab/results/${id}/review`, data);
      return response.data;
    } catch (error) {
      console.error('❌ Erreur reviewLabResult:', error);
      throw error;
    }
  },
};

export default doctorService;