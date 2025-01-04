package com.coderank.execution.ExecutionService.config;

import com.coderank.execution.ExecutionService.service.DockerService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DockerInitializer {
    private final DockerService dockerService;

    public DockerInitializer(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    @PostConstruct
    public void initializeDockerImages() {
        // The docker files will be in /app/ExecutionService/docker inside the container
        String buildContextPath = "/app/ExecutionService/docker";

        // Java
        String contextJava = buildContextPath + "/java";
        String dockerfilePathJava = contextJava + "/Dockerfile-java";
        String imageJava = "coderank-java";

        // Python
        String contextPython = buildContextPath + "/python";
        String dockerfilePathPython = contextPython + "/Dockerfile-python";
        String imagePython = "coderank-python";

        // Javascript
        String contextJavascript = buildContextPath + "/javascript";
        String dockerfilePathJavascript = contextJavascript + "/Dockerfile-javascript";
        String imageJavascript = "coderank-javascript";

        // Ruby
        String contextRuby = buildContextPath + "/ruby";
        String dockerfilePathRuby = contextRuby + "/Dockerfile-ruby";
        String imageRuby = "coderank-ruby";

        try {
            dockerService.buildDockerImage(imagePython, dockerfilePathPython, contextPython);
            dockerService.buildDockerImage(imageJava, dockerfilePathJava, contextJava);
            dockerService.buildDockerImage(imageJavascript, dockerfilePathJavascript, contextJavascript);
            dockerService.buildDockerImage(imageRuby, dockerfilePathRuby, contextRuby);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Docker images", e);
        }
    }
}
