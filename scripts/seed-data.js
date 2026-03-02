const axios = require('axios');
const { faker } = require('@faker-js/faker');

const API_URL = 'http://localhost:8080/api';

const generatePatients = (count) => {
  const patients = [];
  for (let i = 0; i < count; i++) {
    patients.push({
      firstName: faker.person.firstName(),
      lastName: faker.person.lastName(),
      email: faker.internet.email(),
      phoneNumber: faker.phone.number('06########'),
      dateOfBirth: faker.date.birthdate({ min: 18, max: 90, mode: 'age' }).toISOString().split('T')[0],
      socialSecurityNumber: faker.string.numeric(13),
      bloodType: faker.helpers.arrayElement(['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-']),
      address: faker.location.streetAddress(),
      city: faker.location.city(),
      postalCode: faker.location.zipCode(),
    });
  }
  return patients;
};

const seedDatabase = async () => {
  console.log('🌱 Début du seeding de la base de données...');
  
  try {
    // Créer un admin
    const adminData = {
      firstName: 'Admin',
      lastName: 'System',
      email: 'admin@myheart.com',
      password: 'admin123',
      role: 'ADMIN'
    };
    
    try {
      await axios.post(`${API_URL}/auth/register`, adminData);
      console.log('✅ Admin créé');
    } catch (error) {
      console.log('ℹ️ Admin existe déjà');
    }
    
    // Créer des patients
    const patients = generatePatients(20);
    for (const patient of patients) {
      try {
        await axios.post(`${API_URL}/patients`, patient);
        console.log(`✅ Patient créé: ${patient.firstName} ${patient.lastName}`);
      } catch (error) {
        console.log(`❌ Erreur pour ${patient.email}: ${error.message}`);
      }
    }
    
    console.log('✅ Seeding terminé avec succès !');
  } catch (error) {
    console.error('❌ Erreur lors du seeding:', error);
  }
};

seedDatabase();