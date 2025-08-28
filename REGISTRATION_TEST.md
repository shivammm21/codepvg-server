# Registration API Testing Guide

## Prerequisites
1. Ensure the application is running on `http://localhost:8080`
2. MongoDB is running and accessible
3. Judge0 is set up (optional for registration testing)

## Test Student Registration

### 1. Valid Student Registration
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -d '{
    "username": "test_student",
    "email": "test.student@college.edu",
    "password": "password123",
    "fullName": "Test Student",
    "year": "2nd Year",
    "branch": "Computer Science",
    "prnNumber": "PRN123456789",
    "mobileNumber": "9876543210",
    "role": "student"
  }'
```

**Expected Response:**
```json
{
  "message": "User registered successfully. Waiting for admin approval.",
  "userId": "64f8a1b2c3d4e5f6a7b8c9d0",
  "status": "PENDING"
}
```

### 2. Test Duplicate Username
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_student",
    "email": "another.email@college.edu",
    "password": "password123",
    "fullName": "Another Student",
    "year": "1st Year",
    "branch": "Information Technology",
    "prnNumber": "PRN987654321",
    "mobileNumber": "9876543211",
    "role": "student"
  }'
```

**Expected Response:**
```json
{
  "error": "Username already exists"
}
```

## Test Admin Registration

### 1. Valid Admin Registration
```bash
curl -X POST http://localhost:8080/api/auth/register/admin \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "Admin",
    "department": "Computer Science",
    "adminAccessCode": "CODEPVG_ADMIN_2024_SECURE",
    "email": "test.admin@college.edu",
    "password": "adminPassword123",
    "confirmPassword": "adminPassword123"
  }'
```

**Expected Response:**
```json
{
  "message": "Admin registered successfully and approved automatically.",
  "adminId": "64f8a1b2c3d4e5f6a7b8c9d1",
  "username": "test.admin",
  "email": "test.admin@college.edu",
  "fullName": "Test Admin",
  "department": "Computer Science",
  "role": "ADMIN",
  "status": "APPROVED",
  "createdAt": "2024-01-15T10:30:00"
}
```

### 2. Test Invalid Admin Access Code
```bash
curl -X POST http://localhost:8080/api/auth/register/admin \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Invalid",
    "lastName": "Admin",
    "department": "Computer Science",
    "adminAccessCode": "WRONG_CODE",
    "email": "invalid.admin@college.edu",
    "password": "adminPassword123",
    "confirmPassword": "adminPassword123"
  }'
```

**Expected Response:**
```json
{
  "error": "Invalid admin access code"
}
```

### 3. Test Password Mismatch
```bash
curl -X POST http://localhost:8080/api/auth/register/admin \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "Admin",
    "department": "Computer Science",
    "adminAccessCode": "CODEPVG_ADMIN_2024_SECURE",
    "email": "mismatch.admin@college.edu",
    "password": "adminPassword123",
    "confirmPassword": "differentPassword"
  }'
```

**Expected Response:**
```json
{
  "error": "Passwords do not match"
}
```

## Test User Status Check

### 1. Check Student Status by Username
```bash
curl -X GET http://localhost:8080/api/auth/status/test_student \
  -H "Origin: http://localhost:3000"
```

**Expected Response (Pending):**
```json
{
  "username": "test_student",
  "email": "test.student@college.edu",
  "fullName": "Test Student",
  "status": "PENDING",
  "role": "STUDENT",
  "registeredAt": "2024-01-15T10:30:00",
  "message": "Your account is pending approval. Please wait for an admin to review your registration.",
  "canLogin": false,
  "year": "2nd Year",
  "branch": "Computer Science",
  "prnNumber": "PRN123456789"
}
```

### 2. Check Student Status by Email
```bash
curl -X GET "http://localhost:8080/api/auth/status/email/test.student@college.edu" \
  -H "Origin: http://localhost:3000"
```

**Expected Response (Same as above):**
```json
{
  "username": "test_student",
  "email": "test.student@college.edu",
  "fullName": "Test Student",
  "status": "PENDING",
  "role": "STUDENT",
  "registeredAt": "2024-01-15T10:30:00",
  "message": "Your account is pending approval. Please wait for an admin to review your registration.",
  "canLogin": false,
  "year": "2nd Year",
  "branch": "Computer Science",
  "prnNumber": "PRN123456789"
}
```

### 3. Check Admin Status
```bash
curl -X GET http://localhost:8080/api/auth/status/test.admin \
  -H "Origin: http://localhost:3000"
```

**Expected Response (Auto-approved):**
```json
{
  "username": "test.admin",
  "email": "test.admin@college.edu",
  "fullName": "Test Admin",
  "status": "APPROVED",
  "role": "ADMIN",
  "registeredAt": "2024-01-15T10:30:00",
  "message": "Your account has been approved. You can now login.",
  "canLogin": true,
  "department": "Computer Science"
}
```

### 4. Check Non-existent User
```bash
curl -X GET http://localhost:8080/api/auth/status/nonexistent_user \
  -H "Origin: http://localhost:3000"
```

**Expected Response:**
```json
{
  "error": "User not found"
}
```

## Test Login

### 1. Student Login (Before Approval)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -d '{
    "username": "test_student",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "error": "Account not approved yet. Please wait for admin approval."
}
```

### 2. Admin Login (Auto-approved)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -d '{
    "username": "test.admin",
    "password": "adminPassword123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "64f8a1b2c3d4e5f6a7b8c9d1",
    "username": "test.admin",
    "email": "test.admin@college.edu",
    "fullName": "Test Admin",
    "role": "ADMIN",
    "totalSolved": 0,
    "totalSubmissions": 0
  }
}
```

## Validation Testing

### 1. Missing Required Fields
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "incomplete_user",
    "email": "incomplete@college.edu"
  }'
```

**Expected Response:** Validation errors for missing fields

### 2. Invalid Email Format
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "invalid_email_user",
    "email": "invalid-email",
    "password": "password123",
    "fullName": "Invalid Email User",
    "year": "1st Year",
    "branch": "Computer Science",
    "prnNumber": "PRN111111111",
    "mobileNumber": "9876543212",
    "role": "student"
  }'
```

**Expected Response:** Email validation error

### 3. Short Password
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "short_pass_user",
    "email": "shortpass@college.edu",
    "password": "123",
    "fullName": "Short Password User",
    "year": "1st Year",
    "branch": "Computer Science",
    "prnNumber": "PRN222222222",
    "mobileNumber": "9876543213",
    "role": "student"
  }'
```

**Expected Response:** Password length validation error

## Database Verification

After successful registrations, you can verify the data in MongoDB:

```javascript
// Connect to MongoDB
use codepvg

// Check registered users
db.users.find().pretty()

// Check specific user
db.users.findOne({"username": "test_student"})

// Check admin user
db.users.findOne({"role": "ADMIN"})
```

## Notes

1. **Student Status**: Students are created with `PENDING` status and require admin approval
2. **Admin Status**: Admins are automatically `APPROVED` upon registration
3. **Username Generation**: Admin usernames are generated from email (part before @)
4. **Unique Constraints**: Username and email must be unique across all users
5. **Mobile Number**: New field added for student registration (10-15 digits)
6. **Role Field**: Must be "student" for student registration