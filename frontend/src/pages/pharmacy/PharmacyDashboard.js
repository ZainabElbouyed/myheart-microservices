// frontend/src/pages/pharmacy/PharmacyDashboard.js
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
  Alert,
} from '@mui/material';
import {
  Medication as MedicationIcon,
  Pending as PendingIcon,
  Inventory as InventoryIcon,
  Warning as WarningIcon,
} from '@mui/icons-material';
import { useQuery } from '@tanstack/react-query';
import { pharmacyService } from '../../services/pharmacyService';
import { useNavigate } from 'react-router-dom';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const PharmacyDashboard = () => {
  const navigate = useNavigate();

  // ✅ Récupérer les prescriptions en attente
  const { 
    data: pendingPrescriptions = [], 
    isLoading: pendingLoading,
    error: pendingError 
  } = useQuery({
    queryKey: ['pendingPrescriptions'],
    queryFn: pharmacyService.getPendingPrescriptions,
  });

  // ✅ Inventaire
  const { 
    data: inventory = [], 
    isLoading: inventoryLoading,
    error: inventoryError 
  } = useQuery({
    queryKey: ['inventory'],
    queryFn: pharmacyService.getInventory,
  });

  // ✅ Stock faible
  const { 
    data: lowStock = [], 
    isLoading: lowStockLoading,
    error: lowStockError 
  } = useQuery({
    queryKey: ['lowStock'],
    queryFn: pharmacyService.getLowStock,
  });

  useEffect(() => {
    console.log('📊 Prescriptions en attente:', pendingPrescriptions);
    console.log('📊 Inventaire:', inventory);
    console.log('📊 Stocks faibles:', lowStock);
  }, [pendingPrescriptions, inventory, lowStock]);

  const pendingList = Array.isArray(pendingPrescriptions) ? pendingPrescriptions : [];
  const inventoryList = Array.isArray(inventory) ? inventory : [];
  const lowStockList = Array.isArray(lowStock) ? lowStock : [];

  if (pendingLoading || inventoryLoading || lowStockLoading) return <LoadingSpinner />;

  if (pendingError || inventoryError || lowStockError) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Erreur lors du chargement des données
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" fontWeight="500" color="#1976d2" gutterBottom>
        Pharmacie - Tableau de bord
      </Typography>

      {/* Alertes stock faible */}
      {lowStockList.length > 0 && (
        <Alert severity="warning" sx={{ mb: 3 }}>
          {lowStockList.length} médicament(s) en stock faible - Vérifiez les alertes
        </Alert>
      )}

      {/* Statistiques */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={4}>
          <Card sx={{ bgcolor: '#FFF3E0', height: '100%' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom variant="h6">
                    Prescriptions en attente
                  </Typography>
                  <Typography variant="h2" color="warning.main" fontWeight="bold">
                    {pendingList.length}
                  </Typography>
                </Box>
                <PendingIcon sx={{ fontSize: 48, color: 'warning.main', opacity: 0.8 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        
        <Grid item xs={12} sm={4}>
          <Card sx={{ bgcolor: '#E3F2FD', height: '100%' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom variant="h6">
                    Médicaments en stock
                  </Typography>
                  <Typography variant="h2" color="info.main" fontWeight="bold">
                    {inventoryList.length}
                  </Typography>
                </Box>
                <InventoryIcon sx={{ fontSize: 48, color: 'info.main', opacity: 0.8 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        
        <Grid item xs={12} sm={4}>
          <Card sx={{ bgcolor: '#FFEBEE', height: '100%' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="text.secondary" gutterBottom variant="h6">
                    Stock faible
                  </Typography>
                  <Typography variant="h2" color="error.main" fontWeight="bold">
                    {lowStockList.length}
                  </Typography>
                </Box>
                <WarningIcon sx={{ fontSize: 48, color: 'error.main', opacity: 0.8 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Prescriptions en attente */}
      <Typography variant="h5" gutterBottom sx={{ mb: 2 }}>
        Prescriptions à traiter
      </Typography>
      <Paper sx={{ p: 2, mb: 4 }}>
        {pendingList.length === 0 ? (
          <Typography color="text.secondary" sx={{ py: 2, textAlign: 'center' }}>
            Aucune prescription en attente
          </Typography>
        ) : (
          <>
            <List>
              {pendingList.slice(0, 5).map((presc) => (
                <ListItem
                  key={presc.id}
                  secondaryAction={
                    <Button
                      variant="contained"
                      size="small"
                      color="warning"
                      onClick={() => navigate('/pharmacy/prescriptions')}
                    >
                      Traiter
                    </Button>
                  }
                  divider
                >
                  <ListItemIcon>
                    <MedicationIcon color="warning" />
                  </ListItemIcon>
                  <ListItemText
                    primary={
                      <Typography variant="subtitle1" fontWeight="medium">
                        Patient: {presc.patientName || 'Patient'}
                      </Typography>
                    }
                    secondary={
                      <Typography variant="body2" color="text.secondary">
                        Dr. {presc.doctorName || 'Médecin'} • {presc.prescriptionDate ? new Date(presc.prescriptionDate).toLocaleDateString('fr-FR') : ''}
                      </Typography>
                    }
                  />
                </ListItem>
              ))}
            </List>
            {pendingList.length > 5 && (
              <Button 
                fullWidth 
                onClick={() => navigate('/pharmacy/prescriptions')}
                sx={{ mt: 2 }}
              >
                Voir toutes les prescriptions ({pendingList.length})
              </Button>
            )}
          </>
        )}
      </Paper>

      {/* Stock faible */}
      {lowStockList.length > 0 && (
        <>
          <Typography variant="h5" gutterBottom sx={{ mb: 2 }}>
            Alertes stock
          </Typography>
          <Grid container spacing={2}>
            {lowStockList.slice(0, 4).map((item) => (
              <Grid item xs={12} sm={6} key={item.id}>
                <Card variant="outlined" sx={{ borderLeft: '4px solid #f44336' }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                      <WarningIcon color="error" sx={{ mr: 2 }} />
                      <Box>
                        <Typography variant="subtitle1" fontWeight="bold">
                          {item.name}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          Stock: {item.stockQuantity} / Seuil: {item.reorderLevel || 10}
                        </Typography>
                      </Box>
                    </Box>
                    <Button 
                      size="small" 
                      variant="outlined" 
                      color="warning"
                      sx={{ mt: 2 }}
                      onClick={() => navigate('/pharmacy/low-stock')}
                    >
                      Commander
                    </Button>
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
              onClick={() => navigate('/pharmacy/prescriptions')}
            >
              Prescriptions
            </Button>
          </Grid>
          <Grid item xs={12} sm={4}>
            <Button
              fullWidth
              variant="contained"
              color="info"
              size="large"
              startIcon={<InventoryIcon />}
              onClick={() => navigate('/pharmacy/inventory')}
            >
              Inventaire
            </Button>
          </Grid>
          <Grid item xs={12} sm={4}>
            <Button
              fullWidth
              variant="contained"
              color="warning"
              size="large"
              startIcon={<WarningIcon />}
              onClick={() => navigate('/pharmacy/low-stock')}
            >
              Alertes stock
            </Button>
          </Grid>
        </Grid>
      </Paper>
    </Box>
  );
};

export default PharmacyDashboard;