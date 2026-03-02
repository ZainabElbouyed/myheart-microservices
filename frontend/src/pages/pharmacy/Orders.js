// frontend/src/pages/pharmacy/PharmacyOrders.js
import React, { useState } from 'react';
import {
  Box,
  Typography,
  Paper,
  Grid,
  Card,
  CardContent,
  Chip,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
} from '@mui/material';
import {
  ShoppingCart as CartIcon,
  Add as AddIcon,
  History as HistoryIcon,
  Print as PrintIcon,
} from '@mui/icons-material';
import { useQuery, useMutation } from '@tanstack/react-query';
import { pharmacyService } from '../../services/pharmacyService';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const PharmacyOrders = () => {
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState(null);

  // Données simulées pour le développement
  const orders = [
    { id: 'CMD001', supplier: 'Labo Pharma', items: 5, total: 125.50, status: 'IN_PROGRESS', date: '2026-02-26' },
    { id: 'CMD002', supplier: 'MediSupply', items: 3, total: 78.20, status: 'COMPLETED', date: '2026-02-25' },
    { id: 'CMD003', supplier: 'PharmaDistrib', items: 8, total: 234.90, status: 'PENDING', date: '2026-02-24' },
    { id: 'CMD004', supplier: 'Labo Central', items: 12, total: 345.60, status: 'PENDING', date: '2026-02-23' },
    { id: 'CMD005', supplier: 'MedicAll', items: 2, total: 45.30, status: 'COMPLETED', date: '2026-02-22' },
  ];

  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED': return 'success';
      case 'IN_PROGRESS': return 'warning';
      case 'PENDING': return 'default';
      default: return 'default';
    }
  };

  const getStatusLabel = (status) => {
    switch (status) {
      case 'COMPLETED': return 'Livré';
      case 'IN_PROGRESS': return 'En cours';
      case 'PENDING': return 'En attente';
      default: return status;
    }
  };

  const handleViewDetails = (order) => {
    setSelectedOrder(order);
    setOpenDialog(true);
  };

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="500" color="#1976d2">
          Commandes
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => alert('Fonction de création de commande à implémenter')}
        >
          Nouvelle commande
        </Button>
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Historique des commandes
            </Typography>
            <TableContainer>
              <Table>
                <TableHead sx={{ bgcolor: '#f5f5f5' }}>
                  <TableRow>
                    <TableCell><strong>N° Commande</strong></TableCell>
                    <TableCell><strong>Fournisseur</strong></TableCell>
                    <TableCell><strong>Date</strong></TableCell>
                    <TableCell><strong>Articles</strong></TableCell>
                    <TableCell align="right"><strong>Total</strong></TableCell>
                    <TableCell><strong>Statut</strong></TableCell>
                    <TableCell align="right"><strong>Actions</strong></TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {orders.map((order) => (
                    <TableRow key={order.id} hover>
                      <TableCell>
                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                          <CartIcon sx={{ mr: 1, color: 'primary.main' }} fontSize="small" />
                          <Typography variant="body2" fontWeight="medium">
                            {order.id}
                          </Typography>
                        </Box>
                      </TableCell>
                      <TableCell>{order.supplier}</TableCell>
                      <TableCell>{new Date(order.date).toLocaleDateString('fr-FR')}</TableCell>
                      <TableCell>{order.items}</TableCell>
                      <TableCell align="right">{order.total.toFixed(2)}€</TableCell>
                      <TableCell>
                        <Chip
                          label={getStatusLabel(order.status)}
                          color={getStatusColor(order.status)}
                          size="small"
                        />
                      </TableCell>
                      <TableCell align="right">
                        <Button 
                          size="small" 
                          variant="outlined"
                          onClick={() => handleViewDetails(order)}
                        >
                          Détails
                        </Button>
                        <IconButton size="small" sx={{ ml: 1 }}>
                          <PrintIcon />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Paper>
        </Grid>
      </Grid>

      {/* Dialogue de détails */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ bgcolor: '#f5f5f5' }}>
          <Typography variant="h6">
            Détails de la commande {selectedOrder?.id}
          </Typography>
        </DialogTitle>
        <DialogContent>
          {selectedOrder && (
            <Box sx={{ pt: 2 }}>
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">Fournisseur</Typography>
                  <Typography variant="body1" gutterBottom>{selectedOrder.supplier}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">Date</Typography>
                  <Typography variant="body1" gutterBottom>
                    {new Date(selectedOrder.date).toLocaleDateString('fr-FR')}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">Nombre d'articles</Typography>
                  <Typography variant="body1" gutterBottom>{selectedOrder.items}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">Montant total</Typography>
                  <Typography variant="body1" gutterBottom>{selectedOrder.total.toFixed(2)}€</Typography>
                </Grid>
                <Grid item xs={12}>
                  <Typography variant="body2" color="text.secondary">Statut</Typography>
                  <Chip
                    label={getStatusLabel(selectedOrder.status)}
                    color={getStatusColor(selectedOrder.status)}
                    size="small"
                    sx={{ mt: 1 }}
                  />
                </Grid>
              </Grid>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Fermer</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default PharmacyOrders;