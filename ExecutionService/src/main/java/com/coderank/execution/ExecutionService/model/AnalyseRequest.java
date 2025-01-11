package com.coderank.execution.ExecutionService.model;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class AnalyseRequest {
    String code;
    String output;
}
