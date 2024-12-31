package com.coderank.execution.ExecutionService.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ExecutionLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String codeId; // References id from the CodeSnippet table

    @Column(nullable = false)
    private String outputUrl;

    @Column(nullable = false)
    private String executionStatus; // SUCCESS, ERROR

    @Column(nullable = false)
    private double executionTime; // In seconds

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
