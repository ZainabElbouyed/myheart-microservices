// frontend/src/pages/lab/UploadResult.js
import React, { useState, useCallback, useEffect } from 'react';
import {
  Box,
  Typography,
  Paper,
  Grid,
  TextField,
  Button,
  Card,
  CardContent,
  LinearProgress,
  Alert,
  Stepper,
  Step,
  StepLabel,
} from '@mui/material';
import { useDropzone } from 'react-dropzone';
import {
  CloudUpload as CloudUploadIcon,
  Description as DescriptionIcon,
  CheckCircle as CheckCircleIcon,
  ArrowBack as ArrowBackIcon,
} from '@mui/icons-material';
import { useMutation, useQuery } from '@tanstack/react-query';
import { labService } from '../../services/labService';
import { useNavigate } from 'react-router-dom';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const UploadResult = () => {
  const navigate = useNavigate();
  const [activeStep, setActiveStep] = useState(0);
  const [selectedTest, setSelectedTest] = useState(null);
  const [file, setFile] = useState(null);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [resultData, setResultData] = useState({
    summary: '',
    interpretation: '',
    recommendations: '',
  });

  const { data: pendingTests = [], isLoading } = useQuery({
    queryKey: ['pendingTests'],
    queryFn: labService.getPendingTests,
  });

  useEffect(() => {
    console.log('📊 Tests en attente pour upload:', pendingTests);
  }, [pendingTests]);

  const pendingList = Array.isArray(pendingTests) ? pendingTests : [];

  const uploadMutation = useMutation({
    mutationFn: ({ id, file }) => labService.uploadFile(id, file),
    onSuccess: () => {
      setActiveStep(2);
    },
  });

  const onDrop = useCallback((acceptedFiles) => {
    if (acceptedFiles.length > 0) {
      setFile(acceptedFiles[0]);
    }
  }, []);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'application/pdf': ['.pdf'],
      'image/jpeg': ['.jpg', '.jpeg'],
      'image/png': ['.png'],
    },
    maxSize: 10485760, // 10MB
  });

  const handleTestSelect = (test) => {
    setSelectedTest(test);
  };

  const handleUpload = () => {
    if (selectedTest && file) {
      setUploadProgress(0);
      const interval = setInterval(() => {
        setUploadProgress(prev => {
          if (prev >= 90) {
            clearInterval(interval);
            return 100;
          }
          return prev + 10;
        });
      }, 300);
      
      uploadMutation.mutate({
        id: selectedTest.id,
        file: file,
      });
    }
  };

  const steps = ['Sélectionner le test', 'Uploader le fichier', 'Confirmation'];

  if (isLoading) return <LoadingSpinner />;

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/lab/dashboard')}
          sx={{ mr: 2 }}
        >
          Retour
        </Button>
        <Typography variant="h4" fontWeight="500" color="#1976d2">
          Uploader un résultat
        </Typography>
      </Box>

      <Paper sx={{ p: 3 }}>
        <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
          {steps.map((label) => (
            <Step key={label}>
              <StepLabel>{label}</StepLabel>
            </Step>
          ))}
        </Stepper>

        <Grid container spacing={3}>
          {/* Étape 1: Sélection du test */}
          {activeStep === 0 && (
            <Grid item xs={12}>
              <Typography variant="h6" gutterBottom color="primary">
                Sélectionner un test en attente
              </Typography>
              
              {pendingList.length === 0 ? (
                <Alert severity="info">
                  Aucun test en attente de résultat
                </Alert>
              ) : (
                <Grid container spacing={2} sx={{ mt: 1 }}>
                  {pendingList.map((test) => (
                    <Grid item xs={12} sm={6} md={4} key={test.id}>
                      <Card
                        variant={selectedTest?.id === test.id ? 'elevation' : 'outlined'}
                        sx={{
                          cursor: 'pointer',
                          border: selectedTest?.id === test.id ? 2 : 1,
                          borderColor: selectedTest?.id === test.id ? 'primary.main' : 'divider',
                          transition: 'all 0.2s',
                          '&:hover': {
                            borderColor: 'primary.main',
                            boxShadow: 1,
                          },
                        }}
                        onClick={() => handleTestSelect(test)}
                      >
                        <CardContent>
                          <Typography variant="subtitle1" fontWeight="bold" color="primary">
                            {test.patientName || 'Patient'}
                          </Typography>
                          <Typography variant="body2" color="text.secondary" gutterBottom>
                            {test.testType || 'Analyse'}
                          </Typography>
                          <Typography variant="caption" color="text.secondary" display="block">
                            Dr. {test.doctorName || 'Médecin'}
                          </Typography>
                          <Box sx={{ mt: 1 }}>
                            <Typography variant="caption" color="text.secondary">
                              {test.testDate ? new Date(test.testDate).toLocaleDateString('fr-FR') : ''}
                            </Typography>
                          </Box>
                        </CardContent>
                      </Card>
                    </Grid>
                  ))}
                </Grid>
              )}

              <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 3 }}>
                <Button
                  variant="contained"
                  disabled={!selectedTest}
                  onClick={() => setActiveStep(1)}
                >
                  Suivant
                </Button>
              </Box>
            </Grid>
          )}

          {/* Étape 2: Upload du fichier */}
          {activeStep === 1 && (
            <Grid item xs={12}>
              <Typography variant="h6" gutterBottom color="primary">
                Uploader le fichier de résultats
              </Typography>

              {selectedTest && (
                <Alert severity="info" sx={{ mb: 3 }}>
                  <strong>Test sélectionné:</strong> {selectedTest.patientName} - {selectedTest.testType}
                </Alert>
              )}

              <Box
                {...getRootProps()}
                sx={{
                  border: '2px dashed',
                  borderColor: isDragActive ? 'primary.main' : 'grey.300',
                  borderRadius: 2,
                  p: 4,
                  textAlign: 'center',
                  cursor: 'pointer',
                  bgcolor: isDragActive ? 'action.hover' : 'background.paper',
                  transition: 'all 0.3s ease',
                  mb: 3,
                }}
              >
                <input {...getInputProps()} />
                <CloudUploadIcon sx={{ fontSize: 48, color: 'primary.main', mb: 2 }} />
                {isDragActive ? (
                  <Typography variant="h6">Déposez le fichier ici...</Typography>
                ) : (
                  <>
                    <Typography variant="h6" gutterBottom>
                      Glissez-déposez votre fichier ici
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      ou cliquez pour sélectionner un fichier
                    </Typography>
                    <Typography variant="caption" color="text.secondary" sx={{ mt: 2, display: 'block' }}>
                      Formats acceptés: PDF, JPG, PNG (max 10MB)
                    </Typography>
                  </>
                )}
              </Box>

              {file && (
                <Card variant="outlined" sx={{ mb: 3 }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                      <DescriptionIcon sx={{ mr: 2, color: 'primary.main', fontSize: 40 }} />
                      <Box sx={{ flex: 1 }}>
                        <Typography variant="body1" fontWeight="medium">
                          {file.name}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {(file.size / 1024).toFixed(2)} KB
                        </Typography>
                      </Box>
                      {uploadProgress > 0 && (
                        <Box sx={{ width: 200 }}>
                          <LinearProgress
                            variant="determinate"
                            value={uploadProgress}
                            sx={{ height: 8, borderRadius: 4 }}
                          />
                          <Typography variant="caption" align="center" display="block">
                            {uploadProgress}%
                          </Typography>
                        </Box>
                      )}
                    </Box>
                  </CardContent>
                </Card>
              )}

              <TextField
                fullWidth
                multiline
                rows={3}
                label="Résumé (optionnel)"
                value={resultData.summary}
                onChange={(e) => setResultData({ ...resultData, summary: e.target.value })}
                sx={{ mb: 2 }}
                placeholder="Résumé des résultats..."
              />

              <TextField
                fullWidth
                multiline
                rows={2}
                label="Interprétation (optionnel)"
                value={resultData.interpretation}
                onChange={(e) => setResultData({ ...resultData, interpretation: e.target.value })}
                sx={{ mb: 2 }}
                placeholder="Interprétation des résultats..."
              />

              <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 2 }}>
                <Button onClick={() => setActiveStep(0)}>
                  Retour
                </Button>
                <Button
                  variant="contained"
                  onClick={handleUpload}
                  disabled={!file || uploadMutation.isLoading}
                  startIcon={<CloudUploadIcon />}
                >
                  {uploadMutation.isLoading ? 'Upload en cours...' : 'Uploader'}
                </Button>
              </Box>
            </Grid>
          )}

          {/* Étape 3: Confirmation */}
          {activeStep === 2 && (
            <Grid item xs={12}>
              <Box sx={{ textAlign: 'center', py: 4 }}>
                <CheckCircleIcon color="success" sx={{ fontSize: 80, mb: 2 }} />
                <Typography variant="h5" gutterBottom>
                  Upload réussi !
                </Typography>
                <Typography variant="body1" color="text.secondary" paragraph>
                  Le résultat a été uploadé avec succès et sera disponible pour le médecin.
                </Typography>
                
                <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center', mt: 4 }}>
                  <Button
                    variant="outlined"
                    onClick={() => navigate('/lab/dashboard')}
                  >
                    Retour au tableau de bord
                  </Button>
                  <Button
                    variant="contained"
                    onClick={() => {
                      setActiveStep(0);
                      setSelectedTest(null);
                      setFile(null);
                      setResultData({
                        summary: '',
                        interpretation: '',
                        recommendations: '',
                      });
                    }}
                  >
                    Uploader un autre résultat
                  </Button>
                </Box>
              </Box>
            </Grid>
          )}
        </Grid>
      </Paper>
    </Box>
  );
};

export default UploadResult;