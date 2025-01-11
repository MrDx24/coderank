package com.coderank.execution.ExecutionService.service;

import com.coderank.execution.ExecutionService.entity.CodeSnippet;
import com.coderank.execution.ExecutionService.execution.strategies.CodeExecutionStrategy;
import com.coderank.execution.ExecutionService.execution.strategies.CodeExecutionStrategyFactory;
import com.coderank.execution.ExecutionService.model.ExecutionResponse;
import com.coderank.execution.ExecutionService.model.MessagePayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExecutionService {

    private final RabbitTemplate rabbitTemplate;
    private final CodeExecutionStrategyFactory strategyFactory;
    private final CodeExecutionStorageService storageService;

    @Autowired
    public ExecutionService(RabbitTemplate rabbitTemplate,
                            CodeExecutionStorageService storageService,
                            @Value("${code.execution.memory.limit}") String memoryLimit,
                            @Value("${code.execution.cpu.limit}") String cpuLimit) {
        this.rabbitTemplate = rabbitTemplate;
        this.strategyFactory = new CodeExecutionStrategyFactory(memoryLimit, cpuLimit);
        this.storageService = storageService;
    }

    @RabbitListener(queues = "code-execution-queue")
    public void consumeMessage(MessagePayload payload) {
        long startTime = System.currentTimeMillis();
        CodeSnippet codeSnippet = storageService.saveCodeSnippet(payload);

        try {
            String reqId = payload.getRequestId();
            CodeExecutionStrategy strategy = strategyFactory.getStrategy(payload.getLanguage());
            String output = strategy.execute(payload.getCode());

            long time = System.currentTimeMillis() - startTime;

            storageService.saveExecutionLog(codeSnippet.getId(), output, "SUCCESS", time);

            ExecutionResponse executionResponse = new ExecutionResponse(reqId, output, time, "SUCCESS");
            rabbitTemplate.convertAndSend("execution-response-queue", executionResponse);

        } catch (UnsupportedOperationException e) {
            handleExecutionError(codeSnippet, payload, "Unsupported Language", e, startTime);
        } catch (Exception e) {
            handleExecutionError(codeSnippet, payload, "Execution Error", e, startTime);
        }
    }

    private void handleExecutionError(CodeSnippet codeSnippet, MessagePayload payload, String errorType, Exception e, long startTime) {
        long time = System.currentTimeMillis() - startTime;

        String errorMessage = String.format("[%s] %s: %s", errorType, e.getClass().getSimpleName(), e.getMessage());
        storageService.saveExecutionLog(codeSnippet.getId(), errorMessage, "ERROR", time);

        ExecutionResponse executionResponse = new ExecutionResponse(
                payload.getRequestId(), errorMessage, time, "ERROR");
        rabbitTemplate.convertAndSend("execution-response-queue", executionResponse);
    }
}

