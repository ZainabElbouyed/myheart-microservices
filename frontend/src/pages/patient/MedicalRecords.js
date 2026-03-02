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
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Divider,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  TextField,
} from '@mui/material';
import {
  ExpandMore as ExpandMoreIcon,
  MedicalInformation as MedicalInfoIcon,
  Allergy as AllergyIcon,
  Medication as MedicationIcon,
  History as HistoryIcon,
  Bloodtype as BloodtypeIcon,
  Note as NoteIcon,
} from '@mui/icons-material';
import { useQuery } from '@tanstack/react-query';
import { patientService } from '../../services/patientService';
import { useAuth } from '../../contexts/AuthContext';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const MedicalRecords = () => {
  const { user } = useAuth();
  const [note, setNote] = useState('');

  const { data: patient, isLoading } = useQuery({
    queryKey: ['patientRecords', user?.id],
    queryFn: () => patientService.getMedicalRecords(user?.id),
  });

  if (isLoading) return <LoadingSpinner />;

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" gutterBottom>
        Mon Dossier Médical
      </Typography>

      <Grid container spacing={3}>
        {/* Informations générales */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Informations personnelles
              </Typography>
              <List>
                <ListItem>
                  <ListItemIcon>
                    <MedicalInfoIcon color="primary" />
                  </ListItemIcon>
                  <ListItemText
                    primary="Groupe sanguin"
                    secondary={patient?.bloodType || 'Non renseigné'}
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <BloodtypeIcon color="primary" />
                  </ListItemIcon>
                  <ListItemText
                    primary="Allergies"
                    secondary={patient?.allergies || 'Aucune allergie connue'}
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <MedicationIcon color="primary" />
                  </ListItemIcon>
                  <ListItemText
                    primary="Traitements en cours"
                    secondary={patient?.currentMedications || 'Aucun traitement'}
                  />
                </ListItem>
              </List>
            </CardContent>
          </Card>
        </Grid>

        {/* Antécédents médicaux */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Antécédents médicaux
              </Typography>
              <Accordion>
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                  <Typography>Antécédents familiaux</Typography>
                </AccordionSummary>
                <AccordionDetails>
                  <Typography>
                    {patient?.familyHistory || 'Aucun antécédent familial renseigné'}
                  </Typography>
                </AccordionDetails>
              </Accordion>
              <Accordion>
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                  <Typography>Antécédents personnels</Typography>
                </AccordionSummary>
                <AccordionDetails>
                  <Typography>
                    {patient?.personalHistory || 'Aucun antécédent personnel renseigné'}
                  </Typography>
                </AccordionDetails>
              </Accordion>
              <Accordion>
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                  <Typography>Chirurgies</Typography>
                </AccordionSummary>
                <AccordionDetails>
                  <Typography>
                    {patient?.surgeries || 'Aucune chirurgie renseignée'}
                  </Typography>
                </AccordionDetails>
              </Accordion>
            </CardContent>
          </Card>
        </Grid>

        {/* Notes médicales */}
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Notes médicales
              </Typography>
              <TextField
                fullWidth
                multiline
                rows={4}
                placeholder="Ajouter une note personnelle..."
                value={note}
                onChange={(e) => setNote(e.target.value)}
                sx={{ mb: 2 }}
              />
              <Button variant="contained">Ajouter une note</Button>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default MedicalRecords;