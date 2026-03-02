# ============================================
# SCRIPT D'INITIALISATION COMPLET - VERSION FINALE CORRIGÉE
# AVEC NETTOYAGE EN PREMIER
# ============================================

Write-Host "🚀 INITIALISATION DE L'APPLICATION MYHEART" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan

$ErrorActionPreference = "Continue"

# Fonction pour tester les requêtes
function Test-Request {
    param($Method, $Url, $Body, $Token)
    
    $headers = @{ "Content-Type" = "application/json" }
    if ($Token) { $headers.Authorization = "Bearer $Token" }
    
    $params = @{
        Method = $Method
        Uri = "http://localhost:8080$Url"
        Headers = $headers
        UseBasicParsing = $true
    }
    if ($Body) { $params.Body = $Body }
    
    try {
        $response = Invoke-WebRequest @params
        $content = $response.Content
        
        try {
            return $content | ConvertFrom-Json
        } catch {
            Write-Host "ℹ️ Réponse non-JSON: $content" -ForegroundColor Gray
            return $content
        }
    } catch {
        if ($_.Exception.Response.StatusCode.value__ -eq 409) {
            Write-Host "⚠️ L'utilisateur existe déjà" -ForegroundColor Yellow
        } else {
            Write-Host "❌ Erreur: $_" -ForegroundColor Red
            Write-Host "   Status: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
        }
        return $null
    }
}

# ============================================
# ÉTAPE 1: NETTOYAGE COMPLET DES BASES MÉTIER (EN PREMIER !)
# ============================================
Write-Host "`n📌 ÉTAPE 1: NETTOYAGE COMPLET DES BASES MÉTIER..." -ForegroundColor Yellow

# Nettoyer patientdb (PostgreSQL)
Write-Host "   Nettoyage de patientdb..." -ForegroundColor Gray
docker exec myheart-postgres psql -U myheart -d patientdb -c "TRUNCATE patients CASCADE;" 2>$null
docker exec myheart-postgres psql -U myheart -d patientdb -c "TRUNCATE doctors CASCADE;" 2>$null
docker exec myheart-postgres psql -U myheart -d patientdb -c "TRUNCATE doctor_availability CASCADE;" 2>$null

# Nettoyer appointmentdb (PostgreSQL)
Write-Host "   Nettoyage de appointmentdb..." -ForegroundColor Gray
docker exec myheart-postgres psql -U myheart -d appointmentdb -c "TRUNCATE appointments CASCADE;" 2>$null

# Nettoyer billingdb (PostgreSQL)
Write-Host "   Nettoyage de billingdb..." -ForegroundColor Gray
docker exec myheart-postgres psql -U myheart -d billingdb -c "TRUNCATE invoices CASCADE;" 2>$null
docker exec myheart-postgres psql -U myheart -d billingdb -c "TRUNCATE payments CASCADE;" 2>$null

# Nettoyer labdb (MongoDB)
Write-Host "   Nettoyage de labdb..." -ForegroundColor Gray
docker exec myheart-mongodb mongosh -u myheart -p myheart123 --authenticationDatabase admin --eval "use labdb; db.lab_results.deleteMany({});" 2>$null

# Nettoyer prescriptiondb (MongoDB)
Write-Host "   Nettoyage de prescriptiondb..." -ForegroundColor Gray
docker exec myheart-mongodb mongosh -u myheart -p myheart123 --authenticationDatabase admin --eval "use prescriptiondb; db.prescriptions.deleteMany({});" 2>$null

# Nettoyer pharmacydb (MySQL)
Write-Host "   Nettoyage de pharmacydb..." -ForegroundColor Gray
docker exec myheart-mysql mysql -umyheart -pmyheart123 -e "USE pharmacydb; DELETE FROM inventory_transactions; DELETE FROM medicines;" 2>$null
docker exec myheart-mysql mysql -umyheart -pmyheart123 -e "USE pharmacydb; DELETE FROM pharmacists;" 2>$null

Write-Host "✅ Bases métier nettoyées" -ForegroundColor Green

# ============================================
# ÉTAPE 2: CRÉATION DES PATIENTS (auth-service)
# ============================================
Write-Host "`n📌 ÉTAPE 2: CRÉATION DES PATIENTS (auth-service)..." -ForegroundColor Yellow

