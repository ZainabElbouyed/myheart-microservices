// scripts/init-mongodb.js

// ==================== LAB DATABASE ====================
db = db.getSiblingDB('labdb');

// Créer la collection lab_results
db.createCollection('lab_results');
db.lab_results.createIndex({ patientId: 1 });
db.lab_results.createIndex({ testDate: -1 });
db.lab_results.createIndex({ doctorId: 1 });
db.lab_results.createIndex({ status: 1 });
db.lab_results.createIndex({ patientId: 1, testDate: -1 });
db.lab_results.createIndex({ doctorId: 1, status: 1 });
db.lab_results.createIndex({ testType: 1 });
db.lab_results.createIndex({ technician_id: 1 });  

db.runCommand({
   collMod: "lab_results",
   validator: {
      $jsonSchema: {
         bsonType: "object",
         required: ["patientId", "testType", "status"],
         properties: {
            patientId: {
               bsonType: "string",
               description: "must be a string and is required"
            },
            testType: {
               bsonType: "string",
               description: "must be a string and is required"
            },
            status: {
               enum: ["PENDING", "IN_PROGRESS", "COMPLETED", "REVIEWED", "CANCELLED", "ABNORMAL"],
               description: "must be a valid status"
            },
            technician_id: {  
               bsonType: "string",
               description: "ID of the lab technician who performed the test"
            }
         }
      }
   }
});

db.lab_results.updateMany(
    {},
    { $set: { technician_id: null } }  
);

db.createCollection('lab_technicians');

// Index pour lab_technicians
db.lab_technicians.createIndex({ user_id: 1 }, { unique: true, sparse: true });
db.lab_technicians.createIndex({ email: 1 }, { unique: true });

// Insérer un technicien de test 
db.lab_technicians.insertOne({
    _id: ObjectId(),
    user_id: null,  
    firstName: "Claire",
    lastName: "Dubois",
    email: "claire.dubois@lab.com",
    phoneNumber: "0803040506",
    qualification: "Biologiste médical",
    employeeId: "LAB001",
    specialization: "Analyses sanguines",
    active: true,
    created_at: new Date(),
    updated_at: new Date()
});

print("✅ Collections et index créés dans labdb");
// ==================== PRESCRIPTION DATABASE ====================
db = db.getSiblingDB('prescriptiondb');

// Créer la collection prescriptions
db.createCollection('prescriptions');

// Index de base
db.prescriptions.createIndex({ patientId: 1 });
db.prescriptions.createIndex({ prescriptionDate: -1 });
db.prescriptions.createIndex({ doctorId: 1 });
db.prescriptions.createIndex({ status: 1 });
db.prescriptions.createIndex({ prescriptionNumber: 1 });
db.prescriptions.createIndex({ patientId: 1, status: 1 });
db.prescriptions.createIndex({ expiryDate: 1 });
db.prescriptions.createIndex({ patientId: 1, prescriptionDate: -1 });
db.prescriptions.createIndex({ doctorId: 1, prescriptionDate: -1 });

// Index composé pour les recherches
db.prescriptions.createIndex({ 
    "medications.name": "text", 
    "patientName": "text" 
});

print("✅ Collections et index créés dans prescriptiondb");

// ==================== NOTIFICATIONS DATABASE ====================
db = db.getSiblingDB('notificationsdb');

// Créer la collection notifications
db.createCollection('notifications');

// Index
db.notifications.createIndex({ userId: 1 });
db.notifications.createIndex({ createdAt: -1 });
db.notifications.createIndex({ type: 1 });
db.notifications.createIndex({ status: 1 });
db.notifications.createIndex({ userId: 1, createdAt: -1 });
db.notifications.createIndex({ userId: 1, status: 1 });
db.notifications.createIndex({ status: 1, retryCount: 1 });

print("✅ Collections et index créés dans notificationsdb");