package com.coderank.execution.ExecutionService.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class CodeSnippet {

    @Id
    private String id;

    @Column(nullable = false)
    private String userId; // References user_id from the users table

    @Column(nullable = false)
    private String language;

    @Column(nullable = true)
    private String codeUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}