package com.coderank.execution.ExecutionService.execution.strategies;

import com.coderank.execution.ExecutionService.service.DockerService;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.stream.Collectors;

import com.coderank.execution.ExecutionService.service.DockerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.stream.Collectors;

@Slf4j
public class JavaScriptExecutionStrategy implements CodeExecutionStrategy {

    private final String memoryLimit;
    private final String cpuLimit;

    @Autowired
    public JavaScriptExecutionStrategy(String memoryLimit, String cpuLimit) {
        this.memoryLimit = memoryLimit;
        this.cpuLimit = cpuLimit;
    }

    @Override
    public String execute(String code) throws Exception {
        log.info("Executing JavaScript code with memory limit: {} and CPU limit: {}", memoryLimit, cpuLimit);

        ProcessBuilder processBuilder = new ProcessBuilder(
                "docker", "run", "--rm",
                "--memory", memoryLimit,
                "--cpus", cpuLimit,
                "--network", "none",
                "--security-opt", "no-new-privileges",
                "-i",
                "coderank-javascript"  // Using ENTRYPOINT from Dockerfile
        );
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
            writer.write(code);
            writer.flush();  // Important to ensure all data is sent
            process.getOutputStream().close();  // Signal EOF to the Node.js process
        }

        String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("JavaScript Execution Error:\n" + output);
        }

        return output;
    }
}
