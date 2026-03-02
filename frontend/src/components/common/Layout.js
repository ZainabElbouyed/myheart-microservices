import React, { useState } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import {
  Box,
  Drawer,
  AppBar,
  Toolbar,
  List,
  Typography,
  Divider,
  IconButton,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Avatar,
  Menu,
  MenuItem,
  Badge,
  useTheme,
} from '@mui/material';
import {
  Menu as MenuIcon,
  ChevronLeft as ChevronLeftIcon,
  Dashboard as DashboardIcon,
  People as PeopleIcon,
  CalendarMonth as CalendarIcon,
  Medication as MedicationIcon,
  Science as ScienceIcon,
  Inventory as InventoryIcon,
  Assignment as AssignmentIcon,
  Notifications as NotificationsIcon,
  Settings as SettingsIcon,
  Logout as LogoutIcon,
  Person as PersonIcon,
} from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';

const drawerWidth = 280;

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

const Layout = () => {
  const [open, setOpen] = useState(true);
  const [anchorEl, setAnchorEl] = useState(null);
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const theme = useTheme();

  const menuItems = getMenuItems(user?.role);

  const handleDrawerToggle = () => {
    setOpen(!open);
  };

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    handleMenuClose();
    logout();
  };

  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar
        position="fixed"
        sx={{
          width: open ? `calc(100% - ${drawerWidth}px)` : '100%',
          ml: open ? `${drawerWidth}px` : 0,
          transition: theme.transitions.create(['width', 'margin'], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
          }),
          backgroundColor: 'white',
          color: 'text.primary',
          boxShadow: '0 1px 4px rgba(0,0,0,0.1)',
        }}
      >
        <Toolbar>
          <IconButton
            color="inherit"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2 }}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
            MyHeart Healthcare
          </Typography>
          
          <IconButton color="inherit" sx={{ mr: 1 }}>
            <Badge badgeContent={3} color="error">
              <NotificationsIcon />
            </Badge>
          </IconButton>
          
          <IconButton onClick={handleMenuOpen}>
            <Avatar sx={{ bgcolor: 'primary.main' }}>
              {user?.firstName?.charAt(0)}
            </Avatar>
          </IconButton>
          
          <Menu
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={handleMenuClose}
            PaperProps={{
              sx: {
                mt: 1.5,
                borderRadius: 2,
                minWidth: 200,
              },
            }}
          >
            <MenuItem onClick={() => { handleMenuClose(); navigate('/profile'); }}>
              <ListItemIcon>
                <PersonIcon fontSize="small" />
              </ListItemIcon>
              Profil
            </MenuItem>
            <MenuItem onClick={() => { handleMenuClose(); navigate('/settings'); }}>
              <ListItemIcon>
                <SettingsIcon fontSize="small" />
              </ListItemIcon>
              Paramètres
            </MenuItem>
            <Divider />
            <MenuItem onClick={handleLogout}>
              <ListItemIcon>
                <LogoutIcon fontSize="small" />
              </ListItemIcon>
              Déconnexion
            </MenuItem>
          </Menu>
        </Toolbar>
      </AppBar>
      
      <Drawer
        variant="persistent"
        anchor="left"
        open={open}
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: drawerWidth,
            boxSizing: 'border-box',
            border: 'none',
            backgroundColor: '#fafafa',
          },
        }}
      >
        <Toolbar sx={{ justifyContent: 'space-between', px: 2 }}>
          <Typography variant="h5" color="primary" fontWeight="bold">
            MyHeart
          </Typography>
          <IconButton onClick={handleDrawerToggle}>
            <ChevronLeftIcon />
          </IconButton>
        </Toolbar>
        
        <Divider />
        
        <Box sx={{ p: 2 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
            <Avatar sx={{ bgcolor: 'primary.main', width: 48, height: 48, mr: 2 }}>
              {user?.firstName?.charAt(0)}
            </Avatar>
            <Box>
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
          </Box>
        </Box>
        
        <Divider />
        
        <List sx={{ px: 2 }}>
          {menuItems.map((item) => (
            <ListItem key={item.text} disablePadding sx={{ mb: 1 }}>
              <ListItemButton
                onClick={() => navigate(item.path)}
                sx={{
                  borderRadius: 2,
                  '&:hover': {
                    backgroundColor: 'primary.light',
                    color: 'white',
                    '& .MuiListItemIcon-root': {
                      color: 'white',
                    },
                  },
                }}
              >
                <ListItemIcon sx={{ color: 'primary.main', minWidth: 40 }}>
                  {item.icon}
                </ListItemIcon>
                <ListItemText primary={item.text} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
      </Drawer>
      
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: open ? `calc(100% - ${drawerWidth}px)` : '100%',
          minHeight: '100vh',
          backgroundColor: '#f5f5f5',
          transition: theme.transitions.create('width', {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
          }),
        }}
      >
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  );
};

export default Layout;