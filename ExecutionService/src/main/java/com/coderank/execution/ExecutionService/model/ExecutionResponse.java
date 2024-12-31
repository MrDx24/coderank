package com.coderank.execution.ExecutionService.model;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String requestId;
    private String output;
    private long time;
    private String status;
}
