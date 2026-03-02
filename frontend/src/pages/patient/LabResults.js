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
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from '@mui/material';
import {
  Science as ScienceIcon,
  CalendarToday as CalendarIcon,
  Person as PersonIcon,
  Download as DownloadIcon,
  Visibility as VisibilityIcon,
} from '@mui/icons-material';
import { useQuery } from '@tanstack/react-query';
import { patientService } from '../../services/patientService';
import { useAuth } from '../../contexts/AuthContext';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { format } from 'date-fns';

const LabResults = () => {
  const [selectedResult, setSelectedResult] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const { user } = useAuth();

  const { data: results = [], isLoading } = useQuery({
    queryKey: ['patientLabResults', user?.id],
    queryFn: () => patientService.getLabResults(user?.id),
  });

  const handleViewDetails = (result) => {
    setSelectedResult(result);
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setSelectedResult(null);
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED':
        return 'success';
      case 'PENDING':
        return 'warning';
      case 'ABNORMAL':
        return 'error';
      default:
        return 'default';
    }
  };

  const getStatusLabel = (status) => {
    switch (status) {
      case 'COMPLETED':
        return 'Terminé';
      case 'PENDING':
        return 'En attente';
      case 'ABNORMAL':
        return 'Anormal';
      default:
        return status;
    }
  };

  if (isLoading) return <LoadingSpinner />;

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" gutterBottom>
        Résultats de laboratoire
      </Typography>

      <Grid container spacing={3}>
        <Grid item xs={12}>
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom>
              Historique des résultats
            </Typography>
            <List>
              {results.length > 0 ? (
                results.map((result) => (
                  <Card key={result.id} variant="outlined" sx={{ mb: 2 }}>
                    <CardContent>
                      <Grid container spacing={2} alignItems="center">
                        <Grid item xs={12} sm={3}>
                          <Box sx={{ display: 'flex', alignItems: 'center' }}>
                            <CalendarIcon sx={{ mr: 1, color: 'text.secondary' }} />
                            <Typography>
                              {format(new Date(result.testDate), 'dd/MM/yyyy')}
                            </Typography>
                          </Box>
                        </Grid>
                        <Grid item xs={12} sm={3}>
                          <Typography variant="subtitle1" fontWeight="bold">
                            {result.testType}
                          </Typography>
                        </Grid>
                        <Grid item xs={12} sm={2}>
                          <Chip
                            label={getStatusLabel(result.status)}
                            color={getStatusColor(result.status)}
                            size="small"
                          />
                        </Grid>
                        <Grid item xs={12} sm={2}>
                          <Typography variant="body2" color="text.secondary">
                            Dr. {result.doctorName}
                          </Typography>
                        </Grid>
                        <Grid item xs={12} sm={2}>
                          <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                            <Button
                              size="small"
                              startIcon={<VisibilityIcon />}
                              onClick={() => handleViewDetails(result)}
                            >
                              Détails
                            </Button>
                            {result.fileUrl && (
                              <Button size="small" startIcon={<DownloadIcon />}>
                                PDF
                              </Button>
                            )}
                          </Box>
                        </Grid>
                      </Grid>
                    </CardContent>
                  </Card>
                ))
              ) : (
                <Typography color="text.secondary">
                  Aucun résultat disponible
                </Typography>
              )}
            </List>
          </Paper>
        </Grid>
      </Grid>

      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="md" fullWidth>
        {selectedResult && (
          <>
            <DialogTitle>
              <Typography variant="h6">
                {selectedResult.testType} - {format(new Date(selectedResult.testDate), 'dd MMMM yyyy')}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Dr. {selectedResult.doctorName} • Laboratoire: {selectedResult.labName}
              </Typography>
            </DialogTitle>
            <DialogContent>
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Paramètre</TableCell>
                      <TableCell>Valeur</TableCell>
                      <TableCell>Unité</TableCell>
                      <TableCell>Valeurs de référence</TableCell>
                      <TableCell>Interprétation</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {selectedResult.parameters?.map((param, index) => (
                      <TableRow key={index}>
                        <TableCell>{param.name}</TableCell>
                        <TableCell>
                          <Typography
                            color={param.isAbnormal ? 'error' : 'inherit'}
                            fontWeight={param.isAbnormal ? 'bold' : 'normal'}
                          >
                            {param.value}
                          </Typography>
                        </TableCell>
                        <TableCell>{param.unit}</TableCell>
                        <TableCell>{param.referenceRange}</TableCell>
                        <TableCell>{param.interpretation}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>

              {selectedResult.summary && (
                <Box sx={{ mt: 3 }}>
                  <Typography variant="subtitle2" gutterBottom>
                    Résumé
                  </Typography>
                  <Typography variant="body2" paragraph>
                    {selectedResult.summary}
                  </Typography>
                </Box>
              )}

              {selectedResult.interpretation && (
                <Box sx={{ mt: 2 }}>
                  <Typography variant="subtitle2" gutterBottom>
                    Interprétation
                  </Typography>
                  <Typography variant="body2" paragraph>
                    {selectedResult.interpretation}
                  </Typography>
                </Box>
              )}
            </DialogContent>
            <DialogActions>
              <Button onClick={handleCloseDialog}>Fermer</Button>
              {selectedResult.fileUrl && (
                <Button variant="contained" startIcon={<DownloadIcon />}>
                  Télécharger PDF
                </Button>
              )}
            </DialogActions>
          </>
        )}
      </Dialog>
    </Box>
  );
};

export default LabResults;