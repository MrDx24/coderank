package com.coderank.execution.ExecutionService.execution.strategies;

import com.coderank.execution.ExecutionService.service.DockerService;

import java.util.HashMap;
import java.util.Map;

public class CodeExecutionStrategyFactory {

    private final Map<String, CodeExecutionStrategy> strategies = new HashMap<>();

    public CodeExecutionStrategyFactory(DockerService dockerService, String memoryLimit, String cpuLimit) {
        strategies.put("java", new JavaExecutionStrategy(dockerService, memoryLimit, cpuLimit));
        strategies.put("python", new PythonExecutionStrategy(dockerService, memoryLimit, cpuLimit));
        strategies.put("javascript", new JavaScriptExecutionStrategy(dockerService, memoryLimit, cpuLimit));
        strategies.put("ruby", new RubyExecutionStrategy(dockerService, memoryLimit, cpuLimit));
    }

    public CodeExecutionStrategy getStrategy(String language) {
        CodeExecutionStrategy strategy = strategies.get(language.toLowerCase());
        if (strategy == null) {
            throw new UnsupportedOperationException("Unsupported programming language: " + language);
        }
        return strategy;
    }
}
