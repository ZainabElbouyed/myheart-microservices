import React, { useState } from 'react';  
import {
  Box,
  Typography,
  Paper,
  Grid,
  Switch,
  FormControlLabel,
  Divider,
  Button,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Alert,
  Snackbar,
  Slider,  // ← AJOUTER Slider
  List,     // ← AJOUTER List
  ListItem, // ← AJOUTER ListItem
  ListItemIcon, // ← AJOUTER ListItemIcon
  ListItemText, // ← AJOUTER ListItemText
} from '@mui/material';

import {
  Notifications as NotificationsIcon,
  Language as LanguageIcon,
  Security as SecurityIcon,
  Palette as PaletteIcon,
  Save as SaveIcon,
  Backup as BackupIcon,  // ← AJOUTER BackupIcon
  Storage as StorageIcon, // ← AJOUTER StorageIcon
} from '@mui/icons-material';

const Settings = () => {
  const [settings, setSettings] = useState({
    notifications: {
      email: true,
      sms: false,
      push: true,
      appointmentReminders: true,
      labResults: true,
      newsletter: false,
    },
    appearance: {
      theme: 'light',
      compactMode: false,
      fontSize: 14,
    },
    language: 'fr',
    security: {
      twoFactor: false,
      sessionTimeout: 30,
    },
    backup: {
      autoBackup: true,
      backupFrequency: 'daily',
    },
    privacy: {
      shareData: false,
      anonymousStats: true,
    },
  });

  const [saved, setSaved] = useState(false);
  const [activeTab, setActiveTab] = useState('notifications');

  const handleNotificationChange = (key) => {
    setSettings({
      ...settings,
      notifications: {
        ...settings.notifications,
        [key]: !settings.notifications[key],
      },
    });
  };

  const handleSave = () => {
    setSaved(true);
    setTimeout(() => setSaved(false), 3000);
  };

  const tabs = [
    { id: 'notifications', label: 'Notifications', icon: <NotificationsIcon /> },
    { id: 'appearance', label: 'Apparence', icon: <PaletteIcon /> },
    { id: 'language', label: 'Langue', icon: <LanguageIcon /> },
    { id: 'security', label: 'Sécurité', icon: <SecurityIcon /> },
    { id: 'backup', label: 'Sauvegarde', icon: <BackupIcon /> },
    { id: 'privacy', label: 'Confidentialité', icon: <StorageIcon /> },
  ];

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" gutterBottom>
        Paramètres
      </Typography>

      {saved && (
        <Alert severity="success" sx={{ mb: 3 }}>
          Paramètres sauvegardés avec succès !
        </Alert>
      )}

      <Grid container spacing={3}>
        {/* Navigation latérale */}
        <Grid item xs={12} md={3}>
          <Paper sx={{ p: 2 }}>
            <List>
              {tabs.map((tab) => (
                <ListItem
                  key={tab.id}
                  button
                  selected={activeTab === tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  sx={{
                    borderRadius: 1,
                    mb: 0.5,
                    '&.Mui-selected': {
                      backgroundColor: 'primary.main',
                      color: 'white',
                      '& .MuiListItemIcon-root': {
                        color: 'white',
                      },
                    },
                  }}
                >
                  <ListItemIcon sx={{ color: activeTab === tab.id ? 'white' : 'primary.main' }}>
                    {tab.icon}
                  </ListItemIcon>
                  <ListItemText primary={tab.label} />
                </ListItem>
              ))}
            </List>
          </Paper>
        </Grid>

        {/* Contenu des paramètres */}
        <Grid item xs={12} md={9}>
          <Paper sx={{ p: 3 }}>
            {/* Notifications */}
            {activeTab === 'notifications' && (
              <Box>
                <Typography variant="h6" gutterBottom>
                  Notifications
                </Typography>
                <Grid container spacing={2}>
                  <Grid item xs={12}>
                    <FormControlLabel
                      control={
                        <Switch
                          checked={settings.notifications.email}
                          onChange={() => handleNotificationChange('email')}
                        />
                      }
                      label="Notifications par email"
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <FormControlLabel
                      control={
                        <Switch
                          checked={settings.notifications.sms}
                          onChange={() => handleNotificationChange('sms')}
                        />
                      }
                      label="Notifications par SMS"
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <FormControlLabel
                      control={
                        <Switch
                          checked={settings.notifications.push}
                          onChange={() => handleNotificationChange('push')}
                        />
                      }
                      label="Notifications push"
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <FormControlLabel
                      control={
                        <Switch
                          checked={settings.notifications.appointmentReminders}
                          onChange={() => handleNotificationChange('appointmentReminders')}
                        />
                      }
                      label="Rappels de rendez-vous"
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <FormControlLabel
                      control={
                        <Switch
                          checked={settings.notifications.labResults}
                          onChange={() => handleNotificationChange('labResults')}
                        />
                      }
                      label="Résultats de laboratoire"
                    />
                  </Grid>
                </Grid>
              </Box>
            )}

            {/* Apparence */}
            {activeTab === 'appearance' && (
              <Box>
                <Typography variant="h6" gutterBottom>
                  Apparence
                </Typography>
                <Grid container spacing={3}>
                  <Grid item xs={12}>
                    <FormControl fullWidth>
                      <InputLabel>Thème</InputLabel>
                      <Select
                        value={settings.appearance.theme}
                        label="Thème"
                        onChange={(e) => setSettings({
                          ...settings,
                          appearance: { ...settings.appearance, theme: e.target.value }
                        })}
                      >
                        <MenuItem value="light">Clair</MenuItem>
                        <MenuItem value="dark">Sombre</MenuItem>
                        <MenuItem value="system">Système</MenuItem>
                      </Select>
                    </FormControl>
                  </Grid>
                  <Grid item xs={12}>
                    <Typography gutterBottom>
                      Taille de la police ({settings.appearance.fontSize}px)
                    </Typography>
                    <Slider
                      value={settings.appearance.fontSize}
                      min={12}
                      max={20}
                      step={1}
                      onChange={(e, value) => setSettings({
                        ...settings,
                        appearance: { ...settings.appearance, fontSize: value }
                      })}
                      valueLabelDisplay="auto"
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <FormControlLabel
                      control={
                        <Switch
                          checked={settings.appearance.compactMode}
                          onChange={(e) => setSettings({
                            ...settings,
                            appearance: { ...settings.appearance, compactMode: e.target.checked }
                          })}
                        />
                      }
                      label="Mode compact"
                    />
                  </Grid>
                </Grid>
              </Box>
            )}

            {/* Langue */}
            {activeTab === 'language' && (
              <Box>
                <Typography variant="h6" gutterBottom>
                  Langue et région
                </Typography>
                <FormControl fullWidth sx={{ mb: 2 }}>
                  <InputLabel>Langue</InputLabel>
                  <Select
                    value={settings.language}
                    label="Langue"
                    onChange={(e) => setSettings({ ...settings, language: e.target.value })}
                  >
                    <MenuItem value="fr">Français</MenuItem>
                    <MenuItem value="en">English</MenuItem>
                    <MenuItem value="es">Español</MenuItem>
                    <MenuItem value="de">Deutsch</MenuItem>
                  </Select>
                </FormControl>
                <FormControl fullWidth>
                  <InputLabel>Format de date</InputLabel>
                  <Select
                    value={settings.language === 'fr' ? 'fr' : 'en'}
                    label="Format de date"
                    onChange={(e) => console.log('Format changé', e.target.value)}
                  >
                    <MenuItem value="fr">DD/MM/YYYY</MenuItem>
                    <MenuItem value="en">MM/DD/YYYY</MenuItem>
                    <MenuItem value="iso">YYYY-MM-DD</MenuItem>
                  </Select>
                </FormControl>
              </Box>
            )}

            {/* Sécurité */}
            {activeTab === 'security' && (
              <Box>
                <Typography variant="h6" gutterBottom>
                  Sécurité
                </Typography>
                <Grid container spacing={3}>
                  <Grid item xs={12}>
                    <FormControlLabel
                      control={
                        <Switch
                          checked={settings.security.twoFactor}
                          onChange={(e) => setSettings({
                            ...settings,
                            security: { ...settings.security, twoFactor: e.target.checked }
                          })}
                        />
                      }
                      label="Authentification à deux facteurs"
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <FormControl fullWidth>
                      <InputLabel>Délai d'expiration de session</InputLabel>
                      <Select
                        value={settings.security.sessionTimeout}
                        label="Délai d'expiration de session"
                        onChange={(e) => setSettings({
                          ...settings,
                          security: { ...settings.security, sessionTimeout: e.target.value }
                        })}
                      >
                        <MenuItem value={15}>15 minutes</MenuItem>
                        <MenuItem value={30}>30 minutes</MenuItem>
                        <MenuItem value={60}>1 heure</MenuItem>
                        <MenuItem value={120}>2 heures</MenuItem>
                        <MenuItem value={480}>8 heures</MenuItem>
                      </Select>
                    </FormControl>
                  </Grid>
                  <Grid item xs={12}>
                    <Button variant="outlined" color="primary">
                      Changer le mot de passe
                    </Button>
                  </Grid>
                </Grid>
              </Box>
            )}

            {/* Sauvegarde */}
            {activeTab === 'backup' && (
              <Box>
                <Typography variant="h6" gutterBottom>
                  Sauvegarde
                </Typography>
                <Grid container spacing={3}>
                  <Grid item xs={12}>
                    <FormControlLabel
                      control={
                        <Switch
                          checked={settings.backup.autoBackup}
                          onChange={(e) => setSettings({
                            ...settings,
                            backup: { ...settings.backup, autoBackup: e.target.checked }
                          })}
                        />
                      }
                      label="Sauvegarde automatique"
                    />
                  </Grid>
                  {settings.backup.autoBackup && (
                    <Grid item xs={12}>
                      <FormControl fullWidth>
                        <InputLabel>Fréquence</InputLabel>
                        <Select
                          value={settings.backup.backupFrequency}
                          label="Fréquence"
                          onChange={(e) => setSettings({
                            ...settings,
                            backup: { ...settings.backup, backupFrequency: e.target.value }
                          })}
                        >
                          <MenuItem value="hourly">Toutes les heures</MenuItem>
                          <MenuItem value="daily">Quotidienne</MenuItem>
                          <MenuItem value="weekly">Hebdomadaire</MenuItem>
                          <MenuItem value="monthly">Mensuelle</MenuItem>
                        </Select>
                      </FormControl>
                    </Grid>
                  )}
                  <Grid item xs={12}>
                    <Button
                      variant="outlined"
                      startIcon={<BackupIcon />}
                    >
                      Sauvegarder maintenant
                    </Button>
                  </Grid>
                </Grid>
              </Box>
            )}

            {/* Confidentialité */}
            {activeTab === 'privacy' && (
              <Box>
                <Typography variant="h6" gutterBottom>
                  Confidentialité
                </Typography>
                <Grid container spacing={2}>
                  <Grid item xs={12}>
                    <FormControlLabel
                      control={
                        <Switch
                          checked={settings.privacy.shareData}
                          onChange={(e) => setSettings({
                            ...settings,
                            privacy: { ...settings.privacy, shareData: e.target.checked }
                          })}
                        />
                      }
                      label="Partager mes données à des fins de recherche"
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <FormControlLabel
                      control={
                        <Switch
                          checked={settings.privacy.anonymousStats}
                          onChange={(e) => setSettings({
                            ...settings,
                            privacy: { ...settings.privacy, anonymousStats: e.target.checked }
                          })}
                        />
                      }
                      label="Contribuer aux statistiques anonymes"
                    />
                  </Grid>
                </Grid>
              </Box>
            )}

            <Divider sx={{ my: 3 }} />

            <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
              <Button
                variant="contained"
                size="large"
                startIcon={<SaveIcon />}
                onClick={handleSave}
              >
                Sauvegarder les paramètres
              </Button>
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Settings;