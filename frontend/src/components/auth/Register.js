import React, { useState } from 'react';
import {
  Container,
  Box,
  Typography,
  TextField,
  Button,
  Paper,
  Stepper,
  Step,
  StepLabel,
  Alert,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import RoleSelector from './RoleSelector';

const steps = ['Rôle', 'Informations personnelles', 'Compte'];

const Register = () => {
  const [activeStep, setActiveStep] = useState(0);
  const [selectedRole, setSelectedRole] = useState('');
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    phoneNumber: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [error, setError] = useState('');
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleRoleSelect = (role) => {
    setSelectedRole(role);
    setActiveStep(1);
  };

  const handleNext = () => {
    if (activeStep === 1) {
      if (!formData.firstName || !formData.lastName || !formData.phoneNumber) {
        setError('Veuillez remplir tous les champs');
        return;
      }
    } else if (activeStep === 2) {
      if (!formData.email || !formData.password) {
        setError('Veuillez remplir tous les champs');
        return;
      }
      if (formData.password !== formData.confirmPassword) {
        setError('Les mots de passe ne correspondent pas');
        return;
      }
      if (formData.password.length < 6) {
        setError('Le mot de passe doit contenir au moins 6 caractères');
        return;
      }
    }
    setError('');
    setActiveStep((prevStep) => prevStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevStep) => prevStep - 1);
  };

  const handleSubmit = async () => {
    const userData = {
      ...formData,
      role: selectedRole,
    };
    
    const result = await register(userData);
    if (result.success) {
      navigate('/login');
    }
  };

  const getStepContent = (step) => {
    switch (step) {
      case 0:
        return <RoleSelector onSelect={handleRoleSelect} />;
      case 1:
        return (
          <Box sx={{ pt: 2 }}>
            <TextField
              fullWidth
              label="Prénom"
              value={formData.firstName}
              onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
              sx={{ mb: 2 }}
            />
            <TextField
              fullWidth
              label="Nom"
              value={formData.lastName}
              onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
              sx={{ mb: 2 }}
            />
            <TextField
              fullWidth
              label="Téléphone"
              value={formData.phoneNumber}
              onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
              sx={{ mb: 2 }}
              placeholder="0612345678"
            />
          </Box>
        );
      case 2:
        return (
          <Box sx={{ pt: 2 }}>
            <TextField
              fullWidth
              label="Email"
              type="email"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              sx={{ mb: 2 }}
            />
            <TextField
              fullWidth
              label="Mot de passe"
              type="password"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              sx={{ mb: 2 }}
              helperText="Minimum 6 caractères"
            />
            <TextField
              fullWidth
              label="Confirmer le mot de passe"
              type="password"
              value={formData.confirmPassword}
              onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
              sx={{ mb: 2 }}
            />
          </Box>
        );
      default:
        return 'Étape inconnue';
    }
  };

  return (
    <Container component="main" maxWidth="md">
      <Box
        sx={{
          minHeight: '100vh',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          py: 4,
        }}
      >
        <Paper elevation={3} sx={{ p: 4, borderRadius: 3 }}>
          <Typography variant="h4" align="center" gutterBottom>
            Créer un compte
          </Typography>
          
          <Stepper activeStep={activeStep} sx={{ py: 3 }}>
            {steps.map((label) => (
              <Step key={label}>
                <StepLabel>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          {getStepContent(activeStep)}

          {activeStep > 0 && (
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 3 }}>
              <Button onClick={handleBack}>
                Retour
              </Button>
              {activeStep === steps.length - 1 ? (
                <Button variant="contained" onClick={handleSubmit}>
                  S'inscrire
                </Button>
              ) : (
                <Button variant="contained" onClick={handleNext}>
                  Suivant
                </Button>
              )}
            </Box>
          )}
        </Paper>
      </Box>
    </Container>
  );
};

export default Register;