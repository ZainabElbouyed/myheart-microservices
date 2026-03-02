// frontend/src/pages/doctor/PatientList.js
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
  IconButton,
  Avatar,
  Chip,
  Button,
  Alert,
} from '@mui/material';
import {
  Search as SearchIcon,
  Visibility as VisibilityIcon,
  Medication as MedicationIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { useAuth } from '../../contexts/AuthContext';
import { doctorService } from '../../services/doctorService';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const PatientList = () => {
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const { user } = useAuth();
  const navigate = useNavigate();

  console.log('👤 User médecin:', user);

  // Récupérer les patients du médecin
  const { 
    data: patients = [], 
    isLoading,
    error 
  } = useQuery({
    queryKey: ['doctorPatients', user?.id],
    queryFn: () => doctorService.getPatients(user?.id),
    enabled: !!user?.id,
  });

  useEffect(() => {
    console.log('📊 Patients reçus:', patients);
  }, [patients]);

  const patientsList = Array.isArray(patients) ? patients : [];

  const filteredPatients = patientsList.filter(
    (patient) =>
      (patient.firstName?.toLowerCase() || '').includes(searchTerm.toLowerCase()) ||
      (patient.lastName?.toLowerCase() || '').includes(searchTerm.toLowerCase()) ||
      (patient.email?.toLowerCase() || '').includes(searchTerm.toLowerCase())
  );

  if (isLoading) return <LoadingSpinner />;

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Erreur lors du chargement des patients: {error.message}
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom fontWeight="500" color="#2E7D32">
        Mes Patients
      </Typography>

      <Paper sx={{ p: 2, mb: 3 }}>
        <TextField
          fullWidth
          placeholder="Rechercher un patient par nom, prénom ou email..."
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

      {filteredPatients.length === 0 ? (
        <Alert severity="info">
          {searchTerm 
            ? 'Aucun patient ne correspond à votre recherche' 
            : 'Vous n\'avez pas encore de patients'}
        </Alert>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead sx={{ bgcolor: '#f5f5f5' }}>
              <TableRow>
                <TableCell><strong>Patient</strong></TableCell>
                <TableCell><strong>Contact</strong></TableCell>
                <TableCell><strong>Groupe sanguin</strong></TableCell>
                <TableCell><strong>Dernière visite</strong></TableCell>
                <TableCell align="right"><strong>Actions</strong></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredPatients
                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                .map((patient) => (
                  <TableRow key={patient.id} hover>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <Avatar sx={{ mr: 2, bgcolor: '#2E7D32' }}>
                          {patient.firstName?.charAt(0) || 'P'}
                        </Avatar>
                        <Box>
                          <Typography variant="body1" fontWeight="medium">
                            {patient.firstName || ''} {patient.lastName || ''}
                          </Typography>
                          <Typography variant="caption" color="text.secondary">
                            {patient.email || 'Email non disponible'}
                          </Typography>
                        </Box>
                      </Box>
                    </TableCell>
                    <TableCell>{patient.phoneNumber || 'Non renseigné'}</TableCell>
                    <TableCell>
                      <Chip
                        label={patient.bloodType || 'ND'}
                        size="small"
                        color={patient.bloodType ? 'primary' : 'default'}
                      />
                    </TableCell>
                    <TableCell>
                      {patient.lastVisit 
                        ? new Date(patient.lastVisit).toLocaleDateString('fr-FR')
                        : 'Non renseigné'}
                    </TableCell>
                    <TableCell align="right">
                      <IconButton
                        color="primary"
                        onClick={() => navigate(`/doctor/patients/${patient.id}`)}
                        title="Voir le dossier"
                      >
                        <VisibilityIcon />
                      </IconButton>
                      <IconButton
                        color="info"
                        onClick={() => navigate(`/doctor/prescriptions/new?patientId=${patient.id}`)}
                        title="Nouvelle prescription"
                      >
                        <MedicationIcon />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
            </TableBody>
          </Table>
          <TablePagination
            rowsPerPageOptions={[5, 10, 25]}
            component="div"
            count={filteredPatients.length}
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
    </Box>
  );
};

export default PatientList;