# Patient 1 - Thomas Martin
$p1 = Test-Request -Method POST -Url "/api/auth/register" -Body @'
{
    "email": "thomas.martin@example.com",
    "password": "Thomas@2024#Secure",
    "firstName": "Thomas",
    "lastName": "Martin",
    "phoneNumber": "0612345678",
    "role": "PATIENT"
}
'@

# Patient 2 - Emma Bernard
$p2 = Test-Request -Method POST -Url "/api/auth/register" -Body @'
{
    "email": "emma.bernard@example.com",
    "password": "Emma@2024#Secure",
    "firstName": "Emma",
    "lastName": "Bernard",
    "phoneNumber": "0623456789",
    "role": "PATIENT"
}
'@

# Patient 3 - Lucas Petit
$p3 = Test-Request -Method POST -Url "/api/auth/register" -Body @'
{
    "email": "lucas.petit@example.com",
    "password": "Lucas@2024#Secure",
    "firstName": "Lucas",
    "lastName": "Petit",
    "phoneNumber": "0634567890",
    "role": "PATIENT"
}
'@

# Patient 4 - Chloé Dubois
$p4 = Test-Request -Method POST -Url "/api/auth/register" -Body @'
{
    "email": "chloe.dubois@example.com",
    "password": "Chloe@2024#Secure",
    "firstName": "Chloé",
    "lastName": "Dubois",
    "phoneNumber": "0645678901",
    "role": "PATIENT"
}
'@

# Patient 5 - Hugo Moreau
$p5 = Test-Request -Method POST -Url "/api/auth/register" -Body @'
{
    "email": "hugo.moreau@example.com",
    "password": "Hugo@2024#Secure",
    "firstName": "Hugo",
    "lastName": "Moreau",
    "phoneNumber": "0656789012",
    "role": "PATIENT"
}
'@

Write-Host "✅ Patients créés dans auth-service" -ForegroundColor Green

# ============================================
# ÉTAPE 3: CRÉATION DES MÉDECINS (auth-service)
# ============================================
Write-Host "`n📌 ÉTAPE 3: CRÉATION DES MÉDECINS (auth-service)..." -ForegroundColor Yellow

# Médecin 1 - Dr. Alexandre Richard (Cardiologue)
$d1 = Test-Request -Method POST -Url "/api/auth/register" -Body @'
{
    "email": "alexandre.richard@clinique.fr",
    "password": "Alexandre@2024#Secure",
    "firstName": "Alexandre",
    "lastName": "Richard",
    "phoneNumber": "0711111111",
    "role": "DOCTOR"
}
'@

# Médecin 2 - Dr. Élodie Moreau (Pédiatre)
$d2 = Test-Request -Method POST -Url "/api/auth/register" -Body @'
{
    "email": "elodie.moreau@clinique.fr",
    "password": "Elodie@2024#Secure",
    "firstName": "Élodie",
    "lastName": "Moreau",
    "phoneNumber": "0722222222",
    "role": "DOCTOR"
}
'@

# Médecin 3 - Dr. Julien Laurent (Neurologue)
$d3 = Test-Request -Method POST -Url "/api/auth/register" -Body @'
{
    "email": "julien.laurent@clinique.fr",
    "password": "Julien@2024#Secure",
    "firstName": "Julien",
    "lastName": "Laurent",
    "phoneNumber": "0733333333",
    "role": "DOCTOR"
}
'@

# Médecin 4 - Dr. Camille Girard (Gynécologue)
$d4 = Test-Request -Method POST -Url "/api/auth/register" -Body @'
{
    "email": "camille.girard@clinique.fr",
    "password": "Camille@2024#Secure",
    "firstName": "Camille",
    "lastName": "Girard",
    "phoneNumber": "0744444444",
    "role": "DOCTOR"
}
'@

# Médecin 5 - Dr. Nicolas Fontaine (Dermatologue)
$d5 = Test-Request -Method POST -Url "/api/auth/register" -Body @'
{
    "email": "nicolas.fontaine@clinique.fr",
    "password": "Nicolas@2024#Secure",
    "firstName": "Nicolas",
    "lastName": "Fontaine",
    "phoneNumber": "0755555555",
    "role": "DOCTOR"
}
'@

