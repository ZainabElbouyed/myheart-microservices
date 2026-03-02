// frontend/src/pages/pharmacy/PrescriptionFulfillment.js
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
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  IconButton,
  Alert,
  Stepper,
  Step,
  StepLabel,
  Card,
  CardContent,
  Grid,
} from '@mui/material';
import {
  CheckCircle as CheckCircleIcon,
  Visibility as VisibilityIcon,
  LocalPharmacy as PharmacyIcon,
  Refresh as RefreshIcon,
} from '@mui/icons-material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { pharmacyService } from '../../services/pharmacyService';
import { useAuth } from '../../contexts/AuthContext';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const PrescriptionFulfillment = () => {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [selectedPrescription, setSelectedPrescription] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [pharmacistNote, setPharmacistNote] = useState('');
  const [activeStep, setActiveStep] = useState(0);

  // ✅ Récupérer les prescriptions en attente
  const { 
    data: pendingPrescriptions = [], 
    isLoading,
    error,
    refetch 
  } = useQuery({
    queryKey: ['pendingPrescriptions'],
    queryFn: pharmacyService.getPendingPrescriptions,
  });

  useEffect(() => {
    console.log('📊 Prescriptions en attente:', pendingPrescriptions);
  }, [pendingPrescriptions]);

  const pendingList = Array.isArray(pendingPrescriptions) ? pendingPrescriptions : [];

  const fulfillMutation = useMutation({
    mutationFn: ({ id, pharmacyId, pharmacist }) =>
      pharmacyService.fulfillPrescription(id, pharmacyId, pharmacist),
    onSuccess: () => {
      queryClient.invalidateQueries(['pendingPrescriptions']);
      handleCloseDialog();
    },
  });

  const handleOpenDialog = (prescription) => {
    setSelectedPrescription(prescription);
    setOpenDialog(true);
    setActiveStep(0);
    setPharmacistNote('');
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setSelectedPrescription(null);
  };

  const handleFulfill = () => {
    if (selectedPrescription) {
      fulfillMutation.mutate({
        id: selectedPrescription.id,
        pharmacyId: user?.pharmacyId || 'PHARM001',
        pharmacist: `${user?.firstName || 'Pharmacien'} ${user?.lastName || ''}`.trim(),
      });
    }
  };

  const steps = ['Vérification', 'Préparation', 'Délivrance'];

  if (isLoading) return <LoadingSpinner />;

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert 
          severity="error"
          action={
            <Button color="inherit" size="small" onClick={() => refetch()}>
              <RefreshIcon /> Réessayer
            </Button>
          }
        >
          Erreur lors du chargement des prescriptions
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="500" color="#1976d2">
          Gestion des prescriptions
        </Typography>
        <Button 
          variant="outlined" 
          startIcon={<RefreshIcon />}
          onClick={() => refetch()}
        >
          Rafraîchir
        </Button>
      </Box>

      {pendingList.length === 0 ? (
        <Alert severity="success" sx={{ mt: 2 }}>
          Aucune prescription en attente de traitement
        </Alert>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead sx={{ bgcolor: '#f5f5f5' }}>
              <TableRow>
                <TableCell><strong>N° Prescription</strong></TableCell>
                <TableCell><strong>Patient</strong></TableCell>
                <TableCell><strong>Médecin</strong></TableCell>
                <TableCell><strong>Date</strong></TableCell>
                <TableCell><strong>Médicaments</strong></TableCell>
                <TableCell><strong>Statut</strong></TableCell>
                <TableCell align="right"><strong>Actions</strong></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {pendingList.map((presc) => (
                <TableRow key={presc.id} hover>
                  <TableCell>
                    <Typography fontWeight="medium">
                      {presc.prescriptionNumber || presc.id.substring(0, 8)}
                    </Typography>
                  </TableCell>
                  <TableCell>{presc.patientName || 'Patient'}</TableCell>
                  <TableCell>Dr. {presc.doctorName || 'Médecin'}</TableCell>
                  <TableCell>
                    {presc.prescriptionDate 
                      ? new Date(presc.prescriptionDate).toLocaleDateString('fr-FR')
                      : 'Date inconnue'}
                  </TableCell>
                  <TableCell>
                    {presc.medications?.map((med, idx) => (
                      <Chip
                        key={idx}
                        label={med.name || 'Médicament'}
                        size="small"
                        sx={{ mr: 0.5, mb: 0.5 }}
                      />
                    ))}
                    {!presc.medications?.length && 'Aucun médicament'}
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={presc.status || 'PENDING'}
                      color="warning"
                      size="small"
                    />
                  </TableCell>
                  <TableCell align="right">
                    <IconButton
                      color="primary"
                      onClick={() => handleOpenDialog(presc)}
                    >
                      <VisibilityIcon />
                    </IconButton>
                    <IconButton
                      color="success"
                      onClick={() => handleOpenDialog(presc)}
                    >
                      <CheckCircleIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Dialogue de délivrance */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="md" fullWidth>
        <DialogTitle sx={{ bgcolor: '#f5f5f5' }}>
          <Typography variant="h6">
            Délivrer la prescription {selectedPrescription?.prescriptionNumber || selectedPrescription?.id}
          </Typography>
        </DialogTitle>
        <DialogContent dividers>
          {selectedPrescription && (
            <Box>
              <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
                {steps.map((label) => (
                  <Step key={label}>
                    <StepLabel>{label}</StepLabel>
                  </Step>
                ))}
              </Stepper>

              {activeStep === 0 && (
                <Box>
                  <Typography variant="h6" gutterBottom color="primary">
                    Vérification de la prescription
                  </Typography>
                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <Card variant="outlined" sx={{ p: 2, bgcolor: '#fafafa' }}>
                        <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                          Patient: {selectedPrescription.patientName || 'Patient'}
                        </Typography>
                        <Typography variant="body2" gutterBottom>
                          Médecin prescripteur: Dr. {selectedPrescription.doctorName || 'Médecin'}
                        </Typography>
                        <Typography variant="body2" gutterBottom>
                          Date: {selectedPrescription.prescriptionDate 
                            ? new Date(selectedPrescription.prescriptionDate).toLocaleDateString('fr-FR')
                            : 'Date inconnue'}
                        </Typography>
                        {selectedPrescription.diagnosis && (
                          <Typography variant="body2" gutterBottom>
                            Diagnostic: {selectedPrescription.diagnosis}
                          </Typography>
                        )}
                      </Card>
                    </Grid>
                    
                    <Grid item xs={12}>
                      <Typography variant="subtitle2" gutterBottom fontWeight="bold">
                        Médicaments prescrits:
                      </Typography>
                      {selectedPrescription.medications?.map((med, idx) => (
                        <Card key={idx} variant="outlined" sx={{ mb: 1, p: 1 }}>
                          <Typography variant="subtitle2">
                            {med.name} {med.strength ? `- ${med.strength}` : ''}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            Posologie: {med.dosage || ''} - {med.frequency || ''} - {med.duration || ''}
                          </Typography>
                          {med.instructions && (
                            <Typography variant="body2" color="primary" sx={{ mt: 0.5 }}>
                              Note: {med.instructions}
                            </Typography>
                          )}
                        </Card>
                      ))}
                    </Grid>
                  </Grid>
                </Box>
              )}

              {activeStep === 1 && (
                <Box>
                  <Typography variant="h6" gutterBottom color="primary">
                    Préparation des médicaments
                  </Typography>
                  <Alert severity="info" sx={{ mb: 2 }}>
                    Vérifiez la disponibilité des médicaments en stock
                  </Alert>
                  <TextField
                    fullWidth
                    multiline
                    rows={3}
                    label="Notes de préparation"
                    value={pharmacistNote}
                    onChange={(e) => setPharmacistNote(e.target.value)}
                    placeholder="Ajoutez des notes sur la préparation (lot, date de péremption, etc.)"
                  />
                </Box>
              )}

              {activeStep === 2 && (
                <Box>
                  <Typography variant="h6" gutterBottom color="primary">
                    Délivrance au patient
                  </Typography>
                  <Alert severity="success" sx={{ mb: 2 }}>
                    Confirmez la délivrance des médicaments au patient
                  </Alert>
                  <TextField
                    fullWidth
                    multiline
                    rows={2}
                    label="Instructions au patient"
                    value={pharmacistNote}
                    onChange={(e) => setPharmacistNote(e.target.value)}
                    placeholder="Ajoutez des instructions pour le patient (posologie, précautions...)"
                  />
                </Box>
              )}
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Annuler</Button>
          {activeStep > 0 && (
            <Button onClick={() => setActiveStep(activeStep - 1)}>
              Précédent
            </Button>
          )}
          {activeStep < steps.length - 1 ? (
            <Button
              variant="contained"
              onClick={() => setActiveStep(activeStep + 1)}
            >
              Suivant
            </Button>
          ) : (
            <Button
              variant="contained"
              color="success"
              onClick={handleFulfill}
              startIcon={<CheckCircleIcon />}
              disabled={fulfillMutation.isLoading}
            >
              {fulfillMutation.isLoading ? 'Traitement...' : 'Confirmer la délivrance'}
            </Button>
          )}
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default PrescriptionFulfillment;