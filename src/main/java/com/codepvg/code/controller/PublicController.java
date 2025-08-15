package com.codepvg.code.controller;

import com.codepvg.code.service.Judge0Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class PublicController {

    @Autowired
    private Judge0Service judge0Service;

    @GetMapping("/languages")
    public ResponseEntity<?> getSupportedLanguages() {
        try {
            // Get all languages from Judge0
            Map<String, Object> allLanguages = judge0Service.getSupportedLanguages();
            
            // Filter to only include C, C++, Java, and Python
            List<Map<String, Object>> filteredLanguages = new ArrayList<>();
            
            // Define the languages we want to support
            Map<Integer, String> supportedLanguages = new HashMap<>();
            supportedLanguages.put(50, "C (GCC 9.2.0)");           // C
            supportedLanguages.put(54, "C++ (GCC 9.2.0)");         // C++
            supportedLanguages.put(62, "Java (OpenJDK 13.0.1)");   // Java
            supportedLanguages.put(71, "Python (3.8.1)");          // Python
            
            // Create filtered language list
            for (Map.Entry<Integer, String> entry : supportedLanguages.entrySet()) {
                Map<String, Object> language = new HashMap<>();
                language.put("id", entry.getKey());
                language.put("name", entry.getValue());
                
                // Add language-specific details
                switch (entry.getKey()) {
                    case 50: // C
                        language.put("language", "c");
                        language.put("displayName", "C");
                        language.put("extension", ".c");
                        language.put("template", "#include <stdio.h>\n\nint main() {\n    // Your code here\n    return 0;\n}");
                        break;
                    case 54: // C++
                        language.put("language", "cpp");
                        language.put("displayName", "C++");
                        language.put("extension", ".cpp");
                        language.put("template", "#include <iostream>\n#include <vector>\nusing namespace std;\n\nclass Solution {\npublic:\n    // Your solution here\n};\n\nint main() {\n    Solution solution;\n    return 0;\n}");
                        break;
                    case 62: // Java
                        language.put("language", "java");
                        language.put("displayName", "Java");
                        language.put("extension", ".java");
                        language.put("template", "class Solution {\n    // Your solution here\n}");
                        break;
                    case 71: // Python
                        language.put("language", "python");
                        language.put("displayName", "Python");
                        language.put("extension", ".py");
                        language.put("template", "class Solution:\n    # Your solution here\n    pass");
                        break;
                }
                
                filteredLanguages.add(language);
            }
            
            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("languages", filteredLanguages);
            response.put("count", filteredLanguages.size());
            response.put("supported", Arrays.asList("C", "C++", "Java", "Python"));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get supported languages: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok("Coding Platform API is running!");
    }
}