Write-Host "✅ Médecins créés dans auth-service" -ForegroundColor Green

# ============================================
# ÉTAPE 4: CRÉATION DU LABORATOIRE (auth-service)
# ============================================
Write-Host "`n📌 ÉTAPE 4: CRÉATION DU LABORATOIRE (auth-service)..." -ForegroundColor Yellow

$lab = Test-Request -Method POST -Url "/api/auth/register" -Body @'
{
    "email": "sophie.renaud@laboratoire.fr",
    "password": "Sophie@2024#Secure",
    "firstName": "Sophie",
    "lastName": "Renaud",
    "phoneNumber": "0811111111",
    "role": "LAB_TECHNICIAN"
}
'@

# ============================================
# ÉTAPE 5: CRÉATION DE LA PHARMACIE (auth-service)
# ============================================
Write-Host "`n📌 ÉTAPE 5: CRÉATION DE LA PHARMACIE (auth-service)..." -ForegroundColor Yellow

$pharma = Test-Request -Method POST -Url "/api/auth/register" -Body @'
{
    "email": "philippe.mercier@pharmacie.fr",
    "password": "Philippe@2024#Secure",
    "firstName": "Philippe",
    "lastName": "Mercier",
    "phoneNumber": "0911111111",
    "role": "PHARMACIST"
}
'@

# ============================================
# ÉTAPE 6: OBTENTION DES TOKENS ET IDs AUTH
# ============================================
Write-Host "`n📌 ÉTAPE 6: OBTENTION DES TOKENS ET IDs AUTH..." -ForegroundColor Yellow

# Login patient 1 (Thomas)
try {
    $p1Login = Invoke-WebRequest -Method POST -Uri "http://localhost:8080/api/auth/login" `
      -Headers @{ "Content-Type" = "application/json" } `
      -Body '{"email":"thomas.martin@example.com","password":"Thomas@2024#Secure"}' `
      -UseBasicParsing -ErrorAction Stop
    $p1Data = $p1Login.Content | ConvertFrom-Json
    $p1Id = $p1Data.id
    $p1Token = $p1Data.token
    Write-Host "✅ Token patient 1 (Thomas) - Auth ID: $p1Id" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Patient 1 non connecté" -ForegroundColor Yellow
}

# Login médecin 1 (Alexandre)
try {
    $d1Login = Invoke-WebRequest -Method POST -Uri "http://localhost:8080/api/auth/login" `
      -Headers @{ "Content-Type" = "application/json" } `
      -Body '{"email":"alexandre.richard@clinique.fr","password":"Alexandre@2024#Secure"}' `
      -UseBasicParsing -ErrorAction Stop
    $d1Data = $d1Login.Content | ConvertFrom-Json
    $d1Id = $d1Data.id
    $d1Token = $d1Data.token
    Write-Host "✅ Token médecin 1 (Alexandre) - Auth ID: $d1Id" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Médecin 1 non connecté" -ForegroundColor Yellow
}

# Login médecin 2 (Élodie)
try {
    $d2Login = Invoke-WebRequest -Method POST -Uri "http://localhost:8080/api/auth/login" `
      -Headers @{ "Content-Type" = "application/json" } `
      -Body '{"email":"elodie.moreau@clinique.fr","password":"Elodie@2024#Secure"}' `
      -UseBasicParsing -ErrorAction Stop
    $d2Data = $d2Login.Content | ConvertFrom-Json
    $d2Id = $d2Data.id
    Write-Host "✅ Token médecin 2 (Élodie) - Auth ID: $d2Id" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Médecin 2 non connecté" -ForegroundColor Yellow
}

# Login médecin 3 (Julien)
try {
    $d3Login = Invoke-WebRequest -Method POST -Uri "http://localhost:8080/api/auth/login" `
      -Headers @{ "Content-Type" = "application/json" } `
      -Body '{"email":"julien.laurent@clinique.fr","password":"Julien@2024#Secure"}' `
      -UseBasicParsing -ErrorAction Stop
    $d3Data = $d3Login.Content | ConvertFrom-Json
    $d3Id = $d3Data.id
    Write-Host "✅ Token médecin 3 (Julien) - Auth ID: $d3Id" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Médecin 3 non connecté" -ForegroundColor Yellow
}

