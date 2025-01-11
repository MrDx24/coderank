package com.coderank.execution.ExecutionService.execution.strategies;

import com.coderank.execution.ExecutionService.service.DockerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Slf4j
public class JavaExecutionStrategy implements CodeExecutionStrategy {

    private final String memoryLimit;
    private final String cpuLimit;

    @Autowired
    public JavaExecutionStrategy(String memoryLimit, String cpuLimit) {
        this.memoryLimit = memoryLimit;
        this.cpuLimit = cpuLimit;
    }

    @Override
    public String execute(String code) throws Exception {
        log.info("Executing Java code with memory limit: {} and CPU limit: {}", memoryLimit, cpuLimit);

        ProcessBuilder processBuilder = new ProcessBuilder(
                "docker", "run", "--rm",
                "--memory", memoryLimit,
                "--cpus", cpuLimit,
                "--network", "none",
                "--security-opt", "no-new-privileges",
                "coderank-java",
                code
        );
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Java Execution Error:\n" + output);
        }

        return output;
    }
}
