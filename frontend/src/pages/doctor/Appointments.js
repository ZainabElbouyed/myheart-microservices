// frontend/src/pages/doctor/DoctorAppointments.js
import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Paper,
  Grid,
  Card,
  CardContent,
  Chip,
  Button,
  Alert,
  IconButton,
  Tabs,
  Tab,
  TextField,
  InputAdornment,
} from '@mui/material';
import {
  CalendarToday as CalendarIcon,
  Search as SearchIcon,
  Visibility as VisibilityIcon,
  CheckCircle as CheckCircleIcon,
  Cancel as CancelIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { useAuth } from '../../contexts/AuthContext';
import { doctorService } from '../../services/doctorService';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const DoctorAppointments = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [tabValue, setTabValue] = useState(0);
  const [searchTerm, setSearchTerm] = useState('');

  console.log('👤 User médecin:', user);

  // ✅ Récupérer tous les rendez-vous du médecin
  const { 
    data: allAppointments = [], 
    isLoading: allLoading,
    error: allError 
  } = useQuery({
    queryKey: ['doctorAppointments', user?.id],
    queryFn: () => doctorService.getAppointments(user?.id),
    enabled: !!user?.id,
  });

  // ✅ Récupérer les rendez-vous du jour
  const { 
    data: todayAppointments = [], 
    isLoading: todayLoading,
    error: todayError 
  } = useQuery({
    queryKey: ['doctorTodayAppointments', user?.id],
    queryFn: () => doctorService.getTodayAppointments(user?.id),
    enabled: !!user?.id,
  });

  useEffect(() => {
    console.log('📊 Tous les rendez-vous:', allAppointments);
    console.log('📊 Rendez-vous du jour:', todayAppointments);
  }, [allAppointments, todayAppointments]);

  // S'assurer que ce sont des tableaux
  const allAppointmentsList = Array.isArray(allAppointments) ? allAppointments : 
                               allAppointments?.data ? allAppointments.data :
                               allAppointments?.content ? allAppointments.content : [];

  const todayAppointmentsList = Array.isArray(todayAppointments) ? todayAppointments : 
                                 todayAppointments?.data ? todayAppointments.data :
                                 todayAppointments?.content ? todayAppointments.content : [];

  const getFilteredAppointments = () => {
    const source = tabValue === 0 ? todayAppointmentsList : allAppointmentsList;
    if (!searchTerm) return source;
    
    return source.filter(apt =>
      (apt.patientName?.toLowerCase() || '').includes(searchTerm.toLowerCase()) ||
      (apt.reason?.toLowerCase() || '').includes(searchTerm.toLowerCase()) ||
      (apt.status?.toLowerCase() || '').includes(searchTerm.toLowerCase())
    );
  };

  const filteredAppointments = getFilteredAppointments();

  const getStatusColor = (status) => {
    switch (status?.toUpperCase()) {
      case 'CONFIRMED': return 'success';
      case 'SCHEDULED': return 'primary';
      case 'COMPLETED': return 'info';
      case 'CANCELLED': return 'error';
      case 'NO_SHOW': return 'warning';
      default: return 'default';
    }
  };

  const getStatusLabel = (status) => {
    switch (status?.toUpperCase()) {
      case 'CONFIRMED': return 'Confirmé';
      case 'SCHEDULED': return 'Planifié';
      case 'COMPLETED': return 'Terminé';
      case 'CANCELLED': return 'Annulé';
      case 'NO_SHOW': return 'Absent';
      default: return status || 'Inconnu';
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'Date inconnue';
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  };

  const formatTime = (dateString) => {
    if (!dateString) return 'Heure inconnue';
    return new Date(dateString).toLocaleTimeString('fr-FR', {
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (allLoading || todayLoading) return <LoadingSpinner />;

  if (allError || todayError) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Erreur lors du chargement des rendez-vous
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" fontWeight="500" color="#2E7D32" gutterBottom>
        Rendez-vous
      </Typography>

      {/* Statistiques rapides */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={6} sm={3}>
          <Card sx={{ bgcolor: '#E8F5E9' }}>
            <CardContent>
              <Typography variant="h4" color="#2E7D32" align="center">
                {todayAppointmentsList.length}
              </Typography>
              <Typography variant="body2" color="text.secondary" align="center">
                Aujourd'hui
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={6} sm={3}>
          <Card sx={{ bgcolor: '#FFF3E0' }}>
            <CardContent>
              <Typography variant="h4" color="#ED6C02" align="center">
                {allAppointmentsList.filter(a => a.status === 'SCHEDULED').length}
              </Typography>
              <Typography variant="body2" color="text.secondary" align="center">
                À venir
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={6} sm={3}>
          <Card sx={{ bgcolor: '#E3F2FD' }}>
            <CardContent>
              <Typography variant="h4" color="#1976D2" align="center">
                {allAppointmentsList.filter(a => a.status === 'COMPLETED').length}
              </Typography>
              <Typography variant="body2" color="text.secondary" align="center">
                Terminés
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={6} sm={3}>
          <Card sx={{ bgcolor: '#FFEBEE' }}>
            <CardContent>
              <Typography variant="h4" color="#D32F2F" align="center">
                {allAppointmentsList.filter(a => a.status === 'CANCELLED').length}
              </Typography>
              <Typography variant="body2" color="text.secondary" align="center">
                Annulés
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Paper sx={{ width: '100%', mb: 2 }}>
        <Tabs
          value={tabValue}
          onChange={(e, v) => setTabValue(v)}
          indicatorColor="primary"
          textColor="primary"
          sx={{ borderBottom: 1, borderColor: 'divider' }}
        >
          <Tab label="Aujourd'hui" />
          <Tab label="Tous les rendez-vous" />
        </Tabs>

        <Box sx={{ p: 2 }}>
          <TextField
            fullWidth
            placeholder="Rechercher un rendez-vous..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
            }}
            sx={{ mb: 3 }}
          />

          {filteredAppointments.length === 0 ? (
            <Alert severity="info">
              {searchTerm 
                ? 'Aucun rendez-vous ne correspond à votre recherche'
                : tabValue === 0 
                  ? 'Aucun rendez-vous prévu aujourd\'hui'
                  : 'Aucun rendez-vous'}
            </Alert>
          ) : (
            filteredAppointments.map((apt) => (
              <Card key={apt.id} variant="outlined" sx={{ mb: 2 }}>
                <CardContent>
                  <Grid container spacing={2} alignItems="center">
                    <Grid item xs={12} sm={2}>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <CalendarIcon sx={{ mr: 1, color: '#2E7D32' }} />
                        <Box>
                          <Typography variant="body2" color="text.secondary">
                            {formatDate(apt.startTime)}
                          </Typography>
                          <Typography variant="h6" color="primary">
                            {formatTime(apt.startTime)}
                          </Typography>
                        </Box>
                      </Box>
                    </Grid>
                    
                    <Grid item xs={12} sm={3}>
                      <Typography variant="body1" fontWeight="bold">
                        {apt.patientName || 'Patient'}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {apt.reason || 'Consultation'}
                      </Typography>
                    </Grid>
                    
                    <Grid item xs={6} sm={2}>
                      <Chip
                        label={apt.type || 'Consultation'}
                        size="small"
                        variant="outlined"
                      />
                    </Grid>
                    
                    <Grid item xs={6} sm={2}>
                      <Chip
                        label={getStatusLabel(apt.status)}
                        color={getStatusColor(apt.status)}
                        size="small"
                      />
                    </Grid>
                    
                    <Grid item xs={12} sm={3}>
                      <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
                        <IconButton
                          color="primary"
                          onClick={() => navigate(`/doctor/appointments/${apt.id}`)}
                          size="small"
                        >
                          <VisibilityIcon />
                        </IconButton>
                        {apt.status !== 'COMPLETED' && apt.status !== 'CANCELLED' && (
                          <>
                            <IconButton color="success" size="small">
                              <CheckCircleIcon />
                            </IconButton>
                            <IconButton color="error" size="small">
                              <CancelIcon />
                            </IconButton>
                          </>
                        )}
                      </Box>
                    </Grid>
                  </Grid>
                </CardContent>
              </Card>
            ))
          )}
        </Box>
      </Paper>
    </Box>
  );
};

export default DoctorAppointments;