package com.coderank.execution.ExecutionService.service;

import com.coderank.execution.ExecutionService.model.ExecutionHistoryResponse;
import com.coderank.execution.ExecutionService.repository.CodeSnippetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExecutionHistoryService {

    private final CodeSnippetRepository codeSnippetRepository;
    private final S3Service s3Service;

    public ExecutionHistoryService(CodeSnippetRepository codeSnippetRepository, S3Service s3Service) {
        this.codeSnippetRepository = codeSnippetRepository;
        this.s3Service = s3Service;
    }

    public List<ExecutionHistoryResponse> getExecutionHistory(String userId) {
        // Execute the native query
        List<ExecutionHistoryResponse> results = codeSnippetRepository.findExecutionHistoryByUserId(userId);

        // Map the results to ExecutionHistoryResponse
        return results.stream().map(row -> {
            String codeId = row.getCodeId();
            String language = row.getLanguage();
            String codeUrl = row.getCodeContent();
            String outputUrl = row.getOutputContent();
            String executionStatus = row.getExecutionStatus();
            double executionTime = ((Number) row.getExecutionTime()).doubleValue();
            String createdAt = row.getCreatedAt();

            // Fetch actual content from S3
            String codeContent = s3Service.getFileContent(codeUrl);
            String outputContent = s3Service.getFileContent(outputUrl);

            return new ExecutionHistoryResponse(
                    codeId,
                    language,
                    codeContent,
                    outputContent,
                    executionStatus,
                    executionTime,
                    createdAt
            );
        }).collect(Collectors.toList());
    }
}
