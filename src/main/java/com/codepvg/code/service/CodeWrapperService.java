package com.codepvg.code.service;

import org.springframework.stereotype.Service;

@Service
public class CodeWrapperService {

    public String wrapCode(String userCode, String language, int languageId, String problemType) {
        switch (language.toLowerCase()) {
            case "java":
                return wrapJavaCode(userCode, problemType);
            case "python":
                return wrapPythonCode(userCode, problemType);
            case "cpp":
            case "c++":
                return wrapCppCode(userCode, problemType);
            default:
                return userCode; // Return as-is for unsupported languages
        }
    }

    private String wrapJavaCode(String userCode, String problemType) {
        StringBuilder wrapper = new StringBuilder();
        
        wrapper.append("import java.util.*;\n");
        wrapper.append("import java.io.*;\n\n");
        
        // Add user's solution class
        wrapper.append(userCode).append("\n\n");
        
        // Add main class with input/output handling
        wrapper.append("public class Main {\n");
        wrapper.append("    public static void main(String[] args) {\n");
        wrapper.append("        Scanner scanner = new Scanner(System.in);\n");
        wrapper.append("        Solution solution = new Solution();\n\n");
        
        // Add problem-specific input/output handling
        wrapper.append("        try {\n");
        wrapper.append("            // Read input based on problem type\n");
        wrapper.append("            String line = scanner.nextLine().trim();\n");
        wrapper.append("            \n");
        wrapper.append("            // Parse array input like [2,7,11,15]\n");
        wrapper.append("            if (line.startsWith(\"[\") && line.endsWith(\"]\")) {\n");
        wrapper.append("                line = line.substring(1, line.length() - 1);\n");
        wrapper.append("                String[] parts = line.split(\",\");\n");
        wrapper.append("                int[] nums = new int[parts.length];\n");
        wrapper.append("                for (int i = 0; i < parts.length; i++) {\n");
        wrapper.append("                    nums[i] = Integer.parseInt(parts[i].trim());\n");
        wrapper.append("                }\n");
        wrapper.append("                \n");
        wrapper.append("                // Read target if available\n");
        wrapper.append("                int target = 0;\n");
        wrapper.append("                if (scanner.hasNextLine()) {\n");
        wrapper.append("                    target = Integer.parseInt(scanner.nextLine().trim());\n");
        wrapper.append("                }\n");
        wrapper.append("                \n");
        wrapper.append("                // Call solution method (assuming twoSum for now)\n");
        wrapper.append("                int[] result = solution.twoSum(nums, target);\n");
        wrapper.append("                \n");
        wrapper.append("                // Print result in array format\n");
        wrapper.append("                System.out.print(\"[\");\n");
        wrapper.append("                for (int i = 0; i < result.length; i++) {\n");
        wrapper.append("                    System.out.print(result[i]);\n");
        wrapper.append("                    if (i < result.length - 1) System.out.print(\",\");\n");
        wrapper.append("                }\n");
        wrapper.append("                System.out.println(\"]\");\n");
        wrapper.append("            } else {\n");
        wrapper.append("                // Handle other input formats\n");
        wrapper.append("                System.out.println(\"Unsupported input format\");\n");
        wrapper.append("            }\n");
        wrapper.append("            \n");
        wrapper.append("        } catch (Exception e) {\n");
        wrapper.append("            System.err.println(\"Error: \" + e.getMessage());\n");
        wrapper.append("            e.printStackTrace();\n");
        wrapper.append("        } finally {\n");
        wrapper.append("            scanner.close();\n");
        wrapper.append("        }\n");
        wrapper.append("    }\n");
        wrapper.append("}\n");
        
        return wrapper.toString();
    }

