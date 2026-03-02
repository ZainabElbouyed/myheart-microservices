// frontend/src/pages/lab/TestResults.js
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
  IconButton,
  TextField,
  InputAdornment,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Grid,
  Alert,
} from '@mui/material';
import {
  Search as SearchIcon,
  Visibility as VisibilityIcon,
  Refresh as RefreshIcon,
} from '@mui/icons-material';
import { useQuery } from '@tanstack/react-query';
import { labService } from '../../services/labService';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const TestResults = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedTest, setSelectedTest] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);

  const { 
    data: completedTests = [], 
    isLoading,
    error,
    refetch 
  } = useQuery({
    queryKey: ['completedTests'],
    queryFn: labService.getCompletedTests,
  });

  useEffect(() => {
    console.log('📊 Tests complétés reçus:', completedTests);
  }, [completedTests]);

  const completedList = Array.isArray(completedTests) ? completedTests : [];

  const filteredTests = completedList.filter(
    (test) =>
      (test.patientName?.toLowerCase() || '').includes(searchTerm.toLowerCase()) ||
      (test.testType?.toLowerCase() || '').includes(searchTerm.toLowerCase()) ||
      (test.doctorName?.toLowerCase() || '').includes(searchTerm.toLowerCase())
  );

  const handleViewDetails = (test) => {
    setSelectedTest(test);
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setSelectedTest(null);
  };

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
          Erreur lors du chargement des résultats: {error.message}
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="500" color="#1976d2">
          Résultats de tests
        </Typography>
        <Button 
          variant="outlined" 
          startIcon={<RefreshIcon />}
          onClick={() => refetch()}
        >
          Rafraîchir
        </Button>
      </Box>

      <Paper sx={{ p: 2, mb: 3 }}>
        <TextField
          fullWidth
          placeholder="Rechercher par patient, test ou médecin..."
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

      {filteredTests.length === 0 ? (
        <Alert severity="info">
          {searchTerm 
            ? 'Aucun résultat ne correspond à votre recherche'
            : 'Aucun résultat disponible'}
        </Alert>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead sx={{ bgcolor: '#f5f5f5' }}>
              <TableRow>
                <TableCell><strong>Patient</strong></TableCell>
                <TableCell><strong>Test</strong></TableCell>
                <TableCell><strong>Date</strong></TableCell>
                <TableCell><strong>Médecin</strong></TableCell>
                <TableCell align="right"><strong>Actions</strong></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredTests.map((test) => (
                <TableRow key={test.id} hover>
                  <TableCell>
                    <Typography variant="body1" fontWeight="medium">
                      {test.patientName || 'Patient'}
                    </Typography>
                  </TableCell>
                  <TableCell>{test.testType || 'Analyse'}</TableCell>
                  <TableCell>
                    {test.testDate 
                      ? new Date(test.testDate).toLocaleDateString('fr-FR')
                      : 'Date inconnue'}
                  </TableCell>
                  <TableCell>Dr. {test.doctorName || 'Médecin'}</TableCell>
                  <TableCell align="right">
                    <IconButton 
                      color="primary" 
                      size="small"
                      onClick={() => handleViewDetails(test)}
                    >
                      <VisibilityIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Dialogue de détail */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="md" fullWidth>
        <DialogTitle sx={{ bgcolor: '#f5f5f5' }}>
          <Typography variant="h6">
            Détails du résultat - {selectedTest?.patientName}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {selectedTest?.testType} - {selectedTest?.testDate ? new Date(selectedTest.testDate).toLocaleDateString('fr-FR') : ''}
          </Typography>
        </DialogTitle>
        <DialogContent dividers>
          {selectedTest && (
            <Box>
              <Grid container spacing={2} sx={{ mb: 3 }}>
                <Grid item xs={6}>
                  <Typography variant="body2" gutterBottom>
                    <strong>Patient:</strong> {selectedTest.patientName}
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Médecin:</strong> Dr. {selectedTest.doctorName}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" gutterBottom>
                    <strong>Date du test:</strong> {new Date(selectedTest.testDate).toLocaleDateString('fr-FR')}
                  </Typography>
                </Grid>
              </Grid>

              <Typography variant="subtitle1" gutterBottom fontWeight="bold">
                Résultats
              </Typography>

              <TableContainer component={Paper} variant="outlined" sx={{ mb: 3 }}>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Paramètre</TableCell>
                      <TableCell>Valeur</TableCell>
                      <TableCell>Unité</TableCell>
                      <TableCell>Référence</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {selectedTest.parameters && selectedTest.parameters.length > 0 ? (
                      selectedTest.parameters.map((param, index) => (
                        <TableRow key={index}>
                          <TableCell>{param.name || 'Paramètre'}</TableCell>
                          <TableCell>
                            <Typography
                              color={param.isAbnormal ? 'error' : 'inherit'}
                              fontWeight={param.isAbnormal ? 'bold' : 'normal'}
                            >
                              {param.value || '?'}
                            </Typography>
                          </TableCell>
                          <TableCell>{param.unit || ''}</TableCell>
                          <TableCell>{param.referenceRange || 'Non spécifié'}</TableCell>
                        </TableRow>
                      ))
                    ) : (
                      <TableRow>
                        <TableCell colSpan={4} align="center">
                          Aucun paramètre détaillé
                        </TableCell>
                      </TableRow>
                    )}
                  </TableBody>
                </Table>
              </TableContainer>

              {selectedTest.summary && (
                <>
                  <Typography variant="subtitle2" gutterBottom>
                    Résumé
                  </Typography>
                  <Paper variant="outlined" sx={{ p: 2, mb: 2, bgcolor: '#fafafa' }}>
                    <Typography variant="body2">
                      {selectedTest.summary}
                    </Typography>
                  </Paper>
                </>
              )}

              {selectedTest.interpretation && (
                <>
                  <Typography variant="subtitle2" gutterBottom>
                    Interprétation
                  </Typography>
                  <Paper variant="outlined" sx={{ p: 2, mb: 2, bgcolor: '#fafafa' }}>
                    <Typography variant="body2">
                      {selectedTest.interpretation}
                    </Typography>
                  </Paper>
                </>
              )}

              {selectedTest.recommendations && (
                <>
                  <Typography variant="subtitle2" gutterBottom>
                    Recommandations
                  </Typography>
                  <Paper variant="outlined" sx={{ p: 2, bgcolor: '#fafafa' }}>
                    <Typography variant="body2">
                      {selectedTest.recommendations}
                    </Typography>
                  </Paper>
                </>
              )}
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Fermer</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default TestResults;