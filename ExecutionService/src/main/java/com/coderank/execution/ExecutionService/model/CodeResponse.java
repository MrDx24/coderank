package com.coderank.execution.ExecutionService.model;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CodeResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String codeOutput;
}