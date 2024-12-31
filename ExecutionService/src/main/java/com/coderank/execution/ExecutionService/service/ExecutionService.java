package com.coderank.execution.ExecutionService.service;

import com.coderank.execution.ExecutionService.model.ExecutionResponse;
import com.coderank.execution.ExecutionService.model.MessagePayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExecutionService {
    private final RabbitTemplate rabbitTemplate;

    @Value("${code.execution.memory.limit:512m}")
    private String memoryLimit;

    @Value("${code.execution.cpu.limit:1.0}")
    private String cpuLimit;

    public ExecutionService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "code-execution-queue")
    public void consumeMessage(MessagePayload payload) {
        long startTime = System.currentTimeMillis();

        try {
            log.info("Processing execution request: {}", payload.getRequestId());
            log.debug("Code to execute: {}", payload.getCode());

            String output = executeCode(payload);
            System.out.println(output);
            long executionTime = System.currentTimeMillis() - startTime;

            ExecutionResponse response = new ExecutionResponse(
                    payload.getRequestId(),
                    output,
                    executionTime,
                    "SUCCESS"
            );

            log.info("Successfully processed request: {}", response);
            rabbitTemplate.convertAndSend("execution-response-queue", response);

        } catch (Exception e) {
            log.error("Error processing execution request: {}", e.getMessage(), e);
            handleExecutionError(payload, e, startTime);
        }
    }

    private String executeCode(MessagePayload payload) throws Exception {
        if ("java".equalsIgnoreCase(payload.getLanguage())) {
            return "Java code executed. ";
        } else if ("python".equalsIgnoreCase(payload.getLanguage())) {
            return "Python code executed. ";
        } else {
            throw new UnsupportedOperationException("Unsupported programming language: " + payload.getLanguage());
        }
    }

//    private String executeCode(MessagePayload payload) throws Exception {
//        if ("java".equalsIgnoreCase(payload.getLanguage())) {
//            return executeJavaCode(payload.getCode());
//        } else if ("python".equalsIgnoreCase(payload.getLanguage())) {
//            return executePythonCode(payload.getCode());
//        } else {
//            throw new UnsupportedOperationException("Unsupported programming language: " + payload.getLanguage());
//        }
//    }

    private String executeJavaCode(String javaCode) throws Exception {
        // Add security constraints to Docker command
        ProcessBuilder processBuilder = new ProcessBuilder(
                "docker", "run", "--rm",
                "--memory", memoryLimit,
                "--cpus", cpuLimit,
                "--network", "none",  // Disable network access
                "--security-opt", "no-new-privileges",  // Prevent privilege escalation
                "coderank-java",
                javaCode
        );
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Execution Error:\n" + output);
        }

        return output;
    }

    private String executePythonCode(String pythonCode) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "docker", "run", "--rm",
                "--memory", memoryLimit,
                "--cpus", cpuLimit,
                "--network", "none",  // Disable network access
                "--security-opt", "no-new-privileges",  // Prevent privilege escalation
                "-i",  // Interactive mode for stdin
                "coderank-python",
                "python", "-"  // Read from stdin
        );
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // Write Python code to the process input stream
        try (OutputStream outputStream = process.getOutputStream()) {
            outputStream.write(pythonCode.getBytes());
            outputStream.flush();
        }

        String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Execution Error:\n" + output);
        }

        return output;
    }

    private void handleExecutionError(MessagePayload payload, Exception e, long startTime) {
        long executionTime = System.currentTimeMillis() - startTime;

        ExecutionResponse errorResponse = new ExecutionResponse(
                payload.getRequestId(),
                "Execution Error: " + e.getMessage(),
                executionTime,
                "ERROR"
        );

        rabbitTemplate.convertAndSend("execution-response-queue", errorResponse);
    }
}