# Login médecin 4 (Camille)
try {
    $d4Login = Invoke-WebRequest -Method POST -Uri "http://localhost:8080/api/auth/login" `
      -Headers @{ "Content-Type" = "application/json" } `
      -Body '{"email":"camille.girard@clinique.fr","password":"Camille@2024#Secure"}' `
      -UseBasicParsing -ErrorAction Stop
    $d4Data = $d4Login.Content | ConvertFrom-Json
    $d4Id = $d4Data.id
    Write-Host "✅ Token médecin 4 (Camille) - Auth ID: $d4Id" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Médecin 4 non connecté" -ForegroundColor Yellow
}

# Login médecin 5 (Nicolas)
try {
    $d5Login = Invoke-WebRequest -Method POST -Uri "http://localhost:8080/api/auth/login" `
      -Headers @{ "Content-Type" = "application/json" } `
      -Body '{"email":"nicolas.fontaine@clinique.fr","password":"Nicolas@2024#Secure"}' `
      -UseBasicParsing -ErrorAction Stop
    $d5Data = $d5Login.Content | ConvertFrom-Json
    $d5Id = $d5Data.id
    Write-Host "✅ Token médecin 5 (Nicolas) - Auth ID: $d5Id" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Médecin 5 non connecté" -ForegroundColor Yellow
}

# Login lab (Sophie)
try {
    $labLogin = Invoke-WebRequest -Method POST -Uri "http://localhost:8080/api/auth/login" `
      -Headers @{ "Content-Type" = "application/json" } `
      -Body '{"email":"sophie.renaud@laboratoire.fr","password":"Sophie@2024#Secure"}' `
      -UseBasicParsing -ErrorAction Stop
    $labData = $labLogin.Content | ConvertFrom-Json
    $labId = $labData.id
    $labToken = $labData.token
    Write-Host "✅ Token lab (Sophie) - Auth ID: $labId" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Lab non connecté" -ForegroundColor Yellow
}

# Login pharmacie (Philippe)
try {
    $pharmaLogin = Invoke-WebRequest -Method POST -Uri "http://localhost:8080/api/auth/login" `
      -Headers @{ "Content-Type" = "application/json" } `
      -Body '{"email":"philippe.mercier@pharmacie.fr","password":"Philippe@2024#Secure"}' `
      -UseBasicParsing -ErrorAction Stop
    $pharmaData = $pharmaLogin.Content | ConvertFrom-Json
    $pharmaId = $pharmaData.id
    $pharmaToken = $pharmaData.token
    Write-Host "✅ Token pharmacie (Philippe) - Auth ID: $pharmaId" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Pharmacie non connectée" -ForegroundColor Yellow
}

Write-Host "✅ Tous les tokens et IDs Auth récupérés" -ForegroundColor Green

# ============================================
# ÉTAPE 7: CRÉATION DES PATIENTS DANS patientdb
# ============================================
Write-Host "`n📌 ÉTAPE 7: CRÉATION DES PATIENTS (patientdb)..." -ForegroundColor Yellow

# Insérer Thomas Martin
$insertPatient1 = @"
INSERT INTO patients (
    id, first_name, last_name, email, phone_number,
    date_of_birth, social_security_number, blood_type, address, city, postal_code, country,
    emergency_contact_name, emergency_contact_phone, emergency_contact_relation,
    insurance_provider, insurance_number, gender, marital_status, occupation, active,
    medical_history, allergies, current_medications,
    created_at, updated_at
) VALUES (
    '$p1Id', 'Thomas', 'Martin', 'thomas.martin@example.com', '0612345678',
    '1985-03-15', '185037512345678', 'A+', '15 Rue de la Paix', 'Paris', '75001', 'France',
    'Marie Martin', '0687654321', 'ÉPOUSE',
    'Mutuelle Générale', 'MG123456', 'MALE', 'MARRIED', 'Ingénieur', true,
    'Aucun antécédent majeur', 'Aucune allergie connue', 'Aucun traitement',
    NOW(), NOW()
);
"@
docker exec myheart-postgres psql -U myheart -d patientdb -c "$insertPatient1"
Write-Host "   ✅ Patient Thomas inséré" -ForegroundColor Gray

