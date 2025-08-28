# CodePVG Server API Documentation

## Authentication Endpoints

### 1. Student Registration
**Endpoint:** `POST /api/auth/register`

**Description:** Register a new student account. The account will be in PENDING status and requires admin approval.

**Request Body:**
```json
{
  "email": "john.doe@college.edu",
  "password": "securePassword123",
  "fullName": "John Doe",
  "year": "2nd Year",
  "branch": "Computer Science",
  "prnNumber": "PRN123456789",
  "mobileNumber": "9876543210",
  "role": "student"
}
```

**Validation Rules:**
- `email`: Required, valid email format, must be unique (username is auto-generated from email)
- `password`: Required, minimum 6 characters
- `fullName`: Required
- `year`: Required (e.g., "1st Year", "2nd Year", "3rd Year", "4th Year")
- `branch`: Required (e.g., "Computer Science", "Information Technology", "Electronics")
- `prnNumber`: Required, unique student identifier
- `mobileNumber`: Required, 10-15 digits
- `role`: Required, must be "student"

**Success Response (200 OK):**

**If user needs approval:**
```json
{
  "message": "User registered successfully. Waiting for admin approval.",
  "userId": "64f8a1b2c3d4e5f6a7b8c9d0",
  "status": "PENDING",
  "role": "STUDENT",
  "username": "john_doe",
  "email": "john.doe@college.edu",
  "fullName": "John Doe",
  "department": "Computer Science",
  "createdAt": "2025-08-28T22:53:53.645943073"
}
```

**If user is automatically approved:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "User registered successfully and approved automatically.",
  "userId": "64f8a1b2c3d4e5f6a7b8c9d0",
  "status": "APPROVED",
  "role": "STUDENT",
  "username": "john_doe",
  "email": "john.doe@college.edu",
  "fullName": "John Doe",
  "department": "Computer Science",
  "createdAt": "2025-08-28T22:53:53.645943073",
  "user": {
    "id": "64f8a1b2c3d4e5f6a7b8c9d0",
    "username": "john_doe",
    "email": "john.doe@college.edu",
    "fullName": "John Doe",
    "role": "STUDENT",
    "year": "2nd Year",
    "branch": "Computer Science",
    "prnNumber": "PRN123456789",
    "totalSolved": 0,
    "totalSubmissions": 0
  }
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Username already exists"
}
```

### 2. Admin Registration
**Endpoint:** `POST /api/auth/register/admin`

**Description:** Register a new admin account. Admin accounts are automatically approved upon successful registration.

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Smith",
  "department": "Computer Science",
  "adminAccessCode": "CODEPVG_ADMIN_2024_SECURE",
  "email": "admin@college.edu",
  "password": "secureAdminPassword123",
  "confirmPassword": "secureAdminPassword123"
}
```

**Validation Rules:**
- `firstName`: Required
- `lastName`: Required
- `department`: Required (e.g., "Computer Science", "Information Technology", "Electronics")
- `adminAccessCode`: Required, must match the configured admin access code
- `email`: Required, valid email format, must be unique
- `password`: Required, minimum 8 characters
- `confirmPassword`: Required, must match password

**Success Response (200 OK):**
```json
{
  "message": "Admin registered successfully and approved automatically.",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "adminId": "64f8a1b2c3d4e5f6a7b8c9d1",
  "username": "admin",
  "email": "admin@college.edu",
  "fullName": "John Smith",
  "department": "Computer Science",
  "role": "ADMIN",
  "status": "APPROVED",
  "createdAt": "2024-01-15T10:30:00",
  "user": {
    "id": "64f8a1b2c3d4e5f6a7b8c9d1",
    "username": "admin",
    "email": "admin@college.edu",
    "fullName": "John Smith",
    "role": "ADMIN",
    "department": "Computer Science",
    "totalSolved": 0,
    "totalSubmissions": 0
  }
}
```

**Error Responses (400 Bad Request):**
```json
{
  "error": "Invalid admin access code"
}
```
```json
{
  "error": "Passwords do not match"
}
```
```json
{
  "error": "Email already exists"
}
```

### 3. Check User Status by Email
**Endpoint:** `GET /api/auth/status/{email}`

**Description:** Check the registration status of a user by their email address. This endpoint does not require authentication.

**Path Parameters:**
- `email`: The email address to check status for (URL encoded)

