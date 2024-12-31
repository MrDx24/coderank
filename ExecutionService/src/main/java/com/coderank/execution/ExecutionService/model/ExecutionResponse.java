package com.coderank.execution.ExecutionService.model;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExecutionResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String requestId; // Unique ID for tracking
    private String request;
    private long executionTime; // Mock execution time in ms
    private String status; // Mock status
}

