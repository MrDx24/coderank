package com.coderank.execution.ExecutionService.controller;

import com.coderank.execution.ExecutionService.model.AnalyseRequest;
import com.coderank.execution.ExecutionService.model.ExecutionHistoryResponse;
import com.coderank.execution.ExecutionService.model.AnalysisResponse;
import com.coderank.execution.ExecutionService.service.ExecutionHistoryService;
import com.coderank.execution.ExecutionService.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/code-execution")
public class ExecutionController {

    private final ExecutionHistoryService executionHistoryService;
    private final GeminiService geminiService;

    @Autowired
    public ExecutionController(ExecutionHistoryService executionHistoryService, GeminiService geminiService) {
        this.executionHistoryService = executionHistoryService;
        this.geminiService = geminiService;
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<Map<String, Object>> getHistoryByUserId(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ExecutionHistoryResponse> history = executionHistoryService.getExecutionHistory(userId);
            if (history.isEmpty()) {
                response.put("status", "Error");
                response.put("message", "No execution history found for user ID: " + userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("status", "Success");
            response.put("message", "Execution history fetched successfully");
            response.put("data", history);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("status", "Error");
            response.put("message", "Invalid user ID: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("status", "Error");
            response.put("message", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/analyse")
    public ResponseEntity<Map<String, Object>> analyseCode(@RequestBody AnalyseRequest analyseRequest) {
        String code = analyseRequest.getCode();
        String output = analyseRequest.getOutput();

        // Validate inputs
        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Code is required"));
        }
        if (output == null || output.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Output is required"));
        }

        // Interact with OpenAI API
        AnalysisResponse aiResponse = geminiService.analyzeCode(code, output);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("steps", aiResponse.getSteps());
        response.put("optimized_code", aiResponse.getOptimizedCode());

        return ResponseEntity.ok(response);
    }
}
