import React from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Grid,
  Avatar,
} from '@mui/material';
import {
  Person as PatientIcon,
  LocalHospital as DoctorIcon,
  LocalPharmacy as PharmacyIcon,
  Science as LabIcon,
} from '@mui/icons-material';
import { motion } from 'framer-motion';

const roles = [
  {
    id: 'PATIENT',
    label: 'Patient',
    icon: <PatientIcon sx={{ fontSize: 40 }} />,
    color: '#2e7d32',
    description: 'Prenez rendez-vous, consultez vos résultats',
  },
  {
    id: 'DOCTOR',
    label: 'Médecin',
    icon: <DoctorIcon sx={{ fontSize: 40 }} />,
    color: '#1976d2',
    description: 'Accédez aux dossiers patients, prescriptions',
  },
  {
    id: 'PHARMACIST',
    label: 'Pharmacie',
    icon: <PharmacyIcon sx={{ fontSize: 40 }} />,
    color: '#ed6c02',
    description: 'Gérez les prescriptions et stocks',
  },
  {
    id: 'LAB_TECHNICIAN',
    label: 'Laboratoire',
    icon: <LabIcon sx={{ fontSize: 40 }} />,
    color: '#9c27b0',
    description: 'Traitez les résultats de tests',
  },
];

const RoleSelector = ({ onSelect }) => {
  return (
    <Box sx={{ mt: 4 }}>
      <Typography variant="h5" align="center" gutterBottom>
        Choisissez votre rôle
      </Typography>
      <Typography variant="body2" align="center" color="text.secondary" paragraph>
        Sélectionnez le type de compte que vous souhaitez créer
      </Typography>

      <Grid container spacing={3} sx={{ mt: 2 }}>
        {roles.map((role, index) => (
          <Grid item xs={12} sm={6} md={3} key={role.id}>
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
            >
              <Card
                sx={{
                  cursor: 'pointer',
                  transition: 'all 0.3s ease',
                  '&:hover': {
                    transform: 'translateY(-5px)',
                    boxShadow: 3,
                  },
                }}
                onClick={() => onSelect(role.id)}
              >
                <CardContent sx={{ textAlign: 'center' }}>
                  <Avatar
                    sx={{
                      width: 80,
                      height: 80,
                      margin: '0 auto 16px',
                      bgcolor: role.color,
                    }}
                  >
                    {role.icon}
                  </Avatar>
                  <Typography variant="h6" gutterBottom>
                    {role.label}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {role.description}
                  </Typography>
                </CardContent>
              </Card>
            </motion.div>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default RoleSelector;