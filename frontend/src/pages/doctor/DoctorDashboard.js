// frontend/src/pages/doctor/DoctorDashboard.js
import React, { useEffect } from 'react';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  Avatar,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Chip,
  Paper,
  Alert,
} from '@mui/material';
import {
  People as PeopleIcon,
  CalendarToday as CalendarIcon,
  Science as LabIcon,
} from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';
import { useQuery } from '@tanstack/react-query';
import { doctorService } from '../../services/doctorService';
import { useNavigate } from 'react-router-dom';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const DoctorDashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  console.log('👤 User médecin:', user);

  // Récupérer les patients du médecin
  const { 
    data: patients = [], 
    isLoading: patientsLoading,
    error: patientsError,
    refetch: refetchPatients
  } = useQuery({
    queryKey: ['doctorPatients', user?.id],
    queryFn: () => doctorService.getPatients(user?.id),
    enabled: !!user?.id,
  });

  // Récupérer les résultats labo en attente
  const { 
    data: pendingResults = [], 
    isLoading: pendingResultsLoading,
    error: pendingResultsError 
  } = useQuery({
    queryKey: ['pendingLabResults', user?.id],
    queryFn: () => doctorService.getPendingLabResults(user?.id),
    enabled: !!user?.id,
  });

  // Rendez-vous du jour
  const { 
    data: todayAppointments = [], 
    isLoading: todayAppointmentsLoading,
    error: todayAppointmentsError 
  } = useQuery({
    queryKey: ['todayAppointments', user?.id],
    queryFn: () => doctorService.getTodayAppointments(user?.id),
    enabled: !!user?.id,
  });

  // Logs pour déboguer
  useEffect(() => {
    console.log('📊 Patients reçus:', patients);
    console.log('📊 Rendez-vous du jour:', todayAppointments);
    console.log('📊 Résultats en attente:', pendingResults);
  }, [patients, todayAppointments, pendingResults]);

  // Vérifier que ce sont des tableaux
  const patientsList = Array.isArray(patients) ? patients : [];
  const todayAppointmentsList = Array.isArray(todayAppointments) ? todayAppointments : [];
  const pendingResultsList = Array.isArray(pendingResults) ? pendingResults : [];

  if (patientsLoading || todayAppointmentsLoading || pendingResultsLoading) {
    return <LoadingSpinner />;
  }

  if (patientsError || todayAppointmentsError || pendingResultsError) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Erreur de chargement des données. Veuillez rafraîchir.
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      {/* En-tête */}
      <Paper 
        elevation={3} 
        sx={{ 
          p: 4, 
          mb: 4, 
          borderRadius: 3, 
          background: 'linear-gradient(135deg, #2E7D32 0%, #81C784 100%)', 
          color: 'white' 
        }}
      >
        <Typography variant="h3" gutterBottom fontWeight="500">
          Dr. {user?.firstName || ''} {user?.lastName || ''}
        </Typography>
        <Typography variant="h6">
          Tableau de bord médecin
        </Typography>
      </Paper>

      {/* Statistiques */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={4}>
          <Card sx={{ height: '100%', bgcolor: '#E3F2FD' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom variant="h6">
                    Patients
                  </Typography>
                  <Typography variant="h2" color="primary.main" fontWeight="bold">
                    {patientsList.length}
                  </Typography>
                </Box>
                <Avatar sx={{ bgcolor: 'primary.main', width: 60, height: 60 }}>
                  <PeopleIcon fontSize="large" />
                </Avatar>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={4}>
          <Card sx={{ height: '100%', bgcolor: '#FFF3E0' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom variant="h6">
                    Rendez-vous aujourd'hui
                  </Typography>
                  <Typography variant="h2" color="warning.main" fontWeight="bold">
                    {todayAppointmentsList.length}
                  </Typography>
                </Box>
                <Avatar sx={{ bgcolor: 'warning.main', width: 60, height: 60 }}>
                  <CalendarIcon fontSize="large" />
                </Avatar>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={4}>
          <Card sx={{ height: '100%', bgcolor: '#FFEBEE' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom variant="h6">
                    Résultats en attente
                  </Typography>
                  <Typography variant="h2" color="error.main" fontWeight="bold">
                    {pendingResultsList.length}
                  </Typography>
                </Box>
                <Avatar sx={{ bgcolor: 'error.main', width: 60, height: 60 }}>
                  <LabIcon fontSize="large" />
                </Avatar>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Section Patients */}
      <Typography variant="h4" gutterBottom sx={{ mb: 3, fontWeight: '500', color: '#2E7D32' }}>
        👥 Mes patients ({patientsList.length})
      </Typography>
      
      {patientsList.length === 0 ? (
        <Alert severity="info" sx={{ mb: 4 }}>
          Vous n'avez pas encore de patients.
        </Alert>
      ) : (
        <Grid container spacing={3} sx={{ mb: 4 }}>
          {patientsList.slice(0, 6).map((patient) => (
            <Grid item xs={12} sm={6} md={4} key={patient.id}>
              <Card 
                elevation={2}
                sx={{ 
                  height: '100%',
                  transition: 'transform 0.2s',
                  '&:hover': { transform: 'scale(1.02)', boxShadow: 4 }
                }}
              >
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <Avatar 
                      sx={{ 
                        mr: 2, 
                        bgcolor: '#2E7D32',
                        width: 50,
                        height: 50,
                        fontSize: '1.5rem'
                      }}
                    >
                      {patient.firstName?.charAt(0) || 'P'}
                    </Avatar>
                    <Box>
                      <Typography variant="h6" fontWeight="bold">
                        {patient.firstName || ''} {patient.lastName || ''}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {patient.email || 'Email non disponible'}
                      </Typography>
                    </Box>
                  </Box>
                  <Button
                    fullWidth
                    variant="contained"
                    color="primary"
                    onClick={() => navigate(`/doctor/patients/${patient.id}`)}
                    sx={{ mt: 1 }}
                  >
                    Voir le dossier
                  </Button>
                </CardContent>
              </Card>
            </Grid>
          ))}
          {patientsList.length > 6 && (
            <Grid item xs={12} sx={{ textAlign: 'center', mt: 2 }}>
              <Button 
                variant="outlined" 
                color="primary"
                size="large"
                onClick={() => navigate('/doctor/patients')}
              >
                Voir tous les patients ({patientsList.length})
              </Button>
            </Grid>
          )}
        </Grid>
      )}

      {/* Rendez-vous du jour */}
      <Typography variant="h4" gutterBottom sx={{ mb: 3, fontWeight: '500', color: '#ED6C02' }}>
        📅 Rendez-vous aujourd'hui
      </Typography>
      
      {todayAppointmentsList.length === 0 ? (
        <Alert severity="info" sx={{ mb: 4 }}>
          Aucun rendez-vous prévu aujourd'hui.
        </Alert>
      ) : (
        <List sx={{ mb: 4 }}>
          {todayAppointmentsList.map((apt) => (
            <Paper key={apt.id} sx={{ mb: 2, p: 2 }}>
              <ListItem>
                <ListItemIcon>
                  <CalendarIcon color="warning" />
                </ListItemIcon>
                <ListItemText
                  primary={
                    <Typography variant="subtitle1" fontWeight="bold">
                      {apt.patientName || 'Patient'}
                    </Typography>
                  }
                  secondary={
                    <Box>
                      <Typography variant="body2">
                        {new Date(apt.startTime).toLocaleTimeString('fr-FR', { 
                          hour: '2-digit', 
                          minute: '2-digit' 
                        })} - {apt.reason || 'Consultation'}
                      </Typography>
                      <Chip 
                        label={apt.status || 'SCHEDULED'} 
                        size="small" 
                        color={apt.status === 'CONFIRMED' ? 'success' : 'default'}
                        sx={{ mt: 1 }}
                      />
                    </Box>
                  }
                />
                <Button 
                  variant="outlined" 
                  color="warning"
                  onClick={() => navigate(`/doctor/appointments/${apt.id}`)}
                >
                  Détails
                </Button>
              </ListItem>
            </Paper>
          ))}
        </List>
      )}

      {/* Résultats en attente */}
      {pendingResultsList.length > 0 && (
        <>
          <Typography variant="h4" gutterBottom sx={{ mb: 3, fontWeight: '500', color: '#D32F2F' }}>
            🔬 Résultats en attente de révision
          </Typography>
          <List>
            {pendingResultsList.map((result) => (
              <Paper key={result.id} sx={{ mb: 2, p: 2 }}>
                <ListItem
                  secondaryAction={
                    <Button
                      variant="contained"
                      color="error"
                      onClick={() => navigate('/doctor/lab-results')}
                    >
                      Réviser
                    </Button>
                  }
                >
                  <ListItemIcon>
                    <LabIcon color="error" />
                  </ListItemIcon>
                  <ListItemText
                    primary={
                      <Typography variant="subtitle1" fontWeight="bold">
                        {result.patientName || 'Patient'}
                      </Typography>
                    }
                    secondary={
                      <Typography variant="body2">
                        {result.testType || 'Analyse'} - {new Date(result.testDate).toLocaleDateString('fr-FR')}
                      </Typography>
                    }
                  />
                </ListItem>
              </Paper>
            ))}
          </List>
        </>
      )}
    </Box>
  );
};

export default DoctorDashboard;