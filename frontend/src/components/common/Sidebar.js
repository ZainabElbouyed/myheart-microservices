import React from 'react';
import {
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  Divider,
  Avatar,
  Box,
} from '@mui/material';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import {
  Dashboard as DashboardIcon,
  CalendarMonth as CalendarIcon,
  Medication as MedicationIcon,
  Science as ScienceIcon,
  Assignment as AssignmentIcon,
  Inventory as InventoryIcon,
  People as PeopleIcon,
} from '@mui/icons-material';

const drawerWidth = 260;

const getMenuItems = (role) => {
  switch (role) {
    case 'PATIENT':
      return [
        { text: 'Dashboard', icon: <DashboardIcon />, path: '/patient/dashboard' },
        { text: 'Rendez-vous', icon: <CalendarIcon />, path: '/patient/appointments' },
        { text: 'Prescriptions', icon: <MedicationIcon />, path: '/patient/prescriptions' },
        { text: 'Résultats', icon: <ScienceIcon />, path: '/patient/lab-results' },
        { text: 'Dossier médical', icon: <AssignmentIcon />, path: '/patient/records' },
      ];
    case 'DOCTOR':
      return [
        { text: 'Dashboard', icon: <DashboardIcon />, path: '/doctor/dashboard' },
        { text: 'Patients', icon: <PeopleIcon />, path: '/doctor/patients' },
        { text: 'Rendez-vous', icon: <CalendarIcon />, path: '/doctor/appointments' },
        { text: 'Prescriptions', icon: <MedicationIcon />, path: '/doctor/prescriptions' },
        { text: 'Laboratoire', icon: <ScienceIcon />, path: '/doctor/lab-results' },
      ];
    case 'PHARMACIST':
      return [
        { text: 'Dashboard', icon: <DashboardIcon />, path: '/pharmacy/dashboard' },
        { text: 'Inventaire', icon: <InventoryIcon />, path: '/pharmacy/inventory' },
        { text: 'Prescriptions', icon: <MedicationIcon />, path: '/pharmacy/prescriptions' },
        { text: 'Commandes', icon: <AssignmentIcon />, path: '/pharmacy/orders' },
      ];
    case 'LAB_TECHNICIAN':
      return [
        { text: 'Dashboard', icon: <DashboardIcon />, path: '/lab/dashboard' },
        { text: 'Tests en attente', icon: <ScienceIcon />, path: '/lab/pending' },
        { text: 'Résultats', icon: <AssignmentIcon />, path: '/lab/results' },
        { text: 'Upload', icon: <AssignmentIcon />, path: '/lab/upload' },
      ];
    default:
      return [];
  }
};

const Sidebar = ({ mobileOpen, handleDrawerToggle }) => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const menuItems = getMenuItems(user?.role);

  const drawer = (
    <Box>
      <Toolbar sx={{ justifyContent: 'center', py: 2 }}>
        <Typography variant="h5" color="primary" fontWeight="bold">
          MyHeart
        </Typography>
      </Toolbar>
      <Divider />
      <Box sx={{ p: 2, textAlign: 'center' }}>
        <Avatar
          sx={{
            width: 80,
            height: 80,
            mx: 'auto',
            mb: 1,
            bgcolor: 'primary.main',
          }}
        >
          {user?.firstName?.charAt(0)}
        </Avatar>
        <Typography variant="subtitle1" fontWeight="bold">
          {user?.firstName} {user?.lastName}
        </Typography>
        <Typography variant="caption" color="text.secondary">
          {user?.role === 'PATIENT' && 'Patient'}
          {user?.role === 'DOCTOR' && 'Médecin'}
          {user?.role === 'PHARMACIST' && 'Pharmacien'}
          {user?.role === 'LAB_TECHNICIAN' && 'Technicien de laboratoire'}
        </Typography>
      </Box>
      <Divider />
      <List sx={{ px: 2, pt: 2 }}>
        {menuItems.map((item) => {
          const isActive = location.pathname === item.path;
          return (
            <ListItem key={item.text} disablePadding sx={{ mb: 1 }}>
              <ListItemButton
                onClick={() => navigate(item.path)}
                selected={isActive}
                sx={{
                  borderRadius: 2,
                  '&.Mui-selected': {
                    backgroundColor: 'primary.main',
                    color: 'white',
                    '& .MuiListItemIcon-root': {
                      color: 'white',
                    },
                    '&:hover': {
                      backgroundColor: 'primary.dark',
                    },
                  },
                }}
              >
                <ListItemIcon sx={{ color: isActive ? 'white' : 'primary.main' }}>
                  {item.icon}
                </ListItemIcon>
                <ListItemText primary={item.text} />
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>
    </Box>
  );

  return (
    <Box
      component="nav"
      sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}
    >
      <Drawer
        variant="temporary"
        open={mobileOpen}
        onClose={handleDrawerToggle}
        ModalProps={{ keepMounted: true }}
        sx={{
          display: { xs: 'block', sm: 'none' },
          '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
        }}
      >
        {drawer}
      </Drawer>
      <Drawer
        variant="permanent"
        sx={{
          display: { xs: 'none', sm: 'block' },
          '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
        }}
        open
      >
        {drawer}
      </Drawer>
    </Box>
  );
};

export default Sidebar;