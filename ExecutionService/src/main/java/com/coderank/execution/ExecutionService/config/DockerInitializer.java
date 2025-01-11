package com.coderank.execution.ExecutionService.config;

import com.coderank.execution.ExecutionService.model.DockerInitializerHelper;
import com.coderank.execution.ExecutionService.service.DockerService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class DockerInitializer {

    private final DockerService dockerService;

    @Autowired
    public DockerInitializer(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    @PostConstruct
    public void initializeDockerImages() {

        String buildContextPath = "/app/ExecutionService/docker";

        List<DockerInitializerHelper> dockerConfigs = List.of(
                new DockerInitializerHelper("java" ),
                new DockerInitializerHelper("python"),
                new DockerInitializerHelper("javascript"),
                new DockerInitializerHelper("ruby")
        );

        dockerConfigs.forEach(config -> {
            try {
                String contextPath = buildContextPath + "/" + config.getLanguage();
                String dockerfilePath = contextPath + "/Dockerfile-" + config.getLanguage();

                dockerService.buildDockerImage(config.getImageName(), dockerfilePath, contextPath);
            } catch (Exception e) {
                // Log error and continue the flow
                System.err.println("Failed to build Docker image for language: "
                        + config.getLanguage() + ". Error: " + e.getMessage());
            }
        });


    }
}