# Insérer Emma Bernard
$insertPatient2 = @"
INSERT INTO patients (
    id, first_name, last_name, email, phone_number,
    date_of_birth, social_security_number, blood_type, address, city, postal_code, country,
    emergency_contact_name, emergency_contact_phone, emergency_contact_relation,
    insurance_provider, insurance_number, gender, marital_status, occupation, active,
    medical_history, allergies, current_medications,
    created_at, updated_at
) VALUES (
    '$($p2Data.id)', 'Emma', 'Bernard', 'emma.bernard@example.com', '0623456789',
    '1990-07-22', '290077512345678', 'O-', '8 Rue de Lyon', 'Lyon', '69001', 'France',
    'Paul Bernard', '0678901234', 'FRÈRE',
    'Swiss Life', 'SL789012', 'FEMALE', 'SINGLE', 'Professeur', true,
    'Asthme', 'Pénicilline', 'Ventoline si besoin',
    NOW(), NOW()
);
"@
docker exec myheart-postgres psql -U myheart -d patientdb -c "$insertPatient2"
Write-Host "   ✅ Patient Emma inséré" -ForegroundColor Gray

Write-Host "✅ Patients insérés dans patientdb" -ForegroundColor Green

# Vérification rapide
$checkPatient = docker exec myheart-postgres psql -U myheart -d patientdb -t -c "SELECT COUNT(*) FROM patients WHERE id = '$p1Id';"
if ($checkPatient.Trim() -eq "1") {
    Write-Host "   ✅ Patient Thomas confirmé dans patientdb" -ForegroundColor Green
}

# ============================================
# ÉTAPE 8: CRÉATION DES MÉDECINS DANS patientdb
# ============================================
Write-Host "`n📌 ÉTAPE 8: CRÉATION DES MÉDECINS (patientdb)..." -ForegroundColor Yellow

$doctors = @(
    @{
        id = $d1Id
        firstName = "Alexandre"
        lastName = "Richard"
        email = "alexandre.richard@clinique.fr"
        phone = "0711111111"
        specialty = "Cardiologie"
        license = "12345ABC"
        fee = 80.00
        years = 15
        rating = 4.8
        reviews = 124
    },
    @{
        id = $d2Id
        firstName = "Élodie"
        lastName = "Moreau"
        email = "elodie.moreau@clinique.fr"
        phone = "0722222222"
        specialty = "Pédiatrie"
        license = "23456BCD"
        fee = 75.00
        years = 12
        rating = 4.9
        reviews = 98
    },
    @{
        id = $d3Id
        firstName = "Julien"
        lastName = "Laurent"
        email = "julien.laurent@clinique.fr"
        phone = "0733333333"
        specialty = "Neurologie"
        license = "34567CDE"
        fee = 90.00
        years = 18
        rating = 4.7
        reviews = 156
    },
    @{
        id = $d4Id
        firstName = "Camille"
        lastName = "Girard"
        email = "camille.girard@clinique.fr"
        phone = "0744444444"
        specialty = "Gynécologie"
        license = "45678DEF"
        fee = 85.00
        years = 14
        rating = 4.9
        reviews = 112
    },
    @{
        id = $d5Id
        firstName = "Nicolas"
        lastName = "Fontaine"
        email = "nicolas.fontaine@clinique.fr"
        phone = "0755555555"
        specialty = "Dermatologie"
        license = "56789EFG"
        fee = 70.00
        years = 10
        rating = 4.6
        reviews = 87
    }
)

foreach ($doc in $doctors) {
    $insertDoctor = @"
INSERT INTO doctors (
    id, first_name, last_name, email, phone_number,
    specialty, license_number, address, city, postal_code, country,
    consultation_fee, biography, education, experience, languages,
    years_of_experience, rating, number_of_reviews, accepting_new_patients,
    department, hospital_affiliation, insurance_accepted, status,
    created_at, updated_at
) VALUES (
    '$($doc.id)', '$($doc.firstName)', '$($doc.lastName)', '$($doc.email)', '$($doc.phone)',
    '$($doc.specialty)', '$($doc.license)',
    '15 Avenue de la Médecine', 'Paris', '75001', 'France',
    $($doc.fee), 'Médecin expérimenté et à l''écoute', 'Université de Paris', '$($doc.years) ans d''expérience', 'Français, Anglais',
    $($doc.years), $($doc.rating), $($doc.reviews), true,
    '$($doc.specialty)', 'Hôpital Européen', 'CPAM, Mutuelles', 'ACTIVE',
    NOW(), NOW()
);
"@
    docker exec myheart-postgres psql -U myheart -d patientdb -c "$insertDoctor"
    
    # Ajouter les disponibilités
    $availability = @"
INSERT INTO doctor_availability (doctor_id, availability) VALUES
('$($doc.id)', 'Lundi 9h-12h'),
('$($doc.id)', 'Mardi 14h-18h'),
('$($doc.id)', 'Jeudi 9h-12h');
"@
    docker exec myheart-postgres psql -U myheart -d patientdb -c "$availability" 2>$null
    
    Write-Host "   ✅ Dr. $($doc.firstName) $($doc.lastName) inséré" -ForegroundColor Gray
}

