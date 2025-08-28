# Frontend Integration Guide

## CORS Configuration
The CodePVG Server is configured to accept requests only from `http://localhost:3000`. Make sure your frontend application is running on this port.

## JavaScript/React Examples

### 1. Student Registration

```javascript
// Student Registration Function
const registerStudent = async (studentData) => {
  try {
    const response = await fetch('http://localhost:8080/api/auth/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include', // Important for CORS with credentials
      body: JSON.stringify({
        username: studentData.username,
        email: studentData.email,
        password: studentData.password,
        fullName: studentData.fullName,
        year: studentData.year,
        branch: studentData.branch,
        prnNumber: studentData.prnNumber,
        mobileNumber: studentData.mobileNumber,
        role: 'student'
      })
    });

    const data = await response.json();
    
    if (response.ok) {
      console.log('Registration successful:', data);
      return { success: true, data };
    } else {
      console.error('Registration failed:', data.error);
      return { success: false, error: data.error };
    }
  } catch (error) {
    console.error('Network error:', error);
    return { success: false, error: 'Network error occurred' };
  }
};

// Usage Example
const handleStudentRegistration = async (formData) => {
  const result = await registerStudent({
    username: formData.username,
    email: formData.email,
    password: formData.password,
    fullName: `${formData.firstName} ${formData.lastName}`,
    year: formData.year,
    branch: formData.branch,
    prnNumber: formData.prnNumber,
    mobileNumber: formData.mobileNumber
  });

  if (result.success) {
    alert('Registration successful! Please wait for admin approval.');
  } else {
    alert(`Registration failed: ${result.error}`);
  }
};
```

### 2. Admin Registration

```javascript
// Admin Registration Function
const registerAdmin = async (adminData) => {
  try {
    const response = await fetch('http://localhost:8080/api/auth/register/admin', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify({
        firstName: adminData.firstName,
        lastName: adminData.lastName,
        department: adminData.department,
        adminAccessCode: adminData.adminAccessCode,
        email: adminData.email,
        password: adminData.password,
        confirmPassword: adminData.confirmPassword
      })
    });

    const data = await response.json();
    
    if (response.ok) {
      console.log('Admin registration successful:', data);
      return { success: true, data };
    } else {
      console.error('Admin registration failed:', data.error);
      return { success: false, error: data.error };
    }
  } catch (error) {
    console.error('Network error:', error);
    return { success: false, error: 'Network error occurred' };
  }
};

// Usage Example
const handleAdminRegistration = async (formData) => {
  // Validate password confirmation on frontend
  if (formData.password !== formData.confirmPassword) {
    alert('Passwords do not match!');
    return;
  }

  const result = await registerAdmin(formData);

  if (result.success) {
    alert('Admin registration successful! You can now login.');
    // Redirect to login page or dashboard
  } else {
    alert(`Registration failed: ${result.error}`);
  }
};
```

### 3. User Login

```javascript
// Login Function
const loginUser = async (credentials) => {
  try {
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify({
        username: credentials.username,
        password: credentials.password
      })
    });

    const data = await response.json();
    
    if (response.ok) {
      // Store JWT token in localStorage or sessionStorage
      localStorage.setItem('authToken', data.token);
      localStorage.setItem('user', JSON.stringify(data.user));
      
      console.log('Login successful:', data);
      return { success: true, data };
    } else {
      console.error('Login failed:', data.error);
      return { success: false, error: data.error };
    }
  } catch (error) {
    console.error('Network error:', error);
    return { success: false, error: 'Network error occurred' };
  }
};

// Usage Example
const handleLogin = async (formData) => {
  const result = await loginUser({
    username: formData.username,
    password: formData.password
  });

  if (result.success) {
    // Redirect based on user role
    if (result.data.user.role === 'ADMIN') {
      window.location.href = '/admin/dashboard';
    } else {
      window.location.href = '/student/dashboard';
    }
  } else {
    alert(`Login failed: ${result.error}`);
  }
};
```

### 4. Check User Status

```javascript
// Check User Status by Username
const checkUserStatus = async (username) => {
  try {
    const response = await fetch(`http://localhost:8080/api/auth/status/${username}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
    });

    const data = await response.json();
    
    if (response.ok) {
      console.log('User status:', data);
      return { success: true, data };
    } else {
      console.error('Failed to get user status:', data.error);
      return { success: false, error: data.error };
    }
  } catch (error) {
    console.error('Network error:', error);
    return { success: false, error: 'Network error occurred' };
  }
};

