package com.coderank.execution.ExecutionService.execution.strategies;

import com.coderank.execution.ExecutionService.service.DockerService;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Slf4j
public class PythonExecutionStrategy implements CodeExecutionStrategy {

    private final DockerService dockerService;
    private final String memoryLimit;
    private final String cpuLimit;

    public PythonExecutionStrategy(DockerService dockerService, String memoryLimit, String cpuLimit) {
        this.dockerService = dockerService;
        this.memoryLimit = memoryLimit;
        this.cpuLimit = cpuLimit;
    }

    @Override
    public String execute(String code) throws Exception {
        log.info("Executing Python code with memory limit: {} and CPU limit: {}", memoryLimit, cpuLimit);

        ProcessBuilder processBuilder = new ProcessBuilder(
                "docker", "run", "--rm",
                "--memory", memoryLimit,
                "--cpus", cpuLimit,
                "--network", "none",
                "--security-opt", "no-new-privileges",
                "-i",
                "coderank-python",
                "python", "-"
        );
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // Write code to stdin
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
            writer.write(code);
        }

        String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Python Execution Error:\n" + output);
        }

        return output;
    }
}