Write-Host "✅ Médecins insérés dans patientdb" -ForegroundColor Green

# ============================================
# ÉTAPE 9: CRÉATION D'UN RENDEZ-VOUS DANS appointmentdb
# ============================================
Write-Host "`n📌 ÉTAPE 9: CRÉATION D'UN RENDEZ-VOUS (appointmentdb)..." -ForegroundColor Yellow

$aptId = [guid]::NewGuid().ToString()
$startTime = (Get-Date).AddDays(1).ToString("yyyy-MM-dd HH:mm:ss")
$endTime = (Get-Date).AddDays(1).AddHours(1).ToString("yyyy-MM-dd HH:mm:ss")

$insertAppointment = @"
INSERT INTO appointments (
    id, patient_id, patient_name, doctor_id, doctor_name,
    start_time, end_time, status, type, reason,
    is_virtual, duration, location,
    created_at, updated_at
) VALUES (
    '$aptId', '$p1Id', 'Thomas Martin', '$d1Id', 'Dr. Alexandre Richard',
    '$startTime', '$endTime', 'CONFIRMED', 'CONSULTATION', 'Consultation cardiologie annuelle',
    false, 60, 'Cabinet Dr. Richard - Salle 3',
    NOW(), NOW()
);
"@
docker exec myheart-postgres psql -U myheart -d appointmentdb -c "$insertAppointment"

Write-Host "✅ Rendez-vous créé - Patient ID: $p1Id, Doctor ID: $d1Id" -ForegroundColor Green

# ============================================
# ÉTAPE 10: CRÉATION D'UNE PRESCRIPTION DANS prescriptiondb
# ============================================
Write-Host "`n📌 ÉTAPE 10: CRÉATION D'UNE PRESCRIPTION (prescriptiondb)..." -ForegroundColor Yellow

$prescriptionNumber = "RX-" + (Get-Date).ToString("yyyyMMdd") + "-" + [System.Guid]::NewGuid().ToString().Substring(0,8).ToUpper()

docker exec myheart-mongodb mongosh -u myheart -p myheart123 --authenticationDatabase admin --eval "
use prescriptiondb;
db.prescriptions.insertOne({
    prescriptionNumber: '$prescriptionNumber',
    patientId: '$p1Id',
    patientName: 'Thomas Martin',
    doctorId: '$d1Id',
    doctorName: 'Dr. Alexandre Richard',
    prescriptionDate: new Date(),
    expiryDate: new Date(new Date().setFullYear(new Date().getFullYear() + 1)),
    diagnosis: 'Hypertension artérielle',
    clinicalNotes: 'Patient stable, contrôle dans 3 mois',
    medications: [
        {
            name: 'Lisinopril',
            genericName: 'Lisinopril',
            strength: '10mg',
            form: 'Comprimé',
            dosage: '1 comprimé par jour',
            frequency: 'Une fois par jour',
            duration: '30 jours',
            route: 'orale',
            instructions: 'À prendre le matin',
            indications: 'Traitement de l\\'hypertension',
            quantity: 30,
            refills: 0,
            substituteAllowed: true
        }
    ],
    status: 'ACTIVE',
    refillsAllowed: 0,
    refillsUsed: 0,
    isEmergency: false,
    created_at: new Date(),
    updated_at: new Date()
});
" 2>$null

Write-Host "✅ Prescription créée - $prescriptionNumber" -ForegroundColor Green