// Check User Status by Email
const checkUserStatusByEmail = async (email) => {
  try {
    const encodedEmail = encodeURIComponent(email);
    const response = await fetch(`http://localhost:8080/api/auth/status/email/${encodedEmail}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
    });

    const data = await response.json();
    
    if (response.ok) {
      console.log('User status:', data);
      return { success: true, data };
    } else {
      console.error('Failed to get user status:', data.error);
      return { success: false, error: data.error };
    }
  } catch (error) {
    console.error('Network error:', error);
    return { success: false, error: 'Network error occurred' };
  }
};

// Usage Examples
const handleCheckStatus = async (identifier, type = 'username') => {
  let result;
  
  if (type === 'email') {
    result = await checkUserStatusByEmail(identifier);
  } else {
    result = await checkUserStatus(identifier);
  }

  if (result.success) {
    const { status, message, canLogin } = result.data;
    
    // Display status to user
    switch (status) {
      case 'PENDING':
        alert(`Status: Pending\n${message}`);
        break;
      case 'APPROVED':
        alert(`Status: Approved\n${message}`);
        if (canLogin) {
          // Redirect to login page or enable login
          window.location.href = '/login';
        }
        break;
      case 'REJECTED':
        alert(`Status: Rejected\n${message}`);
        break;
    }
  } else {
    alert(`Error: ${result.error}`);
  }
};
```

### 5. Authenticated API Requests

```javascript
// Helper function to make authenticated requests
const makeAuthenticatedRequest = async (url, options = {}) => {
  const token = localStorage.getItem('authToken');
  
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
    credentials: 'include',
  };

  const mergedOptions = {
    ...defaultOptions,
    ...options,
    headers: {
      ...defaultOptions.headers,
      ...options.headers,
    },
  };

  try {
    const response = await fetch(url, mergedOptions);
    const data = await response.json();

    if (response.ok) {
      return { success: true, data };
    } else {
      // Handle token expiration
      if (response.status === 401) {
        localStorage.removeItem('authToken');
        localStorage.removeItem('user');
        window.location.href = '/login';
      }
      return { success: false, error: data.error };
    }
  } catch (error) {
    console.error('Network error:', error);
    return { success: false, error: 'Network error occurred' };
  }
};

// Example: Get student dashboard
const getStudentDashboard = async () => {
  return await makeAuthenticatedRequest('http://localhost:8080/api/student/dashboard');
};

// Example: Submit code
const submitCode = async (submissionData) => {
  return await makeAuthenticatedRequest('http://localhost:8080/api/student/submissions', {
    method: 'POST',
    body: JSON.stringify(submissionData)
  });
};
```

## React Hook Example

```javascript
// Custom hook for API calls
import { useState, useEffect } from 'react';

const useAuth = () => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedToken = localStorage.getItem('authToken');
    const storedUser = localStorage.getItem('user');

    if (storedToken && storedUser) {
      setToken(storedToken);
      setUser(JSON.parse(storedUser));
    }
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    setLoading(true);
    const result = await loginUser(credentials);
    
    if (result.success) {
      setToken(result.data.token);
      setUser(result.data.user);
    }
    
    setLoading(false);
    return result;
  };

  const logout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
  };

  return {
    user,
    token,
    loading,
    login,
    logout,
    isAuthenticated: !!token,
    isAdmin: user?.role === 'ADMIN',
    isStudent: user?.role === 'STUDENT'
  };
};

export default useAuth;
```

## Error Handling

```javascript
// Centralized error handler
const handleApiError = (error) => {
  switch (error) {
    case 'Username already exists':
      return 'This username is already taken. Please choose another.';
    case 'Email already exists':
      return 'This email is already registered. Please use a different email.';
    case 'Invalid admin access code':
      return 'The admin access code is incorrect. Please contact your institution.';
    case 'Account not approved yet. Please wait for admin approval.':
      return 'Your account is pending approval. Please wait for an admin to approve your registration.';
    case 'Invalid username or password':
      return 'The username or password you entered is incorrect.';
    default:
      return error || 'An unexpected error occurred. Please try again.';
  }
};
```

## Form Validation Examples

```javascript
// Student registration form validation
const validateStudentForm = (formData) => {
  const errors = {};

  if (!formData.username || formData.username.length < 3) {
    errors.username = 'Username must be at least 3 characters long';
  }

  if (!formData.email || !/\S+@\S+\.\S+/.test(formData.email)) {
    errors.email = 'Please enter a valid email address';
  }

  if (!formData.password || formData.password.length < 6) {
    errors.password = 'Password must be at least 6 characters long';
  }

  if (!formData.prnNumber) {
    errors.prnNumber = 'PRN number is required';
  }

  if (!formData.mobileNumber || !/^\d{10,15}$/.test(formData.mobileNumber)) {
    errors.mobileNumber = 'Please enter a valid mobile number (10-15 digits)';
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
};

// Admin registration form validation
const validateAdminForm = (formData) => {
  const errors = {};

  if (!formData.firstName) {
    errors.firstName = 'First name is required';
  }

  if (!formData.lastName) {
    errors.lastName = 'Last name is required';
  }

  if (!formData.department) {
    errors.department = 'Department is required';
  }

  if (!formData.adminAccessCode) {
    errors.adminAccessCode = 'Admin access code is required';
  }

  if (!formData.email || !/\S+@\S+\.\S+/.test(formData.email)) {
    errors.email = 'Please enter a valid email address';
  }

  if (!formData.password || formData.password.length < 8) {
    errors.password = 'Password must be at least 8 characters long';
  }

  if (formData.password !== formData.confirmPassword) {
    errors.confirmPassword = 'Passwords do not match';
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
};
```

## Important Notes

1. **CORS**: The server only accepts requests from `http://localhost:3000`
2. **Credentials**: Always include `credentials: 'include'` for CORS requests
3. **JWT Token**: Store the JWT token securely and include it in authenticated requests
4. **Error Handling**: Always handle both network errors and API errors
5. **Validation**: Validate forms on both frontend and backend
6. **Security**: Never expose the admin access code in client-side code (use environment variables)

## Environment Variables (Frontend)

Create a `.env` file in your frontend project:

```env
REACT_APP_API_BASE_URL=http://localhost:8080
REACT_APP_ADMIN_ACCESS_CODE=CODEPVG_ADMIN_2024_SECURE
```

Then use it in your code:

```javascript
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;
const ADMIN_ACCESS_CODE = process.env.REACT_APP_ADMIN_ACCESS_CODE;
```