// frontend/src/pages/lab/LabDashboard.js
import React, { useEffect } from 'react';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Paper,
  LinearProgress,
  Alert,
} from '@mui/material';
import {
  Science as ScienceIcon,
  Pending as PendingIcon,
  CheckCircle as CheckCircleIcon,
  Timeline as TimelineIcon,
} from '@mui/icons-material';
import { useQuery } from '@tanstack/react-query';
import { labService } from '../../services/labService';
import { useNavigate } from 'react-router-dom';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const LabDashboard = () => {
  const navigate = useNavigate();

  console.log('🔍 Chargement du tableau de bord laboratoire...');

  // Récupérer les tests en attente
  const { 
    data: pendingTests = [], 
    isLoading: pendingLoading,
    error: pendingError,
  } = useQuery({
    queryKey: ['pendingTests'],
    queryFn: labService.getPendingTests,
  });

  // Récupérer les tests en cours
  const { 
    data: inProgressTests = [], 
    isLoading: inProgressLoading,
  } = useQuery({
    queryKey: ['inProgressTests'],
    queryFn: labService.getInProgressTests,
  });

  // Récupérer les tests complétés
  const { 
    data: completedTests = [], 
    isLoading: completedLoading,
  } = useQuery({
    queryKey: ['completedTests'],
    queryFn: labService.getCompletedTests,
  });

  // Récupérer les statistiques
  const { 
    data: stats = { pending: 0, inProgress: 0, completed: 0, total: 0 }, 
    isLoading: statsLoading,
  } = useQuery({
    queryKey: ['labStats'],
    queryFn: labService.getStats,
  });

  useEffect(() => {
    console.log('📊 Tests en attente:', pendingTests);
    console.log('📊 Tests en cours:', inProgressTests);
    console.log('📊 Tests complétés:', completedTests);
    console.log('📊 Statistiques:', stats);
  }, [pendingTests, inProgressTests, completedTests, stats]);

  // S'assurer que ce sont des tableaux
  const pendingList = Array.isArray(pendingTests) ? pendingTests : [];
  const inProgressList = Array.isArray(inProgressTests) ? inProgressTests : [];
  const completedList = Array.isArray(completedTests) ? completedTests : [];

  const isLoading = pendingLoading || inProgressLoading || completedLoading || statsLoading;

  if (isLoading) return <LoadingSpinner />;

  if (pendingError) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Erreur lors du chargement des données du laboratoire
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" fontWeight="500" color="#1976d2" gutterBottom>
        Laboratoire - Tableau de bord
      </Typography>

      {/* Statistiques */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={3}>
          <Card sx={{ bgcolor: '#FFF3E0', height: '100%' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom variant="h6">
                    En attente
                  </Typography>
                  <Typography variant="h2" color="warning.main" fontWeight="bold">
                    {stats.pending}
                  </Typography>
                </Box>
                <PendingIcon sx={{ fontSize: 48, color: 'warning.main', opacity: 0.8 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        
        <Grid item xs={12} sm={3}>
          <Card sx={{ bgcolor: '#E3F2FD', height: '100%' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom variant="h6">
                    En cours
                  </Typography>
                  <Typography variant="h2" color="info.main" fontWeight="bold">
                    {stats.inProgress}
                  </Typography>
                </Box>
                <ScienceIcon sx={{ fontSize: 48, color: 'info.main', opacity: 0.8 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        
        <Grid item xs={12} sm={3}>
          <Card sx={{ bgcolor: '#E8F5E9', height: '100%' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom variant="h6">
                    Terminés
                  </Typography>
                  <Typography variant="h2" color="success.main" fontWeight="bold">
                    {stats.completed}
                  </Typography>
                </Box>
                <CheckCircleIcon sx={{ fontSize: 48, color: 'success.main', opacity: 0.8 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        
        <Grid item xs={12} sm={3}>
          <Card sx={{ bgcolor: '#F3E5F5', height: '100%' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom variant="h6">
                    Total
                  </Typography>
                  <Typography variant="h2" color="secondary.main" fontWeight="bold">
                    {stats.total}
                  </Typography>
                </Box>
                <TimelineIcon sx={{ fontSize: 48, color: 'secondary.main', opacity: 0.8 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Tests en attente */}
      <Typography variant="h5" gutterBottom sx={{ mt: 3, mb: 2 }}>
        Tests à traiter
      </Typography>
      <Paper sx={{ p: 2, mb: 4 }}>
        {pendingList.length === 0 ? (
          <Typography color="text.secondary" sx={{ py: 2, textAlign: 'center' }}>
            Aucun test en attente
          </Typography>
        ) : (
          <>
            <List>
              {pendingList.slice(0, 5).map((test) => (
                <ListItem
                  key={test.id}
                  secondaryAction={
                    <Button
                      variant="contained"
                      size="small"
                      color="warning"
                      onClick={() => navigate('/lab/pending')}
                    >
                      Traiter
                    </Button>
                  }
                  divider
                >
                  <ListItemIcon>
                    <ScienceIcon color="warning" />
                  </ListItemIcon>
                  <ListItemText
                    primary={
                      <Typography variant="subtitle1" fontWeight="medium">
                        {test.patientName || 'Patient'} - {test.testType || 'Analyse'}
                      </Typography>
                    }
                    secondary={
                      <Typography variant="body2" color="text.secondary">
                        Dr. {test.doctorName || 'Médecin'} • {test.testDate ? new Date(test.testDate).toLocaleDateString('fr-FR') : 'Date inconnue'}
                      </Typography>
                    }
                  />
                </ListItem>
              ))}
            </List>
            {pendingList.length > 5 && (
              <Button 
                fullWidth 
                onClick={() => navigate('/lab/pending')}
                sx={{ mt: 2 }}
              >
                Voir tous les tests ({pendingList.length})
              </Button>
            )}
          </>
        )}
      </Paper>

      {/* Tests en cours */}
      {inProgressList.length > 0 && (
        <>
          <Typography variant="h5" gutterBottom sx={{ mb: 2 }}>
            Tests en cours
          </Typography>
          <Grid container spacing={2} sx={{ mb: 4 }}>
            {inProgressList.slice(0, 3).map((test) => (
              <Grid item xs={12} key={test.id}>
                <Card variant="outlined">
                  <CardContent>
                    <Grid container alignItems="center" spacing={2}>
                      <Grid item xs={12} sm={3}>
                        <Typography variant="subtitle1" fontWeight="bold">
                          {test.patientName || 'Patient'}
                        </Typography>
                      </Grid>
                      <Grid item xs={12} sm={3}>
                        <Typography variant="body2" color="text.secondary">
                          {test.testType || 'Analyse'}
                        </Typography>
                      </Grid>
                      <Grid item xs={12} sm={4}>
                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                          <Box sx={{ width: '100%', mr: 1 }}>
                            <LinearProgress 
                              variant="determinate" 
                              value={50} 
                              sx={{ height: 8, borderRadius: 4 }}
                            />
                          </Box>
                          <Typography variant="body2" color="text.secondary">
                            50%
                          </Typography>
                        </Box>
                      </Grid>
                      <Grid item xs={12} sm={2}>
                        <Button 
                          size="small" 
                          variant="outlined"
                          onClick={() => navigate('/lab/pending')}
                        >
                          Continuer
                        </Button>
                      </Grid>
                    </Grid>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </>
      )}

      {/* Actions rapides */}
      <Paper sx={{ p: 3, mt: 3 }}>
        <Typography variant="h6" gutterBottom>
          Actions rapides
        </Typography>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={4}>
            <Button
              fullWidth
              variant="contained"
              size="large"
              startIcon={<PendingIcon />}
              onClick={() => navigate('/lab/pending')}
            >
              Tests en attente
            </Button>
          </Grid>
          <Grid item xs={12} sm={4}>
            <Button
              fullWidth
              variant="contained"
              color="info"
              size="large"
              startIcon={<ScienceIcon />}
              onClick={() => navigate('/lab/upload')}
            >
              Uploader résultat
            </Button>
          </Grid>
          <Grid item xs={12} sm={4}>
            <Button
              fullWidth
              variant="contained"
              color="success"
              size="large"
              startIcon={<CheckCircleIcon />}
              onClick={() => navigate('/lab/results')}
            >
              Résultats
            </Button>
          </Grid>
        </Grid>
      </Paper>
    </Box>
  );
};

export default LabDashboard;