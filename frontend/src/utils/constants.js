export const ROLES = {
  PATIENT: 'PATIENT',
  DOCTOR: 'DOCTOR',
  PHARMACIST: 'PHARMACIST',
  LAB_TECHNICIAN: 'LAB_TECHNICIAN',
};

export const APPOINTMENT_STATUS = {
  SCHEDULED: 'SCHEDULED',
  CONFIRMED: 'CONFIRMED',
  COMPLETED: 'COMPLETED',
  CANCELLED: 'CANCELLED',
  NO_SHOW: 'NO_SHOW',
};

export const LAB_STATUS = {
  PENDING: 'PENDING',
  IN_PROGRESS: 'IN_PROGRESS',
  COMPLETED: 'COMPLETED',
  REVIEWED: 'REVIEWED',
  ABNORMAL: 'ABNORMAL',
};

export const PRESCRIPTION_STATUS = {
  ACTIVE: 'ACTIVE',
  FILLED: 'FILLED',
  EXPIRED: 'EXPIRED',
  CANCELLED: 'CANCELLED',
};

export const PAYMENT_STATUS = {
  PENDING: 'PENDING',
  PAID: 'PAID',
  PARTIALLY_PAID: 'PARTIALLY_PAID',
  REFUNDED: 'REFUNDED',
};

export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    ME: '/auth/me',
    REFRESH: '/auth/refresh',
  },
  PATIENT: {
    BASE: '/patients',
    APPOINTMENTS: '/appointments/patient',
    PRESCRIPTIONS: '/prescriptions/patient',
    LAB_RESULTS: '/lab/patient',
  },
  DOCTOR: {
    BASE: '/doctors',
    PATIENTS: '/patients',
    APPOINTMENTS: '/appointments/doctor',
    PRESCRIPTIONS: '/prescriptions/doctor',
    LAB_RESULTS: '/lab/doctor',
  },
  PHARMACY: {
    MEDICINES: '/pharmacy/medicines',
    PRESCRIPTIONS: '/prescriptions',
    INVENTORY: '/pharmacy/inventory',
  },
  LAB: {
    RESULTS: '/lab/results',
    PENDING: '/lab/pending',
    UPLOAD: '/lab/upload',
  },
};

export const DATE_FORMATS = {
  DISPLAY: 'dd/MM/yyyy',
  DISPLAY_WITH_TIME: 'dd/MM/yyyy HH:mm',
  API: 'yyyy-MM-dd',
  API_WITH_TIME: "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
};

export const STORAGE_KEYS = {
  TOKEN: 'token',
  USER: 'user',
  THEME: 'theme',
  LANGUAGE: 'language',
};

export default {
  ROLES,
  APPOINTMENT_STATUS,
  LAB_STATUS,
  PRESCRIPTION_STATUS,
  PAYMENT_STATUS,
  API_ENDPOINTS,
  DATE_FORMATS,
  STORAGE_KEYS,
};