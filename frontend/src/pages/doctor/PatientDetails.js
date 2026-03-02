// frontend/src/pages/doctor/PatientDetails.js
import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Paper,
  Grid,
  Card,
  CardContent,
  Avatar,
  Chip,
  Tabs,
  Tab,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Button,
  Divider,
  Alert,
} from '@mui/material';
import {
  Person as PersonIcon,
  Email as EmailIcon,
  Phone as PhoneIcon,
  Bloodtype as BloodtypeIcon,
  Science as ScienceIcon,
  Medication as MedicationIcon,
  ArrowBack as ArrowBackIcon,
  CalendarToday as CalendarIcon,
} from '@mui/icons-material';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { doctorService } from '../../services/doctorService';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const PatientDetails = () => {
  const { patientId } = useParams();
  const navigate = useNavigate();
  const [tabValue, setTabValue] = useState(0);

  console.log('🔍 Patient ID:', patientId);

  // Récupérer le dossier complet du patient
  const { 
    data: record, 
    isLoading,
    error 
  } = useQuery({
    queryKey: ['patientFullRecord', patientId],
    queryFn: () => doctorService.getFullPatientRecord(patientId),
    enabled: !!patientId,
  });

  useEffect(() => {
    console.log('📊 Dossier patient reçu:', record);
  }, [record]);

  if (isLoading) return <LoadingSpinner />;

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Erreur lors du chargement du dossier: {error.message}
        </Alert>
        <Button 
          startIcon={<ArrowBackIcon />} 
          onClick={() => navigate('/doctor/patients')}
          sx={{ mt: 2 }}
        >
          Retour à la liste des patients
        </Button>
      </Box>
    );
  }

  if (!record || !record.patient) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="warning">
          Patient non trouvé
        </Alert>
        <Button 
          startIcon={<ArrowBackIcon />} 
          onClick={() => navigate('/doctor/patients')}
          sx={{ mt: 2 }}
        >
          Retour à la liste des patients
        </Button>
      </Box>
    );
  }

  const patient = record.patient || {};
  const labResults = Array.isArray(record.labResults) ? record.labResults : [];
  const prescriptions = Array.isArray(record.prescriptions) ? record.prescriptions : [];

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/doctor/patients')}
          sx={{ mr: 2 }}
        >
          Retour
        </Button>
        <Typography variant="h4" fontWeight="500" color="#2E7D32">
          Dossier médical
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {/* Informations patient */}
        <Grid item xs={12} md={4}>
          <Card elevation={3}>
            <CardContent>
              <Box sx={{ textAlign: 'center', mb: 3 }}>
                <Avatar
                  sx={{
                    width: 100,
                    height: 100,
                    mx: 'auto',
                    mb: 2,
                    bgcolor: '#2E7D32',
                    fontSize: '2.5rem',
                  }}
                >
                  {patient.firstName?.charAt(0) || 'P'}
                </Avatar>
                <Typography variant="h5" gutterBottom fontWeight="bold">
                  {patient.firstName || ''} {patient.lastName || ''}
                </Typography>
                <Chip
                  label={`ID: ${patient.id?.substring(0, 8)}...`}
                  size="small"
                  variant="outlined"
                />
              </Box>

              <Divider sx={{ my: 2 }} />

              <List>
                <ListItem>
                  <ListItemIcon>
                    <EmailIcon color="primary" />
                  </ListItemIcon>
                  <ListItemText 
                    primary="Email" 
                    secondary={patient.email || 'Non renseigné'} 
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <PhoneIcon color="primary" />
                  </ListItemIcon>
                  <ListItemText 
                    primary="Téléphone" 
                    secondary={patient.phoneNumber || 'Non renseigné'} 
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <BloodtypeIcon color="primary" />
                  </ListItemIcon>
                  <ListItemText 
                    primary="Groupe sanguin" 
                    secondary={patient.bloodType || 'Non renseigné'} 
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <CalendarIcon color="primary" />
                  </ListItemIcon>
                  <ListItemText 
                    primary="Date de naissance" 
                    secondary={patient.dateOfBirth 
                      ? new Date(patient.dateOfBirth).toLocaleDateString('fr-FR')
                      : 'Non renseignée'} 
                  />
                </ListItem>
              </List>

              <Button
                fullWidth
                variant="contained"
                sx={{ mt: 2, bgcolor: '#2E7D32' }}
                onClick={() => navigate(`/doctor/prescriptions/new?patientId=${patientId}`)}
              >
                Nouvelle prescription
              </Button>
            </CardContent>
          </Card>
        </Grid>

        {/* Onglets avec résultats et prescriptions */}
        <Grid item xs={12} md={8}>
          <Paper sx={{ width: '100%' }}>
            <Tabs 
              value={tabValue} 
              onChange={(e, v) => setTabValue(v)}
              sx={{ borderBottom: 1, borderColor: 'divider' }}
            >
              <Tab 
                label={`Laboratoire (${labResults.length})`} 
                icon={<ScienceIcon />} 
                iconPosition="start"
              />
              <Tab 
                label={`Prescriptions (${prescriptions.length})`} 
                icon={<MedicationIcon />} 
                iconPosition="start"
              />
            </Tabs>

            <Box sx={{ p: 3 }}>
              {/* Onglet Laboratoire */}
              {tabValue === 0 && (
                <Box>
                  {labResults.length === 0 ? (
                    <Alert severity="info">
                      Aucun résultat de laboratoire
                    </Alert>
                  ) : (
                    labResults.map((result) => (
                      <Card key={result.id} variant="outlined" sx={{ mb: 2 }}>
                        <CardContent>
                          <Grid container spacing={2} alignItems="center">
                            <Grid item xs={12} sm={6}>
                              <Typography variant="subtitle1" fontWeight="bold">
                                {result.testType || 'Analyse'}
                              </Typography>
                              <Typography variant="body2" color="text.secondary">
                                {result.testDate 
                                  ? new Date(result.testDate).toLocaleDateString('fr-FR', {
                                      day: 'numeric',
                                      month: 'long',
                                      year: 'numeric'
                                    })
                                  : 'Date inconnue'}
                              </Typography>
                            </Grid>
                            <Grid item xs={6} sm={3}>
                              <Chip
                                label={result.status || 'PENDING'}
                                color={
                                  result.status === 'COMPLETED' ? 'success' :
                                  result.status === 'ABNORMAL' ? 'error' : 
                                  result.status === 'REVIEWED' ? 'info' : 'warning'
                                }
                                size="small"
                              />
                            </Grid>
                            <Grid item xs={6} sm={3}>
                              <Button 
                                variant="outlined" 
                                size="small"
                                onClick={() => navigate(`/doctor/lab-results/${result.id}`)}
                              >
                                Voir détails
                              </Button>
                            </Grid>
                          </Grid>
                        </CardContent>
                      </Card>
                    ))
                  )}
                </Box>
              )}

              {/* Onglet Prescriptions */}
              {tabValue === 1 && (
                <Box>
                  {prescriptions.length === 0 ? (
                    <Alert severity="info">
                      Aucune prescription
                    </Alert>
                  ) : (
                    prescriptions.map((presc) => (
                      <Card key={presc.id} variant="outlined" sx={{ mb: 2 }}>
                        <CardContent>
                          <Grid container spacing={2}>
                            <Grid item xs={12}>
                              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                <Typography variant="subtitle1" fontWeight="bold">
                                  Prescription du {presc.prescriptionDate 
                                    ? new Date(presc.prescriptionDate).toLocaleDateString('fr-FR')
                                    : 'Date inconnue'}
                                </Typography>
                                <Chip
                                  label={presc.status || 'ACTIVE'}
                                  color={presc.status === 'FILLED' ? 'success' : 
                                         presc.status === 'ACTIVE' ? 'primary' : 'default'}
                                  size="small"
                                />
                              </Box>
                              <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                                {presc.diagnosis || 'Diagnostic non spécifié'}
                              </Typography>
                            </Grid>
                            <Grid item xs={12}>
                              <Divider sx={{ my: 1 }} />
                              <Typography variant="subtitle2" gutterBottom fontWeight="bold">
                                Médicaments prescrits:
                              </Typography>
                              <List dense>
                                {presc.medications && presc.medications.length > 0 ? (
                                  presc.medications.map((med, index) => (
                                    <ListItem key={index}>
                                      <ListItemIcon>
                                        <MedicationIcon color="primary" fontSize="small" />
                                      </ListItemIcon>
                                      <ListItemText
                                        primary={`${med.name || 'Médicament'} - ${med.dosage || ''}`}
                                        secondary={`${med.frequency || ''} - ${med.duration || ''}`}
                                      />
                                    </ListItem>
                                  ))
                                ) : (
                                  <Typography variant="body2" color="text.secondary">
                                    Aucun médicament détaillé
                                  </Typography>
                                )}
                              </List>
                            </Grid>
                          </Grid>
                        </CardContent>
                      </Card>
                    ))
                  )}
                </Box>
              )}
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default PatientDetails;