**Success Response (200 OK):**
```json
{
  "username": "john_doe",
  "email": "john.doe@college.edu",
  "fullName": "John Doe",
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

**For Approved User:**
```json
{
  "username": "jane_doe",
  "email": "jane.doe@college.edu",
  "fullName": "Jane Doe",
  "status": "APPROVED",
  "role": "STUDENT",
  "registeredAt": "2024-01-15T10:30:00",
  "message": "Your account has been approved. You can now login.",
  "canLogin": true,
  "year": "3rd Year",
  "branch": "Information Technology",
  "prnNumber": "PRN987654321"
}
```

**For Rejected User:**
```json
{
  "username": "rejected_user",
  "email": "rejected@college.edu",
  "fullName": "Rejected User",
  "status": "REJECTED",
  "role": "STUDENT",
  "registeredAt": "2024-01-15T10:30:00",
  "message": "Your account has been rejected. Please contact the administrator for more information.",
  "canLogin": false,
  "year": "1st Year",
  "branch": "Electronics",
  "prnNumber": "PRN111111111"
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "User not found"
}
```

### 4. Check User Status by Email
**Endpoint:** `GET /api/auth/status/email/{email}`

**Description:** Check the registration status of a user by their email address. This endpoint does not require authentication.

**Path Parameters:**
- `email`: The email address to check status for (URL encoded)

**Success Response (200 OK):**
```json
{
  "username": "john_doe",
  "email": "john.doe@college.edu",
  "fullName": "John Doe",
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

**Error Response (400 Bad Request):**
```json
{
  "error": "User not found with this email address"
}
```

### 5. User Login
**Endpoint:** `POST /api/auth/login`

**Description:** Authenticate user and receive JWT token for API access.

**Request Body:**
```json
{
  "email": "john.doe@college.edu",
  "password": "securePassword123"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "64f8a1b2c3d4e5f6a7b8c9d0",
    "username": "john_doe",
    "email": "john.doe@college.edu",
    "fullName": "John Doe",
    "role": "STUDENT",
    "totalSolved": 5,
    "totalSubmissions": 12
  }
}
```

**Error Responses (400 Bad Request):**
```json
{
  "error": "Invalid email or password"
}
```
```json
{
  "error": "Account not approved yet. Please wait for admin approval."
}
```

## Configuration

### Admin Access Code
The admin access code is configured in `application.properties`:
```properties
admin.access.code=CODEPVG_ADMIN_2024_SECURE
```

**Important:** Change this to a secure, unique value in production environments.

### Environment-Specific Configuration

**Development (`application-dev.properties`):**
- Uses local MongoDB
- Admin access code: `CODEPVG_ADMIN_2024_SECURE`
- Server port: 8080

**Production (`application-prod.properties`):**
- Uses MongoDB Atlas (configure connection string)
- Same admin access code (change for production)

## Security Notes

1. **Admin Access Code**: Keep this secret and change it regularly
2. **JWT Secret**: Use a strong, unique secret key for JWT token signing
3. **Password Policy**: Enforce strong passwords (minimum 8 characters for admins, 6 for students)
4. **Email Validation**: All email addresses must be unique and valid
5. **Username Validation**: Usernames must be unique and between 3-20 characters
6. **CORS Configuration**: API only accepts requests from `http://localhost:3000` for security

## Usage Examples

### Register a Student (cURL)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -d '{
    "username": "jane_doe",
    "email": "jane.doe@college.edu",
    "password": "myPassword123",
    "fullName": "Jane Doe",
    "year": "3rd Year",
    "branch": "Information Technology",
    "prnNumber": "PRN987654321",
    "mobileNumber": "9876543210",
    "role": "student"
  }'
```

### Register an Admin (cURL)
```bash
curl -X POST http://localhost:8080/api/auth/register/admin \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -d '{
    "firstName": "Alice",
    "lastName": "Johnson",
    "department": "Computer Science",
    "adminAccessCode": "CODEPVG_ADMIN_2024_SECURE",
    "email": "alice.johnson@college.edu",
    "password": "adminPassword123",
    "confirmPassword": "adminPassword123"
  }'
```

### Check User Status by Username (cURL)
```bash
curl -X GET http://localhost:8080/api/auth/status/john_doe \
  -H "Origin: http://localhost:3000"
```

### Check User Status by Email (cURL)
```bash
curl -X GET "http://localhost:8080/api/auth/status/email/john.doe@college.edu" \
  -H "Origin: http://localhost:3000"
```

### Login (cURL)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -d '{
    "username": "jane_doe",
    "password": "myPassword123"
  }'
```

## Error Handling

All endpoints return appropriate HTTP status codes:
- `200 OK`: Successful operation
- `400 Bad Request`: Validation errors or business logic errors
- `401 Unauthorized`: Invalid credentials
- `409 Conflict`: Resource already exists (duplicate username/email)
- `500 Internal Server Error`: Server-side errors

Error responses always include an `error` field with a descriptive message.