    private String wrapPythonCode(String userCode, String problemType) {
        StringBuilder wrapper = new StringBuilder();
        
        wrapper.append("import sys\n");
        wrapper.append("import json\n");
        wrapper.append("from typing import List, Optional\n\n");
        
        // Add user's solution class
        wrapper.append(userCode).append("\n\n");
        
        // Add main function with input/output handling
        wrapper.append("def main():\n");
        wrapper.append("    try:\n");
        wrapper.append("        solution = Solution()\n");
        wrapper.append("        \n");
        wrapper.append("        # Read input\n");
        wrapper.append("        line = input().strip()\n");
        wrapper.append("        \n");
        wrapper.append("        # Parse array input like [2,7,11,15]\n");
        wrapper.append("        if line.startswith('[') and line.endswith(']'):\n");
        wrapper.append("            nums_str = line[1:-1]\n");
        wrapper.append("            nums = [int(x.strip()) for x in nums_str.split(',') if x.strip()]\n");
        wrapper.append("            \n");
        wrapper.append("            # Read target if available\n");
        wrapper.append("            try:\n");
        wrapper.append("                target = int(input().strip())\n");
        wrapper.append("            except:\n");
        wrapper.append("                target = 0\n");
        wrapper.append("            \n");
        wrapper.append("            # Call solution method (assuming twoSum for now)\n");
        wrapper.append("            result = solution.twoSum(nums, target)\n");
        wrapper.append("            \n");
        wrapper.append("            # Print result in array format\n");
        wrapper.append("            print(f'[{\",\".join(map(str, result))}]')\n");
        wrapper.append("        else:\n");
        wrapper.append("            print('Unsupported input format')\n");
        wrapper.append("            \n");
        wrapper.append("    except Exception as e:\n");
        wrapper.append("        print(f'Error: {str(e)}', file=sys.stderr)\n");
        wrapper.append("        import traceback\n");
        wrapper.append("        traceback.print_exc()\n");
        wrapper.append("\n");
        wrapper.append("if __name__ == '__main__':\n");
        wrapper.append("    main()\n");
        
        return wrapper.toString();
    }

    private String wrapCppCode(String userCode, String problemType) {
        StringBuilder wrapper = new StringBuilder();
        
        wrapper.append("#include <iostream>\n");
        wrapper.append("#include <vector>\n");
        wrapper.append("#include <string>\n");
        wrapper.append("#include <sstream>\n");
        wrapper.append("#include <algorithm>\n");
        wrapper.append("using namespace std;\n\n");
        
        // Add user's solution class
        wrapper.append(userCode).append("\n\n");
        
        // Add main function with input/output handling
        wrapper.append("int main() {\n");
        wrapper.append("    try {\n");
        wrapper.append("        Solution solution;\n");
        wrapper.append("        \n");
        wrapper.append("        string line;\n");
        wrapper.append("        getline(cin, line);\n");
        wrapper.append("        \n");
        wrapper.append("        // Parse array input like [2,7,11,15]\n");
        wrapper.append("        if (line.front() == '[' && line.back() == ']') {\n");
        wrapper.append("            line = line.substr(1, line.length() - 2);\n");
        wrapper.append("            \n");
        wrapper.append("            vector<int> nums;\n");
        wrapper.append("            stringstream ss(line);\n");
        wrapper.append("            string num;\n");
        wrapper.append("            \n");
        wrapper.append("            while (getline(ss, num, ',')) {\n");
        wrapper.append("                nums.push_back(stoi(num));\n");
        wrapper.append("            }\n");
        wrapper.append("            \n");
        wrapper.append("            // Read target if available\n");
        wrapper.append("            int target = 0;\n");
        wrapper.append("            if (getline(cin, line)) {\n");
        wrapper.append("                target = stoi(line);\n");
        wrapper.append("            }\n");
        wrapper.append("            \n");
        wrapper.append("            // Call solution method (assuming twoSum for now)\n");
        wrapper.append("            vector<int> result = solution.twoSum(nums, target);\n");
        wrapper.append("            \n");
        wrapper.append("            // Print result in array format\n");
        wrapper.append("            cout << \"[\";\n");
        wrapper.append("            for (int i = 0; i < result.size(); i++) {\n");
        wrapper.append("                cout << result[i];\n");
        wrapper.append("                if (i < result.size() - 1) cout << \",\";\n");
        wrapper.append("            }\n");
        wrapper.append("            cout << \"]\" << endl;\n");
        wrapper.append("        } else {\n");
        wrapper.append("            cout << \"Unsupported input format\" << endl;\n");
        wrapper.append("        }\n");
        wrapper.append("        \n");
        wrapper.append("    } catch (const exception& e) {\n");
        wrapper.append("        cerr << \"Error: \" << e.what() << endl;\n");
        wrapper.append("        return 1;\n");
        wrapper.append("    }\n");
        wrapper.append("    \n");
        wrapper.append("    return 0;\n");
        wrapper.append("}\n");
        
        return wrapper.toString();
    }

