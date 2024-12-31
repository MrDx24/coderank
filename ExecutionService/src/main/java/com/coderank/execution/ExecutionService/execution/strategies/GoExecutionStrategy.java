package com.coderank.execution.ExecutionService.execution.strategies;

import com.coderank.execution.ExecutionService.service.DockerService;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.stream.Collectors;

@Slf4j
public class GoExecutionStrategy implements CodeExecutionStrategy {

    private final DockerService dockerService;
    private final String memoryLimit;
    private final String cpuLimit;

    public GoExecutionStrategy(DockerService dockerService, String memoryLimit, String cpuLimit) {
        this.dockerService = dockerService;
        this.memoryLimit = memoryLimit;
        this.cpuLimit = cpuLimit;
    }

    @Override
    public String execute(String code) throws Exception {
        log.info("Executing Go code with memory limit: {} and CPU limit: {}", memoryLimit, cpuLimit);

        ProcessBuilder processBuilder = new ProcessBuilder(
                "docker", "run", "--rm",
                "--memory", memoryLimit,
                "--cpus", cpuLimit,
                "--network", "none",
                "--security-opt", "no-new-privileges",
                "coderank-go",
                code  // Pass code directly as argument
        );
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Go Execution Error:\n" + output);
        }

        return output;
    }
}
