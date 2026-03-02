import React, { useState } from 'react';
import {
  Box,
  Typography,
  Paper,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  ListItemSecondaryAction,
  IconButton,
  Avatar,
  Chip,
  Button,
  Tabs,
  Tab,
  Divider,
  Badge,
} from '@mui/material';
import {
  Notifications as NotificationsIcon,
  CalendarToday as CalendarIcon,
  Science as ScienceIcon,
  Medication as MedicationIcon,
  Receipt as ReceiptIcon,
  Delete as DeleteIcon,
  CheckCircle as CheckCircleIcon,
  Warning as WarningIcon,
  Info as InfoIcon,
} from '@mui/icons-material';
import { formatDistanceToNow } from 'date-fns';
import { fr } from 'date-fns/locale';

const Notifications = () => {
  const [tabValue, setTabValue] = useState(0);
  const [notifications, setNotifications] = useState([
    {
      id: 1,
      type: 'appointment',
      title: 'Rendez-vous confirmé',
      message: 'Votre rendez-vous avec Dr. Martin est confirmé pour demain à 14h30',
      date: new Date(Date.now() - 3600000),
      read: false,
      important: true,
    },
    {
      id: 2,
      type: 'lab',
      title: 'Résultat disponible',
      message: 'Votre résultat d\'analyse sanguine est disponible',
      date: new Date(Date.now() - 86400000),
      read: false,
      important: false,
    },
    {
      id: 3,
      type: 'prescription',
      title: 'Prescription à renouveler',
      message: 'Votre prescription de Doliprane arrive à expiration',
      date: new Date(Date.now() - 172800000),
      read: true,
      important: true,
    },
    {
      id: 4,
      type: 'billing',
      title: 'Facture disponible',
      message: 'Votre facture de consultation est disponible',
      date: new Date(Date.now() - 259200000),
      read: true,
      important: false,
    },
  ]);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleMarkAsRead = (id) => {
    setNotifications(
      notifications.map((notif) =>
        notif.id === id ? { ...notif, read: true } : notif
      )
    );
  };

  const handleDelete = (id) => {
    setNotifications(notifications.filter((notif) => notif.id !== id));
  };

  const handleMarkAllAsRead = () => {
    setNotifications(
      notifications.map((notif) => ({ ...notif, read: true }))
    );
  };

  const getIcon = (type) => {
    switch (type) {
      case 'appointment':
        return <CalendarIcon />;
      case 'lab':
        return <ScienceIcon />;
      case 'prescription':
        return <MedicationIcon />;
      case 'billing':
        return <ReceiptIcon />;
      default:
        return <NotificationsIcon />;
    }
  };

  const getColor = (type) => {
    switch (type) {
      case 'appointment':
        return '#1976d2';
      case 'lab':
        return '#9c27b0';
      case 'prescription':
        return '#2e7d32';
      case 'billing':
        return '#ed6c02';
      default:
        return '#757575';
    }
  };

  const filteredNotifications = notifications.filter((notif) => {
    if (tabValue === 0) return true;
    if (tabValue === 1) return !notif.read;
    if (tabValue === 2) return notif.important;
    return true;
  });

  const unreadCount = notifications.filter((n) => !n.read).length;

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="bold">
          Notifications
        </Typography>
        {unreadCount > 0 && (
          <Button onClick={handleMarkAllAsRead}>
            Tout marquer comme lu
          </Button>
        )}
      </Box>

      <Paper sx={{ mb: 3 }}>
        <Tabs value={tabValue} onChange={handleTabChange} variant="fullWidth">
          <Tab
            label={
              <Badge badgeContent={unreadCount} color="error">
                Toutes
              </Badge>
            }
          />
          <Tab label="Non lues" />
          <Tab label="Importantes" />
        </Tabs>
      </Paper>

      <Paper>
        {filteredNotifications.length === 0 ? (
          <Box sx={{ p: 4, textAlign: 'center' }}>
            <NotificationsIcon sx={{ fontSize: 48, color: 'text.secondary', mb: 2 }} />
            <Typography variant="h6" color="text.secondary" gutterBottom>
              Aucune notification
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Vous n'avez pas de nouvelles notifications pour le moment.
            </Typography>
          </Box>
        ) : (
          <List>
            {filteredNotifications.map((notif, index) => (
              <React.Fragment key={notif.id}>
                <ListItem
                  alignItems="flex-start"
                  sx={{
                    bgcolor: notif.read ? 'transparent' : 'action.hover',
                    cursor: 'pointer',
                  }}
                  onClick={() => handleMarkAsRead(notif.id)}
                >
                  <ListItemIcon>
                    <Badge
                      color="error"
                      variant="dot"
                      invisible={notif.read}
                    >
                      <Avatar sx={{ bgcolor: getColor(notif.type) }}>
                        {getIcon(notif.type)}
                      </Avatar>
                    </Badge>
                  </ListItemIcon>
                  <ListItemText
                    primary={
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <Typography
                          variant="subtitle1"
                          fontWeight={notif.read ? 'normal' : 'bold'}
                        >
                          {notif.title}
                        </Typography>
                        {notif.important && (
                          <Chip
                            icon={<WarningIcon />}
                            label="Important"
                            color="error"
                            size="small"
                            sx={{ ml: 1 }}
                          />
                        )}
                      </Box>
                    }
                    secondary={
                      <>
                        <Typography
                          variant="body2"
                          color="text.primary"
                          component="span"
                        >
                          {notif.message}
                        </Typography>
                        <Typography
                          variant="caption"
                          color="text.secondary"
                          component="div"
                          sx={{ mt: 0.5 }}
                        >
                          {formatDistanceToNow(notif.date, { 
                            addSuffix: true,
                            locale: fr 
                          })}
                        </Typography>
                      </>
                    }
                  />
                  <ListItemSecondaryAction>
                    <IconButton
                      edge="end"
                      onClick={(e) => {
                        e.stopPropagation();
                        handleDelete(notif.id);
                      }}
                    >
                      <DeleteIcon />
                    </IconButton>
                  </ListItemSecondaryAction>
                </ListItem>
                {index < filteredNotifications.length - 1 && <Divider />}
              </React.Fragment>
            ))}
          </List>
        )}
      </Paper>
    </Box>
  );
};

export default Notifications;