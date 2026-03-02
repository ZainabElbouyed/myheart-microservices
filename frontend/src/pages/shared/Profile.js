
import React, { useState } from 'react';
import {
  Box,
  Typography,
  Paper,
  Grid,
  Avatar,
  Button,
  TextField,
  Card,
  CardContent,
  Divider,
  IconButton,
  Alert,
  Snackbar,
  Chip, 
} from '@mui/material';
import {
  Edit as EditIcon,
  Save as SaveIcon,
  Cancel as CancelIcon,
  PhotoCamera as PhotoCameraIcon,
} from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';

const Profile = () => {
  const { user } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);
  const [formData, setFormData] = useState({
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
    email: user?.email || '',
    phoneNumber: user?.phoneNumber || '',
    address: '',
    city: '',
    postalCode: '',
  });

  const handleSave = () => {
    setIsEditing(false);
    setShowSuccess(true);
  };

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" gutterBottom>
        Mon Profil
      </Typography>

      <Grid container spacing={3}>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Box sx={{ position: 'relative', display: 'inline-block', mb: 2 }}>
                <Avatar
                  sx={{
                    width: 120,
                    height: 120,
                    mx: 'auto',
                    bgcolor: 'primary.main',
                    fontSize: '3rem',
                  }}
                >
                  {formData.firstName?.charAt(0)}
                </Avatar>
                <IconButton
                  sx={{
                    position: 'absolute',
                    bottom: 0,
                    right: 0,
                    bgcolor: 'background.paper',
                  }}
                  size="small"
                >
                  <PhotoCameraIcon />
                </IconButton>
              </Box>

              <Typography variant="h5" gutterBottom>
                {formData.firstName} {formData.lastName}
              </Typography>
              
              {/* ← ICI on utilise Chip */}
              <Chip
                label={user?.role || 'Utilisateur'}
                color="primary"
                sx={{ mb: 2 }}
              />

              <Divider sx={{ my: 2 }} />

              <Box sx={{ textAlign: 'left' }}>
                <Typography variant="body2" color="text.secondary">
                  Email
                </Typography>
                <Typography variant="body1" gutterBottom>
                  {formData.email}
                </Typography>

                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                  Téléphone
                </Typography>
                <Typography variant="body1" gutterBottom>
                  {formData.phoneNumber || 'Non renseigné'}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 3 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
              <Typography variant="h6">
                Informations personnelles
              </Typography>
              {!isEditing ? (
                <Button startIcon={<EditIcon />} onClick={() => setIsEditing(true)}>
                  Modifier
                </Button>
              ) : (
                <Box>
                  <Button startIcon={<SaveIcon />} variant="contained" onClick={handleSave} sx={{ mr: 1 }}>
                    Sauvegarder
                  </Button>
                  <Button startIcon={<CancelIcon />} onClick={() => setIsEditing(false)}>
                    Annuler
                  </Button>
                </Box>
              )}
            </Box>

            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Prénom"
                  value={formData.firstName}
                  onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                  disabled={!isEditing}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Nom"
                  value={formData.lastName}
                  onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                  disabled={!isEditing}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Email"
                  type="email"
                  value={formData.email}
                  disabled={!isEditing}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Téléphone"
                  value={formData.phoneNumber}
                  onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
                  disabled={!isEditing}
                />
              </Grid>
            </Grid>
          </Paper>
        </Grid>
      </Grid>

      <Snackbar
        open={showSuccess}
        autoHideDuration={3000}
        onClose={() => setShowSuccess(false)}
      >
        <Alert severity="success">Profil mis à jour avec succès !</Alert>
      </Snackbar>
    </Box>
  );
};

export default Profile;