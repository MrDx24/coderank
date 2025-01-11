package com.coderank.execution.ExecutionService.service;

import com.coderank.execution.ExecutionService.model.AnalysisResponse;
import com.coderank.execution.ExecutionService.util.DatabaseSecretsUtil;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    private final DatabaseSecretsUtil databaseSecretsUtil;

    public GeminiService(DatabaseSecretsUtil databaseSecretsUtil) {
        this.databaseSecretsUtil = databaseSecretsUtil;
    }

    public AnalysisResponse analyzeCode(String code, String output) {
        Map<String, String> secrets = databaseSecretsUtil.getSecrets("gemini_credentials");
        String apiKey = secrets.get("GEMINI_API_KEY");
        if (apiKey == null) {
            throw new RuntimeException("Gemini API key not found in secrets table.");
        }

        String prompt = buildPrompt(code, output);

        Map<String, Object> requestBody = Map.of("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            String urlWithApiKey = GEMINI_API_URL + "?key=" + apiKey;
            ResponseEntity<Map> response = restTemplate.postForEntity(urlWithApiKey, entity, Map.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                System.err.println("Gemini API Error: " + response.getStatusCode());
                if (response.getBody() != null) {
                    System.err.println("Response Body: " + response.getBody());
                }
                return new AnalysisResponse("Gemini API Error (HTTP " + response.getStatusCode() + ")", null);
            }

            Map<String, Object> responseBody = response.getBody(); // Explicit type declaration
            if (responseBody == null || !responseBody.containsKey("candidates")) {
                System.err.println("Invalid Gemini API Response: Missing 'candidates' field");
                return new AnalysisResponse("Invalid Gemini API Response: Missing 'candidates' field", null);
            }

            List<?> candidatesUntyped = (List<?>) responseBody.get("candidates"); // Capture as untyped list
            if (candidatesUntyped.isEmpty()) {
                System.err.println("Invalid Gemini API Response: Empty 'candidates' list");
                return new AnalysisResponse("Invalid Gemini API Response: Empty 'candidates' list", null);
            }
            List<Map> candidates = candidatesUntyped.stream()
                    .filter(Map.class::isInstance)
                    .map(Map.class::cast)
                    .toList();

            if(candidates.isEmpty()){
                return new AnalysisResponse("Invalid Gemini API Response: cast fail in candidates", null);
            }

            Map<String, Object> candidate = candidates.get(0);
            if (!candidate.containsKey("content")) {
                System.err.println("Invalid Gemini API Response: Missing 'content' field in candidate");
                return new AnalysisResponse("Invalid Gemini API Response: Missing 'content' field in candidate", null);
            }

            Object contentObject = candidate.get("content");
            if(!(contentObject instanceof Map)){
                return new AnalysisResponse("Invalid Gemini API Response: content is not map", null);
            }

            Map<String, Object> content = (Map<String, Object>) contentObject;

            if (!content.containsKey("parts")) {
                System.err.println("Invalid Gemini API Response: Missing 'parts' field in content");
                return new AnalysisResponse("Invalid Gemini API Response: Missing 'parts' field in content", null);
            }

            List<?> partsUntyped = (List<?>) content.get("parts");

            if (partsUntyped.isEmpty()) {
                System.err.println("Invalid Gemini API Response: Empty 'parts' list");
                return new AnalysisResponse("Invalid Gemini API Response: Empty 'parts' list", null);
            }

            List<Map> parts = partsUntyped.stream()
                    .filter(Map.class::isInstance)
                    .map(Map.class::cast)
                    .toList();
            if(parts.isEmpty()){
                return new AnalysisResponse("Invalid Gemini API Response: cast fail in parts", null);
            }
            Map<String, Object> part = parts.get(0);
            if (!part.containsKey("text")) {
                System.err.println("Invalid Gemini API Response: Missing 'text' field in part");
                return new AnalysisResponse("Invalid Gemini API Response: Missing 'text' field in part", null);
            }

            String fullText = (String) part.get("text");
            String analysis = null;
            String optimizedCode = null;

            int analysisStartIndex = fullText.indexOf("**Analysis:**");
            if (analysisStartIndex != -1) {
                int analysisEndIndex = fullText.indexOf("**Optimized Code:**");
                if (analysisEndIndex != -1) {
                    analysis = fullText.substring(analysisStartIndex + "**Analysis:**".length(), analysisEndIndex).trim();
                    optimizedCode = fullText.substring(analysisEndIndex + "**Optimized Code:**".length()).trim();
                } else {
                    analysis = fullText.substring(analysisStartIndex + "**Analysis:**".length()).trim();
                }
            } else {
                System.err.println("Could not find Analysis or Optimized Code markers in Gemini response");
                return new AnalysisResponse("Could not parse Gemini Response", null);
            }

            return new AnalysisResponse(analysis, optimizedCode);

        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            return new AnalysisResponse("Error calling Gemini API: " + e.getMessage(), null);
        }
    }

    private String buildPrompt(String code, String output) {
        return String.format("""
                Analyze this code and its execution output:

                Code:
                %s

                Output:
                %s

                Provide your analysis in the following format:

                Analysis:
                [5-10 bullet points about functionality and logic]

                Optimized Code:
                [optimized version if possible]
                """, code, output);
    }
}
