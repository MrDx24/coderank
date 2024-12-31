package com.coderank.execution.ExecutionService.repository;

import com.coderank.execution.ExecutionService.entity.CodeSnippet;
import com.coderank.execution.ExecutionService.model.ExecutionHistoryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CodeSnippetRepository extends JpaRepository<CodeSnippet, String> {

    @Query(value = "SELECT " +
            "cs.id AS codeId, " +
            "cs.language AS language, " +
            "cs.code_url AS codeUrl, " +
            "el.output_url AS outputUrl, " +
            "el.execution_status AS executionStatus, " +
            "el.execution_time AS executionTime, " +
            "TO_CHAR(cs.created_at, 'DD-MM-YYYY HH:MM:SS') AS createdAt " +
            "FROM code_snippet cs " +
            "JOIN execution_logs el ON cs.id = el.code_id " +
            "WHERE cs.user_id = :userId", nativeQuery = true)
    List<ExecutionHistoryResponse> findExecutionHistoryByUserId(@Param("userId") String userId);

}
