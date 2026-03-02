// frontend/src/pages/doctor/CreatePrescription.js
import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Paper,
  Grid,
  TextField,
  Button,
  IconButton,
  Card,
  CardContent,
  FormControlLabel,
  Checkbox,
  MenuItem,
  Alert,
  CircularProgress,
} from '@mui/material';
import {
  Add as AddIcon,
  Delete as DeleteIcon,
  Save as SaveIcon,
  ArrowBack as ArrowBackIcon,
} from '@mui/icons-material';
import { useNavigate, useLocation } from 'react-router-dom';
import { useMutation, useQuery } from '@tanstack/react-query';
import { doctorService } from '../../services/doctorService';
import { useAuth } from '../../contexts/AuthContext';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const CreatePrescription = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useAuth();
  const queryParams = new URLSearchParams(location.search);
  const patientId = queryParams.get('patientId');

  const [formData, setFormData] = useState({
    patientId: patientId || '',
    diagnosis: '',
    clinicalNotes: '',
    medications: [
      {
        name: '',
        strength: '',
        form: 'Comprimé',
        dosage: '',
        frequency: '',
        duration: '',
        quantity: 30,
        instructions: '',
        substituteAllowed: true,
      },
    ],
    status: 'ACTIVE',
    refillsAllowed: 0,
    isEmergency: false,
  });

  const [selectedPatient, setSelectedPatient] = useState(null);

  // ✅ Récupérer les patients du médecin
  const { 
    data: patients = [], 
    isLoading: patientsLoading,
    error: patientsError 
  } = useQuery({
    queryKey: ['doctorPatients', user?.id],
    queryFn: () => doctorService.getPatients(user?.id),
    enabled: !!user?.id,
  });

  useEffect(() => {
    console.log('📊 Patients disponibles:', patients);
  }, [patients]);

  useEffect(() => {
    if (patientId && patients.length > 0) {
      const patient = patients.find(p => p.id === patientId);
      setSelectedPatient(patient);
    }
  }, [patientId, patients]);

  const patientsList = Array.isArray(patients) ? patients : 
                      patients?.data ? patients.data :
                      patients?.content ? patients.content : [];

  const createMutation = useMutation({
    mutationFn: (data) => doctorService.createPrescription(data),
    onSuccess: (data) => {
      console.log('✅ Prescription créée:', data);
      navigate('/doctor/prescriptions');
    },
    onError: (error) => {
      console.error('❌ Erreur création prescription:', error);
    },
  });

  const handleAddMedication = () => {
    setFormData({
      ...formData,
      medications: [
        ...formData.medications,
        {
          name: '',
          strength: '',
          form: 'Comprimé',
          dosage: '',
          frequency: '',
          duration: '',
          quantity: 30,
          instructions: '',
          substituteAllowed: true,
        },
      ],
    });
  };

  const handleRemoveMedication = (index) => {
    const newMedications = formData.medications.filter((_, i) => i !== index);
    setFormData({ ...formData, medications: newMedications });
  };

  const handleMedicationChange = (index, field, value) => {
    const newMedications = [...formData.medications];
    newMedications[index][field] = value;
    setFormData({ ...formData, medications: newMedications });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    // Validation
    if (!formData.patientId) {
      alert('Veuillez sélectionner un patient');
      return;
    }
    if (!formData.diagnosis) {
      alert('Veuillez saisir un diagnostic');
      return;
    }
    
    // Trouver le patient sélectionné
    const selectedPatientObj = patientsList.find(p => p.id === formData.patientId);
    
    // Préparer les données pour l'API
    const prescriptionData = {
      patientId: formData.patientId,
      patientName: selectedPatientObj ? 
        `${selectedPatientObj.firstName || ''} ${selectedPatientObj.lastName || ''}`.trim() : '',
      doctorId: user?.id,
      doctorName: user ? `Dr. ${user.firstName || ''} ${user.lastName || ''}`.trim() : '',
      diagnosis: formData.diagnosis,
      clinicalNotes: formData.clinicalNotes,
      medications: formData.medications.map(med => ({
        ...med,
        quantity: parseInt(med.quantity) || 1,
      })),
      status: formData.status,
      refillsAllowed: parseInt(formData.refillsAllowed) || 0,
      isEmergency: formData.isEmergency,
      prescriptionDate: new Date().toISOString(),
      expiryDate: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString(), // +1 an
    };

    console.log('📤 Envoi prescription:', prescriptionData);
    createMutation.mutate(prescriptionData);
  };

  const medicationForms = [
    'Comprimé',
    'Gélule',
    'Sirop',
    'Injection',
    'Crème',
    'Inhalateur',
    'Suppositoire',
    'Solution buvable',
    'Collyre',
  ];

  if (patientsLoading) return <LoadingSpinner />;

  if (patientsError) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Erreur lors du chargement des patients: {patientsError.message}
        </Alert>
        <Button 
          startIcon={<ArrowBackIcon />} 
          onClick={() => navigate('/doctor/prescriptions')}
          sx={{ mt: 2 }}
        >
          Retour
        </Button>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/doctor/prescriptions')}
          sx={{ mr: 2 }}
        >
          Retour
        </Button>
        <Typography variant="h4" fontWeight="500" color="#2E7D32">
          Nouvelle Prescription
        </Typography>
      </Box>

      {selectedPatient && (
        <Alert severity="info" sx={{ mb: 3 }}>
          Patient sélectionné: {selectedPatient.firstName} {selectedPatient.lastName}
        </Alert>
      )}

      <Paper sx={{ p: 3 }}>
        <form onSubmit={handleSubmit}>
          <Grid container spacing={3}>
            {/* Sélection du patient */}
            <Grid item xs={12}>
              <TextField
                select
                fullWidth
                label="Patient *"
                value={formData.patientId}
                onChange={(e) => setFormData({ ...formData, patientId: e.target.value })}
                required
                disabled={!!patientId}
              >
                {patientsList.length === 0 ? (
                  <MenuItem value="" disabled>Aucun patient disponible</MenuItem>
                ) : (
                  patientsList.map((patient) => (
                    <MenuItem key={patient.id} value={patient.id}>
                      {patient.firstName || ''} {patient.lastName || ''} - {patient.email || ''}
                    </MenuItem>
                  ))
                )}
              </TextField>
            </Grid>

            {/* Diagnostic */}
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Diagnostic *"
                value={formData.diagnosis}
                onChange={(e) => setFormData({ ...formData, diagnosis: e.target.value })}
                required
                multiline
                rows={2}
                placeholder="Diagnostic principal"
              />
            </Grid>

            {/* Options */}
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                type="number"
                label="Renouvellements autorisés"
                value={formData.refillsAllowed}
                onChange={(e) => setFormData({ ...formData, refillsAllowed: e.target.value })}
                inputProps={{ min: 0 }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControlLabel
                control={
                  <Checkbox
                    checked={formData.isEmergency}
                    onChange={(e) => setFormData({ ...formData, isEmergency: e.target.checked })}
                    color="error"
                  />
                }
                label="Prescription d'urgence"
              />
            </Grid>

            {/* Médicaments */}
            <Grid item xs={12}>
              <Typography variant="h6" gutterBottom sx={{ mt: 2, color: '#2E7D32' }}>
                Médicaments prescrits
              </Typography>
            </Grid>

            {formData.medications.map((med, index) => (
              <Grid item xs={12} key={index}>
                <Card variant="outlined" sx={{ bgcolor: '#f9f9f9' }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                      <Typography variant="subtitle1" fontWeight="bold" color="primary">
                        Médicament #{index + 1}
                      </Typography>
                      {formData.medications.length > 1 && (
                        <IconButton
                          color="error"
                          onClick={() => handleRemoveMedication(index)}
                          size="small"
                        >
                          <DeleteIcon />
                        </IconButton>
                      )}
                    </Box>

                    <Grid container spacing={2}>
                      <Grid item xs={12} sm={6}>
                        <TextField
                          fullWidth
                          label="Nom du médicament *"
                          value={med.name}
                          onChange={(e) => handleMedicationChange(index, 'name', e.target.value)}
                          required
                        />
                      </Grid>
                      <Grid item xs={12} sm={3}>
                        <TextField
                          fullWidth
                          label="Dosage *"
                          value={med.strength}
                          onChange={(e) => handleMedicationChange(index, 'strength', e.target.value)}
                          placeholder="ex: 500mg"
                          required
                        />
                      </Grid>
                      <Grid item xs={12} sm={3}>
                        <TextField
                          select
                          fullWidth
                          label="Forme"
                          value={med.form}
                          onChange={(e) => handleMedicationChange(index, 'form', e.target.value)}
                        >
                          {medicationForms.map((form) => (
                            <MenuItem key={form} value={form}>
                              {form}
                            </MenuItem>
                          ))}
                        </TextField>
                      </Grid>

                      <Grid item xs={12} sm={4}>
                        <TextField
                          fullWidth
                          label="Posologie *"
                          value={med.dosage}
                          onChange={(e) => handleMedicationChange(index, 'dosage', e.target.value)}
                          placeholder="ex: 1 comprimé"
                          required
                        />
                      </Grid>
                      <Grid item xs={12} sm={4}>
                        <TextField
                          fullWidth
                          label="Fréquence *"
                          value={med.frequency}
                          onChange={(e) => handleMedicationChange(index, 'frequency', e.target.value)}
                          placeholder="ex: 3 fois par jour"
                          required
                        />
                      </Grid>
                      <Grid item xs={12} sm={4}>
                        <TextField
                          fullWidth
                          label="Durée *"
                          value={med.duration}
                          onChange={(e) => handleMedicationChange(index, 'duration', e.target.value)}
                          placeholder="ex: 7 jours"
                          required
                        />
                      </Grid>

                      <Grid item xs={12} sm={4}>
                        <TextField
                          fullWidth
                          type="number"
                          label="Quantité totale *"
                          value={med.quantity}
                          onChange={(e) => handleMedicationChange(index, 'quantity', e.target.value)}
                          inputProps={{ min: 1 }}
                          required
                        />
                      </Grid>
                      <Grid item xs={12} sm={8}>
                        <FormControlLabel
                          control={
                            <Checkbox
                              checked={med.substituteAllowed}
                              onChange={(e) => handleMedicationChange(index, 'substituteAllowed', e.target.checked)}
                            />
                          }
                          label="Substitution par générique autorisée"
                        />
                      </Grid>

                      <Grid item xs={12}>
                        <TextField
                          fullWidth
                          multiline
                          rows={2}
                          label="Instructions spéciales"
                          value={med.instructions}
                          onChange={(e) => handleMedicationChange(index, 'instructions', e.target.value)}
                          placeholder="Instructions particulières pour ce médicament"
                        />
                      </Grid>
                    </Grid>
                  </CardContent>
                </Card>
              </Grid>
            ))}

            <Grid item xs={12}>
              <Button
                startIcon={<AddIcon />}
                onClick={handleAddMedication}
                variant="outlined"
                color="primary"
              >
                Ajouter un médicament
              </Button>
            </Grid>

            {/* Notes cliniques */}
            <Grid item xs={12}>
              <TextField
                fullWidth
                multiline
                rows={3}
                label="Notes cliniques"
                value={formData.clinicalNotes}
                onChange={(e) => setFormData({ ...formData, clinicalNotes: e.target.value })}
                placeholder="Informations complémentaires pour le pharmacien ou le patient"
              />
            </Grid>

            {/* Boutons d'action */}
            <Grid item xs={12}>
              <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
                <Button 
                  onClick={() => navigate('/doctor/prescriptions')}
                  variant="outlined"
                >
                  Annuler
                </Button>
                <Button
                  type="submit"
                  variant="contained"
                  startIcon={createMutation.isLoading ? <CircularProgress size={20} /> : <SaveIcon />}
                  disabled={createMutation.isLoading}
                  sx={{ bgcolor: '#2E7D32' }}
                >
                  {createMutation.isLoading ? 'Création...' : 'Créer la prescription'}
                </Button>
              </Box>
            </Grid>
          </Grid>
        </form>
      </Paper>
    </Box>
  );
};

export default CreatePrescription;