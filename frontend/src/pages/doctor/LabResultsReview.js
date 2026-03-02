// frontend/src/pages/doctor/LabResultsReview.js
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
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  IconButton,
  Alert,
  Grid,
  Divider,
} from '@mui/material';
import {
  Visibility as VisibilityIcon,
  CheckCircle as CheckCircleIcon,
  Warning as WarningIcon,
} from '@mui/icons-material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useAuth } from '../../contexts/AuthContext';
import { doctorService } from '../../services/doctorService';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const LabResultsReview = () => {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [selectedResult, setSelectedResult] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [reviewNotes, setReviewNotes] = useState('');
  const [conclusion, setConclusion] = useState('NORMAL');

  // Récupérer les résultats en attente
  const { 
    data: pendingResults = [], 
    isLoading,
    error 
  } = useQuery({
    queryKey: ['pendingLabResults', user?.id],
    queryFn: () => doctorService.getPendingLabResults(user?.id),
    enabled: !!user?.id,
  });

  useEffect(() => {
    console.log('📊 Résultats en attente:', pendingResults);
  }, [pendingResults]);

  const pendingResultsList = Array.isArray(pendingResults) ? pendingResults : [];

  const reviewMutation = useMutation({
    mutationFn: ({ id, data }) => doctorService.reviewLabResult(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries(['pendingLabResults', user?.id]);
      handleCloseDialog();
    },
  });

  const handleOpenDialog = (result) => {
    setSelectedResult(result);
    setOpenDialog(true);
    setReviewNotes('');
    setConclusion('NORMAL');
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setSelectedResult(null);
  };

  const handleSubmitReview = () => {
    if (selectedResult) {
      reviewMutation.mutate({
        id: selectedResult.id,
        data: {
          reviewedBy: user?.id,
          reviewedAt: new Date().toISOString(),
          notes: reviewNotes,
          conclusion: conclusion,
          status: 'REVIEWED',
        },
      });
    }
  };

  if (isLoading) return <LoadingSpinner />;

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">
          Erreur de chargement des résultats: {error.message}
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom fontWeight="500" color="#2E7D32">
        Révision des résultats de laboratoire
      </Typography>

      {pendingResultsList.length === 0 ? (
        <Alert severity="info" sx={{ mt: 2 }}>
          Aucun résultat en attente de révision
        </Alert>
      ) : (
        <TableContainer component={Paper} sx={{ mt: 3 }}>
          <Table>
            <TableHead sx={{ bgcolor: '#f5f5f5' }}>
              <TableRow>
                <TableCell><strong>Patient</strong></TableCell>
                <TableCell><strong>Type de test</strong></TableCell>
                <TableCell><strong>Date du test</strong></TableCell>
                <TableCell><strong>Statut</strong></TableCell>
                <TableCell><strong>Actions</strong></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {pendingResultsList.map((result) => (
                <TableRow key={result.id} hover>
                  <TableCell>
                    <Typography fontWeight="medium">
                      {result.patientName || 'Non spécifié'}
                    </Typography>
                  </TableCell>
                  <TableCell>{result.testType || 'Analyse'}</TableCell>
                  <TableCell>
                    {result.testDate 
                      ? new Date(result.testDate).toLocaleDateString('fr-FR')
                      : 'Date inconnue'}
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={result.status || 'PENDING'}
                      color="warning"
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    <IconButton
                      color="primary"
                      onClick={() => handleOpenDialog(result)}
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

      {/* Dialogue de révision */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="md" fullWidth>
        <DialogTitle sx={{ bgcolor: '#f5f5f5' }}>
          Réviser le résultat - {selectedResult?.patientName}
        </DialogTitle>
        <DialogContent dividers>
          {selectedResult && (
            <Box>
              <Typography variant="subtitle1" gutterBottom fontWeight="bold" color="primary">
                {selectedResult.testType} - {selectedResult.testDate 
                  ? new Date(selectedResult.testDate).toLocaleDateString('fr-FR')
                  : 'Date inconnue'}
              </Typography>
              
              <Divider sx={{ my: 2 }} />
              
              <Typography variant="subtitle2" gutterBottom fontWeight="bold">
                Résultats détaillés:
              </Typography>
              
              <Paper variant="outlined" sx={{ p: 2, my: 2, bgcolor: '#fafafa' }}>
                {selectedResult.parameters && selectedResult.parameters.length > 0 ? (
                  selectedResult.parameters.map((param, idx) => (
                    <Box 
                      key={idx} 
                      sx={{ 
                        mb: 1, 
                        p: 1.5, 
                        bgcolor: param.isAbnormal ? '#ffebee' : '#f5f5f5',
                        borderRadius: 1,
                        border: param.isAbnormal ? '1px solid #f44336' : 'none'
                      }}
                    >
                      <Grid container spacing={2}>
                        <Grid item xs={4}>
                          <Typography variant="body2" fontWeight="bold">
                            {param.name}
                          </Typography>
                        </Grid>
                        <Grid item xs={3}>
                          <Typography variant="body2">
                            {param.value} {param.unit}
                          </Typography>
                        </Grid>
                        <Grid item xs={3}>
                          <Typography variant="body2" color="text.secondary">
                            Normale: {param.referenceRange || 'Non spécifiée'}
                          </Typography>
                        </Grid>
                        <Grid item xs={2}>
                          {param.isAbnormal && (
                            <Chip
                              size="small"
                              label="Anormal"
                              color="error"
                              icon={<WarningIcon />}
                            />
                          )}
                        </Grid>
                      </Grid>
                    </Box>
                  ))
                ) : (
                  <Typography color="text.secondary">Aucun paramètre détaillé</Typography>
                )}
              </Paper>

              <FormControl fullWidth sx={{ mt: 2 }}>
                <InputLabel>Conclusion</InputLabel>
                <Select
                  value={conclusion}
                  label="Conclusion"
                  onChange={(e) => setConclusion(e.target.value)}
                >
                  <MenuItem value="NORMAL">Normal</MenuItem>
                  <MenuItem value="ABNORMAL">Anormal</MenuItem>
                  <MenuItem value="CRITICAL">Critique</MenuItem>
                  <MenuItem value="INCONCLUSIVE">Non concluant</MenuItem>
                </Select>
              </FormControl>

              <TextField
                fullWidth
                multiline
                rows={4}
                label="Notes de révision"
                value={reviewNotes}
                onChange={(e) => setReviewNotes(e.target.value)}
                sx={{ mt: 2 }}
                placeholder="Ajoutez vos commentaires ou instructions pour le patient..."
              />
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Annuler</Button>
          <Button
            onClick={handleSubmitReview}
            variant="contained"
            color="primary"
            startIcon={<CheckCircleIcon />}
            disabled={reviewMutation.isLoading}
          >
            {reviewMutation.isLoading ? 'Envoi...' : 'Valider la révision'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default LabResultsReview;