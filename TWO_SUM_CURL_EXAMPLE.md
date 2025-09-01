# Two Sum Problem - Complete cURL Example

## cURL Command to Create the Problem

```bash
curl -X POST http://localhost:4545/api/admin/problems/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -H "Origin: http://localhost:3000" \
  -d '{
    "title": "Two Sum",
    "description": "Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target. You may assume that each input would have exactly one solution, and you may not use the same element twice. You can return the answer in any order.",
    "difficulty": "EASY",
    "topics": ["array", "hash-table", "two-pointers"],
    "targetYears": ["first", "second"],
    "examples": [
      {
        "input": "nums = [2,7,11,15], target = 9",
        "output": "[0,1]",
        "explanation": "Because nums[0] + nums[1] == 9, we return [0, 1]."
      },
      {
        "input": "nums = [3,2,4], target = 6",
        "output": "[1,2]",
        "explanation": "Because nums[1] + nums[2] == 6, we return [1, 2]."
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
        "isHidden": false
      },
      {
        "input": "[3,3]\n6",
        "expectedOutput": "[0,1]",
        "isHidden": true
      }
    ],
    "constraints": "2 <= nums.length <= 10^4\n-10^9 <= nums[i] <= 10^9\n-10^9 <= target <= 10^9\nOnly one valid answer exists.",
    "tags": ["beginner", "interview"],
    "codeTemplates": {
      "cTemplate": "#include <stdio.h>\n#include <stdlib.h>\n\n// Problem: Two Sum\n// Given an array of integers nums and an integer target,\n// return indices of the two numbers such that they add up to target.\n\nclass MySolution {\npublic:\n    /**\n     * Note: The returned array must be malloced, assume caller calls free().\n     */\n    int* twoSum(int* nums, int numsSize, int target, int* returnSize) {\n        // TODO: Implement your logic here\n        // Hint: Use nested loops or hash table approach\n        \n        *returnSize = 2;\n        int* result = (int*)malloc(2 * sizeof(int));\n        \n        // Your code here\n        \n        return result;\n    }\n};\n\nint main() {\n    MySolution solution;\n    \n    // Test case 1\n    int nums1[] = {2, 7, 11, 15};\n    int target1 = 9;\n    int returnSize1;\n    int* result1 = solution.twoSum(nums1, 4, target1, &returnSize1);\n    \n    printf(\"Test 1: [%d, %d]\\n\", result1[0], result1[1]);\n    free(result1);\n    \n    return 0;\n}",
      "cppTemplate": "#include <iostream>\n#include <vector>\n#include <unordered_map>\n#include <algorithm>\nusing namespace std;\n\n// Problem: Two Sum\n// Given an array of integers nums and an integer target,\n// return indices of the two numbers such that they add up to target.\n\nclass MySolution {\npublic:\n    vector<int> twoSum(vector<int>& nums, int target) {\n        // TODO: Implement your logic here\n        // Hint: Use unordered_map for O(n) solution or nested loops for O(n²)\n        \n        vector<int> result;\n        \n        // Method 1: Brute Force O(n²)\n        // for (int i = 0; i < nums.size(); i++) {\n        //     for (int j = i + 1; j < nums.size(); j++) {\n        //         if (nums[i] + nums[j] == target) {\n        //             return {i, j};\n        //         }\n        //     }\n        // }\n        \n        // Method 2: Hash Map O(n) - Implement this\n        // unordered_map<int, int> map;\n        // Your code here\n        \n        return result;\n    }\n};\n\nint main() {\n    MySolution solution;\n    \n    // Test case 1\n    vector<int> nums1 = {2, 7, 11, 15};\n    int target1 = 9;\n    vector<int> result1 = solution.twoSum(nums1, target1);\n    cout << \"Test 1: [\" << result1[0] << \", \" << result1[1] << \"]\" << endl;\n    \n    // Test case 2\n    vector<int> nums2 = {3, 2, 4};\n    int target2 = 6;\n    vector<int> result2 = solution.twoSum(nums2, target2);\n    cout << \"Test 2: [\" << result2[0] << \", \" << result2[1] << \"]\" << endl;\n    \n    return 0;\n}",
      "pythonTemplate": "# Problem: Two Sum\n# Given an array of integers nums and an integer target,\n# return indices of the two numbers such that they add up to target.\n\nclass MySolution:\n    def twoSum(self, nums, target):\n        \"\"\"\n        :type nums: List[int]\n        :type target: int\n        :rtype: List[int]\n        \n        TODO: Implement your logic here\n        Hint: Use dictionary for O(n) solution or nested loops for O(n²)\n        \"\"\"\n        \n        # Method 1: Brute Force O(n²)\n        # for i in range(len(nums)):\n        #     for j in range(i + 1, len(nums)):\n        #         if nums[i] + nums[j] == target:\n        #             return [i, j]\n        \n        # Method 2: Hash Map O(n) - Implement this\n        # num_map = {}\n        # for i, num in enumerate(nums):\n        #     complement = target - num\n        #     if complement in num_map:\n        #         return [num_map[complement], i]\n        #     num_map[num] = i\n        \n        # Your code here\n        pass\n\n# Test your solution\nif __name__ == \"__main__\":\n    solution = MySolution()\n    \n    # Test case 1\n    nums1 = [2, 7, 11, 15]\n    target1 = 9\n    result1 = solution.twoSum(nums1, target1)\n    print(f\"Test 1: {result1}\")\n    \n    # Test case 2\n    nums2 = [3, 2, 4]\n    target2 = 6\n    result2 = solution.twoSum(nums2, target2)\n    print(f\"Test 2: {result2}\")\n    \n    # Test case 3\n    nums3 = [3, 3]\n    target3 = 6\n    result3 = solution.twoSum(nums3, target3)\n    print(f\"Test 3: {result3}\")",
      "javaTemplate": "import java.util.*;\nimport java.io.*;\n\n// Problem: Two Sum\n// Given an array of integers nums and an integer target,\n// return indices of the two numbers such that they add up to target.\n\nclass MySolution {\n    public int[] twoSum(int[] nums, int target) {\n        // TODO: Implement your logic here\n        // Hint: Use HashMap for O(n) solution or nested loops for O(n²)\n        \n        // Method 1: Brute Force O(n²)\n        // for (int i = 0; i < nums.length; i++) {\n        //     for (int j = i + 1; j < nums.length; j++) {\n        //         if (nums[i] + nums[j] == target) {\n        //             return new int[]{i, j};\n        //         }\n        //     }\n        // }\n        \n        // Method 2: HashMap O(n) - Implement this\n        // Map<Integer, Integer> map = new HashMap<>();\n        // for (int i = 0; i < nums.length; i++) {\n        //     int complement = target - nums[i];\n        //     if (map.containsKey(complement)) {\n        //         return new int[]{map.get(complement), i};\n        //     }\n        //     map.put(nums[i], i);\n        // }\n        \n        // Your code here\n        return new int[]{};\n    }\n}\n\npublic class Main {\n    public static void main(String[] args) {\n        MySolution solution = new MySolution();\n        \n        // Test case 1\n        int[] nums1 = {2, 7, 11, 15};\n        int target1 = 9;\n        int[] result1 = solution.twoSum(nums1, target1);\n        System.out.println(\"Test 1: [\" + result1[0] + \", \" + result1[1] + \"]\");\n        \n        // Test case 2\n        int[] nums2 = {3, 2, 4};\n        int target2 = 6;\n        int[] result2 = solution.twoSum(nums2, target2);\n        System.out.println(\"Test 2: [\" + result2[0] + \", \" + result2[1] + \"]\");\n        \n        // Test case 3\n        int[] nums3 = {3, 3};\n        int target3 = 6;\n        int[] result3 = solution.twoSum(nums3, target3);\n        System.out.println(\"Test 3: [\" + result3[0] + \", \" + result3[1] + \"]\");\n    }\n}"
    }
  }'
```

