package com.coderank.execution.ExecutionService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionHistoryResponse {
    private String codeId;          // ID of the code snippet
    private String language;        // Programming language
    private String codeContent;     // Actual code content from S3
    private String outputContent;   // Output or error content from S3
    private String executionStatus; // SUCCESS or ERROR
    private double executionTime;   // Time taken for execution in seconds
    private String createdAt;       // Timestamp of code submission
}
