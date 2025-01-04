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

        String buildContextPath = new File("").getAbsolutePath()
                                                        + File.separator + "ExecutionService"
                                                        + File.separator + "docker";

        // Java
        String contextJava = buildContextPath + File.separator + "java";
        String dockerfilePathJava = contextJava + File.separator + "Dockerfile-java";
        String imageJava = "coderank-java";

        // Python
        String contextPython = buildContextPath + File.separator + "python";
        String dockerfilePathPython = contextPython + File.separator + "Dockerfile-python";
        String imagePython = "coderank-python";


        // Javascript
        String contextJavascript = buildContextPath + File.separator + "javascript";
        String dockerfilePathJavascript = contextJavascript + File.separator + "Dockerfile-javascript";
        String imageJavascript = "coderank-javascript";

        // Ruby
        String contextRuby = buildContextPath + File.separator + "ruby";
        String dockerfilePathRuby = contextRuby + File.separator + "Dockerfile-ruby";
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
