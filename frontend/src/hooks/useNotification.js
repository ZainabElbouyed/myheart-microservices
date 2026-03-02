import { useState, useCallback } from 'react';
import { useSnackbar } from 'notistack';

export const useNotification = () => {
  const { enqueueSnackbar } = useSnackbar();

  const showSuccess = useCallback((message) => {
    enqueueSnackbar(message, { variant: 'success' });
  }, [enqueueSnackbar]);

  const showError = useCallback((message) => {
    enqueueSnackbar(message, { variant: 'error' });
  }, [enqueueSnackbar]);

  const showWarning = useCallback((message) => {
    enqueueSnackbar(message, { variant: 'warning' });
  }, [enqueueSnackbar]);

  const showInfo = useCallback((message) => {
    enqueueSnackbar(message, { variant: 'info' });
  }, [enqueueSnackbar]);

  return {
    showSuccess,
    showError,
    showWarning,
    showInfo,
  };
};