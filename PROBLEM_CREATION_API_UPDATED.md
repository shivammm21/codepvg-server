# Updated Problem Creation API

## Overview
The problem creation API has been enhanced to include:
1. **Student Year Targeting**: Specify which academic years (first, second, third, final) the problem is intended for
2. **Code Templates**: Provide starter code templates for C, C++, Python, and Java with empty MySolution class

## API Endpoint
`POST /api/admin/problems/create`

## Request Body Structure

```json
{
  "title": "Two Sum Problem",
  "description": "Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.",
  "constraints": "2 <= nums.length <= 10^4\n-10^9 <= nums[i] <= 10^9\n-10^9 <= target <= 10^9",
  "difficulty": "EASY",
  "topics": ["array", "hash-table", "two-pointers"],
  "tags": ["beginner", "fundamental"],
  "targetYears": ["first", "second"],
  "examples": [
    {
      "input": "nums = [2,7,11,15], target = 9",
      "output": "[0,1]",
      "explanation": "Because nums[0] + nums[1] == 9, we return [0, 1]."
    }
  ],
  "testCases": [
    {
      "input": "[2,7,11,15]\n9",
      "expectedOutput": "[0,1]",
      "isHidden": false
    },
    {
      "input": "[3,2,4]\n6",
      "expectedOutput": "[1,2]",
      "isHidden": true
    }
  ],
  "codeTemplates": {
    "cTemplate": "#include <stdio.h>\n// Custom C template",
    "cppTemplate": "#include <iostream>\n// Custom C++ template",
    "pythonTemplate": "# Custom Python template\nclass MySolution:",
    "javaTemplate": "// Custom Java template\nclass MySolution {"
  }
}
```

## Field Descriptions

### Required Fields
- **title**: Problem title (string, required)
- **description**: Detailed problem description (string, required)
- **constraints**: Problem constraints and limits (string, required)
- **difficulty**: Problem difficulty level (enum: "EASY", "MEDIUM", "HARD", required)

### Optional Fields
- **topics**: Array of topic tags (e.g., ["array", "hash-table"])
- **tags**: Array of general tags (e.g., ["beginner", "fundamental"])
- **targetYears**: Array of target academic years (values: "first", "second", "third", "final")
- **examples**: Array of example inputs/outputs with explanations
- **testCases**: Array of test cases for validation
- **codeTemplates**: Custom code templates for different languages

### Target Years
Valid values for `targetYears`:
- `"first"` - First year students
- `"second"` - Second year students  
- `"third"` - Third year students
- `"final"` - Final year students

### Code Templates
If `codeTemplates` is not provided, the system automatically generates default templates with:
- Empty `MySolution` class
- Basic imports and structure for each language
- TODO comments for implementation
- Main function/method for testing

## Default Code Templates Generated

### C Template
```c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Problem: [Problem Title]
// Complete the function below

class MySolution {
public:
    // Write your solution here
    int solve() {
        // TODO: Implement your logic
        return 0;
    }
};

int main() {
    MySolution solution;
    // Test your solution
    return 0;
}
```

### C++ Template
```cpp
#include <iostream>
#include <vector>
#include <string>
#include <algorithm>
using namespace std;

// Problem: [Problem Title]
// Complete the function below

class MySolution {
public:
    // Write your solution here
    int solve() {
        // TODO: Implement your logic
        return 0;
    }
};

int main() {
    MySolution solution;
    // Test your solution
    return 0;
}
```

### Python Template
```python
# Problem: [Problem Title]
# Complete the function below

class MySolution:
    def solve(self):
        """
        Write your solution here
        """
        # TODO: Implement your logic
        pass

# Test your solution
if __name__ == "__main__":
    solution = MySolution()
    # Test your solution here
    pass
```

### Java Template
```java
import java.util.*;
import java.io.*;

// Problem: [Problem Title]
// Complete the function below

class MySolution {
    // Write your solution here
    public int solve() {
        // TODO: Implement your logic
        return 0;
    }
}

public class Main {
    public static void main(String[] args) {
        MySolution solution = new MySolution();
        // Test your solution
    }
}
```

## Response Format

### Success Response (200 OK)
```json
{
  "message": "Problem created successfully",
  "problem": {
    "id": "64f8a1b2c3d4e5f6a7b8c9d0",
    "title": "Two Sum Problem",
    "description": "Given an array of integers...",
    "difficulty": "EASY",
    "targetYears": ["first", "second"],
    "codeTemplates": {
      "cTemplate": "...",
      "cppTemplate": "...",
      "pythonTemplate": "...",
      "javaTemplate": "..."
    },
    "createdAt": "2024-01-15T10:30:00",
    "createdBy": "admin@college.edu"
  }
}
```

### Error Response (400 Bad Request)
```json
{
  "error": "Failed to create problem: [error message]"
}
```

## Example cURL Request

```bash
curl -X POST http://localhost:4545/api/admin/problems/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Origin: http://localhost:3000" \
  -d '{
    "title": "Array Sum Problem",
    "description": "Find the sum of all elements in an array",
    "constraints": "1 <= array.length <= 1000\n-1000 <= array[i] <= 1000",
    "difficulty": "EASY",
    "topics": ["array", "math"],
    "targetYears": ["first"],
    "examples": [
      {
        "input": "[1, 2, 3, 4, 5]",
        "output": "15",
        "explanation": "Sum of all elements: 1+2+3+4+5 = 15"
      }
    ],
    "testCases": [
      {
        "input": "[1, 2, 3, 4, 5]",
        "expectedOutput": "15",
        "isHidden": false
      }
    ]
  }'
```

## Benefits

1. **Year-Specific Problems**: Target problems to appropriate academic levels
2. **Consistent Code Structure**: All students start with the same MySolution class template
3. **Multi-Language Support**: Provides templates for C, C++, Python, and Java
4. **Automatic Template Generation**: No need to manually create templates for each problem
5. **Flexible Customization**: Can override default templates with custom ones

## Database Storage

The enhanced Problem model now stores:
- `targetYears`: Array of strings indicating target academic years
- `codeTemplates`: Nested object containing templates for each supported language

This allows for efficient filtering and retrieval of problems based on student year and provides immediate access to appropriate code templates for the coding interface.