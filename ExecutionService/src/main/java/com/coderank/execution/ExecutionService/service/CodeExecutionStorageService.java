package com.coderank.execution.ExecutionService.service;

import com.coderank.execution.ExecutionService.entity.CodeSnippet;
import com.coderank.execution.ExecutionService.entity.ExecutionLogs;
import com.coderank.execution.ExecutionService.model.MessagePayload;
import com.coderank.execution.ExecutionService.repository.CodeSnippetRepository;
import com.coderank.execution.ExecutionService.repository.ExecutionLogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CodeExecutionStorageService {

    private final CodeSnippetRepository codeSnippetRepository;
    private final ExecutionLogsRepository executionLogsRepository;
    private final S3Service s3Service;

    @Autowired
    public CodeExecutionStorageService(CodeSnippetRepository codeSnippetRepository,
                                       ExecutionLogsRepository executionLogsRepository,
                                       S3Service s3Service) {
        this.codeSnippetRepository = codeSnippetRepository;
        this.executionLogsRepository = executionLogsRepository;
        this.s3Service = s3Service;
    }

    public CodeSnippet saveCodeSnippet(MessagePayload payload) {
        // Upload code to S3
        String codeS3Url = s3Service.uploadFile(payload.getCode(), payload.getRequestId() + "-code.txt");

        CodeSnippet codeSnippet = new CodeSnippet();
        codeSnippet.setId(payload.getRequestId());
        codeSnippet.setUserId(payload.getUserId());
        codeSnippet.setLanguage(payload.getLanguage());
        codeSnippet.setCodeUrl(codeS3Url); // Mock S3 URL for code storage
        codeSnippet.setCreatedAt(LocalDateTime.now());

        return codeSnippetRepository.save(codeSnippet);
    }

    public void saveExecutionLog(String codeId, String output, String status, long executionTime) {
        String outputS3Url = s3Service.uploadFile(output, codeId + "-output.txt");

        ExecutionLogs executionLog = new ExecutionLogs();
        executionLog.setCodeId(codeId);
        executionLog.setOutputUrl(outputS3Url); // Mock S3 URL for output/error
        executionLog.setExecutionStatus(status);
        executionLog.setExecutionTime(executionTime / 1000.0); // Convert to seconds
        executionLog.setCreatedAt(LocalDateTime.now());

        executionLogsRepository.save(executionLog);
    }


}
