import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Toaster } from 'react-hot-toast';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import frLocale from 'date-fns/locale/fr';

import { theme } from './styles/theme';
import { AuthProvider } from './contexts/AuthContext';
import PrivateRoute from './components/common/PrivateRoute';
import Layout from './components/common/Layout';

// Pages Auth
import Login from './components/auth/Login';
import Register from './components/auth/Register';

// Pages Patient
import PatientDashboard from './pages/patient/PatientDashboard';
import PatientAppointments from './pages/patient/Appointments';
import PatientPrescriptions from './pages/patient/Prescriptions';
import PatientLabResults from './pages/patient/LabResults';
import PatientMedicalRecords from './pages/patient/MedicalRecords';
import DoctorsList from './pages/patient/DoctorsList';

// Pages Docteur
import DoctorDashboard from './pages/doctor/DoctorDashboard';
import DoctorPatients from './pages/doctor/PatientList';
import DoctorAppointments from './pages/doctor/Appointments';
import DoctorPrescriptions from './pages/doctor/Prescriptions';
import DoctorLabResults from './pages/doctor/LabResultsReview';

// Pages Pharmacie
import PharmacyDashboard from './pages/pharmacy/PharmacyDashboard';
import PharmacyInventory from './pages/pharmacy/Inventory';
import PharmacyPrescriptions from './pages/pharmacy/PrescriptionFulfillment';
import PharmacyOrders from './pages/pharmacy/Orders';

// Pages Laboratoire
import LabDashboard from './pages/lab/LabDashboard';
import LabPending from './pages/lab/PendingTests';
import LabResults from './pages/lab/TestResults';
import LabUpload from './pages/lab/UploadResult';

import Profile from './pages/shared/Profile';
import Settings from './pages/shared/Settings';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 5 * 60 * 1000,
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <LocalizationProvider dateAdapter={AdapterDateFns} locale={frLocale}>
          <CssBaseline />
          <Toaster position="top-right" />
          <Router>
            <AuthProvider>
              <Routes>
                {/* Routes publiques */}
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                
                {/* Routes protégées avec Layout */}
                <Route path="/" element={<PrivateRoute><Layout /></PrivateRoute>}>
                  <Route index element={<Navigate to="/dashboard" />} />
                  
                  {/* Routes Patient */}
                  <Route path="patient/dashboard" element={
                    <PrivateRoute allowedRoles={['PATIENT']}>
                      <PatientDashboard />
                    </PrivateRoute>
                  } />
                  <Route path="patient/appointments" element={
                    <PrivateRoute allowedRoles={['PATIENT']}>
                      <PatientAppointments />
                    </PrivateRoute>
                  } />
                  <Route path="patient/prescriptions" element={
                    <PrivateRoute allowedRoles={['PATIENT']}>
                      <PatientPrescriptions />
                    </PrivateRoute>
                  } />
                  <Route path="patient/lab-results" element={
                    <PrivateRoute allowedRoles={['PATIENT']}>
                      <PatientLabResults />
                    </PrivateRoute>
                  } />
                  <Route path="patient/records" element={
                    <PrivateRoute allowedRoles={['PATIENT']}>
                      <PatientMedicalRecords />
                    </PrivateRoute>
                  } />
                  <Route path="/patient/doctors" element={<DoctorsList />} />

                  {/* Routes Docteur */}
                  <Route path="doctor/dashboard" element={
                    <PrivateRoute allowedRoles={['DOCTOR']}>
                      <DoctorDashboard />
                    </PrivateRoute>
                  } />
                  <Route path="doctor/patients" element={
                    <PrivateRoute allowedRoles={['DOCTOR']}>
                      <DoctorPatients />
                    </PrivateRoute>
                  } />
                  <Route path="doctor/appointments" element={
                    <PrivateRoute allowedRoles={['DOCTOR']}>
                      <DoctorAppointments />
                    </PrivateRoute>
                  } />
                  <Route path="doctor/prescriptions" element={
                    <PrivateRoute allowedRoles={['DOCTOR']}>
                      <DoctorPrescriptions />
                    </PrivateRoute>
                  } />
                  <Route path="doctor/lab-results" element={
                    <PrivateRoute allowedRoles={['DOCTOR']}>
                      <DoctorLabResults />
                    </PrivateRoute>
                  } />
                  
                  {/* Routes Pharmacie */}
                  <Route path="pharmacy/dashboard" element={
                    <PrivateRoute allowedRoles={['PHARMACIST']}>
                      <PharmacyDashboard />
                    </PrivateRoute>
                  } />
                  <Route path="pharmacy/inventory" element={
                    <PrivateRoute allowedRoles={['PHARMACIST']}>
                      <PharmacyInventory />
                    </PrivateRoute>
                  } />
                  <Route path="pharmacy/prescriptions" element={
                    <PrivateRoute allowedRoles={['PHARMACIST']}>
                      <PharmacyPrescriptions />
                    </PrivateRoute>
                  } />
                  <Route path="pharmacy/orders" element={
                    <PrivateRoute allowedRoles={['PHARMACIST']}>
                      <PharmacyOrders />
                    </PrivateRoute>
                  } />
                  
                  {/* Routes Laboratoire */}
                  <Route path="lab/dashboard" element={
                    <PrivateRoute allowedRoles={['LAB_TECHNICIAN']}>
                      <LabDashboard />
                    </PrivateRoute>
                  } />
                  <Route path="lab/pending" element={
                    <PrivateRoute allowedRoles={['LAB_TECHNICIAN']}>
                      <LabPending />
                    </PrivateRoute>
                  } />
                  <Route path="lab/results" element={
                    <PrivateRoute allowedRoles={['LAB_TECHNICIAN']}>
                      <LabResults />
                    </PrivateRoute>
                  } />
                  <Route path="lab/upload" element={
                    <PrivateRoute allowedRoles={['LAB_TECHNICIAN']}>
                      <LabUpload />
                    </PrivateRoute>
                  } />

                  <Route path="profile" element={
                    <PrivateRoute>
                      <Profile />
                    </PrivateRoute>
                  } />
                  <Route path="settings" element={
                    <PrivateRoute>
                      <Settings />
                    </PrivateRoute>
                  } />
                </Route>
              </Routes>
            </AuthProvider>
          </Router>
        </LocalizationProvider>
      </ThemeProvider>
    </QueryClientProvider>
  );
}

export default App;