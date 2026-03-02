// frontend/src/pages/patient/PatientDashboard.js
import React from 'react';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  Avatar,
  Chip,
  Paper,
  Alert,
} from '@mui/material';
import {
  LocalHospital as DoctorIcon,
  CalendarToday as AppointmentIcon,
  Science as LabIcon,
} from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';
import { useQuery } from '@tanstack/react-query';
import { patientService } from '../../services/patientService';
import { useNavigate } from 'react-router-dom';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const PatientDashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  // ✅ Vérifier que l'utilisateur est connecté
  console.log('👤 User dans dashboard:', user);

  // ✅ Récupérer la liste des médecins
  const { 
    data: doctors, 
    isLoading: doctorsLoading, 
    error: doctorsError 
  } = useQuery({
    queryKey: ['doctors'],
    queryFn: async () => {
      try {
        const response = await patientService.getDoctors();
        console.log('✅ Médecins reçus:', response);
        return response;
      } catch (error) {
        console.error('❌ Erreur chargement médecins:', error);
        throw error;
      }
    },
  });

  // ✅ Récupérer les rendez-vous (seulement si user.id existe)
  const { 
    data: appointments, 
    isLoading: appointmentsLoading,
    error: appointmentsError 
  } = useQuery({
    queryKey: ['patientAppointments', user?.id],
    queryFn: async () => {
      if (!user?.id) return [];
      try {
        const response = await patientService.getAppointments(user.id);
        console.log('✅ Rendez-vous reçus:', response);
        return response;
      } catch (error) {
        console.error('❌ Erreur chargement rendez-vous:', error);
        throw error;
      }
    },
    enabled: !!user?.id, // Ne s'exécute que si user.id existe
  });

  // ✅ Récupérer les résultats laboratoire
  const { 
    data: labResults, 
    isLoading: labResultsLoading,
    error: labResultsError 
  } = useQuery({
    queryKey: ['patientLabResults', user?.id],
    queryFn: async () => {
      if (!user?.id) return [];
      try {
        const response = await patientService.getLabResults(user.id);
        console.log('✅ Résultats labo reçus:', response);
        return response;
      } catch (error) {
        console.error('❌ Erreur chargement résultats labo:', error);
        throw error;
      }
    },
    enabled: !!user?.id,
  });

  // ✅ Transformation des données en tableaux
  const doctorsList = Array.isArray(doctors) ? doctors : 
                     doctors?.data ? doctors.data : 
                     doctors?.content ? doctors.content : 
                     [];
                     
  const appointmentsList = Array.isArray(appointments) ? appointments : 
                          appointments?.data ? appointments.data : 
                          appointments?.content ? appointments.content : 
                          [];
                          
  const labResultsList = Array.isArray(labResults) ? labResults : 
                        labResults?.data ? labResults.data : 
                        labResults?.content ? labResults.content : 
                        [];

  console.log('📊 doctorsList final:', doctorsList);
  console.log('📊 appointmentsList final:', appointmentsList);
  console.log('📊 labResultsList final:', labResultsList);

  // ✅ Gestion des états de chargement
  if (doctorsLoading || appointmentsLoading || labResultsLoading) {
    return <LoadingSpinner />;
  }

  // ✅ Gestion des erreurs
  if (doctorsError || appointmentsError || labResultsError) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Erreur de chargement des données. Veuillez rafraîchir la page.
        </Alert>
      </Box>
    );
  }

  // ✅ RENDU RÉEL DU TABLEAU DE BORD
  return (
    <Box sx={{ p: 3 }}>
      {/* En-tête avec bienvenue */}
      <Paper elevation={3} sx={{ p: 4, mb: 4, borderRadius: 3, background: 'linear-gradient(135deg, #1976d2 0%, #64b5f6 100%)', color: 'white' }}>
        <Typography variant="h3" gutterBottom fontWeight="500">
          Bonjour, {user?.firstName || 'Patient'} {user?.lastName || ''}
        </Typography>
        <Typography variant="h6">
          Bienvenue sur votre espace personnel MyHeart
        </Typography>
      </Paper>

      {/* SECTION MÉDECINS */}
      <Typography variant="h4" gutterBottom sx={{ mb: 3, fontWeight: '500', color: '#1976d2' }}>
        👨‍⚕️ Médecins disponibles
      </Typography>
      
      {doctorsList.length === 0 ? (
        <Alert severity="info" sx={{ mb: 4 }}>
          Aucun médecin n'est disponible pour le moment.
        </Alert>
      ) : (
        <Grid container spacing={3} sx={{ mb: 6 }}>
          {doctorsList.slice(0, 4).map((doctor) => (
            <Grid item xs={12} sm={6} md={3} key={doctor.id}>
              <Card 
                elevation={3}
                sx={{ 
                  height: '100%',
                  display: 'flex',
                  flexDirection: 'column',
                  transition: 'transform 0.2s',
                  '&:hover': { transform: 'scale(1.02)' }
                }}
              >
                <CardContent sx={{ flexGrow: 1, textAlign: 'center' }}>
                  <Avatar 
                    sx={{ 
                      bgcolor: '#1976d2', 
                      width: 80, 
                      height: 80, 
                      margin: '0 auto 16px auto',
                      fontSize: '2rem'
                    }}
                  >
                    <DoctorIcon fontSize="large" />
                  </Avatar>
                  
                  <Typography variant="h6" gutterBottom>
                    Dr. {doctor.firstName} {doctor.lastName}
                  </Typography>
                  
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    {doctor.specialty || 'Médecin généraliste'}
                  </Typography>
                  
                  <Chip
                    label={doctor.acceptingNewPatients ? 'Disponible' : 'Complet'}
                    color={doctor.acceptingNewPatients ? 'success' : 'default'}
                    size="small"
                    sx={{ mt: 1, mb: 2 }}
                  />
                  
                  <Button
                    fullWidth
                    variant="contained"
                    onClick={() => navigate('/patient/appointments/new', { 
                      state: { selectedDoctor: doctor } 
                    })}
                    sx={{ mt: 'auto' }}
                  >
                    Prendre RDV
                  </Button>
                </CardContent>
              </Card>
            </Grid>
          ))}
          
          {doctorsList.length > 4 && (
            <Grid item xs={12} sx={{ textAlign: 'center', mt: 2 }}>
              <Button 
                variant="outlined" 
                size="large"
                onClick={() => navigate('/patient/doctors')}
              >
                Voir tous les médecins ({doctorsList.length})
              </Button>
            </Grid>
          )}
        </Grid>
      )}

      {/* SECTION RENDEZ-VOUS */}
      <Typography variant="h4" gutterBottom sx={{ mb: 3, fontWeight: '500', color: '#1976d2' }}>
        📅 Prochains rendez-vous
      </Typography>
      
      {appointmentsList.length === 0 ? (
        <Alert severity="info" sx={{ mb: 4 }}>
          Vous n'avez aucun rendez-vous prévu.
        </Alert>
      ) : (
        <Grid container spacing={2} sx={{ mb: 6 }}>
          {appointmentsList.slice(0, 3).map((apt) => (
            <Grid item xs={12} key={apt.id}>
              <Card variant="outlined" sx={{ '&:hover': { bgcolor: '#f5f5f5' } }}>
                <CardContent>
                  <Grid container alignItems="center" spacing={2}>
                    <Grid item xs={12} sm={2}>
                      <AppointmentIcon color="primary" sx={{ fontSize: 40 }} />
                    </Grid>
                    <Grid item xs={12} sm={3}>
                      <Typography variant="body1" fontWeight="bold">
                        {new Date(apt.startTime).toLocaleDateString('fr-FR', {
                          weekday: 'long',
                          year: 'numeric',
                          month: 'long',
                          day: 'numeric'
                        })}
                      </Typography>
                    </Grid>
                    <Grid item xs={12} sm={2}>
                      <Typography variant="body1">
                        {new Date(apt.startTime).toLocaleTimeString('fr-FR', { 
                          hour: '2-digit', 
                          minute: '2-digit' 
                        })}
                      </Typography>
                    </Grid>
                    <Grid item xs={12} sm={3}>
                      <Typography variant="body1">
                        Dr. {apt.doctorName}
                      </Typography>
                    </Grid>
                    <Grid item xs={12} sm={2}>
                      <Chip
                        label={apt.status}
                        color={apt.status === 'CONFIRMED' ? 'success' : 
                               apt.status === 'PENDING' ? 'warning' : 'primary'}
                        size="small"
                      />
                    </Grid>
                  </Grid>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      {/* SECTION RÉSULTATS LABO */}
      <Typography variant="h4" gutterBottom sx={{ mb: 3, fontWeight: '500', color: '#1976d2' }}>
        🔬 Résultats récents
      </Typography>
      
      {labResultsList.length === 0 ? (
        <Alert severity="info">
          Aucun résultat d'analyse disponible.
        </Alert>
      ) : (
        <Grid container spacing={3}>
          {labResultsList.slice(0, 3).map((result) => (
            <Grid item xs={12} sm={6} md={4} key={result.id}>
              <Card 
                variant="outlined"
                sx={{ 
                  height: '100%',
                  '&:hover': { bgcolor: '#f5f5f5' }
                }}
              >
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <LabIcon color="primary" sx={{ mr: 1 }} />
                    <Typography variant="h6">
                      {result.testType}
                    </Typography>
                  </Box>
                  
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    {new Date(result.testDate).toLocaleDateString('fr-FR', {
                      day: 'numeric',
                      month: 'long',
                      year: 'numeric'
                    })}
                  </Typography>
                  
                  <Chip
                    label={result.status}
                    color={result.status === 'COMPLETED' ? 'success' : 
                           result.status === 'PENDING' ? 'warning' : 'primary'}
                    size="small"
                    sx={{ mt: 1 }}
                  />
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Box>
  );
};

export default PatientDashboard;