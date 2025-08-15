package com.codepvg.code.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class Judge0Service {

    @Value("${judge0.api.url}")
    private String judge0ApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String submitCode(String sourceCode, int languageId, String stdin) {
        try {
            String url = judge0ApiUrl + "/submissions";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("source_code", sourceCode);
            requestBody.put("language_id", languageId);
            requestBody.put("stdin", stdin);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.CREATED) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return jsonNode.get("token").asText();
            }
            
            throw new RuntimeException("Failed to submit code to Judge0");
            
        } catch (Exception e) {
            throw new RuntimeException("Error submitting code: " + e.getMessage());
        }
    }

    public Map<String, Object> getSubmissionResult(String token) {
        try {
            String url = judge0ApiUrl + "/submissions/" + token;
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                
                Map<String, Object> result = new HashMap<>();
                result.put("status", jsonNode.get("status").get("description").asText());
                result.put("statusId", jsonNode.get("status").get("id").asInt());
                
                if (jsonNode.has("stdout") && !jsonNode.get("stdout").isNull()) {
                    result.put("output", jsonNode.get("stdout").asText());
                }
                
                if (jsonNode.has("stderr") && !jsonNode.get("stderr").isNull()) {
                    result.put("error", jsonNode.get("stderr").asText());
                }
                
                if (jsonNode.has("compile_output") && !jsonNode.get("compile_output").isNull()) {
                    result.put("compileError", jsonNode.get("compile_output").asText());
                }
                
                if (jsonNode.has("time") && !jsonNode.get("time").isNull()) {
                    result.put("executionTime", jsonNode.get("time").asDouble());
                }
                
                if (jsonNode.has("memory") && !jsonNode.get("memory").isNull()) {
                    result.put("memoryUsage", jsonNode.get("memory").asInt());
                }
                
                return result;
            }
            
            throw new RuntimeException("Failed to get submission result from Judge0");
            
        } catch (Exception e) {
            throw new RuntimeException("Error getting submission result: " + e.getMessage());
        }
    }

    public Map<String, Object> getSupportedLanguages() {
        try {
            String url = judge0ApiUrl + "/languages";
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                
                Map<String, Object> result = new HashMap<>();
                result.put("languages", jsonNode);
                
                return result;
            }
            
            throw new RuntimeException("Failed to get supported languages from Judge0");
            
        } catch (Exception e) {
            throw new RuntimeException("Error getting supported languages: " + e.getMessage());
        }
    }
}