# Profile Endpoint Fix

## Issue
After changing the authentication system to use email-only login, the profile endpoint was failing with "User not found" error.

## Root Cause
The ProfileController was still using the old authentication pattern:
1. JWT tokens now use email as the subject (instead of username)
2. `auth.getName()` returns the email from the JWT token
3. But the ProfileController was still calling `userService.findByUsername()` with the email

## Fix Applied
Updated ProfileController methods to use email-based user lookup:

### Before:
```java
String username = auth.getName();
Optional<User> userOpt = userService.findByUsername(username);
```

### After:
```java
String email = auth.getName(); // JWT now uses email as subject
Optional<User> userOpt = userService.findByEmail(email);
```

## Changes Made
1. **ProfileController.getProfile()**: Updated to use `findByEmail()` instead of `findByUsername()`
2. **ProfileController.updateProfile()**: Updated to use `findByEmail()` instead of `findByUsername()`
3. **JwtUtil**: Added `getEmailFromToken()` method for clarity

## Testing
Now the profile endpoint should work correctly with the new email-based authentication:

```bash
# 1. Login to get JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'

# 2. Use the returned token to access profile
curl -X GET http://localhost:8080/api/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -H "Origin: http://localhost:3000"
```

## Result
The profile endpoint should now return the user's profile data instead of "User not found" error.