    // Method to create dynamic wrapper based on problem's method signature
    public String wrapCodeForProblem(String userCode, String language, String problemTitle, String methodSignature) {
        switch (language.toLowerCase()) {
            case "java":
                return wrapJavaCodeDynamic(userCode, problemTitle, methodSignature);
            case "python":
                return wrapPythonCodeDynamic(userCode, problemTitle, methodSignature);
            case "cpp":
            case "c++":
                return wrapCppCodeDynamic(userCode, problemTitle, methodSignature);
            default:
                return wrapCode(userCode, language, 0, "default");
        }
    }

    private String wrapJavaCodeDynamic(String userCode, String problemTitle, String methodSignature) {
        StringBuilder wrapper = new StringBuilder();
        
        wrapper.append("import java.util.*;\n");
        wrapper.append("import java.io.*;\n\n");
        
        // Add user's solution class
        wrapper.append(userCode).append("\n\n");
        
        wrapper.append("public class Main {\n");
        wrapper.append("    public static void main(String[] args) {\n");
        wrapper.append("        Scanner scanner = new Scanner(System.in);\n");
        wrapper.append("        Solution solution = new Solution();\n\n");
        
        wrapper.append("        try {\n");
        
        // Dynamic input parsing based on problem type
        if (problemTitle.toLowerCase().contains("two") && problemTitle.toLowerCase().contains("sum") || 
            problemTitle.toLowerCase().contains("twosum")) {
            wrapper.append("            // Two Sum problem input parsing\n");
            wrapper.append("            String line = scanner.nextLine().trim();\n");
            wrapper.append("            System.err.println(\"Debug: Input line = \" + line);\n");
            wrapper.append("            int[] nums = parseIntArray(line);\n");
            wrapper.append("            System.err.println(\"Debug: Parsed nums = \" + java.util.Arrays.toString(nums));\n");
            wrapper.append("            \n");
            wrapper.append("            int target = 0;\n");
            wrapper.append("            if (scanner.hasNextLine()) {\n");
            wrapper.append("                String targetLine = scanner.nextLine().trim();\n");
            wrapper.append("                System.err.println(\"Debug: Target line = \" + targetLine);\n");
            wrapper.append("                target = Integer.parseInt(targetLine);\n");
            wrapper.append("            }\n");
            wrapper.append("            System.err.println(\"Debug: Target = \" + target);\n");
            wrapper.append("            \n");
            wrapper.append("            int[] result = solution.twoSum(nums, target);\n");
            wrapper.append("            System.err.println(\"Debug: Result = \" + java.util.Arrays.toString(result));\n");
            wrapper.append("            printIntArray(result);\n");
        } else if (problemTitle.toLowerCase().contains("reverse") && 
                   problemTitle.toLowerCase().contains("linked")) {
            wrapper.append("            // Reverse Linked List problem\n");
            wrapper.append("            String line = scanner.nextLine().trim();\n");
            wrapper.append("            ListNode head = parseLinkedList(line);\n");
            wrapper.append("            \n");
            wrapper.append("            ListNode result = solution.reverseList(head);\n");
            wrapper.append("            printLinkedList(result);\n");
        } else {
            // Generic array problem
            wrapper.append("            // Generic array problem\n");
            wrapper.append("            String line = scanner.nextLine().trim();\n");
            wrapper.append("            int[] nums = parseIntArray(line);\n");
            wrapper.append("            \n");
            wrapper.append("            // Try to call the first method in Solution class\n");
            wrapper.append("            // This is a generic approach - may need customization\n");
            wrapper.append("            System.out.println(\"Generic input processed\");\n");
        }
        
        wrapper.append("            \n");
        wrapper.append("        } catch (Exception e) {\n");
        wrapper.append("            System.err.println(\"Error: \" + e.getMessage());\n");
        wrapper.append("            e.printStackTrace();\n");
        wrapper.append("        } finally {\n");
        wrapper.append("            scanner.close();\n");
        wrapper.append("        }\n");
        wrapper.append("    }\n");
        
        // Add utility methods
        wrapper.append("    \n");
        wrapper.append("    private static int[] parseIntArray(String line) {\n");
        wrapper.append("        if (line.startsWith(\"[\") && line.endsWith(\"]\")) {\n");
        wrapper.append("            line = line.substring(1, line.length() - 1);\n");
        wrapper.append("        }\n");
        wrapper.append("        if (line.trim().isEmpty()) return new int[0];\n");
        wrapper.append("        \n");
        wrapper.append("        String[] parts = line.split(\",\");\n");
        wrapper.append("        int[] result = new int[parts.length];\n");
        wrapper.append("        for (int i = 0; i < parts.length; i++) {\n");
        wrapper.append("            result[i] = Integer.parseInt(parts[i].trim());\n");
        wrapper.append("        }\n");
        wrapper.append("        return result;\n");
        wrapper.append("    }\n");
        wrapper.append("    \n");
        wrapper.append("    private static void printIntArray(int[] arr) {\n");
        wrapper.append("        System.out.print(\"[\");\n");
        wrapper.append("        for (int i = 0; i < arr.length; i++) {\n");
        wrapper.append("            System.out.print(arr[i]);\n");
        wrapper.append("            if (i < arr.length - 1) System.out.print(\",\");\n");
        wrapper.append("        }\n");
        wrapper.append("        System.out.println(\"]\");\n");
        wrapper.append("    }\n");
        
        // Add ListNode class and methods if needed
        wrapper.append("    \n");
        wrapper.append("    static class ListNode {\n");
        wrapper.append("        int val;\n");
        wrapper.append("        ListNode next;\n");
        wrapper.append("        ListNode() {}\n");
        wrapper.append("        ListNode(int val) { this.val = val; }\n");
        wrapper.append("        ListNode(int val, ListNode next) { this.val = val; this.next = next; }\n");
        wrapper.append("    }\n");
        wrapper.append("    \n");
        wrapper.append("    private static ListNode parseLinkedList(String line) {\n");
        wrapper.append("        int[] values = parseIntArray(line);\n");
        wrapper.append("        if (values.length == 0) return null;\n");
        wrapper.append("        \n");
        wrapper.append("        ListNode head = new ListNode(values[0]);\n");
        wrapper.append("        ListNode current = head;\n");
        wrapper.append("        for (int i = 1; i < values.length; i++) {\n");
        wrapper.append("            current.next = new ListNode(values[i]);\n");
        wrapper.append("            current = current.next;\n");
        wrapper.append("        }\n");
        wrapper.append("        return head;\n");
        wrapper.append("    }\n");
        wrapper.append("    \n");
        wrapper.append("    private static void printLinkedList(ListNode head) {\n");
        wrapper.append("        System.out.print(\"[\");\n");
        wrapper.append("        ListNode current = head;\n");
        wrapper.append("        boolean first = true;\n");
        wrapper.append("        while (current != null) {\n");
        wrapper.append("            if (!first) System.out.print(\",\");\n");
        wrapper.append("            System.out.print(current.val);\n");
        wrapper.append("            first = false;\n");
        wrapper.append("            current = current.next;\n");
        wrapper.append("        }\n");
        wrapper.append("        System.out.println(\"]\");\n");
        wrapper.append("    }\n");
        
        wrapper.append("}\n");
        
        return wrapper.toString();
    }