## Expected Response

```json
{
  "message": "Problem created successfully",
  "problem": {
    "id": "64f8a1b2c3d4e5f6a7b8c9d0",
    "title": "Two Sum",
    "description": "Given an array of integers nums and an integer target...",
    "difficulty": "EASY",
    "topics": ["array", "hash-table", "two-pointers"],
    "targetYears": ["first", "second"],
    "examples": [...],
    "testCases": [...],
    "constraints": "2 <= nums.length <= 10^4...",
    "tags": ["beginner", "interview"],
    "codeTemplates": {
      "cTemplate": "...",
      "cppTemplate": "...",
      "pythonTemplate": "...",
      "javaTemplate": "..."
    },
    "createdAt": "2024-01-15T10:30:00",
    "createdBy": "admin@college.edu",
    "totalSubmissions": 0,
    "totalSolved": 0
  }
}
```

## Key Features in This Example

### 1. **Target Years**
- `"targetYears": ["first", "second"]` - Problem is suitable for first and second year students

### 2. **Complete Code Templates**
Each template includes:
- **MySolution class** with empty method to implement
- **Proper function signatures** for the Two Sum problem
- **Helpful comments and hints** about different approaches (brute force vs hash map)
- **Test cases** in the main function/method
- **Language-specific best practices** (imports, memory management, etc.)

### 3. **Educational Value**
- Templates show both O(n²) and O(n) approaches as comments
- Students learn about trade-offs between time complexity
- Consistent MySolution class structure across all languages
- Ready-to-run code with test cases

### 4. **Problem Structure**
- Clear examples with explanations
- Mix of visible and hidden test cases
- Proper constraints specification
- Relevant tags for categorization

This example demonstrates how the enhanced API provides a complete learning experience with proper code structure and educational guidance for students.