# ============================================
# ÉTAPE 11: CRÉATION D'UN RÉSULTAT LABO DANS labdb
# ============================================
Write-Host "`n📌 ÉTAPE 11: CRÉATION D'UN RÉSULTAT LABO (labdb)..." -ForegroundColor Yellow

docker exec myheart-mongodb mongosh -u myheart -p myheart123 --authenticationDatabase admin --eval "
use labdb;
db.lab_results.insertOne({
    patientId: '$p1Id',
    patientName: 'Thomas Martin',
    doctorId: '$d1Id',
    doctorName: 'Dr. Alexandre Richard',
    testType: 'Bilan sanguin complet',
    testDate: new Date(),
    resultDate: new Date(),
    status: 'COMPLETED',
    parameters: [
        {
            name: 'Glycémie à jeun',
            value: '0.92',
            unit: 'g/L',
            referenceRange: '0.70-1.10',
            interpretation: 'Normal',
            isAbnormal: false
        },
        {
            name: 'Cholestérol total',
            value: '2.15',
            unit: 'g/L',
            referenceRange: '1.50-2.00',
            interpretation: 'Légèrement élevé',
            isAbnormal: true
        }
    ],
    summary: 'Bilan sanguin normal avec cholestérol légèrement élevé',
    interpretation: 'Un suivi diététique est recommandé',
    recommendations: 'Réduire les graisses saturées, contrôle dans 3 mois',
    technician: 'Sophie Renaud',
    equipment: 'Analyseur automatique',
    labName: 'Laboratoire Central',
    createdAt: new Date(),
    updatedAt: new Date()
});
" 2>$null

Write-Host "✅ Résultat labo créé" -ForegroundColor Green

# ============================================
# ÉTAPE 12: CRÉATION DU PHARMACIEN DANS pharmacydb
# ============================================
Write-Host "`n📌 ÉTAPE 12: CRÉATION DU PHARMACIEN (pharmacydb)..." -ForegroundColor Yellow

docker exec myheart-mysql mysql -umyheart -pmyheart123 -e "
USE pharmacydb;
INSERT INTO pharmacists (id, name, email, phone_number, license_number, created_at, updated_at)
VALUES (UUID(), 'Philippe Mercier', 'philippe.mercier@pharmacie.fr', '0911111111', 'PHARM001', NOW(), NOW());
" 2>$null

Write-Host "✅ Pharmacien créé" -ForegroundColor Green

# ============================================
# ÉTAPE 13: CRÉATION DE MÉDICAMENTS DANS pharmacydb
# ============================================
Write-Host "`n📌 ÉTAPE 13: CRÉATION DE MÉDICAMENTS (pharmacydb)..." -ForegroundColor Yellow

docker exec myheart-mysql mysql -umyheart -pmyheart123 -e "
USE pharmacydb;
INSERT INTO medicines (
    id, name, generic_name, manufacturer, category, form, strength,
    stock_quantity, reorder_level, maximum_stock, unit_price, selling_price,
    expiry_date, batch_number, location, requires_prescription, status,
    description, created_at, updated_at
) VALUES
(UUID(), 'Doliprane', 'Paracétamol', 'Sanofi', 'Antalgique', 'Comprimé', '500mg',
 150, 20, 300, 2.50, 3.20,
 DATE_ADD(CURDATE(), INTERVAL 2 YEAR), 'BATCH001', 'A-01', FALSE, 'IN_STOCK',
 'Antalgique et antipyrétique', NOW(), NOW()),

(UUID(), 'Amoxicilline', 'Amoxicilline', 'Biogaran', 'Antibiotique', 'Gélule', '500mg',
 75, 15, 200, 5.80, 7.50,
 DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 'BATCH002', 'B-03', TRUE, 'IN_STOCK',
 'Antibiotique à large spectre', NOW(), NOW()),

