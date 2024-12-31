package com.coderank.execution.ExecutionService.controller;

import com.coderank.execution.ExecutionService.model.ExecutionHistoryResponse;
import com.coderank.execution.ExecutionService.service.ExecutionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/code-execution")
public class ExecutionHistoryController {

    private final ExecutionHistoryService executionHistoryService;

    @Autowired
    public ExecutionHistoryController(ExecutionHistoryService executionHistoryService) {
        this.executionHistoryService = executionHistoryService;
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ExecutionHistoryResponse>> getHistoryByUserId(@PathVariable String userId) {
        List<ExecutionHistoryResponse> history = executionHistoryService.getExecutionHistory(userId);
        return ResponseEntity.ok(history);
    }
}