    private String wrapPythonCodeDynamic(String userCode, String problemTitle, String methodSignature) {
        StringBuilder wrapper = new StringBuilder();
        
        wrapper.append("import sys\n");
        wrapper.append("import json\n");
        wrapper.append("from typing import List, Optional\n\n");
        
        // Add ListNode class for linked list problems
        wrapper.append("class ListNode:\n");
        wrapper.append("    def __init__(self, val=0, next=None):\n");
        wrapper.append("        self.val = val\n");
        wrapper.append("        self.next = next\n\n");
        
        // Add user's solution class
        wrapper.append(userCode).append("\n\n");
        
        wrapper.append("def parse_int_array(line):\n");
        wrapper.append("    if line.startswith('[') and line.endswith(']'):\n");
        wrapper.append("        line = line[1:-1]\n");
        wrapper.append("    if not line.strip():\n");
        wrapper.append("        return []\n");
        wrapper.append("    return [int(x.strip()) for x in line.split(',')]\n\n");
        
        wrapper.append("def print_int_array(arr):\n");
        wrapper.append("    print(f'[{\",\".join(map(str, arr))}]')\n\n");
        
        wrapper.append("def parse_linked_list(line):\n");
        wrapper.append("    values = parse_int_array(line)\n");
        wrapper.append("    if not values:\n");
        wrapper.append("        return None\n");
        wrapper.append("    \n");
        wrapper.append("    head = ListNode(values[0])\n");
        wrapper.append("    current = head\n");
        wrapper.append("    for val in values[1:]:\n");
        wrapper.append("        current.next = ListNode(val)\n");
        wrapper.append("        current = current.next\n");
        wrapper.append("    return head\n\n");
        
        wrapper.append("def print_linked_list(head):\n");
        wrapper.append("    result = []\n");
        wrapper.append("    current = head\n");
        wrapper.append("    while current:\n");
        wrapper.append("        result.append(current.val)\n");
        wrapper.append("        current = current.next\n");
        wrapper.append("    print_int_array(result)\n\n");
        
        wrapper.append("def main():\n");
        wrapper.append("    try:\n");
        wrapper.append("        solution = Solution()\n");
        wrapper.append("        line = input().strip()\n");
        wrapper.append("        \n");
        
        // Dynamic problem handling
        if (problemTitle.toLowerCase().contains("two sum")) {
            wrapper.append("        # Two Sum problem\n");
            wrapper.append("        nums = parse_int_array(line)\n");
            wrapper.append("        target = int(input().strip())\n");
            wrapper.append("        result = solution.twoSum(nums, target)\n");
            wrapper.append("        print_int_array(result)\n");
        } else if (problemTitle.toLowerCase().contains("reverse") && 
                   problemTitle.toLowerCase().contains("linked")) {
            wrapper.append("        # Reverse Linked List problem\n");
            wrapper.append("        head = parse_linked_list(line)\n");
            wrapper.append("        result = solution.reverseList(head)\n");
            wrapper.append("        print_linked_list(result)\n");
        } else {
            wrapper.append("        # Generic array problem\n");
            wrapper.append("        nums = parse_int_array(line)\n");
            wrapper.append("        print('Generic input processed')\n");
        }
        
        wrapper.append("        \n");
        wrapper.append("    except Exception as e:\n");
        wrapper.append("        print(f'Error: {str(e)}', file=sys.stderr)\n");
        wrapper.append("        import traceback\n");
        wrapper.append("        traceback.print_exc()\n");
        wrapper.append("\n");
        wrapper.append("if __name__ == '__main__':\n");
        wrapper.append("    main()\n");
        
        return wrapper.toString();
    }

