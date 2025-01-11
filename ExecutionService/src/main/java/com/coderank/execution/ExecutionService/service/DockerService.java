package com.coderank.execution.ExecutionService.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import java.util.Map;

@Slf4j
@Service
public class DockerService {

    private final Map<String, Boolean> imageBuiltStatus = new ConcurrentHashMap<>();

    public void buildDockerImage(String imageName, String dockerfilePath, String buildContextPath) throws Exception {

        if (imageBuiltStatus.getOrDefault(imageName, false)) {
            log.info("Image {} already built, skipping", imageName);
            return;
        }

        ProcessBuilder processBuilder = new ProcessBuilder(
                "docker", "build", "-t", imageName, "-f", dockerfilePath, buildContextPath
        );
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Docker build failed:\n" + output);
        }

        imageBuiltStatus.put(imageName, true);
        log.info("Docker image built successfully: {}", imageName);
    }

}

