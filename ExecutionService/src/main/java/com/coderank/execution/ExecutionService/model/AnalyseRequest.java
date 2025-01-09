package com.coderank.execution.ExecutionService.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Data
public class AnalyseRequest {
    String code;
    String output;
}