    private String wrapCppCodeDynamic(String userCode, String problemTitle, String methodSignature) {
        StringBuilder wrapper = new StringBuilder();
        
        wrapper.append("#include <iostream>\n");
        wrapper.append("#include <vector>\n");
        wrapper.append("#include <string>\n");
        wrapper.append("#include <sstream>\n");
        wrapper.append("#include <algorithm>\n");
        wrapper.append("using namespace std;\n\n");
        
        // Add ListNode struct for linked list problems
        wrapper.append("struct ListNode {\n");
        wrapper.append("    int val;\n");
        wrapper.append("    ListNode *next;\n");
        wrapper.append("    ListNode() : val(0), next(nullptr) {}\n");
        wrapper.append("    ListNode(int x) : val(x), next(nullptr) {}\n");
        wrapper.append("    ListNode(int x, ListNode *next) : val(x), next(next) {}\n");
        wrapper.append("};\n\n");
        
        // Add user's solution class
        wrapper.append(userCode).append("\n\n");
        
        // Add utility functions
        wrapper.append("vector<int> parseIntArray(string line) {\n");
        wrapper.append("    if (line.front() == '[' && line.back() == ']') {\n");
        wrapper.append("        line = line.substr(1, line.length() - 2);\n");
        wrapper.append("    }\n");
        wrapper.append("    \n");
        wrapper.append("    vector<int> result;\n");
        wrapper.append("    if (line.empty()) return result;\n");
        wrapper.append("    \n");
        wrapper.append("    stringstream ss(line);\n");
        wrapper.append("    string num;\n");
        wrapper.append("    while (getline(ss, num, ',')) {\n");
        wrapper.append("        result.push_back(stoi(num));\n");
        wrapper.append("    }\n");
        wrapper.append("    return result;\n");
        wrapper.append("}\n\n");
        
        wrapper.append("void printIntArray(const vector<int>& arr) {\n");
        wrapper.append("    cout << \"[\";\n");
        wrapper.append("    for (int i = 0; i < arr.size(); i++) {\n");
        wrapper.append("        cout << arr[i];\n");
        wrapper.append("        if (i < arr.size() - 1) cout << \",\";\n");
        wrapper.append("    }\n");
        wrapper.append("    cout << \"]\" << endl;\n");
        wrapper.append("}\n\n");
        
        wrapper.append("ListNode* parseLinkedList(string line) {\n");
        wrapper.append("    vector<int> values = parseIntArray(line);\n");
        wrapper.append("    if (values.empty()) return nullptr;\n");
        wrapper.append("    \n");
        wrapper.append("    ListNode* head = new ListNode(values[0]);\n");
        wrapper.append("    ListNode* current = head;\n");
        wrapper.append("    for (int i = 1; i < values.size(); i++) {\n");
        wrapper.append("        current->next = new ListNode(values[i]);\n");
        wrapper.append("        current = current->next;\n");
        wrapper.append("    }\n");
        wrapper.append("    return head;\n");
        wrapper.append("}\n\n");
        
        wrapper.append("void printLinkedList(ListNode* head) {\n");
        wrapper.append("    cout << \"[\";\n");
        wrapper.append("    ListNode* current = head;\n");
        wrapper.append("    bool first = true;\n");
        wrapper.append("    while (current) {\n");
        wrapper.append("        if (!first) cout << \",\";\n");
        wrapper.append("        cout << current->val;\n");
        wrapper.append("        first = false;\n");
        wrapper.append("        current = current->next;\n");
        wrapper.append("    }\n");
        wrapper.append("    cout << \"]\" << endl;\n");
        wrapper.append("}\n\n");
        
        wrapper.append("int main() {\n");
        wrapper.append("    try {\n");
        wrapper.append("        Solution solution;\n");
        wrapper.append("        string line;\n");
        wrapper.append("        getline(cin, line);\n");
        wrapper.append("        \n");
        
        // Dynamic problem handling
        if (problemTitle.toLowerCase().contains("two sum")) {
            wrapper.append("        // Two Sum problem\n");
            wrapper.append("        vector<int> nums = parseIntArray(line);\n");
            wrapper.append("        getline(cin, line);\n");
            wrapper.append("        int target = stoi(line);\n");
            wrapper.append("        vector<int> result = solution.twoSum(nums, target);\n");
            wrapper.append("        printIntArray(result);\n");
        } else if (problemTitle.toLowerCase().contains("reverse") && 
                   problemTitle.toLowerCase().contains("linked")) {
            wrapper.append("        // Reverse Linked List problem\n");
            wrapper.append("        ListNode* head = parseLinkedList(line);\n");
            wrapper.append("        ListNode* result = solution.reverseList(head);\n");
            wrapper.append("        printLinkedList(result);\n");
        } else {
            wrapper.append("        // Generic array problem\n");
            wrapper.append("        vector<int> nums = parseIntArray(line);\n");
            wrapper.append("        cout << \"Generic input processed\" << endl;\n");
        }
        
        wrapper.append("        \n");
        wrapper.append("    } catch (const exception& e) {\n");
        wrapper.append("        cerr << \"Error: \" << e.what() << endl;\n");
        wrapper.append("        return 1;\n");
        wrapper.append("    }\n");
        wrapper.append("    \n");
        wrapper.append("    return 0;\n");
        wrapper.append("}\n");
        
        return wrapper.toString();
    }
}