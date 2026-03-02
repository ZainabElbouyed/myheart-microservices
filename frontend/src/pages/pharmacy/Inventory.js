// frontend/src/pages/pharmacy/Inventory.js
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
  TablePagination,
  TextField,
  InputAdornment,
  Button,
  Chip,
  IconButton,
  LinearProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Grid,
  Alert,
} from '@mui/material';
import {
  Search as SearchIcon,
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Warning as WarningIcon,
} from '@mui/icons-material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { pharmacyService } from '../../services/pharmacyService';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const Inventory = () => {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedMedicine, setSelectedMedicine] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    genericName: '',
    category: '',
    form: '',
    strength: '',
    stockQuantity: 0,
    maximumStock: 100,
    reorderLevel: 10,
    unitPrice: 0,
    sellingPrice: 0,
    requiresPrescription: false,
  });

  const { 
    data: inventory = [], 
    isLoading,
    error,
    refetch 
  } = useQuery({
    queryKey: ['inventory'],
    queryFn: pharmacyService.getInventory,
  });

  useEffect(() => {
    console.log('📊 Inventaire reçu:', inventory);
  }, [inventory]);

  const inventoryList = Array.isArray(inventory) ? inventory : [];

  const createMutation = useMutation({
    mutationFn: pharmacyService.createMedicine,
    onSuccess: () => {
      queryClient.invalidateQueries(['inventory']);
      handleCloseDialog();
    },
  });

  const updateStockMutation = useMutation({
    mutationFn: ({ id, quantity }) => pharmacyService.updateStock(id, quantity),
    onSuccess: () => {
      queryClient.invalidateQueries(['inventory']);
    },
  });

  const handleOpenDialog = (medicine = null) => {
    if (medicine) {
      setSelectedMedicine(medicine);
      setFormData(medicine);
    } else {
      setSelectedMedicine(null);
      setFormData({
        name: '',
        genericName: '',
        category: '',
        form: '',
        strength: '',
        stockQuantity: 0,
        maximumStock: 100,
        reorderLevel: 10,
        unitPrice: 0,
        sellingPrice: 0,
        requiresPrescription: false,
      });
    }
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setSelectedMedicine(null);
  };

  const handleSubmit = () => {
    if (selectedMedicine) {
      // Update logic here
      alert('Fonction de modification à implémenter');
    } else {
      createMutation.mutate(formData);
    }
  };

  const handleUpdateStock = (id, currentStock, change) => {
    const newQuantity = currentStock + change;
    if (newQuantity >= 0) {
      if (window.confirm(`Voulez-vous ${change > 0 ? 'ajouter' : 'retirer'} ${Math.abs(change)} unité(s) ?`)) {
        updateStockMutation.mutate({ id, quantity: newQuantity });
      }
    }
  };

  const filteredInventory = inventoryList.filter(
    (item) =>
      (item.name?.toLowerCase() || '').includes(searchTerm.toLowerCase()) ||
      (item.genericName?.toLowerCase() || '').includes(searchTerm.toLowerCase()) ||
      (item.category?.toLowerCase() || '').includes(searchTerm.toLowerCase())
  );

  if (isLoading) return <LoadingSpinner />;

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Erreur lors du chargement de l'inventaire
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="500" color="#1976d2">
          Gestion des stocks
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => handleOpenDialog()}
        >
          Nouveau médicament
        </Button>
      </Box>

      <Paper sx={{ p: 2, mb: 3 }}>
        <TextField
          fullWidth
          placeholder="Rechercher un médicament par nom, catégorie..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
          }}
        />
      </Paper>

      {filteredInventory.length === 0 ? (
        <Alert severity="info">
          {searchTerm 
            ? 'Aucun médicament ne correspond à votre recherche'
            : 'Aucun médicament dans l\'inventaire'}
        </Alert>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead sx={{ bgcolor: '#f5f5f5' }}>
              <TableRow>
                <TableCell><strong>Médicament</strong></TableCell>
                <TableCell><strong>Catégorie</strong></TableCell>
                <TableCell><strong>Forme/Dosage</strong></TableCell>
                <TableCell><strong>Stock</strong></TableCell>
                <TableCell><strong>Seuil</strong></TableCell>
                <TableCell><strong>Prix</strong></TableCell>
                <TableCell><strong>Ordonnance</strong></TableCell>
                <TableCell align="right"><strong>Actions</strong></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredInventory
                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                .map((item) => {
                  const maxStock = item.maximumStock || 100;
                  const stockPercentage = Math.min((item.stockQuantity / maxStock) * 100, 100);
                  const isLowStock = item.stockQuantity <= (item.reorderLevel || 10);
                  
                  return (
                    <TableRow key={item.id} hover>
                      <TableCell>
                        <Typography variant="body1" fontWeight="medium">
                          {item.name}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {item.genericName}
                        </Typography>
                      </TableCell>
                      <TableCell>{item.category}</TableCell>
                      <TableCell>
                        {item.form} {item.strength}
                      </TableCell>
                      <TableCell>
                        <Box sx={{ display: 'flex', alignItems: 'center', minWidth: 150 }}>
                          <Box sx={{ flex: 1, mr: 1 }}>
                            <LinearProgress
                              variant="determinate"
                              value={stockPercentage}
                              color={isLowStock ? 'warning' : 'primary'}
                              sx={{ height: 8, borderRadius: 4 }}
                            />
                          </Box>
                          <Typography variant="body2" fontWeight="bold">
                            {item.stockQuantity}
                          </Typography>
                          {isLowStock && (
                            <WarningIcon color="warning" sx={{ ml: 1, fontSize: 16 }} />
                          )}
                        </Box>
                      </TableCell>
                      <TableCell>{item.reorderLevel || 10}</TableCell>
                      <TableCell>
                        <Typography variant="body2">
                          {item.sellingPrice?.toFixed(2)}€
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {item.unitPrice?.toFixed(2)}€ HT
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={item.requiresPrescription ? 'Oui' : 'Non'}
                          color={item.requiresPrescription ? 'primary' : 'default'}
                          size="small"
                        />
                      </TableCell>
                      <TableCell align="right">
                        <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 0.5 }}>
                          <Button 
                            size="small" 
                            variant="outlined"
                            onClick={() => handleUpdateStock(item.id, item.stockQuantity, -1)}
                            disabled={item.stockQuantity <= 0}
                          >
                            -
                          </Button>
                          <Button 
                            size="small" 
                            variant="outlined"
                            onClick={() => handleUpdateStock(item.id, item.stockQuantity, 1)}
                          >
                            +
                          </Button>
                          <IconButton size="small" onClick={() => handleOpenDialog(item)}>
                            <EditIcon />
                          </IconButton>
                        </Box>
                      </TableCell>
                    </TableRow>
                  );
                })}
            </TableBody>
          </Table>
          <TablePagination
            rowsPerPageOptions={[5, 10, 25]}
            component="div"
            count={filteredInventory.length}
            rowsPerPage={rowsPerPage}
            page={page}
            onPageChange={(e, p) => setPage(p)}
            onRowsPerPageChange={(e) => {
              setRowsPerPage(parseInt(e.target.value, 10));
              setPage(0);
            }}
            labelRowsPerPage="Lignes par page"
          />
        </TableContainer>
      )}

      {/* Dialogue nouveau médicament */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="sm" fullWidth>
        <DialogTitle>
          {selectedMedicine ? 'Modifier le médicament' : 'Nouveau médicament'}
        </DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Nom *"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Nom générique"
                  value={formData.genericName || ''}
                  onChange={(e) => setFormData({ ...formData, genericName: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Catégorie *"
                  value={formData.category}
                  onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                  required
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Forme *"
                  value={formData.form}
                  onChange={(e) => setFormData({ ...formData, form: e.target.value })}
                  required
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Dosage"
                  value={formData.strength || ''}
                  onChange={(e) => setFormData({ ...formData, strength: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  type="number"
                  label="Stock initial *"
                  value={formData.stockQuantity}
                  onChange={(e) => setFormData({ ...formData, stockQuantity: parseInt(e.target.value) || 0 })}
                  required
                  inputProps={{ min: 0 }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  type="number"
                  label="Stock maximum"
                  value={formData.maximumStock}
                  onChange={(e) => setFormData({ ...formData, maximumStock: parseInt(e.target.value) || 100 })}
                  inputProps={{ min: 1 }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  type="number"
                  label="Seuil d'alerte"
                  value={formData.reorderLevel}
                  onChange={(e) => setFormData({ ...formData, reorderLevel: parseInt(e.target.value) || 10 })}
                  inputProps={{ min: 0 }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  type="number"
                  label="Prix unitaire (HT) *"
                  value={formData.unitPrice}
                  onChange={(e) => setFormData({ ...formData, unitPrice: parseFloat(e.target.value) || 0 })}
                  required
                  inputProps={{ min: 0, step: 0.01 }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  type="number"
                  label="Prix de vente (TTC) *"
                  value={formData.sellingPrice}
                  onChange={(e) => setFormData({ ...formData, sellingPrice: parseFloat(e.target.value) || 0 })}
                  required
                  inputProps={{ min: 0, step: 0.01 }}
                />
              </Grid>
            </Grid>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Annuler</Button>
          <Button 
            onClick={handleSubmit} 
            variant="contained"
            disabled={createMutation.isLoading}
          >
            {createMutation.isLoading ? 'Création...' : (selectedMedicine ? 'Modifier' : 'Créer')}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Inventory;