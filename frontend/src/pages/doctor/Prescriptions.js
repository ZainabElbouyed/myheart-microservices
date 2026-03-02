// frontend/src/pages/doctor/DoctorPrescriptions.js
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
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Alert,
  Divider,
  IconButton,
  TextField,
  InputAdornment,
} from '@mui/material';
import {
  Medication as MedicationIcon,
  Search as SearchIcon,
  Add as AddIcon,
  Visibility as VisibilityIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { useAuth } from '../../contexts/AuthContext';
import { doctorService } from '../../services/doctorService';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const DoctorPrescriptions = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');

  console.log('👤 User médecin:', user);

  // ✅ Récupérer les prescriptions du médecin
  const { 
    data: prescriptions = [], 
    isLoading,
    error 
  } = useQuery({
    queryKey: ['doctorPrescriptions', user?.id],
    queryFn: () => doctorService.getPrescriptions(user?.id),
    enabled: !!user?.id,
  });

  useEffect(() => {
    console.log('📊 Prescriptions reçues:', prescriptions);
  }, [prescriptions]);

  // S'assurer que prescriptions est un tableau
  const prescriptionsList = Array.isArray(prescriptions) ? prescriptions : 
                            prescriptions?.data ? prescriptions.data :
                            prescriptions?.content ? prescriptions.content : [];

  const filteredPrescriptions = prescriptionsList.filter(
    (presc) =>
      (presc.patientName?.toLowerCase() || '').includes(searchTerm.toLowerCase()) ||
      (presc.diagnosis?.toLowerCase() || '').includes(searchTerm.toLowerCase()) ||
      (presc.prescriptionNumber?.toLowerCase() || '').includes(searchTerm.toLowerCase())
  );

  const getStatusColor = (status) => {
    switch (status?.toUpperCase()) {
      case 'ACTIVE': return 'success';
      case 'FILLED': return 'info';
      case 'EXPIRED': return 'error';
      case 'CANCELLED': return 'default';
      case 'DRAFT': return 'warning';
      default: return 'warning';
    }
  };

  const getStatusLabel = (status) => {
    switch (status?.toUpperCase()) {
      case 'ACTIVE': return 'Active';
      case 'FILLED': return 'Délivrée';
      case 'EXPIRED': return 'Expirée';
      case 'CANCELLED': return 'Annulée';
      case 'DRAFT': return 'Brouillon';
      default: return status || 'Inconnu';
    }
  };

  if (isLoading) return <LoadingSpinner />;

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Erreur lors du chargement des prescriptions: {error.message}
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="500" color="#2E7D32">
          Prescriptions
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate('/doctor/prescriptions/new')}
          sx={{ bgcolor: '#2E7D32' }}
        >
          Nouvelle prescription
        </Button>
      </Box>

      <Paper sx={{ p: 2, mb: 3 }}>
        <TextField
          fullWidth
          placeholder="Rechercher par patient, diagnostic ou numéro de prescription..."
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

      {filteredPrescriptions.length === 0 ? (
        <Alert severity="info">
          {searchTerm 
            ? 'Aucune prescription ne correspond à votre recherche'
            : 'Vous n\'avez pas encore créé de prescriptions'}
        </Alert>
      ) : (
        <Grid container spacing={3}>
          <Grid item xs={12}>
            <Paper sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Prescriptions ({filteredPrescriptions.length})
              </Typography>
              {filteredPrescriptions.map((presc) => (
                <Card key={presc.id} variant="outlined" sx={{ mb: 2 }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                      <Box>
                        <Typography variant="subtitle1" fontWeight="bold" color="primary">
                          {presc.patientName || 'Patient'}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          N° {presc.prescriptionNumber || 'N° inconnu'}
                        </Typography>
                      </Box>
                      <Chip
                        label={getStatusLabel(presc.status)}
                        color={getStatusColor(presc.status)}
                        size="small"
                      />
                    </Box>
                    
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      <strong>Diagnostic:</strong> {presc.diagnosis || 'Non spécifié'}
                    </Typography>
                    
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      <strong>Date:</strong> {presc.prescriptionDate 
                        ? new Date(presc.prescriptionDate).toLocaleDateString('fr-FR', {
                            day: 'numeric',
                            month: 'long',
                            year: 'numeric'
                          })
                        : 'Date inconnue'}
                    </Typography>
                    
                    <Divider sx={{ my: 1 }} />
                    
                    <Typography variant="subtitle2" gutterBottom>
                      Médicaments prescrits:
                    </Typography>
                    <List dense>
                      {presc.medications && presc.medications.length > 0 ? (
                        presc.medications.slice(0, 3).map((med, index) => (
                          <ListItem key={index}>
                            <ListItemIcon>
                              <MedicationIcon color="primary" fontSize="small" />
                            </ListItemIcon>
                            <ListItemText 
                              primary={`${med.name || 'Médicament'} ${med.strength ? `- ${med.strength}` : ''}`}
                              secondary={`${med.dosage || ''} - ${med.frequency || ''} - ${med.duration || ''}`}
                            />
                          </ListItem>
                        ))
                      ) : (
                        <Typography variant="body2" color="text.secondary">
                          Aucun médicament détaillé
                        </Typography>
                      )}
                      {presc.medications?.length > 3 && (
                        <Typography variant="caption" color="text.secondary" sx={{ ml: 2 }}>
                          + {presc.medications.length - 3} autre(s) médicament(s)
                        </Typography>
                      )}
                    </List>
                    
                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2 }}>
                      <IconButton
                        color="primary"
                        onClick={() => navigate(`/doctor/prescriptions/${presc.id}`)}
                      >
                        <VisibilityIcon />
                      </IconButton>
                    </Box>
                  </CardContent>
                </Card>
              ))}
            </Paper>
          </Grid>
        </Grid>
      )}
    </Box>
  );
};

export default DoctorPrescriptions;