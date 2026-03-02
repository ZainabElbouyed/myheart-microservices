// frontend/src/pages/patient/Appointments.js
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
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  FormControl,
  InputLabel,
  Select,
  Alert,
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { useAuth } from '../../contexts/AuthContext';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { patientService } from '../../services/patientService';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { useLocation, useNavigate } from 'react-router-dom';

const Appointments = () => {
  const { user } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedDoctor, setSelectedDoctor] = useState(location.state?.selectedDoctor || null);
  const [formData, setFormData] = useState({
    doctorId: selectedDoctor?.id || '',
    date: new Date(),
    time: '',
    reason: '',
    type: 'CONSULTATION',
    isVirtual: false,
    location: 'Cabinet médical'
  });

  // ✅ Récupérer tous les médecins
  const { data: doctors, isLoading: doctorsLoading, error: doctorsError } = useQuery({
    queryKey: ['doctors'],
    queryFn: patientService.getDoctors,
  });

  // ✅ Rendez-vous du patient
  const { data: appointments, isLoading: appointmentsLoading } = useQuery({
    queryKey: ['patientAppointments', user?.id],
    queryFn: () => patientService.getAppointments(user?.id),
    enabled: !!user?.id,
  });

  // ✅ Vérification que ce sont des tableaux
  const doctorsList = Array.isArray(doctors) ? doctors : [];
  const appointmentsList = Array.isArray(appointments) ? appointments : [];

  // ✅ Mutation pour créer un rendez-vous
  const createMutation = useMutation({
    mutationFn: (appointmentData) => patientService.createAppointment(appointmentData),
    onSuccess: () => {
      // Rafraîchir la liste des rendez-vous
      queryClient.invalidateQueries(['patientAppointments', user?.id]);
      handleCloseDialog();
      // Afficher un message de succès
      alert('Rendez-vous créé avec succès !');
    },
    onError: (error) => {
      console.error('❌ Erreur création:', error);
      alert(error.message || 'Erreur lors de la création du rendez-vous');
    }
  });

  const handleOpenDialog = () => {
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setFormData({
      doctorId: selectedDoctor?.id || '',
      date: new Date(),
      time: '',
      reason: '',
      type: 'CONSULTATION',
      isVirtual: false,
      location: 'Cabinet médical'
    });
  };

  const handleSubmit = () => {
    try {
      // Validation
      if (!formData.doctorId) {
        alert('Veuillez sélectionner un médecin');
        return;
      }
      if (!formData.time) {
        alert('Veuillez sélectionner une heure');
        return;
      }
      if (!formData.reason) {
        alert('Veuillez indiquer le motif de la consultation');
        return;
      }

      // Construire les dates
      const startTime = new Date(formData.date);
      const [hours, minutes] = formData.time.split(':');
      startTime.setHours(parseInt(hours), parseInt(minutes), 0, 0);
      
      // Vérifier que la date est dans le futur
      if (startTime < new Date()) {
        alert('Veuillez sélectionner une date et heure future');
        return;
      }
      
      const endTime = new Date(startTime);
      endTime.setHours(endTime.getHours() + 1);
      
      // Calculer la durée en minutes
      const duration = 60; // 1 heure par défaut
      
      // Trouver le médecin sélectionné pour avoir son nom
      const selectedDoctorObj = doctorsList.find(d => d.id === formData.doctorId);
      
      // Préparer les données complètes pour l'API
      const appointmentData = {
        patientId: user?.id,
        patientName: `${user?.firstName || ''} ${user?.lastName || ''}`.trim(),
        doctorId: formData.doctorId,
        doctorName: selectedDoctorObj ? `Dr. ${selectedDoctorObj.firstName} ${selectedDoctorObj.lastName}`.trim() : '',
        startTime: startTime.toISOString(),
        endTime: endTime.toISOString(),
        reason: formData.reason,
        status: 'SCHEDULED',        // Requis par l'enum AppointmentStatus
        type: formData.type || 'CONSULTATION',  // Requis par l'enum AppointmentType
        isVirtual: formData.isVirtual || false,
        duration: duration,
        location: formData.location || 'Cabinet médical'
      };
      
      console.log('📤 Envoi des données:', appointmentData);
      
      // Appeler la mutation
      createMutation.mutate(appointmentData);
      
    } catch (error) {
      console.error('❌ Erreur dans handleSubmit:', error);
      alert('Erreur lors de la préparation du rendez-vous');
    }
  };

  const timeSlots = [
    '09:00', '09:30', '10:00', '10:30', '11:00', '11:30',
    '14:00', '14:30', '15:00', '15:30', '16:00', '16:30',
  ];

  if (doctorsLoading || appointmentsLoading) return <LoadingSpinner />;

  if (doctorsError) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Erreur lors du chargement des médecins: {doctorsError.message}
        </Alert>
      </Box>
    );
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h4">Mes Rendez-vous</Typography>
        <Button variant="contained" onClick={handleOpenDialog}>
          Nouveau rendez-vous
        </Button>
      </Box>

      {/* Liste des rendez-vous */}
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              Prochains rendez-vous
            </Typography>
            {appointmentsList.length > 0 ? (
              appointmentsList.map((apt) => (
                <Card key={apt.id} variant="outlined" sx={{ mb: 2 }}>
                  <CardContent>
                    <Grid container alignItems="center" spacing={2}>
                      <Grid item xs={12} sm={2}>
                        <Typography>
                          {new Date(apt.startTime).toLocaleDateString('fr-FR')}
                        </Typography>
                      </Grid>
                      <Grid item xs={12} sm={2}>
                        <Typography>
                          {new Date(apt.startTime).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })}
                        </Typography>
                      </Grid>
                      <Grid item xs={12} sm={3}>
                        <Typography fontWeight="bold">
                          {apt.doctorName}
                        </Typography>
                      </Grid>
                      <Grid item xs={12} sm={3}>
                        <Typography variant="body2">
                          {apt.reason}
                        </Typography>
                      </Grid>
                      <Grid item xs={12} sm={2}>
                        <Chip
                          label={apt.status}
                          color={apt.status === 'CONFIRMED' ? 'success' : 
                                 apt.status === 'SCHEDULED' ? 'primary' : 'default'}
                          size="small"
                        />
                      </Grid>
                    </Grid>
                  </CardContent>
                </Card>
              ))
            ) : (
              <Typography color="text.secondary">
                Aucun rendez-vous programmé
              </Typography>
            )}
          </Paper>
        </Grid>
      </Grid>

      {/* Dialogue nouveau rendez-vous */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="sm" fullWidth>
        <DialogTitle>Nouveau rendez-vous</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            {doctorsList.length === 0 ? (
              <Alert severity="info" sx={{ mb: 2 }}>
                Aucun médecin disponible
              </Alert>
            ) : (
              <>
                <FormControl fullWidth sx={{ mb: 2 }}>
                  <InputLabel>Médecin</InputLabel>
                  <Select
                    value={formData.doctorId}
                    label="Médecin"
                    onChange={(e) => setFormData({ ...formData, doctorId: e.target.value })}
                  >
                    {doctorsList.map((doc) => (
                      <MenuItem key={doc.id} value={doc.id}>
                        Dr. {doc.firstName} {doc.lastName} - {doc.specialty}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>

                <DatePicker
                  label="Date"
                  value={formData.date}
                  onChange={(newValue) => setFormData({ ...formData, date: newValue })}
                  slotProps={{ textField: { fullWidth: true, sx: { mb: 2 } } }}
                  minDate={new Date()}
                />

                <FormControl fullWidth sx={{ mb: 2 }}>
                  <InputLabel>Heure</InputLabel>
                  <Select
                    value={formData.time}
                    label="Heure"
                    onChange={(e) => setFormData({ ...formData, time: e.target.value })}
                  >
                    {timeSlots.map((slot) => (
                      <MenuItem key={slot} value={slot}>{slot}</MenuItem>
                    ))}
                  </Select>
                </FormControl>

                <FormControl fullWidth sx={{ mb: 2 }}>
                  <InputLabel>Type de consultation</InputLabel>
                  <Select
                    value={formData.type}
                    label="Type de consultation"
                    onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                  >
                    <MenuItem value="CONSULTATION">Consultation</MenuItem>
                    <MenuItem value="FOLLOW_UP">Suivi</MenuItem>
                    <MenuItem value="CHECKUP">Check-up</MenuItem>
                    <MenuItem value="TELEMEDICINE">Téléconsultation</MenuItem>
                  </Select>
                </FormControl>

                <FormControl fullWidth sx={{ mb: 2 }}>
                  <InputLabel>Lieu</InputLabel>
                  <Select
                    value={formData.location}
                    label="Lieu"
                    onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                  >
                    <MenuItem value="Cabinet médical">Cabinet médical</MenuItem>
                    <MenuItem value="Téléconsultation">Téléconsultation</MenuItem>
                    <MenuItem value="Hôpital">Hôpital</MenuItem>
                  </Select>
                </FormControl>

                <TextField
                  fullWidth
                  multiline
                  rows={3}
                  label="Motif"
                  value={formData.reason}
                  onChange={(e) => setFormData({ ...formData, reason: e.target.value })}
                  required
                />
              </>
            )}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Annuler</Button>
          <Button 
            onClick={handleSubmit} 
            variant="contained"
            disabled={!formData.doctorId || !formData.time || !formData.reason || createMutation.isLoading}
          >
            {createMutation.isLoading ? 'Création...' : 'Confirmer'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Appointments;