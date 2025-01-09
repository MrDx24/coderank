package com.coderank.execution.ExecutionService.model;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class DockerInitializerHelper {
    String language;
    String dockerfileName;
    String imageName;

    public DockerInitializerHelper(String language) {
        this.language = language;
        this.dockerfileName = "Dockerfile-" + language;
        this.imageName = "coderank-" + language;
    }
}
