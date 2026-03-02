// frontend/src/services/patientService.js
import api from './api';

export const patientService = {
  // VOIR LES MÉDECINS - VERSION AVEC LOGS
  getDoctors: async () => {
    try {
      console.log('🔍 Appel API /doctors...');
      const response = await api.get('/doctors');
      
      console.log('📦 Response complète:', response);
      console.log('📦 Response.data:', response.data);
      console.log('📦 Type de response.data:', typeof response.data);
      console.log('📦 response.data est un tableau?', Array.isArray(response.data));
      
      // Si response.data est déjà un tableau, le retourner
      if (Array.isArray(response.data)) {
        console.log('✅ Tableau de médecins:', response.data);
        return response.data;
      }
      
      // Si response.data a une propriété 'data' qui est un tableau
      if (response.data?.data && Array.isArray(response.data.data)) {
        console.log('✅ Médecins dans data.data:', response.data.data);
        return response.data.data;
      }
      
      // Si response.data a une propriété 'content' (pagination Spring)
      if (response.data?.content && Array.isArray(response.data.content)) {
        console.log('✅ Médecins dans content:', response.data.content);
        return response.data.content;
      }
      
      // Si response.data a une propriété '_embedded'
      if (response.data?._embedded) {
        const embeddedKey = Object.keys(response.data._embedded)[0];
        console.log('✅ Médecins dans _embedded:', response.data._embedded[embeddedKey]);
        return response.data._embedded[embeddedKey];
      }
      
      console.log('⚠️ Format inattendu, retour du data brut:', response.data);
      return response.data || [];
      
    } catch (error) {
      console.error('❌ Erreur getDoctors:', error);
      return [];
    }
  },
  
  // Rendez-vous avec logs
  getAppointments: async (patientId) => {
    try {
      console.log(`🔍 Appel API /appointments/patient/${patientId}...`);
      const response = await api.get(`/appointments/patient/${patientId}`);
      
      console.log('📦 Appointments response:', response.data);
      
      if (Array.isArray(response.data)) {
        return response.data;
      }
      if (response.data?.content && Array.isArray(response.data.content)) {
        return response.data.content;
      }
      return response.data || [];
    } catch (error) {
      console.error('❌ Erreur getAppointments:', error);
      return [];
    }
  },
  
  // Résultats labo avec logs
  getLabResults: async (patientId) => {
    try {
      console.log(`🔍 Appel API /lab/patient/${patientId}...`);
      const response = await api.get(`/lab/patient/${patientId}`);
      
      console.log('📦 LabResults response:', response.data);
      
      if (Array.isArray(response.data)) {
        return response.data;
      }
      if (response.data?.content && Array.isArray(response.data.content)) {
        return response.data.content;
      }
      return response.data || [];
    } catch (error) {
      console.error('❌ Erreur getLabResults:', error);
      return [];
    }
  },
  
  // Prescriptions avec logs
  getPrescriptions: async (patientId) => {
    try {
      console.log(`🔍 Appel API /prescriptions/patient/${patientId}...`);
      const response = await api.get(`/prescriptions/patient/${patientId}`);
      
      console.log('📦 Prescriptions response:', response.data);
      
      if (Array.isArray(response.data)) {
        return response.data;
      }
      if (response.data?.content && Array.isArray(response.data.content)) {
        return response.data.content;
      }
      return response.data || [];
    } catch (error) {
      console.error('❌ Erreur getPrescriptions:', error);
      return [];
    }
  },
  
  createAppointment: async (appointmentData) => {
    try {
      console.log('🔍 Données reçues pour création RDV:', appointmentData);
      
      // Vérifier que les données requises sont présentes
      if (!appointmentData.patientId || !appointmentData.doctorId || !appointmentData.startTime) {
        throw new Error('Données manquantes: patientId, doctorId et startTime sont requis');
      }
      
      // Formater les dates au format ISO
      const startTime = new Date(appointmentData.startTime);
      const endTime = appointmentData.endTime 
        ? new Date(appointmentData.endTime)
        : new Date(startTime.getTime() + 60 * 60 * 1000); // +1 heure par défaut
      
      // Calculer la durée en minutes
      const duration = Math.round((endTime - startTime) / (60 * 1000));
      
      // Construire l'objet selon le format attendu par l'API
      const formattedData = {
        patientId: appointmentData.patientId,
        patientName: appointmentData.patientName || '',
        doctorId: appointmentData.doctorId,
        doctorName: appointmentData.doctorName || '',
        startTime: startTime.toISOString(),
        endTime: endTime.toISOString(),
        reason: appointmentData.reason || 'Consultation',
        status: 'SCHEDULED',  // Valeur par défaut selon l'enum AppointmentStatus
        type: appointmentData.type || 'CONSULTATION',  // Valeur par défaut selon l'enum AppointmentType
        isVirtual: appointmentData.isVirtual || false,
        duration: duration,
        location: appointmentData.location || 'Cabinet médical'
      };
      
      console.log('📦 Données formatées envoyées:', formattedData);
      
      const response = await api.post('/api/appointments', formattedData);
      console.log('✅ Rendez-vous créé avec succès:', response.data);
      return response.data;
      
    } catch (error) {
      console.error('❌ Erreur création rendez-vous:', error);
      
      // Afficher plus de détails sur l'erreur
      if (error.response) {
        console.error('📋 Status:', error.response.status);
        console.error('📋 Données d\'erreur:', error.response.data);
        console.error('📋 Headers:', error.response.headers);
        
        // Si l'API retourne un message d'erreur détaillé
        if (error.response.data && error.response.data.message) {
          throw new Error(error.response.data.message);
        }
      } else if (error.request) {
        console.error('📋 Pas de réponse reçue');
      } else {
        console.error('📋 Erreur:', error.message);
      }
      
      throw error;
    }
  },
  
  // Annuler un rendez-vous
  cancelAppointment: async (id, reason) => {
    try {
      const response = await api.patch(`/appointments/${id}/cancel`, { cancellationReason: reason });
      return response.data;
    } catch (error) {
      console.error('❌ Erreur annulation rendez-vous:', error);
      throw error;
    }
  },

  // Garder les autres méthodes
  getDoctorsBySpecialty: (specialty) => api.get(`/doctors/specialty/${specialty}`),
  getAvailableDoctors: () => api.get('/doctors/available'),
  getMedicalRecords: (patientId) => api.get(`/patients/${patientId}`),
};

export default patientService;