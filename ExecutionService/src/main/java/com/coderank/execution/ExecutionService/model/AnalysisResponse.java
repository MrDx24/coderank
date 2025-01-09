package com.coderank.execution.ExecutionService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Data
public class AnalysisResponse {
    private String steps;
    private String optimizedCode;
}
