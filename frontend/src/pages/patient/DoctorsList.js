// frontend/src/pages/patient/DoctorsList.js
import React, { useState } from 'react';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Avatar,
  Chip,
  Button,
  TextField,
  InputAdornment,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Paper,
  Alert,  // ← AJOUTER CET IMPORT
} from '@mui/material';
import {
  Search as SearchIcon,
  LocalHospital as DoctorIcon,
  Star as StarIcon,
} from '@mui/icons-material';
import { useQuery } from '@tanstack/react-query';
import { patientService } from '../../services/patientService';
import { useNavigate } from 'react-router-dom';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const DoctorsList = () => {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [specialty, setSpecialty] = useState('all');

  // Récupérer tous les médecins
  const { data: doctors, isLoading, error } = useQuery({
    queryKey: ['doctors'],
    queryFn: patientService.getDoctors,
  });

  // ✅ Vérification que doctors est un tableau
  const doctorsList = Array.isArray(doctors) ? doctors : [];
  console.log('🔍 DoctorsList - doctorsList:', doctorsList);

  // Extraire les spécialités uniques pour le filtre
  const specialties = ['all', ...new Set(doctorsList.map(d => d.specialty).filter(Boolean))];

  // Filtrer les médecins
  const filteredDoctors = doctorsList.filter(doctor => {
    const matchesSearch = 
      doctor.firstName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      doctor.lastName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      doctor.specialty?.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesSpecialty = specialty === 'all' || doctor.specialty === specialty;
    
    return matchesSearch && matchesSpecialty;
  });

  if (isLoading) return <LoadingSpinner />;
  
  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Erreur lors du chargement des médecins: {error.message}
        </Alert>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Nos Médecins
      </Typography>

      {/* Filtres */}
      <Paper sx={{ p: 2, mb: 3 }}>
        <Grid container spacing={2}>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              placeholder="Rechercher un médecin..."
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
          </Grid>
          <Grid item xs={12} md={6}>
            <FormControl fullWidth>
              <InputLabel>Spécialité</InputLabel>
              <Select
                value={specialty}
                label="Spécialité"
                onChange={(e) => setSpecialty(e.target.value)}
              >
                {specialties.map((spec) => (
                  <MenuItem key={spec} value={spec}>
                    {spec === 'all' ? 'Toutes les spécialités' : spec}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
        </Grid>
      </Paper>

      {/* Résultats */}
      {filteredDoctors.length === 0 ? (
        <Typography color="text.secondary" sx={{ textAlign: 'center', py: 4 }}>
          Aucun médecin trouvé
        </Typography>
      ) : (
        <Grid container spacing={3}>
          {filteredDoctors.map((doctor) => (
            <Grid item xs={12} sm={6} md={4} key={doctor.id}>
              <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                <CardContent sx={{ flexGrow: 1 }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <Avatar
                      sx={{
                        width: 60,
                        height: 60,
                        mr: 2,
                        bgcolor: 'primary.main',
                      }}
                    >
                      <DoctorIcon />
                    </Avatar>
                    <Box>
                      <Typography variant="h6">
                        Dr. {doctor.firstName} {doctor.lastName}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {doctor.specialty}
                      </Typography>
                    </Box>
                  </Box>

                  <Box sx={{ mb: 2 }}>
                    <Typography variant="body2" paragraph>
                      {doctor.bio || 'Aucune description disponible'}
                    </Typography>
                    
                    {doctor.experience && (
                      <Typography variant="body2" color="text.secondary">
                        {doctor.experience} ans d'expérience
                      </Typography>
                    )}
                  </Box>

                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <Chip
                      label={doctor.acceptingNewPatients ? 'Accepte de nouveaux patients' : 'Complet'}
                      color={doctor.acceptingNewPatients ? 'success' : 'default'}
                      size="small"
                      sx={{ mr: 1 }}
                    />
                    {doctor.rating && (
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <StarIcon sx={{ color: 'warning.main', fontSize: 18 }} />
                        <Typography variant="body2" sx={{ ml: 0.5 }}>
                          {doctor.rating}
                        </Typography>
                      </Box>
                    )}
                  </Box>

                  <Button
                    fullWidth
                    variant="contained"
                    onClick={() => navigate('/patient/appointments/new', { 
                      state: { selectedDoctor: doctor } 
                    })}
                    disabled={!doctor.acceptingNewPatients}
                  >
                    Prendre rendez-vous
                  </Button>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Box>
  );
};

export default DoctorsList;