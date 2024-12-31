package com.coderank.execution.ExecutionService.repository;

import com.coderank.execution.ExecutionService.entity.ExecutionLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutionLogsRepository extends JpaRepository<ExecutionLogs, Long> {
}
