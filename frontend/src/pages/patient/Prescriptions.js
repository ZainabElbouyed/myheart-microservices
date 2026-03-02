import React from 'react';
import {
  Box,
  Typography,
  Paper,
  Grid,
  Card,
  CardContent,
  Chip,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Button,
} from '@mui/material';
import {
  Medication as MedicationIcon,
  CalendarToday as CalendarIcon,
  Person as PersonIcon,
  Print as PrintIcon,
} from '@mui/icons-material';
import { useQuery } from '@tanstack/react-query';
import { patientService } from '../../services/patientService';
import { useAuth } from '../../contexts/AuthContext';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { format } from 'date-fns';

const Prescriptions = () => {
  const { user } = useAuth();

  const { data: prescriptions = [], isLoading } = useQuery({
    queryKey: ['patientPrescriptions', user?.id],
    queryFn: () => patientService.getPrescriptions(user?.id),
  });

  if (isLoading) return <LoadingSpinner />;

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" gutterBottom>
        Mes Prescriptions
      </Typography>

      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              Prescriptions en cours
            </Typography>
            <List>
              {prescriptions.length > 0 ? (
                prescriptions.map((presc) => (
                  <Card key={presc.id} variant="outlined" sx={{ mb: 2 }}>
                    <CardContent>
                      <Grid container spacing={2}>
                        <Grid item xs={12}>
                          <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                            <Typography variant="subtitle1" fontWeight="bold">
                              Dr. {presc.doctorName}
                            </Typography>
                            <Chip
                              label={presc.status}
                              color={presc.status === 'ACTIVE' ? 'success' : 'default'}
                              size="small"
                            />
                          </Box>
                          <Typography variant="body2" color="text.secondary">
                            {format(new Date(presc.prescriptionDate), 'dd MMMM yyyy', { locale: fr })}
                          </Typography>
                        </Grid>
                        
                        <Grid item xs={12}>
                          <Typography variant="body2" color="text.secondary" gutterBottom>
                            Diagnostic: {presc.diagnosis}
                          </Typography>
                        </Grid>

                        <Grid item xs={12}>
                          <Typography variant="subtitle2" gutterBottom>
                            Médicaments prescrits:
                          </Typography>
                          <List dense>
                            {presc.medications.map((med, index) => (
                              <ListItem key={index}>
                                <ListItemIcon>
                                  <MedicationIcon color="primary" />
                                </ListItemIcon>
                                <ListItemText
                                  primary={`${med.name} - ${med.strength}`}
                                  secondary={`${med.dosage} • ${med.frequency} • ${med.duration}`}
                                />
                              </ListItem>
                            ))}
                          </List>
                        </Grid>

                        <Grid item xs={12}>
                          <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                            <Button startIcon={<PrintIcon />} size="small">
                              Imprimer
                            </Button>
                          </Box>
                        </Grid>
                      </Grid>
                    </CardContent>
                  </Card>
                ))
              ) : (
                <Typography color="text.secondary">
                  Aucune prescription
                </Typography>
              )}
            </List>
          </Paper>
        </Grid>

        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              Informations
            </Typography>
            <Typography variant="body2" paragraph>
              • Les prescriptions sont valables 1 an à compter de leur date d'émission.
            </Typography>
            <Typography variant="body2" paragraph>
              • Pour renouveler une prescription, contactez votre médecin.
            </Typography>
            <Typography variant="body2">
              • Présentez votre prescription à la pharmacie de votre choix.
            </Typography>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Prescriptions;