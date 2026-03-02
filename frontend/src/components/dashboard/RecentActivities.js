import React from 'react';
import {
  Card,
  CardContent,
  Typography,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Avatar,
  Box,
} from '@mui/material';
import {
  Assignment as AssignmentIcon,
  Person as PersonIcon,
  CalendarToday as CalendarIcon,
  Science as ScienceIcon,
} from '@mui/icons-material';

const getIcon = (type) => {
  switch (type) {
    case 'appointment':
      return <CalendarIcon />;
    case 'patient':
      return <PersonIcon />;
    case 'lab':
      return <ScienceIcon />;
    default:
      return <AssignmentIcon />;
  }
};

const getColor = (type) => {
  switch (type) {
    case 'appointment':
      return '#1976d2';
    case 'patient':
      return '#2e7d32';
    case 'lab':
      return '#9c27b0';
    default:
      return '#ed6c02';
  }
};

const RecentActivities = ({ activities = [] }) => {
  return (
    <Card>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Activités récentes
        </Typography>
        <List>
          {activities.length > 0 ? (
            activities.map((activity, index) => (
              <ListItem
                key={index}
                sx={{
                  borderBottom: '1px solid #eee',
                  '&:last-child': { borderBottom: 'none' },
                }}
              >
                <ListItemIcon>
                  <Avatar sx={{ bgcolor: getColor(activity.type), width: 32, height: 32 }}>
                    {getIcon(activity.type)}
                  </Avatar>
                </ListItemIcon>
                <ListItemText
                  primary={activity.action}
                  secondary={
                    <Box component="span">
                      <Typography variant="caption" color="text.secondary">
                        Par {activity.user} • {activity.time}
                      </Typography>
                    </Box>
                  }
                />
              </ListItem>
            ))
          ) : (
            <Typography color="text.secondary">
              Aucune activité récente
            </Typography>
          )}
        </List>
      </CardContent>
    </Card>
  );
};

export default RecentActivities;