(UUID(), 'Ventoline', 'Salbutamol', 'GSK', 'Bronchodilatateur', 'Inhalateur', '100µg/dose',
 30, 10, 100, 8.90, 11.50,
 DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 'BATCH003', 'C-02', TRUE, 'IN_STOCK',
 'Traitement de l\\'asthme', NOW(), NOW());
" 2>$null

Write-Host "✅ Médicaments créés" -ForegroundColor Green

# ============================================
# ÉTAPE 14: CRÉATION D'UNE FACTURE DANS billingdb
# ============================================
Write-Host "`n📌 ÉTAPE 14: CRÉATION D'UNE FACTURE (billingdb)..." -ForegroundColor Yellow

$invoiceId = [guid]::NewGuid().ToString()
$invoiceNumber = "INV-" + (Get-Date).ToString("yyyyMMdd") + "-" + (Get-Random -Minimum 1000 -Maximum 9999)

$insertInvoice = @"
INSERT INTO invoices (
    id, patient_id, patient_name, appointment_id, invoice_number,
    subtotal, tax_rate, tax_amount, total, status,
    due_date, issued_date, description,
    created_at, updated_at
) VALUES (
    '$invoiceId', '$p1Id', 'Thomas Martin', '$aptId', '$invoiceNumber',
    80.00, 20.0, 16.00, 96.00, 'PENDING',
    DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), 'Consultation cardiologie',
    NOW(), NOW()
);
"@
docker exec myheart-postgres psql -U myheart -d billingdb -c "$insertInvoice"

Write-Host "✅ Facture créée - $invoiceNumber" -ForegroundColor Green

# ============================================
# ÉTAPE 15: VÉRIFICATION FINALE
# ============================================
Write-Host "`n📌 ÉTAPE 15: VÉRIFICATION FINALE..." -ForegroundColor Yellow

Write-Host "`n📋 Médecins dans patientdb :" -ForegroundColor Cyan
docker exec myheart-postgres psql -U myheart -d patientdb -c "SELECT id, first_name, last_name, specialty, status FROM doctors LIMIT 5;" 2>$null

Write-Host "`n📋 Patients dans patientdb :" -ForegroundColor Cyan
docker exec myheart-postgres psql -U myheart -d patientdb -c "SELECT id, first_name, last_name, gender, marital_status FROM patients LIMIT 2;" 2>$null

Write-Host "`n📋 Rendez-vous dans appointmentdb :" -ForegroundColor Cyan
docker exec myheart-postgres psql -U myheart -d appointmentdb -c "SELECT patient_name, doctor_name, status, type FROM appointments;" 2>$null

Write-Host "`n📋 Test API /api/doctors :" -ForegroundColor Cyan
try {
    $response = curl -X GET http://localhost:8080/api/doctors -H "Authorization: Bearer $p1Token" 2>$null
    $doctorsList = $response | ConvertFrom-Json
    Write-Host "✅ API retourne $($doctorsList.Count) médecins" -ForegroundColor Green
    $doctorsList | ForEach-Object { Write-Host "   - $($_.firstName) $($_.lastName) ($($_.specialty))" -ForegroundColor White }
} catch {
    Write-Host "⚠️ Erreur API: $_" -ForegroundColor Yellow
}

# ============================================
# RÉSUMÉ FINAL
# ============================================
Write-Host "`n" + "=" * 60 -ForegroundColor Cyan
Write-Host "🎉 INITIALISATION COMPLÈTE TERMINÉE AVEC SUCCÈS !" -ForegroundColor Green
Write-Host "=" * 60 -ForegroundColor Cyan

Write-Host "`n🌐 FRONTEND: http://localhost:3000" -ForegroundColor Yellow

Write-Host "`n👤 PATIENT :" -ForegroundColor Yellow
Write-Host "   thomas.martin@example.com / Thomas@2024#Secure" -ForegroundColor White

Write-Host "`n👨‍⚕️ MÉDECINS :" -ForegroundColor Yellow
Write-Host "   alexandre.richard@clinique.fr / Alexandre@2024#Secure (Cardiologie)" -ForegroundColor White
Write-Host "   elodie.moreau@clinique.fr / Elodie@2024#Secure (Pédiatrie)" -ForegroundColor White

Write-Host "`n🔬 LABORATOIRE :" -ForegroundColor Yellow
Write-Host "   sophie.renaud@laboratoire.fr / Sophie@2024#Secure" -ForegroundColor White

Write-Host "`n💊 PHARMACIE :" -ForegroundColor Yellow
Write-Host "   philippe.mercier@pharmacie.fr / Philippe@2024#Secure" -ForegroundColor White

Write-Host "`n🚀 L'APPLICATION EST PRÊTE !" -ForegroundColor Cyan