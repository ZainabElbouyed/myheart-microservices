// frontend/src/pages/pharmacy/LowStockAlert.js
import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  Button,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  LinearProgress,
  Grid,
  Card,
  CardContent,
  Alert,
} from '@mui/material';
import {
  Warning as WarningIcon,
  Add as AddIcon,
  History as HistoryIcon,
  ShoppingCart as ShoppingCartIcon,
} from '@mui/icons-material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { pharmacyService } from '../../services/pharmacyService';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const LowStockAlert = () => {
  const queryClient = useQueryClient();
  const [selectedMedicine, setSelectedMedicine] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [orderQuantity, setOrderQuantity] = useState(0);
  const [viewMode, setViewMode] = useState('table'); // 'table' or 'cards'

  const { 
    data: lowStock = [], 
    isLoading,
    error,
    refetch 
  } = useQuery({
    queryKey: ['lowStock'],
    queryFn: pharmacyService.getLowStock,
  });

  useEffect(() => {
    console.log('📊 Stocks faibles reçus:', lowStock);
  }, [lowStock]);

  const lowStockList = Array.isArray(lowStock) ? lowStock : [];

  const updateStockMutation = useMutation({
    mutationFn: ({ id, quantity }) => pharmacyService.updateStock(id, quantity),
    onSuccess: () => {
      queryClient.invalidateQueries(['lowStock']);
      queryClient.invalidateQueries(['inventory']);
      handleCloseDialog();
    },
  });

  const handleOrder = (medicine) => {
    setSelectedMedicine(medicine);
    setOrderQuantity(Math.max(medicine.reorderLevel * 2, medicine.reorderLevel + 10));
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setSelectedMedicine(null);
  };

  const handleConfirmOrder = () => {
    if (selectedMedicine) {
      const newQuantity = selectedMedicine.stockQuantity + orderQuantity;
      updateStockMutation.mutate({
        id: selectedMedicine.id,
        quantity: newQuantity,
      });
    }
  };

  const getStockStatus = (stock, reorderLevel) => {
    if (stock === 0) return { label: 'Rupture de stock', color: 'error', severity: 'critique' };
    if (stock < reorderLevel / 2) return { label: 'Stock très bas', color: 'error', severity: 'urgent' };
    return { label: 'Stock bas', color: 'warning', severity: 'normal' };
  };

  if (isLoading) return <LoadingSpinner />;

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Erreur lors du chargement des alertes de stock
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="500" color="#ed6c02">
          Alertes Stock Bas
        </Typography>
        <Box>
          <Button
            variant={viewMode === 'table' ? 'contained' : 'outlined'}
            onClick={() => setViewMode('table')}
            sx={{ mr: 1 }}
          >
            Tableau
          </Button>
          <Button
            variant={viewMode === 'cards' ? 'contained' : 'outlined'}
            onClick={() => setViewMode('cards')}
          >
            Cartes
          </Button>
        </Box>
      </Box>

      {lowStockList.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: 'center' }}>
          <Typography variant="h6" color="success.main" gutterBottom>
            ✅ Aucun stock bas détecté
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Tous les médicaments sont à des niveaux de stock normaux.
          </Typography>
        </Paper>
      ) : (
        <>
          {/* Vue Tableau */}
          {viewMode === 'table' && (
            <Paper sx={{ p: 2 }}>
              <TableContainer>
                <Table>
                  <TableHead sx={{ bgcolor: '#f5f5f5' }}>
                    <TableRow>
                      <TableCell><strong>Médicament</strong></TableCell>
                      <TableCell><strong>Stock actuel</strong></TableCell>
                      <TableCell><strong>Seuil</strong></TableCell>
                      <TableCell><strong>Statut</strong></TableCell>
                      <TableCell align="right"><strong>Actions</strong></TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {lowStockList.map((item) => {
                      const maxStock = item.maximumStock || 100;
                      const stockPercentage = Math.min((item.stockQuantity / maxStock) * 100, 100);
                      const status = getStockStatus(item.stockQuantity, item.reorderLevel || 10);
                      
                      return (
                        <TableRow key={item.id} hover>
                          <TableCell>
                            <Typography variant="body1" fontWeight="medium">
                              {item.name}
                            </Typography>
                            <Typography variant="caption" color="text.secondary">
                              {item.category} • {item.form} {item.strength}
                            </Typography>
                          </TableCell>
                          <TableCell>
                            <Box sx={{ display: 'flex', alignItems: 'center', minWidth: 150 }}>
                              <Box sx={{ flex: 1, mr: 1 }}>
                                <LinearProgress
                                  variant="determinate"
                                  value={stockPercentage}
                                  color={status.color}
                                  sx={{ height: 8, borderRadius: 4 }}
                                />
                              </Box>
                              <Typography variant="body2" fontWeight="bold">
                                {item.stockQuantity}
                              </Typography>
                            </Box>
                          </TableCell>
                          <TableCell>{item.reorderLevel || 10}</TableCell>
                          <TableCell>
                            <Chip
                              icon={<WarningIcon />}
                              label={status.label}
                              color={status.color}
                              size="small"
                            />
                          </TableCell>
                          <TableCell align="right">
                            <Button
                              variant="contained"
                              size="small"
                              color="warning"
                              startIcon={<ShoppingCartIcon />}
                              onClick={() => handleOrder(item)}
                            >
                              Commander
                            </Button>
                          </TableCell>
                        </TableRow>
                      );
                    })}
                  </TableBody>
                </Table>
              </TableContainer>
            </Paper>
          )}

          {/* Vue Cartes */}
          {viewMode === 'cards' && (
            <Grid container spacing={3}>
              {lowStockList.map((item) => {
                const maxStock = item.maximumStock || 100;
                const stockPercentage = Math.min((item.stockQuantity / maxStock) * 100, 100);
                const status = getStockStatus(item.stockQuantity, item.reorderLevel || 10);
                
                return (
                  <Grid item xs={12} sm={6} md={4} key={item.id}>
                    <Card sx={{ height: '100%', borderLeft: `4px solid ${status.color === 'error' ? '#f44336' : '#ed6c02'}` }}>
                      <CardContent>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                          <Typography variant="h6" component="div">
                            {item.name}
                          </Typography>
                          <Chip
                            icon={<WarningIcon />}
                            label={status.severity}
                            color={status.color}
                            size="small"
                          />
                        </Box>
                        
                        <Typography variant="body2" color="text.secondary" gutterBottom>
                          {item.category} • {item.form} {item.strength}
                        </Typography>

                        <Box sx={{ mt: 2, mb: 1 }}>
                          <Typography variant="body2" color="text.secondary" gutterBottom>
                            Niveau de stock
                          </Typography>
                          <Box sx={{ display: 'flex', alignItems: 'center' }}>
                            <Box sx={{ flex: 1, mr: 1 }}>
                              <LinearProgress
                                variant="determinate"
                                value={stockPercentage}
                                color={status.color}
                                sx={{ height: 10, borderRadius: 5 }}
                              />
                            </Box>
                            <Typography variant="body2" fontWeight="bold">
                              {item.stockQuantity}/{maxStock}
                            </Typography>
                          </Box>
                        </Box>

                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 2 }}>
                          <Typography variant="body2" color="text.secondary">
                            Seuil: {item.reorderLevel || 10}
                          </Typography>
                          <Button
                            variant="contained"
                            size="small"
                            color="warning"
                            startIcon={<ShoppingCartIcon />}
                            onClick={() => handleOrder(item)}
                          >
                            Commander
                          </Button>
                        </Box>
                      </CardContent>
                    </Card>
                  </Grid>
                );
              })}
            </Grid>
          )}
        </>
      )}

      {/* Dialogue de commande */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ bgcolor: '#f5f5f5' }}>
          <Typography variant="h6">
            Commander {selectedMedicine?.name}
          </Typography>
        </DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            <Typography variant="body2" paragraph>
              Stock actuel: <strong>{selectedMedicine?.stockQuantity}</strong> unités
            </Typography>
            <Typography variant="body2" paragraph>
              Seuil de réapprovisionnement: <strong>{selectedMedicine?.reorderLevel || 10}</strong> unités
            </Typography>
            
            <TextField
              fullWidth
              type="number"
              label="Quantité à commander"
              value={orderQuantity}
              onChange={(e) => setOrderQuantity(parseInt(e.target.value) || 0)}
              InputProps={{ inputProps: { min: 1 } }}
              sx={{ mt: 2 }}
            />
            
            <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
              Stock après commande: <strong>{(selectedMedicine?.stockQuantity || 0) + orderQuantity}</strong> unités
            </Typography>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Annuler</Button>
          <Button
            onClick={handleConfirmOrder}
            variant="contained"
            color="warning"
            startIcon={<ShoppingCartIcon />}
            disabled={updateStockMutation.isLoading}
          >
            {updateStockMutation.isLoading ? 'Commande...' : 'Confirmer la commande'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default LowStockAlert;