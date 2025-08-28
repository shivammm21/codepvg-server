# Quick Test Commands for Admin Registration

## Test Admin Registration with cURL

```bash
curl -X POST http://localhost:8080/api/auth/register/admin \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -d '{
    "firstName": "Shivam",
    "lastName": "Thorat",
    "department": "Computer Science",
    "adminAccessCode": "CODEPVG_ADMIN_2024_SECURE",
    "email": "shivam@gmail.com",
    "password": "123456",
    "confirmPassword": "123456"
  }'
```

## Test Student Registration

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -d '{
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

**Expected Response (if automatically approved):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "User registered successfully and approved automatically.",
  "userId": "64f8a1b2c3d4e5f6a7b8c9d0",
  "status": "APPROVED",
  "role": "STUDENT",
  "username": "test_student",
  "email": "test.student@college.edu",
  "fullName": "Test Student",
  "department": "Computer Science",
  "createdAt": "2025-08-28T22:53:53.645943073",
  "user": {
    "id": "64f8a1b2c3d4e5f6a7b8c9d0",
    "username": "test_student",
    "email": "test.student@college.edu",
    "fullName": "Test Student",
    "role": "STUDENT",
    "year": "2nd Year",
    "branch": "Computer Science",
    "prnNumber": "PRN123456789",
    "totalSolved": 0,
    "totalSubmissions": 0
  }
}
```

**Expected Response (if needs approval):**
```json
{
  "message": "User registered successfully. Waiting for admin approval.",
  "userId": "64f8a1b2c3d4e5f6a7b8c9d0",
  "status": "PENDING",
  "role": "STUDENT",
  "username": "test_student",
  "email": "test.student@college.edu",
  "fullName": "Test Student",
  "department": "Computer Science",
  "createdAt": "2025-08-28T22:53:53.645943073"
}
```

## Test Status Check

```bash
# Check by email (primary method)
curl -X GET "http://localhost:8080/api/auth/status/test.student@college.edu" \
  -H "Origin: http://localhost:3000"

# Alternative: Check by email using the email endpoint
curl -X GET "http://localhost:8080/api/auth/status/email/test.student@college.edu" \
  -H "Origin: http://localhost:3000"
```

## Test Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -d '{
    "email": "shivam@gmail.com",
    "password": "123456"
  }'
```

## For Postman/Insomnia Testing

If you're using Postman or similar tools:

1. **URL**: `http://localhost:8080/api/auth/register/admin`
2. **Method**: POST
3. **Headers**:
   - `Content-Type: application/json`
   - `Origin: http://localhost:3000` (Important!)
4. **Body** (raw JSON):
```json
{
  "firstName": "Shivam",
  "lastName": "Thorat",
  "department": "Computer Science",
  "adminAccessCode": "CODEPVG_ADMIN_2024_SECURE",
  "email": "shivam@gmail.com",
  "password": "123456",
  "confirmPassword": "123456"
}
```

## Expected Success Response

```json
{
  "message": "Admin registered successfully and approved automatically.",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "adminId": "64f8a1b2c3d4e5f6a7b8c9d1",
  "username": "shivam",
  "email": "shivam@gmail.com",
  "fullName": "Shivam Thorat",
  "department": "Computer Science",
  "role": "ADMIN",
  "status": "APPROVED",
  "createdAt": "2024-01-15T10:30:00",
  "user": {
    "id": "64f8a1b2c3d4e5f6a7b8c9d1",
    "username": "shivam",
    "email": "shivam@gmail.com",
    "fullName": "Shivam Thorat",
    "role": "ADMIN",
    "department": "Computer Science",
    "totalSolved": 0,
    "totalSubmissions": 0
  }
}
```

## Common Error Responses

### Invalid Admin Access Code
```json
{
  "error": "Invalid admin access code"
}
```

### Password Mismatch
```json
{
  "error": "Passwords do not match"
}
```

### Email Already Exists
```json
{
  "error": "Email already exists"
}
```

### CORS Error (403 Forbidden)
- Make sure to include the `Origin: http://localhost:3000` header
- Or use the updated CORS configuration that allows all origins for testing