// frontend/src/pages/lab/PendingTests.js
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
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  LinearProgress,
} from '@mui/material';
import {
  CheckCircle as CheckCircleIcon,
  Science as ScienceIcon,
  Upload as UploadIcon,
  Refresh as RefreshIcon,
} from '@mui/icons-material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { labService } from '../../services/labService';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const PendingTests = () => {
  const queryClient = useQueryClient();
  const [selectedTest, setSelectedTest] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [activeStep, setActiveStep] = useState(0);
  const [testResults, setTestResults] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);

  // Récupérer les tests en attente
  const { 
    data: pendingTests = [], 
    isLoading,
    error,
    refetch 
  } = useQuery({
    queryKey: ['pendingTests'],
    queryFn: labService.getPendingTests,
  });

  useEffect(() => {
    console.log('📊 Tests en attente reçus:', pendingTests);
  }, [pendingTests]);

  const pendingList = Array.isArray(pendingTests) ? pendingTests : [];

  const completeMutation = useMutation({
    mutationFn: ({ id, results }) => labService.completeTest(id, results),
    onSuccess: () => {
      queryClient.invalidateQueries(['pendingTests']);
      queryClient.invalidateQueries(['inProgressTests']);
      queryClient.invalidateQueries(['completedTests']);
      handleCloseDialog();
    },
  });

  const handleOpenDialog = (test) => {
    setSelectedTest(test);
    setOpenDialog(true);
    setActiveStep(0);
    setTestResults([]);
    setSelectedFile(null);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setSelectedTest(null);
  };

  const handleAddResult = () => {
    setTestResults([...testResults, { 
      name: '', 
      value: '', 
      unit: '', 
      referenceRange: '',
      isAbnormal: false 
    }]);
  };

  const handleResultChange = (index, field, value) => {
    const updated = [...testResults];
    updated[index][field] = value;
    setTestResults(updated);
  };

  const handleRemoveResult = (index) => {
    setTestResults(testResults.filter((_, i) => i !== index));
  };

  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
  };

  const handleSubmit = () => {
    if (selectedTest) {
      const completedResults = {
        parameters: testResults,
        summary: `Résultats pour ${selectedTest.testType}`,
        interpretation: '',
        recommendations: '',
        attachments: selectedFile ? selectedFile.name : null,
      };
      
      completeMutation.mutate({
        id: selectedTest.id,
        results: completedResults,
      });
    }
  };

  const steps = ['Informations', 'Saisie des résultats', 'Validation'];

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
          Erreur lors du chargement des tests: {error.message}
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="500" color="#1976d2">
          Tests en attente
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
        <Alert severity="info" sx={{ mt: 2 }}>
          Aucun test en attente de traitement
        </Alert>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead sx={{ bgcolor: '#f5f5f5' }}>
              <TableRow>
                <TableCell><strong>Patient</strong></TableCell>
                <TableCell><strong>Médecin</strong></TableCell>
                <TableCell><strong>Type de test</strong></TableCell>
                <TableCell><strong>Date demande</strong></TableCell>
                <TableCell align="right"><strong>Actions</strong></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {pendingList.map((test) => (
                <TableRow key={test.id} hover>
                  <TableCell>
                    <Typography variant="body1" fontWeight="medium">
                      {test.patientName || 'Patient'}
                    </Typography>
                  </TableCell>
                  <TableCell>Dr. {test.doctorName || 'Médecin'}</TableCell>
                  <TableCell>{test.testType || 'Analyse'}</TableCell>
                  <TableCell>
                    {test.testDate 
                      ? new Date(test.testDate).toLocaleDateString('fr-FR')
                      : 'Date inconnue'}
                  </TableCell>
                  <TableCell align="right">
                    <Button
                      variant="contained"
                      size="small"
                      startIcon={<ScienceIcon />}
                      onClick={() => handleOpenDialog(test)}
                    >
                      Traiter
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Dialogue de traitement */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="md" fullWidth>
        <DialogTitle sx={{ bgcolor: '#f5f5f5' }}>
          <Typography variant="h6">
            Traiter le test - {selectedTest?.patientName}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {selectedTest?.testType} - {selectedTest?.testDate ? new Date(selectedTest.testDate).toLocaleDateString('fr-FR') : ''}
          </Typography>
        </DialogTitle>
        <DialogContent dividers>
          {selectedTest && (
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
                    Informations du test
                  </Typography>
                  <Grid container spacing={2}>
                    <Grid item xs={6}>
                      <Typography variant="body2" gutterBottom>
                        <strong>Patient:</strong> {selectedTest.patientName}
                      </Typography>
                      <Typography variant="body2" gutterBottom>
                        <strong>Médecin prescripteur:</strong> Dr. {selectedTest.doctorName}
                      </Typography>
                      <Typography variant="body2" gutterBottom>
                        <strong>Type de test:</strong> {selectedTest.testType}
                      </Typography>
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="body2" gutterBottom>
                        <strong>Date de demande:</strong> {new Date(selectedTest.testDate).toLocaleDateString('fr-FR')}
                      </Typography>
                      <Typography variant="body2" gutterBottom>
                        <strong>Instructions:</strong> {selectedTest.notes || 'Aucune instruction spécifique'}
                      </Typography>
                    </Grid>
                  </Grid>
                </Box>
              )}

              {activeStep === 1 && (
                <Box>
                  <Typography variant="h6" gutterBottom color="primary">
                    Saisie des résultats
                  </Typography>
                  
                  {testResults.map((result, index) => (
                    <Paper key={index} sx={{ p: 2, mb: 2, bgcolor: '#fafafa' }}>
                      <Grid container spacing={2} alignItems="center">
                        <Grid item xs={12} sm={4}>
                          <TextField
                            fullWidth
                            size="small"
                            label="Paramètre"
                            value={result.name}
                            onChange={(e) => handleResultChange(index, 'name', e.target.value)}
                            required
                          />
                        </Grid>
                        <Grid item xs={12} sm={2}>
                          <TextField
                            fullWidth
                            size="small"
                            label="Valeur"
                            value={result.value}
                            onChange={(e) => handleResultChange(index, 'value', e.target.value)}
                            required
                          />
                        </Grid>
                        <Grid item xs={12} sm={2}>
                          <TextField
                            fullWidth
                            size="small"
                            label="Unité"
                            value={result.unit}
                            onChange={(e) => handleResultChange(index, 'unit', e.target.value)}
                          />
                        </Grid>
                        <Grid item xs={12} sm={3}>
                          <TextField
                            fullWidth
                            size="small"
                            label="Valeurs de référence"
                            value={result.referenceRange}
                            onChange={(e) => handleResultChange(index, 'referenceRange', e.target.value)}
                            placeholder="ex: 0.70-1.10"
                          />
                        </Grid>
                        <Grid item xs={12} sm={1}>
                          <IconButton
                            color="error"
                            onClick={() => handleRemoveResult(index)}
                          >
                            ×
                          </IconButton>
                        </Grid>
                      </Grid>
                    </Paper>
                  ))}

                  <Button
                    variant="outlined"
                    onClick={handleAddResult}
                    sx={{ mt: 2 }}
                  >
                    + Ajouter un paramètre
                  </Button>

                  <Box sx={{ mt: 3 }}>
                    <Typography variant="subtitle1" gutterBottom>
                      Fichier joint (optionnel)
                    </Typography>
                    <Button
                      variant="outlined"
                      component="label"
                      startIcon={<UploadIcon />}
                    >
                      Choisir un fichier
                      <input
                        type="file"
                        hidden
                        onChange={handleFileChange}
                        accept=".pdf,.jpg,.jpeg,.png"
                      />
                    </Button>
                    {selectedFile && (
                      <Typography variant="body2" sx={{ mt: 1 }}>
                        Fichier sélectionné: {selectedFile.name}
                      </Typography>
                    )}
                  </Box>
                </Box>
              )}

              {activeStep === 2 && (
                <Box>
                  <Typography variant="h6" gutterBottom color="primary">
                    Validation des résultats
                  </Typography>
                  <Alert severity="warning" sx={{ mb: 2 }}>
                    Vérifiez attentivement les résultats avant de valider
                  </Alert>
                  
                  <Paper variant="outlined" sx={{ p: 2, bgcolor: '#fafafa' }}>
                    <Typography variant="subtitle1" gutterBottom fontWeight="bold">
                      Récapitulatif:
                    </Typography>
                    
                    <Typography variant="body2" gutterBottom>
                      <strong>Patient:</strong> {selectedTest.patientName}
                    </Typography>
                    <Typography variant="body2" gutterBottom>
                      <strong>Test:</strong> {selectedTest.testType}
                    </Typography>
                    
                    <Typography variant="subtitle2" sx={{ mt: 2, mb: 1 }} fontWeight="bold">
                      Résultats:
                    </Typography>
                    
                    {testResults.length === 0 ? (
                      <Typography variant="body2" color="text.secondary">
                        Aucun résultat saisi
                      </Typography>
                    ) : (
                      testResults.map((result, idx) => (
                        <Box key={idx} sx={{ mb: 1, p: 1, bgcolor: '#fff', borderRadius: 1 }}>
                          <Typography variant="body2">
                            <strong>{result.name || 'Paramètre'}:</strong> {result.value || '?'} {result.unit || ''}
                            {result.referenceRange && ` (Normale: ${result.referenceRange})`}
                          </Typography>
                        </Box>
                      ))
                    )}
                    
                    {selectedFile && (
                      <Typography variant="body2" sx={{ mt: 2 }}>
                        <strong>Fichier:</strong> {selectedFile.name}
                      </Typography>
                    )}
                  </Paper>
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
              onClick={handleSubmit}
              startIcon={<CheckCircleIcon />}
              disabled={completeMutation.isLoading}
            >
              {completeMutation.isLoading ? 'Envoi...' : 'Valider les résultats'}
            </Button>
          )}
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default PendingTests;