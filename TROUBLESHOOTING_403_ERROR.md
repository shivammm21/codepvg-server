# Troubleshooting 403 Forbidden Error

## Issue Identified
The 403 Forbidden error was caused by multiple issues:

1. **CORS Configuration Conflict** - Fixed ✅
2. **Wrong Server Port** - Your server runs on port **4545**, not 8080
3. **Admin Access Code** - Must be exactly: `CODEPVG_ADMIN_2024_SECURE`

## ✅ Fixed Issues

### 1. CORS Configuration
Updated both SecurityConfig and CorsConfig to allow all origins for testing:
```java
configuration.setAllowedOriginPatterns(Arrays.asList("*"));
```

### 2. Server Port
Your application runs on port **4545** (not 8080 as in examples).

## 🔧 Correct API Testing

### Updated URL for Your Server
**Correct URL**: `http://localhost:4545/api/auth/register/admin`

### Test with cURL
```bash
curl -X POST http://localhost:4545/api/auth/register/admin \
  -H "Content-Type: application/json" \
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

### Test with Postman/Insomnia
- **URL**: `http://localhost:4545/api/auth/register/admin`
- **Method**: POST
- **Headers**: `Content-Type: application/json`
- **Body** (raw JSON):
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

## 🚨 Important Notes

### 1. Server Port
Your server runs on **port 4545**, not 8080. Update all your API calls to use:
- `http://localhost:4545/api/...`

### 2. Admin Access Code
Must be exactly: `CODEPVG_ADMIN_2024_SECURE` (case-sensitive)

### 3. Application Restart Required
After the CORS configuration changes, you need to **restart your Spring Boot application**.

## 📝 Step-by-Step Testing

### Step 1: Restart Application
```bash
# Stop the current application (Ctrl+C)
# Then restart with:
mvn spring-boot:run
```

### Step 2: Test Admin Registration
Use the correct URL with port 4545:
```bash
curl -X POST http://localhost:4545/api/auth/register/admin \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "Admin",
    "department": "Computer Science",
    "adminAccessCode": "CODEPVG_ADMIN_2024_SECURE",
    "email": "test@example.com",
    "password": "password123",
    "confirmPassword": "password123"
  }'
```

### Step 3: Expected Success Response
```json
{
  "message": "Admin registered successfully and approved automatically.",
  "adminId": "64f8a1b2c3d4e5f6a7b8c9d1",
  "username": "test",
  "email": "test@example.com",
  "fullName": "Test Admin",
  "department": "Computer Science",
  "role": "ADMIN",
  "status": "APPROVED",
  "createdAt": "2024-01-15T10:30:00"
}
```

## 🔍 Common Error Responses

### Invalid Admin Access Code
```json
{
  "error": "Invalid admin access code"
}
```
**Solution**: Use exactly `CODEPVG_ADMIN_2024_SECURE`

### Password Mismatch
```json
{
  "error": "Passwords do not match"
}
```
**Solution**: Ensure `password` and `confirmPassword` are identical

### Email Already Exists
```json
{
  "error": "Email already exists"
}
```
**Solution**: Use a different email address

### MongoDB Connection Error
```json
{
  "error": "Database connection failed"
}
```
**Solution**: Ensure MongoDB is running on `localhost:27017`

## 🧪 Additional Test Endpoints

### Test Student Registration
```bash
curl -X POST http://localhost:4545/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_student",
    "email": "student@example.com",
    "password": "password123",
    "fullName": "Test Student",
    "year": "2nd Year",
    "branch": "Computer Science",
    "prnNumber": "PRN123456789",
    "mobileNumber": "9876543210",
    "role": "student"
  }'
```

### Test Status Check
```bash
curl -X GET http://localhost:4545/api/auth/status/test_student
```

### Test Login
```bash
curl -X POST http://localhost:4545/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test",
    "password": "password123"
  }'
```

## 🔧 If Still Getting 403 Error

### 1. Check Application Logs
Look for any error messages in the console when starting the application.

### 2. Verify MongoDB Connection
Ensure MongoDB is running:
```bash
# Check if MongoDB is running
sudo systemctl status mongodb
# Or
mongosh --eval "db.adminCommand('ismaster')"
```

### 3. Check Port Availability
```bash
netstat -tulpn | grep :4545
```

### 4. Test Basic Connectivity
```bash
curl -X GET http://localhost:4545/api/auth/status/nonexistent
# Should return: {"error": "User not found"}
```

## 🎯 Quick Fix Summary

1. **Use port 4545** instead of 8080
2. **Restart your application** after CORS changes
3. **Use correct admin access code**: `CODEPVG_ADMIN_2024_SECURE`
4. **Ensure MongoDB is running**

Try the corrected URL with port 4545 - it should work now! 🚀